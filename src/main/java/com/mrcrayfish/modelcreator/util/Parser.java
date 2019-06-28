package com.mrcrayfish.modelcreator.util;

import com.mrcrayfish.modelcreator.ExporterModel;

import java.text.ParseException;

public class Parser
{
    public static double parseDouble(String text, double def)
    {
        try
        {
            return ExporterModel.FORMAT.parse(text).doubleValue();
        }
        catch(NumberFormatException | ParseException e)
        {
            e.printStackTrace();
        }
        return def;
    }

    public static int parseInt(String text, int def)
    {
        try
        {
            return Integer.parseInt(text);
        }
        catch(NumberFormatException e)
        {
            e.printStackTrace();
        }
        return def;
    }
}
