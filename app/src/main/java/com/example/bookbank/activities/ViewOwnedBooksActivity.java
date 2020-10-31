package com.example.bookbank.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.bookbank.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ViewOwnedBooksActivity extends AppCompatActivity {
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_owned_books);

        /** Move layout up when keyboard is present */
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

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
        final TextView edit_title = findViewById(R.id.edit_book_title);
        final TextView edit_author = findViewById(R.id.edit_author);
        final TextView edit_isbn = findViewById(R.id.edit_isbn);
        final TextView edit_description = findViewById(R.id.edit_description);
        final Button done_edit = findViewById(R.id.done_button);
        final Button cancel_edit = findViewById(R.id.cancel_button);

        /** By default these objects in layout are hidden until an long click action is triggered */
        edit_title.setVisibility(View.INVISIBLE);
        edit_author.setVisibility(View.INVISIBLE);
        edit_isbn.setVisibility(View.INVISIBLE);
        edit_description.setVisibility(View.INVISIBLE);
        done_edit.setVisibility(View.INVISIBLE);
        cancel_edit.setVisibility(View.INVISIBLE);

        /**  Realtime updates, snapshot is the state of the database at any given point of time */
        bookReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            /** Method is executed whenever any new event occurs in the remote database */
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                title.setText(value.getString("title"));
                author.setText("By: " + value.getString("author"));
                isbn.setText("ISBN: " + String.valueOf(value.getData().get("isbn")));
                status.setText("Status: " + value.getString("status"));
                if (value.getString("borrower") == null){
                    borrower.setText("Borrower: None");
                }
                else { // Will have to test this later
                    String name = db.collection("User").document(value.getString("borrowerId")).get().getResult().get("fullname").toString();
                    // Tests
                    Log.d("SAMPLE", name);

                    borrower.setText("Borrower: " + name);
                }
                description.setText("Description: " + value.getString("description"));
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

        /** Long click on the Book title text to edit */
        title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                revertAppearanceChange(title, edit_title, done_edit, cancel_edit, null);

                cancel_edit.setOnClickListener(new View.OnClickListener() {
                    /**
                     * Cancel button is clicked, don't update the document and go back to TextView
                     * @param v
                     */
                    @Override
                    public void onClick(View v) {
                        revertAppearanceChange(title, edit_title, done_edit, cancel_edit, null);
                    }
                });
                done_edit.setOnClickListener(new View.OnClickListener() {
                    /**
                     * Done button is clicked, update the field of the document with the data of the Edit Text
                     * @param v
                     */
                    @Override
                    public void onClick(View v) {
                        String new_text = edit_title.getText().toString();
                        db.collection("Book").document(bookID).update("title", new_text);
                        revertAppearanceChange(title, edit_title, done_edit, cancel_edit, new_text);
                    }
                });
                return false;
            }
        });

        /** Long click on the Book author text to edit */
        author.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String old_text = author.getText().toString();
                String old_author =  old_text.replace("By: ", "");
                appearanceChange(author, edit_author, done_edit, cancel_edit, old_author);

                cancel_edit.setOnClickListener(new View.OnClickListener() {
                    /**
                     * Cancel button is clicked, don't update the document and go back to TextView
                     * @param v
                     */
                    @Override
                    public void onClick(View v) {
                        revertAppearanceChange(author, edit_author, done_edit, cancel_edit, null);
                    }
                });
                done_edit.setOnClickListener(new View.OnClickListener() {
                    /**
                     * Done button is clicked, update the field of the document with the data of the Edit Text
                     * @param v
                     */
                    @Override
                    public void onClick(View v) {
                        String new_text = edit_author.getText().toString();
                        db.collection("Book").document(bookID).update("author", new_text);
                        appearanceChange(author, edit_author, done_edit, cancel_edit, new_text);
                    }
                });
                return false;
            }
        });

        /** Long click on the Book ISBN text to edit */
        isbn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String old_text = isbn.getText().toString();
                String old_isbn =  old_text.replace("ISBN: ", "");
                appearanceChange(isbn, edit_isbn, done_edit, cancel_edit, old_isbn);

                cancel_edit.setOnClickListener(new View.OnClickListener() {
                    /**
                     * Cancel button is clicked, don't update the document and go back to TextView
                     * @param v
                     */
                    @Override
                    public void onClick(View v) {
                        revertAppearanceChange(isbn, edit_isbn, done_edit, cancel_edit, null);
                    }
                });
                edit_isbn.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if(keyCode == KeyEvent.KEYCODE_ENTER ) {
                            String new_text = edit_isbn.getText().toString();
                            long new_isbn = Long.parseLong(new_text);
                            db.collection("Book").document(bookID).update("isbn", new_isbn);
                            revertAppearanceChange(isbn, edit_isbn, done_edit, cancel_edit, new_text);
                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            return true;
                        }
                        return false;
                    }
                });

                done_edit.setOnClickListener(new View.OnClickListener() {
                    /**
                     * Done button is clicked, update the field of the document with the data of the Edit Text
                     * @param v
                     */
                    @Override
                    public void onClick(View v) {
                        String new_text = edit_isbn.getText().toString();
                        long new_isbn = Long.parseLong(new_text);
                        db.collection("Book").document(bookID).update("isbn", new_isbn);
                        revertAppearanceChange(isbn, edit_isbn, done_edit, cancel_edit, new_text);
                    }
                });
                return false;
            }
        });

        /** Long click on the Book description text to edit */
        description.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String old_text = description.getText().toString();
                String old_description =  old_text.replace("Description: ", "");
                appearanceChange(description, edit_description, done_edit, cancel_edit, old_description);

                cancel_edit.setOnClickListener(new View.OnClickListener() {
                    /**
                     * Cancel button is clicked, don't update the document and go back to TextView
                     * @param v
                     */
                    @Override
                    public void onClick(View v) {
                        revertAppearanceChange(description, edit_description, done_edit, cancel_edit, null);
                    }
                });
                done_edit.setOnClickListener(new View.OnClickListener() {
                    /**
                     * Done button is clicked, update the field of the document with the data of the Edit Text
                     * @param v
                     */
                    @Override
                    public void onClick(View v) {
                        String new_text = edit_description.getText().toString();
                        db.collection("Book").document(bookID).update("description", new_text);
                        revertAppearanceChange(description, edit_description, done_edit, cancel_edit, new_text);
                    }
                });
                return false;
            }
        });
    }

    /**
     * Hide EditText/Buttons and make TextView visible.
     * @param current
     * @param editCurrent
     * @param done
     * @param cancel
     * @param text
     */
    private void revertAppearanceChange(TextView current, TextView editCurrent, Button done, Button cancel, String text) {
        if (text != null) { // Input text not empty
            current.setText(text); // Set the text of  the Text View to input text
        }
        current.setVisibility(View.VISIBLE);
        editCurrent.setVisibility(View.INVISIBLE);
        done.setVisibility(View.INVISIBLE);
        cancel.setVisibility(View.INVISIBLE);
    }

    /**
     * Hide the TextView and make EditText/Buttons visible.
     * @param current
     * @param editCurrent
     * @param done
     * @param cancel
     * @param text
     */
    private void appearanceChange(TextView current, TextView editCurrent, Button done, Button cancel, String text) {
        if (text != null) { // Input text not empty
            editCurrent.setText(text); // Set the text of  the Edit Text to input text
        }
        current.setVisibility(View.INVISIBLE);
        editCurrent.setVisibility(View.VISIBLE);
        done.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.VISIBLE);
    }
}