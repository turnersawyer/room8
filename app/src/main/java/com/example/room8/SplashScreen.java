package com.example.room8;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.room8.ui.todolist.todomvp3.data.ToDoItemRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class SplashScreen extends MainActivity {

    private FirebaseAuth mAuth;
    private TextView email;
    private TextView password;
    private static FirebaseFirestore INSTANCE;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        INSTANCE = FirebaseFirestore.getInstance();
        setContentView(R.layout.splash_screen);
        email = findViewById(R.id.emailAddress);
        password = findViewById(R.id.password);

        Button submit = findViewById(R.id.buttonSubmit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInUser(email.getText().toString(), password.getText().toString());
            }
        });

        Button newUser = findViewById(R.id.buttonNewUser);
        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newUserPopup();
            }
        });
    }

    private void signInUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("User sign in", "signInWithEmail:success");
                            setUpDB();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("User sign in", "signInWithEmail:failure", task.getException());
                            Toast.makeText(SplashScreen.this, "Incorrect Email or Password.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setUpDB() {
        String uID = mAuth.getCurrentUser().getUid();
        Log.d("SetupDB", "uID: " + uID);

        INSTANCE.collection("users").document(uID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("SetUpDB", "DocumentSnapshot data: " + document.getData());

                                String apartmentName = document.getString("apartment");
                                ToDoItemRepository.setCollectionPath(apartmentName);

                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            } else {
                                Log.d("SetUpDB", "No such document");
                            }
                        } else {
                            Log.d("SetUpDB", "get failed with ", task.getException());
                        }
                    }
                });
    }

    private void newUserPopup() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View newUserView = layoutInflater.inflate(R.layout.new_user_layout, null);

        final AlertDialog newUser = new AlertDialog.Builder(this).create();
        newUser.setTitle("New User Signup");

        final TextView newEmailTV = (TextView) newUserView.findViewById(R.id.newEmail);
        final TextView newEmailTVConfirm = (TextView) newUserView.findViewById(R.id.confirmNewEmail);
        final TextView newPasswordTV = (TextView) newUserView.findViewById(R.id.newPassword);
        final TextView newPasswordTVConfirm = (TextView) newUserView.findViewById(R.id.confirmNewPassword);
        final TextView apartmentNameTV = (TextView) newUserView.findViewById(R.id.roomName);
        final TextView errorTV = (TextView) newUserView.findViewById(R.id.errorMessage);

        Button cancel = (Button) newUserView.findViewById(R.id.newUserCancel);
        Button confirm = (Button) newUserView.findViewById(R.id.newUserConfirm);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newUser.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newEmail = newEmailTV.getText().toString();
                String newEmailConfirm = newEmailTVConfirm.getText().toString();
                String newPassword = newPasswordTV.getText().toString();
                String newPasswordConfirm = newPasswordTVConfirm.getText().toString();
                String apartmentName = apartmentNameTV.getText().toString();

                if (TextUtils.isEmpty(newEmail) || !Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                    errorTV.setText("Please enter a valid email.");
                } else if (TextUtils.isEmpty(newPassword) || newPassword.length() < 6) {
                    errorTV.setText("Password must be at least 6 characters.");
                } else if ((!newEmail.equals(newEmailConfirm)) && (!newPassword.equals(newPasswordConfirm))) {
                    errorTV.setText("Emails and passwords must match.");
                } else if (!newEmail.equals(newEmailConfirm)) {
                    errorTV.setText("Emails must match.");
                } else if (!newPassword.equals(newPasswordConfirm)) {
                    errorTV.setText("Passwords must match.");
                } else {
                    errorTV.setText("");
                    makeNewUser(newEmail, newPassword, apartmentName);
                    newUser.dismiss();
                }
                Log.d("NEW", "USER: '" + newEmail + "' '" + newEmailConfirm + "'");
                Log.d("NEW", "Pass: '" + newPassword + "' '" + newPasswordConfirm + "'");
            }
        });

        newUser.setView(newUserView);

        newUser.show();
    }

    private void makeNewUser(final String email, final String password, final String apartmentName) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("User creation", "createUserWithEmail:success");

                            String uID = mAuth.getCurrentUser().getUid();

                            Map<String, Object> userObject = new HashMap<>();
                            userObject.put("user", email);
                            userObject.put("userID", uID);
                            userObject.put("apartment", apartmentName);

                            INSTANCE.collection("users").document(uID)
                                    .set(userObject, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            Map<String, Object> apartmentObject = new HashMap<>();
                                            apartmentObject.put("apartmentName", apartmentName);
                                            INSTANCE.collection("apartments").document(apartmentName).set(apartmentObject, SetOptions.merge())
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            mAuth.signOut();
                                                            Toast.makeText(SplashScreen.this, "Account creation successful. Please log in.",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("User creation", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SplashScreen.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
