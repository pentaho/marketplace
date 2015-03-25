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

package org.pentaho.telemetry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.concurrent.*;


/**
 * The telemetry package allows Pentaho plugins to publish telemetry events to a known location, so that developers can
 * track usage/updates of their work.
 * <p/>
 * Collected data for each telemetry event (described in {@link org.pentaho.telemetry.TelemetryEvent}) is: <ul>
 * <li>Plugin Name</li> <li>Plugin Version</li> <li>Platform Version</li> <li>Time stamp </li> <li>Event type: One of
 * Installation, removal, usage or other</li> </ul>
 * <p/>
 * Telemetry can be enabled/disabled using the element telemetry in the pentaho.xml file.
 * <p/>
 * For a BA Server plugin, the workflow for telemetry publishing is:
 * <p/>
 * 1. Create an instance of {@link org.pentaho.telemetry.BaPluginTelemetry} providing the plugin name 2. Execute the {@
 * BaPlugin.Telemetry.sendTelemetryRequest} method. 3. The request is handled by TelemetryHelper, that stores it in a
 * queue. 4. A dedicated thread ({@link org.pentaho.telemetry.TelemetryEventKeeper}) reads this queue and stores the
 * telemetry events on the filesystem (under system/.telemetry). 5. Once a day, or whenever the BA server starts up,
 * another thread ({@link org.pentaho.telemetry.TelemetryEventSender}) reads these events from the filesystem and
 * publishes them (in blocks of 50) to a remote endpoint that will store the data. In case it succeeds, events are
 * removed from the filesystem In case it fails, events are kept for a maximum of 5 days. After this time, events will
 * be purged from the filesystem.
 *
 * @author pedrovale
 */
public class DiTelemetryHandler extends TelemetryHandler {

    public DiTelemetryHandler(String telemetryDirPath) {
        super(new File( "" ).getAbsolutePath());
    }
}
