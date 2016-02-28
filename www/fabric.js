var exec = require('cordova/exec');

var getPromisedCordovaExec = function (command, success, fail, arguments) {
  var toReturn, deferred, injector, $q;
  if (!success || success === undefined) {
    if (window.jQuery) {
      deferred = jQuery.Deferred();
      success = deferred.resolve;
      fail = deferred.reject;
      toReturn = deferred;
    } else if (window.angular) {
      injector = angular.injector(["ng"]);
      $q = injector.get("$q");
      deferred = $q.defer();
      success = deferred.resolve;
      fail = deferred.reject;
      toReturn = deferred.promise;
    } else if (window.Promise) {
      toReturn = new Promise(function(c, e) {
        success = c;
        fail = e;
      });
    } else if (window.WinJS && window.WinJS.Promise) {
      toReturn = new WinJS.Promise(function(c, e) {
        success = c;
        fail = e;
      });
    } else {
      return console.error('Fabric either needs a success callback, or jQuery/AngularJS/Promise/WinJS.Promise defined for using promises');
    }
  }

  cordova.exec(success, fail, "Fabric", command, arguments);

  return toReturn;
};

var Fabric = function() {
  this.crashlytics = new Crashlytics();
  this.digits = new Digits();
}

var Crashlytics = function() {
  var methods = [
    'logException', 'log', 'setBool', 'setDouble', 'setFloat', 'setInt',
    'setLong', 'setString', 'setUserEmail', 'setUserIdentifier',
    'setUserName', 'crash'
  ];

  var self = this;
  for(var i = 0; i < methods.length; i++) {
    (function(index){
      var currentMethod = methods[index];
      self[currentMethod] = function(success, fail){
        return getPromisedCordovaExec(currentMethod, null, null, Array.prototype.slice.call(arguments));
      };
    })(i);
  }

  this.LOG_LEVELS = {
    VERBOSE: 2,
    DEBUG: 3,
    INFO: 4,
    WARN: 5,
    ERROR: 6,
    ASSERT: 7
  };
};

var Digits = function() {
  this.logIn = function(options) {
    return getPromisedCordovaExec('logIn', null, null, [options]);
  };
  this.logOut = function() {
    return getPromisedCordovaExec('logOut');
  }
}

var fabric = new Fabric();

module.exports = fabric;
