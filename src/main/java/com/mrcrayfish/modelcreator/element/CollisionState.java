package com.mrcrayfish.modelcreator.element;

import java.util.List;
import java.util.stream.Collectors;

import com.mrcrayfish.modelcreator.panels.CollisionPanel;

public class CollisionState implements ElementManagerState
{
	private final List<Element> elements;
    private final int selectedIndex;
    
    private final CollisionPanel manager;
    
    public CollisionState(CollisionPanel manager) {
    	this.manager = manager;
    	this.elements = manager.getAllElements().stream().map(Element::new).collect(Collectors.toList());
    	this.selectedIndex = manager.getAllElements().indexOf(manager.getSelectedElement());
    }

	@Override
	public List<Element> getElements() {
		return elements;
	}
	
	public int getSelectedIndex()
	{
		return selectedIndex;
	}

	@Override
	public void restore()
	{
		manager.restoreState(this);
	}
	

}
