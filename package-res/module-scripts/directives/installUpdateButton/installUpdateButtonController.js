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

define( [
      'marketplace' ],
    function ( app ) {
      console.log("Required installUpdateButton/installUpdateButton.js");

      app.controller('installUpdateButtonController',
          ['$scope', 'appService', '$modal',
            function ( $scope , appService, $modal ) {
              var button = {};
              var plugin = $scope.plugin;
              var installedVersion = plugin.installedVersion;

              function installPlugin( plugin, version ) {
                // TODO: i18n
                newDialogModal( "Install Plugin " + plugin.name, "Do you want to proceed?",
                    function( scope, modalInstance ) {
                      scope.dialog.body = "Installing...";
                      var okButton = scope.dialog.buttons[0];
                      var cancelButton = scope.dialog.buttons[1];
                      okButton.disabled = true;
                      cancelButton.disabled = true;
                      appService.installPlugin(plugin, version).then(
                          function () {
                            scope.dialog.body = "Installation successful";
                            okButton.onClick = function() { modalInstance.close(); };
                            okButton.disabled = false;
                            cancelButton.disabled = false;
                          },
                          function () {
                            scope.dialog.body = "Installation Error";
                            okButton.disabled = false;
                            cancelButton.disabled = false;
                          }
                      );
                    },
                    function ( scope, modalInstance ) { modalInstance.close() }
                );
              }

              function updatePlugin( plugin, version ) {
                // TODO: i18n
                newDialogModal( "Update Plugin " + plugin.name, "Do you want to proceed?",
                    function() { appService.installPlugin( plugin, version ); } );
              }

              function noOperation() {}

              function newDialogModal ( title, body, onOk, onCancel ) {
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
                if(onOk) { buttons.push( { text: "Ok", onClick: function() { onOk( $scope, $modalInstance ); } } ) };
                if(onCancel) { buttons.push (  { text: "Cancel", onClick: function() { onCancel( $scope, $modalInstance ); } })};

                $scope.dialog = {
                  title: title,
                  body: body,
                  buttons: buttons
                };

              };

              function updateButton( selectedVersion ) {
                // No version selected to install or update
                if ( !selectedVersion ) {
                  // TODO: i18n
                  button.text = "Invalid selected version";
                  button.disabled = true;
                  button.cssClass = "invalid";
                  button.onClick = noOperation;
                  return;
                }

                if ( plugin.isInstalled ) {
                  if ( selectedVersion.equals( installedVersion ) ) {
                    // TODO i18n
                    button.text = "Reinstall";
                    button.cssClass = "install";
                    button.disabled = false;
                    button.onClick = function () { installPlugin( plugin, selectedVersion ); }
                    return
                  }
                  else if ( selectedVersion.moreRecentThan( installedVersion ) ) {
                    // TODO: i18n
                    button.text = "Update";
                    button.cssClass = "update";
                    button.disabled = false;
                    button.onClick = function () { updatePlugin( plugin, selectedVersion ); }
                    return;
                  }
                }

                // TODO: i18n
                button.text = "Install";
                button.cssClass = "install";
                button.disabled = false;
                button.onClick = function () { installPlugin( plugin, selectedVersion ); };
                return;
              }

              $scope.button = button;
              $scope.$watch( 'selectedVersion', function ( newSelectedVersion ) { updateButton( newSelectedVersion ); } );


            }
          ]);

    }
);