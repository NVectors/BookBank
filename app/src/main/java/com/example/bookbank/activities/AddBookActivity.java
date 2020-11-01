package com.example.bookbank.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bookbank.R;
import com.example.bookbank.helperClasses.InputValidator;
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
    private TextView titleError;
    private EditText isbn;
    private TextView isbnError;
    private EditText author;
    private TextView authorError;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        /** Move layout up when keyboard is present */
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        /** Get instance of Firestore */
        firestore = FirebaseFirestore.getInstance();

        /** Get references to the layout objects */
        title = findViewById(R.id.titleEditText);
        titleError = findViewById(R.id.titleError);
        author = findViewById(R.id.authorEditText);
        authorError = findViewById(R.id.authorError);
        isbn = findViewById(R.id.isbnEditText);
        isbnError = findViewById(R.id.isbnError);
        description = findViewById(R.id.descriptionEditText);


        final Button addBook = findViewById(R.id.addBookButton);
        addBook.setOnClickListener(new View.OnClickListener() {
            /**
             * The add button is clicked
             * @param view
             */
            @Override
            public void onClick(View view) {
                addBook();
            }
        });

        final Button addBookCancel = findViewById(R.id.addBookCancelButton);
        addBookCancel.setOnClickListener(new View.OnClickListener() {
            /**
             * The cancel button is clicked, go back to main activity screen
             * @param view
             */
            @Override
            public void onClick(View view) {
                finish();
                //startActivity(new Intent(AddBookActivity.this, OwnerBooksActivity.class));
            }
        });

        final Button add_image = findViewById(R.id.addImageButton);
        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement getting photos from phone storage
            }
        });
    }

    /**
     * Check if there is any empty fields for input
     * @return
     */
    public boolean validate() {
        boolean[] inputs = {
                InputValidator.notEmpty(title, titleError),
                InputValidator.notEmpty(author, authorError),
                InputValidator.notEmpty(isbn, isbnError),
                InputValidator.isIsbn(isbn, isbnError)
        };
        return InputValidator.validateInputs(inputs);
    }

    /**
     *  Make a new book object and add as a document to the collection "Book"
     */
    public void addBook() {
        if (validate()) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            String ownerId = "";
            if (currentUser != null) {  // Set ownerId to unique User ID
                ownerId = currentUser.getUid();
            }
            // Creating unique User ID
            final String id = UUID.randomUUID().toString();
            // borrowerId will be empty string at creation of a book
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
                /**
                 * Successfully added book as a document to collection "Book",
                 * go back to the main activity screen
                 * @param task
                 */
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        finish();
                        //startActivity(new Intent(AddBookActivity.this, OwnerBooksActivity.class));
                    }
                }
            });
        }
    }
}

