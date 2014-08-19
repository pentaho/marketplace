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
          [ 'appService', '$modal',
            function( appService, $modal ) {

              function onOperationResult ( message, dialog, modalInstance ) {
                dialog.body = message;
                var okButton = dialog.buttons[0];
                okButton.onClick = function() { modalInstance.close(); };
                okButton.disabled = false;
                dialog.buttons.splice( 1, 1); // remove cancel button
              }

              function installPlugin( plugin, version ) {
                // TODO: i18n
                newDialogModal(
                    function ( ) { return appService.installPlugin( plugin, version); },
                    "Install Plugin " + plugin.name,
                    "Do you want to proceed?",
                    "Installing...",
                    "Installation successful",
                    "Installation Error"
                );
              }

              function updatePlugin( plugin, version ) {
                // TODO: i18n
                newDialogModal(
                    function ( ) { return appService.installPlugin( plugin, version); },
                    "Update Plugin " + plugin.name,
                    "Do you want to proceed?",
                    "Updating...",
                    "Plugin updated successfully",
                    "Error occurred when updating plugin"
                );
              }

              function uninstallPlugin( plugin ) {
                if( !plugin.isInstalled ) {
                  return;
                }

                // TODO: i18n
                newDialogModal(
                    function ( ) { return appService.uninstallPlugin( plugin ); },
                    "Uninstall Plugin " + plugin.name,
                    "Do you want to proceed?",
                    "Uninstalling...",
                    "Plugin uninstalled successfully",
                    "Error occurred when uninstalling plugin"
                );
              }


              function newDialogModal ( okAction, title, body, processingMessage, onSuccessMessage, onFailMessage  ) {
                function onOk ( scope, modalInstance ) {
                  scope.dialog.body = processingMessage;
                  var okButton = scope.dialog.buttons[0];
                  var cancelButton = scope.dialog.buttons[1];
                  okButton.disabled = true;
                  cancelButton.disabled = true;
                  // install / update / uninstall plugin
                  okAction().then(
                      // TODO: i18n
                      function () { onOperationResult( onSuccessMessage, scope.dialog, modalInstance ) },
                      // TODO: i18n
                      function () { onOperationResult( onFailMessage, scope.dialog, modalInstance ) }
                  );
                }

                function onCancel ( scope, modalInstance ) { modalInstance.close() }

                var dialogModal = $modal.open( {
                  templateUrl: 'partials/dialogTemplate.html',
                  controller: ModalInstanceCtrl,
                  backdrop: 'static',
                  resolve: {
                    title: function() { return title; },
                    body: function() { return body; },
                    onOk: function() { return onOk; },
                    onCancel: function() { return onCancel; }
                  },
                  //windowClass: "pentaho-dialog"
                });

                return dialogModal;
              };

              function ModalInstanceCtrl ( $scope, $modalInstance, title, body, onOk, onCancel ) {
                var buttons = [];
                // TODO: i18n
                if( onOk ) { buttons.push( { text: "Ok", onClick: function() { onOk( $scope, $modalInstance ); } } ) };
                if( onCancel ) { buttons.push (  { text: "Cancel", onClick: function() { onCancel( $scope, $modalInstance ); } })};

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

