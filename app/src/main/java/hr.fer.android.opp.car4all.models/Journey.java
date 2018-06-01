package hr.fer.android.opp.car4all.models;

import android.util.Log;

import net.sourceforge.jtds.jdbc.DateTime;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hr.fer.android.opp.car4all.dao.DAO;
import hr.fer.android.opp.car4all.dao.DAOException;
import hr.fer.android.opp.car4all.dao.DAOProvider;

public class Journey implements Serializable {

    private int journeyID;

    private boolean locked = false;
    private boolean canceled = false;

    private String startingPoint;
    private String destination;
    private Date startingDate;
    private String startingTime = "";
    private int numberOfSeats = 0;

    private boolean byHighway = false;
    private int price = 0;
    private boolean roundTrip = false;
    private Date returnDate;
    private String returnTime = "";

    private List<String> passingPlaces = new ArrayList<>();
    private String comment = "";

    private User driver;
    private List<User> passengers;

    private Chat chat;

    public Journey() {
    }

    public void addPassingPlace(String passingPlace) {
        passingPlaces.add(passingPlace);
    }

    public int getJourneyID() {
        return journeyID;
    }

    public void setJourneyID(int journeyID) {
        this.journeyID = journeyID;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void lock() throws DAOException{
        this.locked = true;
        DAOProvider.getDao().lockJourney(this);

        this.chat.switchToPrivate();
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void cancel(String message) throws DAOException{
        this.canceled = true;

        DAOProvider.getDao().cancelJourney(this);

        Cancellation cancellation = new Cancellation();
        cancellation.setJourney(this);
        cancellation.setUser(driver);
        cancellation.setCancelMessage(message.isEmpty() ? "-" : message);
        for (User passenger : getPassengers()) {
            if (!passenger.equals(driver)) {
                cancellation.setReviewer(passenger);
                DAOProvider.getDao().addCancellation(cancellation);
            }
        }
    }

    public String getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(String startingPoint) {
        this.startingPoint = startingPoint;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Date getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(Date startingDate) {
        this.startingDate = startingDate;
    }

    public String getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(String startingTime) {
        this.startingTime = startingTime;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public boolean isByHighway() {
        return byHighway;
    }

    public void setByHighway(boolean byHighway) {
        this.byHighway = byHighway;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isRoundTrip() {
        return roundTrip;
    }

    public void setRoundTrip(boolean roundTrip) {
        this.roundTrip = roundTrip;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public String getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(String returnTime) {
        this.returnTime = returnTime;
    }

    public List<String> getPassingPlaces() {
        return passingPlaces;
    }

    public void setPassingPlaces(List<String> passingPlaces) {
        this.passingPlaces = passingPlaces;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

    public User getDriver() throws DAOException{
        if (driver == null){
            driver = loadDriver();
        }

        return driver;
    }

    public User loadDriver() throws DAOException{
        return DAOProvider.getDao().getDriverForJourney(this);
    }

    public List<User> getPassengers() throws DAOException{
        return loadPassengers();
    }

    public List<User> loadPassengers() throws DAOException{
        return DAOProvider.getDao().getPassengersForJourney(this);
    }

    public Chat getChat() throws DAOException {
        if (chat == null){
            chat = loadChat();
        }

        return chat;
    }

    public Chat loadChat() throws DAOException {
        return DAOProvider.getDao().getChatForJourney(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Journey)){
            return false;
        }

        return this.journeyID == ((Journey) obj).journeyID;
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(journeyID).hashCode();
    }
}
