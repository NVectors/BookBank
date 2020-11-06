package com.example.bookbank.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bookbank.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewSearchBookDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_search_book_details);

        Button viewOwnerButton = findViewById(R.id.button_view_owner);

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
        String title = intent.getStringExtra("TITLE");
        String isbn = intent.getStringExtra("ISBN");
        String description = intent.getStringExtra("DESCRIPTION");
        String bookId = intent.getStringExtra("BOOK_ID");
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

        // on view owner profile button. open ViewSearchUser Activity
        viewOwnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewSearchBookDetails.this, ViewSearchUserActivity.class);
                intent.putExtra("USER_ID", ownerID);
                startActivity(intent);
            }
        });
    }
}