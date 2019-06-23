package com.mrcrayfish.modelcreator.block;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;

import com.mrcrayfish.modelcreator.ModelCreator;
import com.mrcrayfish.modelcreator.component.AutoComboBox;
import com.mrcrayfish.modelcreator.util.ComponentUtil;

public class BlockLoot
{
	private boolean silkTouch;
	private int numDrops;
	private String dropItem; // if null, should drop itself
	
	public BlockLoot() {
		this.silkTouch = false;
		this.numDrops = 1;
		this.dropItem = null;
	}

	public boolean isSilkTouch()
	{
		return silkTouch;
	}

	public void setSilkTouch(boolean silkTouch)
	{
		this.silkTouch = silkTouch;
	}

	public int getNumDrops()
	{
		return numDrops;
	}

	public void setNumDrops(int numDrops)
	{
		this.numDrops = numDrops;
	}

	public String getDropItem()
	{
		return dropItem;
	}

	public void setDropItem(String dropItem)
	{
		this.dropItem = dropItem;
	}
	
	public static void show(ModelCreator creator) {
		JDialog dialog = new JDialog(creator, "Drops", Dialog.ModalityType.APPLICATION_MODAL);
		
		JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(500, 140));
        dialog.add(panel);
        
        SpringLayout generalSpringLayout = new SpringLayout();
        JPanel generalPanel = new JPanel(generalSpringLayout);
        panel.add(generalPanel);
        
        final JCheckBox silkCheckBox = ComponentUtil.createCheckBox("Needs silk touch", "", false);
        final SpinnerNumberModel numDropsSpinnerNumberModel = new SpinnerNumberModel();
        final AutoComboBox itemsBox = new AutoComboBox(Resources.items);
        
        //Basic settings
        JPanel basicPanel = new JPanel(new GridLayout(1, 2));
        {
        	generalPanel.add(basicPanel);
        	
        	//Silk touch        	
        	basicPanel.add(silkCheckBox);
        	
        	//Num drops
        	JPanel numDropsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        	basicPanel.add(numDropsPanel);
        	
        	JLabel numDropsLabel = new JLabel("Number of dropped Items");
        	numDropsPanel.add(numDropsLabel);
        	
        	numDropsSpinnerNumberModel.setMinimum(0);
        	numDropsSpinnerNumberModel.setMaximum(64);
        	JSpinner numDropsSpinner = new JSpinner(numDropsSpinnerNumberModel);
        	numDropsSpinner.setPreferredSize(new Dimension(40, 24));
        	numDropsSpinner.setValue(1);
            numDropsPanel.add(numDropsSpinner);
        }
        
        JSeparator separator = new JSeparator();
        generalPanel.add(separator);
        
        //Drops settings
        JPanel dropsPanel = new JPanel(new GridLayout(1, 2));
        {
        	generalPanel.add(dropsPanel);
        	
        	//Should drop itself
        	JCheckBox dropItselfCheckBox = ComponentUtil.createCheckBox("Drops itself", "", true);
        	dropsPanel.add(dropItselfCheckBox);
        	
        	JPanel otherDropPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        	dropsPanel.add(otherDropPanel);
        	
        	JLabel otherLabel = new JLabel("Other");
        	otherDropPanel.add(otherLabel);
        	
        	itemsBox.setEnabled(false);
        	itemsBox.setPreferredSize(new Dimension(200, 24));
        	otherDropPanel.add(itemsBox);
        	
        	dropItselfCheckBox.addActionListener(a -> {
        		boolean checked = ((JCheckBox)a.getSource()).isSelected();
        		itemsBox.setEnabled(!checked);
        	});
        }
        
        JSeparator separator2 = new JSeparator();
        generalPanel.add(separator2);
        
        //Save Button
        JButton saveButton = new JButton("Save");
        saveButton.setPreferredSize(new Dimension(80, 24));
        saveButton.addActionListener(e -> {
        	boolean silkTouch = silkCheckBox.isSelected();
        	int numDrops = (Integer)numDropsSpinnerNumberModel.getValue();
        	boolean dropItself = !itemsBox.isEnabled();
        	
        	BlockManager.loot.setSilkTouch(silkTouch);
        	BlockManager.loot.setNumDrops(numDrops);
        	
        	if(dropItself) {
        		BlockManager.loot.setDropItem(null);
        	}else {
        		String item = (String)itemsBox.getSelectedItem();
        		BlockManager.loot.setDropItem(item);
        	}
        });
        generalPanel.add(saveButton);
        
        generalSpringLayout.putConstraint(SpringLayout.WEST, basicPanel, 5, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, basicPanel, 5, SpringLayout.NORTH, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, basicPanel, 5, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.WEST, separator, 0, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, separator, 0, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, separator, 5, SpringLayout.SOUTH, basicPanel);
        generalSpringLayout.putConstraint(SpringLayout.WEST, dropsPanel, 0, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, dropsPanel, 0, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, dropsPanel, 5, SpringLayout.SOUTH, separator);
        generalSpringLayout.putConstraint(SpringLayout.WEST, separator2, 0, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, separator2, 0, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, separator2, 5, SpringLayout.SOUTH, dropsPanel);
        generalSpringLayout.putConstraint(SpringLayout.WEST, saveButton, 0, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, saveButton, 0, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, saveButton, 5, SpringLayout.SOUTH, separator2);
        
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.requestFocus();
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
	}
}
