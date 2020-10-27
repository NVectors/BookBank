package com.example.bookbank.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
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

import java.util.ArrayList;

public class OwnerBooksAdapter extends ArrayAdapter {

    private ArrayList<Book> books;
    private  Context context;

    public OwnerBooksAdapter(Context context, ArrayList<Book> books) {
        super(context, 0, books);
        this.books = books;
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
            view = LayoutInflater.from(context).inflate(R.layout.owner_book_content,parent,false);
        }

        /** Get the position of book in the ArrayList<Book> */
        Book book = books.get(position);

        /** Get references to the objects in the layout */
        TextView bookTitle = view.findViewById(R.id.owner_book_title);
        TextView bookAuthor = view.findViewById(R.id.owner_book_author);
        TextView bookISBN = view.findViewById(R.id.owner_book_isbn);
        TextView bookStatus = view.findViewById(R.id.owner_book_status);
        TextView bookBorrower = view.findViewById(R.id.owner_book_borrower);
        ImageView bookImage = view.findViewById(R.id.owner_book_image);

        bookTitle.setText(book.getTitle());
        bookAuthor.setText(book.getAuthor());
        bookISBN.setText(book.getIsbn().toString());
        bookStatus.setText(book.getStatus());
        bookImage.setImageResource(R.drawable.default_book_image);

        // Get Borrower ID and find in database name?
        bookBorrower.setText(book.getBorrowerId());

        return view;
    }
}
