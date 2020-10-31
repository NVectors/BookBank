package com.example.bookbank.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookbank.R;
import com.example.bookbank.helperClasses.InputValidator;
import com.example.bookbank.models.Book;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class AddBookActivity extends AppCompatActivity {

    private EditText description;
    private EditText title;
    private TextView titleError;
    private EditText isbn;
    private TextView isbnError;
    private EditText author;
    private TextView authorError;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    private StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        /** Move layout up when keyboard is present */
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        title = findViewById(R.id.titleEditText);
        titleError = findViewById(R.id.titleError);
        author = findViewById(R.id.authorEditText);
        authorError = findViewById(R.id.authorError);
        isbn = findViewById(R.id.isbnEditText);
        isbnError = findViewById(R.id.isbnError);
        description = findViewById(R.id.descriptionEditText);

        storageReference = FirebaseStorage.getInstance().getReference("images");

        final Button addBook = findViewById(R.id.addBookButton);
        addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBook();
            }
        });

        // canceling adding book
        final Button addBookCancel = findViewById(R.id.addBookCancelButton);
        addBookCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddBookActivity.this, OwnerBooksActivity.class));
            }
        });

        final Button add_image = findViewById(R.id.addImageButton);
        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageSelect();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    private void openImageSelect() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            uri = data.getData();
        }
    }

    private void uploadImage(final String id){
        if (uri != null){
            final StorageReference fileRef = storageReference.child(id);
            fileRef.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(AddBookActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddBookActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No image added", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean validate() {
        boolean[] inputs = {
                InputValidator.notEmpty(title, titleError),
                InputValidator.notEmpty(author, authorError),
                InputValidator.notEmpty(isbn, isbnError),
                InputValidator.isIsbn(isbn, isbnError)
        };
        return InputValidator.validateInputs(inputs);
    }

    public void addBook() {
        if (validate()) {
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
                            Long.parseLong(isbn.getText().toString()),
                            description.getText().toString(),
                            "Available",
                            ownerId,
                            borrowerId
                    )
            ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        uploadImage(id);
                        startActivity(new Intent(AddBookActivity.this, OwnerBooksActivity.class));
                    }
                }
            });
        }
    }

}

