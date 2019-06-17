package com.mrcrayfish.modelcreator.display;

import com.mrcrayfish.modelcreator.Camera;
import com.mrcrayfish.modelcreator.ModelCreator;
import com.mrcrayfish.modelcreator.SidebarManager;
import com.mrcrayfish.modelcreator.element.Element;
import com.mrcrayfish.modelcreator.element.ElementManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public abstract class CanvasRenderer
{
    protected List<Element> elements = new ArrayList<>();

    public void onInit(Camera camera) {}

    public void onRenderPerspective(ModelCreator creator, SidebarManager manager, Camera camera) {}

    public void onRenderOverlay(ElementManager manager, Camera camera, ModelCreator creator) {}
}
