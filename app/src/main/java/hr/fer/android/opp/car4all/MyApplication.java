package hr.fer.android.opp.car4all;

import android.app.Application;

import java.util.List;

import hr.fer.android.opp.car4all.models.Cancellation;
import hr.fer.android.opp.car4all.models.Journey;
import hr.fer.android.opp.car4all.models.Person;

/**
 * Created by Bruno on 20.12.2017..
 */

public class MyApplication extends Application {

    private Person person;

    private Journey journey;

    private Person personBeingChecked;

    private List<Journey> newMessagesJourneys;

    private List<Cancellation> newCancellations;

    private Cancellation cancellationBeingChecked;


    public void setPerson(Person person) {
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }

    public void setJourney(Journey journey) {
        this.journey = journey;
    }

    public Journey getJourney() {
        return journey;
    }

    public void setPersonBeingChecked(Person personBeingChecked) {
        this.personBeingChecked = personBeingChecked;
    }

    public Person getPersonBeingChecked() {
        return personBeingChecked;
    }

    public void setNewMessagesJourneys(List<Journey> newMessagesJourneys) {
        this.newMessagesJourneys = newMessagesJourneys;
    }

    public List<Journey> getNewMessagesJourneys() {
        return newMessagesJourneys;
    }

    public void setNewCancellations(List<Cancellation> newCancellations) {
        this.newCancellations = newCancellations;
    }

    public List<Cancellation> getNewCancellations() {
        return newCancellations;
    }

    public void setCancellationBeingChecked(Cancellation cancellationBeingChecked) {
        this.cancellationBeingChecked = cancellationBeingChecked;
    }

    public Cancellation getCancellationBeingChecked() {
        return cancellationBeingChecked;
    }
}
