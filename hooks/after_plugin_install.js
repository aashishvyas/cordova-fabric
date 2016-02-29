var ios = require('./lib/ios');

module.exports = function(context) {
    var platforms = context.opts.cordova.platforms;

    console.log("cordova-fabric: Running after_plugin_install...");

    if (platforms.indexOf('ios') !== -1) {
        var xcodeProjectPath = ios.getXcodeProjectPath(context);

        console.log("cordova-fabric: Removing shell build phase...");
        return ios.removeShellScriptBuildPhase(context, xcodeProjectPath).then(function() {
            console.log("cordova-fabric: Added shell build phase...");
            ios.addShellScriptBuildPhase(context, xcodeProjectPath);
        });
    }
};
