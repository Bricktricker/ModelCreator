package com.mrcrayfish.modelcreator.component;

import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;

public class AutoComboBox extends JComboBox<String> {
	private static final long serialVersionUID = 6471295549403619035L;

	public AutoComboBox(List<String> items) {
		Vector<String> showVector = new Vector<>();
		showVector.addAll(items);
		
		setModel(new DefaultComboBoxModel<String>(showVector));
	    setSelectedIndex(-1);
	    setEditable(true);
	    JTextField text = (JTextField) this.getEditor().getEditorComponent();
	    text.setFocusable(true);
	    text.setText("");
	    text.addKeyListener(new ComboListener(this, showVector));
	}
}
