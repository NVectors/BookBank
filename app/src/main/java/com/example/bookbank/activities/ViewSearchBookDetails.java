package com.example.bookbank.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookbank.R;
import com.example.bookbank.models.Request;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ViewSearchBookDetails extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_search_book_details);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        Button viewOwnerButton = findViewById(R.id.button_view_owner);
        final Button requestBookButton = findViewById(R.id.text_request_book);

        // getting all the views from the layout
        TextView textTitle = findViewById(R.id.text_title);
        TextView textIsbn = findViewById(R.id.text_isbn);
        TextView textDescription = findViewById(R.id.text_description);
        final TextView textOwnerName = findViewById(R.id.text_owner_name);
        TextView textAuthor = findViewById(R.id.text_author);
        TextView textStatus = findViewById(R.id.text_status);
        ImageView bookImage = findViewById(R.id.image_book);

        // fetching the intent
        Intent intent = getIntent();
        final String title = intent.getStringExtra("TITLE");
        String isbn = intent.getStringExtra("ISBN");
        String description = intent.getStringExtra("DESCRIPTION");
        final String bookId = intent.getStringExtra("BOOK_ID");
        String author = intent.getStringExtra("AUTHOR");
        final String ownerID = intent.getStringExtra("OWNER_ID");

        // setting all the views
        textTitle.setText(title);
        textAuthor.setText("Author: " + author);
        textDescription.setText("Description: " + description);
        textIsbn.setText("ISBN: " + isbn);
        textStatus.setText("Status: Available");

        // viewBookPhotoActivity to set the book Image
        ViewBookPhotoActivity.setImage(bookId, bookImage);

        // fetching ownerName from the DB and setting it
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("User").document(String.valueOf(ownerID));
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String ownerNameText = documentSnapshot.getString("fullname");
                    textOwnerName.setText("Owner: " + ownerNameText);

                }
            }
        });

//        // check to see if user has already made a request for this book
//        db.collection("Request").whereEqualTo("bookId", bookId).whereEqualTo("requesterId", firebaseAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                Log.d("debug", String.valueOf(queryDocumentSnapshots.size()));
//                if (queryDocumentSnapshots.size() != 0) {
//                    // user has already requested this book so dont allow them to request it again
//                    requestBookButton.setVisibility(View.INVISIBLE);
//                } else {
//                    // user hasnt requested this book yet, so allow them too
//                    requestBookButton.setVisibility(View.INVISIBLE);
//                }
//            }
//        });

        // on view owner profile button. open ViewSearchUser Activity
        viewOwnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewSearchBookDetails.this, ViewSearchUserActivity.class);
                intent.putExtra("USER_ID", ownerID);
                startActivity(intent);
            }
        });

        // on request book button. create Request object and add to firestore
        requestBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = db.collection("Request").getId();
                Request request = new Request(
                        id,
                        bookId,
                        title,
                        firebaseAuth.getUid(),
                        ownerID,
                        "Pending",
                        null,
                        null
                );
                db.collection("Request").add(request).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(ViewSearchBookDetails.this, "successfully requested book", Toast.LENGTH_SHORT).show();
                        requestBookButton.setVisibility(View.INVISIBLE);
                        // set the book to a status of requested
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("status", "Requested");
                        db.collection("Book").document(bookId).update(map);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ViewSearchBookDetails.this, "failed to request book", Toast.LENGTH_SHORT).show();
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
                startActivity(new Intent(ViewSearchBookDetails.this, EditProfileActivity.class));
                break;
            case R.id.nav_my_books:
                startActivity(new Intent( ViewSearchBookDetails.this, OwnerBooksActivity.class));
                break;
            case R.id.nav_borrowed_books:
                startActivity(new Intent(ViewSearchBookDetails.this, BorrowedBooksActivity.class));
                break;
            case R.id.nav_search_books:
                startActivity(new Intent(ViewSearchBookDetails.this, SearchBooksActivity.class));
                break;
            case R.id.nav_notifications:
                startActivity(new Intent(ViewSearchBookDetails.this, NotificationsActivity.class));
                break;
            case R.id.nav_search_users:
                startActivity(new Intent(ViewSearchBookDetails.this, SearchUsernameActivity.class));
                break;
            case R.id.nav_my_requests:
                startActivity(new Intent(ViewSearchBookDetails.this, MyCurrentRequestsActivity.class));
                break;
            case R.id.nav_sign_out:
                firebaseAuth.signOut();
                Toast.makeText(ViewSearchBookDetails.this, "succcessfully signed out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ViewSearchBookDetails.this, LoginActivity.class));
                break;
            default:
                break;
        }
        return true;
    }
}