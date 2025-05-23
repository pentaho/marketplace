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


'use strict';

define( [ 'marketplaceApp' ],

    function ( marketplace ) {
      // TODO: remove global variable CONTEXT_PATH which is injected by webcontext.js
      var baseUrl = CONTEXT_PATH + 'osgi/cxf/marketplace/services';
      marketplace.constant( 'BASE_URL', baseUrl );
    }

);