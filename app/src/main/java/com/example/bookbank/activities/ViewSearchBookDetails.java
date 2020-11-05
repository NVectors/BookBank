package com.example.bookbank.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.bookbank.R;

public class ViewSearchBookDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_search_book_details);

        Intent intent = getIntent();
        String title = intent.getStringExtra("TITLE");
        String isbn = intent.getStringExtra("ISBN");
        String description = intent.getStringExtra("DESCRIPTION");
        String ownerName = intent.getStringExtra("OWNER_NAME");
        String bookId = intent.getStringExtra("BOOK_ID");
        String author = intent.getStringExtra("AUTHOR");

    }
}