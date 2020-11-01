package com.example.bookbank.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.ArrayAdapter;
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

    // Declaring the keyword to search
    final String keyWord = "Hobbit";
    // Declaring the variables needed
    ListView searchList;
    ArrayAdapter<Book> bookAdapter;
    ArrayList<Book> bookArrayList;

    SearchBooksAdapter searchBooksAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_books);

        final String TAG = "Search";
        FirebaseFirestore db;

        searchList = findViewById(R.id.search_list);

        bookArrayList = new ArrayList<>();

        bookAdapter = new SearchBooksAdapter(this,R.layout.search_book_content , bookArrayList);

        searchList.setAdapter(bookAdapter);

        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("Book");

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

                        if(title.matches(".*\\b"+keyWord+"\\b.*") ||
                                author.matches(".*\\b"+keyWord+"\\b.*") ||
                                String.valueOf(isbn).equals(keyWord))
                        {
                            bookArrayList.add(book);
                        }


                    }


                }

                bookAdapter.notifyDataSetChanged();

            }
        });



    }


}