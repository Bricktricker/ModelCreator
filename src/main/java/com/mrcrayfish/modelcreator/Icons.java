package com.mrcrayfish.modelcreator;

import javax.swing.*;

public class Icons
{
    public static Icon bin;
    public static Icon new_;
    public static Icon import_;
    public static Icon export;
    public static Icon texture;
    public static Icon clear_texture;
    public static Icon copy;
    public static Icon copy_small;
    public static Icon clipboard;
    public static Icon clipboard_texture;
    public static Icon load;
    public static Icon disk;
    public static Icon exit;
    public static Icon settings;
    public static Icon cube;
    public static Icon light_off;
    public static Icon light_on;
    public static Icon arrow_up;
    public static Icon arrow_down;
    public static Icon patreon;
    public static Icon github;
    public static Icon model_cauldron;
    public static Icon model_chair;
    public static Icon extract;
    public static Icon mojang;
    public static Icon undo;
    public static Icon redo;
    public static Icon optimize;
    public static Icon rotate;
    public static Icon rotate_clockwise;
    public static Icon rotate_counter_clockwise;
    public static Icon refresh;
    public static Icon gallery;
    public static Icon bin2;
    public static Icon edit;
    public static Icon edit_image;

    public static void init(Class<?> clazz)
    {
        ClassLoader loader = clazz.getClassLoader();
        cube = new ImageIcon(loader.getResource("icons/cube.png"));
        bin = new ImageIcon(loader.getResource("icons/bin.png"));
        new_ = new ImageIcon(loader.getResource("icons/new.png"));
        import_ = new ImageIcon(loader.getResource("icons/import.png"));
        export = new ImageIcon(loader.getResource("icons/export.png"));
        texture = new ImageIcon(loader.getResource("icons/texture.png"));
        clear_texture = new ImageIcon(loader.getResource("icons/clear_texture.png"));
        copy = new ImageIcon(loader.getResource("icons/copy.png"));
        copy_small = new ImageIcon(loader.getResource("icons/copy_small.png"));
        clipboard = new ImageIcon(loader.getResource("icons/clipboard.png"));
        clipboard_texture = new ImageIcon(loader.getResource("icons/paste_texture.png"));
        load = new ImageIcon(loader.getResource("icons/load.png"));
        disk = new ImageIcon(loader.getResource("icons/disk.png"));
        exit = new ImageIcon(loader.getResource("icons/exit.png"));
        settings = new ImageIcon(loader.getResource("icons/settings.png"));
        extract = new ImageIcon(loader.getResource("icons/extract.png"));
        light_off = new ImageIcon(loader.getResource("icons/box_off.png"));
        light_on = new ImageIcon(loader.getResource("icons/box_on.png"));
        arrow_up = new ImageIcon(loader.getResource("icons/arrow_up.png"));
        arrow_down = new ImageIcon(loader.getResource("icons/arrow_down.png"));
        patreon = new ImageIcon(loader.getResource("icons/patreon.png"));
        github = new ImageIcon(loader.getResource("icons/github.png"));
        model_cauldron = new ImageIcon(loader.getResource("icons/model_cauldron.png"));
        model_chair = new ImageIcon(loader.getResource("icons/model_chair.png"));
        mojang = new ImageIcon(loader.getResource("icons/mojang.png"));
        undo = new ImageIcon(loader.getResource("icons/undo.png"));
        redo = new ImageIcon(loader.getResource("icons/redo.png"));
        optimize = new ImageIcon(loader.getResource("icons/optimize.png"));
        rotate = new ImageIcon(loader.getResource("icons/rotate.png"));
        rotate_clockwise = new ImageIcon(loader.getResource("icons/rotate_clockwise.png"));
        rotate_counter_clockwise = new ImageIcon(loader.getResource("icons/rotate_anticlockwise.png"));
        refresh = new ImageIcon(loader.getResource("icons/refresh.png"));
        gallery = new ImageIcon(loader.getResource("icons/gallery.png"));
        bin2 = new ImageIcon(loader.getResource("icons/bin2.png"));
        edit = new ImageIcon(loader.getResource("icons/edit.png"));
        edit_image = new ImageIcon(loader.getResource("icons/edit_image.png"));
    }
}
