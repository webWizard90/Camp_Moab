package net.androidbootcamp.campmoab.Classes;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminBookingClass extends ReservationClass {
   //user the User class and Booking class to create the AdminBookingClass
    UserClass user = new UserClass();
    ReservationClass booking = new ReservationClass();
    String firstName = user.getFirstName();
    String lastName = user.getLastName();
    String phoneNum = user.getPhoneNum();
    String email = user.getEmail();

    public AdminBookingClass() {
         super(); // Call the parent class (ReservationClass) constructor
         user = new UserClass();
         this.firstName = user.getFirstName();
         this.lastName = user.getLastName();
         this.phoneNum = user.getPhoneNum();
         this.email = user.getEmail();
    }

    public AdminBookingClass(String firstName, String lastName, String phoneNum, String email,
                          String arrivalDate, String departureDate, ArrayList<Long> groupQty, String addTxt,
                          String confirmationStatus, String dateBooked, String resID) {
         // Call the parent class (ReservationClass) constructor
         super(arrivalDate, departureDate, groupQty, addTxt, confirmationStatus, dateBooked, resID);
         this.firstName = firstName;
         this.lastName = lastName;
         this.phoneNum = phoneNum;
         this.email = email;
    }

    public AdminBookingClass(String firstName, String lastName, String phoneNum, String email,
                          String arrivalDate, String departureDate, ArrayList<Long> groupQty, String addTxt,
                          String confirmationStatus, String dateBooked, String resID, String dateEdited, String editedBy, String resUID) {
        // Call the parent class (ReservationClass) constructor
         super(arrivalDate, departureDate, groupQty, addTxt, confirmationStatus, dateBooked, dateEdited, editedBy, resUID);
         this.firstName = firstName;
         this.lastName = lastName;
         this.phoneNum = phoneNum;
         this.email = email;
    }

    public AdminBookingClass(ReservationClass reservationClass, UserClass userClass) {
        super(reservationClass.getArrivalDate(), reservationClass.getDepartureDate(),
              reservationClass.getGroupQty(), reservationClass.getNotes(),
              reservationClass.getStatus(), reservationClass.getDateBooked(), reservationClass.getDateEdited());
         this.user = userClass;
         this.firstName = user.getFirstName();
         this.lastName = user.getLastName();
         this.phoneNum = user.getPhoneNum();
         this.email = user.getEmail();
    }

    public AdminBookingClass(String arrival, String departure, ArrayList<Long> groupQty, String addTxt, String status, String dateBooked, String firstName, String lastName, String email, String resUID) {
    }

    public AdminBookingClass(String arrival, String departure, ArrayList<Long> groupQty, String addTxt, String status, String dateBooked, String resID) {
    }

    public UserClass getUser() {
    return user;
    }

    public void setUser(UserClass user) {
    this.user = user;
    }

    public String getFirstName() {
    return firstName;
    }

    public void setFirstName(String firstName) {
    this.firstName = firstName;
    }

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

    public String fullName() {
    return firstName + " " + lastName;
    }

    @Exclude
    @Override
    public Map<String, Object> toMap() {
    HashMap<String, Object> map = (HashMap<String, Object>) super.toMap();
    map.put("FirstName", firstName);
    map.put("LastName", lastName);
    map.put("PhoneNumber", phoneNum);
    map.put("Email", email);
    return map;
    }
}
