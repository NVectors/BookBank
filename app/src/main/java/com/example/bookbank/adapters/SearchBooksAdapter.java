package com.example.bookbank.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bookbank.R;
import com.example.bookbank.models.Book;
import com.example.bookbank.models.BookSearch;

import java.util.ArrayList;

public class SearchBooksAdapter extends ArrayAdapter<Book> {

    private int resource;
    private Context context;
    private ArrayList<Book> bookList;

    public SearchBooksAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Book> bookList) {
        super(context, resource, bookList);
        this.bookList = bookList;
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // checking if convertView == null
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(context).inflate(resource, parent,false);
        }

        // getting the book at the position
        Book book = bookList.get(position);

        // initializing all the text views
        TextView title = view.findViewById(R.id.title_text);
        TextView author = view.findViewById(R.id.author_text);
        TextView isbn = view.findViewById(R.id.isbn_text);
        TextView status = view.findViewById(R.id.status_text);
        TextView ownerName = view.findViewById(R.id.ownerName_text);

        // setting text in all the 5 textviews
        title.setText(book.getTitle());
        author.setText(book.getAuthor());
        isbn.setText(String.valueOf(book.getIsbn()));
        status.setText(book.getStatus());
        ownerName.setText(book.getOwnerId());


        return view;
    }
}
