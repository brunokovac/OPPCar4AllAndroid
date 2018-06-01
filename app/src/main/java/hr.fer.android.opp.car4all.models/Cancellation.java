package hr.fer.android.opp.car4all.models;

/**
 * Created by Bruno on 10.1.2018..
 */

public class Cancellation {

    private int cancellationId;

    private Journey journey;
    private User user;
    private String cancelMessage;

    private User reviewer;

    public Cancellation(){
    }

    public Cancellation(int cancellationId, Journey journey, User user, String cancelMessage) {
        this.cancellationId = cancellationId;
        this.journey = journey;
        this.user = user;
        this.cancelMessage = cancelMessage;
    }

    public int getCancellationId() {
        return cancellationId;
    }

    public void setCancellationId(int cancellationId) {
        this.cancellationId = cancellationId;
    }

    public Journey getJourney() {
        return journey;
    }

    public void setJourney(Journey journey) {
        this.journey = journey;
    }

    public Person getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCancelMessage() {
        return cancelMessage;
    }

    public void setCancelMessage(String cancelMessage) {
        this.cancelMessage = cancelMessage;
    }

    public User getReviewer() {
        return reviewer;
    }

    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }
}
