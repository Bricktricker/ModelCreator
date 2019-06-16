package com.mrcrayfish.modelcreator.block;

public class BlockManager
{
	public static String projectName = "";
	public static BlockProperties properties = new BlockProperties();
	public static BlockTranslation translation = new BlockTranslation();
	public static BlockCrafting crafting = new BlockCrafting();
	public static BlockLoot loot = new BlockLoot();
	public static BlockNotes notes = new BlockNotes();
	
	public static void clear() {
		projectName = "";
		properties = new BlockProperties();
		translation = new BlockTranslation();
		crafting = new BlockCrafting();
		loot = new BlockLoot();
		notes = new BlockNotes();
	}
	
}
