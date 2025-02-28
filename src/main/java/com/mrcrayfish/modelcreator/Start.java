package com.mrcrayfish.modelcreator;

import com.jtattoo.plaf.fast.FastLookAndFeel;
import com.mrcrayfish.modelcreator.util.SharedLibraryLoader;
import com.mrcrayfish.modelcreator.util.Util;

import javax.swing.*;
import java.util.Properties;

public class Start
{
    public static void main(String[] args)
    {
        SharedLibraryLoader.load(false);

        Double version = Double.parseDouble(System.getProperty("java.specification.version"));
        if(version < 1.8)
        {
            JOptionPane.showMessageDialog(null, "You need Java 1.8 or higher to run this program.");
            return;
        }

        System.setProperty("org.lwjgl.util.Debug", "true");

        try
        {
            Properties props = new Properties();
            props.put("logoString", "");
            props.put("centerWindowTitle", "on");
            props.put("buttonBackgroundColor", "127 132 145");
            props.put("buttonForegroundColor", "255 255 255");
            props.put("windowTitleBackgroundColor", "97 102 115");
            props.put("windowTitleForegroundColor", "255 255 255");
            props.put("backgroundColor", "221 221 228");
            props.put("menuBackgroundColor", "221 221 228");
            props.put("controlForegroundColor", "120 120 120");
            props.put("windowBorderColor", "97 102 110");
            FastLookAndFeel.setTheme(props);
            UIManager.setLookAndFeel("com.jtattoo.plaf.fast.FastLookAndFeel");
        }
        catch(Exception e)
        {
           Util.writeCrashLog(e);
        }
        
        if(!Settings.load()) {
        	Settings.saveSettings(); //Create settings file, if none exists yet	
        }
        
        try {
        	new ModelCreator(Constants.NAME + " v" + Constants.VERSION);	
        }catch(Exception e) {
        	Util.writeCrashLog(e);
        }
    }
}
