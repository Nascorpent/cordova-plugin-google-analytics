function callPlugin(name, params, successCallback, errorCallback) {
  cordova.exec(
      successCallback, // Callback de sucesso
      errorCallback,    // Callback de erro'cordovaanalyticsplugin',
      name,
      params
  );
}

exports.initialize = function (successCallback, errorCallback) {
  callPlugin('initialize', [], successCallback, errorCallback);
};

exports.setFirebaseId = function (firebaseId, successCallback, errorCallback) {
  callPlugin('setFirebaseId', [firebaseId], successCallback, errorCallback);
};

exports.setAnalyticsCollectionEnabled = function (enabled, successCallback, errorCallback) {
  callPlugin('setAnalyticsCollectionEnabled', [enabled], successCallback, errorCallback);
};

exports.setUserId = function (userId, successCallback, errorCallback) {
  callPlugin('setUserId', [userId], successCallback, errorCallback);
};

exports.setUserProperty =function (propertyName, propertyValue, successCallback, errorCallback) {
  callPlugin('setUserProperty', [propertyName, propertyValue], successCallback, errorCallback);
};

exports.logEvent = function (eventName, eventParams, successCallback, errorCallback) {
  callPlugin('logEvent', [eventName, eventParams], successCallback, errorCallback);
};

exports.setCurrentScreen = function (screenName, screenNameParam, successCallback, errorCallback) {
  callPlugin('setCurrentScreen', [screenName, screenNameParam], successCallback, errorCallback);
};