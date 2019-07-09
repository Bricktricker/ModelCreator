package com.mrcrayfish.modelcreator.component;

import com.mrcrayfish.modelcreator.*;
import com.mrcrayfish.modelcreator.block.BlockCrafting;
import com.mrcrayfish.modelcreator.block.BlockLoot;
import com.mrcrayfish.modelcreator.block.BlockManager;
import com.mrcrayfish.modelcreator.block.BlockNotes;
import com.mrcrayfish.modelcreator.block.BlockProperties;
import com.mrcrayfish.modelcreator.block.BlockTranslation;
import com.mrcrayfish.modelcreator.display.DisplayProperties;
import com.mrcrayfish.modelcreator.element.CombinedState;
import com.mrcrayfish.modelcreator.element.Face;
import com.mrcrayfish.modelcreator.integrate.IntegrateDialog;
import com.mrcrayfish.modelcreator.share.Downloader;
import com.mrcrayfish.modelcreator.share.Uploader;
import com.mrcrayfish.modelcreator.util.ComponentUtil;
import com.mrcrayfish.modelcreator.util.KeyboardUtil;
import com.mrcrayfish.modelcreator.util.Util;
import org.lwjgl.input.Keyboard;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;

public class Menu extends JMenuBar
{
	private static final long serialVersionUID = -146313069497706475L;

	private ModelCreator creator;

    /* File */
    private JMenu menuFile;
    private JMenuItem itemNew;
    private JMenuItem itemLoad;
    private JMenuItem itemSave;
    private JMenuItem itemIntegrate;
    private JMenu menuShare;
    private JMenuItem itemUpload;
    private JMenuItem itemDownload;
    private JMenuItem itemImport;
    private JMenuItem itemExport;
    private JMenuItem itemSettings;
    private JMenuItem itemExit;

    /* Edit */
    private JMenu menuEdit;
    private JMenuItem itemUndo;
    private JMenuItem itemRedo;

    /* Model */
    private JMenu menuModel;
    private JMenuItem itemTextureManager;
    private JMenuItem itemDisplayProps;
    private JMenuItem itemOptimise;
    private JMenu menuRotate;
    private JMenuItem itemRotateClockwise;
    private JMenuItem itemRotateCounterClockwise;
    
    /* Block */
    private JMenu menuBlock;
    private JMenuItem itemProperties;
    private JMenuItem itemTransation;
    private JMenuItem itemCrafting;
    private JMenuItem itemLoot;
    private JMenuItem itemNotes;

    /* More */
    private JMenu menuMore;
    private JMenuItem itemExtractAssets;
    private JMenu menuExamples;
    private JMenuItem itemModelCauldron;
    private JMenuItem itemModelChair;
    private JMenu menuWiki;
    private JMenuItem itemHardnessWiki;
    private JMenuItem itemResistanceWiki;
    private JMenuItem itemDonate;
    private JMenuItem itemGitHub;

    public static DisplayPropertiesDialog displayPropertiesDialog = null;
    public static boolean shouldRenderGrid = false;

    public Menu(ModelCreator creator)
    {
        this.creator = creator;
        this.initMenu();
    }

