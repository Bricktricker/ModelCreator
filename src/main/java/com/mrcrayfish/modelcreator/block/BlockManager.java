package com.mrcrayfish.modelcreator.block;

public class BlockManager
{
	public static String projectName = "";
	public static BlockProperties properties = new BlockProperties();
	public static BlockTranslation translation = new BlockTranslation();
	
	public static void clear() {
		projectName = "";
		properties = new BlockProperties();
		translation = new BlockTranslation();
	}
	
}
