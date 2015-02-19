package org.mxeclipse.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import matrix.util.MatrixException;

import org.mxeclipse.exception.MxEclipseException;
import org.mxeclipse.utils.MxEclipseLogger;

public class MxTreeWebSetting extends MxTreeBusiness
{
  protected String value;
  protected String[] range;
  protected String defaultValue;
  protected MxTreeWeb parentWeb;
  protected static Map<String, ArrayList<MxTreeWebSetting>> allSettings;

  private MxTreeWebSetting(String name, String[] range, String defaultValue, MxTreeWeb parentWeb)
    throws MxEclipseException, MatrixException
  {
    super("WebSetting", name);
    this.range = range;
    this.defaultValue = defaultValue;
    this.parentWeb = parentWeb;
  }

  public MxTreeWebSetting(String name, MxTreeWeb parentWeb) throws MxEclipseException, MatrixException {
    super("WebSetting", name);
    this.parentWeb = parentWeb;
  }

  public void setName(String name)
  {
      super.setName(name);
      range = null;
      defaultValue = null;
      if(parentWeb != null)
      {
          ArrayList alTypeSetting = getAllSettings(parentWeb);
          for(Iterator iterator = alTypeSetting.iterator(); iterator.hasNext();)
          {
              MxTreeWebSetting set = (MxTreeWebSetting)iterator.next();
              if(set.getName().equals(name))
              {
                  range = set.getRange();
                  defaultValue = set.getDefaultValue();
              }
          }

      }
  }

  public String getValue()
  {
    return this.value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String[] getRange() {
    return this.range;
  }

  public String getDefaultValue() {
    return this.defaultValue;
  }

  public static ArrayList<MxTreeWebSetting> getAllSettings(MxTreeWeb parent) {
    if (allSettings == null) {
      allSettings = new HashMap();
    }
    ArrayList typeSettings = (ArrayList)allSettings.get(parent.getType());
    if (typeSettings == null) {
      try {
        InputStream ins = MxTreeWebSetting.class.getResourceAsStream("Settings" + parent.getType() + ".txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(ins, "utf-8"));

        int index = 0;
        String setName = null;
        String[] setRange = (String[])null;
        String setDefault = null;
        typeSettings = new ArrayList();
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if(index == 0)
            {
                setName = line;
                index++;
            } else
            if(index == 1)
            {
                if(!line.equals(""))
                {
                    setRange = line.split("\\|");
                    for(int i = 0; i < setRange.length; i++)
                        setRange[i] = setRange[i].trim();

                } else
                {
                    setRange = (String[])null;
                }
                index++;
            } else
            if(index == 2)
            {
                setDefault = line;
                MxTreeWebSetting newSetting = new MxTreeWebSetting(setName, setRange, setDefault, parent);
                typeSettings.add(newSetting);
                index = 0;
            }
        }
        Collections.sort(typeSettings);
        allSettings.put(parent.getType(), typeSettings);

        reader.close();
        ins.close();
      } catch (Exception e) {
        MxEclipseLogger.getLogger().severe("Unable to retrieve all web settings for type " + parent.getType() + ": " + e.getMessage());
      }
    }
    return typeSettings;
  }

  public static String[] getAllSettingNames(MxTreeWeb parent) {
    ArrayList parentSettings = getAllSettings(parent);
    String[] retNames = new String[parentSettings.size()];
    for (int i = 0; i < parentSettings.size(); i++) {
      retNames[i] = ((MxTreeWebSetting)parentSettings.get(i)).getName();
    }
    return retNames;
  }

  public static MxTreeWebSetting createInstance(MxTreeWeb parent, String settingName) throws MxEclipseException, MatrixException {
    ArrayList alTypeSetting = getAllSettings(parent);
    for(Iterator iterator = alTypeSetting.iterator(); iterator.hasNext();)
    {
        MxTreeWebSetting set = (MxTreeWebSetting)iterator.next();
        if(set.getName().equals(settingName))
            return new MxTreeWebSetting(settingName, set.getRange(), set.getDefaultValue(), parent);
    }
    return new MxTreeWebSetting(settingName, null, null, parent);
  }

  public static void clearCache()
  {
    allSettings = null;
  }
}