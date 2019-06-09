package com.mrcrayfish.modelcreator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mrcrayfish.modelcreator.block.BlockManager;
import com.mrcrayfish.modelcreator.component.TextureManager;
import com.mrcrayfish.modelcreator.element.Element;
import com.mrcrayfish.modelcreator.element.ElementManager;
import com.mrcrayfish.modelcreator.element.Face;
import com.mrcrayfish.modelcreator.texture.TextureEntry;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ProjectManager
{

    public static void loadProject(ElementManager manager, File projectFile)
    {
        TextureManager.clear();
        manager.clearElements();
        manager.setParticle(null);
        
        try(ZipInputStream zip = new ZipInputStream(new FileInputStream(projectFile)))
        {
        	Project project = new Project(zip);
        	loadBlockData(project.getBlockData());
        	ModelImporter importer = new ModelImporter(manager, project.getModelData());
        	importer.importFromJSON();
        } catch (IOException e)
		{
			e.printStackTrace();
			return;
		}

    }
    
    private static void loadBlockData(String blockData) {
    	if(blockData == null || blockData.isEmpty())
    		return;
    	
    	JsonParser parser = new JsonParser();
         JsonElement parsed = parser.parse(blockData);
         
         if(!parsed.isJsonObject()) {
        	 System.err.println("Project file is corrupted");
        	 return;
         }
         
         JsonObject block = parsed.getAsJsonObject();
         
         //Basic data
         JsonObject basic = block.get("basic").getAsJsonObject();
         BlockManager.assetID = basic.get("assetID").getAsString();
         BlockManager.javaID = basic.get("javaID").getAsString();
         BlockManager.usedMcVersion = basic.get("mcVersion").getAsString();
         
         //Properties
         JsonObject properties = block.get("properties").getAsJsonObject();
         BlockManager.properties.setHardness(properties.get("hardness").getAsFloat());
         BlockManager.properties.setResistance(properties.get("resistance").getAsFloat());
         BlockManager.properties.setLightLevel(properties.get("lightLevel").getAsFloat());
         if(properties.has("material")) {
        	 BlockManager.properties.setMaterial(properties.get("material").getAsString());
         }
         if(properties.has("sound")) {
        	 BlockManager.properties.setSound(properties.get("sound").getAsString());
         }
         
         //Translation
         JsonObject translations = block.get("translation").getAsJsonObject();
         translations.entrySet().forEach(v -> {
        	 String langKey = v.getKey();
        	 JsonObject trans = v.getValue().getAsJsonObject();
        	 String name = trans.get("name").getAsString();
        	 String tooltip = null;
        	 if(trans.has("tooltip")) {
        		 tooltip = trans.get("tooltip").getAsString();
        	 }
        	 BlockManager.translation.addTranslation(langKey, name, tooltip);
         });
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
        private String modelData;
        private String blockData;

        public Project(ZipInputStream zis) throws IOException
        {
            ZipEntry entry;
            while((entry = zis.getNextEntry()) != null) {
            	String name = entry.getName();
            	if(name.equals("model.json")) {
            		this.modelData = readFile(zis);
            	}else if(name.equals("block.json")) {
            		this.blockData = readFile(zis);
            	}else {
            		System.err.println("Unknown file " + name);
            	}
            	zis.closeEntry();
            }
            
        }

        public String getModelData()
        {
            return modelData;
        }
        
        public String getBlockData() {
        	return blockData;
        }
        
        private String readFile(ZipInputStream zis) throws IOException {
        	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        	byte[] bytes = new byte[1024];
        	int length;
        	while((length = zis.read(bytes)) >= 0) {
        		baos.write(bytes, 0, length);
        	}
        	
        	return new String(baos.toByteArray());
        }

    }
}
