/*
 * FurnitureCategory.java 7 avr. 2006
 * 
 * Sweet Home 3D, Copyright (c) 2006 Emmanuel PUYBARET / eTeks <info@eteks.com>
 *  
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.eteks.sweethome3d.model;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Category of furniture.
 * @author Emmanuel Puybaret
 */
public class FurnitureCategory implements Comparable<FurnitureCategory> {
  private final String                  name;
  private List<CatalogPieceOfFurniture> furniture;
  private boolean                       sorted;
  
  private static final Collator  COMPARATOR = Collator.getInstance();

  /**
   * Create a category.
   * @param name the name of the category.
   */
  public FurnitureCategory(String name) {
    this.name = name;
    this.furniture = new ArrayList<CatalogPieceOfFurniture>();
  }

  /**
   * Returns the name of this category.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Returns the furniture list of this category sorted by name.
   * @return an unmodifiable list of furniture.
   */
  public List<CatalogPieceOfFurniture> getFurniture() {
    checkFurnitureSorted();
    return Collections.unmodifiableList(this.furniture);
  }

  /**
   * Checks furniture is sorted.
   */
  private void checkFurnitureSorted() {
    if (!this.sorted) {
      Collections.sort(this.furniture);
      this.sorted = true;
    }
  }

  /**
   * Returns the count of furniture in this category.
   */
  public int getFurnitureCount() {
    return this.furniture.size();
  }

  /**
   * Returns the piece of furniture at a given <code>index</code>.
   */
  public CatalogPieceOfFurniture getPieceOfFurniture(int index) {
    checkFurnitureSorted();
    return this.furniture.get(index);
  }

  /**
   * Returns the index of the given <code>piece</code> of furniture.
   * @since 3.6
   */
  public int getIndexOfPieceOfFurniture(CatalogPieceOfFurniture piece) {
    checkFurnitureSorted();
    return this.furniture.indexOf(piece);
  }

  /**
   * Adds a piece of furniture to this category.
   * @param piece the piece to add.
   */
  void add(CatalogPieceOfFurniture piece) {
    piece.setCategory(this);
    this.furniture.add(piece);    
    this.sorted = false;
  }

  /**
   * Deletes a piece of furniture from this category.
   * @param piece the piece to remove.
   * @throws IllegalArgumentException if the piece doesn't exist in this category.
   */
  void delete(CatalogPieceOfFurniture piece) {
    int pieceIndex = this.furniture.indexOf(piece);
    if (pieceIndex == -1) {
      throw new IllegalArgumentException(
          this.name + " doesn't contain piece " + piece.getName());
    }
    //  Make a copy of the list to avoid conflicts in the list returned by getFurniture
    this.furniture = new ArrayList<CatalogPieceOfFurniture>(this.furniture);
    this.furniture.remove(pieceIndex);
  }
  
  /**
   * Returns <code>true</code> if this category and the one in parameter have the same name.
   */
  @Override
  public boolean equals(Object obj) {
    return obj instanceof FurnitureCategory
           && COMPARATOR.equals(this.name, ((FurnitureCategory)obj).name);
  }

  @Override
  public int hashCode() {
    return this.name.hashCode();
  }

  /**
   * Compares the names of this category and the one in parameter.
   */
  public int compareTo(FurnitureCategory category) {
    return COMPARATOR.compare(this.name, category.name);
  }
}
