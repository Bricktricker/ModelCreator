package com.mrcrayfish.modelcreator;

import com.google.gson.JsonObject;
import com.mrcrayfish.modelcreator.block.BlockManager;
import com.mrcrayfish.modelcreator.component.TextureManager;
import com.mrcrayfish.modelcreator.element.Element;
import com.mrcrayfish.modelcreator.element.ElementManager;
import com.mrcrayfish.modelcreator.element.Face;
import com.mrcrayfish.modelcreator.texture.TextureEntry;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ProjectManager
{
    private static final Pattern TEXTURE_FIX = Pattern.compile("\\d+$"); //Matches numbers at the end of line

    public static void loadProject(ElementManager manager, String modelFile)
    {
        TextureManager.clear();
        manager.clearElements();
        manager.setParticle(null);

        File projectFolder = extractFiles(modelFile);
        if(projectFolder != null)
        {
            Project project = new Project(projectFolder);
            Importer importer = new Importer(manager, project.getModel().getPath());
            importer.importFromJSON();
        }
        deleteFolder(projectFolder);
    }

    private static void deleteFolder(File file)
    {
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            try
            {
                Files.walk(file.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }));
    }

    private static File extractFiles(String modelFile)
    {
        try
        {
            Path path = Files.createTempDirectory("ModelCreator");
            File folder = path.toFile();

            ZipInputStream zis = new ZipInputStream(new FileInputStream(modelFile));
            ZipEntry ze;
            while((ze = zis.getNextEntry()) != null)
            {
                String fileName = ze.getName();

                /* Fixes old project texture files extracting with numbers on the file name */
                Matcher matcher = TEXTURE_FIX.matcher(ze.getName());
                if(matcher.find())
                {
                    String numbers = matcher.group(0);
                    fileName = fileName.replace(numbers, "");
                }

                File file = new File(folder, fileName);
                file.getParentFile().mkdirs();
                file.createNewFile();

                byte[] buffer = new byte[1024];
                FileOutputStream fos = new FileOutputStream(file);

                int len;
                while((len = zis.read(buffer)) > 0)
                {
                    fos.write(buffer, 0, len);
                }

                fos.flush();
                fos.close();
                zis.closeEntry();
            }
            zis.close();

            return folder;
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveProject(ElementManager manager, File name)
    {
        try
        {
            FileOutputStream fos = new FileOutputStream(name);
            ZipOutputStream zos = new ZipOutputStream(fos);

            //Model
            File model = getModelFile(manager);
            addToZipFile(model, zos, "model.json");
            model.delete();

            //Textures
            for(TextureEntry entry : getAllTextures(manager))
            {
                File temp = File.createTempFile(entry.getName(), "");
                BufferedImage image = entry.getSource();
                ImageIO.write(image, "PNG", temp);
                addToZipFile(temp, zos, "assets/" + entry.getModId() + "/textures/" + entry.getDirectory() + "/", entry.getName() + ".png");
                temp.delete();
            }

            //Block properties
            String blockJson = getBlockFile();
            addToZipFile(blockJson, zos, "block.json");

            zos.close();
            fos.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    private static Set<TextureEntry> getAllTextures(ElementManager manager)
    {
        Set<TextureEntry> textureEntries = new HashSet<>();
        for(Element element : manager.getAllElements())
        {
            for(Face face : element.getAllFaces())
            {
                if(face.getTexture() != null)
                {
                    textureEntries.add(face.getTexture());
                }
            }
        }
        return textureEntries;
    }

    private static File getModelFile(ElementManager manager) throws IOException
    {
        ExporterModel exporter = new ExporterModel(manager);
        exporter.setOptimize(false);
        exporter.setIncludeNonTexturedFaces(true);
        return exporter.writeFile(File.createTempFile("model.json", ""));
    }
    
    private static String getBlockFile() {
    	JsonObject rootObj = new JsonObject();
    	
    	//Construct json object
    	{
    		//Basic data
        	JsonObject basic = new JsonObject();
        	basic.addProperty("assetID", BlockManager.assetID);
        	basic.addProperty("javaID", BlockManager.javaID);
        	basic.addProperty("mcVersion", BlockManager.usedMcVersion);
        	rootObj.add("basic", basic);
        	
        	//Block properties
        	JsonObject properties = new JsonObject();
        	properties.addProperty("hardness", BlockManager.properties.getHardness());
        	properties.addProperty("resistance", BlockManager.properties.getResistance());
        	properties.addProperty("lightLevel", BlockManager.properties.getLightLevel());
        	
        	String material = BlockManager.properties.getMaterial();
        	if(material != null && !material.isEmpty())
        		properties.addProperty("material", material);
        	
        	String sound = BlockManager.properties.getSound();
        	if(sound != null && !sound.isEmpty())
        		properties.addProperty("sound", sound);
        	
        	rootObj.add("properties", properties);
        	
        	//Block translations
        	JsonObject translation = new JsonObject();
        	BlockManager.translation.getAllTranslations().forEach((key, t) -> {
        		JsonObject trans = new JsonObject();
        		trans.addProperty("name", t.name);
        		if(t.tooltip != null)
        			trans.addProperty("tooltip", t.tooltip);
        		translation.add(key, trans);
        	});
        	rootObj.add("translation", translation);
    	}
    	
    	return rootObj.toString();
    }

    private static void addToZipFile(File file, ZipOutputStream zos, String name) throws IOException
    {
        addToZipFile(file, zos, "", name);
    }
    
    private static void addToZipFile(String data, ZipOutputStream zos, String name) throws IOException
    {
        addToZipFile(data, zos, "", name);
    }

    private static void addToZipFile(File file, ZipOutputStream zos, String folder, String name) throws IOException
    {
        FileInputStream fis = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(folder + name);
        zos.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;
        while((length = fis.read(bytes)) >= 0)
        {
            zos.write(bytes, 0, length);
        }

        zos.closeEntry();
        fis.close();
    }
    
    private static void addToZipFile(String data, ZipOutputStream zos, String folder, String name) throws IOException
    {
        ZipEntry zipEntry = new ZipEntry(folder + name);
        zos.putNextEntry(zipEntry);
        zos.write(data.getBytes());
        zos.closeEntry();
    }

    private static class Project
    {
        public File model;
        public File textures;

        public Project(File folder)
        {
            File[] files = folder.listFiles();
            if(files != null)
            {
                for(File file : files)
                {
                    String name = file.getName();
                    if(file.isFile() && name.equals("model.json"))
                    {
                        this.model = file;
                    }
                    else if(file.isDirectory() && name.equals("textures"))
                    {
                        this.textures = file;
                    }
                }
            }
        }

        public File getModel()
        {
            return model;
        }

    }
}
