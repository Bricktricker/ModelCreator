package com.mrcrayfish.modelcreator.element;

import com.mrcrayfish.modelcreator.texture.TextureEntry;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
//TODO: make an interface
public class ElementManagerState
{
    private final List<Element> elements;
    private final int selectedIndex;
    private boolean ambientOcclusion;
    private TextureEntry particleTexture;

    public ElementManagerState(ElementManager manager)
    {
        this.elements = manager.getAllElements().stream().map(Element::new).collect(Collectors.toList());
        this.selectedIndex = manager.getAllElements().indexOf(manager.getSelectedElement());
        //this.ambientOcclusion = manager.getAmbientOcc();
        //this.particleTexture =  manager.getParticle();
    }

    public List<Element> getElements()
    {
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
}
