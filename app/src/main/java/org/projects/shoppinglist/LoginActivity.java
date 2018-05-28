package org.projects.shoppinglist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements LoginActivityView {

    private EditText inputEmail;
    private EditText inputPassword;
    private FirebaseAuth auth;
    private Button btnSignup;
    private Button btnLogin;
    private Button btnReset;

    LoginActivityPresenter presenter;

    // For unit testing purposes
    public EditText getInputEmail() {
        return inputEmail;
    }
    public EditText getInputPassword() {
        return inputPassword;
    }
    public Button loginButton() {
        return btnLogin;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new LoginActivityPresenter(this);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        // set the view now
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        btnSignup =  findViewById(R.id.btn_signup);
        btnLogin =  findViewById(R.id.btn_login);
        btnReset =  findViewById(R.id.btn_reset_password);

       if(savedInstanceState != null) {
           inputEmail.setText(savedInstanceState.getString("savedEmail"));
           inputPassword.setText(savedInstanceState.getString("savedPassword"));
        }

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // !!!!!!!!!!!!!!!
                // ONLY FOR UNIT TESTING - Pre-inputs login info:
                // presenter.inputTextUpdated("unittest@androiduser.com", "testingpurpose");
                // !!!!!!!!!!!!!!!
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();

                if (email.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter your email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    inputPassword.setError(getString(R.string.password_length));
                }

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (task.isSuccessful()) {
                                    presenter.launchOtherActivityButtonClicked(MainActivity.class);
                                    finish();
                                } else {
                                    // Something went wrong
                                    Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
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
    }

    // Unit testing Interface
    @Override
    public void changeEmailText(String email) {
        inputEmail.setText(email);
    }

    @Override
    public void changePasswordText(String password) {
        inputPassword.setText(password);
    }

    @Override
    public void LaunchOtherActivity(Class activity) {
        Intent intent = new Intent(LoginActivity.this, activity);
        startActivity(intent);
    }
}