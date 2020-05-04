// Empty constructor
function BarcodeScanner() {}

// The function that passes work along to native shells
// Message is a string, duration may be 'long' or 'short'
BarcodeScanner.prototype.available = function(successCallback) {
    cordova.exec(successCallback, null, 'BarcodeScanner', 'available',[]);
};

BarcodeScanner.prototype.registerScanningReceiver = function(successCallback, errorCallback) {
    cordova.exec(function (data) {
        successCallback(data.extras.extra_scanning);
    }, errorCallback, 'BarcodeScanner', 'registerScanningReceiver',[]);
    
};

BarcodeScanner.prototype.registerScanDataReceiver = function(successCallback, errorCallback) {
    cordova.exec(success, errorCallback, 'BarcodeScanner', 'registerScanDataReceiver',[]);
    function success (data) {
        var sendData = {
            prefix: data.extras.extra_decode_data_prefix,
            suffix: data.extras.extra_decode_data_suffix,
            additional: data.extras.extra_decode_data_additional,
            clear: data.extras.extra_decode_data_clear,
            focusInput: data.extras.extra_decode_focus_input,
            data: data.extras.extra_decode_data,
            type: data.extras.extra_decode_type
        };
        successCallback(sendData);
    }        
};

BarcodeScanner.prototype.unregisterScanningReceiver = function() {
    cordova.exec(null, null, 'BarcodeScanner', 'unregisterScanningReceiver',[]);

};

BarcodeScanner.prototype.unregisterScanDataReceiver = function() {
    cordova.exec(null, null, 'BarcodeScanner', 'unregisterScanDataReceiver',[]);
};
  
// Installation constructor that binds BarcodeScanner to window
BarcodeScanner.install = function() {
  if (window.cordova) {
    if (!window.cordova.plugins) {
        window.cordova.plugins = {};
      }
      window.cordova.plugins.v710BarcodeScanner = new BarcodeScanner();
      return window.cordova.plugins.v710BarcodeScanner;
  } else { return null; }
};
cordova.addConstructor(BarcodeScanner.install);