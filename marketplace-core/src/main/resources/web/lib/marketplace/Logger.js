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


/**
 * Logger module.
 * Require as marketplace/Logger
 *
 * @class Logger
 * @module Logger
 */

define(function() {

  var logger = {

    /**
     *  Property enumerating the various log levels
     *  @property logLevels
     *  @type Array
     */
    loglevels: ['debug', 'log', 'info', 'warn', 'error', 'exception'],


    /**
     *  Current log level. Assign a new value to this property to change the log level
     *  @property logLevel
     *  @type string
     */
    loglevel: 'debug',

    /**
     *
     * Logs a message at the specified log level
     *
     * @method log
     * @param m Message to log
     * @param type Log level. One of debug, info, warn, error or exception
     * @param css CSS styling rules for the message to log
     */
    log: function(m, type, css) {    
      type = type || "info";
      if(this.loglevels.indexOf(type) < this.loglevels.indexOf(this.loglevel)) {
        return;
      }
      if(typeof console !== "undefined") {

        if(!console[type]) {
          if(type === 'exception') {
            type = "error";
            m = m.stack || m;
          } else {
            type = "log";
          }
        }
        if(css) {
          try {
            console[type]("%c[" + type + "] WD: " + m, css);
            return;
          } catch(e) {
            // styling is not supported
          }
        }
        console[type]("[" + type + "] WD: " + m);
      }
    },

    /**
     * Logs a message at debug level
     * @method debug
     * @param m Message to log
     */
    debug: function(m) {
      return this.log(m, "debug");
    },
    /**
     * Logs a message at info level
     * @method info
     * @param m Message to log
     */

    info: function(m) {
      return this.log(m, "info");
    },

    /**
     * Logs a message at warn level
     * @method warn
     * @param m Message to log
     */
    warn: function(m) {
      return this.log(m, "warn");
    },

    /**
     * Logs a message at error level
     * @method error
     * @param m Message to log
     */
    error: function(m) {
      return this.log(m, "error");
    },

    /**
     * Logs a message at exception level
     * @method exception
     * @param m Message to log
     */
    exception: function(m) {
      return this.log(m, "exception");
    }                       
  };

  return logger;

});
