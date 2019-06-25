package com.mrcrayfish.modelcreator.integrate;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;

import com.mrcrayfish.modelcreator.ExporterModel;
import com.mrcrayfish.modelcreator.panels.SidebarPanel;
import com.mrcrayfish.modelcreator.util.Util;

public class IntegrateModel extends Integrator
{
	private SidebarPanel modelSidebar;
	
	public IntegrateModel(SidebarPanel modelSidebar) {
		this.modelSidebar = modelSidebar;
	}

	@Override
	public String generate() {
		ExporterModel exporter = new ExporterModel(modelSidebar, IntegrateDialog.modid);
        exporter.setOptimize(true);
        exporter.setDisplayProps(false);
        exporter.setIncludeNames(true);
		
        StringWriter stringWriter = new StringWriter();
        try(BufferedWriter writer = new BufferedWriter(stringWriter))
        {
        	exporter.write(writer);
        	writer.flush();
        	return stringWriter.toString();
        } catch (IOException e) {
        	//there should not happen an IO exception when writing to a StringWriter
		}
        
		return "";
	}

	@Override
	public void integrate() {
		Path modelPath = getAssetFolder().resolve("models").resolve("block").resolve(IntegrateDialog.assetName + ".json");
		try{
			writeToFile(modelPath, content + "\n");
		} catch (IOException e) {
			Util.writeCrashLog(e);
		}
		startTextEditor(modelPath);
	}

}
