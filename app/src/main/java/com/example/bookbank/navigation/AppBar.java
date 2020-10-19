package com.example.bookbank.navigation;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.bookbank.R;
import com.example.bookbank.activities.BorrowedBooksActivity;
import com.example.bookbank.activities.LoginActivity;
import com.example.bookbank.activities.MyCurrentRequestsActivity;
import com.example.bookbank.activities.NotificationsActivity;
import com.example.bookbank.activities.OwnerBooksActivity;
import com.example.bookbank.activities.SearchBooksActivity;
import com.example.bookbank.activities.SearchUsernameActivity;
import com.example.bookbank.enums.FirestoreCollectionName;
import com.example.bookbank.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class AppBar extends DrawerLayout {

    private DrawerLayout drawerLayout;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    public AppBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rootView = inflater.inflate(R.layout.app_bar, this, true);

        // set listener for app bar to open up the drawer widget
        drawerLayout = rootView.findViewById(R.id.drawer_layout);
        ImageView navIcon = rootView.findViewById(R.id.nav_icon);
        navIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // open drawer
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        // set authenticated username in drawer view
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        final TextView drawerUsername = rootView.findViewById(R.id.drawer_username);
        Log.d("debug", firebaseAuth.getCurrentUser().getUid());
        DocumentReference userDocRef = firestore.collection("User").document(firebaseAuth.getCurrentUser().getUid());
        userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);

                if (user != null) {
                    drawerUsername.setText(user.getFullname());
                }
            }
        });

        // add navigation for drawer elements
        addDrawerNavigationListeners(rootView);
    }

    private void addDrawerNavigationListeners(final View rootView) {
        //------------------------------------------------------------------------------------------
        // my profile drawer clicked
        TextView myProfileButton = rootView.findViewById(R.id.drawer_my_profile);
        myProfileButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // start up MyProfile Activity
                rootView.getContext().startActivity(new Intent(rootView.getContext(), LoginActivity.class));
            }
        });
        //------------------------------------------------------------------------------------------
        // owner drawer clicked
        TextView myBooksButton = rootView.findViewById(R.id.drawer_my_books);
        myBooksButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // start up MyProfile Activity
                rootView.getContext().startActivity(new Intent(rootView.getContext(), OwnerBooksActivity.class));
            }
        });
        //------------------------------------------------------------------------------------------
        // borrowed books drawer clicked
        TextView borrowedBooksButton = rootView.findViewById(R.id.drawer_borrowed_books);
        borrowedBooksButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // start up MyProfile Activity
                rootView.getContext().startActivity(new Intent(rootView.getContext(), BorrowedBooksActivity.class));
            }
        });
        //------------------------------------------------------------------------------------------
        // search books clicked
        TextView searchBooksButton = rootView.findViewById(R.id.drawer_search_books);
        searchBooksButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // start up MyProfile Activity
                rootView.getContext().startActivity(new Intent(rootView.getContext(), SearchBooksActivity.class));
            }
        });
        //------------------------------------------------------------------------------------------
        // notifications clicked
        TextView notificationsButton = rootView.findViewById(R.id.drawer_notifications);
        notificationsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // start up MyProfile Activity
                rootView.getContext().startActivity(new Intent(rootView.getContext(), NotificationsActivity.class));
            }
        });
        //------------------------------------------------------------------------------------------
        // search username drawer clicked
        TextView searchUsersButton = rootView.findViewById(R.id.drawer_search_users);
        searchUsersButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // start up MyProfile Activity
                rootView.getContext().startActivity(new Intent(rootView.getContext(), SearchUsernameActivity.class));
            }
        });
        //------------------------------------------------------------------------------------------
        // search username drawer clicked
        TextView myRequestsButton = rootView.findViewById(R.id.drawer_my_requests);
        myRequestsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // start up MyProfile Activity
                rootView.getContext().startActivity(new Intent(rootView.getContext(), MyCurrentRequestsActivity.class));
            }
        });
        //------------------------------------------------------------------------------------------
        // search username drawer clicked
        TextView signOutButton = rootView.findViewById(R.id.drawer_sign_out);
        signOutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // start up MyProfile Activity
                firebaseAuth.signOut();
                rootView.getContext().startActivity(new Intent(rootView.getContext(), LoginActivity.class));

            }
        });
    }
}
