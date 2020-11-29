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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bookbank.R;
import com.example.bookbank.adapters.SearchBooksAdapter;
import com.example.bookbank.models.Book;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class SearchBooksActivity extends AppCompatActivity {

    //declaring the keyWord to search
    String keyWord;
    String ownerNameText;

    // Declaring the variables needed for the list
    ListView searchList;
    ArrayAdapter<Book> bookAdapter;
    ArrayList<Book> bookArrayList;
    SearchBooksAdapter searchBooksAdapter;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_books);

        // getting the intent and checking if it has the keyword. If not set it to empty string
        Intent intent = getIntent();
        if(intent.hasExtra("KEYWORD")){
            keyWord = intent.getStringExtra("KEYWORD");
        }

        else{
            keyWord = "";
        }

        // initializing the search_button and the search_field
        Button search_button = findViewById(R.id.search_button) ;
        final EditText search_field = findViewById(R.id.search_field);

        // initializing the firebase db
        final String TAG = "Search";
        final FirebaseFirestore db;

        // setting the view,arraylist and adapter for the list
        searchList = findViewById(R.id.search_list);
        bookArrayList = new ArrayList<>();
        bookAdapter = new SearchBooksAdapter(this,R.layout.search_book_content , bookArrayList);

        // setting the adapter
        searchList.setAdapter(bookAdapter);

        // getting a DB instance and getting a reference to the 'Book' collection.
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        final CollectionReference collectionReference = db.collection("Book");

        // Adding onClickListener to the button to search a new Keyword with Intent data
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newKeyWord = search_field.getText().toString();

                // checking to make sure button only works when field is empty
                if(newKeyWord.trim().length()!= 0){
                    Intent intent = new Intent(SearchBooksActivity.this, SearchBooksActivity.class);
                    intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                    intent.putExtra("KEYWORD",newKeyWord);
                    startActivity(intent);
                    finish();
                }
            }
        });

        // getting a snapshot of the DB
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                bookArrayList.clear();

                // iterating each document in the Book collection to get all the book's attributes
                for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                    Log.d(TAG,String.valueOf(doc.getData().get("id")));
                    Log.d(TAG,String.valueOf(doc.getData().get("title")));
                    Log.d(TAG,String.valueOf(doc.getData().get("status")));

                    // making sure we only query the book which are available
                    String status = (String) doc.getData().get("status");
                    if(status.equals("Available") || status.equals("Requested") ){
                        // fetching all the attributes of the book
                        String author = (String) doc.getData().get("author");
                        String borrowerId = (String) doc.getData().get("borrowerId");
                        String description = (String) doc.getData().get("description");
                        String id = (String) doc.getData().get("id");
                        long isbn = Long.parseLong(String.valueOf(doc.getData().get("isbn")));
                        String ownerId = (String) doc.getData().get("ownerId");
                        String title = (String) doc.getData().get("title");

                        // checking if user has that book requested already
                        final Book book = new Book(id,title,author,isbn,description,status,ownerId,borrowerId, false, false, false);

                        if(status.equals("Requested")) {
                            // check all books to see if they are on there
                            db.collection("Request").whereEqualTo("bookId", id).whereEqualTo("requesterId", firebaseAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    Log.d("debug", String.valueOf(queryDocumentSnapshots.size()));
                                    if (queryDocumentSnapshots.size() != 0) {
                                        book.setStatus("Requested");
                                    }
                                }
                            });
                        }

                        // if keyword is empty string all available books are added as default
                        if(keyWord.equals("")){
                            bookArrayList.add(book);
                        }

                        // if there's a search keyword we search for the keyword in title, author and ISBN fields
                        else{
                            // making author, title and the keyword lowercase for both case searching
                            String lowerTitle = title.toLowerCase();
                            String lowerAuthor = author.toLowerCase();
                            keyWord = keyWord.trim().toLowerCase();

                            // searching with regex
                            if( lowerTitle.contains(keyWord) || lowerAuthor.contains(keyWord)
                                || String.valueOf(isbn).contains(keyWord))
                            {
                                bookArrayList.add(book);
                            }
                        }
                    }
                }
                // notifying the adapter for the change
                bookAdapter.notifyDataSetChanged();

            }
        });

        // opening ViewSearchBookDetails Activity on Click
        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // get the book at this position
                Book book = bookArrayList.get(position);

                Intent intent = new Intent(SearchBooksActivity.this, ViewSearchBookDetails.class);

                 // putting other extras
                intent.putExtra("OWNER_ID",book.getOwnerId());
                intent.putExtra("TITLE", book.getTitle());
                intent.putExtra("ISBN", String.valueOf(book.getIsbn()));
                intent.putExtra("DESCRIPTION", book.getDescription());
                intent.putExtra("AUTHOR",book.getAuthor());
                intent.putExtra("BOOK_ID", book.getId());


                // string the new activity
                startActivity(intent);

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
                startActivity(new Intent(SearchBooksActivity.this, EditProfileActivity.class));
                break;
            case R.id.nav_my_books:
                startActivity(new Intent(SearchBooksActivity.this, OwnerBooksActivity.class));
                break;
            case R.id.nav_borrowed_books:
                startActivity(new Intent(SearchBooksActivity.this, BorrowedBooksActivity.class));
                break;
            case R.id.nav_search_books:
                startActivity(new Intent(SearchBooksActivity.this, SearchBooksActivity.class));
                break;
            case R.id.nav_notifications:
                startActivity(new Intent(SearchBooksActivity.this, NotificationsActivity.class));
                break;
            case R.id.nav_search_users:
                startActivity(new Intent(SearchBooksActivity.this, SearchUsernameActivity.class));
                break;
            case R.id.nav_my_requests:
                startActivity(new Intent(SearchBooksActivity.this, MyCurrentRequestsActivity.class));
                break;
            case R.id.nav_sign_out:
                firebaseAuth.signOut();
                Toast.makeText(SearchBooksActivity.this, "Successfully Signed Out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SearchBooksActivity.this, LoginActivity.class));
                break;
            default:
                break;
        }
        return true;
    }

}
