package com.mrcrayfish.modelcreator.panels.tabs;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JSeparator;

import com.mrcrayfish.modelcreator.element.Element;
import com.mrcrayfish.modelcreator.panels.ElementExtraPanel;
import com.mrcrayfish.modelcreator.panels.GlobalPanel;
import com.mrcrayfish.modelcreator.panels.SidebarPanel;

public class ElementPanelExtra extends ElementPanel
{
	private static final long serialVersionUID = -4223141824697440064L;
	
	private ElementExtraPanel panelExtras;
    private GlobalPanel panelGlobal;
    
	public ElementPanelExtra(SidebarPanel manager)
	{
		super(manager);
	}
	
	@Override
	protected void initComponents() {
		super.initComponents();
        panelGlobal = new GlobalPanel((SidebarPanel)manager);
    	panelExtras = new ElementExtraPanel(manager);
	}
	
	@Override
	protected void addComponents() {
		super.addComponents();
		add(Box.createRigidArea(new Dimension(188, 5)));
		add(panelExtras);
		add(Box.createRigidArea(new Dimension(188, 70)));
		add(new JSeparator(JSeparator.HORIZONTAL));
		add(panelGlobal);
	}
	
	@Override
	public void updateValues(Element cube) {
		super.updateValues(cube);
		panelExtras.updateValues(cube);
		panelGlobal.updateValues(cube);
	}

}
