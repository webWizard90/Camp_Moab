package net.androidbootcamp.campmoab.Classes;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReservationClass {
    String arrivalDate;
    String departureDate;
    String notes;
    String status;
    String dateBooked;
    String dateEdited;
    String editedBy;
    ArrayList<Long> groupQty;
    UserClass user;
    //String UID;
    String reservationID;

    public ReservationClass() {}

    public ReservationClass(String arrivalDate, String departureDate) {
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
    }

    public ReservationClass(String arrivalDate, String departureDate, ArrayList<Long> groupQty, String notes, String status, String dateBooked) {
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
        this.groupQty = groupQty;
        this.notes = notes;
        this.status = status;
        this.dateBooked = dateBooked;
    }

    public ReservationClass(String arrivalDate, String departureDate, ArrayList<Long> groupQty, String notes, String status, String dateBooked, String reservationID) {
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
        this.groupQty = groupQty;
        this.notes = notes;
        this.status = status;
        this.dateBooked = dateBooked;
        this.reservationID = reservationID;
    }

    public ReservationClass(String arrivalDate, String departureDate, ArrayList<Long> groupQty, String notes, String status, String dateBooked, String dateEdited, String editedBy, String reservationID) {
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
        this.groupQty = groupQty;
        this.notes = notes;
        this.status = status;
        this.dateBooked = dateBooked;
        this.dateEdited = dateEdited;
        this.editedBy = editedBy;
        this.reservationID = reservationID;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(String arrivalDate) { this.arrivalDate = arrivalDate; }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) { this.departureDate = departureDate; }

    public ArrayList<Long> getGroupQty() { return groupQty; }

    public void setGroupQty(ArrayList<Long> groupQty) { this.groupQty = groupQty; }

    public String getNotes() { return notes; }

    public void setNotes(String notes) { this.notes = notes; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getDateBooked() { return dateBooked; }

    public void setDateBooked(String dateBooked) { this.dateBooked = dateBooked; }

    //public String getUID() { return UID; }

    //public void setUID(String UID) { this.UID = UID; }

    public String getDateEdited() { return dateEdited; }

    public void setDateEdited(String dateEdited) { this.dateEdited = dateEdited; }

    public String getEditedBy() { return editedBy; }

    public void setEditedBy(String editedBy) { this.editedBy = editedBy; }

    public String getReservationID() { return reservationID; }

    public void setReservationID(String reservationID) { this.reservationID = reservationID; }

    public UserClass getUser() { return user; }

    public void setUser(UserClass user) { this.user = user; }

    @Exclude
    public Map<String, Object> toMap() {
        int count = 0;
        HashMap<String, Object> map = new HashMap<>();
        map.put("arrivalDate", arrivalDate);
        map.put("departureDate", departureDate);

        for (Long i : groupQty) {
            if (i != null) {
                map.put("groupQty", count + ": " + i);
                count++;
            }
            else {
                //map.put("GuestRangeCount", 1);
            }
        }

        map.put("notes", notes);
        //map.put("UID", UID);
        map.put("status", status);
        map.put("dateBooked", dateBooked);
        map.put("dateEdited", dateEdited);
        map.put("editedBy", editedBy);

        return map;
    }
}
