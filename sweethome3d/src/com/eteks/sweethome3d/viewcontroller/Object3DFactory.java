/*
 * Object3DFactory.java 8 fev 2011
 *
 * Sweet Home 3D, Copyright (c) 2011 Emmanuel PUYBARET / eTeks <info@eteks.com>
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
package com.eteks.sweethome3d.viewcontroller;

import com.eteks.sweethome3d.model.Home;
import com.eteks.sweethome3d.model.Selectable;

/**
 * A factory that specifies how to create the 3D objects from Sweet Home 3D model objects. 
 * @author Emmanuel Puybaret
 */
public interface Object3DFactory {
  /**
   * Returns the 3D object matching a given <code>item</code>.
   * @param home           the home of an item
   * @param item           a selectable item of a home
   * @param waitForLoading if <code>true</code> all resources used by the returned object should be available
   */
  public abstract Object createObject3D(Home home, Selectable item, boolean waitForLoading);
}
