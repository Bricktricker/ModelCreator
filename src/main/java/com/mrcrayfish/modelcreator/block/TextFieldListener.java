package com.mrcrayfish.modelcreator.block;

import java.util.function.Consumer;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

public class TextFieldListener implements DocumentListener
{
	private Consumer<String> changeListener;
	
	public TextFieldListener(Consumer<String> changeListener) {
		this.changeListener = changeListener;
	}
	
	@Override
	public void changedUpdate(DocumentEvent e)
	{
		try{
			changeEvent(e.getDocument().getText(0, e.getDocument().getLength()));
		} catch (BadLocationException ex){
			ex.printStackTrace();
		}
	}

	@Override
	public void insertUpdate(DocumentEvent e)
	{
		try{
			changeEvent(e.getDocument().getText(0, e.getDocument().getLength()));
		} catch (BadLocationException ex){
			ex.printStackTrace();
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e)
	{
		try{
			changeEvent(e.getDocument().getText(0, e.getDocument().getLength()));
		} catch (BadLocationException ex){
			ex.printStackTrace();
		}
	}
	
	private void changeEvent(String text) {
		this.changeListener.accept(text);
	}

}
