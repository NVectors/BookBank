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
    private String globalISBN;
    private FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode_return_book);

        /** Get book id of the book that clicked in the list view of BorrowerBooksActivity */
        final String bookID = getIntent().getStringExtra("BOOK_ID");
        final String ISBN_OG = getIntent().getStringExtra("ISBN_OG").replace("ISBN: ", "");
        globalBookID = bookID;
        globalISBN = ISBN_OG;


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

                /*--Change Status + Change BorrowerID--*/
                boolean updateSuccess = updateBook(isbnCode);
                if (updateSuccess == true){
                    builder.setTitle("Scanning Result" + ": Success!");
                }
                else {
                    builder.setTitle("Scanning Result" + ": Fail!");
                }
                builder.setMessage(result.getContents());

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
            }
            else {
                Toast.makeText(this, "No Results", Toast.LENGTH_LONG).show();
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    private boolean updateBook(String isbnCode) {
        db = FirebaseFirestore.getInstance();

        if (isbnCode.equals(globalISBN)){
            /** Update the fields for the document in firestore */
            db.collection("Book").document(globalBookID).update("status", "Available");
            db.collection("Book").document(globalBookID).update("borrowerId", "");
            //finish();
            return true;
        }
        else{
            return false;
        }
    }
}