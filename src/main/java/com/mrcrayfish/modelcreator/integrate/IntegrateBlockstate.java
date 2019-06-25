package com.mrcrayfish.modelcreator.integrate;

import java.awt.FlowLayout;
import java.io.IOException;
import java.nio.file.Path;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import com.google.gson.JsonObject;
import com.mrcrayfish.modelcreator.util.ComponentUtil;
import com.mrcrayfish.modelcreator.util.Util;

public class IntegrateBlockstate extends Integrator
{
	private boolean hasRotation = false;
	
	@Override
	public String generate() {
		JsonObject blockstate = new JsonObject();
		JsonObject variants = new JsonObject();
		
		if(hasRotation) {
			//South
			JsonObject varSouth = new JsonObject();
			varSouth.addProperty("model", addModid("block/"+IntegrateDialog.assetName));
			variants.add("facing=south", varSouth);
			
			//West
			JsonObject varWest = new JsonObject();
			varWest.addProperty("model", addModid("block/"+IntegrateDialog.assetName));
			varWest.addProperty("y", 90);
			variants.add("facing=west", varWest);
			
			//North
			JsonObject varNorth = new JsonObject();
			varNorth.addProperty("model", addModid("block/"+IntegrateDialog.assetName));
			varNorth.addProperty("y", 180);
			variants.add("facing=north", varNorth);
			
			//East
			JsonObject varEast = new JsonObject();
			varEast.addProperty("model", addModid("block/"+IntegrateDialog.assetName));
			varEast.addProperty("y", 270);
			variants.add("facing=east", varEast);
		}else {
			JsonObject var = new JsonObject();
			var.addProperty("model", addModid("block/"+IntegrateDialog.assetName));
			variants.add("", var);
		}
		blockstate.add("variants", variants);
		
		return builder.toJson(blockstate);
	}

	@Override
	public void integrate() {
		Path blockstatePath = getAssetFolder().resolve("blockstates").resolve(IntegrateDialog.assetName + ".json");
		try{
			writeToFile(blockstatePath, content + "\n");
		} catch (IOException e) {
			Util.writeCrashLog(e);
		}
		startTextEditor(blockstatePath);
	}
	
	@Override
	public JPanel getAdditionalPanel() {
		JPanel blockstatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		JCheckBox rotationCheckbox = ComponentUtil.createCheckBox("Is rotatable", "Should the block be rotatable?", hasRotation);
		rotationCheckbox.addActionListener(a -> {
			this.hasRotation = rotationCheckbox.isSelected();
			this.doUpdate();
		});
		blockstatePanel.add(rotationCheckbox);
		
		return blockstatePanel;
	}

}
