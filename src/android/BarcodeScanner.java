package com.jmmateos.plugins.BarcodeScanner;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ClipData;
import android.content.ContentResolver;
import android.webkit.MimeTypeMap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
// Cordova-required packages
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class BarcodeScanner extends CordovaPlugin {
    private static final String TAG = "V710BarcodeScanner";
    private static final String V710 = "V710";
    private static final String SEYPOS = "SEYPOS";
    private static final String AVAILABLE = "available";
    private static final String SCANNING = "registerScanningReceiver";
    private static final String SCANDATA = "registerScanDataReceiver";
    private static final String NOTSCANNING = "unregisterScanningReceiver";
    private static final String NOTSCANDATA = "unregisterScanDataReceiver";    
    private Map<String, BroadcastReceiver> broadcastReceiver = new HashMap<>();
    private Map<String, CallbackContext> onBroadcastCallbackContext = new HashMap<>();
  
    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) {
            if(action.equals(AVAILABLE)) {
                return this.available(callbackContext);
            } else if(action.equals(SCANNING)) {
                final String actionIntent =  "shmaker.android.intent.action.SCANER_SCANNING";
                return this.registerBroadcastReceiver(callbackContext, actionIntent);
            }
            else if(action.equals(SCANDATA)) {
                final String actionIntent =  "shmaker.android.intent.action.SCANER_DECODE_DATA";
                return this.registerBroadcastReceiver(callbackContext, actionIntent);
            }
            if(action.equals(NOTSCANNING)) {
                final String actionIntent =  "shmaker.android.intent.action.SCANER_SCANNING";
                return this.unregisterBroadcastReceiver(actionIntent);
            }
            else if(action.equals(NOTSCANDATA)) {
                final String actionIntent =  "shmaker.android.intent.action.SCANER_DECODE_DATA";
                return this.unregisterBroadcastReceiver(actionIntent);
            }
            else {
                callbackContext.error("Incorrect action parameter: " + action);
            }

            return false;
    }

    private boolean available(CallbackContext callbackContext) {
        if (android.os.Build.MANUFACTURER.equals(V710)) {
            Log.d(TAG, "available V710: " + android.os.Build.MANUFACTURER);
            PluginResult result = new PluginResult(PluginResult.Status.OK, true);
            callbackContext.sendPluginResult(result);
            return true;
        } else if (android.os.Build.MANUFACTURER.equals(SEYPOS)) {
            Log.d(TAG, "available SEYPOS: " + android.os.Build.MANUFACTURER);
            PluginResult result = new PluginResult(PluginResult.Status.OK, true);
            callbackContext.sendPluginResult(result);
            return true;
        } else {
            Log.d(TAG, "not available barcode scanner SE4710 is " + android.os.Build.MANUFACTURER);
            PluginResult result = new PluginResult(PluginResult.Status.OK, false);
            callbackContext.sendPluginResult(result);
            return false;
        }
    }

    private boolean registerBroadcastReceiver(CallbackContext callbackContext, String action) {
        BroadcastReceiver bcReceiver = this.broadcastReceiver.get(action);
        try
        {
            this.cordova.getActivity().unregisterReceiver(bcReceiver);
        }
        catch (IllegalArgumentException e) {}   

        this.onBroadcastCallbackContext.put(action, callbackContext);

        PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
        result.setKeepCallback(true);

        IntentFilter filter = new IntentFilter();
        Log.d(TAG, "Registering broadcast receiver for filter: " + action);
        filter.addAction(action);

        bcReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                Log.d(TAG, "Broadcast call for filter: " + action);
                CallbackContext callback = onBroadcastCallbackContext.get(action);
                if ( callback != null)
                {
                    Log.d(TAG, "Callback found for filter: " + action);
                    PluginResult result = new PluginResult(PluginResult.Status.OK, getIntentJson(intent));
                    result.setKeepCallback(true);
                    callback.sendPluginResult(result);
                } else {
                    Log.d(TAG, "Callback not found for filter: " + action);
                }
            }
        };

        this.broadcastReceiver.put(action, bcReceiver);

        this.cordova.getActivity().registerReceiver(bcReceiver, filter);

        callbackContext.sendPluginResult(result);

        return true;
    }

    private boolean unregisterBroadcastReceiver(String action) {

        try
        {
            BroadcastReceiver bcReceiver = this.broadcastReceiver.get(action);
            this.cordova.getActivity().unregisterReceiver(bcReceiver);
            Log.d(TAG, "Unregister broadcast for filter: " + action);
        }
        catch (IllegalArgumentException e) {
            Log.d(TAG, "Error unregister broadcast for filter: " + action);
        }    
        return true;

    }

      /**
     * Return JSON representation of intent attributes
     *
     * @param intent
     * Credit: https://github.com/napolitano/cordova-plugin-intent
     */
    private JSONObject getIntentJson(Intent intent) {
        JSONObject intentJSON = null;
        ClipData clipData = null;
        JSONObject[] items = null;
        ContentResolver cR = this.cordova.getActivity().getApplicationContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            clipData = intent.getClipData();
            if(clipData != null) {
                int clipItemCount = clipData.getItemCount();
                items = new JSONObject[clipItemCount];

                for (int i = 0; i < clipItemCount; i++) {

                    ClipData.Item item = clipData.getItemAt(i);

                    try {
                        items[i] = new JSONObject();
                        items[i].put("htmlText", item.getHtmlText());
                        items[i].put("intent", item.getIntent());
                        items[i].put("text", item.getText());
                        items[i].put("uri", item.getUri());

                        if (item.getUri() != null) {
                            String type = cR.getType(item.getUri());
                            String extension = mime.getExtensionFromMimeType(cR.getType(item.getUri()));

                            items[i].put("type", type);
                            items[i].put("extension", extension);
                        }

                    } catch (JSONException e) {
                        Log.d(TAG, " Error thrown during intent > JSON conversion");
                        Log.d(TAG, e.getMessage());
                        Log.d(TAG, Arrays.toString(e.getStackTrace()));
                    }

                }
            }
        }

        try {
            intentJSON = new JSONObject();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if(items != null) {
                    intentJSON.put("clipItems", new JSONArray(items));
                }
            }

            intentJSON.put("type", intent.getType());
            intentJSON.put("extras", toJsonObject(intent.getExtras()));
            intentJSON.put("action", intent.getAction());
            intentJSON.put("categories", intent.getCategories());
            intentJSON.put("flags", intent.getFlags());
            intentJSON.put("component", intent.getComponent());
            intentJSON.put("data", intent.getData());
            intentJSON.put("package", intent.getPackage());

            return intentJSON;
        } catch (JSONException e) {
            Log.d(TAG, " Error thrown during intent > JSON conversion");
            Log.d(TAG, e.getMessage());
            Log.d(TAG, Arrays.toString(e.getStackTrace()));

            return null;
        }
    }


    private static JSONObject toJsonObject(Bundle bundle) {
        //  Credit: https://github.com/napolitano/cordova-plugin-intent
        try {
            return (JSONObject) toJsonValue(bundle);
        } catch (JSONException e) {
            throw new IllegalArgumentException("Cannot convert bundle to JSON: " + e.getMessage(), e);
        }
    }

    private static Object toJsonValue(final Object value) throws JSONException {
        //  Credit: https://github.com/napolitano/cordova-plugin-intent
        if (value == null) {
            return null;
        } else if (value instanceof Bundle) {
            final Bundle bundle = (Bundle) value;
            final JSONObject result = new JSONObject();
            for (final String key : bundle.keySet()) {
                result.put(key, toJsonValue(bundle.get(key)));
            }
            return result;
        } else if ((value.getClass().isArray())) {
            final JSONArray result = new JSONArray();
            int length = Array.getLength(value);
            for (int i = 0; i < length; ++i) {
                result.put(i, toJsonValue(Array.get(value, i)));
            }
            return result;
        }
        else if (value instanceof ArrayList<?>) {
            final ArrayList arrayList = (ArrayList<?>)value;
            final JSONArray result = new JSONArray();
            for (int i = 0; i < arrayList.size(); i++)
                result.put(toJsonValue(arrayList.get(i)));
            return result;
        }
        else if (
                value instanceof String
                        || value instanceof Boolean
                        || value instanceof Integer
                        || value instanceof Long
                        || value instanceof Double) {
            return value;
        } else {
            return String.valueOf(value);
        }
    }

}