package com.mrcrayfish.modelcreator.panels;

import com.mrcrayfish.modelcreator.element.Element;
import com.mrcrayfish.modelcreator.element.ElementManager;

import javax.swing.*;
import java.awt.*;

public class CuboidTabbedPane extends JTabbedPane implements IElementUpdater
{
	private static final long serialVersionUID = -2177899971092802618L;

	public void updateValues(Element elem)
    {
        for(int i = 0; i < getTabCount(); i++)
        {
            Component component = getComponentAt(i);
            if(component != null)
            {
                if(component instanceof IElementUpdater)
                {
                    IElementUpdater updater = (IElementUpdater) component;
                    updater.updateValues(elem);
                }
            }
        }
    }
}
