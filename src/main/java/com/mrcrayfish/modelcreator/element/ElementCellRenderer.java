package com.mrcrayfish.modelcreator.element;

import javax.swing.*;
import java.awt.*;

/**
 * Author: MrCrayfish
 */
public class ElementCellRenderer extends DefaultListCellRenderer
{
	private static final long serialVersionUID = -662253886566066896L;

	@Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
        ElementCellEntry entry = (ElementCellEntry) value;
        JPanel panel = entry.getPanel();
        panel.setBackground(isSelected ? new Color(186, 193, 211) : new Color(234, 234, 242));
        entry.getName().setText(entry.getElement().getName());
        return panel;
    }
}
