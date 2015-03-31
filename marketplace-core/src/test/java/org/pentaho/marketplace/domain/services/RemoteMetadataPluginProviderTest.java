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
 * Copyright (c) 2015 Pentaho Corporation. All rights reserved.
 */

package org.pentaho.marketplace.domain.services;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class RemoteMetadataPluginProviderTest {

  // TODO Remove constant...
  private static final String SETTINGS_MARKETPLACE_SITE_ = "settings/marketplace-site";


  /**
   * Tests that the metadata plugin provider fed into the PluginService is initialized
   * with the URL specified in the settings.xml provided by the resource loader
   */
  @Test
  public void testUsesUrlSpecifiedByPluginResourceLoader()  {

    //TODO Setup test. This test was copied from plugin service test as it longer makes sense for it to be tested there

    assertTrue( false );

    /*
    // arrange
    IDomainStatusMessageFactory domainStatusMessageFactory = this.domainStatusMessageFactory;
    IVersionDataFactory versionDataFactory = this.versionDataFactory;

    IRemotePluginProvider pluginProvider = mock( IRemotePluginProvider.class );
    MarketplaceXmlSerializer serializer = mock( MarketplaceXmlSerializer.class );
    ISecurityHelper securityHelper = mock( ISecurityHelper.class );

    IPluginResourceLoader resourceLoader = mock( IPluginResourceLoader.class );
    String resourceMetadataUrl = "http://myresource.com/metadata.xml";
    when(resourceLoader.getPluginSetting( BAPluginService.class, SETTINGS_MARKETPLACE_SITE_ ) )
      .thenReturn( resourceMetadataUrl );

    ITelemetryService telemetryService = mock ( ITelemetryService.class );
    Bundle bundle = mock( Bundle.class );

    // act
    BAPluginService service = new BAPluginService( pluginProvider, versionDataFactory,
      domainStatusMessageFactory, telemetryService, serializer, securityHelper, bundle );

    // assert
    verify( pluginProvider, times( 1 ) ).setUrl( new URL( resourceMetadataUrl )  );
    */
  }
}
