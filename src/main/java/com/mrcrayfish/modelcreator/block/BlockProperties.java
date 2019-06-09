package com.mrcrayfish.modelcreator.block;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JComboBox;
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
import com.mrcrayfish.modelcreator.Settings;

public class BlockProperties
{
	private float hardness;
	private float resistance;
	private float lightLevel;
	private String material;
	private String sound;
	
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
	
	public String getMaterial()
	{
		return material;
	}

	public void setMaterial(String material)
	{
		this.material = material;
	}

	public String getSound()
	{
		return sound;
	}

	public void setSound(String sound)
	{
		this.sound = sound;
	}

	public static void show(ModelCreator creator) {
		JDialog dialog = new JDialog(creator, "Properties", Dialog.ModalityType.APPLICATION_MODAL);
		
		JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(500, 250));
        dialog.add(panel);
        
        SpringLayout generalSpringLayout = new SpringLayout();
        JPanel generalPanel = new JPanel(generalSpringLayout);
        panel.add(generalPanel);
        
        //Settings to set the block asset- and java ID and used MC version
        JPanel idsPanel = new JPanel(new GridLayout(1, 3));
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
	        
	        //MC version
	        JPanel mcVersionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	        idsPanel.add(mcVersionPanel);
	        
	        JLabel versionLabel = new JLabel("MC");
	        mcVersionPanel.add(versionLabel);
	        
	        JComboBox<String> comboBoxVersions = new JComboBox<>();
	        comboBoxVersions.setPreferredSize(new Dimension(100, 24));
        	Settings.getExtractedAssets().forEach(comboBoxVersions::addItem);
        	comboBoxVersions.addActionListener(a -> {
        		BlockManager.usedMcVersion = (String)comboBoxVersions.getSelectedItem();
        	});
        	mcVersionPanel.add(comboBoxVersions);
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
        
        JSeparator separator2 = new JSeparator();
        generalPanel.add(separator2);
        
        //Set Material and SoundType
        JPanel soundMatPanel = new JPanel(new GridLayout(1, 2));
        {
        	generalPanel.add(soundMatPanel);
        	
        	//Material
        	JPanel materialPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        	soundMatPanel.add(materialPanel);
        	JLabel materialLabel = new JLabel("Block material");
        	materialPanel.add(materialLabel);
        	
        	JComboBox<String> comboBoxMaterials = new JComboBox<>();
        	comboBoxMaterials.setPreferredSize(new Dimension(150, 24));
        	comboBoxMaterials.addItem("");
        	Resources.materials.forEach(comboBoxMaterials::addItem);
        	comboBoxMaterials.addActionListener(a -> {
        		String material = (String)comboBoxMaterials.getSelectedItem();
        		BlockManager.properties.material = material;
        	});
        	materialPanel.add(comboBoxMaterials);
        	
        	//Sound
        	JPanel soundPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        	soundMatPanel.add(soundPanel);
        	JLabel soundLabel = new JLabel("Block sound");
        	soundPanel.add(soundLabel);
        	
        	JComboBox<String> comboBoxSounds = new JComboBox<>();
        	comboBoxSounds.setPreferredSize(new Dimension(150, 24));
        	comboBoxSounds.addItem("");
        	Resources.soundTypes.forEach(comboBoxSounds::addItem);
        	comboBoxSounds.addActionListener(a -> {
        		String sound = (String)comboBoxSounds.getSelectedItem();
        		BlockManager.properties.sound = sound;
        	});
        	soundPanel.add(comboBoxSounds);
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
        generalSpringLayout.putConstraint(SpringLayout.WEST, separator2, 0, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, separator2, 0, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, separator2, 5, SpringLayout.SOUTH, blockPropertiesPanel);
        generalSpringLayout.putConstraint(SpringLayout.WEST, soundMatPanel, 10, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, soundMatPanel, -10, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, soundMatPanel, 10, SpringLayout.SOUTH, separator2);
		
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.requestFocus();
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
	}
	
}
