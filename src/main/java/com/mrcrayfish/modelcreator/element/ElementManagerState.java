package com.mrcrayfish.modelcreator.element;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public interface ElementManagerState
{
    public List<Element> getElements();
    
    public void restore();

}
