var path = require('path');

module.exports = {
    getPluginConfig: function(platform) {
        var pluginConfig = require(path.join('..', '..', '..', platform + '.json'));

        return {
            apiKey: pluginConfig.installed_plugins['co.flocode.cordova.fabric'].FABRIC_API_KEY,
            apiSecret: pluginConfig.installed_plugins['co.flocode.cordova.fabric'].FABRIC_API_SECRET,
            digitsKey: pluginConfig.installed_plugins['co.flocode.cordova.fabric'].DIGITS_KEY,
            digitsSecret: pluginConfig.installed_plugins['co.flocode.cordova.fabric'].DIGITS_SECRET,
        };
    }
};
