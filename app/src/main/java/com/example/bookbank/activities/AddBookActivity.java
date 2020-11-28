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
import com.example.bookbank.helperClasses.FetchBooks;
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

    /** Barcode String*/
    private String Barcode;

    private EditText description;
    private EditText title;
    private TextView titleError;
    private EditText isbn;
    private TextView isbnError;
    private EditText author;
    private TextView authorError;
    private FirebaseFirestore firestore;
    private Button scanBarcodeButton;

    private StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri uri;

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
        scanBarcodeButton = findViewById(R.id.addBookBarcode);

        storageReference = FirebaseStorage.getInstance().getReference("images");

        /** Get intent String of Barcode and set the fields, if passed */
        Intent intent = getIntent();
        if(intent.hasExtra("BARCODE")){
            Barcode = intent.getStringExtra("BARCODE");
            isbn.setText(Barcode);
            searchBooks(Barcode);

        }

        /** On click Listener for 'Scan Barcode' Button */
        scanBarcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent barcodeActivity = new Intent(AddBookActivity.this,ScanBarcodeActivity.class);
//                startActivity(barcodeActivity);
                isbn.setText("9780826215499");
                searchBooks("9780826215499");
            }
        });


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
                openImageSelect();
            }
        });
    }

    private void searchBooks(String barcode) {
        new FetchBooks(title,author,description,barcode).execute(barcode);
    }

    /**
     * Creates a new intent to choose a image from the device
     */
    private void openImageSelect() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Gets the uri of the selected image
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            uri = data.getData();
        }
    }

    /**
     * This function is responsible for uploading the selected image
     * @param id This is the id of the book that the image is being attached too,
     *           the image is also stored with the id as it's name.
     */
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
                        //startActivity(new Intent(AddBookActivity.this, OwnerBooksActivity.class));
                        uploadImage(id);
                        finish();
                    }
                }
            });
        }
    }

}

