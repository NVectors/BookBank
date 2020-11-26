package com.example.bookbank.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.bookbank.R;
import com.example.bookbank.adapters.MyCurrentRequestsAdapter;
import com.example.bookbank.adapters.OwnerBooksAdapter;
import com.example.bookbank.adapters.RequestsAdapter;
import com.example.bookbank.models.Book;
import com.example.bookbank.models.Request;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

public class RequestsActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private DocumentReference bookReference;
    private FirebaseAuth firebaseAuth;
    private static final String TAG = "SCANNED";
    private ListView requestsList;
    private ArrayList<Request> requestsDataList;
    private RequestsAdapter requestsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        /** Get references to the layout objects */
        final TextView bookTitle = findViewById(R.id.book_title);
        final TextView bookAuthor = findViewById(R.id.book_author);
        final TextView bookISBN = findViewById(R.id.book_isbn);

        /** Get book id of the book that clicked in the list view of OwnerBooksActivity */
        final String bookID = getIntent().getStringExtra("BOOK_ID");

        /** Get instance of Firestore */
        firestore = FirebaseFirestore.getInstance();

        /** Get top level reference to the book in collection  by ID */
        bookReference = firestore.collection("Book").document(bookID);


        firebaseAuth = FirebaseAuth.getInstance();
        requestsList = findViewById(R.id.requests_list);
        requestsDataList = new ArrayList<>();

        requestsAdapter = new RequestsAdapter(this, R.layout.activity_requests, requestsDataList);
        requestsList.setAdapter(requestsAdapter);

        firestore.collection("Book").document(bookID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Book book = documentSnapshot.toObject(Book.class);
                    bookTitle.setText(book.getTitle());
                    bookAuthor.setText(book.getAuthor());
                    bookISBN.setText(book.getIsbn().toString());
                }
            }
        });

        firestore.collection("Request").whereEqualTo("bookId", bookID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Request newRequest = document.toObject(Request.class);
                        String docId = document.getId();
                        newRequest.setId(docId);
                        requestsDataList.add(newRequest);
                    }
                    requestsAdapter.notifyDataSetChanged();
                } else {
                    Log.d("debug", "Error getting documents: ", task.getException());
                }
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
                startActivity(new Intent(RequestsActivity.this, EditProfileActivity.class));
                break;
            case R.id.nav_my_books:
                startActivity(new Intent(RequestsActivity.this, OwnerBooksActivity.class));
                break;
            case R.id.nav_borrowed_books:
                startActivity(new Intent(RequestsActivity.this, BorrowedBooksActivity.class));
                break;
            case R.id.nav_search_books:
                startActivity(new Intent(RequestsActivity.this, SearchBooksActivity.class));
                break;
            case R.id.nav_notifications:
                startActivity(new Intent(RequestsActivity.this, NotificationsActivity.class));
                break;
            case R.id.nav_search_users:
                startActivity(new Intent(RequestsActivity.this, SearchUsernameActivity.class));
                break;
            case R.id.nav_my_requests:
                startActivity(new Intent(RequestsActivity.this, MyCurrentRequestsActivity.class));
                break;
            case R.id.nav_sign_out:
                firebaseAuth.signOut();
                Toast.makeText(RequestsActivity.this, "succcessfully signed out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RequestsActivity.this, LoginActivity.class));
                break;
            default:
                break;
        }
        return true;
    }
}