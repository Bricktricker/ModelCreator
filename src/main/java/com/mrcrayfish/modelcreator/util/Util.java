package com.mrcrayfish.modelcreator.util;

import com.google.gson.Gson;
import com.mrcrayfish.modelcreator.ProjectManager;
import com.mrcrayfish.modelcreator.Settings;
import com.mrcrayfish.modelcreator.SidebarManager;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class Util
{
    private static final List<String> minecraftVersions;

    static
    {
        List<String> versionList = new ArrayList<>();
        File file = getMinecraftDirectory();
        if(file != null && file.exists() && file.isDirectory())
        {
            File versions = null;
            for(File folder : getSubFolders(file))
            {
                if(folder.getName().equals("versions"))
                {
                    versions = folder;
                    break;
                }
            }
            if(versions != null)
            {
                for(File folder : getSubFolders(versions))
                {
                    File json = getFile(folder, folder.getName() + ".json");
                    if(json != null && !isLegacyAssets(json))
                    {
                        if(hasFile(folder, folder.getName() + ".jar"))
                        {
                            versionList.add(folder.getName());
                        }
                    }
                }
            }
        }
        minecraftVersions = versionList;
    }

    public static List<String> getMinecraftVersions()
    {
        return minecraftVersions;
    }

    public static Dimension getImageDimension(File image) throws IOException
    {
        int pos = image.getName().lastIndexOf(".");
        if(pos != -1)
        {
            String suffix = image.getName().substring(pos + 1);
            Iterator<ImageReader> it = ImageIO.getImageReadersBySuffix(suffix);
            while(it.hasNext())
            {
                ImageReader reader = it.next();
                try
                {
                    ImageInputStream stream = new FileImageInputStream(image);
                    reader.setInput(stream);
                    int width = reader.getWidth(reader.getMinIndex());
                    int height = reader.getHeight(reader.getMinIndex());
                    stream.flush();
                    stream.close();
                    return new Dimension(width, height);
                }
                catch(IOException e)
                {
                    Util.writeCrashLog(e);
                }
                finally
                {
                    reader.dispose();
                }
            }
        }
        throw new IOException("Not a known image file: " + image.getAbsolutePath());
    }

    public static void openUrl(String url)
    {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if(desktop != null && desktop.isSupported(Desktop.Action.BROWSE))
        {
            try
            {
                desktop.browse(new URL(url).toURI());
            }
            catch(Exception e)
            {
                Util.writeCrashLog(e);
            }
        }
    }

    public static void loadModelFromJar(SidebarManager manager, Class<?> clazz, String name)
    {
        
    	InputStream is = clazz.getClassLoader().getResourceAsStream(name + ".block");
    	ProjectManager.loadProject(manager, is);
        
    }

    public static void extractMinecraftAssets(String version, Window window)
    {
    	File destination = new File("resources", version);
    	if(!destination.mkdirs()) {
    		System.err.println("Could not create folders");
    		return;
    	}
    	File jar = new File(getMinecraftDirectory(), "versions/" + version + "/" + version + ".jar");
    	Function<ZipEntry, String> conditions = zipEntry -> {
    		String entryName = zipEntry.getName();
    		boolean isAssetRoot = entryName.endsWith("mcassetsroot");
    		if(isAssetRoot)
    			return null;
  
    		boolean isAsset = entryName.startsWith("assets/");
    		if(!isAsset)
    			return null;
    		
    		final String[] allowedDirs = {"blockstates", "models", "textures"};
    		for(String s : allowedDirs) {
    			if(entryName.contains(s)) {
    				assert(entryName.startsWith("assets/minecraft"));
    				String newName = zipEntry.getName().replace("assets/minecraft", "");
    				if(newName.startsWith("/"))
    					newName = newName.substring(1);
    				return newName;
    			}
    		}
    		
    		return null;
    	};
    	extractZipFiles(jar, conditions, window, destination, suc -> {
    		if(suc) {
			Settings.addExtractedAsset(version);
        		Settings.saveSettings();
		}   
	});
    }

    private static void extractZipFiles(File zipFile, Function<ZipEntry, String> conditions, Window window, File extractionFolder, Consumer<Boolean> finishCallback)
    {
        final boolean[] cancelled = {false};

        JDialog dialog = new JDialog(window, "Extracting Assets", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosed(WindowEvent e)
            {
                cancelled[0] = true;
            }
        });

        SpringLayout layout = new SpringLayout();
        JPanel panel = new JPanel(layout);
        panel.setPreferredSize(new Dimension(300, 60));
        dialog.add(panel);

        JLabel labelProcessing = new JLabel("Processing");
        panel.add(labelProcessing);

        JLabel labelFile = new JLabel();
        panel.add(labelFile);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setForeground(new Color(129, 192, 0));
        panel.add(progressBar);

        layout.putConstraint(SpringLayout.NORTH, labelProcessing, 10, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, labelProcessing, 10, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, labelFile, 10, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, labelFile, 5, SpringLayout.EAST, labelProcessing);
        layout.putConstraint(SpringLayout.EAST, labelFile, -10, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.NORTH, progressBar, 10, SpringLayout.SOUTH, labelProcessing);
        layout.putConstraint(SpringLayout.WEST, progressBar, 10, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.EAST, progressBar, -10, SpringLayout.EAST, panel);

        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);

        new Thread(() ->
        {
            List<ZipEntry> entries = new ArrayList<>();
            try(ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile)))
            {
                ZipEntry ze;
                while((ze = zis.getNextEntry()) != null)
                {
                    if(cancelled[0])
                    {
                    	finishCallback.accept(false);
                        return;
                    }
                    if(conditions.apply(ze) == null)
                    {
                        continue;
                    }
                    entries.add(ze);
                }
                zis.closeEntry();
                zis.close();
            }
            catch(IOException e)
            {
                Util.writeCrashLog(e);
            }

            if(entries.size() > 0)
            {
                SwingUtilities.invokeLater(() -> progressBar.setMaximum(entries.size()));
            }

            if(cancelled[0])
            {
            	finishCallback.accept(false);
                return;
            }

            try(ZipFile f = new ZipFile(zipFile))
            {
                for(int i = 0; i < entries.size(); i++)
                {
                    if(cancelled[0])
                    {
                    	finishCallback.accept(false);
                        return;
                    }

                    ZipEntry entry = entries.get(i);
                    SwingUtilities.invokeLater(() -> labelFile.setText(entry.getName()));

                    InputStream is = f.getInputStream(entry);

                    File file = new File(extractionFolder, conditions.apply(entry));
                    file.getParentFile().mkdirs();
                    file.createNewFile();

                    byte[] buffer = new byte[8192];
                    FileOutputStream fos = new FileOutputStream(file);
                    int len;
                    while((len = is.read(buffer)) > 0)
                    {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();

                    final int value = i;
                    SwingUtilities.invokeLater(() -> progressBar.setValue(value + 1));
                }
                SwingUtilities.invokeLater(window::dispose);
            }
            catch(IOException e)
            {
                Util.writeCrashLog(e);
            }
            
            finishCallback.accept(true);
        }).start();

        dialog.setVisible(true);
    }

    public static File getMinecraftDirectory()
    {
        String userHome = System.getProperty("user.home", ".");
        OperatingSystem os = OperatingSystem.get();
        switch(os)
        {
            case WINDOWS:
                String appDataDir = System.getenv("APPDATA");
                if(appDataDir != null)
                {
                    return new File(appDataDir, ".minecraft");
                }
            case MAC:
                return new File(userHome, "Library/Application Support/minecraft");
            case LINUX:
                return new File(userHome, ".minecraft");
            default:
                return null;
        }
    }

    private static File[] getSubFolders(File parent)
    {
        return parent.listFiles((dir, name) -> dir.isDirectory());
    }

    private static boolean hasFile(File parent, String targetName)
    {
        File[] files = parent.listFiles((dir, name) -> name.equals(targetName));
        return files != null && files.length == 1;
    }

    private static File getFile(File parent, String targetName)
    {
        File[] files = parent.listFiles((dir, name) -> name.equals(targetName));
        if(files != null)
        {
            return Arrays.stream(files).filter(file -> !file.isDirectory() && file.getName().equals(targetName)).findFirst().orElse(null);
        }
        return null;
    }

    public static boolean hasFolder(File parent, String targetName)
    {
        File[] files = parent.listFiles((dir, name) -> name.equals(targetName));
        return files != null && Arrays.stream(files).anyMatch(File::isDirectory);
    }
    
    public static void writeCrashLog(Throwable e) {
    	e.printStackTrace();
    	try {
    	JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    	}catch(Exception ex) {}
    	
    	DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy HH_mm_ss");
    	File file = new File("crash_" + dateFormat.format(new Date()) + ".log");
    	try(PrintStream stream = new PrintStream(file))
    	{
    		String message = e.getMessage();
    		stream.print(message + "\n");
    		e.printStackTrace(stream);
    		if(e.getCause() != null)
    			stream.print("Cause: \n" + e.getCause().getMessage());
    	}catch(Exception ex) {
    		ex.printStackTrace();
    	}
		System.exit(1);
    }

    private static boolean isLegacyAssets(File file)
    {
        try
        {
            String json = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
            Gson gson = new Gson();
            VersionProperties properties = gson.fromJson(json, VersionProperties.class);
            return properties != null && properties.assetIndex != null && "legacy".equals(properties.assetIndex.id);
        }
        catch(IOException e)
        {
           Util.writeCrashLog(e);
        }
        return false;
    }

    private static class VersionProperties
    {
        public AssetIndex assetIndex;
    }

    private static class AssetIndex
    {
        private String id;
    }
}
