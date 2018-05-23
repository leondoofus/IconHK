/*
 * DefaultHomeInputStream.java 13 Oct 2008
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
package com.eteks.sweethome3d.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.tools.OperatingSystem;
import com.eteks.sweethome3d.tools.URLContent;

/**
 * An <code>InputStream</code> filter that reads a home from a stream 
 * at .sh3d file format. 
 * @see DefaultHomeOutputStream
 */
public class DefaultHomeInputStream extends FilterInputStream {
  private final ContentRecording contentRecording;
  private File tempFile;

  /**
   * Creates a home input stream filter able to read a home and its content
   * from <code>in</code>.
   */
  public DefaultHomeInputStream(InputStream in) throws IOException {
    this(in, ContentRecording.INCLUDE_ALL_CONTENT);
  }

  /**
   * Creates a home input stream filter able to read a home and its content
   * from <code>in</code>.
   */
  public DefaultHomeInputStream(InputStream in, 
                                ContentRecording contentRecording) throws IOException {
    super(in);
    this.contentRecording = contentRecording;
  }

  /**
   * Throws an <code>InterruptedRecorderException</code> exception 
   * if current thread is interrupted. The interrupted status of the current thread 
   * is cleared when an exception is thrown.
   */
  private static void checkCurrentThreadIsntInterrupted() throws InterruptedIOException {
    if (Thread.interrupted()) {
      throw new InterruptedIOException();
    }
  }
  
  /**
   * Reads home from a zipped stream.
   */
  public Home readHome() throws IOException, ClassNotFoundException {
    if (this.contentRecording != ContentRecording.INCLUDE_NO_CONTENT) {
      // Copy home stream in a temporary file 
      this.tempFile = OperatingSystem.createTemporaryFile("open", ".sweethome3d");
      checkCurrentThreadIsntInterrupted();
      OutputStream tempOut = null;
      try {
        tempOut = new FileOutputStream(this.tempFile);
        byte [] buffer = new byte [8192];
        int size; 
        while ((size = this.in.read(buffer)) != -1) {
          tempOut.write(buffer, 0, size);
        }
      } finally {
        if (tempOut != null) {
          tempOut.close();
        }
      }
    }
    
    ZipInputStream zipIn = null;
    try {
      // Open a zip input from temp file
      zipIn = new ZipInputStream(this.contentRecording == ContentRecording.INCLUDE_NO_CONTENT
          ? this.in : new FileInputStream(this.tempFile));
      // Read Home entry
      ZipEntry entry;
      while ((entry = zipIn.getNextEntry()) != null
          && !"Home".equals(entry.getName())) {
      }
      if (entry == null) {
        throw new IOException("Missing entry \"Home\"");
      }
      checkCurrentThreadIsntInterrupted();
      // Use an ObjectInputStream that replaces temporary URLs of Content objects 
      // by URLs relative to file 
      ObjectInputStream objectStream = new HomeObjectInputStream(zipIn);
      return (Home)objectStream.readObject();
    } finally {
      if (zipIn != null) {
        zipIn.close();
      }
    }
  }

  /**
   * <code>ObjectInputStream</code> that replaces temporary <code>URLContent</code> 
   * objects by <code>URLContent</code> objects that points to file.
   */
  private class HomeObjectInputStream extends ObjectInputStream {
    public HomeObjectInputStream(InputStream in) throws IOException {
      super(in);
      if (contentRecording != ContentRecording.INCLUDE_NO_CONTENT) {
        enableResolveObject(true);
      }
    }

    @Override
    protected Object resolveObject(Object obj) throws IOException {
      if (obj instanceof URLContent) {
        URL tmpURL = ((URLContent)obj).getURL();
        String url = tmpURL.toString();
        if (url.startsWith("jar:file:temp!/")) {
          // Replace "temp" in URL by current temporary file
          String entryName = url.substring(url.indexOf('!') + 2);
          URL fileURL = new URL("jar:" + tempFile.toURI() + "!/" + entryName);
          HomeURLContent urlContent = new HomeURLContent(fileURL);
          try {
            // Check entry exists
            urlContent.openStream().close();
          } catch (IOException ex) {
            throw new IOException("Missing entry \"" + entryName + "\"");
          }
          return urlContent;
        } else {
          return obj;
        }
      } else {
        return obj;
      }
    }
  }
}