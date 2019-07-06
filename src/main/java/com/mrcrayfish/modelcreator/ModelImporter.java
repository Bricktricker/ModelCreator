package com.mrcrayfish.modelcreator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mrcrayfish.modelcreator.block.BlockManager;
import com.mrcrayfish.modelcreator.component.TextureManager;
import com.mrcrayfish.modelcreator.display.DisplayProperties;
import com.mrcrayfish.modelcreator.element.Element;
import com.mrcrayfish.modelcreator.element.ElementManager;
import com.mrcrayfish.modelcreator.element.Face;
import com.mrcrayfish.modelcreator.panels.SidebarPanel;
import com.mrcrayfish.modelcreator.texture.TextureEntry;
import com.mrcrayfish.modelcreator.util.AssetsUtil;
import com.mrcrayfish.modelcreator.util.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class ModelImporter
{
    private Map<String, String> textureMap = new HashMap<>();
    private String[] faceNames = {"north", "east", "south", "west", "up", "down"};
    private String[] displayNames = {"gui", "ground", "fixed", "head", "firstperson_righthand", "firstperson_lefthand", "thirdperson_righthand", "thirdperson_lefthand"};

    // Input File
    private String modelData;

    // Model Variables
    private SidebarPanel manager;

    public ModelImporter(SidebarPanel manager, String modelData)
    {
        this.manager = manager;
        this.modelData = modelData;
    }

    public void importFromJSON()
    {
    	try
    	{
			readComponents();
		} catch (IOException e)
		{
			Util.writeCrashLog(e);
		}
    }

    private void readComponents() throws IOException
    {
        manager.clearElements();
        manager.setParticle(null);
        BlockManager.displayProperties = DisplayProperties.MODEL_CREATOR_BLOCK;

        JsonParser parser = new JsonParser();
        JsonElement read = parser.parse(this.modelData);
        readComponents(read);
    }
    
    private void readComponents(JsonElement read) throws IOException {
    	if(read.isJsonObject())
        {
            JsonObject obj = read.getAsJsonObject();

            if(obj.has("parent") && obj.get("parent").isJsonPrimitive())
            {
                String parent = obj.get("parent").getAsString();
                if(parent.contains(":")) {
                	String namespace = parent.split(":")[0];
                	if(!namespace.equals("minecraft")) {
                		System.err.println("Can not load models from non-minecraft namespace");
                		return;
                	}
                }
                File file = AssetsUtil.getModelPath(parent).toFile();
                if(!file.exists())
                {
                    parent = parent.substring(parent.lastIndexOf('/') + 1, parent.length());
                    file = AssetsUtil.getModelPath(parent).toFile();
                }

                if(file.exists())
                {
                    // load textures
                    loadTextures(obj);

                    // Load Parent
                    readExternalModel(file);
                }

                //return; //WHY??
            }

            // load textures
            loadTextures(obj);

            // load display properties
            if(obj.has("display") && obj.get("display").isJsonObject())
            {
                readDisplayProperties(obj.getAsJsonObject("display"));
            }

            // load elements
            if(obj.has("elements") && obj.get("elements").isJsonArray())
            {
                JsonArray elements = obj.get("elements").getAsJsonArray();

                for(int i = 0; i < elements.size(); i++)
                {
                    if(elements.get(i).isJsonObject())
                    {
                        readElement(elements.get(i).getAsJsonObject(), manager);
                    }
                }
            }

            manager.setAmbientOcc(true);
            if(obj.has("ambientocclusion") && obj.get("ambientocclusion").isJsonPrimitive())
            {
                manager.setAmbientOcc(obj.get("ambientocclusion").getAsBoolean());
            }
        }
    }
    
    private void readExternalModel(File file) throws IOException {
    	try(FileReader fr = new FileReader(file))
    	{
    		BufferedReader reader = new BufferedReader(fr);
            
            JsonParser parser = new JsonParser();
            JsonElement read = parser.parse(reader);
            
            readComponents(read);
            reader.close();
    	}
    }

    private void loadTextures(JsonObject obj)
    {
        if(obj.has("textures") && obj.get("textures").isJsonObject())
        {
            JsonObject textures = obj.get("textures").getAsJsonObject();

            for(Entry<String, JsonElement> entry : textures.entrySet())
            {
                if(entry.getValue().isJsonPrimitive())
                {
                    String key = entry.getKey().trim().toLowerCase(Locale.ENGLISH);
                    String value = entry.getValue().getAsString().trim().toLowerCase(Locale.ENGLISH);
                    if(!textureMap.containsKey(key))
                    {
                        if(key.equals("particle"))
                        {
                            manager.setParticle(this.loadTexture(key, value));
                        }
                        else if(!value.startsWith("#"))
                        {
                            textureMap.put(key, value);
                            this.loadTexture(key, value);
                        }
                    }
                }
            }
        }
    }

    private TextureEntry loadTexture(String id, String texture)
    {
        TexturePath texturePath = new TexturePath(texture);
        File textureFile = AssetsUtil.getTexturePath(texturePath.getPath()).toFile();
        if(textureFile.exists()) {
        	return TextureManager.addImage(id, texturePath, textureFile);	
        }
        //non vanilla texture, should be already loaded, if loaded from project
        //when loading a json file, textures may missing
        TextureEntry entry = TextureManager.getTexture(id);
        return entry;
    }

    private void readDisplayProperties(JsonObject obj)
    {
        DisplayProperties properties = BlockManager.displayProperties;
        properties.getEntries().forEach((s, entry) -> entry.setEnabled(false));
        for(String displayName : displayNames)
        {
            if(obj.has(displayName) && obj.get(displayName).isJsonObject())
            {
                readEntry(obj.getAsJsonObject(displayName), displayName, properties);
            }
        }
    }

    private void readEntry(JsonObject obj, String id, DisplayProperties properties)
    {
        DisplayProperties.Entry entry = new DisplayProperties.Entry(id, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        if(obj.has("rotation") && obj.get("rotation").isJsonArray())
        {
            JsonArray array = obj.get("rotation").getAsJsonArray();
            if(array.size() == 3)
            {
                entry.setRotationX(array.get(0).getAsDouble());
                entry.setRotationY(array.get(1).getAsDouble());
                entry.setRotationZ(array.get(2).getAsDouble());
            }
        }
        if(obj.has("translation") && obj.get("translation").isJsonArray())
        {
            JsonArray array = obj.get("translation").getAsJsonArray();
            if(array.size() == 3)
            {
                entry.setTranslationX(array.get(0).getAsDouble());
                entry.setTranslationY(array.get(1).getAsDouble());
                entry.setTranslationZ(array.get(2).getAsDouble());
            }
        }
        if(obj.has("scale") && obj.get("scale").isJsonArray())
        {
            JsonArray array = obj.get("scale").getAsJsonArray();
            if(array.size() == 3)
            {
                entry.setScaleX(array.get(0).getAsDouble());
                entry.setScaleY(array.get(1).getAsDouble());
                entry.setScaleZ(array.get(2).getAsDouble());
            }
        }
        properties.getEntries().put(id, entry);
    }

    private void readElement(JsonObject obj, ElementManager manager)
    {
        String name = "Element";
        JsonArray from = null;
        JsonArray to = null;

        if(obj.has("name") && obj.get("name").isJsonPrimitive())
        {
            name = obj.get("name").getAsString();
        }
        else if(obj.has("comment") && obj.get("comment").isJsonPrimitive())
        {
            name = obj.get("comment").getAsString();
        }
        else if(obj.has("__comment") && obj.get("__comment").isJsonPrimitive())
        {
            name = obj.get("__comment").getAsString();
        }
        if(obj.has("from") && obj.get("from").isJsonArray())
        {
            from = obj.get("from").getAsJsonArray();
        }
        if(obj.has("to") && obj.get("to").isJsonArray())
        {
            to = obj.get("to").getAsJsonArray();
        }

        if(from != null && to != null)
        {
            double x = from.get(0).getAsDouble();
            double y = from.get(1).getAsDouble();
            double z = from.get(2).getAsDouble();

            double w = to.get(0).getAsDouble() - x;
            double h = to.get(1).getAsDouble() - y;
            double d = to.get(2).getAsDouble() - z;

            Element element = new Element(w, h, d);
            element.setName(name);
            element.setStartX(x);
            element.setStartY(y);
            element.setStartZ(z);

            if(obj.has("rotation") && obj.get("rotation").isJsonObject())
            {
                JsonObject rot = obj.get("rotation").getAsJsonObject();

                if(rot.has("origin") && rot.get("origin").isJsonArray())
                {
                    JsonArray origin = rot.get("origin").getAsJsonArray();

                    double ox = origin.get(0).getAsDouble();
                    double oy = origin.get(1).getAsDouble();
                    double oz = origin.get(2).getAsDouble();

                    element.setOriginX(ox);
                    element.setOriginY(oy);
                    element.setOriginZ(oz);
                }

                if(rot.has("axis") && rot.get("axis").isJsonPrimitive())
                {
                    element.setRotationAxis(Element.parseAxisString(rot.get("axis").getAsString()));
                }

                if(rot.has("angle") && rot.get("angle").isJsonPrimitive())
                {
                    element.setRotation(rot.get("angle").getAsDouble());
                }

                if(rot.has("rescale") && rot.get("rescale").isJsonPrimitive())
                {
                    element.setRescale(rot.get("rescale").getAsBoolean());
                }
            }

            element.setShade(true);
            if(obj.has("shade") && obj.get("shade").isJsonPrimitive())
            {
                element.setShade(obj.get("shade").getAsBoolean());
            }

            for(Face face : element.getAllFaces())
            {
                face.setEnabled(false);
            }

            if(obj.has("faces") && obj.get("faces").isJsonObject())
            {
                JsonObject faces = obj.get("faces").getAsJsonObject();

                for(String faceName : faceNames)
                {
                    if(faces.has(faceName) && faces.get(faceName).isJsonObject())
                    {
                        readFace(faces.get(faceName).getAsJsonObject(), faceName, element);
                    }
                }
            }

            manager.addElement(element);
        }
    }

    private void readFace(JsonObject obj, String name, Element element)
    {
        Face face = null;
        for(Face f : element.getAllFaces())
        {
            if(f.getSide() == Face.getFaceSide(name))
            {
                face = f;
            }
        }

        if(face != null)
        {
            face.setEnabled(true);

            // automatically set uv if not specified
            face.setEndU(element.getFaceDimension(face.getSide()).getWidth());
            face.setEndV(element.getFaceDimension(face.getSide()).getHeight());
            face.setAutoUVEnabled(true);

            if(obj.has("uv") && obj.get("uv").isJsonArray())
            {
                JsonArray uv = obj.get("uv").getAsJsonArray();

                double uStart = uv.get(0).getAsDouble();
                double vStart = uv.get(1).getAsDouble();
                double uEnd = uv.get(2).getAsDouble();
                double vEnd = uv.get(3).getAsDouble();

                face.setStartU(uStart);
                face.setStartV(vStart);
                face.setEndU(uEnd);
                face.setEndV(vEnd);

                if(element.getFaceDimension(face.getSide()).getWidth() != face.getEndU() - face.getStartU() || element.getFaceDimension(face.getSide()).getHeight() != face.getEndV() - face.getStartV())
                {
                    face.setAutoUVEnabled(false);
                }
            }

            if(obj.has("texture") && obj.get("texture").isJsonPrimitive())
            {
                String id = obj.get("texture").getAsString().replace("#", "");
                TextureEntry entry = TextureManager.getTexture(id);
                if(entry != null)
                {
                    face.setTexture(entry);
                }
            }

            if(obj.has("rotation") && obj.get("rotation").isJsonPrimitive())
            {
                face.setRotation((int) obj.get("rotation").getAsDouble() / 90);
            }

            // TODO cullface with different direction than face,tintindex
            if(obj.has("cullface") && obj.get("cullface").isJsonPrimitive())
            {
                String cullface = obj.get("cullface").getAsString();

                if(cullface.equals(Face.getFaceName(face.getSide())))
                {
                    face.setCullface(true);
                }
            }

            if(obj.has("tintindex") && obj.get("tintindex").isJsonPrimitive())
            {
                int tintIndex = obj.get("tintindex").getAsInt();
                if(tintIndex >= 0)
                {
                    face.setTintIndexEnabled(true);
                    face.setTintIndex(tintIndex);
                }
            }
        }
    }
}
