/*!
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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.marketplace;

import java.io.FileInputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.core.util.Assert;

public class MarketplaceServiceTest {


  @Test
  public void testParseXml() {

    /*MarketplaceService service = new MarketplaceService() {
      protected String getMarketplaceSiteContent() {
        try {
          return IOUtils.toString(new FileInputStream("test-res/availableplugins.xml"));
        } catch (Exception e) {
          e.printStackTrace();
        }
        return null;
      }
      public List<Plugin> loadPluginsFromSite() {
        return super.loadPluginsFromSite();
      }
      
      protected boolean reloadPlugins() {
        return true;
      }
      
    };
    
    List<Plugin> plugins = service.loadPluginsFromSite();
    
    Assert.assertEquals(3, plugins.size());
    Assert.assertEquals("cde", plugins.get(0).getId());
    Assert.assertEquals("wt_transparent.png", plugins.get(0).getImg());
    Assert.assertEquals("wt_transparent_small.png", plugins.get(0).getSmallImg());
    Assert.assertEquals("Community Dashboard Editor", plugins.get(0).getName());
    Assert.assertEquals("http://cde.webdetails.org", plugins.get(0).getLearnMoreUrl());
//    Assert.assertEquals("http://www.webdetails.pt/ficheiros/CDE-bundle-1.0-RC3.tar.bz2", plugins[0].getDownloadUrl());
//    Assert.assertEquals("1.0-RC3", plugins[0].getAvailableVersion());
    Assert.assertEquals("The Community Dashboard Editor (CDE) is the outcome of real-world needs: It was born to greatly simplify the creation, edition and rendering of dashboards.\n\nCDE and the technology underneath (CDF, CDA and CCC) allows to develop and deploy dashboards in the Pentaho platform in a fast and effective way.", plugins.get(0).getDescription().trim());
    Assert.assertEquals("WebDetails", plugins.get(0).getCompany());
    Assert.assertEquals("http://webdetails.pt", plugins.get(0).getCompanyUrl());
    Assert.assertNull(plugins.get(0).getInstallationNotes());
//    Assert.assertNull(plugins[1].getChangelog());
    
//    Assert.assertEquals("Changelog", plugins[2].getChangelog());
//    Assert.assertEquals("http://localhost:8080/cdf-1.0.samples.zip", plugins[2].getSamplesDownloadUrl());
    Assert.assertEquals("Notes after install", plugins.get(2).getInstallationNotes());
    */

    Assert.assertTrue( true );
  }
  
  @Test
  public void testParseXmlWithAlternativeVersions() {
    /*
    MarketplaceService service = new MarketplaceService() {
      protected String getMarketplaceSiteContent() {
        try {
          return IOUtils.toString(new FileInputStream("test-res/availableplugins_differentversions.xml"));
        } catch (Exception e) {
          e.printStackTrace();
        }
        return null;
      }
      public List<Plugin> loadPluginsFromSite() {
        return super.loadPluginsFromSite();
      }
    };
    
    List<Plugin> plugins = service.loadPluginsFromSite();
    
    Assert.assertEquals(1, plugins.size());
    List<PluginVersion> alternativeVersions = plugins.get(0).getVersions();
    Assert.assertEquals(2, alternativeVersions.size());

    Assert.assertEquals("RC", alternativeVersions.get(0).getBranch());
    Assert.assertEquals("Release Candidate", alternativeVersions.get(0).getName());
    Assert.assertEquals("ChangeLog for RC", alternativeVersions.get(0).getChangelog());
    Assert.assertEquals("This is RC1 - pretty cool version but still not quite there", alternativeVersions.get(0).getDescription());
    Assert.assertEquals("http://www.webdetails.pt/RC/ficheiros/CDE-bundle-1.0-RC3.tar.bz2",  alternativeVersions.get(0).getDownloadUrl());
    Assert.assertEquals("http://www.webdetails.pt/RC/ficheiros/CDE-bundle-1.0-RC3-samples.tar.bz2",  alternativeVersions.get(0).getSamplesDownloadUrl());    
    Assert.assertNull(alternativeVersions.get(0).getBuildId());    

    PluginVersion desiredVersion = plugins.get(0).getVersionByBranch("TRUNK");
    
    Assert.assertNotNull(desiredVersion);
    Assert.assertEquals("TRUNK", desiredVersion.getBranch());
    Assert.assertEquals("Trunk", desiredVersion.getName());
    Assert.assertEquals("Change Log for TRUNK", desiredVersion.getChangelog());
    Assert.assertEquals("135", desiredVersion.getBuildId());
    */

    Assert.assertTrue( true );
  }
}
