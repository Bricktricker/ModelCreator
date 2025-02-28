package com.mrcrayfish.modelcreator.block;

import com.mrcrayfish.modelcreator.display.DisplayProperties;

public class BlockManager
{
	public static String projectName = "";
	public static BlockProperties properties = new BlockProperties();
	public static BlockTranslation translation = new BlockTranslation();
	public static BlockCrafting crafting = new BlockCrafting();
	public static BlockLoot loot = new BlockLoot();
	public static BlockNotes notes = new BlockNotes();
	
	public static DisplayProperties displayProperties = new DisplayProperties(DisplayProperties.MODEL_CREATOR_BLOCK);
	
	public static void clear() {
		projectName = "";
		properties = new BlockProperties();
		translation = new BlockTranslation();
		crafting = new BlockCrafting();
		loot = new BlockLoot();
		notes = new BlockNotes();
		displayProperties = new DisplayProperties(DisplayProperties.MODEL_CREATOR_BLOCK);
	}
	
}
