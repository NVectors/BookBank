package com.example.bookbank.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.example.bookbank.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScanBarcodeActivity extends AppCompatActivity {

    // Strings for permission
    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;

    private static final String TAG = "SCANNER";

    private Button scanBarcode;
    private PreviewView previewView;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;
    private ExecutorService executor;
    private BarcodeImageAnalysis imageAnalysis;

    private String returnKeyword;
    private Intent returnIntent;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);

        /** getting the intent and checking the RETURN */
        Intent intent = getIntent();
        if(intent.hasExtra("RETURN")){
            returnKeyword = intent.getStringExtra("RETURN");
        }



        /** References to layout objects */
        previewView = findViewById(R.id.cameraPreview);
        scanBarcode = findViewById(R.id.barcodeButton);

        Log.d(TAG, "Start the scanner!");
        /** Check if camera permission is granted by user */
        if (checkCameraPermission() == false) {
            getCameraPermission(); // Get camera permission
        }

        /** Initialize object to execute Runnable Task(s) */
        executor = Executors.newSingleThreadExecutor();

        /** Get instance of the Image Analyzer class */
        imageAnalysis = new BarcodeImageAnalysis(returnKeyword);

        /** Get instance of the */
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

        /** Configure the Image Capture object to be able to take photos*/
        imageCapture = new ImageCapture.Builder()
                .setBufferFormat(ImageFormat.YUV_420_888)
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

        /** "Take Photo" button is clicked */
        scanBarcode.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button is pressed!");
                takePhoto(); // Call function to handle captured photo
            }
        });
    }

    /**
     * Use Image Capture object to take photo, if successful send image to the analyzer
     */
    private void takePhoto() {
        Log.d(TAG, "Taking a photo!");
        imageCapture.takePicture(executor, new ImageCapture.OnImageCapturedCallback() {
            @SuppressLint("UnsafeExperimentalUsageError")
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                Log.d(TAG, "Picture is taken!");
                imageAnalysis.analyze(image);


            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.d(TAG, "Picture not taken!");
                Toast.makeText(getApplicationContext(), "Failed to capture the image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Allows the camera to be displayed on the phone's screen
     * @param cameraProvider
     */
    private void showPreview(ProcessCameraProvider cameraProvider) {
        /** Construct a preview stream to display the camera on-screen*/
        Preview preview = new Preview.Builder().build();

        /** Use the back side camera */
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

        /** Create a surface for the camera preview layout that's connected to the preview stream*/
        preview.setSurfaceProvider(previewView.createSurfaceProvider());

        /** Bind the lifecycle of camera to LifecycleOwner within application's process */
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this,
                cameraSelector,
                imageCapture,
                preview);
    }

    /**
     * Get camera permission from user
     */
    private void getCameraPermission() {
        ActivityCompat.requestPermissions(this, CAMERA_PERMISSION, CAMERA_REQUEST_CODE);
    }

    /**
     * Check and return if camera permission is granted
     * @return
     */
    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }
}