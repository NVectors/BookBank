package com.example.bookbank.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookbank.R;
import com.example.bookbank.activities.EditProfileActivity;
import com.example.bookbank.enums.FirestoreCollectionName;
import com.example.bookbank.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfileFragment extends Fragment {

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        // Inflate the layout for this fragment
        // set firebase references
        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        emailTextView = root.findViewById(R.id.emailTextView);
        fullnameTextView = root.findViewById(R.id.fullnameTextView);
        addressTextView = root.findViewById(R.id.addressTextView);
        phoneNumberTextView = root.findViewById(R.id.phoneNumberTextView);

        emailEditText = root.findViewById(R.id.emailEditText);
        fullnameEditText = root.findViewById(R.id.fullnameEditText);
        addressEditText = root.findViewById(R.id.addressEditText);
        phoneNumberEditText = root.findViewById(R.id.phoneNumberEditText);

        editProfileButton = root.findViewById(R.id.editProfileButton);
        updateProfileButton = root.findViewById(R.id.updateProfileButton);
        cancelButton = root.findViewById(R.id.cancelUpdateProfileButton);

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
                            Toast.makeText(getActivity(), "successfully update profile", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "update profile failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "FirebaseAuth UID is null", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "failed to get user from firebase", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getActivity(), "FirebaseAuth UID is null", Toast.LENGTH_SHORT).show();
        }

        return root;
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