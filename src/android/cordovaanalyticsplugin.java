package com.nascorpent.cordovaanalyticsplugin;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.firebase.analytics.FirebaseAnalytics;

public class cordovaanalyticsplugin extends CordovaPlugin {

    private String firebaseId;

    private FirebaseAnalytics mFirebaseAnalytics;
    
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        switch (action) {
            case "initialize":
                this.initialize(callbackContext);
                return true;
            case "setFirebaseId":
                this.firebaseId = args.getString(0);
                callbackContext.success();
                return true;    
            case "setAnalyticsCollectionEnabled":
                boolean enabled = args.getBoolean(0);
                mFirebaseAnalytics.setAnalyticsCollectionEnabled(enabled);
                callbackContext.success();
                return true;
            case "setUserId":
                String userId = args.getString(0);
                this.setUserId(userId, callbackContext);
                return true;
            case "setUserProperty":
                String propertyName = args.getString(0);
                String propertyValue = args.getString(1);
                this.setUserProperty(propertyName, propertyValue, callbackContext);
                return true;
            case "logEvent":
                String eventName = args.getString(0);
                JSONArray eventParams = args.getJSONArray(1); // Corrigido: obter JSONArray
                this.logEvent(eventName, eventParams, callbackContext); // Corrigido: passar JSONArray
                return true;
            case "setCurrentScreen":
                String screenName = args.getString(0);
                this.setCurrentScreen(screenName, args, callbackContext); // Corrigido: passar JSONArray
                return true;
            default:
                return false;
        }
    }

    private void initialize(CallbackContext callbackContext) {
        if (firebaseId == null || firebaseId.isEmpty()) {callbackContext.error("Firebase ID não definido.");
        return;
        } else {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(cordova.getActivity());
            callbackContext.success(); // Indica que a inicialização foibem-sucedida
        }
    }

    private void setUserId(String userId, CallbackContext callbackContext) {
        mFirebaseAnalytics.setUserId(userId);
        callbackContext.success();
    }

    private void setUserProperty(String propertyName, String propertyValue, CallbackContext callbackContext) {
        mFirebaseAnalytics.setUserProperty(propertyName, propertyValue);
        callbackContext.success();
    }

    private void logEvent(String eventName, JSONArray args, CallbackContext callbackContext) throws JSONException {
        String identifier = args.getString(0);
        String description = args.getString(1);
        long timestamp = System.currentTimeMillis(); // Obter timestamp atual

        Bundle bundle = new Bundle();
        bundle.putString("identifier", identifier);
        bundle.putString("description", description);
        bundle.putLong("timestamp", timestamp);

        mFirebaseAnalytics.logEvent(eventName, bundle);
        callbackContext.success();
    }

    private void setCurrentScreen(String screenName, JSONArray args, CallbackContext callbackContext) throws JSONException {
        String screenNameParam = args.getString(1);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenNameParam); // Usar screenNameParam como nome da tela
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenName); // Manter screenName como classe da tela

        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
        callbackContext.success();
    }
}