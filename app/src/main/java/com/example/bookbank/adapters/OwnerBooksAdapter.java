package com.example.bookbank.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bookbank.R;
import com.example.bookbank.models.Book;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class OwnerBooksAdapter extends ArrayAdapter {

    private ArrayList<Book> books;
    private  Context context;
    private FirebaseFirestore firestore;

    public OwnerBooksAdapter(Context context, ArrayList<Book> books) {
        super(context, 0, books);
        this.books = books;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.owner_book_content,parent,false);
        }

        firestore  = FirebaseFirestore.getInstance();

        /** Get the position of book in the ArrayList<Book> */
        final Book book = books.get(position);

        /** Get references to the objects in the layout */
        TextView bookTitle = view.findViewById(R.id.owner_book_title);
        TextView bookAuthor = view.findViewById(R.id.owner_book_author);
        TextView bookISBN = view.findViewById(R.id.owner_book_isbn);
        TextView bookStatus = view.findViewById(R.id.owner_book_status);
        final TextView bookBorrower = view.findViewById(R.id.owner_book_borrower);
        ImageView bookImage = view.findViewById(R.id.owner_book_image);

        /** Set references to the book object data */
        bookTitle.setText(book.getTitle());
        bookAuthor.setText("By " + book.getAuthor());
        bookISBN.setText("ISBN: " + book.getIsbn().toString());
        bookStatus.setText("Status: " + book.getStatus());
        bookBorrower.setVisibility(View.INVISIBLE); // Default of Borrower text view
        bookImage.setImageResource(R.drawable.default_book_image); // Default image

        // User borrowerID to get User's full name in database (Need to test later on)
        if (book.getBorrowerId() != "") {
            DocumentReference documentRef = firestore.collection("User").document(book.getBorrowerId());
            documentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String name = document.getString("fullname");
                            // Test
                            Log.d("NAME", name);

                            bookBorrower.setVisibility(View.VISIBLE); // Default of Borrower text view
                            bookBorrower.setText("Borrower: " + name);

                        } else {
                            Log.d("TAG", "No such document");
                            bookBorrower.setText("Borrower: FAILED");
                        }
                    } else {
                        Log.d("TAG", "get failed with ", task.getException());
                    }
                }
            });
        }
        return view;
    }
}
