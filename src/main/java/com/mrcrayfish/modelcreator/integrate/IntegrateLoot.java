package com.mrcrayfish.modelcreator.integrate;

import java.io.IOException;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mrcrayfish.modelcreator.block.BlockManager;
import com.mrcrayfish.modelcreator.util.Util;

public class IntegrateLoot extends Integrator
{
	
	@Override
	public String generate() {
		JsonObject loot = new JsonObject();
		loot.addProperty("type", "minecraft:block");
		JsonArray pools = new JsonArray();
		
		JsonObject pool = new JsonObject();
		pool.addProperty("rools", BlockManager.loot.getNumDrops());
		
		JsonArray entries = new JsonArray();
		JsonObject entry = new JsonObject();
		entry.addProperty("type", "minecraft:item");
		String drop;
		if(BlockManager.loot.getDropItem() == null) {
			drop = getItemForBlock();
		}else {
			drop = addModid(BlockManager.loot.getDropItem());
		}
		
		entry.addProperty("name", drop);
		entries.add(entry);
		pool.add("entries", entries);
		
		JsonArray conditions = new JsonArray();
		JsonObject condition = BlockManager.loot.isSilkTouch() ? getSilkCondition() : getExplotionCondition();
		conditions.add(condition);
		pool.add("conditions", conditions);
		
		pools.add(pool);
		loot.add("pools", pools);
		
		Gson g = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		return g.toJson(loot);
	}
	
	@Override
	public void integrate() {
		Path craftingPath = getDataFolder().resolve("loot_tables").resolve(IntegrateDialog.assetName + ".json");
		try{
			writeToFile(craftingPath, content + "\n");
		} catch (IOException e) {
			Util.writeCrashLog(e);
		}
		startTextEditor(craftingPath);
	}
	
	private JsonObject getExplotionCondition() {
		JsonObject condition = new JsonObject();
		condition.addProperty("condition", "minecraft:survives_explosion");
		return condition;
	}
	
	private JsonObject getSilkCondition() {
		JsonObject condition = new JsonObject();
		condition.addProperty("condition", "minecraft:match_tool");
		JsonObject predicate = new JsonObject();
		JsonArray enchantments = new JsonArray();
		JsonObject enchantment = new JsonObject();
		enchantment.addProperty("enchantment", "minecraft:silk_touch");
		JsonObject levels = new JsonObject();
		levels.addProperty("min", 1);
		
		enchantment.add("levels", levels);
		enchantments.add(enchantment);
		predicate.add("enchantments", enchantments);
		condition.add("predicate", predicate);
		
		return condition;
	}

}
