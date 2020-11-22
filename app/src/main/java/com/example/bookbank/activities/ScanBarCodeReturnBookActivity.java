package com.example.bookbank.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.bookbank.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanBarCodeReturnBookActivity extends AppCompatActivity implements View.OnClickListener {
    
    private Button scanButton;
    private String globalBookID;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode_return_book);

        /** Get book id of the book that clicked in the list view of BorrowerBooksActivity */
        final String bookID = getIntent().getStringExtra("BOOK_ID");
        globalBookID = bookID;

        Log.d("debug", "ARRIVED ScanBarCodeReturnBookActivity. FROM ViewBorrowedBookActivity");

        Log.d("debug", "bookID: " + bookID);

        scanButton = findViewById(R.id.ScanButton);
        scanButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        scanCode();
    }

    private void scanCode(){
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setCaptureActivity(CaptureBarCodeActivity.class);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.setPrompt("Scanning Code");
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null){
            if (result.getContents() != null){
                String isbnCode = result.getContents().toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(result.getContents());
                builder.setTitle("Scanning Result");
                Log.d("debug", "ISBN retrieved: " + isbnCode);

                /*--Change Status + Change BorrowerID--*/
                updateBook(isbnCode);
                /*------------------------------------*/
                builder.setPositiveButton("Scan Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        scanCode();
                    }
                }).setNegativeButton("finish", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                Toast.makeText(this, "SUCCESS", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "No Results", Toast.LENGTH_LONG).show();
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void updateBook(String isbnCode) {
        //Log.d("debug", "PARSED VALUE: " + globalBookID);
        Log.d("debug", "SCANNED VALUE: " + isbnCode);
        db = FirebaseFirestore.getInstance();

        /** Update the fields for the document in firestore */
        db.collection("Book").document(globalBookID).update("status", "Available");
        db.collection("Book").document(globalBookID).update("borrowerId", "");
        finish();
    }

    private void showToast(){
        Toast.makeText(this, "Success", Toast.LENGTH_LONG).show();
    }
}