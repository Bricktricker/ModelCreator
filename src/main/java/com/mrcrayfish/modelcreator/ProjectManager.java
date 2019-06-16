package com.mrcrayfish.modelcreator;

import com.google.gson.JsonArray;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ProjectManager
{

    public static void loadProject(ElementManager manager, File projectFile)
    {
        manager.clearElements();
        manager.setParticle(null);
        
        try(ZipInputStream zis = new ZipInputStream(new FileInputStream(projectFile)))
        {
        	Project project = new Project(zis);
        	loadBlockData(project.getBlockData());
        	loadImages(project);
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
        
         //Properties
         JsonObject properties = block.get("properties").getAsJsonObject();
         BlockManager.properties.setHardness(properties.get("hardness").getAsFloat());
         BlockManager.properties.setResistance(properties.get("resistance").getAsFloat());
         BlockManager.properties.setLightLevel(properties.get("lightLevel").getAsInt());
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
         
         //Crafting
         if(block.has("crafting")) {
        	 JsonObject crafting = block.get("crafting").getAsJsonObject();
        	 boolean isShapeless = crafting.get("shapeless").getAsBoolean();
        	 BlockManager.crafting.setShapeLess(isShapeless);
        	 int numOutput = crafting.get("numOutput").getAsInt();
        	 BlockManager.crafting.setNumOutputItems(numOutput);
        	 JsonArray recipe = crafting.getAsJsonArray("recipe");
        	 final ArrayList<String> craftItems = new ArrayList<>();
        	 recipe.forEach(item -> {
        		 craftItems.add(item.getAsString());
        	 });
        	 assert(craftItems.size() == 9);
        	 BlockManager.crafting.setCraftItems(craftItems);
         }
    }
    
    public static void loadImages(Project project) throws IOException {
    	String texturesData = project.getTextures();
    	if(texturesData == null || texturesData.isEmpty())
    		return;
    	
    	JsonParser parser = new JsonParser();
    	JsonElement parsed = parser.parse(texturesData);
    	
    	JsonArray textures = parsed.getAsJsonArray();
    	for(JsonElement e : textures) {
    		JsonObject texture = e.getAsJsonObject();
    		String directory = texture.get("directory").getAsString();
			String mcTexture = texture.get("texture").getAsString();
    		String modid = texture.get("modid").getAsString();
    		String key = texture.get("key").getAsString();
    		
    		//TODO: delete texture from temp folder
    		TexturePath texturepath = new TexturePath(modid, directory, mcTexture);
    		Path textureFile = Files.createTempFile(mcTexture, "");
    		Files.write(textureFile, project.getFileData(mcTexture));
    		TextureManager.addImage(key, texturepath, textureFile.toFile());
    	}
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
            JsonArray textureRoot = new JsonArray();
            for(TextureEntry entry : getAllTextures(manager))
            {
            	if(entry.getModId().equals("minecraft"))
            		continue;
            	
            	JsonObject textureJson = new JsonObject();
        		textureJson.addProperty("directory", entry.getDirectory());
        		textureJson.addProperty("texture", entry.getName());
        		textureJson.addProperty("modid", entry.getModId());
        		textureJson.addProperty("key", entry.getKey());
        		
        		//Save image in project zip
        		BufferedImage image = entry.getSource();
        		ZipEntry zipEntry = new ZipEntry(entry.getName());
                zos.putNextEntry(zipEntry);
                ImageIO.write(image, "PNG", zos);
                zos.closeEntry();
        		
            	textureRoot.add(textureJson);
            }
            if(textureRoot.size() != 0) {
            	String textureStr = textureRoot.toString();
                addToZipFile(textureStr, zos, "textures.json");
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
    	
    	//Block properties
    	{
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
    	}
    	
    	//Block translations
    	{
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
        
    	//Crafting
    	{
    		if(!BlockManager.crafting.isEmpty()) {
    			JsonObject crafting = new JsonObject();
            	crafting.addProperty("shapeless", BlockManager.crafting.isShapeLess());
            	crafting.addProperty("numOutput", BlockManager.crafting.getNumOutputItems());
            	JsonArray recipe = new JsonArray();
            	BlockManager.crafting.getCraftItems().forEach(recipe::add);
            	crafting.add("recipe", recipe);
            	rootObj.add("crafting", crafting);	
    		}
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
        private Map<String, byte[]> fileBuffer;

        public Project(ZipInputStream zis) throws IOException
        {
        	this.fileBuffer = new HashMap<>();
            ZipEntry entry;
            while((entry = zis.getNextEntry()) != null) {
            	String name = entry.getName();
            	byte[] data = readFile(zis);
            	this.fileBuffer.put(name, data); 
            	zis.closeEntry();
            }
            
        }

        public String getModelData()
        {
        	byte[] data = this.fileBuffer.get("model.json");
        	return data == null ? null : new String(data);
        }
        
        public String getBlockData() {
        	byte[] data = this.fileBuffer.get("block.json");
        	return data == null ? null : new String(data);
        }
        
        public String getTextures() {
        	byte[] data = this.fileBuffer.get("textures.json");
        	return data == null ? null : new String(data);
        }
        
        public byte[] getFileData(String file) {
        	return this.fileBuffer.get(file);
        }
        
        private byte[] readFile(ZipInputStream zis) throws IOException {
        	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        	byte[] bytes = new byte[1024];
        	int length;
        	while((length = zis.read(bytes)) >= 0) {
        		baos.write(bytes, 0, length);
        	}
        	
        	return baos.toByteArray();
        }

    }
}
