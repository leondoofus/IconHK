/*
 * HomeAppletRecorder.java 13 Oct 2008
 *
 * Sweet Home 3D, Copyright (c) 2008 Emmanuel PUYBARET / eTeks <info@eteks.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.eteks.sweethome3d.applet;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import com.eteks.sweethome3d.io.ContentRecording;
import com.eteks.sweethome3d.io.DefaultHomeInputStream;
import com.eteks.sweethome3d.io.DefaultHomeOutputStream;
import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.HomeRecorder;
import com.eteks.sweethome3d.model.InterruptedRecorderException;
import com.eteks.sweethome3d.model.RecorderException;

/**
 * Recorder that stores homes on a HTTP server.
 * @author Emmanuel Puybaret
 */
public class HomeAppletRecorder implements HomeRecorder {
  private final String           writeHomeURL;
  private final String           readHomeURL;
  private final String           listHomesURL;
  private final ContentRecording contentRecording;
  private long                   availableHomesCacheTime;
  private String []              availableHomesCache;

  /**
   * Creates a recorder that will use the URLs in parameter to write, read and list homes.
   * @see SweetHome3DApplet
   */
  public HomeAppletRecorder(String writeHomeURL, 
                            String readHomeURL,
                            String listHomesURL) {
    this(writeHomeURL, readHomeURL, listHomesURL, true);
  }
  
  /**
   * Creates a recorder that will use the URLs in parameter to write, read and list homes.
   * @see SweetHome3DApplet
   */
  public HomeAppletRecorder(String writeHomeURL, 
                            String readHomeURL,
                            String listHomesURL,
                            boolean includeTemporaryContent) {
    this(writeHomeURL, readHomeURL, listHomesURL, 
        includeTemporaryContent 
            ? ContentRecording.INCLUDE_TEMPORARY_CONTENT
            : ContentRecording.INCLUDE_ALL_CONTENT);
  }
  
  /**
   * Creates a recorder that will use the URLs in parameter to write, read and list homes.
   * @see SweetHome3DApplet
   */
  public HomeAppletRecorder(String writeHomeURL, 
                            String readHomeURL,
                            String listHomesURL,
                            ContentRecording contentRecording) {
    this.writeHomeURL = writeHomeURL;
    this.readHomeURL = readHomeURL;
    this.listHomesURL = listHomesURL;
    this.contentRecording = contentRecording;
  }
  
  /**
   * Posts home data to the server URL returned by <code>getHomeSaveURL</code>.
   * @throws RecorderException if a problem occurred while writing home.
   */
  public void writeHome(Home home, String name) throws RecorderException {
    HttpURLConnection connection = null;
    try {
      // Open a stream to server 
      connection = (HttpURLConnection)new URL(this.writeHomeURL).openConnection();
      connection.setRequestMethod("POST");
      String multiPartBoundary = "---------#@&$!d3emohteews!$&@#---------";
      connection.setRequestProperty("Content-Type", "multipart/form-data; charset=UTF-8; boundary=" + multiPartBoundary);
      connection.setDoOutput(true);
      connection.setDoInput(true);
      connection.setUseCaches(false);
      
      // Post home part
      OutputStream out = connection.getOutputStream();
      out.write(("--" + multiPartBoundary + "\r\n").getBytes("UTF-8"));
      out.write(("Content-Disposition: form-data; name=\"home\"; filename=\"" 
          + name.replace('\"', '\'') + "\"\r\n").getBytes("UTF-8"));
      out.write(("Content-Type: application/octet-stream\r\n\r\n").getBytes("UTF-8"));
      out.flush();
      DefaultHomeOutputStream homeOut = new DefaultHomeOutputStream(out, 9, this.contentRecording);
      // Write home with HomeOuputStream
      homeOut.writeHome(home);
      homeOut.flush();
      
      // Post last boundary
      out.write(("\r\n--" + multiPartBoundary + "--\r\n").getBytes("UTF-8"));
      out.close();
      
      // Read response
      InputStream in = connection.getInputStream();
      int read = in.read();
      in.close();
      if (read != '1') {
        throw new RecorderException("Saving home " + name + " failed");
      }
      // Reset availableHomes to force a new request at next getAvailableHomes or exists call
      this.availableHomesCache = null; 
    } catch (InterruptedIOException ex) {
      throw new InterruptedRecorderException("Save " + name + " interrupted");
    } catch (IOException ex) {
      throw new RecorderException("Can't save home " + name, ex);
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }

  /**
   * Returns a home instance read from its file <code>name</code>.
   * @throws RecorderException if a problem occurred while reading home, 
   *   or if file <code>name</code> doesn't exist.
   */
  public Home readHome(String name) throws RecorderException {
    URLConnection connection = null;
    DefaultHomeInputStream in = null;
    try {
      // Replace % sequence by %% except %s before formating readHomeURL with home name 
      String readHomeURL = String.format(this.readHomeURL.replaceAll("(%[^s])", "%$1"), 
          URLEncoder.encode(name, "UTF-8"));
      // Open a home input stream to server
      connection = new URL(readHomeURL).openConnection();
      connection.setRequestProperty("Content-Type", "charset=UTF-8");
      connection.setUseCaches(false);
      in = new DefaultHomeInputStream(connection.getInputStream(), this.contentRecording);
      // Read home with HomeInputStream
      Home home = in.readHome();
      return home;
    } catch (InterruptedIOException ex) {
      throw new InterruptedRecorderException("Read " + name + " interrupted");
    } catch (IOException ex) {
      throw new RecorderException("Can't read home from " + name, ex);
    } catch (ClassNotFoundException ex) {
      throw new RecorderException("Missing classes to read home from " + name, ex);
    } finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (IOException ex) {
        throw new RecorderException("Can't close file " + name, ex);
      }
    }
  }

  /**
   * Returns <code>true</code> if the home <code>name</code> exists.
   */
  public boolean exists(String name) throws RecorderException {
    String [] availableHomes;    
    if (this.availableHomesCache != null 
        && this.availableHomesCacheTime + 100 > System.currentTimeMillis()) {
      // Return available homes list in cache if the cache is less than 100 ms old  
      availableHomes = this.availableHomesCache;
    } else {
      availableHomes = getAvailableHomes();
    }
    for (String home : availableHomes) {
      if (home.equals(name)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the available homes on server.
   */
  public String [] getAvailableHomes() throws RecorderException {
    URLConnection connection = null;
    InputStream in = null;
    try {
      // Open a stream to server 
      connection = new URL(this.listHomesURL).openConnection();
      connection.setUseCaches(false);
      in = connection.getInputStream();
      String contentEncoding = connection.getContentEncoding();
      if (contentEncoding == null) {
        contentEncoding = "UTF-8";
      }
      Reader reader = new InputStreamReader(in, contentEncoding);
      StringWriter homes = new StringWriter();
      for (int c; (c = reader.read()) != -1; ) {
        homes.write(c);
      }
      String [] availableHomes = homes.toString().split("\n");
      if (availableHomes.length == 1 && availableHomes [0].length() == 0) {
        this.availableHomesCache = new String [0];
      } else {
        this.availableHomesCache = availableHomes;
      }
      this.availableHomesCacheTime = System.currentTimeMillis();
      return this.availableHomesCache;
    } catch (IOException ex) {
      throw new RecorderException("Can't read homes from server", ex);
    } finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (IOException ex) {
        throw new RecorderException("Can't close connection", ex);
      }
    }
  }
}
