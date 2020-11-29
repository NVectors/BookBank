package com.example.bookbank.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookbank.R;
import com.example.bookbank.models.Book;
//import com.example.bookbank.models.Request;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import javax.annotation.Nullable;


public class ViewOwnedBooksActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private DocumentReference bookReference;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri uri;
    private String bookID;
    private static final String TAG = "SCANNED";
    private static final String tag = "VIEW OWNED BOOK";
    private String borrowerID;
    private String ownerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_owned_books);

        /** Move layout up when keyboard is present */
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        /** Get book id of the book that clicked in the list view of OwnerBooksActivity */
        bookID = getIntent().getStringExtra("BOOK_ID");

        /** Get instance of Firestore */
        db = FirebaseFirestore.getInstance();

        /** Get top level reference to the book in collection  by ID */
        bookReference = db.collection("Book").document(bookID);

        /** Get top level reference to the photo in storage */
        final StorageReference imageRef = FirebaseStorage.getInstance().getReference("images/" + bookID);

        /** Get references in the layout*/
        final TextView title = findViewById(R.id.book_title);
        final TextView author = findViewById(R.id.author);
        final TextView isbn = findViewById(R.id.isbn);
        final TextView status = findViewById(R.id.status);
        final TextView borrower = findViewById(R.id.borrower);
        final TextView description = findViewById(R.id.description);
        final Button handOver = findViewById(R.id.hand_over_button);

        final ImageView bookImage = findViewById(R.id.owner_book_image);
        ViewBookPhotoActivity.setImage(bookID, bookImage);

        /**  Realtime updates, snapshot is the state of the database at any given point of time */
        bookReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            /**
             * Method is executed whenever any new event occurs in the remote database
             */
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && value.exists()) {
                    // Set the text views
                    title.setText(value.getString("title"));
                    author.setText("By: " + value.getString("author"));
                    isbn.setText("ISBN: " + String.valueOf(value.getData().get("isbn")));
                    status.setText("Status: " + value.getString("status"));

                    Boolean ownerScanned = value.getBoolean("ownerScanHandOver");
                    String bookStatus = value.getString("status");
                    // first state of the button is Requests

                    borrowerID = value.getString("borrowerId");
                    ownerID = value.getString("ownerId");
                    // changing request button to handOver
                    if (bookStatus.equals("Accepted") && !ownerScanned) {
                        handOver.setText("HAND OVER");
                    } else if (bookStatus.equals("Accepted") && ownerScanned) {
                        handOver.setText("CANCEL HAND OVER");
                    }
                    // receiving book back from borrower
                    else if (bookStatus.equals("Borrowed") && ownerScanned) {
                        handOver.setVisibility(View.VISIBLE);
                        handOver.setText("RECEIVE BOOK");
                    }
                    // dont need button if no request or book is borrowed and not in middle of handover
                    else if ((bookStatus.equals("Borrowed") && !ownerScanned) || bookStatus.equals("Available")) {
                        handOver.setVisibility(View.INVISIBLE);
                    } else if (bookStatus.equals("Requested")) {
                        handOver.setVisibility(View.VISIBLE);
                    }

                    // Set the borrower name text view next
                    if (value.getString("borrowerId") == "") {
                        borrower.setText("Borrower: None");
                    } else {
                        DocumentReference borrowerRef = db.collection("User").document(value.getString("borrowerId"));
                        borrowerRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        String name = document.getString("fullname");
                                        // Test
                                        Log.d(tag, "Book name: " + name);

                                        borrower.setVisibility(View.VISIBLE); // Default of Borrower text view
                                        borrower.setText("Borrower: " + name);

                                    } else {
                                        Log.d("TAG", "No such document");
                                        borrower.setText("Borrower: FAILED");
                                    }
                                } else {
                                    Log.d("TAG", "Failed with ", task.getException());
                                }
                            }
                        });
                    }
                    description.setText("Description: " + value.getString("description"));
                }
            }
        });

        //CODE HERE FOR OWNER RECIEVING BOOK FROM BORROWER
        /** Hand Over button is clicked */
        handOver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check status of book
                bookReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot snapshot = task.getResult();
                        String bookStatus =  snapshot.getString("status");
                        Boolean ownerScanned = snapshot.getBoolean("ownerScanHandOver");
                        // to check all requests for book
                        if (bookStatus.equals("Requested")) {
                            Intent intent = new Intent(ViewOwnedBooksActivity.this, RequestsActivity.class);
                            intent.putExtra("BOOK_ID", bookID);
                            startActivity(intent);
                        }
                        // Owner Handover of book
                        else if (bookStatus.equals("Accepted") && (!ownerScanned)){
                            /** Hand Over button is clicked, go to Barcode Scanner to scan book */
                            final Button handOver = findViewById(R.id.hand_over_button);
                            handOver.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(ViewOwnedBooksActivity.this, ScanBarcodeActivity.class);
                                    /** Barcode Scanner activity will return the value of the barcode and error messages */
                                    startActivityForResult(intent, 0);
                                }
                            });
                        }
                        // canceling handover
                        // moment borrower scans changes status to Borrowed, and ownerScanned to false
                        else if (bookStatus.equals("Accepted") && ownerScanned) {
                            bookReference.update("ownerScanHandOver", false);
                        }
                        // Owner receiving book from borrower. when borrower scans --> set ownerScanHandOver = true
                        else if (bookStatus.equals("Borrowed") && ownerScanned) {
                            String originalBookISBN = isbn.getText().toString();
                            Intent intent = new Intent(getBaseContext(), ScanBarCodeReturnBookActivity.class);
                            Log.d("DEBUG5", "line 210");
                            intent.putExtra("BOOK_ID", bookID); //string
                            intent.putExtra("ISBN_OG", originalBookISBN); //string
                            intent.putExtra("OWNER_SCAN", false); //bool. Borrower must scan first
                            intent.putExtra("BORROWER_SCAN", ownerScanned);
                            intent.putExtra("BORROWER_ID", borrowerID); //string
                            intent.putExtra("OWNER_ID", ownerID); //string
                            startActivity(intent);
                            finish();
                        }
                    }
                });

            }
        });

        /** Delete button is clicked */
        final Button delete = findViewById(R.id.delete_button);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** Delete image first always */
                StorageReference photoRef = FirebaseStorage.getInstance().getReference("images/" + bookID);
                if (photoRef != null) {
                    photoRef.delete();
                }
                /** Delete the document from the collection in firestore */
                db.collection("Book").document(bookID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Successfully deleted book", Toast.LENGTH_LONG).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Did not delete book", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }
        });

        /** Long click on the Book title text to edit */
        title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                editDescription(title, author, isbn, description, bookID);
                return false;
            }
        });

        /** Long click on the Book author text to edit */
        author.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                editDescription(title, author, isbn, description, bookID);
                return false;
            }
        });

        /** Long click on the Book ISBN text to edit */
        isbn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                editDescription(title, author, isbn, description, bookID);
                return false;
            }
        });

        /** Long click on the Book description text to edit */
        description.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                editDescription(title, author, isbn, description, bookID);
                return false;
            }
        });

        /** Image add buton is clicked */
        final Button addImage = findViewById(R.id.add_image_button);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(tag, "Adding an image");

                //Implement
            }
        });


        /** Image delete button is clicked */
        final Button deleteImage = findViewById(R.id.delete_image);
        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageReference photoRef = FirebaseStorage.getInstance().getReference("images/" + bookID);
                photoRef.delete();
                bookImage.setImageResource(R.drawable.default_book_image);
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
            case (0) : {
                if (resultCode == Activity.RESULT_OK) {
                    /** Get string from key of resultIntent passed back from child activity */
                    String returnValue = data.getStringExtra("RESULT");

                    /** Get the value of the barcode scanned */
                    String barcodeValue = data.getStringExtra("VALUE");

                    /** Display the string to the user */
                    Toast.makeText(getApplicationContext(), returnValue, Toast.LENGTH_SHORT).show();

                    /** Display the barcode value to the user */
                    if (!barcodeValue.equals("ERROR")) {
                        Toast.makeText(getApplicationContext(), "Correct Barcode Type", Toast.LENGTH_LONG).show();
                        ownerScan(barcodeValue);  // Handle firestore code in ownerScan()
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
     * Then the boolean will be updated to True.
     * @param barcodeValue
     */
    private void ownerScan(String barcodeValue) {
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
                                    /** Update the boolean to True for the book */
                                    db.collection("Book").document(bookID).update("ownerScanHandOver", true);
                                }

                                /** Notify user the next steps in handing over the book */
                                Toast.makeText(getApplicationContext(), "Success! Borrow must scan book now", Toast.LENGTH_LONG).show();
                            }
                            /** ISBN of the book scanned matches but status is not "Accepted" */
                            else if (!bookStatus.toLowerCase().equals("accepted")){
                                Toast.makeText(getApplicationContext(), "Accept request for the book first", Toast.LENGTH_LONG).show();
                            }
                        }
                        /** ISBN of the book scanned doesn't match the ISBN of the book in database */
                        else if (!bookISBN.equals(barcodeValue)){
                            Toast.makeText(getApplicationContext(), "Book scanned doesn't match the selected book", Toast.LENGTH_LONG).show();
                        }
                    } else { // Document is null
                        Toast.makeText(getApplicationContext(), "Book scanned is not in your list", Toast.LENGTH_LONG).show();
                    }
                } else { // Task not successful
                    Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Start new activity screen and pass along the TextView data to allow editing of the field strings
     */
    private void editDescription (TextView title, TextView author, TextView isbn, TextView
            description, String bookID){
        Intent intent = new Intent(ViewOwnedBooksActivity.this, EditDescriptionActivity.class);
        intent.putExtra("BOOK_ID", bookID);
        intent.putExtra("TITLE", title.getText().toString());
        intent.putExtra("AUTHOR", author.getText().toString());
        intent.putExtra("ISBN", isbn.getText().toString());
        intent.putExtra("DESCRIPTION", description.getText().toString());
        startActivity(intent);
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
                startActivity(new Intent(ViewOwnedBooksActivity.this, EditProfileActivity.class));
                break;
            case R.id.nav_my_books:
                startActivity(new Intent( ViewOwnedBooksActivity.this, OwnerBooksActivity.class));
                break;
            case R.id.nav_borrowed_books:
                startActivity(new Intent(ViewOwnedBooksActivity.this, BorrowedBooksActivity.class));
                break;
            case R.id.nav_search_books:
                startActivity(new Intent(ViewOwnedBooksActivity.this, SearchBooksActivity.class));
                break;
            case R.id.nav_notifications:
                startActivity(new Intent(ViewOwnedBooksActivity.this, NotificationsActivity.class));
                break;
            case R.id.nav_search_users:
                startActivity(new Intent(ViewOwnedBooksActivity.this, SearchUsernameActivity.class));
                break;
            case R.id.nav_my_requests:
                startActivity(new Intent(ViewOwnedBooksActivity.this, MyCurrentRequestsActivity.class));
                break;
            case R.id.nav_sign_out:
                firebaseAuth.signOut();
                Toast.makeText(ViewOwnedBooksActivity.this, "Successfully Signed Out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ViewOwnedBooksActivity.this, LoginActivity.class));
                break;
            default:
                break;
        }
        return true;
    }
}