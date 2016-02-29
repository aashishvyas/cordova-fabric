package co.flocode.cordova.fabric;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;

import java.util.*;
import android.util.Log;
import android.os.Bundle;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import io.fabric.sdk.android.Fabric;
import com.digits.sdk.android.*;
import com.crashlytics.android.Crashlytics;

public class FabricPlugin extends CordovaPlugin {
  volatile DigitsClient digitsClient;
  private static final String META_DATA_KEY = "io.fabric.digits.ConsumerKey";
  private static final String META_DATA_SECRET = "io.fabric.digits.ConsumerSecret";
  private static final String TAG = "CORDOVA FABRIC PLUGIN";

  private AuthCallback authCallback;

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);

    TwitterAuthConfig authConfig = getTwitterConfig();
    Fabric.with(cordova.getActivity().getApplicationContext(), new Crashlytics(), new TwitterCore(authConfig), new Digits());
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    Log.i(TAG, "executing action " + action);

    if ("authenticate".equals(action)) {
      authenticate(args, callbackContext);
    } else if ("logOut".equals(action)) {
      logOut(args, callbackContext);
    } else if ("logException".equals(action)) {
      logException(args, callbackContext);
    }  else if ("log".equals(action)) {
      log(args, callbackContext);
    }  else if ("setBool".equals(action)) {
      setBool(args, callbackContext);
    }  else if ("setDouble".equals(action)) {
      setDouble(args, callbackContext);
    }  else if ("setFloat".equals(action)) {
      setFloat(args, callbackContext);
    }  else if ("setInt".equals(action)) {
      setInt(args, callbackContext);
    }  else if ("setLong".equals(action)) {
      setLong(args, callbackContext);
    } else if ("setString".equals(action)) {
      setString(args, callbackContext);
    } else if ("setUserEmail".equals(action)) {
      setUserEmail(args, callbackContext);
    } else if ("setUserIdentifier".equals(action)) {
      setUserIdentifier(args, callbackContext);
    } else if ("setUserName".equals(action)) {
      setUserName(args, callbackContext);
    } else if ("crash".equals(action)) {
      crash(args, callbackContext);
    } else {
      Log.w(TAG, "unknown action `" + action + "`");
      return false;
    }

    return true;
  }

  public void authenticate(JSONArray args, final CallbackContext callbackContext) {
    authCallback = new AuthCallback() {
      @Override
      public void success(DigitsSession session, String phoneNumber) {
        // Do something with the session and phone number
        Log.i(TAG, "authentication successful");

        TwitterAuthConfig authConfig = TwitterCore.getInstance().getAuthConfig();
        TwitterAuthToken authToken = (TwitterAuthToken) session.getAuthToken();
        DigitsOAuthSigning oauthSigning = new DigitsOAuthSigning(authConfig, authToken);
        Map<String, String> authHeaders = oauthSigning.getOAuthEchoHeadersForVerifyCredentials();

        String result = new JSONObject(authHeaders).toString();
        callbackContext.success(result);
      }

      @Override
      public void failure(DigitsException exception) {
        // Do something on failure
        Log.e(TAG, "error " + exception.getMessage());
        callbackContext.error(exception.getMessage());
      }
    };

    Digits.authenticate(authCallback, cordova.getActivity().getResources().getIdentifier("CustomDigitsTheme", "style", cordova.getActivity().getPackageName()));
  }

  public void logOut(JSONArray args, final CallbackContext callbackContext) {
    Digits.getSessionManager().clearActiveSession();
    callbackContext.success();
  }

  public void logException(JSONArray args, final CallbackContext callbackContext) {
    try {
      Crashlytics.getInstance().core.logException(new RuntimeException(args.getString(0)));
      callbackContext.success();
    } catch (Exception exception) {
      callbackContext.error(exception.getMessage());
    }
  }

  public void log(JSONArray args, final CallbackContext callbackContext) {
    try {
      Crashlytics.getInstance().core.log(args.getInt(0), args.getString(1), args.getString(2));
      callbackContext.success();
    } catch (Exception exception) {
      try {
        Crashlytics.getInstance().core.log(args.getString(0));
        callbackContext.success();
      } catch (Exception exception2) {
        callbackContext.error(exception.getMessage());
      }
    }
  }

  public void setBool(JSONArray args, final CallbackContext callbackContext) {
    try {
      Crashlytics.getInstance().core.setBool(args.getString(0), args.getBoolean(1));
      callbackContext.success();
    } catch (Exception exception) {
      callbackContext.error(exception.getMessage());
    }
  }

  public void setDouble(JSONArray args, final CallbackContext callbackContext) {
    try {
      Crashlytics.getInstance().core.setDouble(args.getString(0), args.getDouble(1));
      callbackContext.success();
    } catch (Exception exception) {
      callbackContext.error(exception.getMessage());
    }
  }

  public void setFloat(JSONArray args, final CallbackContext callbackContext) {
    try {
      Crashlytics.getInstance().core.setFloat(args.getString(0), Float.valueOf(args.getString(1)));
      callbackContext.success();
    } catch (Exception exception) {
      callbackContext.error(exception.getMessage());
    }
  }

  public void setInt(JSONArray args, final CallbackContext callbackContext) {
    try {
      Crashlytics.getInstance().core.setInt(args.getString(0), args.getInt(1));
      callbackContext.success();
    } catch (Exception exception) {
      callbackContext.error(exception.getMessage());
    }
  }

  public void setLong(JSONArray args, final CallbackContext callbackContext) {
    try {
      Crashlytics.getInstance().core.setLong(args.getString(0), args.getLong(1));
      callbackContext.success();
    } catch (Exception exception) {
      callbackContext.error(exception.getMessage());
    }
  }

  public void setString(JSONArray args, final CallbackContext callbackContext) {
    try {
      Crashlytics.getInstance().core.setString(args.getString(0), args.getString(1));
      callbackContext.success();
    } catch (Exception exception) {
      callbackContext.error(exception.getMessage());
    }
  }

  public void setUserEmail(JSONArray args, final CallbackContext callbackContext) {
    try {
      Crashlytics.getInstance().core.setUserEmail(args.getString(0));
      callbackContext.success();
    } catch (Exception exception) {
      callbackContext.error(exception.getMessage());
    }
  }

  public void setUserIdentifier(JSONArray args, final CallbackContext callbackContext) {
    try {
      Crashlytics.getInstance().core.setUserIdentifier(args.getString(0));
      callbackContext.success();
    } catch (Exception exception) {
      callbackContext.error(exception.getMessage());
    }
  }

  public void setUserName(JSONArray args, final CallbackContext callbackContext) {
    try {
      Crashlytics.getInstance().core.setUserName(args.getString(0));
      callbackContext.success();
    } catch (Exception exception) {
      callbackContext.error(exception.getMessage());
    }
  }

  public void crash(JSONArray args, final CallbackContext callbackContext) throws JSONException {
    String message = args.length() == 0 ? "Crashing from Cordova" : args.getString(0);
    throw new RuntimeException(message);
  }

  private TwitterAuthConfig getTwitterConfig() {
    String key = getMetaData(META_DATA_KEY);
    String secret = getMetaData(META_DATA_SECRET);

    return new TwitterAuthConfig(key, secret);
  }

  private String getMetaData(String name) {
    try {
      Context context = cordova.getActivity().getApplicationContext();
      ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);

      Bundle metaData = ai.metaData;
      if(metaData == null) {
        Log.w(TAG, "metaData is null. Unable to get meta data for " + name);
      }
      else {
        String value = metaData.getString(name);
        return value;
      }
    } catch (NameNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }
}
