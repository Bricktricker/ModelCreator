package com.mrcrayfish.modelcreator.share;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mrcrayfish.modelcreator.Icons;
import com.mrcrayfish.modelcreator.ModelCreator;
import com.mrcrayfish.modelcreator.Settings;
import com.mrcrayfish.modelcreator.block.BlockManager;
import com.mrcrayfish.modelcreator.util.StreamUtils;
import com.mrcrayfish.modelcreator.util.Util;

public class Uploader {

	public static final String SHARE_URL = "https://pastie.io/documents";
	
	public static void upload(ModelCreator creator) {
		final JDialog dialog = new JDialog(creator, "Uploading...", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        SpringLayout layout = new SpringLayout();
        JPanel panel = new JPanel(layout);
        panel.setPreferredSize(new Dimension(120, 70));
        dialog.add(panel);
        
        JLabel label = new JLabel("Uploading...");
        panel.add(label);
        
        layout.putConstraint(SpringLayout.NORTH, label, 10, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.EAST, label, 10, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.SOUTH, label, 10, SpringLayout.SOUTH, panel);
        layout.putConstraint(SpringLayout.WEST, label, 10, SpringLayout.WEST, panel);
        
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        
        new Thread(() -> {
        	File projectFile = new File(Settings.getProjectsDir(), BlockManager.projectName + ".block");
    		if(!projectFile.exists())
    			return;
    		
    		String response = null;
    		String base64Project = null;
    		try {
    			byte[] data = getProjectBytes(projectFile);
    			base64Project = new String(data);	
    		}catch(IOException e) {
    			Util.writeCrashLog(e);
    			return;
    		}
    		try {
    			response = doUpload(base64Project.getBytes(StandardCharsets.UTF_8));
    		}catch(IOException e) {
    			//error while uploading
    			String errorMsg = "Failed to upload project:" + e.getMessage();
    			SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, errorMsg, "Upload failed", JOptionPane.ERROR_MESSAGE));
    			return;
    		}
    		
    		JsonParser parser = new JsonParser();
            JsonElement parsed = parser.parse(response);
    		String key = parsed.getAsJsonObject().get("key").getAsString();
    		
    		SwingUtilities.invokeLater(() -> {
    			dialog.dispose();
    			showUploaded(creator, key);
    		});
        }).start();
        
        dialog.setVisible(true);
	}
	
	private static String doUpload(byte[] data) throws IOException {
		URL url = new URL(SHARE_URL);
		URLConnection con = url.openConnection();
		HttpURLConnection http = (HttpURLConnection)con;
		http.setRequestMethod("POST");
		http.setDoOutput(true);
		http.setFixedLengthStreamingMode(data.length);
		http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		http.setRequestProperty("Accept", "application/json, */*");
		http.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:67.0) Gecko/20100101 Firefox/67.0");
		http.connect();
		try(OutputStream os = http.getOutputStream()) {
		    os.write(data);
		}
		String response = StreamUtils.convertToString(http.getInputStream());
		return response;
	}
	
	private static byte[] getProjectBytes(File project) throws IOException {
		byte[] rawData = Files.readAllBytes(project.toPath());
		Base64.Encoder encoder = Base64.getEncoder();
		return encoder.encode(rawData);
	}
	
	private static void showUploaded(ModelCreator creator, String key) {
		JDialog dialog = new JDialog(creator, "Project uploaded", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        SpringLayout layout = new SpringLayout();
        JPanel panel = new JPanel(layout);
        panel.setPreferredSize(new Dimension(300, 100));
        dialog.add(panel);

        JLabel labelInfo = new JLabel("<html>The project has been uploaded with the following key:</html>");
        panel.add(labelInfo);
        
        JTextArea urlField = new JTextArea();
        urlField.setEditable(false);
        urlField.setText(key);
        panel.add(urlField);
        
        JButton copyButton = new JButton();
        copyButton.setIcon(Icons.clipboard);
        copyButton.addActionListener(a -> {
        	StringSelection stringSelection = new StringSelection(key);
    		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    		clipboard.setContents(stringSelection, null);
        });
        panel.add(copyButton);
        
        layout.putConstraint(SpringLayout.NORTH, labelInfo, 10, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.EAST, labelInfo, -10, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.WEST, labelInfo, 10, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, copyButton, 10, SpringLayout.SOUTH, labelInfo);
        layout.putConstraint(SpringLayout.EAST, copyButton, -10, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.NORTH, urlField, 10, SpringLayout.SOUTH, labelInfo);
        layout.putConstraint(SpringLayout.EAST, urlField, -10, SpringLayout.WEST, copyButton);
        layout.putConstraint(SpringLayout.WEST, urlField, 10, SpringLayout.WEST, panel);
        
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
	}
}
