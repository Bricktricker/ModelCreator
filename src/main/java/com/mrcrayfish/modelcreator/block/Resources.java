package com.mrcrayfish.modelcreator.block;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
	public static List<String> creativeTabs;
	
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
	
	public static void loadResources(Class<?> clazz) throws FileNotFoundException {
		ClassLoader classLoader = clazz.getClassLoader();
		
		//Block materials
		{
			InputStream stream = classLoader.getResourceAsStream("res/Materials.json");
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			JsonParser parser = new JsonParser();
			JsonElement jsonElement = parser.parse(reader);
			JsonArray materialJsonArray = jsonElement.getAsJsonArray();
			materials = new ArrayList<>();
			materialJsonArray.forEach(v -> {
				materials.add(v.getAsString());
			});
			Collections.sort(materials);
			materials = Collections.unmodifiableList(materials);
		}
		
		//Block breaking sound
		{
			InputStream stream = classLoader.getResourceAsStream("res/SoundTypes.json");
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			JsonParser parser = new JsonParser();
			JsonElement jsonElement = parser.parse(reader);
			JsonArray soundJsonArray = jsonElement.getAsJsonArray();
			soundTypes = new ArrayList<>();
			soundJsonArray.forEach(v -> {
				soundTypes.add(v.getAsString());
			});
			Collections.sort(soundTypes);
			soundTypes = Collections.unmodifiableList(soundTypes);
		}
		
		//Minecraft languages
		{
			InputStream stream = classLoader.getResourceAsStream("res/Languages.json");
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
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
			languages = Collections.unmodifiableList(languages);
		}
		
		//Items
		{
			InputStream stream = classLoader.getResourceAsStream("res/Items.json");
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			JsonParser parser = new JsonParser();
			JsonElement jsonElement = parser.parse(reader);
			JsonArray itemJsonArray = jsonElement.getAsJsonArray();
			items = new ArrayList<>();
			itemJsonArray.forEach(i -> {
				items.add(i.getAsString());
			});
			Collections.sort(items);
			items = Collections.unmodifiableList(items);
		}
		
		//Creative tabs
		{
			InputStream stream = classLoader.getResourceAsStream("res/CreativeTabs.json");
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			JsonParser parser = new JsonParser();
			JsonElement jsonElement = parser.parse(reader);
			JsonArray creativeJsonArray = jsonElement.getAsJsonArray();
			creativeTabs = new ArrayList<>();
			creativeJsonArray.forEach(i -> {
				creativeTabs.add(i.getAsString());
			});
			creativeTabs = Collections.unmodifiableList(creativeTabs);
		}
	}
}
