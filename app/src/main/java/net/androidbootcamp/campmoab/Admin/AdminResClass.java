package net.androidbootcamp.campmoab.Admin;

import com.google.firebase.database.Exclude;

import net.androidbootcamp.campmoab.Bookings.BookingClass;
import net.androidbootcamp.campmoab.UserAccountAttributes.UserClass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AdminResClass {
   //user the User class and Booking class to create the AdminResClass
    UserClass user = new UserClass();
    BookingClass booking = new BookingClass();
    String firstName = user.getFirstName();
    String lastName = user.getLastName();
    String phoneNum = user.getPhoneNum();
    String email = user.getEmail();
    String arrivalDate = booking.getArrivalDate();
    String departureDate = booking.getDepartureDate();
    String addTxt = booking.getAddTxt();
    String confirmationStatus = booking.getConfirmationStatus();
    String dateBooked = booking.getDateBooked();
    ArrayList<String> guestNames = booking.getGuestNames();

    public AdminResClass() {}

    public AdminResClass(String firstName, String lastName, String phoneNum, String email, String arrivalDate, String departureDate, ArrayList<String> guestNames, String addTxt, String confrimationStatus, String dateBooked) {
          this.firstName = firstName;
          this.lastName = lastName;
          this.phoneNum = phoneNum;
          this.email = email;
          this.arrivalDate = arrivalDate;
          this.departureDate = departureDate;
          this.guestNames = guestNames;
          this.addTxt = addTxt;
          this.confirmationStatus = confrimationStatus;
          this.dateBooked = dateBooked;
      }

     public AdminResClass(BookingClass bookingClass, UserClass userClass) {
            this.booking = bookingClass;
            this.user = userClass;
     }

    public UserClass getUser() {
     return user;
    }

    public void setUser(UserClass user) {
     this.user = user;
    }

    public BookingClass getBooking() {
     return booking;
    }

    public void setBooking(BookingClass booking) {
     this.booking = booking;
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

    public String getArrivalDate() {
     return arrivalDate;
    }

    public void setArrivalDate(String arrivalDate) {
     this.arrivalDate = arrivalDate;
    }

    public String getDepartureDate() {
     return departureDate;
    }

    public void setDepartureDate(String departureDate) {
     this.departureDate = departureDate;
    }

    public String getAddTxt() {
     return addTxt;
    }

    public void setAddTxt(String addTxt) {
     this.addTxt = addTxt;
    }

    public String getConfirmationStatus() {
     return confirmationStatus;
    }

    public void setConfirmationStatus(String confirmationStatus) {
     this.confirmationStatus = confirmationStatus;
    }

    public String getDateBooked() {
     return dateBooked;
    }

    public void setDateBooked(String dateBooked) {
     this.dateBooked = dateBooked;
    }

    public ArrayList<String> getGuestNames() {
     return guestNames;
    }

    public void setGuestNames(ArrayList<String> guestNames) {
     this.guestNames = guestNames;
    }

    public String fullName() {
     return firstName + " " + lastName;
    }

    @Exclude
    Map<String, Object> toMap() {
     HashMap<String, Object> map = new HashMap<>();
     map.put("FirstName", firstName);
     map.put("LastName", lastName);
     map.put("PhoneNumber", phoneNum);
     map.put("Email", email);
     map.put("Arrival", arrivalDate);
     map.put("Departure", departureDate);

     for(String s : guestNames) {
      if (s != null) {
       map.put("GuestNames", s);
      }
      else {
       map.put("GuestNames", "");
      }
     }

     map.put("Notes", addTxt);
     //map.put("uid", uid);
     map.put("status", confirmationStatus);
     map.put("DateBooked", dateBooked);

     return map;
    }
}
