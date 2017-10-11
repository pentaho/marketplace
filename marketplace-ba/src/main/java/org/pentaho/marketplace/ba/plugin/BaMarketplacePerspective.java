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
 * Copyright (c) 2015 - 2017 Hitachi Vantara. All rights reserved.
 */

package org.pentaho.marketplace.ba.plugin;

import org.pentaho.platform.api.engine.perspective.pojo.IPluginPerspective;
import org.pentaho.ui.xul.XulOverlay;

import java.util.ArrayList;

public class BaMarketplacePerspective implements IPluginPerspective {

  // region Properties
  @Override public String getId() {
    return this.id;
  }
  @Override public void setId( String id ) {
    this.id = id;
  }
  private String id;

  @Override public String getTitle() {
    return this.title;
  }
  @Override public void setTitle( String title ) {
    this.title = title;
  }
  private String title;


  @Override public String getContentUrl() {
    return this.contentUrl;
  }
  @Override public void setContentUrl( String contentUrl ) {
    this.contentUrl = contentUrl;
  }
  private String contentUrl;

  @Override public String getResourceBundleUri() {
    return this.resourceBundleUri;
  }
  @Override public void setResourceBundleUri( String resourceBundleUri ) {
    this.resourceBundleUri = resourceBundleUri;
  }
  private String resourceBundleUri;

  @Override public ArrayList<XulOverlay> getOverlays() {
    return this.overlays;
  }
  @Override public void setOverlays( ArrayList<XulOverlay> overlays ) {
    this.overlays = overlays;
  }
  private ArrayList<XulOverlay> overlays;

  @Override public int getLayoutPriority() {
    return this.layoutPriority;
  }
  @Override public void setLayoutPriority( int layoutPriority ) {
    this.layoutPriority = layoutPriority;
  }
  private int layoutPriority;

  @Override public ArrayList<String> getRequiredSecurityActions() {
    return this.requiredSecurityActions;
  }
  @Override public void setRequiredSecurityActions( ArrayList<String> requiredSecurityActions ) {
    this.requiredSecurityActions = requiredSecurityActions;
  }
  private ArrayList<String> requiredSecurityActions;
}
