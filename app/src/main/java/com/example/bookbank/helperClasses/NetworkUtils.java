package com.example.bookbank.helperClasses;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    /** setting the variables for the api request */
    private static final String BOOK_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
    private static final String QUERY_PARAM = "q";
    private static final String MAX_RESULTS = "maxResults";
    private static final String PRINT_TYPE = "printType";


    static String getBookInfo(String query){
        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;
        String bookJsonString = null;

        try{
            Uri builtURI = Uri.parse(BOOK_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM,query)
                    .appendQueryParameter(MAX_RESULTS,"10")
                    .appendQueryParameter(PRINT_TYPE,"books")
                    .build();
            URL request = new URL(builtURI.toString());

            /** opening the URL connection and making request */
            urlConnection = (HttpURLConnection) request.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            /** Getting the input stream */
            InputStream inputStream = urlConnection.getInputStream();

            /** Creating a Buffered reader from the input stream */
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            /** Creating a String Reader to hold the response */
            StringBuilder builder = new StringBuilder();

            /** Read input line by line */
            String line;
            while((line = bufferedReader.readLine()) != null){
                builder.append(line);
                builder.append("\n");
            }

            /** return if response is empty */
            if(builder.length()== 0){
                return null;
            }

            bookJsonString = builder.toString();

        } catch (IOException e){
            e.printStackTrace();
        } finally {

            /** close connection at last */
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(bufferedReader != null ){
                try {
                    bufferedReader.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }

        }

        Log.d(LOG_TAG, bookJsonString);
        return bookJsonString;
    }
}
