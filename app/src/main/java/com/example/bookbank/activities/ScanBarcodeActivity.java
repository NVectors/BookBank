package com.example.bookbank.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

import com.example.bookbank.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;
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
    private BarcodeScanner scanner;

    private FirebaseFirestore db;
    private String bookID;
    private DocumentReference docRef;
    private Intent resultIntent;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);

        /** References to camera preview layout object */
        previewView = findViewById(R.id.cameraPreview);

        /** Create intent to send back data to main activity later */
        resultIntent = new Intent();

        /** Get book id of the book that clicked in the list view of OwnerBooksActivity */
        bookID = getIntent().getStringExtra("BOOK_ID");

        /** Get instance of Firestore */
        db = FirebaseFirestore.getInstance();

        /** Get top level reference to the book in collection  by ID */
        docRef = db.collection("Book").document(bookID);


        Log.d(TAG, "Start the scanner!");
        /** Check if camera permission is granted by user */
        if (checkCameraPermission() == false) {
            getCameraPermission(); // Get camera permission
        }

        /** Initialize object to execute Runnable Task(s) */
        executor = Executors.newSingleThreadExecutor();

        /** Configure the barcode scanner to recognize only ISBN format */
        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                Barcode.FORMAT_EAN_13,
                                Barcode.FORMAT_EAN_8)
                        .build();

        /** Get instance of BarcodeScanner */
        scanner = BarcodeScanning.getClient();


        /** Get instance of the */
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    /** Link the layout view finder to the camera preview for live display */
                    ScanBarcodeActivity.this.showPreview(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));
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

        /** Image Analysis Function, only accept one image at a time for processing */
        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        imageAnalysis.setAnalyzer(executor, new ImageAnalysis.Analyzer() {
                    @Override
                    @SuppressLint("UnsafeExperimentalUsageError")
                    public void analyze(@NonNull ImageProxy image) {
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
                                        Toast.makeText(getApplicationContext(), "Scanning", Toast.LENGTH_SHORT).show();

                                        for (Barcode barcode: barcodes){
                                            String rawValue = barcode.getRawValue();
                                            Integer type = barcode.getFormat();
                                            Log.d(TAG, "BAR CODE IS " + rawValue);
                                            Log.d(TAG, "BAR CODE TYPE IS " + type.toString());

                                            if ( (type != Barcode.FORMAT_EAN_8) && (type != Barcode.FORMAT_EAN_13) ) {
                                                resultIntent.putExtra("WRONG_FORMAT", "Not an ISBN barcode");
                                                setResult(Activity.RESULT_OK, resultIntent);
                                            }

                                            resultIntent.putExtra("CORRECT_FORMAT", "Valid ISBN barcode");
                                            setResult(Activity.RESULT_OK, resultIntent);

                                        }
                                        Log.d(TAG,"Done analyzing");
                                        image.close();
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        Log.d(TAG,"BARCODE SCAN FAILED");
                                        Log.d(TAG,"Done analyzing");

                                        resultIntent.putExtra("ERROR", "There was an error: " + e.getMessage());
                                        setResult(Activity.RESULT_OK, resultIntent);
                                        image.close();
                                        finish();
                                    }
                                });
                    }
                });

        /** Bind the lifecycle of camera to LifecycleOwner within application's process */
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this,
                cameraSelector,
                imageAnalysis,
                preview);
    }

    private void ownerScan(String rawValue) {
        Log.d(TAG, "In ownerScan function!");

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        /** Get the ISBN and Status of the book in the database */
                        String bookISBN = String.valueOf(document.getData().get("isbn"));
                        String bookStatus = document.getString("status");

                        Log.d(TAG, "BOOK ISBN: " + bookISBN);
                        Log.d(TAG,"BARCODE ISBN: " + rawValue);
                        Log.d(TAG, "BOOK STATUS: " + bookStatus);

                        /** ISBN of the book scanned matches with the ISBN of book in database */
                        if (bookISBN.equals(rawValue)) {
                            Log.d(TAG, "ISBN MATCHES");

                            if(bookStatus.toLowerCase().equals("accepted")) {
                                Log.d(TAG, "STATUS IS CORRECT");
                                resultIntent.putExtra("WRONG_ISBN", "The scanned ISBN value doesn't match ");
                                setResult(Activity.RESULT_OK, resultIntent);
                            }
                            else if (!bookStatus.toLowerCase().equals("accepted")){
                                Log.d(TAG, "STATUS IS INCORRECT");
                                resultIntent.putExtra("WRONG_STATUS", "The book's request is not accepted!");
                                setResult(Activity.RESULT_OK, resultIntent);
                            }
                        }
                        else if (!bookISBN.equals(rawValue)){
                            Log.d(TAG, "ISBN DON'T MATCH");
                            resultIntent.putExtra("WRONG_ISBN", "The scanned ISBN value doesn't match!");
                            setResult(Activity.RESULT_OK, resultIntent);
                        }

                        /** CLose the scanner */
                        finish();
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "Failed with ", task.getException());
                }
            }
        });
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