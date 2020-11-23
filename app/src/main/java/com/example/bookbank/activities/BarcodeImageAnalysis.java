package com.example.bookbank.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.Image;
import android.util.Log;
import android.widget.Toast;

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
    public BarcodeImageAnalysis(){
        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                Barcode.FORMAT_QR_CODE,
                                Barcode.FORMAT_AZTEC,
                                Barcode.FORMAT_EAN_13,
                                Barcode.FORMAT_EAN_8)
                        .build();

    }


    @Override
    @androidx.camera.core.ExperimentalGetImage
    public void analyze(@NonNull ImageProxy image) {

        if(image == null || image.getImage() == null){
            return;
        }

        Image barcodeImage = image.getImage();
        int rotationDegrees = image.getImageInfo().getRotationDegrees();

        InputImage inputImage = InputImage.fromMediaImage(barcodeImage,rotationDegrees);

        BarcodeScanner scanner = BarcodeScanning.getClient();

        Task<List<Barcode>> result = scanner.process(inputImage)
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        Log.d("MOHIT","BARCODE SCANNER");
                        Toast.makeText(getActivity().getApplicationContext(),"ScanningBarcode",Toast.LENGTH_LONG).show();


                        for(Barcode barcode: barcodes){
                            String data = barcode.getRawValue();
                            Log.d("MOHIT","BARCODE IS " + data );
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MOHIT","BARCODE SCAN FAILED");

                    }
                });

    }
}
