package com.example.bookbank.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookbank.R;
import com.example.bookbank.adapters.SearchUsernameAdapter;
import com.example.bookbank.helperClasses.InputValidator;
import com.example.bookbank.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchUsernameActivity extends AppCompatActivity {

    ListView userList;
    ArrayAdapter<User> userAdapter;
    ArrayList<User> userDataList;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_username);

        final String TAG = "Sample";
        final EditText userName;
        final TextView userNameError;
        Button searchUserButton;
        FirebaseFirestore db;
        firebaseAuth =  FirebaseAuth.getInstance();

        userList = findViewById(R.id.search_user_list);
        userName = findViewById(R.id.search_user_field);
        searchUserButton = findViewById(R.id.search_user_button);
        userList = findViewById(R.id.search_user_list);

        userDataList = new ArrayList<>();
        userAdapter = new SearchUsernameAdapter(this, userDataList);
        userList.setAdapter(userAdapter);

        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("User");

        searchUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = userName.getText().toString();
                findUser(collectionReference, key, userAdapter);
            }
        });

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String userID = userDataList.get(position).getId();
                Intent intent = new Intent(SearchUsernameActivity.this, ViewSearchUserActivity.class);
                intent.putExtra("USER_ID", userID);
                startActivity(intent);
            }
        });

        // --------------------------Required for Toolbar---------------------------------//
        // set tool bar
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);
    }

    // --------------------------Create Toolbar Menu---------------------------------//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.inflateMenu(R.menu.activity_main_drawer);
        tb.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return onOptionsItemSelected(item);
                    }
                });
        return true;
    }

    // --------------------------Create Toolbar Menu---------------------------------//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.nav_my_profile:
                startActivity(new Intent(SearchUsernameActivity.this, EditProfileActivity.class));
                break;
            case R.id.nav_my_books:
                startActivity(new Intent(SearchUsernameActivity.this, OwnerBooksActivity.class));
                break;
            case R.id.nav_borrowed_books:
                startActivity(new Intent(SearchUsernameActivity.this, BorrowedBooksActivity.class));
                break;
            case R.id.nav_search_books:
                startActivity(new Intent(SearchUsernameActivity.this, SearchBooksActivity.class));
                break;
            case R.id.nav_notifications:
                startActivity(new Intent(SearchUsernameActivity.this, NotificationsActivity.class));
                break;
            case R.id.nav_search_users:
                startActivity(new Intent(SearchUsernameActivity.this, SearchUsernameActivity.class));
                break;
            case R.id.nav_my_requests:
                startActivity(new Intent(SearchUsernameActivity.this, RequestsActivity.class));
                break;
            case R.id.nav_sign_out:
                firebaseAuth.signOut();
                Toast.makeText(SearchUsernameActivity.this, "succcessfully signed out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SearchUsernameActivity.this, LoginActivity.class));
                break;
            default:
                break;
        }
        return true;
    }

    public void findUser(CollectionReference collectionReference, final String keyWord, final ArrayAdapter<User> userAdapter) {

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                userDataList.clear();

                // iterating each document in the Book collection to get all the book's attributes
                for(QueryDocumentSnapshot doc : queryDocumentSnapshots){

                    String id = (String) doc.getData().get("id");
                    String email = (String) doc.getData().get("email");
                    String password = (String) doc.getData().get("password");
                    String fullname = (String) doc.getData().get("fullname");
                    String address = (String) doc.getData().get("address");
                    String phoneNumber = (String) doc.getData().get("phoneNumber");
                    User user = new User(id, email, password, fullname, address, phoneNumber);

                    // making author, title and the keyword lowerase for both case searching
                    String loEmail = email.toLowerCase();
                    String loFullName = fullname.toLowerCase();
                    String loPhoneNumber = phoneNumber.toLowerCase();
                    String key = keyWord.trim().toLowerCase();

                    // searching with regex
                    if(loEmail.matches(".*\\b"+key+"\\b.*") ||
                            loFullName.matches(".*\\b"+key+"\\b.*") ||
                            loPhoneNumber.matches(".*\\b"+key+"\\b.*"))
                    {
                        userDataList.add(user);
                    }

                }
                // notifying the adapter for the change
                userAdapter.notifyDataSetChanged();
            }
        });
    }

}

