/*
 * Copyright 2002 - 2017 Webdetails, a Hitachi Vantara company.  All rights reserved.
 *
 * This software was developed by Webdetails and is provided under the terms
 * of the Mozilla Public License, Version 2.0, or any later version. You may not use
 * this file except in compliance with the license. If you need a copy of the license,
 * please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
 * the license for the specific language governing your rights and limitations.
 */


'use strict';

define(
    [
      'marketplaceApp',
      'underscore',
      'marketplace-lib/Logger'
    ],
    function ( app, _, logger ) {
      logger.log("Required services/categoryService.js");

      var service = app.factory( 'categoryService',
          [ '$translate',
            function ( $translate ) {

              var categories = {};

              function categoryIdToTranslationId( categoryId ) {
                return 'marketplace.categories.' +
                       // whitespaces => _
                       categoryId.toLowerCase().replace(/ /g,"_") +
                       '.name';
              }

              function Category( main, sub ) {
                var that = this;
                that.main = main;
                that.mainName = main;
                that.mainTranslateId = categoryIdToTranslationId( main );

                $translate( that.mainTranslateId )
                    .then( function ( translatedName ) {
                      if ( translatedName != that.mainTranslateId ) {
                        that.mainName = translatedName;
                      }
                    });

                if( sub ) {
                  that.sub = sub;
                  that.subName = sub;
                  that.subTranslateId = categoryIdToTranslationId( sub );

                  $translate( that.subTranslateId )
                      .then( function (translatedName ) {
                        if ( translatedName != that.subTranslateId ) {
                          that.subName = translatedName;
                        }
                      });
                }
              }

              Category.prototype.getId = function () {
                return this.main + this.sub;
              }

              function getCategory( main, sub ) {
                var category = categories[ main + sub ];
                if ( !category ) {
                  category = new Category( main, sub );
                  categories[ main + sub ] = category;
                }

                return category;
              }

              return {
                getCategory: getCategory
              }
            }
          ]);

      return service;
    }
);

