/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.telemetry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import org.junit.Ignore;


/**
 *
 * @author pedrovale
 */
 @Ignore
public class TelemetryBaseTest {

  
//   @Test
//  public void testDummy() {
//     
//   }
  
   protected File[] getTelFilesInFolder(File f1) {
    return f1.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
              return name.endsWith(".tel");
            }          
          });    
  }
  
  protected void cleanFolder(File f1) {
    File[] requests = f1.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
              return name.endsWith(".tel");
            }          
          });    
    
    for (File f : requests)
      f.delete();    
  }
  
  
  protected void createTelEvent(TelemetryEvent te, File dir) {
       try {
              FileOutputStream fout = new FileOutputStream(dir.getAbsolutePath() + "/" + System.currentTimeMillis() + ".tel");
              ObjectOutputStream oos = new ObjectOutputStream(fout);
              oos.writeObject(te);
              oos.close();            
            } catch (FileNotFoundException fnfe) {

            }
            catch (IOException ioe) {

            }                    
  }
  
  
    protected ITelemetryDataProvider getDefaultTelemetryDataProvider(final String pluginName, 
         final boolean enabled) {
      return new ITelemetryDataProvider() {

      @Override
      public String getPlatformVersion() {
        return "4.5";
      }

      @Override
      public String getPluginName() {
        return pluginName;
      }

      @Override
      public String getPluginVersion() {
        return "12.09.05";
      }

      @Override
      public Map<String, String> getExtraInformation() {
        HashMap<String, String> extraInfo = new HashMap<String, String>();
        extraInfo.put("ep1", "ev1");
        return extraInfo;
      }

      @Override
      public TelemetryHelper.TelemetryEventType getEventType() {
        return TelemetryHelper.TelemetryEventType.OTHER;
      }

      @Override
      public boolean isTelemetryEnabled() {
        return enabled;
      }
 
      @Override 
      public String getBaseUrl() {
        return "pentahoTelemetry";
      }      
      
      
    };    
      
    }
      
    protected ITelemetryDataProvider getDefaultTelemetryDataProvider(final String pluginName, 
         final boolean enabled, final String url) {
      return new ITelemetryDataProvider() {

      @Override
      public String getPlatformVersion() {
        return "4.5";
      }

      @Override
      public String getPluginName() {
        return pluginName;
      }

      @Override
      public String getPluginVersion() {
        return "12.09.05";
      }

      @Override
      public Map<String, String> getExtraInformation() {
        HashMap<String, String> extraInfo = new HashMap<String, String>();
        extraInfo.put("ep1", "ev1");
        return extraInfo;
      }

      @Override
      public TelemetryHelper.TelemetryEventType getEventType() {
        return TelemetryHelper.TelemetryEventType.OTHER;
      }

      @Override
      public boolean isTelemetryEnabled() {
        return enabled;
      }
 
      @Override 
      public String getBaseUrl() {
        return url;
      }      
      
      
    };    
      
      
      
  }    
  
  
}
