package com.example.bookbank.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.bookbank.R;
import com.example.bookbank.adapters.OwnerBooksAdapter;
import com.example.bookbank.models.Book;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
        String []author = {"Jeffrey Archer", "Arnold Bennett"};
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

        /** Get top level reference to the collection Book */
        CollectionReference collectionReference = db.collection("Book");

        /** Find reference to the add book button */
        FloatingActionButton addBookButton = findViewById(R.id.owner_add_book);

        /** If the add book button is clicked */
        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OwnerBooksActivity.this, AddBookActivity.class));
            }
        });


        /**  Realtime updates, snapshot is the state of the database at any given point of time */
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            /** Method is executed whenever any new event occurs in the remote database */
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {

                // Clear the old list
                bookDataList.clear();

                for (QueryDocumentSnapshot doc: queryDocumentSnapshots)
                {
                    // Tests
                    Log.d("SAMPLE", String.valueOf(doc.getData().get("id")));
                    Log.d("SAMPLE", String.valueOf(doc.getData().get("isbn")));

                    /**
                    String id = (String) doc.getData().get("id");
                    String title = (String) doc.getData().get("title");
                    String author = (String) doc.getData().get("author");
                    String isbn = (String) doc.getData().get("isbn");
                    Integer ISBN = 1;
                    String description = (String) doc.getData().get("description");
                    String status = "";
                    String ownerID = (String) doc.getData().get("ownerId");
                    String borrowerID = (String) doc.getData().get("borrowerId");


                    bookDataList.add(new Book(id, title, author, ISBN, description, status, ownerID, borrowerID)); // Add book from FireStore */
                }
                bookAdapter.notifyDataSetChanged(); //Notify the adapter of data change
            }
        });

        bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(OwnerBooksActivity.this, ViewOwnedBooksActivity.class));
            }
        });

    }
}