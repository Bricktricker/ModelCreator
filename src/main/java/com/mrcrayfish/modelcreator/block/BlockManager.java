package com.mrcrayfish.modelcreator.block;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class BlockManager
{
	public static String assetID = "";
	public static String javaID = "";
	public static BlockProperties properties = new BlockProperties();
	public static String usedMcVersion = "";
	
	//May move to someware else?
	public static List<String> soundTypes;
	public static List<String> materials;
	
	public static void clear() {
		assetID = "";
		javaID = "";
		usedMcVersion = "";
		properties = new BlockProperties();
	}
	
	public static void loadResources() throws FileNotFoundException {
		ClassLoader classLoader = BlockManager.class.getClassLoader();
		
		//Block materials
		{
			File materialsFile = new File(classLoader.getResource("materials.json").getFile());
			BufferedReader reader = new BufferedReader(new FileReader(materialsFile));
			JsonParser parser = new JsonParser();
			JsonElement jsonElement = parser.parse(reader);
			JsonArray materialJsonArray = jsonElement.getAsJsonArray();
			materials = new ArrayList<>();
			materialJsonArray.forEach(v -> {
				materials.add(v.getAsString());
			});
			Collections.sort(materials);
		}
		
		//Block breaking sound
		{
			File soundsFile = new File(classLoader.getResource("SoundTypes.json").getFile());
			BufferedReader reader = new BufferedReader(new FileReader(soundsFile));
			JsonParser parser = new JsonParser();
			JsonElement jsonElement = parser.parse(reader);
			JsonArray soundJsonArray = jsonElement.getAsJsonArray();
			soundTypes = new ArrayList<>();
			soundJsonArray.forEach(v -> {
				soundTypes.add(v.getAsString());
			});
			Collections.sort(soundTypes);
		}
	}
	
}
