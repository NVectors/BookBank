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
import com.example.bookbank.adapters.BorrowedBooksAdapter;
import com.example.bookbank.adapters.OwnerBooksAdapter;
import com.example.bookbank.models.Book;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class BorrowedBooksActivity extends AppCompatActivity {

    ListView bookList;
    ArrayAdapter<Book> bookAdapter;
    ArrayList<Book> bookDataList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrowed_books);

        /** Find reference to the ListView */
        bookList = findViewById(R.id.borrower_book_list);
        bookDataList = new ArrayList<>();

        bookAdapter = new BorrowedBooksAdapter(this, bookDataList);
        bookList.setAdapter(bookAdapter);

        /** Get instance of Firestore */
        db = FirebaseFirestore.getInstance();

        /** Get top level reference to the collection Book */
        final CollectionReference collectionReference = db.collection("Book");

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
                    Log.d("ID", String.valueOf(doc.getData().get("id")));
                    Log.d("TITLE", String.valueOf(doc.getData().get("title")));
                    Log.d("AUTHOR", String.valueOf(doc.getData().get("author")));
                    Log.d("ISBN", String.valueOf(doc.getData().get("isbn")));

                    String id = (String) doc.getData().get("id");
                    String title = (String) doc.getData().get("title");
                    String author = (String) doc.getData().get("author");
                    long isbn = Long.parseLong(String.valueOf(doc.getData().get("isbn")));
                    String description = (String) doc.getData().get("description");
                    String status = (String) doc.getData().get("status");
                    String ownerID = (String) doc.getData().get("ownerId");
                    String borrowerID = (String) doc.getData().get("borrowerId");

                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (borrowerID.equals(currentUser.getUid())) { //Display books that only belong to that user
                        bookDataList.add(new Book(id, title, author, isbn, description, status, ownerID, borrowerID)); // Add book from FireStore
                    }
                }
                bookAdapter.notifyDataSetChanged(); //Notify the adapter of data change
            }
        });

        bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String bookID = bookDataList.get(position).getId();
                Intent intent = new Intent(getBaseContext(), ViewBorrowedBookActivity.class);
                intent.putExtra("BOOK_ID", bookID);
                startActivity(intent);
            }
        });
    }
}