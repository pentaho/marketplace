/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

'use strict';

define([
  'marketplaceApp',
  'underscore',
  'marketplace-lib/Logger'
], function(app, _, logger) {
  logger.log("Required services/categoryService.js");

  return app.factory('categoryService', ['$translate', function($translate) {
    var TRANSLATION_PLACEHOLDER = '{0}';
    var TRANSLATION_ID_PATTERN = 'marketplace.categories.' + TRANSLATION_PLACEHOLDER + '.name';

    var categories = {};

    function Category(main, sub) {
      this.main = main;
      this.mainName = main;

      translateMainName.call(this);

      if (sub) {
        this.sub = sub;
        this.subName = sub;

        translateSubName.call(this);
      }
    }

    Category.prototype.getId = function() {
      return this.main + this.sub;
    };

    function getCategory(main, sub) {
      var categoryId = main + sub;

      var category = categories[categoryId];
      if (!category) {
        category = new Category(main, sub);

        categories[categoryId] = category;
      }

      return category;
    }

    /** @private */
    function buildTranslationId(category) {
      return TRANSLATION_ID_PATTERN.replace(TRANSLATION_PLACEHOLDER,
        category.toLowerCase().replace(/ /g,"_"));
    }

    /** @private */
    function translateMainName() {
      var me = this;

      me.mainTranslateId = buildTranslationId(me.main);

      return $translate(me.mainTranslateId).then(function(translatedName) {
        if (translatedName !== me.mainTranslateId) {
          me.mainName = translatedName;
        }
      }).catch(handleTranslationError);
    }

    /** @private */
    function translateSubName() {
      var me = this;

      me.subTranslateId = buildTranslationId(me.sub);

      return $translate(me.subTranslateId).then(function(translatedName) {
        if (translatedName !== me.subTranslateId) {
          me.subName = translatedName;
        }
      }).catch(handleTranslationError);
    }

    /** @private */
    function handleTranslationError(error) {
      if (error != null) {
        logger.error(error)
      }
    }

    return {
      getCategory: getCategory
    }
  }]);
});
