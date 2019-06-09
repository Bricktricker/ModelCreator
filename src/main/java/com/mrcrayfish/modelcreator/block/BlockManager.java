package com.mrcrayfish.modelcreator.block;

public class BlockManager
{
	public static String assetID = "";
	public static String javaID = "";
	public static String usedMcVersion = "";
	public static BlockProperties properties = new BlockProperties();
	public static BlockTranslation translation = new BlockTranslation();
	
	public static void clear() {
		assetID = "";
		javaID = "";
		usedMcVersion = "";
		properties = new BlockProperties();
		translation = new BlockTranslation();
	}
	
}
