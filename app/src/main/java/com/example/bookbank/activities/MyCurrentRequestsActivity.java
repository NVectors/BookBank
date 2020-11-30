package com.example.bookbank.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bookbank.R;
import com.example.bookbank.adapters.MyCurrentRequestsAdapter;
import com.example.bookbank.adapters.OwnerBooksAdapter;
import com.example.bookbank.models.Book;
import com.example.bookbank.models.Request;
import com.google.android.gms.common.api.internal.OnConnectionFailedListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MyCurrentRequestsActivity extends AppCompatActivity {
    public ListView requestList; //changed to public -> solo need to access this.
    private ArrayAdapter<Request> requestAdapter;
    private ArrayList<Request> requestDataList;

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    private static String TAG = "Scanner";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_current_requests);

        /** Find reference to the ListView */
        requestList = findViewById(R.id.my_requests_list);
        requestDataList = new ArrayList<Request>();

        requestAdapter = new MyCurrentRequestsAdapter(this, R.layout.my_current_requests_item, requestDataList);
        requestList.setAdapter(requestAdapter);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        firestore.collection("Request").whereEqualTo("requesterId", firebaseAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    Request request = document.toObject(Request.class);
                    requestDataList.add(request);
                }
                requestAdapter.notifyDataSetChanged();
                Toast.makeText(MyCurrentRequestsActivity.this, "got requests", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MyCurrentRequestsActivity.this, "failed to get requests", Toast.LENGTH_SHORT).show();
            }
        });

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            /** Case 0 = Barcode Scanner child activity */
            case (1) : {
                if (resultCode == Activity.RESULT_OK) {
                    /** Get string from key of resultIntent passed back from child activity */
                    String returnValue = data.getStringExtra("RESULT");

                    /** Get the value of the barcode scanned */
                    String barcodeValue = data.getStringExtra("VALUE");

                    /** Get the id of the book from adapter */
                    String bookId = data.getStringExtra("BOOK");

                    /** Display the string to the user */
                    Toast.makeText(getApplicationContext(), returnValue, Toast.LENGTH_SHORT).show();

                    /** Display the barcode value to the user */
                    if (!barcodeValue.equals("ERROR")) {
                        Toast.makeText(getApplicationContext(), "Correct Barcode Type", Toast.LENGTH_LONG).show();
                        borrowerScan(barcodeValue, bookId); // Handle firestore code in borrowerScan()
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Incorrect Barcode Type", Toast.LENGTH_LONG).show();
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
     * @param bookId
     */
    private void borrowerScan(String barcodeValue, String bookId) {
        /** Get top level reference to the book in collection  by ID */
        DocumentReference bookReference = firestore.collection("Book").document(bookId);

        bookReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        /** Get the ISBN and Status of the book in the database */
                        String bookISBN = String.valueOf(document.getData().get("isbn"));
                        String bookStatus = document.getString("status");

                        /** ISBN of the book scanned matches with the ISBN of book in database */
                        if (bookISBN.equals(barcodeValue)) {
                            /** Status of the book is correct should be "Accepted"*/
                            if(bookStatus.toLowerCase().equals("accepted")) {
                                /** Check if the boolean to keep track of owner scanning first is false by default */
                                Boolean check = (Boolean) document.getData().get("ownerScanHandOver");
                                if (check == false){
                                    /** Notify user the correct steps in handing over the book */
                                    Toast.makeText(getApplicationContext(), "Owner of book must scan book first", Toast.LENGTH_LONG).show();
                                }

                                /** Update the status of the book to "Borrowed" */
                                firestore.collection("Book").document(bookId).update("status", "Borrowed");

                                /** Update Borrower ID */
                                String borrowerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                firestore.collection("Book").document(bookId).update("borrowerId", borrowerId);

                                /** Update the boolean to False for the book */
                                firestore.collection("Book").document(bookId).update("ownerScanHandOver", false);

                                /** Notify user handing off the book was a success*/
                                Toast.makeText(getApplicationContext(), "Success! Borrower can loan the book now", Toast.LENGTH_LONG).show();

                                /** Go to the Borrower Book List */
                                startActivity(new Intent(MyCurrentRequestsActivity.this, ViewBorrowedBookActivity.class));


                            }
                            /** ISBN of the book scanned matches but status is not "Accepted" */
                            else if (!bookStatus.toLowerCase().equals("accepted")){
                                Toast.makeText(getApplicationContext(), "Owner must accept request for the book first", Toast.LENGTH_LONG).show();
                            }
                        }
                        /** ISBN of the book scanned doesn't match the ISBN of the book in database */
                        else if (!bookISBN.equals(barcodeValue)){
                            Toast.makeText(getApplicationContext(), "Book scanned doesn't match the requested book", Toast.LENGTH_LONG).show();
                        }

                    } else { // Document is null
                        Toast.makeText(getApplicationContext(), "Book scanned is not the requested book", Toast.LENGTH_LONG).show();
                    }
                } else { // Task is not successful
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
                startActivity(new Intent(MyCurrentRequestsActivity.this, EditProfileActivity.class));
                break;
            case R.id.nav_my_books:
                startActivity(new Intent(MyCurrentRequestsActivity.this, OwnerBooksActivity.class));
                break;
            case R.id.nav_borrowed_books:
                startActivity(new Intent(MyCurrentRequestsActivity.this, BorrowedBooksActivity.class));
                break;
            case R.id.nav_search_books:
                startActivity(new Intent(MyCurrentRequestsActivity.this, SearchBooksActivity.class));
                break;
            case R.id.nav_notifications:
                startActivity(new Intent(MyCurrentRequestsActivity.this, NotificationsActivity.class));
                break;
            case R.id.nav_search_users:
                startActivity(new Intent(MyCurrentRequestsActivity.this, SearchUsernameActivity.class));
                break;
            case R.id.nav_my_requests:
                startActivity(new Intent(MyCurrentRequestsActivity.this, MyCurrentRequestsActivity.class));
                break;
            case R.id.nav_sign_out:
                firebaseAuth.signOut();
                Toast.makeText(MyCurrentRequestsActivity.this, "succcessfully signed out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MyCurrentRequestsActivity.this, LoginActivity.class));
                break;
            default:
                break;
        }
        return true;
    }
}