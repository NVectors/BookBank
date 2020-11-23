package com.example.bookbank.adapters;

import android.annotation.SuppressLint;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchBooksAdapter extends ArrayAdapter<Book> {

    private int resource;
    private Context context;
    private ArrayList<Book> bookList;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    public SearchBooksAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Book> bookList) {
        super(context, resource, bookList);
        this.bookList = bookList;
        this.context = context;
        this.resource = resource;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // checking if convertView == null
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(context).inflate(resource, parent,false);
        }

        db  = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // getting the book at the position
        final Book book = bookList.get(position);


        // initializing all the text views
        TextView title = view.findViewById(R.id.title_text);
        TextView author = view.findViewById(R.id.author_text);
        TextView isbn = view.findViewById(R.id.isbn_text);
        final TextView status = view.findViewById(R.id.status_text);
        final TextView ownerName = view.findViewById(R.id.ownerName_text);
        ImageView bookPhoto = view.findViewById(R.id.search_photo);

        // Creating document reference to the ownerId user
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("User").document(String.valueOf(book.getOwnerId()));

        // getting ownerName
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String ownerNameText = documentSnapshot.getString("fullname");
                    ownerName.setText(ownerNameText);
                }
            }
        });

        // setting text in all the 5 textviews
        title.setText(book.getTitle());
        author.setText("By " + book.getAuthor());
        isbn.setText("ISBN: " + String.valueOf(book.getIsbn()));

        // if status is requested check if current user is the one that requested it if not set status as available still
        String bookStatus = book.getStatus();
        if(bookStatus.equals("Requested")) {
            db.collection("Request").whereEqualTo("bookId", book.getId()).whereEqualTo("requesterId", firebaseAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    Log.d("debug", String.valueOf(queryDocumentSnapshots.size()));
                    if (queryDocumentSnapshots.size() == 0) {
                        status.setText("Status: " + "Available");
                    } else {
                        status.setText("Status: " + "Requested");
                    }
                }
            });
        } else {
            status.setText("Status: " + book.getStatus());
        }

        // viewBookPhotoActivity to set the book Image
        ViewBookPhotoActivity.setImage(book.getId(), bookPhoto);


        return view;
    }
}
