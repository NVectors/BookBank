package com.example.bookbank.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.bookbank.R;
import com.example.bookbank.adapters.SearchBooksAdapter;
import com.example.bookbank.models.Book;
import com.example.bookbank.models.BookSearch;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class SearchBooksActivity extends AppCompatActivity {

    String keyWord;
    // Declaring the variables needed
    ListView searchList;
    ArrayAdapter<Book> bookAdapter;
    ArrayList<Book> bookArrayList;

    SearchBooksAdapter searchBooksAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_books);

        Intent intent = getIntent();
        Log.d("HEREIAM","INTENT");

        if (intent.getStringExtra("KEYWORD") == null){
            Log.d("HEREIAM","BEFORE");
            if(intent.getStringExtra("KEYWORD") == null){
                Log.d("HEREIAM","HERE");
                keyWord = "";
            }
            else{
                Log.d("HEREIAM","ELSE");
                keyWord = intent.getStringExtra("KEYWORD");
            }

        }

        else{
            Log.d("HEREIAM","NEXT");
            keyWord = intent.getStringExtra("KEYWORD");
        }

       // keyWord = intent.getStringExtra(LoginActivity.EXTRA_MESSAGE);

        // initializing the search_button and the search_field
        Button search_button = findViewById(R.id.search_button) ;
        final EditText search_field = findViewById(R.id.search_field);

        // checking if an intent with extra word was passed, if yes setting that as the keyword
//        if(intent!=null){
//            keyWord = intent.getStringExtra("KEYWORD");
//
//        }
//        else{
//            Log.d("Running","HI");
//            keyWord = "Hobbit";
//        }



        // initializing the firebase db
        final String TAG = "Search";
        FirebaseFirestore db;


        // setting the view,arraylist and adapter for the list
        searchList = findViewById(R.id.search_list);
        bookArrayList = new ArrayList<>();
        bookAdapter = new SearchBooksAdapter(this,R.layout.search_book_content , bookArrayList);

        // setting the adapter
        searchList.setAdapter(bookAdapter);

        // getting a DB instance and getting a reference to the 'Book' collection.
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("Book");

        // Adding onClickListener to the button to search a new Keyword with Intent data
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchBooksActivity.this, SearchBooksActivity.class);
                String newKeyWord = search_field.getText().toString();
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                intent.putExtra("KEYWORD",newKeyWord);
 //               Log.d("NEWKEYIAM",newKeyWord);
                startActivity(intent);
                finish();
            }
        });

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                bookArrayList.clear();

                for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                    Log.d(TAG,String.valueOf(doc.getData().get("id")));
                    Log.d(TAG,String.valueOf(doc.getData().get("title")));
                    Log.d(TAG,String.valueOf(doc.getData().get("status")));

                    String status = (String) doc.getData().get("status");
                    if(status.equals("Available")){
                        String author = (String) doc.getData().get("author");
                        String borrowerId = (String) doc.getData().get("borrowerId");
                        String description = (String) doc.getData().get("description");
                        String id = (String) doc.getData().get("id");
                        long isbn = Long.parseLong(String.valueOf(doc.getData().get("isbn")));
                        String ownerId = (String) doc.getData().get("ownerId");
                        String title = (String) doc.getData().get("title");

                        Book book = new Book(id,title,author,isbn,description,status,ownerId,borrowerId);

                        if(keyWord.equals("")){
                            bookArrayList.add(book);
                        }
                        else{
                            if(title.matches(".*\\b"+keyWord+"\\b.*") ||
                                    author.matches(".*\\b"+keyWord+"\\b.*") ||
                                    String.valueOf(isbn).equals(keyWord))
                            {
                                bookArrayList.add(book);
                            }
                        }



                    }


                }

                bookAdapter.notifyDataSetChanged();

            }
        });



    }

//    public void keyWordSearch(View view) {
//        Intent intent = new Intent(this, SearchBooksActivity.class);
//        EditText search_field = findViewById(R.id.search_field);
//        String newKeyWord = search_field.getText().toString();
//        Log.d("NEWKEY",newKeyWord);
//        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
//        intent.putExtra("KEYWORD",newKeyWord);
//        startActivity(intent);
//        finish();
//    }


}
