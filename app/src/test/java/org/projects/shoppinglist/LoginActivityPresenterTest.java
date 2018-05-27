package org.projects.shoppinglist;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by pjuke on 27-05-2018.
 */
public class LoginActivityPresenterTest {
    LoginActivityPresenter presenter;
    LoginActivityView view;

    class MockedView implements LoginActivityView {
        String textViewEmail;
        String textViewPassword;

        @Override
        public void changeEmailText(String email) {
            textViewEmail = email;
        }

        @Override
        public void changePasswordText(String password) {
            textViewPassword = password;
        }

        @Override
        public void LaunchOtherActivity(Class activity) {

        }
    }

    @Before
    public void setUp() throws Exception {
        view = new MockedView();
        presenter = new LoginActivityPresenter(view);
    }

    @Test
    public void inputTextUpdated() throws Exception {
        // Arrange
        String givenEmail = "unittest@androiduser.com";
        String givenPassword = "testingpurpose";
        // Act
        presenter.inputTextUpdated(givenEmail, givenPassword);
        // Assert
        // Cast view to MockedView to let android know that we want the MockedView, and not the LoginActivityView
        // to get access to the textViewEmail and textViewPassword
        assertEquals(givenEmail, ((MockedView)view).textViewEmail);
        assertEquals(givenPassword, ((MockedView)view).textViewPassword);
    }

    @Test
    public void launchOtherActivityButtonClicked() throws Exception {
        
    }

}