package com.example.bookbank.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.example.bookbank.R;
import com.example.bookbank.models.Book;
import com.example.bookbank.models.Request;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ViewOwnedBooksActivity extends AppCompatActivity {
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_owned_books);

        /** Get book id of the book that clicked in the list view of OwnerBooksActivity */
        final String bookID = getIntent().getStringExtra("BOOK_ID");

        /** Get instance of Firestore */
        db = FirebaseFirestore.getInstance();


        /** Get top level reference to the book in collection  by ID */
        final DocumentReference bookReference = db.collection("Book").document(bookID);

        /** Get references in the layout*/
        final TextView title = findViewById(R.id.book_title);
        final TextView author = findViewById(R.id.author);
        final TextView isbn = findViewById(R.id.isbn);
        final TextView status = findViewById(R.id.status);
        final TextView borrower = findViewById(R.id.borrower);
        final TextView description = findViewById(R.id.description);

        /**  Realtime updates, snapshot is the state of the database at any given point of time */
        bookReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            /** Method is executed whenever any new event occurs in the remote database */
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                title.setText(value.getString("title"));
                author.setText(value.getString("author"));
                isbn.setText(String.valueOf(value.getData().get("isbn")));
                status.setText("Status: " + value.getString("status"));
                if (value.getString("borrower") == null){
                    borrower.setText("Borrower: ");
                }
                else   {
                    borrower.setText("Borrower: " + value.getString("borrower"));
                }
                description.setText(value.getString("description"));
            }
        });

        /** Request button is clicked */
        final Button request = findViewById(R.id.request_button);
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewOwnedBooksActivity.this, RequestsActivity.class));
            }
        });

        /** Delete button is clicked */
        final Button delete = findViewById(R.id.delete_button);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Book").document(bookID).delete();
            }
        });
    }
}