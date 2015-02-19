package org.mxeclipse.utils;

import java.util.logging.Level;
import java.util.logging.Logger;
import matrix.db.ClientTask;
import matrix.db.ClientTaskItr;
import matrix.db.ClientTaskList;
import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.util.MatrixException;

public class MxEclispeMqlUtils
{
  private static MQLCommand mqlcommand = new MQLCommand();
  private static Logger logger = MxEclipseLogger.getLogger();

  public static String mqlCommand(Context context, String query) throws MatrixException
  {
    String strResult = null;
    try {
      if (mqlcommand == null) {
        mqlcommand = new MQLCommand();
      }
      boolean executeCommand = mqlcommand.executeCommand(context, query);
      context.updateClientTasks();
      ClientTaskList tasksList = context.getClientTasks();
      logger.logp(Level.FINE, "MxEclispeMqlUtils", "mqlCommand", tasksList.toString(), tasksList.toString());
      ClientTaskItr itr = new ClientTaskItr(tasksList);
      while (itr.next()) {
        ClientTask task = itr.obj();
        logger.logp(Level.FINE, "MxEclispeMqlUtils", "mqlCommand", task.getTaskData(), task.toString());
      }

      if (!executeCommand) {
        String strError = mqlcommand.getError();
        int j = strError.length();
        if (j > 0) {
          j--;
          strError = strError.substring(0, j);
        }
        throw new MatrixException(strError);
      }
      strResult = mqlcommand.getResult();
      int i = strResult.length();
      if (i > 0) {
        i--;
        strResult = strResult.substring(0, i);
      }
    } catch (Exception ex) {
      throw new MatrixException(ex.getMessage());
    }
    return strResult;
  }
}