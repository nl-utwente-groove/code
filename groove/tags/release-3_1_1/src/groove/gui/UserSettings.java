/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package groove.gui;

import groove.trans.SystemProperties;
import groove.util.Groove;
import groove.gui.Simulator;

import java.awt.Component;
import java.awt.Container;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

/**
 * Program that working with user settings
 * @author Maria Zimakova
 * @version $Revision: 1490 $
 */

/** Class saving a previous user settings. */
public class UserSettings {

    /** Get parameter index in the array */
    private static int GetIndex(int p_id, String p_string, String p_parameter,
            int p_index) {
        int ind;
        if (p_id < 0) {
            if (p_string.indexOf(p_parameter + "=") >= 0) {
                ind = p_index - 1;
            } else {
                ind = -1;
            }
        } else {
            ind = p_id;
        }
        return ind;
    }

    /** Checks a previous user settings on maximization */
    private static boolean CheckMaximize(String p_string) {
        if (p_string.substring(p_string.indexOf("=") + 1).equals("Y")) {
            return true;
        } else {
            return false;
        }
    }

    /** Get a number from string parameter */
    private static int GetNumber(String p_string) {
        int num;
        try {
            String str = p_string.substring(p_string.indexOf("=") + 1);
            num = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            num = 0;
        }
        return num;
    }

    /** Reads and applies a previous user settings. */
    public static void applyUserSettings(JFrame MyFrame) {
         ArrayList<String> user_settings = new ArrayList<String>();
         int id_maximize = -1;
         int id_width = -1;
         int id_height = -1;
         int id_divider = -1;

         String[] sh =
             Options.userPrefs.get(SystemProperties.USER_SETTINGS, "").split(",");
         for (String p : sh) {
             user_settings.add(p);
             id_maximize = GetIndex(id_maximize, p, "maximization", user_settings.size());
             id_width    = GetIndex(id_width, p, "width", user_settings.size());
             id_height   = GetIndex(id_height, p, "height", user_settings.size());
             id_divider  = GetIndex(id_divider, p, "divider", user_settings.size());
         }

         if ((id_maximize >= 0) && (CheckMaximize(user_settings.get(id_maximize)))) {
              MyFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);                 
         } else {
             if ((id_width >= 0) && (GetNumber(user_settings.get(id_width)) > 0)) {
                  MyFrame.setSize(GetNumber(user_settings.get(id_width)), MyFrame.getHeight());                     
             }
             if ((id_height >= 0) && (GetNumber(user_settings.get(id_height)) > 0)) {
                  MyFrame.setSize(MyFrame.getWidth(), GetNumber(user_settings.get(id_height)));                     
             }
         }
         
         if ((id_divider >= 0) && (GetNumber(user_settings.get(id_divider)) > 0)) {
              Container contentPane = MyFrame.getContentPane();
              for (int i=0; i < contentPane.getComponentCount(); i++) {
                  Component comp = contentPane.getComponent(i);
                  if (comp instanceof JSplitPane) {
                      JSplitPane jsp = (JSplitPane) comp;
                      jsp.setDividerLocation(GetNumber(user_settings.get(id_divider)));
                  }
              }                              
         }
     }

    /** Generates a user setting string */
    private static String makeUserSettingString(JFrame MyFrame) {
       ArrayList<String> hist = new ArrayList<String>();
       String ret = "";
        if (MyFrame.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
            ret = ret + "maximization=Y,";
        } else {
            ret = ret + "maximization=N,";
            ret = ret + "width=" + MyFrame.getWidth() + ",";
            ret = ret + "height=" + MyFrame.getHeight() + ",";
        }

        Container contentPane = MyFrame.getContentPane();
        for (int i=0; i < contentPane.getComponentCount(); i++) {
            Component comp = contentPane.getComponent(i);
            if (comp instanceof JSplitPane) {
                JSplitPane jsp = (JSplitPane) comp;
                ret = ret + "divider=" + jsp.getDividerLocation() + ",";
            }
        }    
        
        String[] ht =
            Options.userPrefs.get(SystemProperties.HISTORY_KEY, "").split(",");
        for (String p : ht) {
            hist.add(p);
        }

        if (ret.lastIndexOf(",") == ret.length()) {
            ret = ret.substring(0, ret.length() - 1);
        }
        return ret;
    }

    /**
     * Synchronizes saved user settings with the current ones
     */
    public static void synchSettings(JFrame MyFrame) {
        String newStr = makeUserSettingString(MyFrame);
        Options.userPrefs.put(SystemProperties.USER_SETTINGS, newStr);
    }


}