    private void initMenu()
    {
        menuFile = new JMenu("File");
        {
            itemNew = createMenuItem("New", "New Model", KeyEvent.VK_N, Icons.new_, KeyEvent.VK_N, Keyboard.KEY_N, InputEvent.CTRL_MASK);
            itemLoad = createMenuItem("Load Project...", "Load Project from File", KeyEvent.VK_S, Icons.load, KeyEvent.VK_O, Keyboard.KEY_O, InputEvent.CTRL_MASK);
            itemSave = createMenuItem("Save Project...", "Save Project to File", KeyEvent.VK_S, Icons.disk, KeyEvent.VK_S, Keyboard.KEY_S, InputEvent.CTRL_MASK);
            itemIntegrate = createMenuItem("Integrate Project...", "Integrate the project into the mod environment", -1, Icons.extract);
            
            menuShare = new JMenu("Share");
            menuShare.setIcon(Icons.share);
            {
            	itemUpload = createMenuItem("Upload", "Uploads project to share it with others", KeyEvent.VK_U, Icons.upload);
            	itemDownload = createMenuItem("Download", "Download project from others", KeyEvent.VK_D, Icons.download);
            }
            
            itemImport = createMenuItem("Import JSON...", "Import Model from JSON", KeyEvent.VK_I, Icons.import_);
            itemExport = createMenuItem("Export JSON...", "Export Model to JSON", KeyEvent.VK_E, Icons.export);
            itemSettings = createMenuItem("Settings", "Change the settings of the Model Creator", KeyEvent.VK_S, Icons.settings, KeyEvent.VK_S, Keyboard.KEY_S, InputEvent.CTRL_MASK + InputEvent.ALT_MASK);
            itemExit = createMenuItem("Exit", "Exit Application", KeyEvent.VK_E, Icons.exit);
        }

        menuEdit = new JMenu("Edit");
        {
            itemUndo = createMenuItem("Undo", "Undos the previous action", KeyEvent.VK_U, Icons.undo, KeyEvent.VK_Z, Keyboard.KEY_Z, InputEvent.CTRL_MASK);
            itemRedo = createMenuItem("Redo", "Redos the previous action", KeyEvent.VK_R, Icons.redo, KeyEvent.VK_Y, Keyboard.KEY_Y, InputEvent.CTRL_MASK);
        }

        menuModel = new JMenu("Model");
        {
            itemTextureManager = createMenuItem("Texture Manager", "Manage the textures entries for the model", KeyEvent.VK_T, Icons.texture, KeyEvent.VK_T, Keyboard.KEY_T, InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK);
            itemDisplayProps = createMenuItem("Display Properties", "Change the display properties of the model", KeyEvent.VK_D, Icons.gallery, KeyEvent.VK_D, Keyboard.KEY_D, InputEvent.CTRL_MASK + InputEvent.ALT_MASK);
            itemOptimise = createMenuItem("Optimize", "Performs basic optimizion by disabling faces that aren't visible", KeyEvent.VK_O, Icons.optimize, KeyEvent.VK_N, Keyboard.KEY_N, InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK);
            menuRotate = new JMenu("Rotate");
            menuRotate.setMnemonic(KeyEvent.VK_R);
            menuRotate.setIcon(Icons.rotate);
            {
                itemRotateClockwise = createMenuItem("90\u00B0 Clockwise", "Rotates all elements clockwise by 90\u00B0", KeyEvent.VK_C, Icons.rotate_clockwise, KeyEvent.VK_RIGHT, Keyboard.KEY_RIGHT, InputEvent.CTRL_MASK);
                itemRotateCounterClockwise = createMenuItem("90\u00B0 Counter Clockwise", "Rotates all elements counter clockwise by 90\u00B0", KeyEvent.VK_C, Icons.rotate_counter_clockwise, KeyEvent.VK_LEFT, Keyboard.KEY_LEFT, InputEvent.CTRL_MASK);
            }
        }
        
        menuBlock = new JMenu("Block");
        {
        	//TODO: update key bindings
        	itemProperties = createMenuItem("Block Properties", "Set the block properties", KeyEvent.VK_P, Icons.edit);
        	itemTransation = createMenuItem("Block Translation", "Set the block translations", KeyEvent.VK_T, Icons.edit);
        	itemCrafting = createMenuItem("Crafting", "Set the crafting recipe", KeyEvent.VK_C, Icons.edit);
        	itemLoot = createMenuItem("Block drops", "Set the block loot", KeyEvent.VK_L, Icons.edit);
        	itemNotes = createMenuItem("Notes", "Take some notes", KeyEvent.VK_N, Icons.new_);
        }

        menuMore = new JMenu("More");
        {
            itemExtractAssets = createMenuItem("Extract Assets...", "Extract Minecraft assets so you can get access to block and item textures", KeyEvent.VK_E, Icons.extract);
            menuExamples = new JMenu("Examples");
            menuExamples.setMnemonic(KeyEvent.VK_E);
            menuExamples.setIcon(Icons.new_);
            {
                itemModelCauldron = createMenuItem("Cauldron", "<html>Model by MrCrayfish<br><b>Private use only</b></html>", KeyEvent.VK_C, Icons.model_cauldron);
                itemModelChair = createMenuItem("Chair", "<html>Model by MrCrayfish<br><b>Private use only</b></html>", KeyEvent.VK_C, Icons.model_chair);
            }
            menuWiki = new JMenu("Wiki");
            menuWiki.setIcon(Icons.mojang);
            {
            	itemHardnessWiki = createMenuItem("Hardness values", "Open wiki for the hardness values of the blocks", KeyEvent.VK_H, Icons.gallery);
            	itemResistanceWiki = createMenuItem("Resistance values", "Open wiki for the resistance values of the blocks", KeyEvent.VK_R, Icons.gallery);
            }
            itemDonate = createMenuItem("Donate (Patreon)", "Pledge to MrCrayfish", KeyEvent.VK_D, Icons.patreon);
            itemGitHub = createMenuItem("Source Code", "View Source Code", KeyEvent.VK_S, Icons.github);
        }

        this.initActions();

        /* Menu File */
        menuShare.add(itemUpload);
        menuShare.add(itemDownload);
        
        menuFile.add(itemNew);
        menuFile.addSeparator();
        menuFile.add(itemLoad);
        menuFile.add(itemSave);
        menuFile.add(itemIntegrate);
        menuFile.add(menuShare);
        menuFile.addSeparator();
        menuFile.add(itemImport);
        menuFile.add(itemExport);
        menuFile.addSeparator();
        menuFile.add(itemSettings);
        menuFile.addSeparator();
        menuFile.add(itemExit);
        this.add(menuFile);

        /* Menu Edit */
        menuEdit.add(itemUndo);
        menuEdit.add(itemRedo);
        menuEdit.addMenuListener(new MenuAdapter()
        {
            @Override
            public void menuSelected(MenuEvent e)
            {
                itemRedo.setEnabled(StateManager.canRestoreNextState());
                itemUndo.setEnabled(StateManager.canRestorePreviousState());
            }
        });
        this.add(menuEdit);

        /* Menu Model Sub Menus */
        menuRotate.add(itemRotateClockwise);
        menuRotate.add(itemRotateCounterClockwise);

        /* Menu Model */
        menuModel.add(itemTextureManager);
        menuModel.add(itemDisplayProps);
        menuModel.add(itemOptimise);
        menuModel.addSeparator();
        menuModel.add(menuRotate);
        this.add(menuModel);
        
        /* Menu Block */
        menuBlock.add(itemProperties);
        menuBlock.add(itemTransation);
        menuBlock.add(itemCrafting);
        menuBlock.add(itemLoot);
        menuBlock.add(itemNotes);
        this.add(menuBlock);

        /* Menu More Sub Menus */
        menuExamples.add(itemModelCauldron);
        menuExamples.add(itemModelChair);
        menuWiki.add(itemHardnessWiki);
        menuWiki.add(itemResistanceWiki);

        /* Menu More */
        menuMore.add(itemExtractAssets);
        menuMore.addSeparator();
        menuMore.add(menuExamples);
        menuMore.addSeparator();
        menuMore.add(menuWiki);
        menuMore.addSeparator();
        menuMore.add(itemGitHub);
        menuMore.add(itemDonate);
        this.add(menuMore);
    }

