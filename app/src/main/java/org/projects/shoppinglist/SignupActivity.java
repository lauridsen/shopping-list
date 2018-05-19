package org.projects.shoppinglist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputUsername;
    private Button btnSignIn, btnSignUp;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private DatabaseReference firebaseRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        firebaseRoot = FirebaseDatabase.getInstance().getReference();

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputUsername = (EditText) findViewById(R.id.username);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String username = inputUsername.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Please enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(getApplicationContext(), "Please enter your name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password is too short, enter at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Show ProgressBar while signing up
                progressBar.setVisibility(View.VISIBLE);

                // Create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignupActivity.this, "Your account was successfully created: " + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user.
                                // If sign in succeeds, send user to MainActivity
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    String username = inputUsername.getText().toString().trim();
                                    User user = new User(username);
                                    firebaseRoot.child("users/" + auth.getUid()).setValue(user);
                                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Remove Progress Bar if app resumes
        progressBar.setVisibility(View.GONE);
    }
}