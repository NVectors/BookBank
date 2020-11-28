package com.example.bookbank.helperClasses;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FetchBooks extends AsyncTask<String,Void,String> {

    private  EditText title;
    private EditText author;
    private EditText description;
    private String ISBN;


    public FetchBooks(EditText title, EditText author, EditText description,String ISBN) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.ISBN = ISBN;
    }

    @Override
    protected String doInBackground(String... strings) {
        /** Calling NetworkUtils helper class's getBookInfo to get the JSON string for query */
        return NetworkUtils.getBookInfo(strings[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try{
            /** convert the response into a JSON object */
            JSONObject jsonObject = new JSONObject(s);

            /** Fetch the JSON array of book items */
            JSONArray jsonArray = jsonObject.getJSONArray("items");

            /** initializing loop variable and Strings to put fetched book data in */
            int i = 0;
            String bookTitle= null;
            String bookAuthors = null;
            String bookDescription = null;
            String bookISBN = null;

            /** bool to see if we found the book with exact isbn number */
            boolean bookFound = false;

            /** loop through the items array find a book result with all 3 details */
            while ((i < jsonArray.length()) && bookFound == false){

                /** get the current book volume details */
                JSONObject book = jsonArray.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                /** check if "industryIdentifiers" is present in this book */
                if(volumeInfo.has("industryIdentifiers")){

                    /** fetching ISBN_13 info of book */
                    JSONArray industryIdentifiers = volumeInfo.getJSONArray("industryIdentifiers");
                    JSONObject IdentifierArray_ISBN_13 = industryIdentifiers.getJSONObject(1);

                    try{
                        bookISBN = IdentifierArray_ISBN_13.getString("identifier");

                        /** check if ISBN scanned matches book ISBN */
                        if(bookISBN.equals(ISBN)){
                            /** get title,description and Authors */
                            bookTitle = volumeInfo.getString("title");
                            bookDescription = volumeInfo.getString("description");
                            bookAuthors = volumeInfo.getString("authors");

                            /** Set Edit text fields */
                            title.setText(bookTitle);
                            description.setText(bookDescription);
                            author.setText(bookAuthors);

                            /** set loop variable to true */
                            bookFound = true;

                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                /** next book */
                i++;
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
