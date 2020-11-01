package com.example.bookbank.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookbank.R;
import com.example.bookbank.helperClasses.InputValidator;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditDescriptionActivity extends AppCompatActivity {

    private EditText editTitle;
    private EditText editAuthor;
    private EditText editISBN;
    private EditText editDescription;
    private TextView titleError;
    private TextView authorError;
    private TextView isbnError;
    private String bookID;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book_description);

        /** Move layout up when keyboard is present */
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        /** Get data passed on from ViewOwnedBooksActivity.class  */
        bookID = getIntent().getStringExtra("BOOK_ID");
        final String oldTitle = getIntent().getStringExtra("TITLE");
        final String oldAuthor = getIntent().getStringExtra("AUTHOR");
        final String oldISBN = getIntent().getStringExtra("ISBN");
        final String oldDescription = getIntent().getStringExtra("DESCRIPTION");

        /** Get references to layout objects */
        editTitle = findViewById(R.id.edit_title);
        editAuthor = findViewById(R.id.edit_author);
        editISBN = findViewById(R.id.edit_isbn);
        editDescription = findViewById(R.id.edit_description);
        titleError = findViewById(R.id.titleError);
        authorError = findViewById(R.id.authorError);
        isbnError = findViewById(R.id.isbnError);

        /** Set Edit Text text value */
        editTitle.setText(oldTitle);
        editAuthor.setText(oldAuthor.replace("By: ", ""));
        editISBN.setText(oldISBN.replace("ISBN: ",""));
        editDescription.setText(oldDescription.replace("Description: ", ""));

        /** Get instance of Firestore */
        db = FirebaseFirestore.getInstance();

        final Button cancel = findViewById(R.id.cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            /**
             * The cancel button is clicked, go back to main activity screen
             * @param view
             */
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final Button done = findViewById(R.id.done_button);
        done.setOnClickListener(new View.OnClickListener() {
            /**
             * The done button is clicked, update document in Firestore and go back to main activity screen
             * @param view
             */
            @Override
            public void onClick(View view) {
               updateBook();
            }
        });

    }

    private void updateBook() {
        if (validate()) {
            /** Set variables with the Edit Text data */
            Long newISBN = Long.parseLong(editISBN.getText().toString());
            String newTitle = editTitle.getText().toString();
            String newAuthor = editAuthor.getText().toString();
            String newDescription = editDescription.getText().toString();

            /** Update the fields for the document in firestore */
            db.collection("Book").document(bookID).update("title", newTitle);
            db.collection("Book").document(bookID).update("author", newAuthor);
            db.collection("Book").document(bookID).update("isbn", newISBN);
            db.collection("Book").document(bookID).update("description", newDescription);
            finish();
        }
    }

    /**
     * Check if there is any empty fields for input
     * @return
     */
    public boolean validate() {
        boolean[] inputs = {
                InputValidator.notEmpty(editTitle, titleError),
                InputValidator.notEmpty(editAuthor, authorError),
                InputValidator.notEmpty(editISBN, isbnError),
                InputValidator.isIsbn(editISBN, isbnError)
        };
        return InputValidator.validateInputs(inputs);
    }
}
