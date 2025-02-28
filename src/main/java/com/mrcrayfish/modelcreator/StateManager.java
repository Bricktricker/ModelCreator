package com.mrcrayfish.modelcreator;

import com.mrcrayfish.modelcreator.element.ElementManager;
import com.mrcrayfish.modelcreator.element.ElementManagerState;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.Stack;

/**
 * Author: MrCrayfish
 */
public class StateManager
{
    /* Undo/Redo Stack */
    private static Stack<ElementManagerState> states = new Stack<>();
    private static int tailIndex = -1;
    private static PropertyIdentifier lastId = PropertyIdentifier.UNDEFINED;
    private static Timer timer;

    public static void pushState(ElementManager manager)
    {
        pushState(manager.createState());
    }

    public static void pushState(ElementManagerState state)
    {
        if(timer != null && timer.isRunning())
        {
            for(ActionListener listener : timer.getActionListeners())
            {
                listener.actionPerformed(null);
            }
            timer.stop();
            timer = null;
        }
        pushManagerState(state);
    }

    private static void pushManagerState(ElementManagerState state)
    {
        while(tailIndex < states.size() - 1)
        {
            states.pop();
        }

        if(states.size() >= 50) //Make configurable
        {
            while(states.size() > 50)
            {
                states.remove(0);
            }
        }
        states.push(state);
        tailIndex = states.size() - 1;
    }

    public static void restorePreviousState()
    {
        if(canRestorePreviousState())
        {
            ElementManagerState state = states.get(tailIndex - 1);
            state.restore();
            tailIndex--;
        }
    }

    public static void restoreNextState()
    {
        if(canRestoreNextState())
        {
            ElementManagerState state = states.get(tailIndex + 1);
            state.restore();
            tailIndex++;
        }
    }

    public static boolean canRestorePreviousState()
    {
        return tailIndex > 0;
    }

    public static boolean canRestoreNextState()
    {
        return tailIndex < states.size() - 1;
    }

    public static void clear()
    {
        if(timer != null && timer.isRunning())
        {
            timer.stop();
            timer = null;
        }
        states.clear();
        tailIndex = -1;
    }

    public static Stack<ElementManagerState> getStates()
    {
        return states;
    }

    public static int getTailIndex()
    {
        return tailIndex;
    }

    public static void pushStateDelayed(ElementManager manager, PropertyIdentifier id)
    {
        if(lastId != id)
        {
            if(timer != null && timer.isRunning())
            {
                for(ActionListener listener : timer.getActionListeners())
                {
                    listener.actionPerformed(null);
                }
                timer.stop();
            }
        }
        else
        {
            if(timer != null && timer.isRunning())
            {
                timer.stop();
            }
        }

        ElementManagerState state = manager.createState();
        ActionListener listener = e -> pushManagerState(state);
        timer = new Timer(400, listener);
        timer.setRepeats(false);
        timer.start();
        lastId = id;
    }
}
