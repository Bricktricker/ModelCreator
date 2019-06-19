package com.mrcrayfish.modelcreator.element;

import java.util.ArrayList;
import java.util.List;

import com.mrcrayfish.modelcreator.SidebarManager;
import com.mrcrayfish.modelcreator.panels.CollisionPanel;
import com.mrcrayfish.modelcreator.panels.SidebarPanel;

public class CombinedState implements ElementManagerState {

	private ElementManagerState modelState;
	private ElementManagerState collisionState;
	
	public CombinedState(SidebarPanel modelPanel, CollisionPanel collisionPanel) {
		this.modelState = modelPanel.createState();
		this.collisionState = collisionPanel.createState();
	}
	
	public CombinedState(SidebarManager manager) {
		this(manager.getModelPanel(), manager.getCollisionPanel());
	}
	
	@Override
	public List<Element> getElements() {
		List<Element> elems = new ArrayList<>(modelState.getElements());
		elems.addAll(collisionState.getElements());
		return elems;
	}

	@Override
	public void restore() {
		modelState.restore();
		collisionState.restore();
	}

}
