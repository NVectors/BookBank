package com.example.bookbank.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.bookbank.R;
import com.example.bookbank.models.Book;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

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

        /** Move layout up when keyboard is present */
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

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

        // canceling adding book
        Button addBookCancel = findViewById(R.id.addBookCancelButton);
        addBookCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddBookActivity.this, OwnerBooksActivity.class));
            }
        });

        // need photograph button

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    public void addBook(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String ownerId = "";
        if (currentUser != null) {
            ownerId = currentUser.getUid();
        }
        // creating unique id
        final String id = UUID.randomUUID().toString();
        // borrowerId will be empty string at creating of book
        String borrowerId = "";
        firestore.collection("Book").document(id).set(
                new Book(
                        id,
                        title.getText().toString(),
                        author.getText().toString(),
                        Integer.parseInt(isbn.getText().toString()),
                        description.getText().toString(),
                        "Available",
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