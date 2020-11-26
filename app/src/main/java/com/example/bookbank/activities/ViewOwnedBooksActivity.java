package com.example.bookbank.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import javax.annotation.Nullable;

public class ViewOwnedBooksActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri uri;
    private String bookID;

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
        final DocumentReference bookReference = db.collection("Book").document(bookID);

        final StorageReference imageRef = FirebaseStorage.getInstance().getReference("images/" + bookID);

        /** Get references in the layout*/
        final TextView title = findViewById(R.id.book_title);
        final TextView author = findViewById(R.id.author);
        final TextView isbn = findViewById(R.id.isbn);
        final TextView status = findViewById(R.id.status);
        final TextView borrower = findViewById(R.id.borrower);
        final TextView description = findViewById(R.id.description);
        final Button request = findViewById(R.id.request_button);

        final ImageView bookImage = findViewById(R.id.owner_book_image);
        ViewBookPhotoActivity.setImage(bookID, bookImage);

        /**  Realtime updates, snapshot is the state of the database at any given point of time */
        bookReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            /**
             * Method is executed whenever any new event occurs in the remote database
             */
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                title.setText(value.getString("title"));
                author.setText("By: " + value.getString("author"));
                isbn.setText("ISBN: " + String.valueOf(value.getData().get("isbn")));
                status.setText("Status: " + value.getString("status"));

                Boolean ownerScanned = value.getBoolean("ownerScanHandOver");
                String bookStatus = value.getString("status");
                // changing request button to handOver
                if (bookStatus.equals("Accepted") && !ownerScanned) {
                    request.setText("HAND OVER");
                }
                else if (bookStatus.equals("Accepted") && ownerScanned) {
                    request.setText("CANCEL HAND OVER");
                }
                // receiving book back from borrower
                else if (bookStatus.equals("Borrowed") && ownerScanned) {
                    request.setText("RECEIVE BOOK");
                }
                // dont need button if no request or book is borrowed and not in middle of handover
                else if ((bookStatus.equals("Borrowed") && !ownerScanned) || bookStatus.equals("Available")) {
                    request.setVisibility(View.INVISIBLE);
                }

                // if borrowed and handOver != true --> set button to invisible

                if (value.getString("borrowerId") == "") {
                    borrower.setText("Borrower: None");
                } else { // Will have to test this later
                    DocumentReference documentRef = db.collection("User").document(value.getString("borrowerId"));
                    documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        /**
                         * Use DocumentSnapshot to find field value in the document
                         * @param task
                         */
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String name = document.getString("fullname");
                                    // Test
                                    Log.d("NAME", name);

                                    borrower.setVisibility(View.VISIBLE); // Default of Borrower text view
                                    borrower.setText("Borrower: " + name);

                                } else {
                                    Log.d("TAG", "No such document");
                                    borrower.setText("Borrower: FAILED");
                                }
                            } else {
                                Log.d("TAG", "get failed with ", task.getException());
                            }
                        }
                    });
                }
                description.setText("Description: " + value.getString("description"));
            }
        });

        /** Request button is clicked */
        request.setOnClickListener(new View.OnClickListener() {
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
                            // change handedOver to true and start handover
                            Intent intent = new Intent(ViewOwnedBooksActivity.this, ScanBarcodeActivity.class);
                            startActivity(intent);
                            // wip scan barcode to verify then update -->
                            bookReference.update("ownerScanHandOver", true);
                        }
                        // canceling handover
                        // moment borrower scans changes status to Borrowed, and ownerScanned to false
                        else if (bookStatus.equals("Accepted") && ownerScanned) {
                            bookReference.update("ownerScanHandOver", false);
                        }
                        // Owner recieving book from borrower. when borrower scans --> set ownerScanHandOver = true
                        else if (bookStatus.equals("Borrowed") && ownerScanned) {
                            Intent intent = new Intent(ViewOwnedBooksActivity.this, ScanBarcodeActivity.class);
                            startActivity(intent);
                            // wip scan barcode to verify then update -->
                            bookReference.update("status", "Available");
                            bookReference.update("borrowerId", "");
                            bookReference.update("ownerScanHandOver", false);
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
                StorageReference photoRef = FirebaseStorage.getInstance().getReference("images/" + bookID);
                photoRef.delete();
                db.collection("Book").document(bookID).delete();
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
                Toast.makeText(ViewOwnedBooksActivity.this, "succcessfully signed out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ViewOwnedBooksActivity.this, LoginActivity.class));
                break;
            default:
                break;
        }
        return true;
    }
}