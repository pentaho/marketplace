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
