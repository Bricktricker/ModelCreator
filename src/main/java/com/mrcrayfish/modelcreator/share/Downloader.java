package com.mrcrayfish.modelcreator.share;

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

import javax.swing.JOptionPane;

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
    	
    	String response;
    	try {
			InputStream is = new BufferedInputStream(openStream(key));
			response = StreamUtils.convertToString(is);
		} catch (MalformedURLException e) {
			//invalid key
			JOptionPane.showMessageDialog(null, "Invalid key", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		} catch (IOException e) {
			//error while downloading
			JOptionPane.showMessageDialog(null, "Error while downloading project:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        ProjectManager.loadProject(creator.getSidebarManager(), is);
    	}catch(Exception e) {
    		Util.writeCrashLog(e);
    	}
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
