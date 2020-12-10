package com.example.room8;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.room8.ui.expenses.data.ExpenseItemRepository;
import com.example.room8.ui.todolist.todomvp3.data.ToDoItemRepository;
import com.example.room8.ui.todolist.todomvp3.todolistactivity.ToDoListContract;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static FirebaseFirestore INSTANCE;
    private ToDoListContract.Presenter mPresenter;
    private TextView apartmentNameTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_expense)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        INSTANCE = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        ImageButton settingsBtn = findViewById(R.id.settingsButton);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Settings button", "in on click");
                runSettingsPopUp();
            }
        });

        apartmentNameTV = (TextView) findViewById(R.id.apartmentTitle);

        if (mAuth.getCurrentUser() != null) {
            getApartmentName();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * This might have disabled the back button
     */
    @Override
    public void onBackPressed() {
        if (mAuth.getCurrentUser() != null) {
            getApartmentName();
        }
    }

    private void logout() {
        // INSTANCE.terminate();
        mAuth.signOut();
        startActivity(new Intent(getApplicationContext(), SplashScreen.class));
    }


    public void getApartmentName() {
        String userEmail = mAuth.getCurrentUser().getEmail();
        final String[] apartmentName = {""};
        INSTANCE.collection("users")
                .whereEqualTo("user", userEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Getting User Apartment NAME", document.getId() + " => " + document.getString("apartment"));
                                apartmentName[0] = document.getString("apartment");
                                Log.d("SETTING TITLE", "APARTMENT: " + apartmentName[0]);
                                apartmentNameTV.setText(apartmentName[0]);

                                Log.d("GETTING TITLE", "APARTMENT: " + apartmentNameTV.getText());
                            }
                        } else {
                            Log.d("Getting User Apartment", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void runSettingsPopUp() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View settingsView = layoutInflater.inflate(R.layout.settings_layout, null);

        final AlertDialog settingsDialog = new AlertDialog.Builder(this).create();
        settingsDialog.setTitle("Settings");

        final TextView userEmail = (TextView) settingsView.findViewById(R.id.userEmailSettings);
        final TextView userApartment = (TextView) settingsView.findViewById(R.id.userApartmentSettings);

        userEmail.setText(mAuth.getCurrentUser().getEmail());

        INSTANCE.collection("users")
                .whereEqualTo("user", mAuth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Getting User Apartment", document.getId() + " => " + document.getData());
                                userApartment.setText(document.getString("apartment"));
                            }
                        } else {
                            Log.d("Getting User Apartment", "Error getting documents: ", task.getException());
                        }
                    }
                });

        Button changePasswordBtn = (Button) settingsView.findViewById(R.id.changePasswordBtn);
        Button changeApartmentBtn = (Button) settingsView.findViewById(R.id.changeApartmentBtn);
        Button settingsCloseBtn = (Button) settingsView.findViewById(R.id.closeSettingsBtn);
        Button logoutBtn = (Button) settingsView.findViewById(R.id.logoutButton);

        changeApartmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNewApartmentName();
                settingsDialog.dismiss();
            }
        });

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNewPassword();
                settingsDialog.dismiss();
            }
        });

        settingsCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsDialog.dismiss();
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsDialog.dismiss();
                logout();
            }
        });

        settingsDialog.setView(settingsView);

        settingsDialog.show();
    }

    private void getNewPassword() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View passwordView = layoutInflater.inflate(R.layout.change_password_layout, null);

        final AlertDialog passwordDialog = new AlertDialog.Builder(this).create();
        passwordDialog.setTitle("Change Password");

        final TextView newPasswordTV = (TextView) passwordView.findViewById(R.id.changePasswordTV);
        final TextView confirmNewPasswordTV = (TextView) passwordView.findViewById(R.id.confirmPasswordTV);

        Button passwordCancelButton = (Button) passwordView.findViewById(R.id.cancelPasswordBtn);
        Button passwordConfirmButton = (Button) passwordView.findViewById(R.id.confirmPasswordBtn);

        passwordCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordDialog.dismiss();
            }
        });

        passwordConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPasswordName = newPasswordTV.getText().toString();
                String confirmPasswordName = confirmNewPasswordTV.getText().toString();

                if (!newPasswordName.equals(confirmPasswordName)) {
                    Toast.makeText(MainActivity.this, "Please enter matching passwords.",
                            Toast.LENGTH_SHORT).show();
                } else if (newPasswordName.isEmpty() || confirmPasswordName.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter matching passwords.",
                            Toast.LENGTH_SHORT).show();
                } else if (newPasswordName.startsWith(" ") || confirmPasswordName.startsWith(" ")) {
                    Toast.makeText(MainActivity.this, "Please enter matching passwords.",
                            Toast.LENGTH_SHORT).show();
                } else if (newPasswordName.length() < 6) {
                    Toast.makeText(MainActivity.this, "Password must be at least 6 characters long.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    changeField("password", newPasswordName);
                    passwordDialog.dismiss();
                }
            }
        });


        passwordDialog.setView(passwordView);

        passwordDialog.show();
    }

    private void getNewApartmentName() {

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View apartmentView = layoutInflater.inflate(R.layout.change_apartment_layout, null);

        final AlertDialog apartmentDialog = new AlertDialog.Builder(this).create();
        apartmentDialog.setTitle("Change Apartment");

        final TextView newApartmentTV = (TextView) apartmentView.findViewById(R.id.changeApartmentNameTV);
        final TextView confirmNewApartmentTV = (TextView) apartmentView.findViewById(R.id.confirmApartmentNameTV);

        Button apartmentCancelButton = (Button) apartmentView.findViewById(R.id.changeApartmentBtnCancel);
        Button apartmentConfirmButton = (Button) apartmentView.findViewById(R.id.changeApartmentBtnConfirm);

        apartmentCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apartmentDialog.dismiss();
            }
        });

        apartmentConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newApartmentName = newApartmentTV.getText().toString();
                String confirmApartmentName = confirmNewApartmentTV.getText().toString();

                if (!newApartmentName.equals(confirmApartmentName)) {
                    Toast.makeText(MainActivity.this, "Please enter matching apartment names.",
                            Toast.LENGTH_SHORT).show();
                } else if (newApartmentName.isEmpty() || confirmApartmentName.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter matching apartment names.",
                            Toast.LENGTH_SHORT).show();
                } else if (newApartmentName.startsWith(" ") || newApartmentName.startsWith(" ")) {
                    Toast.makeText(MainActivity.this, "Please enter matching apartment names.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    changeField("apartment", newApartmentName);
                    ToDoItemRepository.setCollectionPath(newApartmentName);
                    ExpenseItemRepository.setCollectionPath(newApartmentName);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    apartmentDialog.dismiss();
                }
            }
        });


        apartmentDialog.setView(apartmentView);

        apartmentDialog.show();
    }

    private void changeField(String field, final String input) {
        if (field.equals("apartment")) {
            INSTANCE.collection("users").document(mAuth.getUid()).update("apartment", input)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Apartment not changed. Please try again later.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Map<String, Object> apt = new HashMap<>();
                            apt.put("apartmentName", input);
                            INSTANCE.collection("apartments").document(input).set(apt)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(MainActivity.this, "Apartment successfully changed.",
                                                Toast.LENGTH_SHORT).show();
                                        ToDoItemRepository.setCollectionPath(input);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Apartment not changed. Please try again later.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                        }
                    });
        } else {
            mAuth.getCurrentUser().updatePassword(input)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Password not changed. Please try again later.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(MainActivity.this, "Password successfully changed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

}