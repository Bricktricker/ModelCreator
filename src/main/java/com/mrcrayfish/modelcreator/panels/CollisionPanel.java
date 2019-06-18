package com.mrcrayfish.modelcreator.panels;

import java.awt.Dimension;

import com.mrcrayfish.modelcreator.ModelCreator;
import com.mrcrayfish.modelcreator.element.CollisionState;
import com.mrcrayfish.modelcreator.element.Element;
import com.mrcrayfish.modelcreator.element.ElementCellEntry;
import com.mrcrayfish.modelcreator.element.ElementManager;
import com.mrcrayfish.modelcreator.element.ElementManagerState;
import com.mrcrayfish.modelcreator.panels.tabs.ElementPanel;

public class CollisionPanel extends ElementManager
{
	private static final long serialVersionUID = 813967678684407621L;
    
	public CollisionPanel(ModelCreator creator) {
		super(creator);
	}
	
    @Override
    protected void addElementPanel() {
    	ElementPanel elemTmp = new ElementPanel(this);
    	elemTmp.setPreferredSize(new Dimension(190, 500));
        add(elemTmp);
        this.elementPanel = elemTmp;
    }

	@Override
	public ElementManagerState createState()
	{
		return new CollisionState(this);
	}
	
	public void restoreState(CollisionState state) {
		this.reset();
        for(Element element : state.getElements())
        {
            this.model.addElement(new ElementCellEntry(new Element(element)));
        }
        this.setSelectedElement(state.getSelectedIndex());
        this.updateValues();
	}

}
