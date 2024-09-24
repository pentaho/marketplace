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

define(
    [
      'marketplaceApp', 'marketplace-lib/Logger'
    ],
    function ( app, logger ) {
      logger.log("Required services/installFlowService/installFlowService.js");

      var installFlowService = app.factory( 'installFlowService',
          [ 'appService', '$uibModal', '$translate',
            function( appService, $modal, $translate ) {

              function onOperationResult ( message , dialog, modalInstance ) {
                dialog.body = message;
                var okButton = dialog.buttons[0];
                okButton.onClick = function() { modalInstance.close(); };
                okButton.disabled = false;
                dialog.buttons.splice( 1, 1); // remove cancel button
              }

              function installPlugin( plugin, version ) {
                newDialogModal( {
                  okAction: function ( ) { return appService.installPlugin( plugin, version); },
                  titleId: 'marketplace.installationDialog.title',
                  bodyId:  'marketplace.installationDialog.confirmation',
                  processingMessageId:  'marketplace.installationDialog.installing',
                  onSuccessMessageId: 'marketplace.installationDialog.success',
                  onFailMessageId: 'marketplace.installationDialog.error',
                  notes: plugin.installationNotes,
                  plugin: plugin
                });
              }

              function updatePlugin( plugin, version ) {
                newDialogModal( {
                  okAction: function ( ) { return appService.installPlugin( plugin, version); },
                  titleId: 'marketplace.updateDialog.title',
                  bodyId: 'marketplace.updateDialog.confirmation',
                  processingMessageId: 'marketplace.updateDialog.installing',
                  onSuccessMessageId: 'marketplace.updateDialog.success',
                  onFailMessageId: 'marketplace.updateDialog.error',
                  plugin: plugin
                });
              }

              function uninstallPlugin( plugin ) {
                if( !plugin.isInstalled ) {
                  return;
                }

                newDialogModal( {
                  okAction: function ( ) { return appService.uninstallPlugin( plugin ); },
                  titleId: 'marketplace.uninstallDialog.title',
                  bodyId: 'marketplace.uninstallDialog.confirmation',
                  processingMessageId: 'marketplace.uninstallDialog.installing',
                  onSuccessMessageId: 'marketplace.uninstallDialog.success',
                  onFailMessageId: 'marketplace.uninstallDialog.error',
                  plugin: plugin
                });
              }


              function newDialogModal ( opts  ) {

                function onOk ( scope, modalInstance ) {
                  scope.dialog.body = $translate.instant( opts.processingMessageId, { pluginName: opts.plugin.name } );
                  var okButton = scope.dialog.buttons[0];
                  var cancelButton = scope.dialog.buttons[1];
                  okButton.disabled = true;
                  cancelButton.disabled = true;
                  // install / update / uninstall plugin
                  opts.okAction().then(
                      function () {
                        var successMessage = $translate.instant( opts.onSuccessMessageId, { pluginName: opts.plugin.name } );
                        scope.dialog.notes = opts.notes;
                        onOperationResult( successMessage, scope.dialog, modalInstance );
                      },
                      function ( error ) {
                        var errorMessage = error.message ? "[ " + error.message + "]" : "";
                        errorMessage = $translate.instant( opts.onFailMessageId, { pluginName: opts.plugin.name } ) + errorMessage;
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
                    title: function() { return $translate.instant( opts.titleId, { pluginName: opts.plugin.name } ); },
                    body: function() { return $translate.instant( opts.bodyId, { pluginName: opts.plugin.name } ); },
                    onOk: function() { return onOk; },
                    onCancel: function() { return onCancel; }
                  },
                  windowClass: "confirmationDialog"
                });

                return dialogModal;
              };

              function ModalInstanceCtrl ( $scope, $uibModalInstance, title, body, onOk, onCancel ) {
                var buttons = [];
                if( onOk ) { buttons.push( { text: $translate.instant( 'marketplace.okButton.text' ), onClick: function() { onOk( $scope, $uibModalInstance ); } } ) };
                if( onCancel ) { buttons.push (  { text: $translate.instant( 'marketplace.cancelButton.text' ), onClick: function() { onCancel( $scope, $uibModalInstance ); } })};

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

