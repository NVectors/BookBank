package com.example.bookbank.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.bookbank.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ViewBorrowedBookActivity extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private DocumentReference bookReference;
    private String bookID;
    private static String TAG = "SCANNER";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_borrowed_book);

        /** Move layout up when keyboard is present */
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        /** Get book id of the book that clicked in the list view of BorrowerBooksActivity */
        bookID = getIntent().getStringExtra("BOOK_ID");

        /** Get instance of Firestore */
        firestore = FirebaseFirestore.getInstance();

        /** Get top level reference to the book in collection  by ID */
        bookReference = firestore.collection("Book").document(bookID);

        /** Get references in the layout*/
        final TextView title = findViewById(R.id.book_title);
        final TextView author = findViewById(R.id.author);
        final TextView isbn = findViewById(R.id.isbn);
        final TextView status = findViewById(R.id.status);
        final TextView owner = findViewById(R.id.owner);
        final TextView description = findViewById(R.id.description);
        final ImageView bookImage = findViewById(R.id.owner_book_image);
        ViewBookPhotoActivity.setImage(bookID, bookImage);

        /**  Realtime updates, snapshot is the state of the database at any given point of time */
        bookReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            /** Method is executed whenever any new event occurs in the remote database */
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                title.setText(value.getString("title"));
                author.setText("By: " + value.getString("author"));
                isbn.setText("ISBN: " + String.valueOf(value.getData().get("isbn")));
                status.setText("Status: " + value.getString("status"));

                if (value.getString("ownerId") == "") {
                    owner.setText("Owner: None");
                } else { // Will have to test this later
                    DocumentReference documentRef = firestore.collection("User").document(value.getString("ownerId"));
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

                                    owner.setText("Owner: " + name);

                                } else {
                                    Log.d("TAG", "No such document");
                                    owner.setText("Owner: FAILED");
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

        /** returnBook button is clicked */
        final Button returnBook = findViewById(R.id.return_book_button);
        returnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DocumentReference bookReference = firestore.collection("book").document(bookID);
                bookReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot snapshot = task.getResult();
                        Boolean ownerScanned = snapshot.getBoolean("ownerScanHandOver");
                        if (!ownerScanned) {
                            // scan barcode here, if good --> update


                            //bookReference.update("ownerScanHandOver", true);
                        }
                    }
                });
            }
        });

        // --------------------------Required for Toolbar---------------------------------//
        // set tool bar
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
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
                startActivity(new Intent(ViewBorrowedBookActivity.this, EditProfileActivity.class));
                break;
            case R.id.nav_my_books:
                startActivity(new Intent(ViewBorrowedBookActivity.this, OwnerBooksActivity.class));
                break;
            case R.id.nav_borrowed_books:
                startActivity(new Intent(ViewBorrowedBookActivity.this, BorrowedBooksActivity.class));
                break;
            case R.id.nav_search_books:
                startActivity(new Intent(ViewBorrowedBookActivity.this, SearchBooksActivity.class));
                break;
            case R.id.nav_notifications:
                startActivity(new Intent(ViewBorrowedBookActivity.this, NotificationsActivity.class));
                break;
            case R.id.nav_search_users:
                startActivity(new Intent(ViewBorrowedBookActivity.this, SearchUsernameActivity.class));
                break;
            case R.id.nav_my_requests:
                startActivity(new Intent(ViewBorrowedBookActivity.this, MyCurrentRequestsActivity.class));
                break;
            case R.id.nav_sign_out:
                firebaseAuth.signOut();
                Toast.makeText(ViewBorrowedBookActivity.this, "succcessfully signed out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ViewBorrowedBookActivity.this, LoginActivity.class));
                break;
            default:
                break;
        }
        return true;
    }
}