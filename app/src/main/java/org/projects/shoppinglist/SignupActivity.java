package org.projects.shoppinglist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail;
    private EditText  inputPassword;
    private EditText inputUsername;
    private Button btnSignIn;
    private Button btnSignUp;
    private FirebaseAuth auth;
    private DatabaseReference firebaseRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        firebaseRoot = FirebaseDatabase.getInstance().getReference();

        btnSignIn = findViewById(R.id.sign_in_button);
        btnSignUp = findViewById(R.id.sign_up_button);
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        inputUsername = findViewById(R.id.username);

        if(savedInstanceState != null) {
            inputEmail.setText(savedInstanceState.getString("savedEmail"));
            inputPassword.setText(savedInstanceState.getString("savedPassword"));
            inputUsername.setText(savedInstanceState.getString("savedUsername"));
        }

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

                if (email.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (username.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter your name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password is too short, enter at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignupActivity.this, "Sign Up Success! Welcome", Toast.LENGTH_SHORT).show();
                                // If sign in succeeds, send user to MainActivity
                                // If sign in fails, display a message to the user.
                                if (task.isSuccessful()) {
                                    String username = inputUsername.getText().toString().trim();
                                    User user = new User(username);
                                    firebaseRoot.child("users/" + auth.getUid()).setValue(user);
                                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(SignupActivity.this, getString(R.string.auth_failed) + " " + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("savedEmail", inputEmail.getText().toString());
        outState.putString("savedPassword", inputPassword.getText().toString());
        outState.putString("savedUsername", inputUsername.getText().toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}