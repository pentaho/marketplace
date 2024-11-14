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


package org.pentaho.marketplace.util.web;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.net.URL;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import org.ops4j.pax.web.service.spi.util.Path;


public final class MarketplaceHttpContext implements HttpContext {

  // region Properties
  private Log getLogger() {
    return this.logger;
  }
  private Log logger = LogFactory.getLog( this.getClass() );

  public Bundle getBundle() {
    return this.bundle;
  }
  private Bundle bundle;

  public Map<String, String> getMimeTypes() {
    return this.mimeTypes;
  }
  public void setMimeTypes( Map<String, String> mimeTypes ) {
    this.mimeTypes = mimeTypes;
  }
  private Map<String, String> mimeTypes;

  // endregion

  // region Constructors
  public MarketplaceHttpContext() {
    this.mimeTypes = new Hashtable<>();
    this.bundle = FrameworkUtil.getBundle( MarketplaceHttpContext.class );
  }
  // endregion

  // region Methods
  /**
   * There is no security by default, so always return "true". {@inheritDoc}
   */
  @Override
  public boolean handleSecurity(final HttpServletRequest httpServletRequest,
                                final HttpServletResponse httpServletResponse) {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public URL getResource( final String name ) {
    final String normalizedname = Path.normalizeResourcePath( name );
    Bundle bundle = this.getBundle();
    this.getLogger().debug("Searching bundle [" + bundle + "] for resource [" + normalizedname + "]");
    return bundle.getResource( normalizedname );
  }

  @Override public String getMimeType( String name ) {
    String extension = FilenameUtils.getExtension( name );
    return this.getMimeTypes().get( extension );
  }

  // endregion
}
