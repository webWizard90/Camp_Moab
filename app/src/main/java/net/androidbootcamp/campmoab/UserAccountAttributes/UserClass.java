package net.androidbootcamp.campmoab.UserAccountAttributes;

import android.widget.EditText;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class UserClass {
    private String firstName;
    private String lastName;
    private String phoneNum;
    private String email;
    private String password;
    private String photoURL;

    public UserClass() {}

    public UserClass(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public UserClass(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public UserClass(String firstName, String lastName, String phoneNum, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNum = phoneNum;
        this.email = email;
    }

    public UserClass(String photoURL) {
        this.photoURL = photoURL;
    }

    public UserClass(String firstName, String lastName, String phoneNum, String email, String photoURL) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNum = phoneNum;
        this.email = email;
        this.photoURL = photoURL;
    }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getPhotoURL() { return photoURL; }

    public void setPhotoURL(String photoURL) { this.photoURL = photoURL; }

    public String concatName(String firstName, String lastName) {
        return firstName + " " + lastName;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("firstName", firstName);
        map.put("lastName", lastName);
        map.put("phoneNum", phoneNum);
        map.put("email", email);
        map.put("photoURL", photoURL);
        return map;
    }
}
