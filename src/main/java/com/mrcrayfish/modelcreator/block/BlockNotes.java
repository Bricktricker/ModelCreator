package com.mrcrayfish.modelcreator.block;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;

import com.mrcrayfish.modelcreator.ModelCreator;

public class BlockNotes
{
	private String notes;
	
	public BlockNotes() {}
	public BlockNotes(String text) {
		this.notes = text;
	}
	
	public String getNotes() {
		return notes;
	}
	
	public void setNotes(String text) {
		notes = text;
	}
	
	public static void show(ModelCreator creator) {
		JDialog dialog = new JDialog(creator, "Notes", Dialog.ModalityType.APPLICATION_MODAL);
		
		JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(500, 250));
        dialog.add(panel);
        
        SpringLayout generalSpringLayout = new SpringLayout();
        JPanel generalPanel = new JPanel(generalSpringLayout);
        panel.add(generalPanel);
        
        JTextArea textArea = new JTextArea();
        JScrollPane scrollpane = new JScrollPane(textArea);
        scrollpane.setPreferredSize(new Dimension(475, 200));
        generalPanel.add(scrollpane);
        
        //Save Button
    	JButton saveButton = new JButton("Save");
        saveButton.setPreferredSize(new Dimension(80, 24));
        saveButton.addActionListener(e ->
        {
        	String text = textArea.getText();
        	BlockManager.notes.setNotes(text);
        });
        generalPanel.add(saveButton);
        
        generalSpringLayout.putConstraint(SpringLayout.WEST, scrollpane, 10, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, scrollpane, -10, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, scrollpane, 10, SpringLayout.NORTH, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.SOUTH, scrollpane, -10, SpringLayout.NORTH, saveButton);
        generalSpringLayout.putConstraint(SpringLayout.WEST, saveButton, 5, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, saveButton, -5, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.SOUTH, saveButton, -10, SpringLayout.SOUTH, generalPanel);
        
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.requestFocus();
        dialog.pack();
        dialog.setResizable(true);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
	}
	
}
