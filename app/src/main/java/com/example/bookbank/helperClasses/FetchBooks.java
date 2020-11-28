package com.example.bookbank.helperClasses;

import android.os.AsyncTask;

public class FetchBooks extends AsyncTask<String,Void,String> {


    @Override
    protected String doInBackground(String... strings) {
        return NetworkUtils.getBookInfo(strings[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
