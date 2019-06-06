package com.mrcrayfish.modelcreator.block;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeListener;

import com.mrcrayfish.modelcreator.ModelCreator;

public class BlockProperties
{
	private float hardness;
	private float resistance;
	private float lightLevel;
	//private Material material;
	//private SoundType sound;
	
	public BlockProperties() {
		hardness = 0.8F;
		resistance = 0.8F;
		lightLevel = 0;
	}

	public float getHardness()
	{
		return hardness;
	}

	public void setHardness(float hardness)
	{
		this.hardness = hardness;
	}

	public float getResistance()
	{
		return resistance;
	}

	public void setResistance(float resistance)
	{
		this.resistance = resistance;
	}

	public float getLightLevel()
	{
		return lightLevel;
	}

	public void setLightLevel(float lightLevel)
	{
		this.lightLevel = lightLevel;
	}
	
	public static void show(ModelCreator creator) {
		JDialog dialog = new JDialog(creator, "Properties", Dialog.ModalityType.APPLICATION_MODAL);
		
		JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(500, 300));
        dialog.add(panel);
        
        SpringLayout generalSpringLayout = new SpringLayout();
        JPanel generalPanel = new JPanel(generalSpringLayout);
        panel.add(generalPanel);
        
        //Settings to set the block asset- and java ID
        JPanel idsPanel = new JPanel(new GridLayout(1, 2));
        {
        	generalPanel.add(idsPanel);
        	
	        JPanel assetPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	        idsPanel.add(assetPanel);
	
	        JLabel labelUndoLimit = new JLabel("Asset-ID");
	        assetPanel.add(labelUndoLimit);
	
	        JTextField assetText = new JTextField();
	        assetText.setPreferredSize(new Dimension(100, 24));
	        assetText.getDocument().addDocumentListener(new TextFieldListener(assetID -> {
	        	//TODO: check correct format
	        	BlockManager.assetID = assetID;
	        }));
	        assetPanel.add(assetText);
	
	        JPanel javaPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	        idsPanel.add(javaPanel);
	        
	        JLabel labelJavaText = new JLabel("Java-ID");
	        javaPanel.add(labelJavaText);
	
	        JTextField javaText = new JTextField();
	        javaText.setPreferredSize(new Dimension(100, 24));
	        javaText.getDocument().addDocumentListener(new TextFieldListener(javaID -> {
	        	//TODO: check correct format
	        	BlockManager.javaID = javaID;
	        }));
	        javaPanel.add(javaText);
        }
        
        JSeparator separator = new JSeparator();
        generalPanel.add(separator);
        
        //Setting for BlockProperties
        JPanel blockPropertiesPanel = new JPanel(new GridLayout(0, 1));
        {
        	generalPanel.add(blockPropertiesPanel);
        	
        	//Hardness
        	JPanel hardnessPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        	blockPropertiesPanel.add(hardnessPanel);
        	JLabel hardnessLabel = new JLabel("Hardness");
        	hardnessPanel.add(hardnessLabel);
        	JSlider hardnessSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, (int)(BlockManager.properties.getHardness() * 100));
        	hardnessPanel.add(hardnessSlider);
        	JTextField hardnessText = new JTextField();
        	hardnessText.setPreferredSize(new Dimension(30, 24));
	        hardnessText.setEditable(false);
	        hardnessPanel.add(hardnessText);
	        ChangeListener clHardness = a -> {
	        	float value = hardnessSlider.getValue() / 100F;
	        	hardnessText.setText(String.valueOf(value));
	        	BlockManager.properties.setHardness(value);
	        };
	        hardnessSlider.addChangeListener(clHardness);
	        clHardness.stateChanged(null);
        	
        	//Resistance
	        JPanel resistancePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        	blockPropertiesPanel.add(resistancePanel);
        	JLabel resistanceLabel = new JLabel("Resistance");
        	resistancePanel.add(resistanceLabel);
        	JSlider resistanceSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, (int)(BlockManager.properties.getResistance() * 100));
        	resistancePanel.add(resistanceSlider);
        	JTextField resistanceText = new JTextField();
        	resistanceText.setPreferredSize(new Dimension(30, 24));
        	resistanceText.setEditable(false);
        	resistancePanel.add(resistanceText);
	        ChangeListener clResistance = a -> {
	        	float value = resistanceSlider.getValue() / 100F;
	        	resistanceText.setText(String.valueOf(value));
	        	BlockManager.properties.setResistance(value);
	        };
	        resistanceSlider.addChangeListener(clResistance);
	        clResistance.stateChanged(null);
	        
	        //Lightlevel
	        JPanel lightPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        	blockPropertiesPanel.add(lightPanel);
        	JLabel lightLabel = new JLabel("Light level");
        	lightPanel.add(lightLabel);
        	JSlider lightSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, (int)(BlockManager.properties.getLightLevel() * 100));
        	lightPanel.add(lightSlider);
        	JTextField lightText = new JTextField();
        	lightText.setPreferredSize(new Dimension(30, 24));
        	lightText.setEditable(false);
        	lightPanel.add(lightText);
	        ChangeListener clLight = a -> {
	        	float value = lightSlider.getValue() / 100F;
	        	lightText.setText(String.valueOf(value));
	        	BlockManager.properties.setResistance(value);
	        };
	        lightSlider.addChangeListener(clLight);
	        clLight.stateChanged(null);
        }
        
        //Set Material and SoundType
        {
        	
        }
        
        generalSpringLayout.putConstraint(SpringLayout.WEST, idsPanel, 5, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, idsPanel, 5, SpringLayout.NORTH, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, idsPanel, 5, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.WEST, separator, 0, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, separator, 0, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, separator, 5, SpringLayout.SOUTH, idsPanel);
        generalSpringLayout.putConstraint(SpringLayout.WEST, blockPropertiesPanel, 10, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, blockPropertiesPanel, -10, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, blockPropertiesPanel, 10, SpringLayout.SOUTH, separator);
		
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.requestFocus();
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
	}
	
}
