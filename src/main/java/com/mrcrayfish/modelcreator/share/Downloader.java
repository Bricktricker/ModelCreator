package com.mrcrayfish.modelcreator.share;

import java.awt.Dialog;
import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.Base64.Decoder;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mrcrayfish.modelcreator.ModelCreator;
import com.mrcrayfish.modelcreator.ProjectManager;
import com.mrcrayfish.modelcreator.util.StreamUtils;
import com.mrcrayfish.modelcreator.util.Util;

public class Downloader {

	public static void download(ModelCreator creator) {
		String key = JOptionPane.showInputDialog("Enter upload key");
    	if(key == null || key.isEmpty()) {
    		JOptionPane.showMessageDialog(null, "Invalid upload key", "Error", JOptionPane.ERROR_MESSAGE);
    		return;
    	}
    	
    	final JDialog dialog = new JDialog(creator, "Downloading...", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        SpringLayout layout = new SpringLayout();
        JPanel panel = new JPanel(layout);
        panel.setPreferredSize(new Dimension(120, 70));
        dialog.add(panel);
        
        JLabel label = new JLabel("Downloading...");
        panel.add(label);
        
        layout.putConstraint(SpringLayout.NORTH, label, 10, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.EAST, label, 10, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.SOUTH, label, 10, SpringLayout.SOUTH, panel);
        layout.putConstraint(SpringLayout.WEST, label, 10, SpringLayout.WEST, panel);
        
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
    	
    	new Thread(() -> {
    		String response;
        	try {
    			InputStream is = new BufferedInputStream(openStream(key));
    			response = StreamUtils.convertToString(is);
    		} catch (MalformedURLException e) {
    			//invalid key
    			SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "Invalid key", "Error", JOptionPane.ERROR_MESSAGE)); 
    			return;
    		} catch (IOException e) {
    			//error while downloading
    			Util.writeCrashLog(e);
    			return;
    		}
        	
        	try {
    	    	JsonParser parser = new JsonParser();
    	        JsonObject parsed = parser.parse(response).getAsJsonObject();
    	        String data = parsed.get("data").getAsString();
    	        Decoder decoder = Base64.getDecoder();
    	        byte[] rawData = decoder.decode(data);
    	        ByteArrayInputStream is = new ByteArrayInputStream(rawData);
    	        
    	        SwingUtilities.invokeLater(() -> {
    	        	dialog.dispose();
    	        	ProjectManager.loadProject(creator.getSidebarManager(), is);
    	        });
    	    }catch(Exception e) {
        		Util.writeCrashLog(e);
        	}
    	}).start();
    	
    	dialog.setVisible(true);
	}
	
	private static InputStream openStream(String key) throws IOException {
		URL url = new URL("https://hastebin.com/documents/" + key);
		URLConnection con = url.openConnection();
		HttpURLConnection http = (HttpURLConnection)con;
		http.setRequestMethod("GET");
		http.setDoOutput(true);
		http.setRequestProperty("Accept", "application/json, */*");
		http.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:67.0) Gecko/20100101 Firefox/67.0");
		http.connect();
		return http.getInputStream();
	}
}
