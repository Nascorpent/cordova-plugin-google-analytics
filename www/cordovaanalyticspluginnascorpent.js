function callPlugin(name, params, successCallback, errorCallback) {
  cordova.exec(
      successCallback, 
      errorCallback,   
      'cordovaanalyticspluginnascorpent', 
      name,
      params
  );
}

exports.setUserId = function(userId, successCallback, errorCallback) {
  callPlugin('setUserId', [userId], successCallback, errorCallback);
};

exports.setUserProperty = function(propertyName, propertyValue, successCallback, errorCallback) {
  callPlugin('setUserProperty', [propertyName, propertyValue], successCallback, errorCallback);
};

exports.logEvent = function(eventName, eventParam, successCallback, errorCallback) {
  callPlugin('logEvent', [eventName, eventParam], successCallback, errorCallback);
};

exports.setCurrentScreen = function(screenName, screenClassOverride, successCallback, errorCallback) {
  callPlugin('setCurrentScreen', [screenName, screenClassOverride], successCallback, errorCallback);
};

exports.resetAnalyticsData = function(successCallback, errorCallback) {
  callPlugin('resetAnalyticsData', [], successCallback, errorCallback);
};

exports.setAnalyticsCollectionEnabled = function(enabled, successCallback, errorCallback) {
  callPlugin('setAnalyticsCollectionEnabled', [enabled], successCallback, errorCallback);
};

exports.setDefaultEventParameters = function(eventParams, successCallback, errorCallback) {
  callPlugin('setDefaultEventParameters', [eventParams], successCallback, errorCallback);
};
