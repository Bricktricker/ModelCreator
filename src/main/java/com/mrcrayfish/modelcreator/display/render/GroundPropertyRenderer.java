package com.mrcrayfish.modelcreator.display.render;

import com.mrcrayfish.modelcreator.Animation;
import com.mrcrayfish.modelcreator.Camera;
import com.mrcrayfish.modelcreator.ModelCreator;
import com.mrcrayfish.modelcreator.SidebarManager;
import com.mrcrayfish.modelcreator.block.BlockManager;
import com.mrcrayfish.modelcreator.display.DisplayProperties;
import com.mrcrayfish.modelcreator.element.Element;

import static org.lwjgl.opengl.GL11.*;

/**
 * Author: MrCrayfish
 */
public class GroundPropertyRenderer extends DisplayPropertyRenderer
{
    public GroundPropertyRenderer()
    {
        this.addElements();
    }

    private void addElements()
    {
        Element block = new Element(16, 16, 16);
        block.setStartX(-8);
        block.setStartY(-16);
        block.setStartZ(-8);
        elements.add(block);
    }

    @Override
    public void onInit(Camera camera)
    {
        camera.setX(0);
        camera.setY(0);
        camera.setZ(-25);
        camera.setRX(20);
        camera.setRY(0);
        camera.setRZ(0);
    }

    @Override
    public void onRenderPerspective(ModelCreator creator, SidebarManager manager, Camera camera)
    {
        DisplayProperties.Entry entry = BlockManager.displayProperties.getEntry("ground");
        if(entry != null)
        {
            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();
            glEnable(GL_DEPTH_TEST);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glLoadIdentity();
            camera.useView();

            for(Element element : elements)
            {
                element.drawExtras(manager.getModelPanel());
                element.draw();
            }

            double yOffset = (Animation.getCounter() + Animation.getCounter()) / 60F;
            yOffset = Math.sin(yOffset);
            glTranslated(0, (yOffset * 1.5) * entry.getScaleY(), 0);
            glRotated((Animation.getCounter() + Animation.getPartialTicks()), 0, 1, 0);

            glTranslated(-entry.getTranslationX(), entry.getTranslationY(), -entry.getTranslationZ());
            glScaled(entry.getScaleX(), entry.getScaleY(), entry.getScaleZ());

            glTranslated(0, 5.5, 0);

            glRotatef(180F, 0, 1, 0);
            glRotatef((float) entry.getRotationX(), 1, 0, 0);
            glRotatef((float) entry.getRotationY(), 0, 1, 0);
            glRotatef((float) entry.getRotationZ(), 0, 0, 1);
            glTranslated(0, -8, 0);

            glPushMatrix();
            {
                this.drawGrid(camera, false);                
                this.drawElements(manager.getModelPanel());
            }
            glPopMatrix();

            glDisable(GL_DEPTH_TEST);
            glDisable(GL_CULL_FACE);
            glDisable(GL_TEXTURE_2D);
            glDisable(GL_LIGHTING);
        }
    }
}
