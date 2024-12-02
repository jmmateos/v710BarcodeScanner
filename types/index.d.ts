// Type definitions for Cordova V710 Barcode Scanner plugin
// Project: https://github.com/jmmateos/v710BarcodeScanner
// Definitions by: Microsoft Open Technologies Inc <http://msopentech.com>
// Definitions: https://github.com/DefinitelyTyped/DefinitelyTyped
//
// Copyright (c) Microsoft Open Technologies Inc
// Licensed under the MIT license.
// TypeScript Version: 2.3

interface CordovaPlugins {
    v710BarcodeScanner: BarcodeScannerSE4710;
}

interface ScanData {
    prefix: string;
    suffix: string;
    additional: boolean;
    clear: boolean;
    focusInput: boolean;
    data: string;
    type: string;   
}

interface BarcodeScannerSE4710 {
    /**
     * It is a compatible device?
     * @param onSuccess returns true or false.
     */
    available(
        onSuccess: (data: boolean) => void): void;
    /**
     * This method is used when the application wants to subscribe to the activation / deactivation of the device's scanner.
     * @param onSuccess true, the scanner has been activated, false has been deactivated.
     * @param onError Error callback, that get an error message.
     */    
    registerScanningReceiver(
        onSuccess: (data: boolean) => void,
        onError: (message: string) => void): void;    
    /**
     * This method is used when the application wants to obtain the data scanned by the device.
     * @param onSuccess returns the scanned data.
     * @param onError Error callback, that get an error message.
     */  
    registerScanDataReceiver(
        onSuccess: (data: ScanData) => void,
        onError: (message: string) => void): void;    
    /**
     * This method disables the subscription to the event.
     */  
    unregisterScanningReceiver(): void;    
    /**
     * This method disables the subscription to the event.
     */  
    unregisterScanDataReceiver(): void;  
}