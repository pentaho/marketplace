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
 * Copyright 2011 Pentaho Corporation.  All rights reserved.
 *
 * Created Oct 10th, 2011
 * @author Will Gorman (wgorman@pentaho.com)
 */
package org.pentaho.marketplace;

import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class MarketplaceServiceTest {
  
  @Test
  public void testParseXml() {
    MarketplaceService service = new MarketplaceService() {
      protected String getMarketplaceSiteContent() {
        try {
          return IOUtils.toString(new FileInputStream("test-res/availableplugins.xml"));
        } catch (Exception e) {
          e.printStackTrace();
        }
        return null;
      }
      public Plugin[] loadPluginsFromSite() {
        return super.loadPluginsFromSite();
      }
      
      public String resolveVersion(String url) {
        return super.resolveVersion(url);
      }
    };
    
    Plugin plugins[] = service.loadPluginsFromSite();
    
    Assert.assertEquals(3, plugins.length);
    Assert.assertEquals("cde", plugins[0].getId());
    Assert.assertEquals("wt_transparent.png", plugins[0].getImg());
    Assert.assertEquals("Community Dashboard Editor", plugins[0].getName());
    Assert.assertEquals("http://cde.webdetails.org", plugins[0].getLearnMoreUrl());
    Assert.assertEquals("http://www.webdetails.pt/ficheiros/CDE-bundle-1.0-RC3.tar.bz2", plugins[0].getDownloadUrl());
    Assert.assertEquals("1.0-RC3", plugins[0].getAvailableVersion());
    Assert.assertEquals("The Community Dashboard Editor (CDE) is the outcome of real-world needs: It was born to greatly simplify the creation, edition and rendering of dashboards.\n\nCDE and the technology underneath (CDF, CDA and CCC) allows to develop and deploy dashboards in the Pentaho platform in a fast and effective way.", plugins[0].getDescription().trim());
    Assert.assertEquals("WebDetails", plugins[0].getCompany());
    Assert.assertEquals("http://webdetails.pt", plugins[0].getCompanyUrl());
    Assert.assertNull(plugins[0].getInstallationNotes());
    Assert.assertNull(plugins[1].getChangelog());
    
    Assert.assertEquals("Changelog", plugins[2].getChangelog());
    Assert.assertEquals("http://localhost:8080/cdf-1.0.samples.zip", plugins[2].getSamplesDownloadUrl());
    Assert.assertEquals("Notes after install", plugins[2].getInstallationNotes());
  }
  
  @Test
  public void testResolveVersion() {
    MarketplaceService service = new MarketplaceService() {
      public String resolveVersion(String url) {
        return super.resolveVersion(url);
      }
    };
    Assert.assertEquals("http://plugins", service.resolveVersion("http://plugins"));
    Assert.assertTrue(service.resolveVersion("http://plugins_[VERSION]").indexOf("[VERSION]") < 0);
  }
  
  
  @Test
  public void testParseXmlWithAlternativeVersions() {
    MarketplaceService service = new MarketplaceService() {
      protected String getMarketplaceSiteContent() {
        try {
          return IOUtils.toString(new FileInputStream("test-res/availableplugins_differentversions.xml"));
        } catch (Exception e) {
          e.printStackTrace();
        }
        return null;
      }
      public Plugin[] loadPluginsFromSite() {
        return super.loadPluginsFromSite();
      }
      
      public String resolveVersion(String url) {
        return super.resolveVersion(url);
      }
    };
    
    Plugin plugins[] = service.loadPluginsFromSite();
    
    Assert.assertEquals(1, plugins.length);
    PluginVersion[] alternativeVersions = plugins[0].getAlternativeVersions();
    Assert.assertEquals(2, alternativeVersions.length);

    Assert.assertEquals("RC", alternativeVersions[0].getId());
    Assert.assertEquals("Release Candidate", alternativeVersions[0].getName());
    Assert.assertEquals("This is RC1 - pretty cool version but still not quite there", alternativeVersions[0].getDescription());
    Assert.assertEquals("http://www.webdetails.pt/RC/ficheiros/CDE-bundle-1.0-RC3.tar.bz2",  alternativeVersions[0].getDownloadUrl());
    Assert.assertEquals("http://www.webdetails.pt/RC/ficheiros/CDE-bundle-1.0-RC3-samples.tar.bz2",  alternativeVersions[0].getSamplesDownloadUrl());    

    
    PluginVersion desiredVersion = plugins[0].getVersionById("TRUNK");
    
    Assert.assertNotNull(desiredVersion);
    Assert.assertEquals("TRUNK", desiredVersion.getId());
    Assert.assertEquals("Trunk", desiredVersion.getName());
    
  }
  
  
  
}
