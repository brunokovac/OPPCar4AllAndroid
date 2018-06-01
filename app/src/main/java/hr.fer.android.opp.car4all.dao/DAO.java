package hr.fer.android.opp.car4all.dao;

import android.widget.ListView;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import hr.fer.android.opp.car4all.RequestForOfferedJourneyArrayAdadpter;
import hr.fer.android.opp.car4all.models.Cancellation;
import hr.fer.android.opp.car4all.models.Chat;
import hr.fer.android.opp.car4all.models.Journey;
import hr.fer.android.opp.car4all.models.JourneyRequest;
import hr.fer.android.opp.car4all.models.Message;
import hr.fer.android.opp.car4all.models.Moderator;
import hr.fer.android.opp.car4all.models.Person;
import hr.fer.android.opp.car4all.models.PersonType;
import hr.fer.android.opp.car4all.models.Review;
import hr.fer.android.opp.car4all.models.User;

public interface DAO {

    public Person getPersonForID(int ID) throws DAOException;

    Person getPersonForUsername(String username) throws DAOException;

    List<Person> getPersonsForType(PersonType type) throws DAOException;

    boolean isPersonFound(String username, String password) throws DAOException;

    void addPerson(Person user) throws DAOException;

    boolean usernameExists(String username) throws DAOException;

    boolean oibExists(String OIB) throws DAOException;

    void editPerson(Person person) throws DAOException;

    int createChat() throws DAOException;

    void addPassingPlace(String place, Journey journey) throws DAOException;

    void addJourney(Journey journey, User driver) throws DAOException;

    void editJourney(Journey journey) throws DAOException;

    List<String> getPassingPlacesForJourney(Journey journey) throws DAOException;

    Journey getJourneyForID(int journeyID) throws DAOException;

    List<Journey> getSpecificJourneys(String startingPoint, String destination, Date startingDateTime, User user) throws DAOException;

    Chat getChatForJourney(Journey journey) throws DAOException;

    List<User> getPassengersForJourney(Journey journey) throws DAOException;

    User getDriverForJourney(Journey journey) throws DAOException;

    List<Message> getMessagesForChat(Chat chat) throws DAOException;

    void addMessageToChat(Message message, Chat chat) throws DAOException;

    List<Review> getReviewsForUser(User user) throws DAOException;

    boolean sendJourneyRequest(User user, Journey journey) throws DAOException;

    void addChatSeenByUsers(Chat chat, List<Integer> userIDs) throws DAOException;

    List<JourneyRequest> getJourneyRequestsForUser(User user) throws DAOException;

    List<JourneyRequest> getJourneyRequestsForJourney(Journey journey) throws DAOException;

    void editJourneyRequest(JourneyRequest request) throws DAOException;

    void addReview(Review review) throws DAOException;

    List<Journey> getOfferedJourneysForUser(User user) throws DAOException;

    List<Journey> getAttendedJourneysForUser(User user) throws DAOException;

    List<Journey> getNewMessageJourneysForUser(User user) throws DAOException;

    List<User> getNewPotentialUsers() throws DAOException;

    void acceptUser(User user) throws DAOException;

    void declineUser(User user) throws DAOException;

    void lockJourney(Journey journey) throws DAOException;

    void switchChatToPrivate(Chat chat) throws DAOException;

    void cancelJourney(Journey journey) throws DAOException;

    void cancelJourneyRequest(JourneyRequest request) throws DAOException;

    JourneyRequest getJourneyRequestForId(int journeyRequestId) throws DAOException;

    List<Cancellation> getCancellationsForUser(User user) throws DAOException;

    void addCancellation(Cancellation cancellation) throws DAOException;

    void checkCancellation(Cancellation cancellation) throws DAOException;

    List<Review> getReviewsForModeratorToCheck() throws DAOException;

    void deleteReview(Review review) throws DAOException;

    void editReview(Review review) throws DAOException;

    List<Review> getReviewsAsAResultOfCancellation() throws DAOException;

    void changeAuthority(User user, PersonType newPersonType) throws DAOException;
}