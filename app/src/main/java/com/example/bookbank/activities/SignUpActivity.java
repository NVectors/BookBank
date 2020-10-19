package com.example.bookbank.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookbank.R;
import com.example.bookbank.helperClasses.InputValidator;
import com.example.bookbank.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private TextView emailError;
    private TextView passwordError;
    private EditText confirmPassword;
    private TextView confirmPasswordError;
    private EditText fullname;
    private TextView fullnameError;
    private EditText phoneNumber;
    private TextView phoneNumberError;
    private EditText address;
    private TextView addressError;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);
        confirmPassword = findViewById(R.id.confirmPasswordEditText);
        fullname = findViewById(R.id.fullnameEditText);
        phoneNumber = findViewById(R.id.phoneNumberEditText);
        address = findViewById(R.id.addressEditText);
        emailError = findViewById(R.id.emailError);
        passwordError = findViewById(R.id.passwordError);
        confirmPasswordError = findViewById(R.id.confirmPasswordError);
        fullnameError = findViewById(R.id.fullnameError);
        phoneNumberError = findViewById(R.id.phoneNumberError);
        addressError = findViewById(R.id.addressError);
        final Button signUp = findViewById(R.id.signUpButton);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
        TextView loginText = findViewById(R.id.loginText);
        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(this, SearchBooksActivity.class));
        }
    }

    public boolean validate() {
        // validate and display error messages if any
        boolean[] inputs = {
                InputValidator.notEmpty(email, emailError),
                InputValidator.isEmail(email, emailError),
                InputValidator.notEmpty(password, passwordError),
                InputValidator.fieldsMatch(password, confirmPassword, confirmPasswordError),
                InputValidator.notEmpty(fullname, fullnameError),
                InputValidator.phoneNumber(phoneNumber, phoneNumberError),
        };
        return InputValidator.validateInputs(inputs);
    }

    public void signUp() {
        if (validate()) {
            firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        // Add User Object in Firestore
                        String id = user.getUid();
                        firestore.collection("User").document(id).set(
                                new User(
                                        id,
                                        email.getText().toString(),
                                        password.getText().toString(),
                                        fullname.getText().toString(),
                                        address.getText().toString(),
                                        phoneNumber.getText().toString()
                                )
                        ).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // successfully authenticted user, go to search books activity
                                    startActivity(new Intent(SignUpActivity.this, SearchBooksActivity.class));
                                } else {
                                    Toast.makeText(SignUpActivity.this, "failed to create User in Firestore", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof FirebaseAuthWeakPasswordException) {
                        // weak password
                        passwordError.setText("Password is too weak, try again");
                        Toast.makeText(SignUpActivity.this, "password is weak", Toast.LENGTH_SHORT).show();
                    } else if (e instanceof FirebaseAuthUserCollisionException) {
                        emailError.setText("email already exists for an account");
                        Toast.makeText(SignUpActivity.this, "email already exists for an account", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignUpActivity.this, "failed to Login", Toast.LENGTH_SHORT).show();
                    }


                }
            });
        }
    }
}