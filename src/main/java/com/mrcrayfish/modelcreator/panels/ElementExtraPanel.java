package com.mrcrayfish.modelcreator.panels;

import com.mrcrayfish.modelcreator.ModelCreator;
import com.mrcrayfish.modelcreator.StateManager;
import com.mrcrayfish.modelcreator.element.Element;
import com.mrcrayfish.modelcreator.element.ElementManager;
import com.mrcrayfish.modelcreator.util.ComponentUtil;

import javax.swing.*;
import java.awt.*;

public class ElementExtraPanel extends JPanel implements IElementUpdater
{
	private static final long serialVersionUID = 2404530469699783994L;

	private ElementManager manager;

    private JRadioButton btnShade;

    public ElementExtraPanel(ElementManager manager)
    {
        this.manager = manager;
        this.setBackground(ModelCreator.BACKGROUND);
        this.setLayout(new GridLayout(1, 2));
        this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(ModelCreator.BACKGROUND, 5), "<html><b>Extras</b></html>"));
        this.setMaximumSize(new Dimension(186, 50));
        this.initComponents();
        this.addComponents();
    }

    private void initComponents()
    {
        btnShade = ComponentUtil.createRadioButton("Shade", "<html>Determines if shadows should be rendered<br>Default: On</html>");
        btnShade.setBackground(ModelCreator.BACKGROUND);
        btnShade.addActionListener(e ->
        {
            Element selectedElement = manager.getSelectedElement();
            if(selectedElement != null)
            {
                selectedElement.setShade(btnShade.isSelected());
                StateManager.pushState(manager);
            }
        });
    }

    private void addComponents()
    {
        add(btnShade);
    }

    @Override
    public void updateValues(Element cube)
    {
        if(cube != null)
        {
            btnShade.setEnabled(true);
            btnShade.setSelected(cube.isShaded());
        }
        else
        {
            btnShade.setEnabled(false);
            btnShade.setSelected(false);
        }
    }
}
