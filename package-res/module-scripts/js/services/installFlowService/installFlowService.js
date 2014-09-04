/*
 * Copyright 2002 - 2014 Webdetails, a Pentaho company.  All rights reserved.
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
      'marketplace'
    ],
    function ( app ) {
      console.log("Required services/installFlowService/installFlowService.js");

      var installFlowService = app.factory( 'installFlowService',
          [ 'appService', '$modal', '$translate',
            function( appService, $modal, $translate ) {

              function onOperationResult ( message , dialog, modalInstance ) {
                dialog.body = message;
                var okButton = dialog.buttons[0];
                okButton.onClick = function() { modalInstance.close(); };
                okButton.disabled = false;
                dialog.buttons.splice( 1, 1); // remove cancel button
              }

              function installPlugin( plugin, version ) {
                newDialogModal(
                    function ( ) { return appService.installPlugin( plugin, version); },
                    'marketplace.installationDialog.title',
                    'marketplace.installationDialog.confirmation',
                    'marketplace.installationDialog.installing',
                    'marketplace.installationDialog.success',
                    'marketplace.installationDialog.error',
                    plugin
                );
              }

              function updatePlugin( plugin, version ) {
                newDialogModal(
                    function ( ) { return appService.installPlugin( plugin, version); },
                    'marketplace.updateDialog.title',
                    'marketplace.updateDialog.confirmation',
                    'marketplace.updateDialog.installing',
                    'marketplace.updateDialog.success',
                    'marketplace.updateDialog.error',
                    plugin
                );
              }

              function uninstallPlugin( plugin ) {
                if( !plugin.isInstalled ) {
                  return;
                }

                newDialogModal(
                    function ( ) { return appService.uninstallPlugin( plugin ); },
                    'marketplace.uninstallDialog.title',
                    'marketplace.uninstallDialog.confirmation',
                    'marketplace.uninstallDialog.installing',
                    'marketplace.uninstallDialog.success',
                    'marketplace.uninstallDialog.error',
                    plugin
                );
              }

              function newDialogModal ( okAction, titleId, bodyId, processingMessageId, onSuccessMessageId, onFailMessageId, plugin  ) {
                function onOk ( scope, modalInstance ) {
                  scope.dialog.body = $translate.instant( processingMessageId, { pluginName: plugin.name } );
                  var okButton = scope.dialog.buttons[0];
                  var cancelButton = scope.dialog.buttons[1];
                  okButton.disabled = true;
                  cancelButton.disabled = true;
                  // install / update / uninstall plugin
                  okAction().then(
                      function () {
                        var successMessage = $translate.instant( onSuccessMessageId, { pluginName: plugin.name } );
                        onOperationResult( successMessage, scope.dialog, modalInstance );
                      },
                      function ( error ) {
                        var errorMessage = $translate.instant( onFailMessageId, { pluginName: plugin.name } ) +
                            "[ " + error.message + "]";
                        onOperationResult( errorMessage, scope.dialog, modalInstance );
                      }
                  );
                }

                function onCancel ( scope, modalInstance ) { modalInstance.close(); }

                var dialogModal = $modal.open( {
                  templateUrl: 'partials/dialogTemplate.html',
                  controller: ModalInstanceCtrl,
                  backdrop: 'static',
                  keyboard: false,
                  resolve: {
                    title: function() { return $translate.instant( titleId, { pluginName: plugin.name } ); },
                    body: function() { return $translate.instant( bodyId, { pluginName: plugin.name } ); },
                    onOk: function() { return onOk; },
                    onCancel: function() { return onCancel; }
                  },
                  windowClass: "confirmationDialog"
                });

                return dialogModal;
              };

              function ModalInstanceCtrl ( $scope, $modalInstance, title, body, onOk, onCancel ) {
                var buttons = [];
                if( onOk ) { buttons.push( { text: $translate.instant( 'marketplace.okButton.text' ), onClick: function() { onOk( $scope, $modalInstance ); } } ) };
                if( onCancel ) { buttons.push (  { text: $translate.instant( 'marketplace.cancelButton.text' ), onClick: function() { onCancel( $scope, $modalInstance ); } })};

                $scope.dialog = {
                  title: title,
                  body: body,
                  buttons: buttons
                };

              };

              return {
                installPlugin: installPlugin,

                updatePlugin: updatePlugin,

                uninstallPlugin: uninstallPlugin
              }

            }
          ]);

      return installFlowService;
    }
);

