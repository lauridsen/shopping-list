package org.projects.shoppinglist;

/**
 * Created by Michael on 27-05-2018.
 */

public interface LoginActivityView {
    void changeEmailText(String email);
    void changePasswordText(String password);
    void LaunchOtherActivity(Class activity);
}
