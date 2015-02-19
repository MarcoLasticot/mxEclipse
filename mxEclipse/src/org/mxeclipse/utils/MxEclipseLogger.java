package org.mxeclipse.utils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MxEclipseLogger extends Logger
{
  protected MxEclipseLogger(String name)
  {
    super(name, null);
  }

  public static Logger getLogger() {
    Logger logger = getLogger("MxEclipse");
    try {
      FileHandler handler = new FileHandler("%t/MxEclipse.log", 
        true);
      handler.setLevel(Level.FINEST);
      handler.setFormatter(new SimpleFormatter());
      logger.addHandler(handler);
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return logger;
  }
}