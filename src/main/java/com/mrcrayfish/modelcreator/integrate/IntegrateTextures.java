package com.mrcrayfish.modelcreator.integrate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import com.mrcrayfish.modelcreator.element.Element;
import com.mrcrayfish.modelcreator.element.Face;
import com.mrcrayfish.modelcreator.panels.SidebarPanel;
import com.mrcrayfish.modelcreator.texture.TextureEntry;
import com.mrcrayfish.modelcreator.util.Util;

public class IntegrateTextures extends Integrator
{
	private List<TextureEntry> textureEntries; //list of all non vanilla textures
	private int current = -1;

	public IntegrateTextures(SidebarPanel manager) {
		Set<TextureEntry> textureEntriesSet = new HashSet<>();
		
		for(Element element : manager.getAllElements()) {
			
            for(Face face : element.getAllFaces()) {
            	
                if(face.getTexture() != null && !face.getTexture().getModId().equals("minecraft")) {
                	textureEntriesSet.add(face.getTexture());
                }
            }
        }
		
		textureEntries = new ArrayList<>(textureEntriesSet);
		if(!textureEntries.isEmpty()) {
			current = 0;
		}
	}
	
	@Override
	public String generate() {
		if(current == -1)
			return "No textures to integrate!";
		
		TextureEntry currentTexture = textureEntries.get(current);
		Path textureDir = Paths.get("assets", IntegrateDialog.modid).resolve("textures").resolve(currentTexture.getDirectory()).resolve(currentTexture.getName() + ".png");
		StringBuilder textures = new StringBuilder();
		String currentStr = "--> " + textureDir.toString();
		textures.append(currentStr);
		textures.append("\n\n");
		
		for(int i = current+1; i < textureEntries.size(); i++) {
			TextureEntry te = textureEntries.get(i);
			Path p = Paths.get("assets", IntegrateDialog.modid).resolve("textures").resolve(te.getDirectory()).resolve(te.getName() + ".png");
			textures.append(p.toString());
			textures.append('\n');
		}
		
		return textures.toString();
	}

	@Override
	public void integrate() {
		TextureEntry currentTexture = textureEntries.get(current);
		Path path = getAssetFolder().resolve("textures").resolve(currentTexture.getDirectory()).resolve(currentTexture.getName() + ".png");
		
		File file = path.toFile();
		file.getParentFile().mkdirs();
		try(FileOutputStream fos = new FileOutputStream(file)) {
			ImageIO.write(currentTexture.getSource(), "PNG", fos);
		}catch (IOException e) {
			e.printStackTrace();
			Util.writeCrashLog(e);
		}
		if(current+1 >= textureEntries.size()) {
			current = -1;
		}else{
			current++;	
		}
		this.doUpdate();
	}

}
