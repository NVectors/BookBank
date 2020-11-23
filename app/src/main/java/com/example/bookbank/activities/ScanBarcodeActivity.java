package com.example.bookbank.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.bookbank.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScanBarcodeActivity extends AppCompatActivity {

    // strings for permission
    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;

    private Button scanBarcode;
    private PreviewView previewView;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;
    private ExecutorService executor;
    private BarcodeImageAnalysis imageAnalysis;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);

        Log.d("MOHIT", "START");
        if (checkCameraPermission() == false) {
            getCameraPermission();
        }

        executor = Executors.newSingleThreadExecutor();
        imageAnalysis = new BarcodeImageAnalysis();

        previewView = findViewById(R.id.cameraPreview);
        scanBarcode = findViewById(R.id.barcodeButton);

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    showPreview(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }, ContextCompat.getMainExecutor(this));

        imageCapture = new ImageCapture.Builder()
                .setBufferFormat(ImageFormat.YUV_420_888)
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

        // Button on click listener to scan bar code
        scanBarcode.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.d("MOHIT", "ONCLICK BUTTON");
                takePhoto();
                   }
              });
    }

    private void takePhoto() {
        Log.d("MOHIT", "take Photo Function");
        imageCapture.takePicture(executor, new ImageCapture.OnImageCapturedCallback() {
            @SuppressLint("UnsafeExperimentalUsageError")
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                Log.d("MOHIT", "Picture Taken");
                imageAnalysis.analyze(image);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.d("MOHIT", "PIC FAILED");


            }
        });
    }


    private void showPreview(ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        preview.setSurfaceProvider(previewView.createSurfaceProvider());
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this,
                cameraSelector,
                imageCapture,
                preview);

    }

    private void getCameraPermission() {
        ActivityCompat.requestPermissions(this, CAMERA_PERMISSION, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }


//    public void onClick(View v) {
//        if(v.getId() == R.id.barcodeButton){
//            Log.d("MOHIT", "ONCLICK BUTTON");
//            imageCapture.takePicture(executor, new ImageCapture.OnImageCapturedCallback() {
//                @SuppressLint("UnsafeExperimentalUsageError")
//                @Override
//                public void onCaptureSuccess(@NonNull ImageProxy image) {
//                    Log.d("MOHIT", "IMAGE SUCCESS");
//                    imageAnalysis.analyze(image);
//
//                }
//
//                @Override
//                public void onError(@NonNull ImageCaptureException exception) {
//                    Log.d("MOHIT", "IMAGE CAPTURE ERROR");
//                }
//            });
//
//        }
//
//    }


}