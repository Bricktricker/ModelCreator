package com.mrcrayfish.modelcreator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mrcrayfish.modelcreator.block.BlockManager;
import com.mrcrayfish.modelcreator.display.DisplayProperties;
import com.mrcrayfish.modelcreator.element.Element;
import com.mrcrayfish.modelcreator.element.ElementManager;
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
    private ElementManager manager; //TODO: set ad SidebarPanel
    private boolean optimize = true;
    private boolean includeNames = true;
    private boolean displayProps = true;
    private boolean includeNonTexturedFaces = false;

    public ExporterModel(ElementManager manager, String modid)
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
    
    public File writeFile(File file)
    {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file)))
        {
            if(!file.exists())
            {
                file.createNewFile();
            }
            this.write(writer);
            return file;
        }
        catch(IOException e)
        {
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
                    
                    //temporarily overwrite new modid
                    //TODO: overload toString to take a modid
                    String oldModid = path.getModId();
                    if(!oldModid.equals("minecraft")) {
                    	assert(oldModid.equals(modid));
                    	path.setModId(modid);
                    }
                    textureMap.put(entry.getKey(), entry.getTexturePath().toString());
                    path.setModId(oldModid);
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
    	
    	if(!((SidebarPanel)manager).getAmbientOcc()) {
    		root.addProperty("ambientocclusion", ((SidebarPanel)manager).getAmbientOcc());
    	}
    	
    	root.add("textures", writeTextures());
    			
    	return builder.toJson(root);
    }

    @Deprecated
    public void write(BufferedWriter writer) throws IOException
    {
        writer.write("{");
        writer.newLine();

        writer.write(space(1) + "\"__comment\": \"Model generated using MrCrayfish's Model Creator (https://mrcrayfish.com/tools?id=mc)\",");
        writer.newLine();
        
        if(displayProps) {
            writeDisplayProperties(writer);
            writer.newLine();
        }else{
        	writer.write(space(1) + "\"parent\": \"block/block\",");
        	writer.newLine();
        }

        if(!((SidebarPanel)manager).getAmbientOcc())
        {
            writer.write("\"ambientocclusion\": " + ((SidebarPanel)manager).getAmbientOcc() + ",");
            writer.newLine();
        }

        writeTextures(writer);
        writer.newLine();

        writer.write(space(1) + "\"elements\": [");

        for(int i = 0; i < manager.getElementCount() - 1; i++)
        {
            Element element = manager.getElement(i);
            if(canWriteElement(element))
            {
                writeElement(writer, manager.getElement(i));
                writer.write(",");
            }
        }
        if(manager.getElementCount() > 0)
        {
            Element element = manager.getElement(manager.getElementCount() - 1);
            if(canWriteElement(element))
            {
                writeElement(writer, manager.getElement(manager.getElementCount() - 1));
            }
        }

        writer.newLine();
        writer.write(space(1) + "]");
        writer.newLine();
        writer.write("}");
    }
    
    private JsonObject writeTextures() {
    	JsonObject textures = new JsonObject();
    	
    	if(((SidebarPanel)manager).getParticle() != null) {
    		TextureEntry entry = ((SidebarPanel)manager).getParticle();
    		String particlePath = this.modid + ":";
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

    @Deprecated
    private void writeTextures(BufferedWriter writer) throws IOException
    {
        writer.write(space(1) + "\"textures\": {");
        writer.newLine();
        if(((SidebarPanel)manager).getParticle() != null)
        {
            TextureEntry entry = ((SidebarPanel)manager).getParticle();
            writer.write(space(2) + "\"particle\": \"" + this.modid + ":");
            if(!entry.getDirectory().isEmpty())
            {
                writer.write(entry.getDirectory() + "/");
            }
            writer.write(entry.getName() + "\"");
            if(textureMap.size() > 0)
            {
                writer.write(",");
            }
            writer.newLine();
        }

        List<String> ids = new ArrayList<>(textureMap.keySet());
        for(int i = 0; i < ids.size() - 1; i++)
        {
            String id = ids.get(i);
            String texture = textureMap.get(id);
            writer.write(space(2) + "\"" + id + "\": \"" + texture + "\"");
            writer.write(",");
            writer.newLine();
        }
        if(ids.size() > 0)
        {
            String id = ids.get(ids.size() - 1);
            String texture = textureMap.get(id);
            writer.write(space(2) + "\"" + id + "\": \"" + texture + "\"");
            writer.newLine();
        }

        writer.write(space(1) + "},");
    }
    
    private JsonObject writeElement(Element cuboid) {
    	JsonObject element = new JsonObject();
    	
    	if(includeNames) {
    		element.addProperty("name", cuboid.getName());
    	}
    	
    	//bounds
    	JsonArray from = new JsonArray();
    	from.add(FORMAT.format(cuboid.getStartX()));
    	from.add(FORMAT.format(cuboid.getStartY()));
    	from.add(FORMAT.format(cuboid.getStartZ()));
    	element.add("from", from);
    	
    	JsonArray to = new JsonArray();
    	to.add(FORMAT.format(cuboid.getStartX() + cuboid.getWidth()));
    	to.add(FORMAT.format(cuboid.getStartY() + cuboid.getHeight()));
    	to.add(FORMAT.format(cuboid.getStartZ() + cuboid.getDepth()));
    	element.add("to", to);
    	
    	if(!cuboid.isShaded()) {
    		element.addProperty("shade", cuboid.isShaded());
    	}
    	
    	if(cuboid.getRotation() != 0.0) {
    		JsonObject rotation = new JsonObject();
    		//Continue here
    		element.add("rotation", rotation);
    	}
    	
    	
    	return element;
    }

    private void writeElement(BufferedWriter writer, Element cuboid) throws IOException
    {
        writer.newLine();
        writer.write(space(2) + "{");
        writer.newLine();
        if(includeNames)
        {
            writer.write(space(3) + "\"name\": \"" + cuboid.getName() + "\",");
            writer.newLine();
        }
        writeBounds(writer, cuboid);
        writer.newLine();
        if(!cuboid.isShaded())
        {
            writeShade(writer, cuboid);
            writer.newLine();
        }
        if(cuboid.getRotation() != 0)
        {
            writeRotation(writer, cuboid);
            writer.newLine();
        }
        writeFaces(writer, cuboid);
        writer.newLine();
        writer.write(space(2) + "}");
    }

    private void writeBounds(BufferedWriter writer, Element cuboid) throws IOException
    {
        writer.write(space(3) + "\"from\": [ " + FORMAT.format(cuboid.getStartX()) + ", " + FORMAT.format(cuboid.getStartY()) + ", " + FORMAT.format(cuboid.getStartZ()) + " ], ");
        writer.newLine();
        writer.write(space(3) + "\"to\": [ " + FORMAT.format(cuboid.getStartX() + cuboid.getWidth()) + ", " + FORMAT.format(cuboid.getStartY() + cuboid.getHeight()) + ", " + FORMAT.format(cuboid.getStartZ() + cuboid.getDepth()) + " ], ");
    }

    private void writeShade(BufferedWriter writer, Element cuboid) throws IOException
    {
        writer.write(space(3) + "\"shade\": " + cuboid.isShaded() + ",");
    }

    private void writeRotation(BufferedWriter writer, Element cuboid) throws IOException
    {
        writer.write(space(3) + "\"rotation\": { ");
        writer.write("\"origin\": [ " + FORMAT.format(cuboid.getOriginX()) + ", " + FORMAT.format(cuboid.getOriginY()) + ", " + FORMAT.format(cuboid.getOriginZ()) + " ], ");
        writer.write("\"axis\": \"" + Element.parseAxis(cuboid.getRotationAxis()) + "\", ");
        writer.write("\"angle\": " + cuboid.getRotation());
        if(cuboid.shouldRescale())
        {
            writer.write(", \"rescale\": " + cuboid.shouldRescale());
        }
        writer.write(" },");
    }

    private void writeFaces(BufferedWriter writer, Element cuboid) throws IOException
    {
        writer.write(space(3) + "\"faces\": {");
        writer.newLine();

        /* Creates a list of all the valid faces to export */
        List<Face> validFaces = new ArrayList<>();
        for(Face face : cuboid.getAllFaces())
        {
            if(face.isEnabled() && (includeNonTexturedFaces || face.getTexture() != null) && (!optimize || face.isVisible(manager)))
            {
                validFaces.add(face);
            }
        }

        /* Writes the valid faces to the writer */
        for(int i = 0; i < validFaces.size() - 1; i++)
        {
            Face face = validFaces.get(i);
            writeFace(writer, face);
            writer.write(",");
            writer.newLine();
        }
        if(validFaces.size() > 0)
        {
            writeFace(writer, validFaces.get(validFaces.size() - 1));
        }

        writer.newLine();
        writer.write(space(3) + "}");
    }

    private void writeFace(BufferedWriter writer, Face face) throws IOException
    {
        writer.write(space(4) + "\"" + Face.getFaceName(face.getSide()) + "\": { ");
        if(face.getTexture() != null)
        {
            writer.write("\"texture\": \"#" + face.getTexture().getKey() + "\"");
            writer.write(", \"uv\": [ " + FORMAT.format(face.getStartU()) + ", " + FORMAT.format(face.getStartV()) + ", " + FORMAT.format(face.getEndU()) + ", " + FORMAT.format(face.getEndV()) + " ]");
            if(face.getRotation() > 0)
            {
                writer.write(", \"rotation\": " + face.getRotation() * 90);
            }
            if(face.isCullfaced())
            {
                writer.write(", \"cullface\": \"" + Face.getFaceName(face.getSide()) + "\"");
            }
            if(face.isTintIndexEnabled() && face.getTintIndex() >= 0)
            {
                writer.write(", \"tintindex\": " + face.getTintIndex());
            }
        }
        writer.write(" }");
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
        	rotation.add(FORMAT.format(entry.getRotationX()));
        	rotation.add(FORMAT.format(entry.getRotationY()));
        	rotation.add(FORMAT.format(entry.getRotationZ()));
        	jsonEntry.add("rotation", rotation);
        	
        	//translation
        	JsonArray translation = new JsonArray();
        	translation.add(FORMAT.format(entry.getTranslationX()));
        	translation.add(FORMAT.format(entry.getTranslationY()));
        	translation.add(FORMAT.format(entry.getTranslationZ()));
        	jsonEntry.add("translation", translation);
        	
        	//scale
        	JsonArray scale = new JsonArray();
        	scale.add(FORMAT.format(entry.getScaleX()));
        	scale.add(FORMAT.format(entry.getScaleY()));
        	scale.add(FORMAT.format(entry.getScaleZ()));
        	jsonEntry.add("scale", scale);
        	
        	display.add(key, jsonEntry);
        });

    	
    	return display;
    }

    @Deprecated
    private void writeDisplayProperties(BufferedWriter writer) throws IOException
    {
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

        writer.write(space(1) + "\"display\": {");
        writer.newLine();

        for(int i = 0; i < ids.size() - 1; i++)
        {
            String key = ids.get(i);
            writeDisplayEntry(writer, key, entries.get(key));
            writer.write(",");
            writer.newLine();
        }
        if(ids.size() > 0)
        {
            String key = ids.get(ids.size() - 1);
            writeDisplayEntry(writer, key, entries.get(key));
        }

        writer.newLine();
        writer.write(space(1) + "},");
    }

    @Deprecated
    private void writeDisplayEntry(BufferedWriter writer, String id, DisplayProperties.Entry entry) throws IOException
    {
        writer.write(space(2) + "\"" + id + "\": {");
        writer.newLine();
        writer.write(space(3) + String.format("\"rotation\": [ %s, %s, %s ],", FORMAT.format(entry.getRotationX()), FORMAT.format(entry.getRotationY()), FORMAT.format(entry.getRotationZ())));
        writer.newLine();
        writer.write(space(3) + String.format("\"translation\": [ %s, %s, %s ],", FORMAT.format(entry.getTranslationX()), FORMAT.format(entry.getTranslationY()), FORMAT.format(entry.getTranslationZ())));
        writer.newLine();
        writer.write(space(3) + String.format("\"scale\": [ %s, %s, %s ]", FORMAT.format(entry.getScaleX()), FORMAT.format(entry.getScaleY()), FORMAT.format(entry.getScaleZ())));
        writer.newLine();
        writer.write(space(2) + "}");
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
    
    protected String space(int size)
    {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < size; i++)
        {
            //TODO add setting to export with tabs instead
            builder.append("    ");
        }
        return builder.toString();
    }
}
