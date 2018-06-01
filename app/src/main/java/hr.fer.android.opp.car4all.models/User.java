package hr.fer.android.opp.car4all.models;

import android.util.Log;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.OnClick;
import hr.fer.android.opp.car4all.dao.DAOException;
import hr.fer.android.opp.car4all.dao.DAOProvider;

public class User extends Person implements Serializable {

    private List<Journey> offeredJourneys;
    private List<Journey> attendedJourneys;

    private List<Journey> newMessagesJourneys;

    private List<JourneyRequest> journeyRequests;

    private List<Review> reviews;

    public User() {
    }

    public User(Person person) {
        super(person);
    }

    public void offerJourney(Journey journey) throws DAOException{
        DAOProvider.getDao().addJourney(journey, this);
    }

    public void reviewLastJourney(Review review) {
    }

    public void acceptRequest(JourneyRequest journeyRequest) {
    }

    public void confirmJourney(Journey journey) {
    }

    public void cancelJourney(Journey journey, String comment) {
    }

    public List<Journey> findJourneys(LocalDateTime startingDateAndTime, String startingPoint, String destination) {
        return null;
    }

    public void showJourneyInformation(Journey journey) {
    }

    public void showUserInformation(User user) {
    }

    public boolean sendJourneyRequest(Journey journey) {
        return DAOProvider.getDao().sendJourneyRequest(this, journey);
    }

    public void withdrawJourneyRequest(JourneyRequest request, String comment) {
    }

    public void setNewMessagesJourneys(List<Journey> newMessagesJourneys) {
        this.newMessagesJourneys = newMessagesJourneys;
    }

    public List<Journey> loadNewMessagesJourneys() throws DAOException {
        return DAOProvider.getDao().getNewMessageJourneysForUser(this);
    }

    public List<Journey> getNewMessagesJourneys() throws DAOException {
        return loadNewMessagesJourneys();
    }

    public List<Journey> loadOfferedJourneys() {
        return DAOProvider.getDao().getOfferedJourneysForUser(this);
    }

    public List<Journey> getOfferedJourneys() {
        return loadOfferedJourneys();
    }

    public void setOfferedJourneys(List<Journey> offeredJourneys) {
        this.offeredJourneys = offeredJourneys;
    }

    public List<Journey> loadAttendedJourneys() throws DAOException{
        List<Journey> journeys =  DAOProvider.getDao().getAttendedJourneysForUser(this);
        Collections.sort(journeys, new Comparator<Journey>() {
            @Override
            public int compare(Journey j1, Journey j2) {
                return j2.getStartingDate().compareTo(j1.getStartingDate());
            }
        });
        return journeys;
    }

    public List<Journey> getAttendedJourneys() {
        return loadAttendedJourneys();
    }

    public void setAttendedJourneys(List<Journey> attendedJourneys) {
        this.attendedJourneys = attendedJourneys;
    }

    public List<JourneyRequest> getJourneyRequests() throws DAOException{
        return loadJourneyRequests();
    }

    public List<JourneyRequest> loadJourneyRequests() throws DAOException{
        return DAOProvider.getDao().getJourneyRequestsForUser(this);
    }

    public void setJourneyRequests(List<JourneyRequest> journeyRequests) {
        this.journeyRequests = journeyRequests;
    }

    public List<Review> getReviews() throws DAOException{
        return loadReviews();
    }

    public List<Review> loadReviews() throws DAOException{
        return DAOProvider.getDao().getReviewsForUser(this);
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public void addReview(Review review) throws DAOException{
        DAOProvider.getDao().addReview(review);
    }

    public List<Cancellation> loadCancellations(){
        return DAOProvider.getDao().getCancellationsForUser(this);
    }

    public List<Cancellation> getCancellations() throws DAOException {
        return loadCancellations();
    }

}
