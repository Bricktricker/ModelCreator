package com.mrcrayfish.modelcreator.integrate;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;

import com.mrcrayfish.modelcreator.ModelCreator;

public class IntegrateDialog
{
	public static void show(ModelCreator creator) {
		JDialog dialog = new JDialog(creator, "Integrate", Dialog.ModalityType.APPLICATION_MODAL);
		
		JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(500, 400));
        dialog.add(panel);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        panel.add(tabbedPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("general", createGeneralPanel(v -> tabbedPane.setEnabledAt(1, v)));
        JPanel dataPanel = createIntegratePanel();
        tabbedPane.addTab("data", dataPanel);
        tabbedPane.setEnabledAt(1, false);
        
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.requestFocus();
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
	}
	
	private static JPanel createGeneralPanel(Consumer<Boolean> valid) {
		SpringLayout generalSpringLayout = new SpringLayout();
        JPanel generalPanel = new JPanel(generalSpringLayout);
        
        return generalPanel;
	}
	
	private static JPanel createIntegratePanel() {
		SpringLayout generalSpringLayout = new SpringLayout();
        JPanel generalPanel = new JPanel(generalSpringLayout);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        generalPanel.add(tabbedPane, BorderLayout.CENTER);
        
        
        generalSpringLayout.putConstraint(SpringLayout.WEST, tabbedPane, 5, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, tabbedPane, -5, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, tabbedPane, 5, SpringLayout.NORTH, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.SOUTH, tabbedPane, -20, SpringLayout.SOUTH, generalPanel);
        
        return generalPanel;
	}
	
	private static JPanel createPanel(IIntegrate integrator) {
		SpringLayout generalSpringLayout = new SpringLayout();
        JPanel generalPanel = new JPanel(generalSpringLayout);    
        
        JTextArea textArea = new JTextArea();
        textArea.setText(integrator.getContent());
        textArea.setEditable(false);
        JScrollPane scrollpane = new JScrollPane(textArea);
        generalPanel.add(scrollpane);
        
        //integrate Button
    	JButton integrateButton = new JButton("Integrate");
    	integrateButton.setMaximumSize(new Dimension(80, 24));
    	integrateButton.addActionListener(e -> integrator.integrate(textArea.getText()));
        generalPanel.add(integrateButton);
        
        generalSpringLayout.putConstraint(SpringLayout.WEST, scrollpane, 10, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, scrollpane, -10, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, scrollpane, 10, SpringLayout.NORTH, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.WEST, integrateButton, 10, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, integrateButton, -10, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.SOUTH, scrollpane, -10, SpringLayout.NORTH,  integrateButton);
        generalSpringLayout.putConstraint(SpringLayout.SOUTH, integrateButton, -10, SpringLayout.SOUTH, generalPanel);
        
        return generalPanel;
	}
	
}
