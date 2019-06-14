package com.mrcrayfish.modelcreator.component;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;

public class ComboListener extends KeyAdapter {
	JComboBox<String> cbListener;
	List<String> vector;

	public ComboListener(JComboBox<String> cbListenerParam, List<String> vectorParam) {
		cbListener = cbListenerParam;
		vector = vectorParam;
	}

	@Override
	public void keyTyped(KeyEvent key) {
		String text = ((JTextField) key.getSource()).getText();
		Vector<String> suggestions = getFilteredList(text);
		cbListener.setModel(new DefaultComboBoxModel<String>(suggestions));
		cbListener.setSelectedIndex(-1);
		((JTextField) cbListener.getEditor().getEditorComponent()).setText(text);
		cbListener.showPopup();
	}

	private Vector<String> getFilteredList(String text) {
		Vector<String> v = new Vector<>();
		for (int a = 0; a < vector.size(); a++) {
			if (vector.get(a).toString().startsWith(text)) {
				v.add(vector.get(a).toString());
			}
		}
		return v;
	}
}
