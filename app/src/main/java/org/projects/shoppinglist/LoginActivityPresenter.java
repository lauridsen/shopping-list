package org.projects.shoppinglist;

/**
 * Created by pjuke on 27-05-2018.
 */

public class LoginActivityPresenter {
    LoginActivityView view;

    public LoginActivityPresenter(LoginActivityView view) {
        this.view = view;
    }

    public void inputTextUpdated(String email, String password) {
        view.changeEmailText(email);
        view.changePasswordText(password);
    }

    public void launchOtherActivityButtonClicked (Class activity) {
        view.LaunchOtherActivity(activity);
    }

}
