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

	private ElementManager manager;

    private SizePanel panelSize;
    private PositionPanel panelPosition;
    private ElementExtraPanel panelExtras;
    private GlobalPanel panelGlobal;

    public ElementPanel(ElementManager manager)
    {
        this.manager = manager;
        setBackground(ModelCreator.BACKGROUND);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        initComponents();
        addComponents();
    }

    private void initComponents()
    {
        panelSize = new SizePanel(manager);
        panelPosition = new PositionPanel(manager);
        panelExtras = new ElementExtraPanel(manager);
        panelGlobal = new GlobalPanel(manager);
    }

    private void addComponents()
    {
        add(Box.createRigidArea(new Dimension(188, 5)));
        add(panelSize);
        add(Box.createRigidArea(new Dimension(188, 5)));
        add(panelPosition);
        add(Box.createRigidArea(new Dimension(188, 5)));
        add(panelExtras);
        add(Box.createRigidArea(new Dimension(188, 70)));
        add(new JSeparator(JSeparator.HORIZONTAL));
        add(panelGlobal);
    }

    @Override
    public void updateValues(Element cube)
    {
        panelSize.updateValues(cube);
        panelPosition.updateValues(cube);
        panelExtras.updateValues(cube);
        panelGlobal.updateValues(cube);
    }
}
