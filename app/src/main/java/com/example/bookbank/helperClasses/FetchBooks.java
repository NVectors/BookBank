package com.example.bookbank.helperClasses;

import android.os.AsyncTask;
import android.widget.EditText;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FetchBooks extends AsyncTask<String,Void,String> {

    private  EditText title;
    private EditText author;
    private EditText description;


    public FetchBooks(EditText title, EditText author, EditText description) {
        this.title = title;
        this.author = author;
        this.description = description;

    }

    @Override
    protected String doInBackground(String... strings) {
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

            int i = 0;
            String bookTitle= null;
            String bookAuthors = null;
            String bookDescription = null;

            /** loop through the items array find a book result with all 3 details */
            while (i < jsonArray.length() &&
                    (bookTitle == null && bookAuthors == null && bookDescription == null)){

                /** get the current book details */
                JSONObject book = jsonArray.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                /** try to get all 3 fields from this book */
                try{
                    bookTitle = volumeInfo.getString("title");
                    bookDescription = volumeInfo.getString("description");
                    bookAuthors = volumeInfo.getString("authors");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            /** next book */
            i++;

            /** Set Edit text fields */
            title.setText(bookTitle);
            description.setText(bookDescription);
            author.setText(bookAuthors);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
