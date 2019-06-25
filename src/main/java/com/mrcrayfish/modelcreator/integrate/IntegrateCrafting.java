package com.mrcrayfish.modelcreator.integrate;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mrcrayfish.modelcreator.block.BlockManager;
import com.mrcrayfish.modelcreator.util.Util;

public class IntegrateCrafting extends Integrator
{
	
	@Override
	public String generate() {
		JsonObject recipe = BlockManager.crafting.isShapeLess() ? generateShapeless() : generateShaped();
		return builder.toJson(recipe);
	}
	
	@Override
	public void integrate() {
		Path craftingPath = getDataFolder().resolve("recipes").resolve(IntegrateDialog.assetName + ".json");
		try{
			writeToFile(craftingPath, content + "\n");
		} catch (IOException e) {
			Util.writeCrashLog(e);
		}
		startTextEditor(craftingPath);
	}
	
	private JsonObject generateShapeless() {
		JsonObject rootObj = new JsonObject();
		rootObj.addProperty("type", "minecraft:crafting_shapeless");
		
		Set<String> items = BlockManager.crafting.getCraftItems().stream().filter(s -> !s.isEmpty()).collect(Collectors.toSet());
		JsonArray ingredients = new JsonArray();
		items.forEach(item -> {
			JsonObject itemObj = new JsonObject();
			itemObj.addProperty("item", item);
			ingredients.add(itemObj);
		});
		rootObj.add("ingredients", ingredients);
		
		JsonObject result = new JsonObject();
		result.addProperty("item", getItemForBlock());
		result.addProperty("count", BlockManager.crafting.getNumOutputItems());
		rootObj.add("result", result);
		
		return rootObj;
	}
	
	private JsonObject generateShaped() {
		JsonObject rootObj = new JsonObject();
		
		rootObj.addProperty("type", "minecraft:crafting_shaped");
		//TODO. add groups https://github.com/skylinerw/guides/blob/master/java/recipes.md#groups
		
		JsonArray pattern = new JsonArray();
		Map<String, Character> keys = new HashMap<>(); 
		{
			String[] patternLines = new String[]{"", "", ""};
			char startChar = 'A';
			
			for(int i = 0; i < 9; i++) {
				String item = BlockManager.crafting.getCraftItems().get(i);
				if(!item.isEmpty()) {
					if(!keys.containsKey(item)) {
						keys.put(item, startChar++);
					}
					
					patternLines[i/3] += keys.get(item);
				}else {
					patternLines[i/3] += " ";
				}
			}
			pattern.add(patternLines[0]);
			pattern.add(patternLines[1]);
			pattern.add(patternLines[2]);
		}
		rootObj.add("pattern", pattern);
		
		JsonObject key = new JsonObject();
		{
			keys.forEach((item, value) -> {
				JsonObject keyJson = new JsonObject();
				keyJson.addProperty("item", item);
				key.add(String.valueOf(value), keyJson);
			});
		}
		rootObj.add("key", key);
		
		JsonObject result = new JsonObject();
		result.addProperty("item", getItemForBlock());
		result.addProperty("count", BlockManager.crafting.getNumOutputItems());
		rootObj.add("result", result);
		
		return rootObj;
	}

}
