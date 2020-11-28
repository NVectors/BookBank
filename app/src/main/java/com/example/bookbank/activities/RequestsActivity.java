package com.example.bookbank.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.bookbank.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

public class RequestsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private DocumentReference bookReference;
    private FirebaseAuth firebaseAuth;
    private static final String TAG = "SCANNED";
    private String bookID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        /** Get book id of the book that clicked in the list view of OwnerBooksActivity */
        //bookID = getIntent().getStringExtra("BOOK_ID");
        bookID = "fe3b2289-cdd8-4a0b-b032-b931b7c761c6";

        /** Get instance of Firestore */
        db = FirebaseFirestore.getInstance();

        /** Get top level reference to the book in collection  by ID */
        bookReference = db.collection("Book").document(bookID);


        firebaseAuth = FirebaseAuth.getInstance();

        /** Borrow Book button is clicked, go to Barcode Scanner to scan book */
        final Button confirmHandOver = findViewById(R.id.borrow_book_button);
        confirmHandOver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RequestsActivity.this, ScanBarcodeActivity.class);
                intent.putExtra("BOOK_ID", bookID);
                /** Barcode Scanner activity will return the value of the barcode and error messages */
                startActivityForResult(intent, 1);
            }
        });



        // --------------------------Required for Toolbar---------------------------------//
        // set tool bar
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
    }

    /**
     * Handle data that is sent back by the child activity via intent
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            /** Case 0 = Barcode Scanner child activity */
            case (1) : {
                if (resultCode == Activity.RESULT_OK) {
                    /** Get string from key of resultIntent passed back from child activity */
                    String returnValue = data.getStringExtra("RESULT");

                    /** Display the string to the user */
                    Toast.makeText(getApplicationContext(), returnValue, Toast.LENGTH_SHORT).show();

                    /** Get the value of the barcode scanned */
                    String barcodeValue = data.getStringExtra("VALUE");

                    /** Display the barcode value to the user */
                    //Toast.makeText(getApplicationContext(), "Barcode value returned: " + barcodeValue, Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"Barcode value returned: " + barcodeValue);

                    /** Handle firestore in ownerScan() */
                    if (!barcodeValue.equals("ERROR")) {
                        borrowerScan(barcodeValue);
                    }
                }
                break;
            }
        }
    }

    /**
     * Handles the cases after the barcode of the book is scanned.
     * If the ISBN of the book scanned matches the ISBN of the book in the database.
     * And only if the boolean is True then set book status to "Borrowed"
     * @param barcodeValue
     */
    private void borrowerScan(String barcodeValue) {
        Log.d(TAG, "In borrowerScan function!");

        bookReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        /** Get the ISBN and Status of the book in the database */
                        String bookISBN = String.valueOf(document.getData().get("isbn"));
                        String bookStatus = document.getString("status");

                        Log.d(TAG, "BOOK ISBN: " + bookISBN);
                        Log.d(TAG,"BARCODE ISBN: " + barcodeValue);
                        Log.d(TAG, "BOOK STATUS: " + bookStatus);

                        /** ISBN of the book scanned matches with the ISBN of book in database */
                        if (bookISBN.equals(barcodeValue)) {
                            Log.d(TAG, "ISBN MATCHES");
                            /** Status of the book is correct should be "Accepted"*/
                            if(bookStatus.toLowerCase().equals("accepted")) {
                                Log.d(TAG, "STATUS IS CORRECT");
                                Log.d(TAG, "BOOLEAN IS: " + document.getData().get("ownerScanHandOver"));

                                /** Check if the boolean to keep track of owner scanning first is false by default */
                                Boolean check = (Boolean) document.getData().get("ownerScanHandOver");
                                if (check == false){
                                    /** Notify user the correct steps in handing over the book */
                                    Toast.makeText(getApplicationContext(), "Owner of book must scan book first", Toast.LENGTH_LONG).show();
                                }

                                /** Update the status of the book to "Borrowed" */
                                db.collection("Book").document(bookID).update("status", "Borrowed");

                                /** Update the boolean to True for the book */
                                db.collection("Book").document(bookID).update("ownerScanHandOver", false);

                                /** Notify user handing off the book was a success*/
                                Toast.makeText(getApplicationContext(), "Borrower can loan the book now", Toast.LENGTH_LONG).show();
                            }
                            /** ISBN of the book scanned matches but status is not "Accepted" */
                            else if (!bookStatus.toLowerCase().equals("accepted")){
                                Log.d(TAG, "STATUS IS INCORRECT");
                                Toast.makeText(getApplicationContext(), "Owner must accept request for the book first", Toast.LENGTH_LONG).show();
                            }
                        }
                        /** ISBN of the book scanned doesn't match the ISBN of the book in database */
                        else if (!bookISBN.equals(barcodeValue)){
                            Log.d(TAG, "ISBN DON'T MATCH");
                            Toast.makeText(getApplicationContext(), "Book scanned doesn't match the requested book", Toast.LENGTH_LONG).show();
                        }

                    } else { /** Book not in the database */
                        Log.d(TAG, "No such document");
                        Toast.makeText(getApplicationContext(), "Book scanned is not the requested book", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d(TAG, "Failed with ", task.getException());
                    Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // --------------------------Create Toolbar Menu---------------------------------//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.inflateMenu(R.menu.activity_main_drawer);
        tb.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return onOptionsItemSelected(item);
                    }
                });
        return true;
    }

    // --------------------------Create Toolbar Menu---------------------------------//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.nav_my_profile:
                startActivity(new Intent(RequestsActivity.this, EditProfileActivity.class));
                break;
            case R.id.nav_my_books:
                startActivity(new Intent(RequestsActivity.this, OwnerBooksActivity.class));
                break;
            case R.id.nav_borrowed_books:
                startActivity(new Intent(RequestsActivity.this, BorrowedBooksActivity.class));
                break;
            case R.id.nav_search_books:
                startActivity(new Intent(RequestsActivity.this, SearchBooksActivity.class));
                break;
            case R.id.nav_notifications:
                startActivity(new Intent(RequestsActivity.this, NotificationsActivity.class));
                break;
            case R.id.nav_search_users:
                startActivity(new Intent(RequestsActivity.this, SearchUsernameActivity.class));
                break;
            case R.id.nav_my_requests:
                startActivity(new Intent(RequestsActivity.this, RequestsActivity.class));
                break;
            case R.id.nav_sign_out:
                firebaseAuth.signOut();
                Toast.makeText(RequestsActivity.this, "succcessfully signed out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RequestsActivity.this, LoginActivity.class));
                break;
            default:
                break;
        }
        return true;
    }
}