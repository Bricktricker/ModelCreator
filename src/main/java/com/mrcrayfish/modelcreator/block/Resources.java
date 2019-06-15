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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Resources
{
	public static List<String> soundTypes;
	public static List<String> materials;
	public static List<LangPair> languages;
	public static List<String> items;
	
	public static class LangPair {
		public String key;
		public String name;
		
		public LangPair(String key, String name) {
			this.key = key;
			this.name = name;
		}
		
		@Override
		public String toString() {
			return this.name;
		}
	}
	
	public static void loadResources() throws FileNotFoundException {
		ClassLoader classLoader = Resources.class.getClassLoader();
		
		//Block materials
		{
			File materialsFile = new File(classLoader.getResource("block/materials.json").getFile());
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
			File soundsFile = new File(classLoader.getResource("block/SoundTypes.json").getFile());
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
		
		//Minecraft languages
		{
			File langFile = new File(classLoader.getResource("block/Languages.json").getFile());
			BufferedReader reader = new BufferedReader(new FileReader(langFile));
			JsonParser parser = new JsonParser();
			JsonElement jsonElement = parser.parse(reader);
			JsonArray langJsonArray = jsonElement.getAsJsonArray();
			languages = new ArrayList<>();
			langJsonArray.forEach(v -> {
				JsonObject langObject = v.getAsJsonObject();
				String id = langObject.get("id").getAsString();
				String name = langObject.get("name").getAsString();
				languages.add(new LangPair(id, name));
			});
		}
		
		//Items
		{
			File itemFile = new File(classLoader.getResource("block/Items.json").getFile());
			BufferedReader reader = new BufferedReader(new FileReader(itemFile));
			JsonParser parser = new JsonParser();
			JsonElement jsonElement = parser.parse(reader);
			JsonArray itemJsonArray = jsonElement.getAsJsonArray();
			items = new ArrayList<>();
			itemJsonArray.forEach(i -> {
				items.add(i.getAsString());
			});
		}
	}
}
