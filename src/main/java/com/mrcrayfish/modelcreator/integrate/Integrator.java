package com.mrcrayfish.modelcreator.integrate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JPanel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mrcrayfish.modelcreator.Settings;
import com.mrcrayfish.modelcreator.util.Util;

public abstract class Integrator
{
	protected String content;
	protected Gson builder;
	private Runnable updateCallback;
	
	public Integrator() {
		this.builder = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		this.content = generate();
	}
	
	public String getContent() {
		return content;
	}
	
	public abstract String generate();
	
	public abstract void integrate();
	
	public JPanel getAdditionalPanel() {
		return null;
	}
	
	public void setUpdateListener(Runnable listener) {
		this.updateCallback = listener;
	}
	
	public void doUpdate() {
		this.content = generate();
		this.updateCallback.run();
	}
	
	protected String addModid(String text) {
		if(text.contains(":")) {
			return text;
		}
		return IntegrateDialog.modid + ":" + text;
	}
	
	protected String getItemForBlock() {
		if(IntegrateDialog.BlockItem == null) {
			return addModid(IntegrateDialog.assetName);
		}else {
			return addModid(IntegrateDialog.BlockItem);
		}
	}
	
	//Returns the path to the asset/<modid>/ folder
	protected Path getAssetFolder() {
		return Paths.get(IntegrateDialog.resourcePath, "assets", IntegrateDialog.modid);
	}
	
	//Returns the path to the data/<modid>/ folder
	protected Path getDataFolder() {
		return Paths.get(IntegrateDialog.resourcePath, "data", IntegrateDialog.modid);
	}
	
	protected void writeToFile(Path path, String data) throws IOException {
		File file = path.toFile();
		file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(file);
		writer.write(data);
		writer.close();
	}
	
	protected void startTextEditor(Path path) {
		try {
            String command = Settings.getTextEditorPath() + " " + path.toString();
            Runtime.getRuntime().exec(command);
        }
        catch(IOException e) {
            Util.writeCrashLog(e);
        }
	}

}
