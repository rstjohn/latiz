/**
 *  Copyright 2010 Latiz Technologies, LLC
 *
 *  This file is part of Latiz.
 *
 *  Latiz is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Latiz is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Latiz.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.AandR.library;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Useful class for dynamically changing the classpath, adding classes during runtime.
 * @author unknown
 */
public class ClassPathHacker {

  /**
   * Parameters of the method to add an URL to the System classes.
   */
  private static final Class[] parameters = new Class[] {URL.class};


  /**
   * Adds the array of files to the class path, skipping any that are already on the class path.
   * @param files
   * @throws IOException
   */
  public static void addFiles(File[] files) throws IOException {
    for(File f : files) {
      if(exists(f)) continue;
      addFile(f);
    }
  }

  /**
   * Adds the array of files to the class path, skipping any that are already on the class path.
   * @param files
   * @throws IOException
   */
  public static void addFiles(File[] files, URLClassLoader loader) throws IOException {
	  for(File f : files) {
		  if(exists(f,loader)) continue;
		  addFile(f,loader);
	  }
  }

  /**
   * Adds a file to the classpath.
   * @param s a String pointing to the file
   * @throws IOException
   */
  public static void addFile(String s) throws IOException {
    File f = new File(s);
    addFile(f);
  }

  /**
   * Adds a file to the classpath.
   * @param s a String pointing to the file
   * @throws IOException
   */
  public static void addFile(String s, URLClassLoader loader) throws IOException {
	  File f = new File(s);
	  addFile(f, loader);
  }


  /**
   * Adds a file to the classpath
   * @param f the file to be added
   * @throws IOException
   */
  public static void addFile(File f) throws IOException {
    addURL(f.toURI().toURL());
  }

  /**
   * Adds a file to the classpath
   * @param f the file to be added
   * @throws IOException
   */
  public static void addFile(File f, URLClassLoader loader) throws IOException {
	  addURL(f.toURI().toURL(),loader);
  }


  /**
   * Adds the content pointed by the URL to the classpath.
   * @param u the URL pointing to the content to be added
   * @throws IOException
   */
  public static void addURL(URL u) throws IOException {
    URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
    Class<?> sysclass = URLClassLoader.class;
    try {
      Method method = sysclass.getDeclaredMethod("addURL", parameters);
      method.setAccessible(true);
      method.invoke(sysloader,new Object[]{ u });
    } catch (Throwable t) {
      t.printStackTrace();
      throw new IOException("Error, could not add URL to system classloader");
    }

  }


  /**
   * Adds the content pointed by the URL to the classpath of the loader.
   * @param u the URL pointing to the content to be added
   * @throws IOException
   */
  public static void addURL(URL u, URLClassLoader loader) throws IOException {
	  //URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
	  Class<?> sysclass = URLClassLoader.class;
	  try {
		  Method method = sysclass.getDeclaredMethod("addURL", parameters);
		  method.setAccessible(true);
		  method.invoke(loader,new Object[]{ u });
	  } catch (Throwable t) {
		  t.printStackTrace();
		  throw new IOException("Error, could not add URL to system classloader");
	  }

  }


  /**
   * Determines whether the specified jar file in currently in the class path.
   * @param file
   * @return
   * @throws MalformedURLException
   */
  public static boolean exists(File file) throws MalformedURLException {
    URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
    URL[] paths = sysloader.getURLs();
    for(URL u : paths) {
      if(u.equals(file.toURI().toURL())) return true;
    }
    return false;
  }

  /**
   * Determines whether the specified jar file in currently in the class path.
   * @param file
   * @return
   * @throws MalformedURLException
   */
  public static boolean exists(File file, URLClassLoader loader) throws MalformedURLException {
	  URL[] paths = loader.getURLs();
	  for(URL u : paths) {
		  if(u.equals(file.toURI().toURL())) return true;
	  }
	  return false;
  }


  public static URL[] getURLs() {
    return ((URLClassLoader)ClassLoader.getSystemClassLoader()).getURLs();
  }
}