package com.example.bookbank.adapters;

import android.content.Context;
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
import com.example.bookbank.activities.ViewBookPhotoActivity;
import com.example.bookbank.models.Book;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class BorrowedBooksAdapter extends ArrayAdapter {

    private ArrayList<Book> bookList;
    private Context context;
    private FirebaseFirestore firestore;

    public BorrowedBooksAdapter(@NonNull Context context, @NonNull ArrayList<Book> bookList) {
        super(context, 0, bookList);
        this.bookList = bookList;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // custom array adapter for formatting each item in our list
        // inflate our custom layout (R.layout.gear_list_view) instead of the default view
        // LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // View view = inflater.inflate(R.layout.list_item, null);

        View view = convertView;

        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.borrower_book_content,parent,false);
        }

        /** Get the position of book in the ArrayList<Book> */
        Book book = bookList.get(position);

        firestore = FirebaseFirestore.getInstance();

        /** Get references to the objects in the layout */
        TextView bookTitle = view.findViewById(R.id.borrower_book_title);
        TextView bookAuthor = view.findViewById(R.id.borrower_book_author);
        TextView bookISBN = view.findViewById(R.id.borrower_book_isbn);
        TextView bookStatus = view.findViewById(R.id.borrower_book_status);
        final TextView bookOwner = view.findViewById(R.id.book_owner);
        ImageView bookImage = view.findViewById(R.id.borrower_book_image);

        /** Set references to the book object data */
        bookTitle.setText(book.getTitle());
        bookAuthor.setText("By " + book.getAuthor());
        bookISBN.setText("ISBN: " + book.getIsbn().toString());
        bookStatus.setText("Status: " + book.getStatus());
        if (book.getOwnerId() == "") {
            bookOwner.setText("Owner: None");
        } else { // Will have to test this later
            DocumentReference documentRef = firestore.collection("User").document(book.getOwnerId());
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
                            bookOwner.setText("Owner: " + name);
                        } else {
                            Log.d("TAG", "No such document");
                            bookOwner.setText("Owner: FAILED");
                        }
                    } else {
                        Log.d("TAG", "get failed with ", task.getException());
                    }
                }
            });
        }
        ViewBookPhotoActivity.setImage(book.getId(), bookImage);
        return view;
    }
}
