package com.mrcrayfish.modelcreator.element;

import java.util.List;
import java.util.stream.Collectors;

import com.mrcrayfish.modelcreator.panels.SidebarPanel;
import com.mrcrayfish.modelcreator.texture.TextureEntry;

public class SidebarState implements ElementManagerState
{
	private final List<Element> elements;
    private final int selectedIndex;
    private final boolean ambientOcclusion;
    private final TextureEntry particleTexture;
    
    private final SidebarPanel manager;
    
    public SidebarState(SidebarPanel manager) {
    	this.manager = manager;
    	this.elements = manager.getAllElements().stream().map(Element::new).collect(Collectors.toList());
    	this.selectedIndex = manager.getAllElements().indexOf(manager.getSelectedElement());
        this.ambientOcclusion = manager.getAmbientOcc();
        this.particleTexture =  manager.getParticle();
    }
    
    @Override
    public List<Element> getElements() {
        return elements;
    }

	public int getSelectedIndex()
	{
		return selectedIndex;
	}

	public boolean isAmbientOcclusion()
	{
		return ambientOcclusion;
	}

	public TextureEntry getParticleTexture()
	{
		return particleTexture;
	}

	@Override
	public void restore()
	{
		this.manager.restoreState(this);
	}
    
}
