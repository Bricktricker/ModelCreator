package com.mrcrayfish.modelcreator.util;

import java.io.File;

import com.mrcrayfish.modelcreator.Settings;

/**
 * Author: MrCrayfish
 */
public class AssetsUtil
{
    public static String getTextureDirectory(File file)
    {
        StringBuilder builder = new StringBuilder();
        File parent = file;
        while((parent = parent.getParentFile()) != null)
        {
            if(!parent.getName().equals("textures"))
            {
                builder.insert(0, parent.getName()).insert(0, "/");
                continue;
            }
            return builder.length() > 0 ? builder.substring(1, builder.length()) : "";
        }
        return "blocks";
    }

    public static String getModId(File file)
    {
        String texturePath = file.getAbsolutePath();
        String assetPath = (new File(AssetsUtil.getAssetFolder())).getAbsolutePath();
    	if(texturePath.startsWith(assetPath)) {
    		return "minecraft";
    	}
        
    	//selected texture is not in default asset folder, assume is's not a vanilla texture
    	//return modid as "modid", to replace it later in the export step with the actual modid
    	return "modid";
    }
    
    public static String getAssetFolder() {
    	return "resources/" + Settings.getUsedMcVersion();
    }
    
    public static String getModelPath(String model) {
    	return getAssetFolder() + "/models/" + model + ".json";
    }
    
    public static String getTexturePath(String texture) {
    	return getAssetFolder() + "/textures/" + texture + ".png";
    }
}
