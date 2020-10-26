package com.example.bookbank.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.bookbank.R;
import com.example.bookbank.adapters.OwnerBooksAdapter;
import com.example.bookbank.models.Book;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class OwnerBooksActivity extends AppCompatActivity {

    ListView bookList;
    ArrayAdapter<Book> bookAdapter;
    ArrayList<Book> bookDataList;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_books);

        /** Find reference to the ListView */
        bookList = findViewById(R.id.owner_book_list);

        String []id = {"01", "02"};
        String []titles ={"The Grass is Always Greener", "Murder!"};
        String []author = {"Jeffrey Archer", "Arnold Bennett "};
        Integer []isbn = {1860920497, 1860920128};
        String []description = {"Book is about grass", "Book is about murder"};
        String []status = {"Available", "Available"};
        String []ownerId = {"05", "09"};
        String []borrowerId = {"",""};

        bookDataList = new ArrayList<>();

        for(int i=0;i<titles.length;i++){
            bookDataList.add((new Book(id[i], titles[i], author[i], isbn[i], description[i], status[i], ownerId[i], borrowerId[i])));
        }

        bookAdapter = new OwnerBooksAdapter(this, bookDataList);
        bookList.setAdapter(bookAdapter);

        /** Get instance of Firestore */
        db = FirebaseFirestore.getInstance();

        /** Find reference to the add book button */
        final FloatingActionButton addBookButton = findViewById(R.id.owner_add_book);
        /** If the add book button is clicked */
        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OwnerBooksActivity.this, AddBookActivity.class));
            }
        });
    }
}