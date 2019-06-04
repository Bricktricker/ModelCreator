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
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;

import com.mrcrayfish.modelcreator.ModelCreator;

public class BlockProperties
{
	private float hardness;
	private float resistance;
	private float lightValue;
	//private Material material;
	//private SoundType sound;
	
	public BlockProperties() {
		hardness = 0.8F;
		resistance = 0.8F;
		lightValue = 0;
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

	public float getLightValue()
	{
		return lightValue;
	}

	public void setLightValue(float lightValue)
	{
		this.lightValue = lightValue;
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
        	
	        JPanel undoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	        idsPanel.add(undoPanel);
	
	        JLabel labelUndoLimit = new JLabel("Asset-ID");
	        undoPanel.add(labelUndoLimit);
	
	        JTextField assetText = new JTextField();
	        assetText.setPreferredSize(new Dimension(100, 24));
	        undoPanel.add(assetText);
	
	        JLabel labelJavaText = new JLabel("Asset-ID");
	        undoPanel.add(labelJavaText);
	
	        JTextField javaText = new JTextField();
	        javaText.setPreferredSize(new Dimension(100, 24));
	        undoPanel.add(javaText);
        }
        
        JSeparator separator = new JSeparator();
        generalPanel.add(separator);
        
        //Setting for BlockProperties
        {
        	
        }
        
        generalSpringLayout.putConstraint(SpringLayout.WEST, idsPanel, 5, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, idsPanel, 5, SpringLayout.NORTH, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, idsPanel, 5, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.WEST, separator, 0, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, separator, 0, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, separator, 5, SpringLayout.SOUTH, idsPanel);
		
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.requestFocus();
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
	}
	
}
