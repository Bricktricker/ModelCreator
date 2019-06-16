package com.mrcrayfish.modelcreator.block;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeListener;

import com.mrcrayfish.modelcreator.ModelCreator;

public class BlockProperties
{
	private float hardness; //In vanilla between 0 and 100
	private float resistance;
	private int lightLevel;
	private String material;
	private String sound;
	
	public BlockProperties() {
		hardness = 0.5F;
		resistance = 30.0F;
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

	public int getLightLevel()
	{
		return lightLevel;
	}

	public void setLightLevel(int lightLevel)
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
        panel.setPreferredSize(new Dimension(500, 225));
        dialog.add(panel);
        
        SpringLayout generalSpringLayout = new SpringLayout();
        JPanel generalPanel = new JPanel(generalSpringLayout);
        panel.add(generalPanel);
        
        final SpinnerNumberModel hardnessSpinnerModel = new SpinnerNumberModel(BlockManager.properties.getHardness(), 0.0, 1000.0, 0.1);
        final SpinnerNumberModel resistanceSpinnerModel = new SpinnerNumberModel(BlockManager.properties.getResistance(), 0.0, Double.MAX_VALUE, 1.0);
        final JSlider lightSlider = new JSlider();
        final JComboBox<String> comboBoxMaterials = new JComboBox<>();
        final JComboBox<String> comboBoxSounds = new JComboBox<>();
        
        //Setting for BlockProperties
        JPanel blockPropertiesPanel = new JPanel(new GridLayout(0, 1));
        {
        	generalPanel.add(blockPropertiesPanel);
        	
        	//Hardness
        	JPanel hardnessPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        	blockPropertiesPanel.add(hardnessPanel);
        	
        	JLabel hardnessLabel = new JLabel("Hardness");
        	hardnessPanel.add(hardnessLabel);
        	
        	JSpinner hardnessSpinner = new JSpinner(hardnessSpinnerModel);
        	hardnessSpinner.setPreferredSize(new Dimension(50, 24));
        	hardnessPanel.add(hardnessSpinner);
        	
        	//Resistance
	        JPanel resistancePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        	blockPropertiesPanel.add(resistancePanel);
        	
        	JLabel resistanceLabel = new JLabel("Resistance");
        	resistancePanel.add(resistanceLabel);
        	
        	JSpinner resistanceSpinner = new JSpinner(resistanceSpinnerModel);
        	resistanceSpinner.setPreferredSize(new Dimension(50, 24));
        	resistancePanel.add(resistanceSpinner);
	        
	        //Lightlevel
	        JPanel lightPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        	blockPropertiesPanel.add(lightPanel);
        	
        	JLabel lightLabel = new JLabel("Light level");
        	lightPanel.add(lightLabel);
        	
        	lightSlider.setOrientation(JSlider.HORIZONTAL);
        	lightSlider.setMinimum(0);
        	lightSlider.setMaximum(15);
        	lightSlider.setValue(BlockManager.properties.getLightLevel());
        	lightSlider.setMinorTickSpacing(1);
        	lightPanel.add(lightSlider);
        	
        	JTextField lightText = new JTextField();
        	lightText.setPreferredSize(new Dimension(50, 24));
        	lightText.setEditable(false);
        	lightPanel.add(lightText);
	        ChangeListener clLight = a -> {
	        	int value = lightSlider.getValue();
	        	lightText.setText(String.valueOf(value));
	        };
	        lightSlider.addChangeListener(clLight);
	        clLight.stateChanged(null);
        }
        
        JSeparator separator = new JSeparator();
        generalPanel.add(separator);
        
        //Set Material and SoundType
        JPanel soundMatPanel = new JPanel(new GridLayout(1, 2));
        {
        	generalPanel.add(soundMatPanel);
        	
        	//Material
        	JPanel materialPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        	soundMatPanel.add(materialPanel);
        	JLabel materialLabel = new JLabel("Block material");
        	materialPanel.add(materialLabel);
        	
        	comboBoxMaterials.setPreferredSize(new Dimension(150, 24));
        	comboBoxMaterials.addItem("");
        	Resources.materials.forEach(comboBoxMaterials::addItem);
        	materialPanel.add(comboBoxMaterials);
        	
        	//Sound
        	JPanel soundPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        	soundMatPanel.add(soundPanel);
        	JLabel soundLabel = new JLabel("Block sound");
        	soundPanel.add(soundLabel);
        	
        	comboBoxSounds.setPreferredSize(new Dimension(150, 24));
        	comboBoxSounds.addItem("");
        	Resources.soundTypes.forEach(comboBoxSounds::addItem);
        	soundPanel.add(comboBoxSounds);
        }
        
        //Save Button
        JPanel savePanel = new JPanel(new GridLayout(0, 1));
        generalPanel.add(savePanel);
    	JButton saveButton = new JButton("Save");
        saveButton.setPreferredSize(new Dimension(80, 24));
        saveButton.addActionListener(e ->
        {
        	double hardness = (Double)hardnessSpinnerModel.getValue();
        	double resistance = (Double)resistanceSpinnerModel.getValue();
        	int lightLevel = lightSlider.getValue();
        	String material = (String)comboBoxMaterials.getSelectedItem();
        	String sound = (String)comboBoxSounds.getSelectedItem();
        	
        	BlockManager.properties.setHardness((float)hardness);
        	BlockManager.properties.setResistance((float)resistance);
        	BlockManager.properties.setLightLevel(lightLevel);
        	BlockManager.properties.setMaterial(material);
        	BlockManager.properties.setSound(sound);
        });
        savePanel.add(saveButton);
        
        generalSpringLayout.putConstraint(SpringLayout.WEST, blockPropertiesPanel, 10, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, blockPropertiesPanel, -10, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, blockPropertiesPanel, 10, SpringLayout.NORTH, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.WEST, separator, 0, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, separator, 0, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, separator, 5, SpringLayout.SOUTH, blockPropertiesPanel);
        generalSpringLayout.putConstraint(SpringLayout.WEST, soundMatPanel, 10, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, soundMatPanel, -10, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, soundMatPanel, 10, SpringLayout.SOUTH, separator); 
        generalSpringLayout.putConstraint(SpringLayout.WEST, savePanel, 10, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, savePanel, -10, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, savePanel, 10, SpringLayout.SOUTH, soundMatPanel);
		
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.requestFocus();
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
	}
	
}
