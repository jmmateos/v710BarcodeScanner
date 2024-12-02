// Empty constructor
function BarcodeScannerSE4710() {}

// The function that passes work along to native shells
// Message is a string, duration may be 'long' or 'short'
BarcodeScannerSE4710.prototype.available = function(successCallback) {
    cordova.exec(successCallback, null, 'BarcodeScannerSE4710', 'available',[]);
};

BarcodeScannerSE4710.prototype.registerScanningReceiver = function(successCallback, errorCallback) {
    cordova.exec(function (data) {
        successCallback(data.extras.extra_scanning);
    }, errorCallback, 'BarcodeScannerSE4710', 'registerScanningReceiver',[]);
    
};

BarcodeScannerSE4710.prototype.registerScanDataReceiver = function(successCallback, errorCallback) {
    cordova.exec(success, errorCallback, 'BarcodeScannerSE4710', 'registerScanDataReceiver',[]);
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

BarcodeScannerSE4710.prototype.unregisterScanningReceiver = function() {
    cordova.exec(null, null, 'BarcodeScannerSE4710', 'unregisterScanningReceiver',[]);

};

BarcodeScannerSE4710.prototype.unregisterScanDataReceiver = function() {
    cordova.exec(null, null, 'BarcodeScannerSE4710', 'unregisterScanDataReceiver',[]);
};
  
// Installation constructor that binds BarcodeScannerSE4710 to window
BarcodeScannerSE4710.install = function() {
  if (window.cordova) {
    if (!window.cordova.plugins) {
        window.cordova.plugins = {};
      }
      window.cordova.plugins.v710BarcodeScanner = new BarcodeScannerSE4710();
      return window.cordova.plugins.v710BarcodeScanner;
  } else { return null; }
};
cordova.addConstructor(BarcodeScannerSE4710.install);