package com.example.bookbank.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookbank.R;
import com.example.bookbank.enums.FirestoreCollectionName;
import com.example.bookbank.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfileActivity extends AppCompatActivity {
    public static String EDIT_PROFILE_TAG = "EditProfileTag";

    private TextView emailTextView;
    private TextView fullnameTextView;
    private TextView addressTextView;
    private TextView phoneNumberTextView;
    private EditText emailEditText;
    private EditText fullnameEditText;
    private EditText addressEditText;
    private EditText phoneNumberEditText;
    private Button editProfileButton;
    private Button updateProfileButton;
    private Button cancelButton;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // set firebase references
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        emailTextView = findViewById(R.id.emailTextView);
        fullnameTextView = findViewById(R.id.fullnameTextView);
        addressTextView = findViewById(R.id.addressTextView);
        phoneNumberTextView = findViewById(R.id.phoneNumberTextView);

        emailEditText = findViewById(R.id.emailEditText);
        fullnameEditText = findViewById(R.id.fullnameEditText);
        addressEditText = findViewById(R.id.addressEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);

        editProfileButton = findViewById(R.id.editProfileButton);
        updateProfileButton = findViewById(R.id.updateProfileButton);
        cancelButton = findViewById(R.id.cancelUpdateProfileButton);

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // adjust visibility of elements
                Log.d("debug", "sup dude");
                toggleEditMode();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // adjust visibility of elements
                toggleViewMode();
            }
        });

        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                    user.setEmail(emailEditText.getText().toString());
                    user.setFullname(fullnameEditText.getText().toString());
                    user.setAddress(addressEditText.getText().toString());
                    user.setPhoneNumber(phoneNumberEditText.getText().toString());
                    firestore.collection(FirestoreCollectionName.User.toString()).document(user.getId()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            toggleViewMode();
                            refreshTextViews();
                            Toast.makeText(EditProfileActivity.this, "successfully update profile", Toast.LENGTH_SHORT);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditProfileActivity.this, "update profile failed", Toast.LENGTH_SHORT);
                        }
                    });
                } else {
                    Toast.makeText(EditProfileActivity.this, "FirebaseAuth UID is null", Toast.LENGTH_SHORT).show();
                }

            }
        });

        String id = firebaseAuth.getUid();
        if (id != null) {
            firestore.collection(FirestoreCollectionName.User.toString()).document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        emailTextView.setText(user.getEmail());
                        fullnameTextView.setText(user.getFullname());
                        addressTextView.setText(user.getAddress());
                        phoneNumberTextView.setText(user.getPhoneNumber());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfileActivity.this, "failed to get user from firebase", Toast.LENGTH_SHORT);
                }
            });
        } else {
            Toast.makeText(this, "FirebaseAuth UID is null", Toast.LENGTH_SHORT).show();
        }

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
                startActivity(new Intent(EditProfileActivity.this, EditProfileActivity.class));
                break;
            case R.id.nav_my_books:
                startActivity(new Intent(EditProfileActivity.this, OwnerBooksActivity.class));
                break;
            case R.id.nav_borrowed_books:
                startActivity(new Intent(EditProfileActivity.this, BorrowedBooksActivity.class));
                break;
            case R.id.nav_search_books:
                startActivity(new Intent(EditProfileActivity.this, SearchBooksActivity.class));
                break;
            case R.id.nav_notifications:
                startActivity(new Intent(EditProfileActivity.this, NotificationsActivity.class));
                break;
            case R.id.nav_search_users:
                startActivity(new Intent(EditProfileActivity.this, SearchUsernameActivity.class));
                break;
            case R.id.nav_my_requests:
                startActivity(new Intent(EditProfileActivity.this, RequestsActivity.class));
                break;
            case R.id.nav_sign_out:
                firebaseAuth.signOut();
                Toast.makeText(EditProfileActivity.this, "succcessfully signed out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(EditProfileActivity.this, LoginActivity.class));
                break;
            default:
                break;
        }
        return true;
    }

    public void toggleViewMode() {
        editProfileButton.setVisibility(View.VISIBLE);
        updateProfileButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);
        emailTextView.setVisibility(View.VISIBLE);
        fullnameTextView.setVisibility(View.VISIBLE);
        addressTextView.setVisibility(View.VISIBLE);
        phoneNumberTextView.setVisibility(View.VISIBLE);
        emailEditText.setVisibility(View.GONE);
        fullnameEditText.setVisibility(View.GONE);
        addressEditText.setVisibility(View.GONE);
        phoneNumberEditText.setVisibility(View.GONE);
    }

    public void toggleEditMode() {
        emailEditText.setText(emailTextView.getText());
        fullnameEditText.setText(fullnameTextView.getText());
        addressEditText.setText(addressTextView.getText());
        phoneNumberEditText.setText(phoneNumberTextView.getText());

        editProfileButton.setVisibility(View.GONE);
        updateProfileButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
        emailTextView.setVisibility(View.GONE);
        fullnameTextView.setVisibility(View.GONE);
        addressTextView.setVisibility(View.GONE);
        phoneNumberTextView.setVisibility(View.GONE);
        emailEditText.setVisibility(View.VISIBLE);
        fullnameEditText.setVisibility(View.VISIBLE);
        addressEditText.setVisibility(View.VISIBLE);
        phoneNumberEditText.setVisibility(View.VISIBLE);
    }

    public void refreshTextViews() {
        emailTextView.setText(emailEditText.getText().toString());
        fullnameTextView.setText(fullnameEditText.getText().toString());
        addressTextView.setText(addressEditText.getText().toString());
        phoneNumberTextView.setText(phoneNumberEditText.getText().toString());
    }
}