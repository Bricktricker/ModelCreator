package com.mrcrayfish.modelcreator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mrcrayfish.modelcreator.block.BlockManager;
import com.mrcrayfish.modelcreator.component.TextureManager;
import com.mrcrayfish.modelcreator.element.Element;
import com.mrcrayfish.modelcreator.element.ElementCellEntry;
import com.mrcrayfish.modelcreator.element.ElementManager;
import com.mrcrayfish.modelcreator.element.Face;
import com.mrcrayfish.modelcreator.panels.CollisionPanel;
import com.mrcrayfish.modelcreator.texture.TextureEntry;
import com.mrcrayfish.modelcreator.util.Util;

import javax.imageio.ImageIO;
import javax.swing.ListModel;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

	public static void loadProject(SidebarManager manager, File projectFile) {
		try(InputStream is = new FileInputStream(projectFile))
		{
			loadProject(manager, is);
		} catch (IOException e) {
			Util.writeCrashLog(e);
		}
	}
	
    public static void loadProject(SidebarManager manager, InputStream is)
    {
        manager.getModelPanel().clearElements();
        manager.getModelPanel().setParticle(null);
        manager.getCollisionPanel().clearElements();
        
        try(ZipInputStream zis = new ZipInputStream(is))
        {
        	Project project = new Project(zis);
        	loadBlockData(project.getBlockData(), manager.getCollisionPanel());
        	loadImages(project);
        	ModelImporter importer = new ModelImporter(manager.getModelPanel(), project.getModelData());
        	importer.importFromJSON();
        	
        	byte[] notesBytes = project.getFileData("notes.txt");
        	if(notesBytes != null) {
        		String text = new String(notesBytes);
        		BlockManager.notes.setNotes(text);
        	}
        } catch (IOException e)
		{
			Util.writeCrashLog(e);
		}

    }
    
    private static void loadBlockData(String blockData, CollisionPanel collisionPanel) {
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
         if(properties.has("creative")) {
        	 BlockManager.properties.setCreativeTab(properties.get("creative").getAsString());
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
        	 boolean isExactly = crafting.get("exact").getAsBoolean();
        	 BlockManager.crafting.setExactly(isExactly);
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
         
         //Loot
         JsonObject loot = block.get("loot").getAsJsonObject();
         boolean silk = loot.get("silk").getAsBoolean();
         BlockManager.loot.setSilkTouch(silk);
         int num = loot.get("num").getAsInt();
         BlockManager.loot.setNumDrops(num);
         if(loot.has("drop")) {
        	 String item = loot.get("drop").getAsString();
        	 BlockManager.loot.setDropItem(item);
         }
         
         //Collision
         JsonArray collision = block.getAsJsonArray("collision");
         collision.forEach(b -> {
        	 JsonObject box = b.getAsJsonObject();
        	 Element elem = new Element(0, 0, 0);
        	 String name = box.get("name").getAsString();
        	 
        	 JsonArray startJson = box.getAsJsonArray("start");
        	 elem.setStartX(startJson.get(0).getAsDouble());
        	 elem.setStartY(startJson.get(1).getAsDouble());
        	 elem.setStartZ(startJson.get(2).getAsDouble());
        	 
        	 JsonArray endJson = box.getAsJsonArray("end");
        	 elem.setWidth(endJson.get(0).getAsDouble() - elem.getStartX());
        	 elem.setHeight(endJson.get(1).getAsDouble() - elem.getStartY());
        	 elem.setDepth(endJson.get(2).getAsDouble() - elem.getStartZ());
        	 
        	 elem.setName(name);
        	 collisionPanel.addElement(elem);
         });
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
    		String key = texture.get("key").getAsString();
    		
    		//TODO: delete texture from temp folder
    		TexturePath texturepath = new TexturePath("modid", directory, mcTexture);
    		Path textureFile = Files.createTempFile(mcTexture, ".png");
    		Files.write(textureFile, project.getFileData(mcTexture));
    		TextureManager.addImage(key, texturepath, textureFile.toFile());
    	}
    }

    public static void saveProject(SidebarManager manager, File name)
    {
        try
        {
            FileOutputStream fos = new FileOutputStream(name);
            ZipOutputStream zos = new ZipOutputStream(fos);

            //Model
            File model = getModelFile(manager.getModelPanel());
            addToZipFile(model, zos, "model.json");
            model.delete();

            //Textures
            JsonArray textureRoot = new JsonArray();
            for(TextureEntry entry : getAllTextures(manager.getModelPanel()))
            {
            	if(entry.getModId().equals("minecraft"))
            		continue;
            	
            	JsonObject textureJson = new JsonObject();
        		textureJson.addProperty("directory", entry.getDirectory());
        		textureJson.addProperty("texture", entry.getName());
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
            String blockJson = getBlockFile(manager.getCollisionPanel());
            addToZipFile(blockJson, zos, "block.json");
            
            //Block notes
            String notes = BlockManager.notes.getNotes();
            if(notes != null && !notes.isEmpty()) {
            	addToZipFile(notes, zos, "notes.txt");
            }

            zos.close();
            fos.close();
        }
        catch(IOException e)
        {
            Util.writeCrashLog(e);
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
        ExporterModel exporter = new ExporterModel(manager, "modid");
        exporter.setOptimize(false);
        exporter.setIncludeNonTexturedFaces(true);
        return exporter.writeFile(File.createTempFile("model.json", ""));
    }
    
    private static String getBlockFile(CollisionPanel collisionPanel) {
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
        	
        	String creativeTab = BlockManager.properties.getCreativeTab();
        	if(creativeTab != null && !creativeTab.isEmpty())
        		properties.addProperty("creative", creativeTab);
        	
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
            	crafting.addProperty("exact", BlockManager.crafting.isExactly());
            	crafting.addProperty("numOutput", BlockManager.crafting.getNumOutputItems());
            	JsonArray recipe = new JsonArray();
            	BlockManager.crafting.getCraftItems().forEach(recipe::add);
            	crafting.add("recipe", recipe);
            	rootObj.add("crafting", crafting);	
    		}
    	}
    	
    	//Loot
    	{
    		JsonObject loot = new JsonObject();
    		loot.addProperty("silk", BlockManager.loot.isSilkTouch());
    		loot.addProperty("num", BlockManager.loot.getNumDrops());
    		if(BlockManager.loot.getDropItem() != null) {
    			loot.addProperty("drop", BlockManager.loot.getDropItem());
    		}
    		rootObj.add("loot", loot);
    	}
    	
    	//Collision box
    	{
    		JsonArray collision = new JsonArray();
    		ListModel<ElementCellEntry> boxes = collisionPanel.getList().getModel();
    		for(int i = 0; i < boxes.getSize(); i++) {
    			Element elem = boxes.getElementAt(i).getElement();
    			JsonObject jsonElem = new JsonObject();
    			jsonElem.addProperty("name", elem.getName());
    			
    			//start
    			JsonArray startArray = new JsonArray();
    			startArray.add(elem.getStartX());
    			startArray.add(elem.getStartY());
    			startArray.add(elem.getStartZ());
    			jsonElem.add("start", startArray);
    			
    			//end
    			JsonArray endArray = new JsonArray();
    			endArray.add(elem.getStartX() + elem.getWidth());
    			endArray.add(elem.getStartY() + elem.getHeight());
    			endArray.add(elem.getStartZ() + elem.getDepth());
    			jsonElem.add("end", endArray);
    			
    			collision.add(jsonElem);
    		}
    		
    		rootObj.add("collision", collision);
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
