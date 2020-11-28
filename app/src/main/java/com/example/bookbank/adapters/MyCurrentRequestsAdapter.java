package com.example.bookbank.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bookbank.R;
import com.example.bookbank.activities.MyCurrentRequestsActivity;
import com.example.bookbank.activities.RequestsActivity;
import com.example.bookbank.activities.ScanBarcodeActivity;
import com.example.bookbank.activities.ViewBookPhotoActivity;
import com.example.bookbank.activities.ViewLocationActivity;
import com.example.bookbank.models.Book;
import com.example.bookbank.models.BookPhotograph;
import com.example.bookbank.models.Request;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MyCurrentRequestsAdapter extends ArrayAdapter {
    private ArrayList<Request> requestList;
    private Context context;
    private FirebaseFirestore firestore;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static String TAG = "Scanner";
    private String bookId;

    public MyCurrentRequestsAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Request> requestList) {
        super(context, 0, requestList);
        this.requestList = requestList;
        this.context = context;


    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // custom array adapter for formatting each item in our list
        // inflate our custom layout (R.layout.gear_list_view) instead of the default view
        // LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // View view = inflater.inflate(R.layout.list_item, null);

        final View view;

        if (convertView == null){
            view = LayoutInflater.from(context).inflate(R.layout.my_current_requests_item,parent,false);
        } else {
            view = convertView;
        }

        firestore  = FirebaseFirestore.getInstance();

        /** Get the position of book in the ArrayList<Book> */
        final Request request = requestList.get(position);

        /** Create global string */
        bookId = request.getBookId();

        /** Get references to the objects in the layout */
        final TextView bookTitle = view.findViewById(R.id.book_title);
        final TextView bookAuthor = view.findViewById(R.id.book_author);
        final TextView bookISBN = view.findViewById(R.id.book_isbn);
        final TextView bookStatus = view.findViewById(R.id.book_status);
        final ImageView bookImage = view.findViewById(R.id.book_image);
        Button viewMap = view.findViewById(R.id.view_map);
        final Button scanBook = view.findViewById(R.id.scan_book);
        Log.d("debug", request.getBookId());

        firestore.collection("Book").document(request.getBookId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Book book = documentSnapshot.toObject(Book.class);
                if (book != null) {
                    /** Set references to the book object data */
                    bookTitle.setText(book.getTitle());
                    bookAuthor.setText("By " + book.getAuthor());
                    bookISBN.setText("ISBN: " + book.getIsbn().toString());
                    bookStatus.setText("Status: " + book.getStatus());

                    String status = book.getStatus();
                    if(status.equals("Accepted") && book.getOwnerScanHandOver()) {
                        scanBook.setVisibility(view.VISIBLE);
                    }
                    //set book imageto ImageView
                    ViewBookPhotoActivity.setImage(book.getId(), bookImage);
                }
            }
        });

        viewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get map coordinates from request
                if (checkServices()) {
                    Intent intent = new Intent(context, ViewLocationActivity.class);
                    String docId = request.getId();
                    intent.putExtra("REQUEST_DOC", docId);
                    context.startActivity(intent);
                }
            }
        });

        scanBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DocumentReference bookReference = firestore.collection("book").document(request.getBookId());
                bookReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot snapshot = task.getResult();
                        String bookStatus =  snapshot.getString("status");
                        Boolean ownerScanned = snapshot.getBoolean("ownerScanHandOver");
                        if (bookStatus.equals("Accepted") && ownerScanned) {
                            // wip scan barcode and if good update -->
                            Intent intent = new Intent(context, ScanBarcodeActivity.class);
                            String bookID = request.getBookId();
                            intent.putExtra("BOOK_ID", bookID);
                            /** Barcode Scanner activity will return the value of the barcode and error messages */
                            ((Activity) context).startActivityForResult(intent, 1);

                        }
                    }
                });
            }
        });

        return view;

    }

    /**
     * Handle data that is sent back by the child activity via intent
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            /** Case 0 = Barcode Scanner child activity */
            case (1) : {
                if (resultCode == Activity.RESULT_OK) {
                    /** Get string from key of resultIntent passed back from child activity */
                    String returnValue = data.getStringExtra("RESULT");

                    /** Display the string to the user */
                    Toast.makeText(context, returnValue, Toast.LENGTH_SHORT).show();

                    /** Get the value of the barcode scanned */
                    String barcodeValue = data.getStringExtra("VALUE");

                    /** Display the barcode value to the user */
                    //Toast.makeText(getApplicationContext(), "Barcode value returned: " + barcodeValue, Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"Barcode value returned: " + barcodeValue);

                    /** Handle firestore in ownerScan() */
                    if (!barcodeValue.equals("ERROR")) {
                        borrowerScan(barcodeValue);
                    }
                }
                break;
            }
        }
    }

    /**
     * Handles the cases after the barcode of the book is scanned.
     * If the ISBN of the book scanned matches the ISBN of the book in the database.
     * And only if the boolean is True then set book status to "Borrowed"
     * @param barcodeValue
     */
    private void borrowerScan(String barcodeValue) {
        Log.d(TAG, "In borrowerScan function!");

         /** Get top level reference to the book in collection  by ID */
        DocumentReference bookReference = firestore.collection("Book").document(bookId);

        bookReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        /** Get the ISBN and Status of the book in the database */
                        String bookISBN = String.valueOf(document.getData().get("isbn"));
                        String bookStatus = document.getString("status");

                        Log.d(TAG, "BOOK ISBN: " + bookISBN);
                        Log.d(TAG,"BARCODE ISBN: " + barcodeValue);
                        Log.d(TAG, "BOOK STATUS: " + bookStatus);

                        /** ISBN of the book scanned matches with the ISBN of book in database */
                        if (bookISBN.equals(barcodeValue)) {
                            Log.d(TAG, "ISBN MATCHES");
                            /** Status of the book is correct should be "Accepted"*/
                            if(bookStatus.toLowerCase().equals("accepted")) {
                                Log.d(TAG, "STATUS IS CORRECT");
                                Log.d(TAG, "BOOLEAN IS: " + document.getData().get("ownerScanHandOver"));

                                /** Check if the boolean to keep track of owner scanning first is false by default */
                                Boolean check = (Boolean) document.getData().get("ownerScanHandOver");
                                if (check == false){
                                    /** Notify user the correct steps in handing over the book */
                                    Toast.makeText(context, "Owner of book must scan book first", Toast.LENGTH_LONG).show();
                                }

                                /** Update the status of the book to "Borrowed" */
                                firestore.collection("Book").document(bookId).update("status", "Borrowed");

                                /** Update the boolean to True for the book */
                                firestore.collection("Book").document(bookId).update("ownerScanHandOver", false);

                                /** Notify user handing off the book was a success*/
                                Toast.makeText(context, "Borrower can loan the book now", Toast.LENGTH_LONG).show();
                            }
                            /** ISBN of the book scanned matches but status is not "Accepted" */
                            else if (!bookStatus.toLowerCase().equals("accepted")){
                                Log.d(TAG, "STATUS IS INCORRECT");
                                Toast.makeText(context, "Owner must accept request for the book first", Toast.LENGTH_LONG).show();
                            }
                        }
                        /** ISBN of the book scanned doesn't match the ISBN of the book in database */
                        else if (!bookISBN.equals(barcodeValue)){
                            Log.d(TAG, "ISBN DON'T MATCH");
                            Toast.makeText(context, "Book scanned doesn't match the requested book", Toast.LENGTH_LONG).show();
                        }

                    } else { /** Book not in the database */
                        Log.d(TAG, "No such document");
                        Toast.makeText(context, "Book scanned is not the requested book", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d(TAG, "Failed with ", task.getException());
                    Toast.makeText(context, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Check Google Services to make sure map requests is possible for user
     * @return
     */
    public boolean checkServices(){
        Log.d("LOCATION", "Check Google Services Version!");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);

        if (available == ConnectionResult.SUCCESS){
            Log.d("LOCATION", "Google Play Services is working!");
            return  true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d("Location", "Fixable error!");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog((Activity) context, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(context, "We can't make map requests!", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
