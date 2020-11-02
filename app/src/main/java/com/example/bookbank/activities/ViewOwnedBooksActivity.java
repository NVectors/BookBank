package com.example.bookbank.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookbank.R;
import com.example.bookbank.models.Book;
//import com.example.bookbank.models.Request;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import javax.annotation.Nullable;

public class ViewOwnedBooksActivity extends AppCompatActivity {
    private FirebaseFirestore db;

    private StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri uri;

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

        final StorageReference imageRef = FirebaseStorage.getInstance().getReference("images/" + bookID);

        /** Get references in the layout*/
        final TextView title = findViewById(R.id.book_title);
        final TextView author = findViewById(R.id.author);
        final TextView isbn = findViewById(R.id.isbn);
        final TextView status = findViewById(R.id.status);
        final TextView borrower = findViewById(R.id.borrower);
        final TextView description = findViewById(R.id.description);

        final ImageView bookImage = findViewById(R.id.owner_book_image);
        ViewBookPhotoActivity.setImage(bookID, bookImage);

        /**  Realtime updates, snapshot is the state of the database at any given point of time */
        bookReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            /** Method is executed whenever any new event occurs in the remote database */
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                title.setText(value.getString("title"));
                author.setText("By: " + value.getString("author"));
                isbn.setText("ISBN: " + String.valueOf(value.getData().get("isbn")));
                status.setText("Status: " + value.getString("status"));

                if (value.getString("borrowerId") == ""){
                    borrower.setText("Borrower: None");
                }
                else { // Will have to test this later
                    DocumentReference documentRef = db.collection("User").document(value.getString("borrowerId"));
                    documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        /**
                         * Use DocumentSnapshot to find field value in the document
                         * @param task
                         */
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String name = document.getString("fullname");
                                    // Test
                                    Log.d("NAME", name);

                                    borrower.setVisibility(View.VISIBLE); // Default of Borrower text view
                                    borrower.setText("Borrower: " + name);

                                } else {
                                    Log.d("TAG", "No such document");
                                    borrower.setText("Borrower: FAILED");
                                }
                            } else {
                                Log.d("TAG", "get failed with ", task.getException());
                            }
                        }
                    });
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
                StorageReference photoRef = FirebaseStorage.getInstance().getReference("images/" + bookID);
                photoRef.delete();
            }
        });

        /** Long click on the Book title text to edit */
        title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                editDescription(title, author, isbn, description, bookID);
                return false;
            }
        });

        /** Long click on the Book author text to edit */
        author.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                editDescription(title, author, isbn, description, bookID);
                return false;
            }
        });

        /** Long click on the Book ISBN text to edit */
        isbn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                editDescription(title, author, isbn, description, bookID);
                return false;
            }
        });

        /** Long click on the Book description text to edit */
        description.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                editDescription(title, author, isbn, description, bookID);
                return false;
            }
        });

        /** Image delete button is clicked */
        final Button deleteImage = findViewById(R.id.delete_image);
        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageReference photoRef = FirebaseStorage.getInstance().getReference("images/" + bookID);
                photoRef.delete();
                ViewBookPhotoActivity.setImage(bookID, bookImage);
            }
        });
    }

    /**
     * Start new activity screen and pass along the TextView data to allow editing of the field strings
     */
    private void editDescription(TextView title, TextView author, TextView isbn, TextView description, String bookID) {
        Intent intent = new Intent(ViewOwnedBooksActivity.this, EditDescriptionActivity.class);
        intent.putExtra("BOOK_ID", bookID);
        intent.putExtra("TITLE", title.getText().toString());
        intent.putExtra("AUTHOR", author.getText().toString());
        intent.putExtra("ISBN", isbn.getText().toString());
        intent.putExtra("DESCRIPTION", description.getText().toString());
        startActivity(intent);
    }
}