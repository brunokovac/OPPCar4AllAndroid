package hr.fer.android.opp.car4all.models;

import java.io.Serializable;

import hr.fer.android.opp.car4all.dao.DAOException;
import hr.fer.android.opp.car4all.dao.DAOProvider;

public class JourneyRequest implements Serializable {

    private int journeyRequestID;

    private User sender;
    private Journey journey;

    private boolean accepted;
    private boolean senderCanceled;
    private boolean checked;

    public JourneyRequest() {
    }

    public void acceptRequest() throws DAOException {
        this.accepted = true;
        this.checked = true;
        DAOProvider.getDao().editJourneyRequest(this);
    }

    public void declineRequest() throws DAOException {
        this.accepted = false;
        this.checked = true;
        DAOProvider.getDao().editJourneyRequest(this);
    }

    public Journey loadJourney() {
        // TODO DAO
        return null;
    }

    public User loadSender() {
        // TODO DAO
        return null;
    }

    public int getJourneyRequestID() {
        return journeyRequestID;
    }

    public void setJourneyRequestID(int journeyRequestID) {
        this.journeyRequestID = journeyRequestID;
    }

    public User getSender() {
        if (sender == null){
            sender = loadSender();
        }

        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Journey getJourney() {
        if (journey == null){
            journey = loadJourney();
        }

        return journey;
    }

    public void setJourney(Journey journey) {
        this.journey = journey;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isSenderCanceled() {
        return senderCanceled;
    }

    public void setSenderCanceled(boolean senderCanceled) {
        this.senderCanceled = senderCanceled;
    }

    public void cancel(String message) throws DAOException {
        this.senderCanceled = true;
        DAOProvider.getDao().cancelJourneyRequest(this);

        if (journey.isLocked() && !journey.isCanceled()){
            Cancellation cancellation = new Cancellation();
            cancellation.setJourney(journey);
            cancellation.setUser(sender);
            cancellation.setCancelMessage(message.isEmpty() ? "-" : message);
            cancellation.setReviewer(journey.getDriver());
            DAOProvider.getDao().addCancellation(cancellation);
        }
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
