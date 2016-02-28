# co.flocode.cordova.fabric

This plugin provides a bridge between javascript error handling and [Fabric](https://www.fabric.io/) server-side
client API. It is based heavily on the initial work done by [smistry-toushay](https://github.com/smistry-toushay/cordova-crashlytics-plugin) and [4sh-projects](https://github.com/4sh-projects/cordova-crashlytics-plugin).

## Installation

```sh
cordova plugin add https://github.com/lookitsatravis/cordova-fabric \
  --variable FABRIC_API_SECRET=<YOUR FABRIC API SECRET HERE>
  --variable FABRIC_API_KEY=<YOUR FABRIC API KEY HERE> \
  --variable DIGITS_KEY=<YOUR DIGITS API KEY HERE> \
  --variable DIGITS_SECRET=<YOUR DIGITS SECRET KEY HERE>
```

## Features

* Crashlytics interface
* Digits Interface
* iOS Support
* Android Support

## Crashlytics

Plugin provides a `navigator.fabric.crashlytics` object with following methods:
- `logException(string)` : Sends an exception (non fatal) to the Crashlytics backend
- `log(string)` : Sends a standard log message (non fatal) to the Crashlytics backend
- `log(errorLevel, tag, msg)` (Android only)
- `setBool(key, value)`
- `setDouble(key, value)`
- `setFloat(key, value)`
- `setInt(key, value)`
- `setLong(key, value)`
- `setString(key, value)`
- `setUserEmail(email)`
- `setUserIdentifier(userId)`
- `setUserName(userName)`
- `crash(string)`

## Digits

Plugin provides a `navigator.fabric.digits` object with the following methods:
- `logIn(options)` : Takes an object literal `options` for customizing the Digits login UI. Options:
  - `backgroundColor` - Takes a string hex color value. *Do not include the "#"*
  - `accentColor` - Takes a string hex color value. *Do not include the "#"*
  - `headerFont` - Takes a string font name. This is the name as recognized by iOS. It is not necessarily the same as the name of the font resource that you added to the project. Learn about adding fonts to iOS [here](http://codewithchris.com/common-mistakes-with-adding-custom-fonts-to-your-ios-app/).
  - `headerFontSize` - Number representing the font size
  - `labelFont` - Takes a string font name. This is the name as recognized by iOS. It is not necessarily the same as the name of the font resource that you added to the project. Learn about adding fonts to iOS [here](http://codewithchris.com/common-mistakes-with-adding-custom-fonts-to-your-ios-app/).
  - `labelFontSize` - Number representing the font size
  - `bodyFont` - Takes a string font name. This value is the name as recognized by iOS. It is not necessarily the same as the name of the font resource that you added to the project. Learn about adding fonts to iOS [here](http://codewithchris.com/common-mistakes-with-adding-custom-fonts-to-your-ios-app/).
  - `bodyFontSize` - Number representing the font size
  - `logoImage` - Takes a string representing the name of the image resource you want to use. I suggest putting the logo you want in `www/img` and then setting this value to `www/img/my-logo.png`.

### AngularJS integration (For use with Ionic framework)

Use the following snippet to integrate the plugin in your AngularJS app gracefully :

```js
var module = angular.module("my-module", []);

module.config(['$provide', function($provide) {
  $provide.decorator("$exceptionHandler", ['$delegate', function($delegate) {
    return function(exception, cause) {
      $delegate(exception, cause);

      if(navigator.fabric) {
        // Here, I rely on stacktrace-js (http://www.stacktracejs.com/) to format
        // exception stacktraces before sending it to the native bridge

        var message = exception.toString();
        var stacktrace = $window.printStackTrace({e: exception});
        var errorMessage = "ERROR: " + message + ", stacktrace: " + stacktrace;

        navigator.fabric.crashlytics.logException(errorMessage);

        // You may also want to crash the app because of a JS error. This is because
        // logException on iOS does not cause a crash, and so the error is not
        // sent to Fabric.
        navigator.fabric.crashlytics.crash();
      }
    };
  }]);
}]);
```
