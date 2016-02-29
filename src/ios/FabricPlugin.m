#import <Fabric/Fabric.h>
#import <Crashlytics/Crashlytics.h>
#import <DigitsKit/DigitsKit.h>

#import "FabricPlugin.h"

@interface FabricPlugin ()

@end

@implementation FabricPlugin

+ (UIColor *)colorFromHexString:(NSString *)hexString {
    unsigned rgbValue = 0;

    NSScanner *scanner = [NSScanner scannerWithString:hexString];
    [scanner setScanLocation:1];
    [scanner scanHexInt:&rgbValue];

    return [UIColor colorWithRed:((rgbValue & 0xFF0000) >> 16) / 255.0
                           green:((rgbValue & 0xFF00) >> 8) / 255.0
                            blue:(rgbValue & 0xFF) / 255.0
                           alpha:1.0];
}

#pragma mark - Initializers

- (void)pluginInitialize {
    [super pluginInitialize];

    [Fabric with:@[[Crashlytics class], [Digits class]]];
}

- (void)logException:(CDVInvokedUrlCommand *)command {
    [self log:command];
}

- (void)log:(CDVInvokedUrlCommand *)command {
    CLS_LOG(@"%@", command.arguments[0]);

    [self resultOK:command];
}

- (void)setBool:(CDVInvokedUrlCommand *)command {
    [CrashlyticsKit setBoolValue:((NSNumber*)command.arguments[1]).boolValue forKey:(NSString *)command.arguments[0]];

    [self resultOK:command];
}

- (void)setDouble:(CDVInvokedUrlCommand *)command {
    [self setFloat:command];
}

- (void)setFloat:(CDVInvokedUrlCommand *)command {
    [CrashlyticsKit setFloatValue:((NSNumber*)command.arguments[1]).floatValue forKey:(NSString *)command.arguments[0]];

    [self resultOK:command];
}

- (void)setInt:(CDVInvokedUrlCommand *)command {
    [CrashlyticsKit setIntValue:((NSNumber*)command.arguments[1]).intValue forKey:(NSString *)command.arguments[0]];

    [self resultOK:command];
}

- (void)setLong:(CDVInvokedUrlCommand *)command {
    [self setInt:command];
}

- (void)setString:(CDVInvokedUrlCommand *)command {
    [CrashlyticsKit setObjectValue:(NSString *)command.arguments[1] forKey:(NSString *)command.arguments[0]];

    [self resultOK:command];
}

- (void)setUserEmail:(CDVInvokedUrlCommand *)command {
    [CrashlyticsKit setUserEmail:(NSString *)command.arguments[0]];

    [self resultOK:command];
}

- (void)setUserIdentifier:(CDVInvokedUrlCommand *)command {
    [CrashlyticsKit setUserIdentifier:(NSString *)command.arguments[0]];

    [self resultOK:command];
}

- (void)setUserName:(CDVInvokedUrlCommand *)command {
    [CrashlyticsKit setUserName:(NSString *)command.arguments[0]];

    [self resultOK:command];
}

- (void)crash:(CDVInvokedUrlCommand *)command {
    if (command.arguments.count == 0) {
        [[Crashlytics sharedInstance] crash];
    } else {
        [NSException raise:@"Simulated Crash" format:@"%@", command.arguments[0]];
    }

    [self resultOK:command];
}

- (void)resultOK:(CDVInvokedUrlCommand *)command {
    CDVPluginResult* res = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:res callbackId:command.callbackId];
}

- (void)authenticate:(CDVInvokedUrlCommand *)command {
    NSLog(@"FabricPlugin: Starting Digits authenticate...");

    NSDictionary *options = [command argumentAtIndex:0];

    Digits *digits = [Digits sharedInstance];

    DGTAuthenticationConfiguration *configuration = [[DGTAuthenticationConfiguration alloc] initWithAccountFields:DGTAccountFieldsDefaultOptionMask];
    configuration.appearance = [[DGTAppearance alloc] init];

    if ([options objectForKey:@"backgroundColor"]) { configuration.appearance.backgroundColor = [FabricPlugin colorFromHexString:[options objectForKey:@"backgroundColor"]]; }
    if ([options objectForKey:@"accentColor"]) { configuration.appearance.accentColor = [FabricPlugin colorFromHexString:[options objectForKey:@"accentColor"]]; }

    if ([options objectForKey:@"headerFont"]) {
        NSInteger defaultHeaderSize = 18;
        NSInteger fontSize = ([options objectForKey:@"headerFontSize"]) ? [[options objectForKey:@"headerFontSize"] integerValue] : defaultHeaderSize;
        configuration.appearance.headerFont = [UIFont fontWithName:[options objectForKey:@"headerFont"] size: fontSize];
    }

    if ([options objectForKey:@"labelFont"]) {
        NSInteger defaultHeaderSize = 18;
        NSInteger fontSize = ([options objectForKey:@"labelFontSize"]) ? [[options objectForKey:@"labelFontSize"] integerValue] : defaultHeaderSize;
        configuration.appearance.labelFont = [UIFont fontWithName:[options objectForKey:@"labelFont"] size: fontSize];
    }

    if ([options objectForKey:@"bodyFont"]) {
        NSInteger defaultHeaderSize = 18;
        NSInteger fontSize = ([options objectForKey:@"bodyFontSize"]) ? [[options objectForKey:@"bodyFontSize"] integerValue] : defaultHeaderSize;
        configuration.appearance.bodyFont = [UIFont fontWithName:[options objectForKey:@"bodyFont"] size:fontSize];
    }

    if ([options objectForKey:@"logoImage"]) { configuration.appearance.logoImage = [UIImage imageNamed:[options objectForKey:@"logoImage"]]; }

    [[Digits sharedInstance] authenticateWithViewController:nil configuration:configuration completion:^(DGTSession *session, NSError *error) {

        if(error) {
            NSLog(@"FabricPlugin: Digits authenticate error!");
            CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:error.localizedDescription];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        } else {
            NSLog(@"FabricPlugin: Digits authenticate success!");
            DGTOAuthSigning *oauthSigning = [[DGTOAuthSigning alloc] initWithAuthConfig:digits.authConfig authSession:digits.session];
            NSMutableDictionary *results = [NSMutableDictionary dictionary];
            NSDictionary *authHeaders = [oauthSigning OAuthEchoHeadersToVerifyCredentials];

            [results setValue:session.emailAddress forKey:@"email"];
            [results setValue:[NSNumber numberWithBool:session.emailAddressIsVerified] forKey:@"emailVerified"];
            [results setValue:session.phoneNumber forKey:@"phoneNumber"];
            [results setValue:session.userID forKey:@"userId"];
            [results setValue:authHeaders forKey:@"authData"];

            CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:results];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }
    }];
}

- (void)logOut:(CDVInvokedUrlCommand *)command {
    NSLog(@"FabricPlugin: Starting Digits log out...");
    Digits *digits = [Digits sharedInstance];
    [digits logOut];
    NSLog(@"FabricPlugin: Digits Log out success!");
    [self resultOK:command];
}

@end
