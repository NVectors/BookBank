package com.example.bookbank.activities;

import android.media.Image;
import android.util.Log;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;

public class BarcodeImageAnalysis implements ImageAnalysis.Analyzer {

    private static final String TAG = "ANALYZE";
    private BarcodeScanner scanner;

    public BarcodeImageAnalysis() {
        /** Configure the barcode scanner to recognize only ISBN format */
        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                Barcode.FORMAT_EAN_13,
                                Barcode.FORMAT_EAN_8)
                        .build();

        /** Get instance of BarcodeScanner */
        scanner = BarcodeScanning.getClient();
    }

    /**
     * Analyze the image that was captured by the user
     * @param image
     */
    @Override
    @androidx.camera.core.ExperimentalGetImage
    public void analyze(ImageProxy image) {

    Image inputImage = image.getImage();

    /** Image does not exists */
    if(inputImage == null ){
        return;
    }

    int rotationDegrees = image.getImageInfo().getRotationDegrees();
    InputImage barcodeImage = InputImage.fromMediaImage(inputImage,rotationDegrees);

    /** Process the image captured */
    Task<List<Barcode>> result = scanner.process(barcodeImage)
            .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                @Override
                public void onSuccess(List<Barcode> barcodes) {
                    // Task completed successfully
                    Log.d(TAG,"Scanned the image!");

                    //Toast.makeText(getActivity().getApplicationContext(),"ScanningBarcode",Toast.LENGTH_LONG).show();

                    for(Barcode barcode: barcodes){
                        String data = barcode.getRawValue();
                        Log.d(TAG,"BARCODE IS " + data );

                        int valueType = barcode.getValueType();
                        switch (valueType) {
                            case Barcode.FORMAT_EAN_13:
                                Log.d(TAG,"BARCODE IS EAN-13");
                                Log.d(TAG,"BARCODE IS " + barcode.getDisplayValue() );
                                break;
                            case Barcode.FORMAT_EAN_8:
                                Log.d(TAG,"BARCODE IS " + barcode.getDisplayValue() );
                                Log.d(TAG,"BARCODE IS EAN-8");
                                break;
                        }
                    }
                    Log.d(TAG,"Done analyzing");
                    image.close();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Task failed with an exception
                    Log.d(TAG,"BARCODE SCAN FAILED");
                    Log.d(TAG,"Done analyzing");
                    image.close();
                }
            });
    }
}
