package com.mrcrayfish.modelcreator.element;

import com.mrcrayfish.modelcreator.texture.TextureEntry;

import java.util.List;

import javax.swing.JList;

public interface ElementManager
{
	public JList<ElementCellEntry> getList();
	
    Element getSelectedElement();
    
    public ElementCellEntry getSelectedElementEntry();

    void setSelectedElement(int pos);

    List<Element> getAllElements();

    Element getElement(int index);

    int getElementCount();

    void clearElements();

    void updateName();

    void updateValues();

    boolean getAmbientOcc();

    void setAmbientOcc(boolean occ);

    void addElement(Element e);

    void setParticle(TextureEntry entry);

    TextureEntry getParticle();

    void reset();

    default ElementManagerState createState()
    {
        return new ElementManagerState(this);
    }

    void restoreState(ElementManagerState state);
    
    public void newElement();
    
    public void deleteElement();
}
