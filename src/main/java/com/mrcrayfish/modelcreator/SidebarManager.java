package com.mrcrayfish.modelcreator;

import com.mrcrayfish.modelcreator.element.ElementManager;
import com.mrcrayfish.modelcreator.panels.CollisionPanel;
import com.mrcrayfish.modelcreator.panels.SidebarPanel;

public class SidebarManager
{
	private SidebarPanel modelPanel;
	private CollisionPanel collisionPanel;
	private ElementManager activePanel;
	
	public SidebarManager(SidebarPanel sidebar, CollisionPanel collision) {
		this.modelPanel = sidebar;
		this.collisionPanel = collision;
		this.activePanel = this.modelPanel;
	}
	
	public ElementManager getActivePanel() {
		return this.activePanel;
	}
	
	public SidebarPanel getModelPanel() {
		return this.modelPanel;
	}
	
	public CollisionPanel getCollisionPanel() {
		return this.collisionPanel;
	}
	
	public void setActivePanel(int panel) {
		if(panel == 0) {
			this.activePanel = modelPanel;
			collisionPanel.setSelectedElement(-1);
		}else if(panel == 1) {
			this.activePanel = collisionPanel;
			modelPanel.setSelectedElement(-1);
		}else {
			throw new IllegalArgumentException("new active panel out of range");
		}
	}
	
}