    private void initActions()
    {
        itemNew.addActionListener(a -> newProject(creator));

        itemLoad.addActionListener(a -> loadProject(creator));

        itemSave.addActionListener(a -> saveProject(creator));
        
        itemIntegrate.addActionListener(a -> IntegrateDialog.show(creator));
        
        itemUpload.addActionListener(a -> {
        	saveProject(creator);
        	Uploader.upload(creator);
        });
        
        itemDownload.addActionListener(a -> Downloader.download(creator));

        itemImport.addActionListener(a -> showImportJson(creator));
        
        itemExport.addActionListener(a -> showExportJson(creator));

        itemSettings.addActionListener(a -> showSettings(creator));

        itemExit.addActionListener(a -> creator.close());

        itemTextureManager.addActionListener(a ->
        {
            TextureManager manager = new TextureManager(creator, creator.getSidebarPanel(), Dialog.ModalityType.APPLICATION_MODAL, false);
            manager.setLocationRelativeTo(null);
            manager.setVisible(true);
        });

        itemDisplayProps.addActionListener(a -> showDisplayProperties(creator));

        itemOptimise.addActionListener(a -> optimizeModel(creator));

        itemRotateClockwise.addActionListener(a ->  {
        	Actions.rotateModel(creator.getSidebarPanel(), true);
        	Actions.rotateModel(creator.getCollisionPanel(), true);
        });

        itemRotateCounterClockwise.addActionListener(a -> {
        	Actions.rotateModel(creator.getSidebarPanel(), false);
        	Actions.rotateModel(creator.getCollisionPanel(), false);
        });
        
        itemProperties.addActionListener(a -> BlockProperties.show(creator));
        
        itemTransation.addActionListener(a -> BlockTranslation.show(creator));
        
        itemCrafting.addActionListener(a -> BlockCrafting.show(creator));
        
        itemLoot.addActionListener(a -> BlockLoot.show(creator));
        
        itemNotes.addActionListener(a -> BlockNotes.show(creator));
        
        itemHardnessWiki.addActionListener(a -> Util.openUrl(Constants.URL_HARDNESS));
        
        itemResistanceWiki.addActionListener(a -> Util.openUrl(Constants.URL_RESISTANCE));

        itemGitHub.addActionListener(a -> Util.openUrl(Constants.URL_GITHUB));

        itemDonate.addActionListener(a -> Util.openUrl(Constants.URL_DONATE));

        itemExtractAssets.addActionListener(a -> showExtractAssets(creator));

        itemModelCauldron.addActionListener(a ->
        {
        	creator.getSidebarPanel().clearElements();
        	creator.getCollisionPanel().clearElements();
        	TextureManager.clear();
            StateManager.clear();
            BlockManager.clear();
            Util.loadModelFromJar(creator.getSidebarManager(), getClass(), "models/cauldron");
            StateManager.pushState(new CombinedState(creator.getSidebarManager()));
        });

        itemModelChair.addActionListener(a ->
        {
        	creator.getSidebarPanel().clearElements();
        	creator.getCollisionPanel().clearElements();
        	TextureManager.clear();
            StateManager.clear();
            BlockManager.clear();
            Util.loadModelFromJar(creator.getSidebarManager(), getClass(), "models/modern_chair");
            StateManager.pushState(new CombinedState(creator.getSidebarManager()));
        });

        itemUndo.addActionListener(a -> StateManager.restorePreviousState());

        itemRedo.addActionListener(a -> StateManager.restoreNextState());
    }

    private JMenuItem createMenuItem(String name, String tooltip, int mnemonic, Icon icon)
    {
        return createMenuItem(name, tooltip, mnemonic, icon, -1, -1, -1);
    }

