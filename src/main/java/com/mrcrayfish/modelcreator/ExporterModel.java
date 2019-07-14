package com.mrcrayfish.modelcreator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mrcrayfish.modelcreator.block.BlockManager;
import com.mrcrayfish.modelcreator.display.DisplayProperties;
import com.mrcrayfish.modelcreator.element.Element;
import com.mrcrayfish.modelcreator.element.Face;
import com.mrcrayfish.modelcreator.panels.SidebarPanel;
import com.mrcrayfish.modelcreator.texture.TextureEntry;
import com.mrcrayfish.modelcreator.util.Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ExporterModel
{
	/**
     * decimalformatter for rounding
     */
    public static final DecimalFormat FORMAT = new DecimalFormat("#.###");
    private static final DecimalFormatSymbols SYMBOLS = new DecimalFormatSymbols();
    
    static
    {
        SYMBOLS.setDecimalSeparator('.');
        FORMAT.setDecimalFormatSymbols(SYMBOLS);
    }
    
    private static final String[] DISPLAY_PROPERTY_ORDER = {"gui", "ground", "fixed", "head", "firstperson_righthand", "thirdperson_righthand"};

    private Map<String, String> textureMap = new HashMap<>();
    private String modid;
    private SidebarPanel manager;
    private boolean optimize = true;
    private boolean includeNames = true;
    private boolean displayProps = true;
    private boolean includeNonTexturedFaces = false;

    public ExporterModel(SidebarPanel manager, String modid)
    {
        this.modid = modid;
        this.manager = manager;
        compileTextureList();
    }

    public void setOptimize(boolean optimize)
    {
        this.optimize = optimize;
    }

    public void setIncludeNames(boolean includeNames)
    {
        this.includeNames = includeNames;
    }

    public void setDisplayProps(boolean displayProps)
    {
        this.displayProps = displayProps;
    }

    public void setIncludeNonTexturedFaces(boolean includeNonTexturedFaces)
    {
        this.includeNonTexturedFaces = includeNonTexturedFaces;
    }
    
    public File writeFile(File file) {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file)))
        {
            if(!file.exists()) {
                file.createNewFile();
            }
            
            writer.write(this.write());
            return file;
        }
        catch(IOException e) {
            Util.writeCrashLog(e);
        }
        return null;
    }

    private void compileTextureList()
    {
        for(Element cuboid : manager.getAllElements())
        {
            for(Face face : cuboid.getAllFaces())
            {
                if(face.getTexture() != null && face.isEnabled() && (!optimize || face.isVisible(manager)))
                {
                    TextureEntry entry = face.getTexture();
                    TexturePath path = entry.getTexturePath();
                    
                    String modid = path.getModId().equals("minecraft") ? "minecraft" : this.modid;
                    textureMap.put(entry.getKey(), entry.getTexturePath().toString(modid));
                }
            }
        }
    }
    
    /**
     * Generates model json
     * @return json string
     */
    public String write() {
    	Gson builder = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    	JsonObject root = new JsonObject();
    	
    	root.addProperty("__comment", "Model generated using MrCrayfish's Model Creator (https://mrcrayfish.com/tools?id=mc)");
    	
    	if(displayProps) {
    		root.add("display", writeDisplayProperties());
    	}else{
    		root.addProperty("parent", "block/block");
    	}
    	
    	if(!manager.getAmbientOcc()) {
    		root.addProperty("ambientocclusion", manager.getAmbientOcc());
    	}
    	
    	root.add("textures", writeTextures());
    	
    	JsonArray elements = new JsonArray();
    	manager.getAllElements().stream().filter(e -> canWriteElement(e)).map(e -> writeElement(e)).forEach(elements::add);
    	root.add("elements", elements);
    	
    	String modelJson = builder.toJson(root);
    	
    	Pattern doubleArrayPattern = Pattern.compile("\\[(\\s+[+-]?\\d*\\.?\\d+,?)+\\s+\\]"); //Matches json pretty printed array with double values
    	Matcher matcher = doubleArrayPattern.matcher(modelJson);

    	while(matcher.find()) {
    		String text = matcher.group();
    		String modText = text.replaceAll("\\s+", " ");
    		modelJson = modelJson.replace(text, modText);
    	}
    	
    	return modelJson;
    }
    
    private JsonObject writeTextures() {
    	JsonObject textures = new JsonObject();
    	
    	if(manager.getParticle() != null) {
    		TextureEntry entry = manager.getParticle();
    		String particlePath = entry.getModId() + ":";
    		if(!entry.getDirectory().isEmpty()) {
    			particlePath += entry.getDirectory() + "/";
            }
    		particlePath += entry.getName();
    		textures.addProperty("particle", particlePath);
    	}
    	
    	textureMap.keySet().forEach(key -> {
    		String texture = textureMap.get(key);
    		textures.addProperty(key, texture);
    	});
    	
    	return textures;
    }
    
    private JsonObject writeElement(Element cuboid) {
    	JsonObject element = new JsonObject();
    	
    	if(includeNames) {
    		element.addProperty("name", cuboid.getName());
    	}
    	
    	//bounds
    	JsonArray from = new JsonArray();
    	from.add(cuboid.getStartX());
    	from.add(cuboid.getStartY());
    	from.add(cuboid.getStartZ());
    	element.add("from", from);
    	
    	JsonArray to = new JsonArray();
    	to.add(cuboid.getStartX() + cuboid.getWidth());
    	to.add(cuboid.getStartY() + cuboid.getHeight());
    	to.add(cuboid.getStartZ() + cuboid.getDepth());
    	element.add("to", to);
    	
    	if(!cuboid.isShaded()) {
    		element.addProperty("shade", cuboid.isShaded());
    	}
    	
    	if(cuboid.getRotation() != 0.0) {
    		JsonObject rotation = new JsonObject();
    		
    		//Origin
    		JsonArray origin = new JsonArray();
    		origin.add(cuboid.getOriginX());
    		origin.add(cuboid.getOriginY());
    		origin.add(cuboid.getOriginZ());
    		rotation.add("origin", origin);
    		
    		//axis
    		rotation.addProperty("axis", Element.parseAxis(cuboid.getRotationAxis()));

    		//angle
    		rotation.addProperty("angle", cuboid.getRotation());
    		
    		element.add("rotation", rotation);
    	}
    	
    	//faces
    	JsonObject faces = new JsonObject();
    	Stream.of(cuboid.getAllFaces()).filter(face -> face.isEnabled() && (includeNonTexturedFaces || face.getTexture() != null) && (!optimize || face.isVisible(manager)))
    	.forEach(face -> {
    		JsonObject jsonFace = new JsonObject();
    		
    		if(face.getTexture() != null) {
    			jsonFace.addProperty("texture", face.getTexture().getKey());
    			
    			JsonArray uv = new JsonArray();
    			uv.add(face.getStartU());
    			uv.add(face.getStartV());
    			uv.add(face.getEndU());
    			uv.add(face.getEndV());
    			jsonFace.add("uv", uv);
    			
    			if(face.getRotation() > 0) {
    				jsonFace.addProperty("rotation", face.getRotation() * 90);
    			}
    			
    			if(face.isCullfaced()) {
    				jsonFace.addProperty("cullface", Face.getFaceName(face.getSide()));
    			}
    			
    			if(face.isTintIndexEnabled() && face.getTintIndex() >= 0) {
    				jsonFace.addProperty("tintindex", face.getTintIndex());
    			}
    		}
    		
    		faces.add(Face.getFaceName(face.getSide()), jsonFace);
    	});
    	
    	element.add("faces", faces);
    	
    	return element;
    }
    
    private JsonObject writeDisplayProperties() {
    	JsonObject display = new JsonObject();
    	Map<String, DisplayProperties.Entry> entries = BlockManager.displayProperties.getEntries();
        List<String> ids = new ArrayList<>();
        for(String id : DISPLAY_PROPERTY_ORDER)
        {
            DisplayProperties.Entry entry = entries.get(id);
            if(entry != null && entry.isEnabled())
            {
                ids.add(id);
            }
        }
        
        ids.forEach(key -> {
        	JsonObject jsonEntry = new JsonObject();
        	DisplayProperties.Entry entry = entries.get(key);
        	
        	//rotation
        	JsonArray rotation = new JsonArray();
        	rotation.add(entry.getRotationX());
        	rotation.add(entry.getRotationY());
        	rotation.add(entry.getRotationZ());
        	jsonEntry.add("rotation", rotation);
        	
        	//translation
        	JsonArray translation = new JsonArray();
        	translation.add(entry.getTranslationX());
        	translation.add(entry.getTranslationY());
        	translation.add(entry.getTranslationZ());
        	jsonEntry.add("translation", translation);
        	
        	//scale
        	JsonArray scale = new JsonArray();
        	scale.add(entry.getScaleX());
        	scale.add(entry.getScaleY());
        	scale.add(entry.getScaleZ());
        	jsonEntry.add("scale", scale);
        	
        	display.add(key, jsonEntry);
        });

    	
    	return display;
    }

    private boolean canWriteElement(Element element)
    {
        for(Face face : element.getAllFaces())
        {
            if(face.isEnabled() && (includeNonTexturedFaces || face.getTexture() != null) && (!optimize || face.isVisible(manager)))
            {
                return true;
            }
        }
        return false;
    }
}
