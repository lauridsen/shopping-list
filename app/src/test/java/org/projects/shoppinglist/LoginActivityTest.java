package org.projects.shoppinglist;

import android.app.Instrumentation;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by pjuke on 27-05-2018.
 */

    // unittest@androiduser.com
    // testingpurpose
    // Test User

    // !! DOES NOT WORK !!
    // Only to show that unit testing the activity directly does not work as expected

public class LoginActivityTest {

    private LoginActivity loginActivity;
    private MainActivity mainActivity;

    @Before
    public void setUp() {
        loginActivity = new LoginActivity();
        loginActivity.onCreate(null);
    }

    @Test
    public void insertLoginSuccesfullyLogins() throws Exception {
        // Arrange
        String email = "unittest@androiduser.com";
        String password = "testingpurpose";
        loginActivity.getInputEmail().setText(email);
        loginActivity.getInputPassword().setText(password);
        // Act
        loginActivity.loginButton().performClick();
        // Assert

    }
}
