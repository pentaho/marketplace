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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.marketplace;

import org.junit.Assert;
import org.junit.Test;
import org.pentaho.marketplace.domain.model.entities.VersionData;

public class VersionDataTest {

  @Test
  public void testVersionData() {
    VersionData vd = new VersionData("4.5.0-GA");
    Assert.assertEquals("4.5.0-GA", vd.getInfo());
    Assert.assertEquals(4, vd.getMajor());
    Assert.assertEquals(5, vd.getMinor());
    Assert.assertEquals(0, vd.getPatch());

    vd = new VersionData("4.5.0.GA");
    Assert.assertEquals("4.5.0.GA", vd.getInfo());
    Assert.assertEquals(4, vd.getMajor());
    Assert.assertEquals(5, vd.getMinor());
    Assert.assertEquals(0, vd.getPatch());
    
    vd = new VersionData("4.5.0-stable");
    Assert.assertEquals("4.5.0-stable", vd.getInfo());
    Assert.assertEquals(4, vd.getMajor());
    Assert.assertEquals(5, vd.getMinor());
    Assert.assertEquals(0, vd.getPatch());
    
    vd = new VersionData("4.5-SNAPSHOT");
    Assert.assertEquals(4, vd.getMajor());
    Assert.assertEquals(5, vd.getMinor());
    Assert.assertEquals(Integer.MAX_VALUE, vd.getPatch());
    
    vd = new VersionData("TRUNK-SNAPSHOT");    
    Assert.assertEquals(Integer.MAX_VALUE, vd.getMajor());
    Assert.assertEquals(Integer.MAX_VALUE, vd.getMinor());
    Assert.assertEquals(Integer.MAX_VALUE, vd.getPatch());
    
    vd = new VersionData("SUGAR-SNAPSHOT");
    Assert.assertEquals(Integer.MAX_VALUE, vd.getMajor());
    Assert.assertEquals(Integer.MAX_VALUE, vd.getMinor());
    Assert.assertEquals(Integer.MAX_VALUE, vd.getPatch());
    
    vd = new VersionData("4.5");
    Assert.assertEquals(4, vd.getMajor());
    Assert.assertEquals(5, vd.getMinor());
    Assert.assertEquals(0, vd.getPatch());
    
    vd = new VersionData("4.5.0");
    Assert.assertEquals(4, vd.getMajor());
    Assert.assertEquals(5, vd.getMinor());
    Assert.assertEquals(0, vd.getPatch());
    
    vd = new VersionData("4");
    Assert.assertEquals(4, vd.getMajor());
    Assert.assertEquals(0, vd.getMinor());
    Assert.assertEquals(0, vd.getPatch());
    
    vd = new VersionData("4-SNAPSHOT");
    Assert.assertEquals(4, vd.getMajor());
    Assert.assertEquals(Integer.MAX_VALUE, vd.getMinor());
    Assert.assertEquals(Integer.MAX_VALUE, vd.getPatch());
    
    vd = new VersionData(null);
    Assert.assertEquals("", vd.getInfo());
    Assert.assertEquals(Integer.MAX_VALUE, vd.getMajor());
    Assert.assertEquals(Integer.MAX_VALUE, vd.getMinor());
    Assert.assertEquals(Integer.MAX_VALUE, vd.getPatch());
    
    Assert.assertTrue("snapshot check failed", new VersionData("4.5.0-GA").within(new VersionData("4.5"), new VersionData("4.5-SNAPSHOT")));
    Assert.assertFalse("ga check failed", new VersionData("4.5.0-GA").within(new VersionData("4.5.0-stable"), new VersionData("4.5.0-stable")));
    Assert.assertTrue("min version failed", new VersionData("4.5.0-GA").within(new VersionData("4"), new VersionData("TRUNK-SNAPSHOT")));
    Assert.assertTrue("max version failed", new VersionData("TRUNK-SNAPSHOT").within(new VersionData("4"), new VersionData("TRUNK-SNAPSHOT")));
    Assert.assertFalse("too small major failed", new VersionData("3.5.0-GA").within(new VersionData("4"), new VersionData("5")));
    Assert.assertFalse("too small minor failed", new VersionData("4.5.0-GA").within(new VersionData("4.6"), new VersionData("4.9")));
    Assert.assertFalse("too big patch failed", new VersionData("4.5.0-GA").within(new VersionData("2.5"), new VersionData("3.5.0")));
    Assert.assertTrue("patch snapshot failed", new VersionData("5.0.0-stable").within(new VersionData("5.0"), new VersionData("5.0-SNAPSHOT")));
    Assert.assertTrue("null check failed", new VersionData("5.0.0-stable").within(new VersionData("5.0"), new VersionData("5.0-SNAPSHOT")));
    
    
  }
}
