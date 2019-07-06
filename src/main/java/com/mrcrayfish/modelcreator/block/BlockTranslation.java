package com.mrcrayfish.modelcreator.block;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;

import com.mrcrayfish.modelcreator.ModelCreator;
import com.mrcrayfish.modelcreator.block.Resources.LangPair;

public class BlockTranslation
{
	private Map<String, Translation> translations;
	
	public BlockTranslation() {
		translations = new HashMap<>();
	}
	
	public void addTranslation(String language, String name, String tooltip) {
		assert(language != null && !language.isEmpty());
		if(tooltip != null && tooltip.isEmpty()) {
			addTranslation(language, name, null);
			return;
		}
		Translation trans = new Translation(name, tooltip);
		this.translations.put(language, trans);
	}
	
	public void addTranslation(String language, String name) {
		addTranslation(language, name, null);
	}
	
	public Translation getTranslation(String language) {
		return this.translations.get(language);
	}
	
	public Map<String, Translation> getAllTranslations() {
		return translations;
	}
	
	public static class Translation {
		public String name;
		public String tooltip;
		
		public Translation(String name, String tooltip) {
			this.name = name;
			this.tooltip = tooltip;
		}
	}
	
	public static void show(ModelCreator creator) {
		JDialog dialog = new JDialog(creator, "Translation", Dialog.ModalityType.APPLICATION_MODAL);
		
		JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(500, 175));
        dialog.add(panel);
        
        SpringLayout generalSpringLayout = new SpringLayout();
        JPanel generalPanel = new JPanel(generalSpringLayout);
        panel.add(generalPanel);
        
        final JComboBox<LangPair> comboBoxLangs = new JComboBox<>();
        final JTextField nameText = new JTextField();
        final JTextField tooltipText = new JTextField();
        
        //Language Selection
        JPanel selectPanel = new JPanel(new GridLayout(0, 1));
        {
        	generalPanel.add(selectPanel);
        	
        	JPanel textPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); 
        	selectPanel.add(textPanel);
        	
        	JLabel languageLabel = new JLabel("Language");
        	textPanel.add(languageLabel);
        	
        	comboBoxLangs.setPreferredSize(new Dimension(150, 24));
        	comboBoxLangs.addItem(new LangPair("", ""));
        	Resources.languages.forEach(comboBoxLangs::addItem);
        	comboBoxLangs.addActionListener(a -> {
        		if(comboBoxLangs.getSelectedIndex() == 0) return;
        		LangPair lang = (LangPair)comboBoxLangs.getSelectedItem();
        		Translation trans = BlockManager.translation.getTranslation(lang.key);
        		if(trans == null) {
        			nameText.setText("");
            		tooltipText.setText("");
        		}else {
            		nameText.setText(trans.name);
            		tooltipText.setText(trans.tooltip);	
        		}
        	});
        	textPanel.add(comboBoxLangs);
        }
        
        JSeparator separator = new JSeparator();
        generalPanel.add(separator);
        
        //Set actual translation
        JPanel translatePanel = new JPanel(new GridLayout(0, 1));
        {
        	generalPanel.add(translatePanel);
        	
        	//Name
        	JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        	translatePanel.add(namePanel);
        	
        	JLabel nameLabel = new JLabel("Name");
        	namePanel.add(nameLabel);
        	
        	nameText.setPreferredSize(new Dimension(125, 24));
        	namePanel.add(nameText);
        	
        	//Tooltip
        	JPanel tooltipPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        	translatePanel.add(tooltipPanel);
        	
        	JLabel tooltipLabel = new JLabel("Tooltip (can be empty)");
        	tooltipPanel.add(tooltipLabel);
        	
        	tooltipText.setPreferredSize(new Dimension(250, 24));
        	tooltipPanel.add(tooltipText);
        	
        	//Save button
        	JButton saveButton = new JButton("Save");
            saveButton.setPreferredSize(new Dimension(80, 24));
            saveButton.addActionListener(e ->
            {
            	if(comboBoxLangs.getSelectedIndex() == 0) return;
            	LangPair lang = (LangPair)comboBoxLangs.getSelectedItem();
            	String name = nameText.getText();
            	String tooltip = tooltipText.getText();
            	BlockManager.translation.addTranslation(lang.key, name, tooltip);
            });
            
            translatePanel.add(saveButton);
        }
        
        generalSpringLayout.putConstraint(SpringLayout.WEST, selectPanel, 5, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, selectPanel, 5, SpringLayout.NORTH, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, selectPanel, 5, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.WEST, separator, 0, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, separator, 0, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, separator, 5, SpringLayout.SOUTH, selectPanel);
        generalSpringLayout.putConstraint(SpringLayout.WEST, translatePanel, 0, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, translatePanel, 0, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, translatePanel, 5, SpringLayout.SOUTH, separator);
        
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.requestFocus();
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
	}
}
