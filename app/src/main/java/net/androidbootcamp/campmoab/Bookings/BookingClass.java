package net.androidbootcamp.campmoab.Bookings;

import com.google.firebase.database.Exclude;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BookingClass {
    String arrivalDate;
    String departureDate;
    String addTxt;
    String confirmationStatus;
    String dateBooked;
    ArrayList<String> guestNames;
    //String uid;
    //String email;

    public BookingClass() {}

    public BookingClass(String arrivalDate, String departureDate) {
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
    }

    public BookingClass(String arrivalDate, String departureDate, ArrayList<String> guestNames, String addTxt, String confirmationStatus) {
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
        this.guestNames = guestNames;
        this.addTxt = addTxt;
        this.confirmationStatus = confirmationStatus;
    }

    public BookingClass(String arrivalDate, String departureDate, ArrayList<String> guestNames, String addTxt, String confirmationStatus, String dateBooked) {
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
        this.guestNames = guestNames;
        this.addTxt = addTxt;
        this.confirmationStatus = confirmationStatus;
        this.dateBooked = dateBooked;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(String arrivalDate) { this.arrivalDate = arrivalDate; }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) { this.departureDate = departureDate; }

    public ArrayList<String> getGuestNames() { return guestNames; }

    public void setGuestNames(ArrayList<String> guestNames) { this.guestNames = guestNames; }

    public String getAddTxt() { return addTxt; }

    public void setAddTxt(String addTxt) { this.addTxt = addTxt; }

    //public String getUid() { return uid; }

    public String getConfirmationStatus() { return confirmationStatus; }

    public void setConfirmationStatus(String confirmationStatus) { this.confirmationStatus = confirmationStatus; }

    public String getDateBooked() {
        return dateBooked;
    }

    public void setDateBooked(String dateBooked) { this.dateBooked = dateBooked; }

    public String setDateFormat(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateBooked = sdf.format(new Date());
        return dateBooked;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
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
