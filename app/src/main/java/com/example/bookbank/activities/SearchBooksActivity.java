package com.example.bookbank.activities;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.bookbank.R;
import com.example.bookbank.adapters.SearchBooksAdapter;
import com.example.bookbank.models.BookSearch;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class SearchBooksActivity extends AppCompatActivity {

    // Declaring the variables needed
    ListView searchList;
    ArrayAdapter<BookSearch> bookAdapter;
    ArrayList<BookSearch> bookArrayList;

    SearchBooksAdapter searchBooksAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_books);

        searchList = findViewById(R.id.search_list);

        bookArrayList = new ArrayList<>();

        bookArrayList.add(new BookSearch("1","2","3","4","5","6"));

        bookAdapter = new SearchBooksAdapter(this,R.layout.search_book_content , bookArrayList);

        searchList.setAdapter(bookAdapter);



    }
}