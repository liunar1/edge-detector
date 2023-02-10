package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register extends Fragment {
    private View view;
    private EditText mUsername, mPassword;
    private Button registerButton;
    private Button toLogin;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference("users");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.register, container, false);

        mUsername = view.findViewById(R.id.registerUser);
        mPassword = view.findViewById(R.id.registerPassword);
        registerButton = view.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                User user = new User(username, password);
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    // if the text fields are empty
                    // then show the below message.
                    Snackbar.make(view, "Sections Cannot Be Blank", Snackbar.LENGTH_LONG).
                            setAction("Action", null).show();
                } else {

                    // else call the method to add
                    // data to our database.
                    databaseReference.child(user.getmUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Snackbar.make(view, "Username Unavailable", Snackbar.LENGTH_LONG).
                                        setAction("Action", null).show();
                            }
                            else {
                                databaseReference.child(user.getmUsername()).setValue(user);
                                Snackbar.make(view, "Registration Successful", Snackbar.LENGTH_LONG).
                                        setAction("Action", null).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            throw databaseError.toException();
                        }
                    });
                }
            }
        });

        toLogin = view.findViewById(R.id.toLogin);
        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // to login fragment
                NavHostFragment.findNavController(Register.this)
                        .navigate(R.id.action_Register_to_Login);
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}

