package com.example.bookbank.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bookbank.R;

public class ViewSearchBookDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_search_book_details);

        // getting all the views from the layout
        TextView textTitle = findViewById(R.id.text_title);
        TextView textIsbn = findViewById(R.id.text_isbn);
        TextView textDescription = findViewById(R.id.text_description);
        TextView textOwner_name = findViewById(R.id.text_owner_name);
        TextView textAuthor = findViewById(R.id.text_author);
        TextView textStatus = findViewById(R.id.text_status);
        ImageView bookImage = findViewById(R.id.image_book);

        // fetching the intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("TITLE");
        String isbn = intent.getStringExtra("ISBN");
        String description = intent.getStringExtra("DESCRIPTION");
        String ownerName = intent.getStringExtra("OWNER_NAME");
        String bookId = intent.getStringExtra("BOOK_ID");
        String author = intent.getStringExtra("AUTHOR");

        // setting all the views
        textTitle.setText(title);
        textAuthor.setText("Author: " + author);
        textDescription.setText("Description: " + description);
        textIsbn.setText("ISBN: " + isbn);
        textOwner_name.setText("Owner: " + ownerName);
        textStatus.setText("Status: Available");

        // viewBookPhotoActivity to set the book Image
        ViewBookPhotoActivity.setImage(bookId, bookImage);

    }
}