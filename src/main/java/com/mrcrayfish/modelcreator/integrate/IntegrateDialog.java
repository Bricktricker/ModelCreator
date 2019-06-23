package com.mrcrayfish.modelcreator.integrate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

import com.mrcrayfish.modelcreator.ModelCreator;
import com.mrcrayfish.modelcreator.Settings;
import com.mrcrayfish.modelcreator.block.TextFieldListener;
import com.mrcrayfish.modelcreator.util.ComponentUtil;

public class IntegrateDialog
{
	public static String modid;
	public static String resourcePath;
	public static String assetName;
	public static String javaClass;
	
	public static void show(ModelCreator creator) {
		JDialog dialog = new JDialog(creator, "Integrate", Dialog.ModalityType.APPLICATION_MODAL);
		
		JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(500, 400));
        dialog.add(panel);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        panel.add(tabbedPane, BorderLayout.CENTER);
        
        boolean[] dataValid = {false};
        tabbedPane.addTab("general", createGeneralPanel(dialog, v -> {
        	if(tabbedPane.getTabCount() > 1) {
        		tabbedPane.setEnabledAt(1, v);	
        	}else{
        		dataValid[0] = v;
        	}
        }));
        JPanel dataPanel = createIntegratePanel();
        tabbedPane.addTab("data", dataPanel);
        tabbedPane.setEnabledAt(1, dataValid[0]);
        
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                Settings.saveSettings();
            }
        });
        
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.requestFocus();
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
	}
	
	private static JPanel createGeneralPanel(Component parent, Consumer<Boolean> isValid) {
		Border invalidBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.red);
		Border defaultBorder = (new JTextField()).getBorder();
		boolean[] validPanels = {false, false, false, false}; //modid, resource path, asset name, java class
		
		//Register input handler
        BiConsumer<Integer, JTextField> textFieldHandler = (id, textField) -> {
        	String text = textField.getText();
        	if(text.isEmpty() || (!Pattern.matches("[A-Za-z0-9]+", text) && id != 1)) {
        		textField.setBorder(invalidBorder);
        		validPanels[id] = false;
        		isValid.accept(false);
        	}else {
        		textField.setBorder(defaultBorder);
        		validPanels[id] = true;

        		switch(id) {
        		case 0:
        			Settings.setModID(text);
        			modid = text;
        			break;
        		case 1:
        			Settings.setResourcePath(text);
        			resourcePath = text;
        			break;
        		case 2:
        			assetName = text;
        			break;
        		case 3:
        			javaClass = text;
        			break;
        		}
        		
        		isValid.accept(validPanels[0] && validPanels[1] && validPanels[2] && validPanels[3]);
        	}
        };
		
		SpringLayout generalSpringLayout = new SpringLayout();
        JPanel generalPanel = new JPanel(generalSpringLayout);
        
        //Needed: modid, item name, java/resource path, notepad.exe path
        //in general tab: Block Java name, block resource name, modid, resource path
        
        //modid
        JPanel modidPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        generalPanel.add(modidPanel);
        
        JLabel modidLabel = new JLabel("Mod ID");
        modidPanel.add(modidLabel);
        
        JTextField modidText = new JTextField();
        modidText.setPreferredSize(new Dimension(125, 24));
        modidText.setText(Settings.getModID());
        
        textFieldHandler.accept(0, modidText);
        modidText.getDocument().addDocumentListener(new TextFieldListener(s -> textFieldHandler.accept(0, modidText)));
        
        modidPanel.add(modidText);
        
        //resource path
        JPanel resourcePanel = ComponentUtil.createFolderSelector("Mod resources", parent, Settings.getResourcePath());
        generalPanel.add(resourcePanel);
        JTextField resourceField = Stream.of(resourcePanel.getComponents()).filter(c -> c instanceof JTextField).map(c -> (JTextField)c).collect(Collectors.toList()).get(0);
        textFieldHandler.accept(1, resourceField);
        resourceField.getDocument().addDocumentListener(new TextFieldListener(s -> textFieldHandler.accept(1, resourceField)));
        
        JSeparator separator = new JSeparator();
        generalPanel.add(separator);
        
        //asset name
        JPanel assetNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        generalPanel.add(assetNamePanel);
        
        JLabel assetNameLabel = new JLabel("Asset name");
        assetNamePanel.add(assetNameLabel);
        
        JTextField assetText = new JTextField();
        assetText.setPreferredSize(new Dimension(125, 24));
        assetText.setText(assetName);
        
        textFieldHandler.accept(2, assetText);
        assetText.getDocument().addDocumentListener(new TextFieldListener(s -> textFieldHandler.accept(2, assetText)));
        assetNamePanel.add(assetText);
        
        //java name
        JPanel javaNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        generalPanel.add(javaNamePanel);
        
        JLabel javaNameLabel = new JLabel("Java class");
        javaNamePanel.add(javaNameLabel);
        
        JTextField javaText = new JTextField();
        javaText.setPreferredSize(new Dimension(125, 24));
        javaText.setText(javaClass);
        
        textFieldHandler.accept(3, javaText);
        javaText.getDocument().addDocumentListener(new TextFieldListener(s -> textFieldHandler.accept(3, javaText)));
        javaNamePanel.add(javaText);
        
        generalSpringLayout.putConstraint(SpringLayout.WEST, modidPanel, 5, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, modidPanel, 5, SpringLayout.NORTH, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, modidPanel, -5, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, resourcePanel, -10, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.WEST, resourcePanel, 10, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, resourcePanel, 10, SpringLayout.SOUTH, modidPanel);
        generalSpringLayout.putConstraint(SpringLayout.WEST, separator, 0, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, separator, 0, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, separator, 5, SpringLayout.SOUTH, resourcePanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, assetNamePanel, -5, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.WEST, assetNamePanel, 5, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, assetNamePanel, 5, SpringLayout.SOUTH, separator);
        generalSpringLayout.putConstraint(SpringLayout.EAST, javaNamePanel, -5, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.WEST, javaNamePanel, 5, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, javaNamePanel, 5, SpringLayout.SOUTH, assetNamePanel);
        
        return generalPanel;
	}
	
	private static JPanel createIntegratePanel() {
		SpringLayout generalSpringLayout = new SpringLayout();
        JPanel generalPanel = new JPanel(generalSpringLayout);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        generalPanel.add(tabbedPane, BorderLayout.CENTER);
        
        JPanel craftingPanel = createPanel(new IntegrateCrafting());
        tabbedPane.addTab("crafting", craftingPanel);
        
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
