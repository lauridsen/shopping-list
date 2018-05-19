package org.projects.shoppinglist;

/**
 * Created by pjuke on 19-05-2018.
 */

public class User {

    public String username;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username) {
        this.username = username;
    }

}
