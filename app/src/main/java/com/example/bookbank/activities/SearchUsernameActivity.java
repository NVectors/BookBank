package com.example.bookbank.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_username);

        final String TAG = "Sample";
        final EditText userName;
        final TextView userNameError;
        Button searchUserButton;
        FirebaseFirestore db;

        userList = findViewById(R.id.search_user_list);
        userName = findViewById(R.id.search_user_field);
        userNameError = findViewById(R.id.search_user_error);
        searchUserButton = findViewById(R.id.search_user_button);

        userDataList = new ArrayList<>();
        userAdapter = new SearchUsernameAdapter(this, userDataList);
        userList.setAdapter(userAdapter);

        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("User");

        searchUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findUser(collectionReference, userName, userNameError);
            }
        });

    }
    public boolean validate(EditText userName, TextView userNameError) {
        boolean[] inputs = {
                InputValidator.notEmpty(userName, userNameError),
                InputValidator.isEmail(userName, userNameError)
        };
        return InputValidator.validateInputs(inputs);
    }

    public void findUser(CollectionReference collectionReference, EditText userName, TextView userNameError) {
        if(validate(userName, userNameError)){
            final Query query = collectionReference
                    .whereEqualTo("email", userName.getText().toString());
            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                    userDataList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String id = (String) doc.getData().get("id");
                        String email = (String) doc.getData().get("email");
                        String password = (String) doc.getData().get("password");
                        String fullname = (String) doc.getData().get("fullname");
                        String address = (String) doc.getData().get("address");
                        String phoneNumber = (String) doc.getData().get("phoneNumber");
                        userDataList.add(new User(id, email, password, fullname, address, phoneNumber)); // Adding the cities and provinces from FireStore
                    }
                    if (userDataList.isEmpty()){
                        Toast.makeText(SearchUsernameActivity.this, "No such username exists", Toast.LENGTH_SHORT).show();
                    }
                    userAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud
                }
            });
        }
    }

}

