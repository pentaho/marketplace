/*
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
 * Copyright (c) 2015 Pentaho Corporation. All rights reserved.
 */

package org.pentaho.marketplace.domain.services.helpers;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import java.util.jar.JarFile;

/**
 * General utility class for the marketplace.
 *
 * @author Will Gorman (wgorman@pentaho.com)
 */
@SuppressWarnings("rawtypes")
public class Util {

  private static Object getFieldObject( Class clazz, String name, Object obj ) throws Exception {
    Field field = clazz.getDeclaredField( name );
    field.setAccessible( true );
    return field.get( obj );
  }

  /**
   * This method is designed to clear out classloader file locks in windows.
   *
   * @param clazzLdr class loader to clean up
   */
  public static void closeURLClassLoader( URLClassLoader clazzLdr ) {
    HashSet<String> closedFiles = new HashSet<String>();
    try {
      Object obj = getFieldObject( URLClassLoader.class, "ucp", clazzLdr );
      ArrayList loaders = (ArrayList) getFieldObject( obj.getClass(), "loaders", obj );
      for ( Object ldr : loaders ) {
        try {
          JarFile file = (JarFile) getFieldObject( ldr.getClass(), "jar", ldr );
          closedFiles.add( file.getName() );
          file.close();
        } catch ( Exception e ) {
          // skip
        }
      }
    } catch ( Exception e ) {
      // skip
    }

    try {
      Vector nativeLibArr = (Vector) getFieldObject( ClassLoader.class, "nativeLibraries", clazzLdr );
      for ( Object lib : nativeLibArr ) {
        try {
          Method fMethod = lib.getClass().getDeclaredMethod( "finalize", new Class[ 0 ] );
          fMethod.setAccessible( true );
          fMethod.invoke( lib, new Object[ 0 ] );
        } catch ( Exception e ) {
          // skip
        }
      }
    } catch ( Exception e ) {
      // skip
    }

    HashMap uCache = null;
    HashMap fCache = null;

    try {
      Class factory =
        getFieldObject( classForName( "sun.net.www.protocol.jar.JarURLConnection" ), "factory", null )
          .getClass();
      try {
        fCache = (HashMap) getFieldObject( factory, "fileCache", null );
      } catch ( Exception e ) {
        // skip
      }
      try {
        uCache = (HashMap) getFieldObject( factory, "urlCache", null );
      } catch ( Exception e ) {
        // skip
      }
      if ( uCache != null ) {
        for ( Object file : ( (HashMap) uCache.clone() ).keySet() ) {
          if ( file instanceof JarFile ) {
            JarFile jar = (JarFile) file;
            if ( !closedFiles.contains( jar.getName() ) ) {
              continue;
            }
            try {
              jar.close();
            } catch ( IOException e ) {
              // skip
            }
            if ( fCache != null ) {
              fCache.remove( uCache.get( jar ) );
            }
            uCache.remove( jar );
          }
        }
      } else if ( fCache != null ) {
        for ( Object key : ( (HashMap) fCache.clone() ).keySet() ) {
          Object file = fCache.get( key );
          if ( file instanceof JarFile ) {
            JarFile jar = (JarFile) file;
            if ( !closedFiles.contains( jar.getName() ) ) {
              continue;
            }
            try {
              jar.close();
            } catch ( IOException e ) {
              // ignore
            }
            fCache.remove( key );
          }
        }
      }
    } catch ( Exception e ) {
      // skip
    }
  }

  public static Class classForName( String name ) throws ClassNotFoundException {
    try {
      ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
      if ( contextClassLoader != null ) {
        return contextClassLoader.loadClass( name );
      }
    } catch ( Throwable ignore ) { }
    return Class.forName( name );
  }
}
