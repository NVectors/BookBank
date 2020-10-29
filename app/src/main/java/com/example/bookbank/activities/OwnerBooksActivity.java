package com.example.bookbank.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.bookbank.R;
import com.example.bookbank.adapters.OwnerBooksAdapter;
import com.example.bookbank.models.Book;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_books);

        /** Find reference to the ListView */
        bookList = findViewById(R.id.owner_book_list);
        bookDataList = new ArrayList<>();

        bookAdapter = new OwnerBooksAdapter(this, bookDataList);
        bookList.setAdapter(bookAdapter);

        /** Get instance of Firestore */
        db = FirebaseFirestore.getInstance();

        /** Get top level reference to the collection Book */
       final CollectionReference collectionReference = db.collection("Book");

        /** Find reference to the add book button */
        final FloatingActionButton addBookButton = findViewById(R.id.owner_add_book);

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


                    String id = (String) doc.getData().get("id");
                    String title = (String) doc.getData().get("title");
                    String author = (String) doc.getData().get("author");
                    long isbn = Long.parseLong(String.valueOf(doc.getData().get("isbn")));
                    String description = (String) doc.getData().get("description");
                    String status = (String) doc.getData().get("status");
                    String ownerID = (String) doc.getData().get("ownerId");
                    String borrowerID = (String) doc.getData().get("borrowerId");

                    //FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    //if (ownerID.equals(currentUser.getUid())) {
                    bookDataList.add(new Book(id, title, author, isbn, description, status, ownerID, borrowerID)); // Add book from FireStore
                    //}
                }
                bookAdapter.notifyDataSetChanged(); //Notify the adapter of data change
            }
        });

        bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String bookID = bookDataList.get(position).getId();
                Intent intent = new Intent(getBaseContext(), ViewOwnedBooksActivity.class);
                intent.putExtra("BOOK_ID", bookID);
                startActivity(intent);
            }
        });

    }
}