    private JMenuItem createMenuItem(String name, String tooltip, int mnemonic, Icon icon, int awtCode, int keyCode, int modifiers)
    {
        JMenuItem item = new JMenuItem(name);
        item.setToolTipText(tooltip);
        item.setMnemonic(mnemonic);
        item.setIcon(icon);

        if(awtCode != -1 && keyCode != -1 && modifiers != -1)
        {
            KeyStroke shortcut = KeyStroke.getKeyStroke(awtCode, modifiers);
            if(shortcut != null)
            {
                String shortcutText = KeyboardUtil.convertKeyStokeToString(shortcut);
                item.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
                JLabel label = new JLabel("<html><p style='color:#666666;font-size:9px'>" + shortcutText + "<p></html>");
                item.add(label);
                Dimension size = new Dimension((int) Math.ceil(item.getPreferredSize().getWidth() + label.getPreferredSize().getWidth()) + 10, 20);
                item.setPreferredSize(size);
            }

            if(shortcut != null)
            {
                creator.registerKeyAction(new ModelCreator.KeyAction(awtCode, keyCode, (modifier, pressed) ->
                {
                    if(pressed && modifier == modifiers)
                    {
                        for(ActionListener listener : item.getActionListeners())
                        {
                            listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, item.getActionCommand(), modifier));
                        }
                    }
                }));
            }
        }

        return item;
    }

    public static void newProject(ModelCreator creator)
    {
        int returnVal = JOptionPane.showConfirmDialog(creator, "You current work will be cleared, are you sure?", "Note", JOptionPane.YES_NO_OPTION);
        if(returnVal == JOptionPane.YES_OPTION)
        {
            TextureManager.clear();
            StateManager.clear();
            BlockManager.clear();
            SidebarManager manager = creator.getSidebarManager();
            manager.getModelPanel().reset();
            manager.getCollisionPanel().reset();
            manager.getModelPanel().updateValues();
            manager.getCollisionPanel().updateValues();
            DisplayPropertiesDialog.update(creator);
            StateManager.pushState(new CombinedState(manager.getModelPanel(), manager.getCollisionPanel()));
        }
    }

    public static void loadProject(ModelCreator creator)
    {
    	if(creator.getSidebarPanel().getElementCount() > 0 || creator.getCollisionPanel().getElementCount() > 0) {
            int returnVal = JOptionPane.showConfirmDialog(null, "Your current project will be cleared, are you sure you want to continue?", "Warning", JOptionPane.YES_NO_OPTION);
            if(returnVal == JOptionPane.NO_OPTION)
            	return;
    	}
    	
    	List<String> projectNames = new ArrayList<>();
    	File dir = new File(Settings.getProjectsDir());
    	File[] projects = dir.listFiles(file -> file.getName().endsWith(".block"));
    	for(File project : projects) {
    		if(project.isFile()) {
    			String name = project.getName().split("\\.")[0];
    			projectNames.add(name);
    		}
    	}
    	
    	//Show project selection
    	JDialog dialog = new JDialog(creator, "Load project", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        SpringLayout layout = new SpringLayout();
        JPanel panel = new JPanel(layout);
        panel.setPreferredSize(new Dimension(300, 150));
        dialog.add(panel);

        JLabel labelInfo = new JLabel("<html>Select the project you want to load</html>");
        panel.add(labelInfo);

        JLabel labelProject = new JLabel("Project");
        panel.add(labelProject);

        JComboBox<String> comboBoxProjects = new JComboBox<>();
        comboBoxProjects.setPreferredSize(new Dimension(40, 24));
        projectNames.forEach(comboBoxProjects::addItem);
        panel.add(comboBoxProjects);

        JButton btnLoad = new JButton("Load");
        btnLoad.setIcon(Icons.extract);
        btnLoad.setPreferredSize(new Dimension(80, 24));
        btnLoad.addActionListener(e ->
        {
            TextureManager.clear();
            StateManager.clear();
            BlockManager.clear();
            
            int selection = comboBoxProjects.getSelectedIndex();
            BlockManager.projectName = projectNames.get(selection);
            File project = projects[selection];
            ProjectManager.loadProject(creator.getSidebarManager(), project);
            DisplayPropertiesDialog.update(creator);
            StateManager.pushState(new CombinedState(creator.getSidebarManager()));
            dialog.dispose();
            
        });
        panel.add(btnLoad);

        layout.putConstraint(SpringLayout.NORTH, labelInfo, 10, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.EAST, labelInfo, -10, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.WEST, labelInfo, 10, SpringLayout.WEST, panel);

        layout.putConstraint(SpringLayout.NORTH, labelProject, 2, SpringLayout.NORTH, comboBoxProjects);
        layout.putConstraint(SpringLayout.WEST, labelProject, 10, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, comboBoxProjects, 15, SpringLayout.SOUTH, labelInfo);
        layout.putConstraint(SpringLayout.WEST, comboBoxProjects, 10, SpringLayout.EAST, labelProject);
        layout.putConstraint(SpringLayout.EAST, comboBoxProjects, -10, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.SOUTH, btnLoad, -10, SpringLayout.SOUTH, panel);
        layout.putConstraint(SpringLayout.EAST, btnLoad, -10, SpringLayout.EAST, panel);

        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    public static void saveProject(ModelCreator creator)
    {
    	if(BlockManager.projectName.isEmpty()) {
    		String name = JOptionPane.showInputDialog("Enter project name");
        	if(name == null || name.isEmpty()) {
        		JOptionPane.showMessageDialog(null, "Invalid project name", "Error", JOptionPane.ERROR_MESSAGE);
        		return;
        	}
        	
        	//check for valid filename
        	if(name.matches(".*[/`\\?\\*\\<>|\":\\.\\s].*")) {
        		JOptionPane.showMessageDialog(null, "Invalid project name", "Error", JOptionPane.ERROR_MESSAGE);
        		return;
        	}
        	BlockManager.projectName = name;
    	}
    	
    	File dir = new File(Settings.getProjectsDir());
    	dir.mkdirs();
    	File filePath = new File(dir, BlockManager.projectName + ".block");
    	ProjectManager.saveProject(creator.getSidebarManager(), filePath);
    }

    public static void optimizeModel(ModelCreator creator)
    {
        int result = JOptionPane.showConfirmDialog(null, "<html>Are you sure you want to optimize the model?<br/>It is recommended you save the project before running this<br/>action, otherwise you will have to re-enable the disabled faces.<html>", "Optimize Confirmation", JOptionPane.YES_NO_OPTION);
        if(result == JOptionPane.YES_OPTION)
        {
            int count = Actions.optimiseModel(creator.getSidebarPanel());
            JOptionPane.showMessageDialog(null, "<html>Optimizing the model disabled <b>" + count + "</b> faces</html>", "Optimization Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void showImportJson(ModelCreator creator)
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Import JSON Model");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setApproveButtonText("Import");

        FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON (.json)", "json");
        chooser.setFileFilter(filter);

        String dir = Settings.getJSONDir();
        if(dir != null)
        {
            chooser.setCurrentDirectory(new File(dir));
        }

        int returnVal = chooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION)
        {
            if(creator.getSidebarPanel().getElementCount() > 0 || creator.getCollisionPanel().getElementCount() > 0)
            {
                returnVal = JOptionPane.showConfirmDialog(null, "Your current project will be cleared, are you sure you want to continue?", "Warning", JOptionPane.YES_NO_OPTION);
            }
            if(returnVal != JOptionPane.NO_OPTION && returnVal != JOptionPane.CLOSED_OPTION)
            {
                File location = chooser.getSelectedFile().getParentFile();
                Settings.setJSONDir(location.toString());
                
                BlockManager.clear();
                TextureManager.clear();
                StateManager.clear();
                try
				{
					String modelJson = new String(Files.readAllBytes(chooser.getSelectedFile().toPath()));
					ModelImporter importer = new ModelImporter(creator.getSidebarPanel(), modelJson);
	                importer.importFromJSON();
				} catch (IOException e)
				{
					JOptionPane.showMessageDialog(null, "Could not load model.\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
					return;
				}
                StateManager.pushState(creator.getSidebarPanel());
            }
            creator.getSidebarPanel().updateValues();
        }
    }

    public static void showExportJson(ModelCreator creator)
    {
        JDialog dialog = new JDialog(creator, "Export JSON Model", Dialog.ModalityType.APPLICATION_MODAL);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(500, 225));

        SpringLayout springLayout = new SpringLayout();
        JPanel exportDir = new JPanel(springLayout);

        JLabel labelName = new JLabel("Name");
        labelName.setHorizontalAlignment(SwingConstants.RIGHT);
        exportDir.add(labelName);

        JTextField textFieldName = new JTextField();
        textFieldName.setPreferredSize(new Dimension(100, 24));
        textFieldName.setCaretPosition(0);
        exportDir.add(textFieldName);

        JTextField textFieldDestination = new JTextField();
        textFieldDestination.setPreferredSize(new Dimension(100, 24));

        String exportJsonDir = Settings.getJSONDir();
        if(exportJsonDir != null)
        {
            textFieldDestination.setText(exportJsonDir);
        }
        else
        {
            String userHome = System.getProperty("user.home", ".");
            textFieldDestination.setText(userHome);
        }

        textFieldDestination.setEditable(false);
        textFieldDestination.setFocusable(false);
        textFieldDestination.setCaretPosition(0);
        exportDir.add(textFieldDestination);

        JButton btnBrowserDir = new JButton("Browse");
        btnBrowserDir.setPreferredSize(new Dimension(80, 24));
        btnBrowserDir.setIcon(Icons.load);
        btnBrowserDir.addActionListener(e ->
        {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Export Destination");
            chooser.setCurrentDirectory(new File(textFieldDestination.getText()));
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setApproveButtonText("Select");
            int returnVal = chooser.showOpenDialog(dialog);
            if(returnVal == JFileChooser.APPROVE_OPTION)
            {
                File file = chooser.getSelectedFile();
                if(file != null)
                {
                    textFieldDestination.setText(file.getAbsolutePath());
                }
            }
        });
        exportDir.add(btnBrowserDir);

        JLabel labelExportDir = new JLabel("Destination");
        exportDir.add(labelExportDir);
        
        JLabel labelModid = new JLabel("Mod ID");
        exportDir.add(labelModid);
        
        JTextField textFieldModid = new JTextField();
        textFieldModid.setPreferredSize(new Dimension(100, 24));
        textFieldModid.setCaretPosition(0);
        textFieldModid.setText(Settings.getModID());
        exportDir.add(textFieldModid);

        //JComponent optionSeparator = DefaultComponentFactory.getInstance().createSeparator("Export Options");
        JComponent optionSeparator = new JSeparator();
        exportDir.add(optionSeparator);

        JCheckBox checkBoxOptimize = ComponentUtil.createCheckBox("Optimize Model", "Removes unnecessary faces that can't been seen in the model", true);
        exportDir.add(checkBoxOptimize);

        JCheckBox checkBoxDisplayProps = ComponentUtil.createCheckBox("Include Display Properties", "Adds the display definitions (first-person, third-person, etc) to the model file", false);
        exportDir.add(checkBoxDisplayProps);

        JCheckBox checkBoxElementNames = ComponentUtil.createCheckBox("Include Element Names", "The name of each element will be added to it's entry in the json model elements array. Useful for identifying elements, and when importing back into Model Creator, it will use those names", true);
        exportDir.add(checkBoxElementNames);

        JSeparator separator = new JSeparator();
        exportDir.add(separator);

		/* Constraints */

        springLayout.putConstraint(SpringLayout.NORTH, textFieldName, 10, SpringLayout.NORTH, exportDir);
        springLayout.putConstraint(SpringLayout.WEST, textFieldName, 0, SpringLayout.WEST, textFieldDestination);
        springLayout.putConstraint(SpringLayout.EAST, textFieldName, 0, SpringLayout.EAST, textFieldDestination);
        
        springLayout.putConstraint(SpringLayout.NORTH, labelName, 3, SpringLayout.NORTH, textFieldName);
        springLayout.putConstraint(SpringLayout.WEST, labelName, 10, SpringLayout.WEST, exportDir);
        springLayout.putConstraint(SpringLayout.EAST, labelName, -5, SpringLayout.WEST, textFieldDestination);
        springLayout.putConstraint(SpringLayout.WEST, optionSeparator, 10, SpringLayout.WEST, exportDir);
        springLayout.putConstraint(SpringLayout.EAST, optionSeparator, -10, SpringLayout.EAST, exportDir);
        springLayout.putConstraint(SpringLayout.NORTH, optionSeparator, 10, SpringLayout.SOUTH, textFieldModid);
        springLayout.putConstraint(SpringLayout.NORTH, btnBrowserDir, 0, SpringLayout.NORTH, textFieldDestination);
        springLayout.putConstraint(SpringLayout.EAST, btnBrowserDir, -10, SpringLayout.EAST, exportDir);
        springLayout.putConstraint(SpringLayout.NORTH, textFieldDestination, 10, SpringLayout.SOUTH, textFieldName);
        springLayout.putConstraint(SpringLayout.WEST, textFieldDestination, 5, SpringLayout.EAST, labelExportDir);
        springLayout.putConstraint(SpringLayout.EAST, textFieldDestination, -10, SpringLayout.WEST, btnBrowserDir);
        springLayout.putConstraint(SpringLayout.NORTH, labelExportDir, 3, SpringLayout.NORTH, textFieldDestination);
        springLayout.putConstraint(SpringLayout.WEST, labelExportDir, 10, SpringLayout.WEST, exportDir);
       
        springLayout.putConstraint(SpringLayout.NORTH, labelModid, 10, SpringLayout.SOUTH, labelExportDir);
        springLayout.putConstraint(SpringLayout.WEST, labelModid, 10, SpringLayout.WEST, exportDir);
        springLayout.putConstraint(SpringLayout.NORTH, textFieldModid, 10, SpringLayout.SOUTH, labelExportDir);
        springLayout.putConstraint(SpringLayout.WEST, textFieldModid, 10, SpringLayout.EAST, labelModid);
        springLayout.putConstraint(SpringLayout.EAST, textFieldModid, -10, SpringLayout.EAST, exportDir);
        springLayout.putConstraint(SpringLayout.NORTH, checkBoxOptimize, 5, SpringLayout.SOUTH, optionSeparator);
        springLayout.putConstraint(SpringLayout.WEST, checkBoxOptimize, 10, SpringLayout.WEST, exportDir);
        springLayout.putConstraint(SpringLayout.NORTH, checkBoxDisplayProps, 0, SpringLayout.SOUTH, checkBoxOptimize);
        springLayout.putConstraint(SpringLayout.WEST, checkBoxDisplayProps, 10, SpringLayout.WEST, exportDir);
        springLayout.putConstraint(SpringLayout.NORTH, checkBoxElementNames, 0, SpringLayout.SOUTH, checkBoxDisplayProps);
        springLayout.putConstraint(SpringLayout.WEST, checkBoxElementNames, 10, SpringLayout.WEST, exportDir);
        springLayout.putConstraint(SpringLayout.WEST, separator, 10, SpringLayout.WEST, exportDir);
        springLayout.putConstraint(SpringLayout.EAST, separator, -10, SpringLayout.EAST, exportDir);
        springLayout.putConstraint(SpringLayout.NORTH, separator, 5, SpringLayout.SOUTH, checkBoxElementNames);
        springLayout.putConstraint(SpringLayout.SOUTH, separator, 5, SpringLayout.SOUTH, exportDir);

        panel.setPreferredSize(panel.getPreferredSize());
        panel.add(exportDir, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new AbstractAction()
        {
			private static final long serialVersionUID = -23993140236291660L;

			@Override
            public void actionPerformed(ActionEvent e)
            {
                dialog.dispose();
            }
        });
        buttons.add(btnCancel);

        JButton btnExport = new JButton("Export");
        btnExport.addActionListener(e ->
        {
            String name = textFieldName.getText().trim();
            String modid = textFieldModid.getText().trim();
            if(!textFieldDestination.getText().isEmpty() && !name.isEmpty() && !modid.isEmpty())
            {
                File destination = new File(textFieldDestination.getText());
                destination.mkdirs();

                File modelFile = new File(destination, textFieldName.getText() + ".json");
                if(modelFile.exists())
                {
                    int returnVal = JOptionPane.showConfirmDialog(dialog, "A file for that name already exists in the directory. Are you sure you want to override it?", "Warning", JOptionPane.YES_NO_OPTION);
                    if(returnVal != JOptionPane.YES_OPTION)
                    {
                        return;
                    }
                }

                try
                {
                    modelFile.createNewFile();
                }
                catch(IOException e1)
                {
                    JOptionPane.showMessageDialog(dialog, "Unable to create the file. Check that your destination folder is writable", "Error", JOptionPane.ERROR_MESSAGE);
                    Util.writeCrashLog(e1);
                }

                dialog.dispose();

                ExporterModel exporter = new ExporterModel(creator.getSidebarPanel(), modid);
                exporter.setOptimize(checkBoxOptimize.isSelected());
                exporter.setDisplayProps(checkBoxDisplayProps.isSelected());
                exporter.setIncludeNames(checkBoxElementNames.isSelected());
                if(exporter.writeFile(modelFile) == null)
                {
                    modelFile.delete();
                    JOptionPane.showMessageDialog(dialog, "An error occured while exporting the model. Please try again", "Error", JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    Settings.setJSONDir(textFieldDestination.getText());
                    int returnVal = JOptionPane.showOptionDialog(dialog, "Model exported successfully!", "Success", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{"Open Folder", "Close"}, "Close");
                    if(returnVal == 0)
                    {
                        Desktop desktop = Desktop.getDesktop();
                        try
                        {
                            desktop.open(destination);
                        }
                        catch(IOException e1)
                        {
                            Util.writeCrashLog(e1);
                        }
                    }
                }
            }
        });
        buttons.add(btnExport);

        panel.add(buttons, BorderLayout.SOUTH);

        dialog.add(panel);

        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    public static void showSettings(ModelCreator creator)
    {
        JDialog dialog = new JDialog(creator, "Settings", Dialog.ModalityType.APPLICATION_MODAL);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(500, 300));
        dialog.add(panel);

        JTabbedPane tabbedPane = new JTabbedPane();
        panel.add(tabbedPane, BorderLayout.CENTER);

        SpringLayout generalSpringLayout = new SpringLayout();
        JPanel generalPanel = new JPanel(generalSpringLayout);
        tabbedPane.addTab("General", generalPanel);

        JPanel optionsPanel = new JPanel(new GridLayout(1, 2));
        generalPanel.add(optionsPanel);

        JPanel undoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        optionsPanel.add(undoPanel);

        JLabel labelUndoLimit = new JLabel("Undo / Redo Limit");
        undoPanel.add(labelUndoLimit);

        final Boolean[] changed = {false};
        SpinnerNumberModel undoSpinnerNumberModel = new SpinnerNumberModel();
        undoSpinnerNumberModel.setMinimum(1);
        JSpinner undoLimitSpinner = new JSpinner(undoSpinnerNumberModel);
        undoLimitSpinner.setPreferredSize(new Dimension(40, 24));
        undoLimitSpinner.setValue(Settings.getUndoLimit());
        undoLimitSpinner.addChangeListener(e ->
        {
            if(!changed[0])
            {
                JOptionPane.showMessageDialog(dialog, "Increasing the undo/redo limit will increase the amount of memory the program use. Change this setting with caution.", "Warning", JOptionPane.WARNING_MESSAGE);
                changed[0] = true;
            }
        });
        undoPanel.add(undoLimitSpinner);

        JCheckBox checkBoxCardinalPoints = ComponentUtil.createCheckBox("Show Cardinal Points", "", Settings.getCardinalPoints());
        optionsPanel.add(checkBoxCardinalPoints);
        
        //used Mc version
        JPanel mcVersionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        optionsPanel.add(mcVersionPanel);
        JLabel versionLabel = new JLabel("MC");
        mcVersionPanel.add(versionLabel);
        JComboBox<String> comboBoxVersions = new JComboBox<>();
        comboBoxVersions.setPreferredSize(new Dimension(100, 24));
    	Settings.getExtractedAssets().forEach(comboBoxVersions::addItem);
    	comboBoxVersions.addActionListener(a -> {
    		String newVersion = (String)comboBoxVersions.getSelectedItem();
    		Settings.setUsedMcVersion(newVersion);
    	});
    	if(Settings.getUsedMcVersion() != null) {
    		String selectedVersion = Settings.getUsedMcVersion();
    		if(!selectedVersion.isEmpty())
    			comboBoxVersions.setSelectedItem(selectedVersion);
    	}
    	mcVersionPanel.add(comboBoxVersions);

        JSeparator separator = new JSeparator();
        generalPanel.add(separator);

        String imageEditorPath = Settings.getImageEditor() != null ? Settings.getImageEditor() : "";
        JPanel imageEditorPanel = ComponentUtil.createFileSelector("Image Editor", dialog, imageEditorPath, null, null);
        generalPanel.add(imageEditorPanel);

        JLabel labelArguments = new JLabel("Arguments");
        generalPanel.add(labelArguments);

        String imageEditorArgs = Settings.getImageEditorArgs() != null ? Settings.getImageEditorArgs() : "\"%s\"";
        JTextField textFieldArguments = new JTextField(imageEditorArgs);
        textFieldArguments.setPreferredSize(new Dimension(0, 24));
        generalPanel.add(textFieldArguments);
        
        JSeparator separator2 = new JSeparator();
        generalPanel.add(separator2);
        
        //text editor
        JPanel textEditorPanel = ComponentUtil.createFileSelector("Text Editor", dialog, Settings.getTextEditorPath(), new FileNameExtensionFilter("Executable", "exe"), null);
        generalPanel.add(textEditorPanel);

        generalSpringLayout.putConstraint(SpringLayout.WEST, optionsPanel, 5, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, optionsPanel, 5, SpringLayout.NORTH, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, optionsPanel, 5, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.WEST, separator, 0, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, separator, 0, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, separator, 5, SpringLayout.SOUTH, optionsPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, imageEditorPanel, -10, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.WEST, imageEditorPanel, 10, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, imageEditorPanel, 10, SpringLayout.SOUTH, separator);
        generalSpringLayout.putConstraint(SpringLayout.WEST, labelArguments, 10, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, labelArguments, 2, SpringLayout.NORTH, textFieldArguments);
        generalSpringLayout.putConstraint(SpringLayout.EAST, textFieldArguments, -10, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.WEST, textFieldArguments, 20, SpringLayout.EAST, labelArguments);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, textFieldArguments, 10, SpringLayout.SOUTH, imageEditorPanel);
        generalSpringLayout.putConstraint(SpringLayout.WEST, separator2, 0, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.EAST, separator2, 0, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, separator2, 5, SpringLayout.SOUTH, textFieldArguments);
        generalSpringLayout.putConstraint(SpringLayout.EAST, textEditorPanel, -10, SpringLayout.EAST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.WEST, textEditorPanel, 10, SpringLayout.WEST, generalPanel);
        generalSpringLayout.putConstraint(SpringLayout.NORTH, textEditorPanel, 10, SpringLayout.SOUTH, separator2);

        JPanel colorGrid = new JPanel();
        colorGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane colorScrollPane = new JScrollPane(colorGrid);
        tabbedPane.addTab("Appearance", colorScrollPane);

        colorGrid.add(createColorSelector(dialog, "North Face", Face.getFaceColour(Face.NORTH), createFaceColorProcessor(Face.NORTH)));
        colorGrid.add(createColorSelector(dialog, "East Face", Face.getFaceColour(Face.EAST), createFaceColorProcessor(Face.EAST)));
        colorGrid.add(createColorSelector(dialog, "South Face", Face.getFaceColour(Face.SOUTH), createFaceColorProcessor(Face.SOUTH)));
        colorGrid.add(createColorSelector(dialog, "West Face", Face.getFaceColour(Face.WEST), createFaceColorProcessor(Face.WEST)));
        colorGrid.add(createColorSelector(dialog, "Up Face", Face.getFaceColour(Face.UP), createFaceColorProcessor(Face.UP)));
        colorGrid.add(createColorSelector(dialog, "Down Face", Face.getFaceColour(Face.DOWN), createFaceColorProcessor(Face.DOWN)));

        JButton btnReset = new JButton("Reset Colors");
        btnReset.addActionListener(a ->
        {
            Face.setFaceColors(Settings.DEFAULT_FACE_COLORS);
            dialog.dispose();
            JOptionPane.showMessageDialog(creator, "Colors reset");
        });
        colorGrid.add(btnReset);

        colorGrid.setLayout(new GridLayout(colorGrid.getComponentCount(), 1, 20, 10));

        dialog.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosed(WindowEvent e)
            {
                Settings.setUndoLimit((int) undoLimitSpinner.getValue());
                Settings.setFaceColors(Face.getFaceColors());
                Settings.setImageEditor(getDirectoryFromSelector(imageEditorPanel));
                Settings.setImageEditorArgs(textFieldArguments.getText());
                Settings.setCardinalPoints(checkBoxCardinalPoints.isSelected());
                Settings.setTextEditor(getDirectoryFromSelector(textEditorPanel));
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

    public static String getDirectoryFromSelector(JPanel panel)
    {
        for(Component component : panel.getComponents())
        {
            if(component instanceof JTextField)
            {
                return ((JTextField) component).getText();
            }
        }
        return "";
    }

    public static void showExtractAssets(ModelCreator creator)
    {
        JDialog dialog = new JDialog(creator, "Extract Assets", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        SpringLayout layout = new SpringLayout();
        JPanel panel = new JPanel(layout);
        panel.setPreferredSize(new Dimension(300, 150));
        dialog.add(panel);

        JLabel labelInfo = new JLabel("<html>This tool allows you to extract Minecraft's assets. The versions listed below are the ones you have downloaded with the Java edition of the game.</html>");
        panel.add(labelInfo);

        JLabel labelMinecraftAssets = new JLabel("Minecraft Version");
        panel.add(labelMinecraftAssets);

        JComboBox<String> comboBoxMinecraftVersions = new JComboBox<>();
        comboBoxMinecraftVersions.setPreferredSize(new Dimension(40, 24));
        Util.getMinecraftVersions().forEach(comboBoxMinecraftVersions::addItem);
        panel.add(comboBoxMinecraftVersions);

        JButton btnExtract = new JButton("Extract");
        btnExtract.setIcon(Icons.extract);
        btnExtract.setPreferredSize(new Dimension(80, 24));
        btnExtract.addActionListener(e ->
        {
            Util.extractMinecraftAssets((String) comboBoxMinecraftVersions.getSelectedItem(), dialog);
            dialog.dispose();
        });
        panel.add(btnExtract);

        layout.putConstraint(SpringLayout.NORTH, labelInfo, 10, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.EAST, labelInfo, -10, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.WEST, labelInfo, 10, SpringLayout.WEST, panel);

        layout.putConstraint(SpringLayout.NORTH, labelMinecraftAssets, 2, SpringLayout.NORTH, comboBoxMinecraftVersions);
        layout.putConstraint(SpringLayout.WEST, labelMinecraftAssets, 10, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.NORTH, comboBoxMinecraftVersions, 15, SpringLayout.SOUTH, labelInfo);
        layout.putConstraint(SpringLayout.WEST, comboBoxMinecraftVersions, 10, SpringLayout.EAST, labelMinecraftAssets);
        layout.putConstraint(SpringLayout.EAST, comboBoxMinecraftVersions, -10, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.SOUTH, btnExtract, -10, SpringLayout.SOUTH, panel);
        layout.putConstraint(SpringLayout.EAST, btnExtract, -10, SpringLayout.EAST, panel);

        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    public static JPanel createColorSelector(Window parent, String labelText, int startColor, Processor<Integer> processor)
    {
        SpringLayout layout = new SpringLayout();
        JPanel panel = new JPanel(layout);
        panel.setPreferredSize(new Dimension(200, 30));
        panel.setBackground(new Color(0, 0, 0, 0));

        JLabel label = new JLabel(labelText);
        panel.add(label);

        JPanel colorPanel = new JPanel();
        colorPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        colorPanel.setBackground(new Color(startColor));
        colorPanel.setPreferredSize(new Dimension(24, 24));
        panel.add(colorPanel);

        JButton button = new JButton("Change");
        button.setPreferredSize(new Dimension(80, 24));
        button.addActionListener(e ->
        {
            int color = selectColor(parent, startColor);
            if(processor.run(color))
            {
                colorPanel.setBackground(new Color(color));
            }
        });
        panel.add(button);

        layout.putConstraint(SpringLayout.WEST, label, 0, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, label, 0, SpringLayout.VERTICAL_CENTER, panel);
        layout.putConstraint(SpringLayout.EAST, label, 5, SpringLayout.WEST, colorPanel);
        layout.putConstraint(SpringLayout.WEST, colorPanel, 80, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, colorPanel, 0, SpringLayout.VERTICAL_CENTER, panel);
        layout.putConstraint(SpringLayout.EAST, colorPanel, -10, SpringLayout.WEST, button);
        layout.putConstraint(SpringLayout.EAST, button, 0, SpringLayout.EAST, panel);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, button, 0, SpringLayout.VERTICAL_CENTER, panel);
        return panel;
    }

    private static int selectColor(Window parent, int startColor)
    {
        JDialog dialog = new JDialog(parent, "Select a Color", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        dialog.add(panel);

        JColorChooser colorChooser = new JColorChooser();
        colorChooser.setColor(startColor);
        panel.add(colorChooser, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(buttons, BorderLayout.SOUTH);

        JButton btnExtract = new JButton("Select");
        btnExtract.setIcon(Icons.extract);
        btnExtract.setPreferredSize(new Dimension(80, 24));
        btnExtract.addActionListener(e -> dialog.dispose());
        buttons.add(btnExtract);

        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

        return colorChooser.getColor().getRGB();
    }

    private static Processor<Integer> createFaceColorProcessor(int side)
    {
        return integer ->
        {
            if(Face.getFaceColour(side) != integer)
            {
                Face.setFaceColor(side, integer);
                return true;
            }
            return false;
        };
    }

    private static void showDisplayProperties(ModelCreator creator)
    {
        DisplayPropertiesDialog dialog = new DisplayPropertiesDialog(creator);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosed(WindowEvent e)
            {
                Menu.displayPropertiesDialog = null;
                ModelCreator.restoreStandardRenderer();
            }
        });
        dialog.setLocationRelativeTo(null);
        dialog.setLocation(dialog.getLocation().x - 500, dialog.getLocation().y);
        dialog.setVisible(true);
        dialog.requestFocus();

        Menu.displayPropertiesDialog = dialog;
        ModelCreator.setCanvasRenderer(DisplayProperties.RENDER_MAP.get("gui"));
    }
}
