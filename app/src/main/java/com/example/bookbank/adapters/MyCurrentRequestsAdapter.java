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
        final Button viewMap = view.findViewById(R.id.view_map);
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
                    Boolean scannedOver = documentSnapshot.getBoolean("ownerScanHandOver");
                    if(status.equals("Accepted")) {
                        viewMap.setVisibility(view.VISIBLE);
                    }
                    if(status.equals("Accepted") && scannedOver) {
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
                    Double latitude = request.getLatitude();
                    Double longitude = request.getLongitude();

                    intent.putExtra("LATITUDE", latitude);
                    intent.putExtra("LONGITUDE", longitude);

                    context.startActivity(intent);
                }
            }
        });

        scanBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("Book").document(request.getBookId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String bookStatus =  documentSnapshot.getString("status");
                        Boolean ownerScanned = documentSnapshot.getBoolean("ownerScanHandOver");
                        System.out.println(bookStatus + ownerScanned);
                        if (bookStatus.equals("Accepted") && ownerScanned) {
                            // wip scan barcode and if good update -->
                            Intent intent = new Intent(context, ScanBarcodeActivity.class);
                            String bookID = request.getBookId(); // Get the book id
                            intent.putExtra("BOOK_ID", bookID); // Pass along book id
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
