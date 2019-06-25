package com.mrcrayfish.modelcreator.integrate;

import java.util.HashSet;
import java.util.Set;

import com.mrcrayfish.modelcreator.element.Element;
import com.mrcrayfish.modelcreator.element.Face;
import com.mrcrayfish.modelcreator.panels.SidebarPanel;
import com.mrcrayfish.modelcreator.texture.TextureEntry;

public class IntegrateTextures extends Integrator
{
	private Set<TextureEntry> textureEntries; //list of all non vanilla textures

	public IntegrateTextures(SidebarPanel manager) {
		textureEntries = new HashSet<>();
		
		for(Element element : manager.getAllElements()) {
			
            for(Face face : element.getAllFaces()) {
            	
                if(face.getTexture() != null && !face.getTexture().getModId().equals("minecraft")) {
                    textureEntries.add(face.getTexture());
                }
            }
        }
	}
	
	@Override
	public String generate() {
		return "";
	}

	@Override
	public void integrate() {

	}

}
