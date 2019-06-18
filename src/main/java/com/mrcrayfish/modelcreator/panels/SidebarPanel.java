package com.mrcrayfish.modelcreator.panels;


import com.mrcrayfish.modelcreator.ModelCreator;
import com.mrcrayfish.modelcreator.SidebarManager.SidebarTypes;
import com.mrcrayfish.modelcreator.element.*;
import com.mrcrayfish.modelcreator.panels.tabs.ElementPanelExtra;
import com.mrcrayfish.modelcreator.panels.tabs.FacePanel;
import com.mrcrayfish.modelcreator.panels.tabs.RotationPanel;
import com.mrcrayfish.modelcreator.texture.TextureEntry;

import javax.swing.*;
import java.awt.*;

public class SidebarPanel extends ElementManager
{
	private static final long serialVersionUID = -6064334563594181490L;
	
	private TextureEntry particle = null;
    private boolean ambientOcc = true;
    
    public SidebarPanel(ModelCreator creator) {
    	super(creator);
    }

    @Override
    protected void addElementPanel() {
    	CuboidTabbedPane tabbedPane = new CuboidTabbedPane();
    	tabbedPane.setBackground(new Color(127, 132, 145));
        tabbedPane.setForeground(Color.WHITE);
        tabbedPane.add("Element", new ElementPanelExtra(this));
        tabbedPane.add("Rotation", new RotationPanel(this));
        tabbedPane.add("Faces", new FacePanel(this));
        tabbedPane.setPreferredSize(new Dimension(190, 500));
        tabbedPane.setTabPlacement(JTabbedPane.TOP);
        tabbedPane.addChangeListener(c ->
        {
            if(tabbedPane.getSelectedIndex() == 2)
            {
                creator.setSidebar(ModelCreator.uvSidebar);
                ModelCreator.isUVSidebarOpen = true;
            }
            else
            {
                creator.setSidebar(null);
                ModelCreator.isUVSidebarOpen = false;
            }
        });
        add(tabbedPane);
        this.elementPanel = tabbedPane;
    }
    
    public static void initIncrementButton(JButton button, Font defaultFont, String subject, boolean increase)
    {
        button.setPreferredSize(new Dimension(62, 30));
        button.setFont(defaultFont);
        button.setToolTipText(String.format("<html>%screases the %s.<br><b>Hold shift for decimals</b></html>", increase ? "In" : "De", subject));
    }

    public static void initIncrementableField(JTextField field, Font defaultFont)
    {
        field.setSize(new Dimension(62, 30));
        field.setFont(defaultFont);
        field.setHorizontalAlignment(JTextField.CENTER);
    }

    public boolean getAmbientOcc()
    {
        return ambientOcc;
    }

    public void setAmbientOcc(boolean occ)
    {
        ambientOcc = occ;
    }

    public void setParticle(TextureEntry particle)
    {
        this.particle = particle;
    }

    public TextureEntry getParticle()
    {
        return particle;
    }

	@Override
	public ElementManagerState createState()
	{
		return new SidebarState(this);
	}
	
	public void restoreState(SidebarState state) {
		this.reset();
        for(Element element : state.getElements())
        {
            this.model.addElement(new ElementCellEntry(new Element(element)));
        }
        this.setSelectedElement(state.getSelectedIndex());
        this.setAmbientOcc(state.isAmbientOcclusion());
        this.setParticle(state.getParticleTexture());
        this.updateValues();
        creator.getSidebarManager().setActivePanel(SidebarTypes.COLLISION);
	}

}
