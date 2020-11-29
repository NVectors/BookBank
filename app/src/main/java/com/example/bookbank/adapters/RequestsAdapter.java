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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bookbank.R;
import com.example.bookbank.activities.EditProfileActivity;
import com.example.bookbank.activities.RequestsActivity;
import com.example.bookbank.activities.SetLocationActivity;
import com.example.bookbank.activities.ViewOwnedBooksActivity;
import com.example.bookbank.activities.ViewSearchBookDetails;
import com.example.bookbank.models.Notification;
import com.example.bookbank.models.Request;
import com.example.bookbank.models.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;

import static androidx.core.content.ContextCompat.startActivity;

public class RequestsAdapter extends ArrayAdapter {
    private ArrayList<Request> requestList;
    private Context context;
    private FirebaseFirestore firestore;
    private static final int ERROR_DIALOG_REQUEST = 9001;

    public RequestsAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Request> requestList) {
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
        View view = convertView;

        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.book_requests_item,parent,false);
        }

        firestore  = FirebaseFirestore.getInstance();

        /** Get the position of book in the ArrayList<Book> */
        final Request request = requestList.get(position);

        /** Get references to the objects in the layout */
        final TextView requesterName = view.findViewById(R.id.requester_name);
        TextView requestStatus = view.findViewById(R.id.request_status);
        Button acceptRequestButton = view.findViewById(R.id.accept_request_button);
        Button rejectRequestButton = view.findViewById(R.id.reject_request_button);

        firestore.collection("User").document(request.getRequesterId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    requesterName.setText(user.getFullname());
                }
            }
        });
        requestStatus.setText(request.getStatus());

        rejectRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // deleting the request
                firestore.runTransaction(new Transaction.Function<Void>() {
                    public Void apply(Transaction transaction) {
                        // first delete the request then send notification to requester
                        String Id;
                        Id = firestore.collection("Notification").document().getId();
                        DocumentReference requestAcceptedNotificationRef = firestore.collection("Notification").document(Id);
                        transaction.set(requestAcceptedNotificationRef, new Notification(Id, request.getRequesterId(), "Your request for " + request.getBookTitle() + " has been rejected"));
                        return null;
                    };
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference requestRejectRef = firestore.collection("Request").document(request.getId());
                        requestRejectRef.delete();
                        // if adapter list is empty change to Available
                    }
                });
            }
        });


        acceptRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // accept this request and delete all other requests for this book
                // also send notifications to both denied and accepted with people

                // update accepted request
                DocumentReference requestAcceptedRef = firestore.collection("Request").document(request.getId());
                requestAcceptedRef.update("status", "Accepted");

                // update book to be accepted
                DocumentReference bookAcceptedRef = firestore.collection("Book").document(request.getBookId());
                bookAcceptedRef.update("status", "Accepted");

                firestore.runTransaction(new Transaction.Function<Void>() {
                    @Override
                    public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                        // send Notifications to relavent people
                        //send notification that request was accepted to person borrowing
                        String Id;
                        Id = firestore.collection("Notification").document().getId();
                        DocumentReference requestAcceptedNotificationRef = firestore.collection("Notification").document(Id);
                        transaction.set(requestAcceptedNotificationRef, new Notification(Id, request.getRequesterId(), "Your request for " + request.getBookTitle() + " has been accepted"));
                        // send notification to owner that they accepted the request
                        Id = firestore.collection("Notification").document().getId();
                        DocumentReference bookAcceptedNotificationRef = firestore.collection("Notification").document(Id);
                        transaction.set(bookAcceptedNotificationRef, new Notification(Id, request.getOwnerId(), "You accepted the request for " + request.getBookTitle() + " to be borrowed"));
                        // Success
                        return null;
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // transacton was successful
                        // delete all requests that were not accepted for that book
                        firestore.collection("Request").whereEqualTo("bookId", request.getBookId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(final QuerySnapshot queryDocumentSnapshots) {
                                firestore.runTransaction(new Transaction.Function<Void>() {
                                    @Override
                                    public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                        for (DocumentSnapshot document: queryDocumentSnapshots) {
                                            final Request toDeleteRequest = document.toObject(Request.class);
                                            String docId = document.getId();
                                            toDeleteRequest.setId(docId);
                                            if (!toDeleteRequest.getStatus().equals("Accepted")) {
                                                System.out.println(toDeleteRequest.getStatus());
                                                // delete request for this book since it wasnt accepted
                                                DocumentReference deleteRequestRef = firestore.collection("Request").document(toDeleteRequest.getId());
                                                deleteRequestRef.delete();
                                                // send notification to user saying that their request was denied
                                                String Id = firestore.collection("Notification").document().getId();
                                                DocumentReference requestDenied = firestore.collection("Notification").document(Id);
                                                transaction.set(requestDenied, new Notification(Id, request.getOwnerId(), "You request for " + toDeleteRequest.getBookTitle() + " was denied"));
                                            }
                                        }
                                        // Success
                                        return null;
                                    }
                                });

                            }
                        });
                    }
                }).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {

                            if (checkServices()) {
                                Intent intent = new Intent(context, SetLocationActivity.class);
                                String docId = request.getId();
                                intent.putExtra("REQUEST_DOC", docId);
                                context.startActivity(intent);

                                //Close adapter to go back to main activity
                                ((Activity)context).finish();
                            }
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
