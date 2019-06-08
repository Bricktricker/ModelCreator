package com.mrcrayfish.modelcreator;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Settings
{
	private static Properties settings;
	
	private static final String SETTINGS_PATH = "settings.properties";
	
    private static final String IMAGE_IMPORT_DIR = "image_import_dir";
    private static final String MODEL_DIR = "model_dir";
    private static final String JSON_DIR = "json_dir";
    private static final String EXPORT_JSON_DIR = "export_json_dir";
    private static final String UNDO_LIMIT = "undo_limit";
    private static final String RENDER_CARDINAL_POINTS = "cardinal_points";
    private static final String EXTRACTED_ASSETS = "extracted_assets";
    private static final String FACE_COLORS = "face_colors";
    private static final String IMAGE_EDITOR = "image_editor";
    private static final String IMAGE_EDITOR_ARGS = "image_editor_args";

    public static final int[] DEFAULT_FACE_COLORS = {16711680, 65280, 255, 16776960, 16711935, 65535};

    public static String getImageImportDir()
    {
        return settings.getProperty(IMAGE_IMPORT_DIR, null);
    }

    public static void setImageImportDir(String dir)
    {
    	if(isNullOrEmpty(dir)) {
    		settings.remove(dir);
    	}else {
    		settings.put(IMAGE_IMPORT_DIR, dir);	
    	}
    }

    public static String getModelDir()
    {
        return settings.getProperty(MODEL_DIR, null);
    }

    public static void setModelDir(String dir)
    {
    	if(isNullOrEmpty(dir)) {
    		settings.remove(dir);
    	}else {
    		settings.put(MODEL_DIR, dir);	
    	}
    }

    public static String getJSONDir()
    {
        return settings.getProperty(JSON_DIR, null);
    }

    public static void setJSONDir(String dir)
    {
    	if(isNullOrEmpty(dir)) {
    		settings.remove(dir);
    	}else {
    		settings.put(JSON_DIR, dir);	
    	}
    }

    public static String getExportJSONDir()
    {
        return settings.getProperty(EXPORT_JSON_DIR, null);
    }

    public static void setExportJSONDir(String dir)
    {
    	if(isNullOrEmpty(dir)) {
    		settings.remove(dir);
    	}else {
    		settings.put(EXPORT_JSON_DIR, dir);	
    	}
    }
    
    public static List<String> getExtractedAssets() {
    	String concatedVersions = settings.getProperty(EXTRACTED_ASSETS);
    	if(isNullOrEmpty(concatedVersions)) {
    		return new ArrayList<>();
    	}
    	String[] versions = concatedVersions.split("\\|");
    	return Arrays.asList(versions);
    }
    
    public static void setExtractedAssets(List<String> versions) {
    	if(versions == null || versions.isEmpty()) {
    		settings.remove(EXTRACTED_ASSETS);
    		return;
    	}
    	
    	String concatedVersions = "";
    	for(String v : versions) {
    		concatedVersions += v + '|';
    	}
    	settings.put(EXTRACTED_ASSETS, concatedVersions);
    }
    
    public static void addExtractedAsset(String version) {
    	List<String> extractedVersions = getExtractedAssets();
    	extractedVersions.add(version);
    	setExtractedAssets(extractedVersions);
    }

    public static int getUndoLimit()
    {
        String s = settings.getProperty(UNDO_LIMIT, null);
        try
        {
            return Math.max(1, Integer.parseInt(s));
        }
        catch(NumberFormatException e)
        {
            return 50;
        }
    }

    public static void setUndoLimit(int limit)
    {
    	settings.put(UNDO_LIMIT, Integer.toString(Math.max(1, limit)));
    }

    public static boolean getCardinalPoints()
    {
        String s = settings.getProperty(RENDER_CARDINAL_POINTS, "true");
        return Boolean.parseBoolean(s);
    }

    public static void setCardinalPoints(boolean renderCardinalPoints)
    {
    	settings.put(RENDER_CARDINAL_POINTS, Boolean.toString(renderCardinalPoints));
    }

    public static int[] getFaceColors()
    {
        String s = settings.getProperty(FACE_COLORS, "");
        String[] values = s.split(",");
        if(values.length == 6)
        {
            int[] colors = new int[6];
            for(int i = 0; i < values.length; i++)
            {
                int color = Integer.parseInt(values[i]);
                colors[i] = color;
            }
            return colors;
        }
        return DEFAULT_FACE_COLORS;
    }

    public static void setFaceColors(int[] colors)
    {
        StringBuilder builder = new StringBuilder();
        for(int value : colors)
        {
            builder.append(value);
            builder.append(",");
        }
        builder.setLength(builder.length() - 1);
        settings.put(FACE_COLORS, builder.toString());
    }

    public static String getImageEditor()
    {
        return settings.getProperty(IMAGE_EDITOR, null);
    }

    public static void setImageEditor(String file)
    {
    	if(isNullOrEmpty(file)) {
    		settings.remove(file);
    	}else {
    		settings.put(IMAGE_EDITOR, file);	
    	}
    }

    public static String getImageEditorArgs()
    {
        return settings.getProperty(IMAGE_EDITOR_ARGS, "\"%s\"");
    }

    public static void setImageEditorArgs(String args)
    {
    	if(isNullOrEmpty(args)) {
    		settings.remove(args);
    	}else {
    		settings.put(IMAGE_EDITOR_ARGS, args);	
    	}
    }
    
    public static boolean saveSettings() {
    	try(FileOutputStream fos = new FileOutputStream(SETTINGS_PATH))
    	{
    		settings.store(fos, "");
    	}catch(IOException e) {
    		return false;
    	}
    	return true;
    }
    
    public static boolean load() {
    	settings = new Properties();
    	try(FileInputStream ifr = new FileInputStream(SETTINGS_PATH))
    	{
    		settings.load(ifr);
    	}catch(IOException e) {
    		return false;
    	}
    	return true;
    }
    
    private static boolean isNullOrEmpty(String s) {
    	return s == null || s.isEmpty();
    }
}
