package com.mrcrayfish.modelcreator;

import com.mrcrayfish.modelcreator.util.AssetsUtil;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Author: MrCrayfish
 */
public class TexturePath
{
    public static final Pattern PATTERN = Pattern.compile("([a-z_0-9]+:)?([a-z_0-9]+/)*[a-z_0-9]+");

    private String modId = "minecraft";
    private String directory;
    private String name;

    public TexturePath(String s)
    {
        String[] split = s.split(":");
        if(split.length == 2)
        {
            this.modId = split[0];
        }
        String assetPath = split[split.length - 1];
        int dirSlash = Math.max(0, assetPath.lastIndexOf("/"));
        this.directory = assetPath.substring(0, dirSlash);
        this.name = assetPath.substring(dirSlash+1);
    }

    public TexturePath(File file)
    {
        this.modId = AssetsUtil.getModId(file); //Either "minecraft" or "modid"
        this.directory = this.modId.equals("minecraft") ? AssetsUtil.getTextureDirectory(file) : "block";
        this.name = file.getName().substring(0, file.getName().indexOf("."));
    }
    
    public TexturePath(String modid, String directory, String name) {
    	this.modId = modid; //allways "modid"
    	this.directory = directory;
    	this.name = name;
    }

    public String getModId()
    {
        return modId;
    }
    
    public void setModId(String modid)
    {
    	this.modId = modid;
    }

    public String getDirectory()
    {
        return directory;
    }

    public String getName()
    {
        return name;
    }
    
    public String getPath() {
    	return directory + File.separator + name + ".png";
    }

    @Override
    public String toString()
    {
        return modId + ":" + directory + "/" + name;
    }
    
    public String toString(String modid) {
    	return modid + ":" + directory + "/" + name;
    }

    public String toRelativePath()
    {
        return modId + File.separator + "textures" + File.separator + directory + File.separator + name + ".png";
    }
}
