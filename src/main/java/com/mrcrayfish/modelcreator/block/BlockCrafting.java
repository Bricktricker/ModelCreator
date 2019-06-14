package com.mrcrayfish.modelcreator.block;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;

import com.mrcrayfish.modelcreator.ModelCreator;
import com.mrcrayfish.modelcreator.component.AutoComboBox;
import com.mrcrayfish.modelcreator.util.ComponentUtil;

public class BlockCrafting {
	
	private boolean shapeLess;
	private int numOutputItems;
	private List<String> craftItems;
	
	public BlockCrafting() {
		this.craftItems = new ArrayList<>();
	}
	
	public boolean isShapeLess() {
		return shapeLess;
	}

	public void setShapeLess(boolean shapeLess) {
		this.shapeLess = shapeLess;
	}

	public int getNumOutputItems() {
		return numOutputItems;
	}

	public void setNumOutputItems(int numOutputItems) {
		this.numOutputItems = numOutputItems;
	}

	public List<String> getCraftItems() {
		return craftItems;
	}

	public void setCraftItems(List<String> craftItems) {
		this.craftItems = craftItems;
	}

	public static void show(ModelCreator creator) {
		JDialog dialog = new JDialog(creator, "Crafting", Dialog.ModalityType.APPLICATION_MODAL);
		
		JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(500, 250));
        dialog.add(panel);
        
        SpringLayout generalSpringLayout = new SpringLayout();
        JPanel generalPanel = new JPanel(generalSpringLayout);
        panel.add(generalPanel);
        
        final JCheckBox checkBoxShapeless = ComponentUtil.createCheckBox("Shapeless recipe", "", false);
        final SpinnerNumberModel numoutSpinnerNumberModel = new SpinnerNumberModel();
        final List<JComboBox<String>> craftingSlots = new ArrayList<>();
        
        //shapeless + num output items
        JPanel outputPanel = new JPanel(new GridLayout(1, 2));
        {
        	generalPanel.add(outputPanel);
        	
        	outputPanel.add(checkBoxShapeless);
            
            JPanel numoutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            outputPanel.add(numoutPanel);
            
            JLabel numoutLabel = new JLabel("Number of output items");
            numoutPanel.add(numoutLabel);
            
            numoutSpinnerNumberModel.setMinimum(1);
            numoutSpinnerNumberModel.setMaximum(64);
            JSpinner numoutLimitSpinner = new JSpinner(numoutSpinnerNumberModel);
            numoutLimitSpinner.setPreferredSize(new Dimension(40, 24));
            numoutLimitSpinner.setValue(1);
            numoutPanel.add(numoutLimitSpinner);
        }
        
        JSeparator separator = new JSeparator();
        generalPanel.add(separator);
        
        JPanel craftingPanel = new JPanel(new GridLayout(3, 3));
        {
        	generalPanel.add(craftingPanel);
        	
        	for(int i = 0; i < 9; i++) {
        		final JComboBox<String> comboBoxSlot = new JComboBox<>();
        		comboBoxSlot.setPreferredSize(new Dimension(150, 24));
        		comboBoxSlot.addItem("");
        		craftingPanel.add(comboBoxSlot);
        		craftingSlots.add(comboBoxSlot);
        	}
        }
        
        JSeparator separator2 = new JSeparator();
        generalPanel.add(separator2);
        
        //List of all Items in minecraft
        JPanel itemPanel = new JPanel(new GridLayout(1, 2));
        {
        	generalPanel.add(itemPanel);
        	
        	List<String> itemList = new ArrayList<>();
        	itemList.add("item");
        	itemList.add("block");
        	itemList.add("cobble");
        	itemList.add("dirt");
        	itemList.add("grass");
        	itemList.add("web");
        	
        	AutoComboBox itemsBox = new AutoComboBox(itemList);
        	itemPanel.add(itemsBox);
        	
        	JButton addButton = new JButton("ADD");
        	addButton.setPreferredSize(new Dimension(80, 24));
        	addButton.addActionListener(a -> {
        		craftingSlots.forEach(s -> {
        			s.addItem((String)itemsBox.getSelectedItem());
        		});
        		JTextField text = (JTextField) itemsBox.getEditor().getEditorComponent();
        		text.setText("");
        	});
        	itemPanel.add(addButton);
        }
        
        JSeparator separator3 = new JSeparator();
        generalPanel.add(separator3);
        
        //Save button
    	JButton saveButton = new JButton("Save");
        saveButton.setPreferredSize(new Dimension(80, 24));
        saveButton.addActionListener(e ->
        {
        	boolean isShapeless = checkBoxShapeless.isSelected();
        	int numOutputItems = (Integer)numoutSpinnerNumberModel.getValue();
        	List<String> recipe = new ArrayList<>();
        	craftingSlots.forEach(s -> {
        		String item = (String)s.getSelectedItem();
        		recipe.add(item);
        	});
        	
        	BlockManager.crafting.setShapeLess(isShapeless);
        	BlockManager.crafting.setNumOutputItems(numOutputItems);
        	BlockManager.crafting.setCraftItems(recipe);
        });
        generalPanel.add(saveButton);
        
        generalSpringLayout.putConstraint(SpringLayout.WEST, outputPanel, 5, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, outputPanel, 5, SpringLayout.NORTH, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, outputPanel, 5, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.WEST, separator, 0, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, separator, 0, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, separator, 5, SpringLayout.SOUTH, outputPanel);
        generalSpringLayout.putConstraint(SpringLayout.WEST, craftingPanel, 0, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, craftingPanel, 0, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, craftingPanel, 5, SpringLayout.SOUTH, separator);
        generalSpringLayout.putConstraint(SpringLayout.WEST, separator2, 0, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, separator2, 0, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, separator2, 5, SpringLayout.SOUTH, craftingPanel);
        generalSpringLayout.putConstraint(SpringLayout.WEST, itemPanel, 0, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, itemPanel, 0, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, itemPanel, 5, SpringLayout.SOUTH, separator2);
        generalSpringLayout.putConstraint(SpringLayout.WEST, separator3, 0, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, separator3, 0, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, separator3, 5, SpringLayout.SOUTH, itemPanel);
        generalSpringLayout.putConstraint(SpringLayout.WEST, saveButton, 0, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, saveButton, 0, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, saveButton, 5, SpringLayout.SOUTH, separator3);
        
        
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.requestFocus();
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
	}
}
