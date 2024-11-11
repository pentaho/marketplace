/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
@SuppressWarnings( "rawtypes" )
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
    HashSet<String> closedFiles = new HashSet<>();
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
