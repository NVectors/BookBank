package com.example.bookbank.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.bookbank.R;
import com.example.bookbank.models.Book;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddBookActivity extends AppCompatActivity {

    private EditText description;
    private EditText title;
    private EditText isbn;
    private EditText author;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        title = findViewById(R.id.titleEditText);
        author = findViewById(R.id.authorEditText);
        isbn = findViewById(R.id.isbnEditText);
        description = findViewById(R.id.descriptionEditText);

        Button addBook = findViewById(R.id.addBookButton);
        addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBook();
            }
        });

        Button addBookCancel = findViewById(R.id.addBookCancelButton);
        addBookCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddBookActivity.this, OwnerBooksActivity.class));
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    public void addBook(){
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String ownerId = currentUser.getUid();
        String id = "";
        String borrowerId = "";
        firestore.collection("Book").document(id).set(
                new Book(
                        id,
                        title.getText().toString(),
                        author.getText().toString(),
                        Integer.parseInt(isbn.getText().toString()),
                        description.getText().toString(),
                        ownerId,
                        borrowerId
                )
        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    startActivity(new Intent(AddBookActivity.this, OwnerBooksActivity.class));
                }
            }
        });

    }

}