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

package org.pentaho.marketplace.util.web;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.URL;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.http.HttpContext;

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
