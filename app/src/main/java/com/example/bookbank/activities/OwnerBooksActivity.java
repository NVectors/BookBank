package com.example.bookbank.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class OwnerBooksActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    public ListView bookList; //changed to public -> solo need to access this.
    private ArrayAdapter<Book> bookAdapter;
    private ArrayList<Book> bookDataList;
    private ArrayList<Book> originalBookDataList;
    private String tempStatus = "Show All";

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_books);

        /** Find reference to the ListView */
        bookList = findViewById(R.id.owner_book_list);
        bookDataList = new ArrayList<>();
        originalBookDataList = new ArrayList<>();

        bookAdapter = new OwnerBooksAdapter(this, R.layout.owner_book_content, bookDataList);
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
                originalBookDataList.clear();

                for (QueryDocumentSnapshot doc: queryDocumentSnapshots)
                {
                    // Tests
                    Log.d("ID", String.valueOf(doc.getData().get("id")));
                    Log.d("TITLE", String.valueOf(doc.getData().get("title")));
                    Log.d("AUTHOR", String.valueOf(doc.getData().get("author")));
                    Log.d("ISBN", String.valueOf(doc.getData().get("isbn")));
//
                    String id = (String) doc.getData().get("id");
                    String title = (String) doc.getData().get("title");
                    String author = (String) doc.getData().get("author");
                    long isbn = Long.parseLong(String.valueOf(doc.getData().get("isbn")));
                    String description = (String) doc.getData().get("description");
                    String status = (String) doc.getData().get("status");
                    String ownerID = (String) doc.getData().get("ownerId");
                    String borrowerID = (String) doc.getData().get("borrowerId");
//
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (ownerID.equals(currentUser.getUid())) { //Display books that only belong to that user
                        bookDataList.add(new Book(id, title, author, isbn, description, status, ownerID, borrowerID)); // Add book from FireStore
                        originalBookDataList.add(new Book(id, title, author, isbn, description, status, ownerID, borrowerID));
                    }
                }
                bookAdapter.notifyDataSetChanged(); //Notify the adapter of data change
            }
        });

        bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String bookID = bookDataList.get(position).getId();
                Intent intent = new Intent(OwnerBooksActivity.this, ViewOwnedBooksActivity.class);
                intent.putExtra("BOOK_ID", bookID);
                startActivity(intent);
            }
        });

        /** Find reference to the spinner */
        Spinner spinner = findViewById(R.id.owner_book_filter);

        /** Create an array adapter for the spinner. Create from bookStatus in strings.xml */
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.bookStatus, android.R.layout.simple_spinner_item);

        /** simple_spinner_dropdown_item from android.R.layout */
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        /** Find reference to the filter button */
        Button filterButton = findViewById(R.id.owner_filter_button);

        /** When filter button is clicked */
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Book> toDisplayBook = new ArrayList<>();
                for (Book book: originalBookDataList){
                    if (tempStatus.equals("Show All")){
                        toDisplayBook.addAll(originalBookDataList);
                        break;
                    }
                    else if (book.getStatus().equals(tempStatus)){
                        toDisplayBook.add(book);
                    }
                }
                bookDataList.clear();
                bookDataList.addAll(toDisplayBook);
                bookAdapter.notifyDataSetChanged();
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
                startActivity(new Intent(OwnerBooksActivity.this, EditProfileActivity.class));
                break;
            case R.id.nav_my_books:
                startActivity(new Intent(OwnerBooksActivity.this, OwnerBooksActivity.class));
                break;
            case R.id.nav_borrowed_books:
                startActivity(new Intent(OwnerBooksActivity.this, BorrowedBooksActivity.class));
                break;
            case R.id.nav_search_books:
                startActivity(new Intent(OwnerBooksActivity.this, SearchBooksActivity.class));
                break;
            case R.id.nav_notifications:
                startActivity(new Intent(OwnerBooksActivity.this, NotificationsActivity.class));
                break;
            case R.id.nav_search_users:
                startActivity(new Intent(OwnerBooksActivity.this, SearchUsernameActivity.class));
                break;
            case R.id.nav_my_requests:
                startActivity(new Intent(OwnerBooksActivity.this, RequestsActivity.class));
                break;
            case R.id.nav_sign_out:
                firebaseAuth.signOut();
                Toast.makeText(OwnerBooksActivity.this, "succcessfully signed out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(OwnerBooksActivity.this, LoginActivity.class));
                break;
            default:
                break;
        }
        return true;
    }

    /**
     *  Retrieve the value of the spinner when filter button is clicked.
     *  If user didn't specify spinner value; spinner value default to "Available/Borrowed".
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text = adapterView.getItemAtPosition(i).toString();
        tempStatus = text;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

}