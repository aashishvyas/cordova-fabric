var fs = require('fs');
var path = require('path');

var common = require('./common');

module.exports = {
    createPropertiesFile: function() {
        var pluginConfig = common.getPluginConfig('android');

        var fabricPropertiesPath = this._getFabricPropertiesPath();

        var fabricProperties = '';
        fabricProperties += 'apiKey=' + pluginConfig.apiKey + '\n';
        fabricProperties += 'apiSecret=' + pluginConfig.apiSecret + '\n';
        fabricProperties += 'digitsKey=' + pluginConfig.digitsKey + '\n';
        fabricProperties += 'digitsSecret=' + pluginConfig.digitsSecret + '\n';

        fs.writeFileSync(fabricPropertiesPath, fabricProperties);
    },

    deletePropertiesFile: function() {
        var fabricPropertiesPath = this._getFabricPropertiesPath();

        try {
            fs.unlinkSync(fabricPropertiesPath);
        } catch (e) {}
    },

    addBuildGradleExtras: function() {
        var buildGradle = this._readBuildGradle();

        buildGradle +=  '\n' +
                        '// FABRIC PLUGIN EXTRAS START\n' +
                        'buildscript {\n' +
                        '    repositories {\n' +
                        '        jcenter()\n' +
                        '        maven { url \'https://maven.fabric.io/public\' }\n' +
                        '    }\n' +
                        '    dependencies {\n' +
                        '        classpath \'io.fabric.tools:gradle:1.+\'\n' +
                        '    }\n' +
                        '}\n' +
                        '\n' +
                        'apply plugin: \'io.fabric\'\n' +
                        '\n' +
                        'repositories {\n' +
                        '   jcenter()\n' +
                        '   maven { url \'https://maven.fabric.io/public\' }\n' +
                        '}\n' +
                        '\n' +
                        'dependencies {\n' +
                        '   compile(\'com.crashlytics.sdk.android:crashlytics:2.5.5@aar\') {\n' +
                        '     transitive = true\n' +
                        '   }\n' +
                        '   compile(\'com.digits.sdk.android:digits:1.9.4@aar\') {\n' +
                        '     transitive = true\n' +
                        '   }\n' +
                        '}\n' +
                        '// FABRIC PLUGIN EXTRAS END\n';

        this._writeBuildGradle(buildGradle);
    },

    removeBuildGradleExtras: function() {
        var buildGradle = this._readBuildGradle();

        buildGradle = buildGradle.replace(/\n\/\/ FABRIC PLUGIN EXTRAS START[\s\S]*\/\/ FABRIC PLUGIN EXTRAS END\n/, '');

        this._writeBuildGradle(buildGradle);
    },

    _getFabricPropertiesPath: function() {
        return path.join('platforms', 'android', 'fabric.properties');
    },

    _readBuildGradle: function() {
        return fs.readFileSync(this._getBuildGradlePath(), 'utf-8');
    },

    _writeBuildGradle: function(buildGradle) {
        fs.writeFileSync(this._getBuildGradlePath(), buildGradle);
    },

    _getBuildGradlePath: function() {
        return path.join('platforms', 'android', 'build.gradle');
    }
};
