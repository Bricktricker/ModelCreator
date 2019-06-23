package com.mrcrayfish.modelcreator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.mrcrayfish.modelcreator.util.OperatingSystem;
import com.mrcrayfish.modelcreator.util.Util;

public class Settings
{
	private static Properties settings;
	
	private static final String SETTINGS_PATH = "settings.properties";
	
    private static final String PROJECTS_DIR = "projects_dir";
    private static final String JSON_DIR = "json_dir";
    private static final String UNDO_LIMIT = "undo_limit";
    private static final String RENDER_CARDINAL_POINTS = "cardinal_points";
    private static final String EXTRACTED_ASSETS = "extracted_assets";
    private static final String FACE_COLORS = "face_colors";
    private static final String IMAGE_EDITOR = "image_editor";
    private static final String IMAGE_EDITOR_ARGS = "image_editor_args";
    private static final String USED_MC_VERSION = "used_mc_version";
    private static final String MODID = "mod_id";
    private static final String RESOURCE_PATH = "resource_path";
    private static final String TEXT_EDITOR = "text_editor";

    public static final int[] DEFAULT_FACE_COLORS = {16711680, 65280, 255, 16776960, 16711935, 65535};

    public static String getProjectsDir()
    {
        return settings.getProperty(PROJECTS_DIR, "projects");
    }

    public static void setProjectsDir(String dir)
    {
    	if(isNullOrEmpty(dir)) {
    		settings.remove(PROJECTS_DIR);
    	}else {
    		settings.put(PROJECTS_DIR, dir);	
    	}
    }

    //Used when importing JSON model, save the dir and open fileshooser next time there
    public static String getJSONDir()
    {
        return settings.getProperty(JSON_DIR, null);
    }

    public static void setJSONDir(String dir)
    {
    	if(isNullOrEmpty(dir)) {
    		settings.remove(JSON_DIR);
    	}else {
    		settings.put(JSON_DIR, dir);	
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
    	if(extractedVersions.isEmpty()) {
    		setUsedMcVersion(version);
    	}
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
    		settings.remove(IMAGE_EDITOR);
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
    		settings.remove(IMAGE_EDITOR_ARGS);
    	}else {
    		settings.put(IMAGE_EDITOR_ARGS, args);	
    	}
    }
    
    public static String getUsedMcVersion() {
    	return settings.getProperty(USED_MC_VERSION);
    }
    
    public static void setUsedMcVersion(String version) {
    	if(isNullOrEmpty(version)) {
    		settings.remove(USED_MC_VERSION);
    	}else {
    		settings.put(USED_MC_VERSION, version);	
    	}
    }
    
    public static String getModID() {
    	return settings.getProperty(MODID, "");
    }
    
    public static void setModID(String modid) {
    	if(isNullOrEmpty(modid)) {
    		settings.remove(MODID);
    	}else {
    		settings.put(MODID, modid);
    	}
    }
    
    public static String getResourcePath() {
    	return settings.getProperty(RESOURCE_PATH, "");
    }
    
    public static void setResourcePath(String path) {
    	if(isNullOrEmpty(path)) {
    		settings.remove(RESOURCE_PATH);
    	}else {
    		settings.put(RESOURCE_PATH, path);
    	}
    }
    
    public static String getTextEditorPath() {
    	if(settings.containsKey(TEXT_EDITOR)) {
    		return settings.getProperty(TEXT_EDITOR);
    	}
    	
    	if(OperatingSystem.get() == OperatingSystem.WINDOWS) {
    		String winDir = System.getenv("WINDIR");
    		return winDir + "/system32/notepad.exe";
    	}else {
    		return "";
    	}
    }
    
    public static void setTextEditor(String path) {
    	if(isNullOrEmpty(path)) {
    		settings.remove(TEXT_EDITOR);
    	}else {
    		settings.put(TEXT_EDITOR, path);
    	}
    }
    
    public static void saveSettings() {
    	try(FileOutputStream fos = new FileOutputStream(SETTINGS_PATH))
    	{
    		settings.store(fos, "");
    	}catch(IOException e) {
    		Util.writeCrashLog(e);
    	}
    }
    
    public static boolean load() {
    	settings = new Properties();
    	try(FileInputStream ifr = new FileInputStream(SETTINGS_PATH))
    	{
    		settings.load(ifr);
    	}catch(FileNotFoundException e) {
    		return false;
    	} catch (IOException e) {
			Util.writeCrashLog(e);
		}
    	
    	if(getUsedMcVersion() == null) {
    		List<String> extractedAssets = getExtractedAssets();
    		if(!extractedAssets.isEmpty())
    			setUsedMcVersion(extractedAssets.get(0));
    	}
    	
    	return true;
    }
    
    private static boolean isNullOrEmpty(String s) {
    	return s == null || s.isEmpty();
    }
}
