package com.mrcrayfish.modelcreator.panels.tabs;

import com.mrcrayfish.modelcreator.ModelCreator;
import com.mrcrayfish.modelcreator.element.Element;
import com.mrcrayfish.modelcreator.element.ElementManager;
import com.mrcrayfish.modelcreator.panels.*;

import javax.swing.*;
import java.awt.*;

/**
 *Panel for the "element" tab on the right
 *Here you can set the size and position of the cube
 */
public class ElementPanel extends JPanel implements IElementUpdater
{
	private static final long serialVersionUID = 2345409972608231073L;

	protected ElementManager manager;

	protected SizePanel panelSize;
	protected PositionPanel panelPosition;

    public ElementPanel(ElementManager manager)
    {
        this.manager = manager;
        setBackground(ModelCreator.BACKGROUND);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        initComponents();
        addComponents();
    }

    protected void initComponents()
    {
        panelSize = new SizePanel(manager);
        panelPosition = new PositionPanel(manager);
    }

    protected void addComponents()
    {
        add(Box.createRigidArea(new Dimension(188, 5)));
        add(panelSize);
        add(Box.createRigidArea(new Dimension(188, 5)));
        add(panelPosition);
    }

    @Override
    public void updateValues(Element cube)
    {
        panelSize.updateValues(cube);
        panelPosition.updateValues(cube);
    }
}
