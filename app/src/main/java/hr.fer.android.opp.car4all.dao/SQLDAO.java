package hr.fer.android.opp.car4all.dao;

import android.util.Base64;
import android.util.Log;
import android.widget.ListView;

import net.sourceforge.jtds.jdbc.DateTime;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import hr.fer.android.opp.car4all.models.Administrator;
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


public class SQLDAO implements DAO {

    @Override
    public void addPerson(Person user) throws DAOException {
        final Person finalUser = user;

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("insert into Users values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                    statement.setString(1, finalUser.getUsername());
                    statement.setString(2, finalUser.getPassword());
                    statement.setString(3, finalUser.getName());
                    statement.setString(4, finalUser.getSurname());
                    statement.setString(5, finalUser.getAddress());
                    statement.setString(6, finalUser.getOIB());
                    statement.setString(7, finalUser.getEmail());

                    statement.setString(8, finalUser.getWebpage());
                    statement.setString(9, finalUser.getFacebook());
                    statement.setString(10, finalUser.getLinkedIn());

                    statement.setBoolean(11, finalUser.getType() != PersonType.USER);

                    statement.setBoolean(12, false);
                    statement.setString(13, finalUser.getType().toString());
                    statement.setString(14, finalUser.getProfilePicture());

                    statement.executeUpdate();
                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }
    }

    @Override
    public Person getPersonForUsername(String username) throws DAOException {
        final String finalUsername = username;

        String personType = getPersonTypeForUsername(username);

        Person tmpPerson = null;
        switch (personType) {
            case "USER":
                tmpPerson = new User();
                break;
            case "MODERATOR":
                tmpPerson = new Moderator();
                break;
            case "ADMINISTRATOR":
                tmpPerson = new Administrator();
                break;
        }

        final Person person = tmpPerson;

        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("select * from Users where username=?");
                    statement.setString(1, finalUsername);

                    ResultSet rs = statement.executeQuery();

                    if (rs.next()) {
                        person.setPersonID(rs.getInt(1));
                        person.setUsername(rs.getString(2));
                        person.setPassword(rs.getString(3));
                        person.setName(rs.getString(4));
                        person.setSurname(rs.getString(5));
                        person.setAddress(rs.getString(6));
                        person.setOIB(rs.getString(7));
                        person.setEmail(rs.getString(8));

                        person.setWebpage(rs.getString(9));
                        person.setFacebook(rs.getString(10));
                        person.setLinkedIn(rs.getString(11));
                        person.setConfirmed(rs.getBoolean(12));
                        person.setDeleted(rs.getBoolean(13));
                        person.setType(PersonType.valueOf(rs.getString(14)));
                        person.setProfilePicture(rs.getString(15));
                    }

                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }
        return person;
    }

    @Override
    public Person getPersonForID(int ID) throws DAOException {
        final int finalID = ID;

        final List<Person> persons = new ArrayList<>();

        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("select username from Users where personId=?");
                    statement.setInt(1, finalID);

                    ResultSet rs = statement.executeQuery();

                    if (rs.next()) {
                        persons.add(getPersonForUsername(rs.getString(1)));
                    }

                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }

        return persons.get(0);
    }

    @Override
    public List<Person> getPersonsForType(PersonType type) throws DAOException {
        final List<Person> persons = new ArrayList<>();
        final String sType = type.toString();

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("select username from Users where personType=?");
                    statement.setString(1, sType);

                    ResultSet rs = statement.executeQuery();

                    while (rs.next()) {
                        persons.add(getPersonForUsername(rs.getString(1)));
                    }

                } catch (Exception e) {
                    Log.d("greska", e.getMessage());
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }

        return persons;
    }

    private String getPersonTypeForUsername(String username) throws DAOException {
        final String finalUsername = username;

        final StringBuilder sb = new StringBuilder();

        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("select personType from Users where username=?");
                    statement.setString(1, finalUsername);

                    ResultSet rs = statement.executeQuery();

                    if (rs.next()) {
                        sb.append(rs.getString(1));
                    }
                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }
        return sb.toString();
    }

    @Override
    public boolean isPersonFound(String username, String password) throws DAOException {
        final String finalUsername = username;
        final String finalPassword = password;

        final AtomicBoolean found = new AtomicBoolean();

        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("select * from Users where username=? and password=?");
                    statement.setString(1, finalUsername);
                    statement.setString(2, finalPassword);

                    ResultSet rs = statement.executeQuery();
                    found.set(rs.next());
                } catch (Exception e) {
                    found.set(false);
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }
        return found.get();
    }

    @Override
    public boolean usernameExists(String username) throws DAOException {
        final String finalUsername = username;

        final AtomicBoolean exists = new AtomicBoolean();

        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("select * from Users where username=?");
                    statement.setString(1, finalUsername);

                    ResultSet rs = statement.executeQuery();
                    exists.set(rs.next());
                } catch (Exception e) {
                    exists.set(false);
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }
        return exists.get();
    }

    @Override
    public boolean oibExists(String OIB) throws DAOException {
        final String finalOIB = OIB;

        final AtomicBoolean exists = new AtomicBoolean();

        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("select * from Users where username=?");
                    statement.setString(1, finalOIB);

                    ResultSet rs = statement.executeQuery();
                    exists.set(rs.next());
                } catch (Exception e) {
                    exists.set(false);
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }
        return exists.get();
    }


    @Override
    public void editPerson(Person person) throws DAOException {
        final Person finalPerson = person;

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("update Users set username=?," +
                            "password=?, address=?, email=?, webpage=?, facebook=?, linkedIn=?, profilePicture=?," +
                            " confirmed=?, deleted=? where personId=?");
                    statement.setString(1, finalPerson.getUsername());
                    statement.setString(2, finalPerson.getPassword());
                    statement.setString(3, finalPerson.getAddress());
                    statement.setString(4, finalPerson.getEmail());
                    statement.setString(5, finalPerson.getWebpage());
                    statement.setString(6, finalPerson.getFacebook());
                    statement.setString(7, finalPerson.getLinkedIn());
                    statement.setString(8, finalPerson.getProfilePicture());
                    statement.setBoolean(9, finalPerson.isConfirmed());
                    statement.setBoolean(10, finalPerson.isDeleted());
                    statement.setInt(11, finalPerson.getPersonID());

                    statement.executeUpdate();
                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }
    }

    @Override
    public int createChat() throws DAOException {
        final AtomicInteger chatID = new AtomicInteger(-1);

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("insert into Chat values (?,?)", Statement.RETURN_GENERATED_KEYS);
                    statement.setBoolean(1, true);
                    statement.setString(2, "");

                    statement.executeUpdate();

                    ResultSet rs = statement.getGeneratedKeys();
                    if (rs.next()){
                        chatID.set(rs.getInt(1));
                    }else{
                        throw new SQLException();
                    }
                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }

        return chatID.get();
    }

    public void addPassingPlace(String place, final Journey journey) throws DAOException {
        final String finalPlace = place;
        final Journey finalJourney = journey;

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("insert into PassingPlaces values (?,?)");
                    statement.setInt(1, finalJourney.getJourneyID());
                    statement.setString(2, finalPlace);

                    statement.executeUpdate();

                } catch (Exception e) {
                    Log.d("greska", e.getMessage());
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }
    }

    @Override
    public void addJourney(Journey journey, User driver) throws DAOException {
        final Journey finalJourney = journey;
        final User finalDriver = driver;

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("insert into Journeys values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
                        , Statement.RETURN_GENERATED_KEYS);
                    statement.setInt(1, finalDriver.getPersonID());
                    statement.setString(2, finalJourney.getStartingPoint().toUpperCase());
                    statement.setString(3, finalJourney.getDestination().toUpperCase());
                    statement.setDate(4, new Date(finalJourney.getStartingDate().getTime()));
                    statement.setString(5, finalJourney.getStartingTime());
                    statement.setInt(6, finalJourney.getNumberOfSeats());
                    statement.setBoolean(7, finalJourney.isByHighway());
                    statement.setInt(8, finalJourney.getPrice());
                    statement.setBoolean(9, finalJourney.isRoundTrip());

                    long time = finalJourney.getReturnDate() != null ? finalJourney.getReturnDate().getTime() : 0;
                    statement.setDate(10, new Date(time));
                    statement.setString(11, finalJourney.getReturnTime());

                    statement.setString(12, finalJourney.getComment());
                    statement.setBoolean(13, false);
                    statement.setBoolean(14, false);
                    statement.setInt(15, createChat());

                    statement.executeUpdate();

                    ResultSet rs = statement.getGeneratedKeys();
                    if (rs.next()){
                        finalJourney.setJourneyID(rs.getInt(1));
                    }else{
                        throw new SQLException();
                    }

                    for (String place : finalJourney.getPassingPlaces()){
                        addPassingPlace(place.toUpperCase(), finalJourney);
                    }
                } catch (Exception e) {
                    Log.d("greska", e.getMessage());
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }
    }

    @Override
    public void editJourney(Journey journey) throws DAOException {
        final Journey finalJourney = journey;

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("update Journeys set startingTime=?," +
                            "numberOfSeats=?, byHighway=?, price=?, roundTrip=?, returnDate=?, returnTime=?, comment=? where journeyId=?");
                    statement.setString(1, finalJourney.getStartingTime());
                    statement.setInt(2, finalJourney.getNumberOfSeats());
                    statement.setBoolean(3, finalJourney.isByHighway());
                    statement.setInt(4, finalJourney.getPrice());
                    statement.setBoolean(5, finalJourney.isRoundTrip());

                    long time = finalJourney.getReturnDate() != null ? finalJourney.getReturnDate().getTime() : 0;
                    statement.setDate(6, new Date(time));
                    statement.setString(7, finalJourney.getReturnTime());

                    statement.setString(8, finalJourney.getComment());
                    statement.setInt(9, finalJourney.getJourneyID());

                    statement.executeUpdate();

                    PreparedStatement deleteStatement = con.prepareStatement("delete from PassingPlaces where journeyId=?");
                    deleteStatement.setInt(1, finalJourney.getJourneyID());
                    deleteStatement.executeUpdate();

                    for (String place : finalJourney.getPassingPlaces()){
                        addPassingPlace(place, finalJourney);
                    }
                } catch (Exception e) {
                    Log.d("greska", e.getMessage());
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }
    }

    @Override
    public List<String> getPassingPlacesForJourney(Journey journey) throws DAOException {
        final Journey finalJourney = journey;

        final List<String> passingPlaces = new ArrayList<>();

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("select placeName from PassingPlaces where journeyId=?");
                    statement.setInt(1, finalJourney.getJourneyID());

                    ResultSet rs = statement.executeQuery();

                    while (rs.next()) {
                        passingPlaces.add(rs.getString(1));
                    }

                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }

        return passingPlaces;
    }

    @Override
    public Journey getJourneyForID(int journeyID) throws DAOException {
        final int finalID = journeyID;
        final Journey finalJourney = new Journey();

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("select * from Journeys where journeyId=?");
                    statement.setInt(1, finalID);

                    ResultSet rs = statement.executeQuery();

                    if (rs.next()) {
                        finalJourney.setJourneyID(rs.getInt(1));
                        finalJourney.getDriver();

                        finalJourney.setStartingPoint(rs.getString(3));
                        finalJourney.setDestination(rs.getString(4));
                        finalJourney.setStartingDate(new java.util.Date(rs.getDate(5).getTime()));
                        finalJourney.setStartingTime(rs.getString(6));
                        finalJourney.setNumberOfSeats(rs.getInt(7));
                        finalJourney.setByHighway(rs.getBoolean(8));
                        finalJourney.setPrice(rs.getInt(9));
                        finalJourney.setRoundTrip(rs.getBoolean(10));

                        if (finalJourney.isRoundTrip()){
                            finalJourney.setReturnDate(new java.util.Date(rs.getDate(11).getTime()));
                            finalJourney.setReturnTime(rs.getString(12));
                        }

                        finalJourney.setPassingPlaces(getPassingPlacesForJourney(finalJourney));

                        finalJourney.setComment(rs.getString(13));
                        finalJourney.setLocked(rs.getBoolean(14));
                        finalJourney.setCanceled(rs.getBoolean(15));

                        finalJourney.getChat();
                    }

                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }

        return finalJourney;
    }

    @Override
    public List<Journey> getSpecificJourneys(String startingPoint, String destination, final java.util.Date startingDate, User user) throws DAOException {
        final String finalStart = startingPoint;
        final String finalDestination = destination;
        final java.util.Date finalStartingDate = startingDate;
        final User finalUser = user;

        final List<Journey> journeys = new ArrayList<>();

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("select journeyId, startingPoint, destination, startingDate, startingTime, price" +
                            " from Journeys where startingPoint=? and destination=? and startingDate=? and driverId<>?" +
                            " and locked=? and canceled=?");
                    statement.setString(1, finalStart);
                    statement.setString(2, finalDestination);
                    statement.setDate(3, new Date(finalStartingDate.getTime()));
                    statement.setInt(4, finalUser.getPersonID());
                    statement.setBoolean(5, false);
                    statement.setBoolean(6, false);

                    ResultSet rs = statement.executeQuery();

                    while (rs.next()) {
                        Journey journey = new Journey();

                        journey.setJourneyID(rs.getInt(1));
                        journey.setStartingPoint(rs.getString(2));
                        journey.setDestination(rs.getString(3));
                        journey.setStartingDate(new java.util.Date(rs.getDate(4).getTime()));
                        journey.setStartingTime(rs.getString(5));
                        journey.setPrice(rs.getInt(6));

                        journeys.add(journey);
                    }

                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }

        return journeys;
    }

    @Override
    public Chat getChatForJourney(Journey journey) throws DAOException {
        final Journey finalJourney = journey;

        final Chat chat = new Chat();

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("select * from Chat join Journeys " +
                                    "on Chat.chatId = Journeys.chatId " +
                                    "where Journeys.journeyId=?");
                    statement.setInt(1, finalJourney.getJourneyID());

                    ResultSet rs = statement.executeQuery();

                    if (rs.next()) {
                        chat.setChatID(rs.getInt(1));
                        chat.setPublicChat(rs.getBoolean(2));

                        String lastSeenByUsers = rs.getString(3);
                        for (String id : lastSeenByUsers.split("\\$")){
                            chat.addSeenByUser(Integer.parseInt(id), false);
                        }

                    }

                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }

        return chat;
    }

    @Override
    public List<User> getPassengersForJourney(Journey journey) throws DAOException {
        final Journey finalJourney = journey;

        final List<User> passengers = new ArrayList<>();

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("select personId, profilePicture, username from JourneyRequests join Users" +
                            " on senderId = personId " +
                            " where journeyId=? and accepted=? and senderCanceled=?");
                    statement.setInt(1, finalJourney.getJourneyID());
                    statement.setBoolean(2, true);
                    statement.setBoolean(3, false);

                    ResultSet rs = statement.executeQuery();

                    while (rs.next()) {
                        User user = new User();

                        user.setPersonID(rs.getInt(1));
                        user.setProfilePicture(rs.getString(2));
                        user.setUsername(rs.getString(3));

                        passengers.add(user);
                    }

                    PreparedStatement statement2 = con.prepareStatement("select personId, profilePicture, username from Journeys join Users" +
                            " on driverId = personId " +
                            " where driverId=?");
                    statement2.setInt(1, finalJourney.getDriver().getPersonID());

                    ResultSet rs2 = statement2.executeQuery();
                    if (rs2.next()){
                        User driver = new User();

                        driver.setPersonID(rs2.getInt(1));
                        driver.setProfilePicture(rs2.getString(2));
                        driver.setUsername(rs2.getString(3));

                        passengers.add(driver);
                    }

                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }

        return passengers;
    }

    @Override
    public User getDriverForJourney(Journey journey) throws DAOException {
        final Journey finalJourney = journey;

        final List<User> users = new ArrayList<>();

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("select username from Journeys join Users" +
                            " on driverId = personId " +
                            " where journeyId=?");
                    statement.setInt(1, finalJourney.getJourneyID());

                    ResultSet rs = statement.executeQuery();

                    if (rs.next()) {
                        users.add((User) getPersonForUsername(rs.getString(1)));
                    }

                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }

        return users.get(0);
    }

    @Override
    public List<Message> getMessagesForChat(Chat chat) throws DAOException {
        final Chat finalChat = chat;

        final List<Message> messages = new ArrayList<>();

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("select senderId, username, sendingTime, content " +
                            " from Messages join Users on senderId = personId" +
                            " where chatId=? order by messageId DESC");
                    statement.setInt(1, finalChat.getChatID());

                    ResultSet rs = statement.executeQuery();

                    while (rs.next()) {
                        Message message = new Message();

                        User user = new User();
                        user.setPersonID(rs.getInt(1));
                        user.setUsername(rs.getString(2));
                        message.setSender(user);

                        message.setSendingTime(rs.getString(3));
                        message.setContent(rs.getString(4));

                        messages.add(message);
                    }

                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }

        return messages;
    }

    @Override
    public void addMessageToChat(Message message, Chat chat) throws DAOException {
        final Message finalMessage = message;
        final Chat finalChat = chat;

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("insert into Messages values (?,?,?,?)");
                    statement.setInt(1, finalMessage.getSender().getPersonID());
                    statement.setInt(2, finalChat.getChatID());
                    statement.setString(3, finalMessage.getSendingTime());
                    statement.setString(4, finalMessage.getContent());

                    statement.executeUpdate();

                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }
    }

    @Override
    public List<Review> getReviewsForUser(User user) throws DAOException {
        final User finalUser = user;

        final List<Review> reviews = new ArrayList<>();

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("select grade, comment, username, resultOfCancellation " +
                            " from Reviews join Users on reviewerId = personId " +
                            " where userId=? and deletedBy<=? order by reviewId DESC");
                    statement.setInt(1, finalUser.getPersonID());
                    statement.setInt(2, 0);

                    ResultSet rs = statement.executeQuery();

                    while (rs.next()) {
                        Review review = new Review();

                        review.setGrade(rs.getInt(1));
                        review.setComment(rs.getString(2));

                        User reviewer = new User();
                        reviewer.setUsername(rs.getString(3));
                        review.setReviewer(reviewer);
                        review.setResultOfCancellation(rs.getBoolean(4));

                        reviews.add(review);
                    }

                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }

        return reviews;
    }

    @Override
    public boolean sendJourneyRequest(User user, Journey journey) throws DAOException {
        final User finalUser = user;
        final Journey finalJourney = journey;

        final AtomicBoolean sent = new AtomicBoolean(false);

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    // check if request already exists
                    PreparedStatement checkStatement = con.prepareStatement("select * from JourneyRequests " +
                            " where journeyId=? and senderId=?");
                    checkStatement.setInt(1, finalJourney.getJourneyID());
                    checkStatement.setInt(2, finalUser.getPersonID());

                    ResultSet checkRs = checkStatement.executeQuery();
                    if (checkRs.next()){
                        return;
                    }

                    PreparedStatement statement = con.prepareStatement("insert into JourneyRequests values (?,?,?,?,?)");
                    statement.setInt(1, finalJourney.getJourneyID());
                    statement.setInt(2, finalUser.getPersonID());
                    statement.setBoolean(3, false);
                    statement.setBoolean(4, false);
                    statement.setBoolean(5, false);

                    statement.executeUpdate();

                    sent.set(true);
                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }

        return sent.get();
    }

    @Override
    public void addChatSeenByUsers(Chat chat, List<Integer> userIDs) throws DAOException {
        StringBuilder sb = new StringBuilder();
        for (int userID : userIDs){
            sb.append(String.format("%d$", userID));
        }
        final String finalLastSeenBy = sb.toString().substring(0, sb.length() - 1);

        final Chat finalChat = chat;

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("update Chat set lastSeenByUsers=? where chatId=?");
                    statement.setString(1, finalLastSeenBy);
                    statement.setInt(2, finalChat.getChatID());

                    statement.executeUpdate();
                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }
    }

    @Override
    public List<JourneyRequest> getJourneyRequestsForUser(User user) throws DAOException {
        final User finalUser = user;

        final List<JourneyRequest> requests = new ArrayList<>();

        final Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("select JourneyRequests.*" +
                            " from JourneyRequests join Journeys" +
                            " on JourneyRequests.journeyId = Journeys.journeyId and senderId=? " +
                            " where senderCanceled=? and Journeys.startingDate>=?" +
                            " order by journeyRequestId ASC");
                    statement.setInt(1, finalUser.getPersonID());
                    statement.setBoolean(2, false);

                    Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                    statement.setDate(3, new Date(cal.getTime().getTime()));

                    ResultSet rs = statement.executeQuery();

                    while (rs.next()) {
                        JourneyRequest request = new JourneyRequest();

                        request.setJourneyRequestID(rs.getInt(1));
                        request.setJourney(getJourneyForID(rs.getInt(2)));

                        PreparedStatement statement2 = con.prepareStatement("select username from Users where personId=?");
                        statement2.setInt(1, rs.getInt(3));
                        ResultSet rs2 = statement2.executeQuery();
                        if (rs2.next()){
                            request.setSender((User) getPersonForUsername(rs2.getString(1)));
                        }

                        request.setAccepted(rs.getBoolean(4));
                        request.setSenderCanceled(rs.getBoolean(5));
                        request.setChecked(rs.getBoolean(6));

                        requests.add(request);
                    }

                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }

        return requests;
    }

    @Override
    public List<JourneyRequest> getJourneyRequestsForJourney(Journey journey) throws DAOException {
        final Journey finalJourney = journey;

        final List<JourneyRequest> requests = new ArrayList<>();

        final Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("select JourneyRequests.* " +
                            " from JourneyRequests join Journeys " +
                            " on JourneyRequests.journeyId = Journeys.journeyId and JourneyRequests.journeyId=? " +
                            " where checked=? and Journeys.startingDate>=? " +
                            " order by journeyRequestId ASC ");
                    statement.setInt(1, finalJourney.getJourneyID());
                    statement.setBoolean(2, false);

                    Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                    statement.setDate(3, new Date(cal.getTime().getTime()));

                    ResultSet rs = statement.executeQuery();

                    while (rs.next()) {
                        JourneyRequest request = new JourneyRequest();

                        request.setJourneyRequestID(rs.getInt(1));
                        request.setJourney(getJourneyForID(rs.getInt(2)));

                        PreparedStatement statement2 = con.prepareStatement("select username from Users where personId=?");
                        statement2.setInt(1, rs.getInt(3));
                        ResultSet rs2 = statement2.executeQuery();
                        if (rs2.next()) {
                            request.setSender((User) getPersonForUsername(rs2.getString(1)));
                        }

                        request.setAccepted(rs.getBoolean(4));
                        request.setSenderCanceled(rs.getBoolean(5));
                        request.setChecked(rs.getBoolean(6));

                        requests.add(request);
                    }

                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }

        return requests;
    }

    @Override
    public void editJourneyRequest(JourneyRequest request) throws DAOException {
        final JourneyRequest finalRequest = request;

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("update JourneyRequests " +
                            " set accepted=?, senderCanceled=?, checked=? where journeyRequestId=?");
                    statement.setBoolean(1, finalRequest.isAccepted());
                    statement.setBoolean(2, finalRequest.isSenderCanceled());
                    statement.setBoolean(3, finalRequest.isChecked());
                    statement.setInt(4, finalRequest.getJourneyRequestID());

                    statement.executeUpdate();
                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }
    }

    @Override
    public void addReview(Review review) throws DAOException {
        final Review finalReview = review;

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("insert into Reviews values (?,?,?,?,?,?)");
                    statement.setInt(1, finalReview.getUser().getPersonID());
                    statement.setInt(2, finalReview.getReviewer().getPersonID());
                    statement.setInt(3, finalReview.getGrade());
                    statement.setString(4, finalReview.getComment());
                    statement.setInt(5, 0);
                    statement.setBoolean(6, finalReview.isResultOfCancellation());

                    statement.executeUpdate();
                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }
    }

    @Override
    public List<Journey> getOfferedJourneysForUser(User user) throws DAOException {
        final User finalUser = user;

        final List<Journey> journeys = new ArrayList<>();

        final Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("select journeyId" +
                            " from Journeys " +
                            " where canceled=? and driverId=? and startingDate>=? " +
                            " order by startingDate DESC");
                    statement.setBoolean(1, false);
                    statement.setInt(2, finalUser.getPersonID());

                    Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                    statement.setDate(3, new Date(cal.getTime().getTime()));

                    ResultSet rs = statement.executeQuery();

                    while (rs.next()) {
                        journeys.add(getJourneyForID(rs.getInt(1)));
                    }

                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }

        return journeys;
    }


    @Override
    public List<Journey> getAttendedJourneysForUser(User user) throws DAOException {
        final User finalUser = user;

        final List<Journey> journeys = new ArrayList<>();

        final Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("select Journeys.journeyId" +
                            " from JourneyRequests join Journeys" +
                            " on JourneyRequests.journeyId = Journeys.journeyId  " +
                            " where accepted=? and senderCanceled=? and canceled=? and senderId=? " +
                            " order by startingDate DESC");
                    statement.setBoolean(1, true);
                    statement.setBoolean(2, false);
                    statement.setBoolean(3, false);
                    statement.setInt(4, finalUser.getPersonID());

                    ResultSet rs = statement.executeQuery();

                    while (rs.next()) {
                        journeys.add(getJourneyForID(rs.getInt(1)));
                    }

                    PreparedStatement statement2 = con.prepareStatement("select journeyId" +
                            " from Journeys " +
                            " where canceled=? and driverId=? and startingDate<?" +
                            " order by startingDate DESC");
                    statement2.setBoolean(1, false);
                    statement2.setInt(2, finalUser.getPersonID());
                    Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                    statement2.setDate(3, new Date(cal.getTime().getTime()));

                    ResultSet rs2 = statement2.executeQuery();

                    while (rs2.next()) {
                        journeys.add(getJourneyForID(rs2.getInt(1)));
                    }

                } catch (Exception e) {
                    Log.d("greska", e.getMessage());
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }

        Collections.sort(journeys, new Comparator<Journey>() {
            @Override
            public int compare(Journey j1, Journey j2) {
                return j2.getStartingDate().compareTo(j1.getStartingDate());
            }
        });

        return journeys;
    }

    @Override
    public List<Journey> getNewMessageJourneysForUser(User user) throws DAOException {
        final User finalUser = user;

        final List<Journey> journeys = new ArrayList<>();

        final Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("select distinct Journeys.journeyId " +
                            " from Journeys join JourneyRequests" +
                            " on Journeys.journeyId = JourneyRequests.journeyId and JourneyRequests.senderId=?" +
                            " where ((accepted=? and senderCanceled=?) or locked=?) and startingDate>=? and canceled=?");
                    statement.setInt(1, finalUser.getPersonID());
                    statement.setBoolean(2, true);
                    statement.setBoolean(3, false);
                    statement.setBoolean(4, false);

                    Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                    statement.setDate(5, new Date(cal.getTime().getTime()));
                    statement.setBoolean(6, false);

                    ResultSet rs = statement.executeQuery();

                    Journey journey = new Journey();
                    while(rs.next()){
                        journey.setJourneyID(rs.getInt(1));

                        if (!journeys.contains(journey)) {
                            Chat chat = getChatForJourney(journey);
                            if (!chat.getLastSeenByUsersIDs().contains(finalUser.getPersonID())) {
                                journeys.add(getJourneyForID(rs.getInt(1)));
                            }
                        }
                    }

                    PreparedStatement statement2 = con.prepareStatement("select distinct journeyId " +
                            " from Journeys join Messages on Journeys.chatId = Messages.chatId " +
                            " where driverId=? and startingDate>=? and canceled=?");
                    statement2.setInt(1, finalUser.getPersonID());
                    statement2.setDate(2, new Date(cal.getTime().getTime()));
                    statement2.setBoolean(3, false);

                    ResultSet rs2 = statement2.executeQuery();

                    while (rs2.next()){
                        journey.setJourneyID(rs2.getInt(1));

                        if (!journeys.contains(journey)) {
                            Chat chat = getChatForJourney(journey);
                            if (!chat.getLastSeenByUsersIDs().contains(finalUser.getPersonID())) {
                                journeys.add(getJourneyForID(rs2.getInt(1)));
                            }
                        }
                    }

                    PreparedStatement statement3 = con.prepareStatement("select distinct journeyId " +
                            " from Journeys join Messages on Journeys.chatId = Messages.chatId" +
                            " where senderId=? and startingDate>=? and locked=? and canceled=?");
                    statement3.setInt(1, finalUser.getPersonID());
                    statement3.setDate(2, new Date(cal.getTime().getTime()));
                    statement3.setBoolean(3, false);
                    statement3.setBoolean(4, false);

                    ResultSet rs3 = statement3.executeQuery();

                    while (rs3.next()){
                        journey.setJourneyID(rs3.getInt(1));

                        if (!journeys.contains(journey)) {
                            Chat chat = getChatForJourney(journey);
                            if (!chat.getLastSeenByUsersIDs().contains(finalUser.getPersonID())) {
                                journeys.add(getJourneyForID(rs3.getInt(1)));
                            }
                        }
                    }

                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }

        return journeys;
    }

    @Override
    public List<User> getNewPotentialUsers() throws DAOException {
        final List<User> newUsers = new ArrayList<>();

        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("select username from Users where confirmed=? and deleted=?");
                    statement.setBoolean(1, false);
                    statement.setBoolean(2, false);

                    ResultSet rs = statement.executeQuery();

                    while (rs.next()) {
                        newUsers.add((User) getPersonForUsername(rs.getString(1)));
                    }

                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }

        return newUsers;
    }

    @Override
    public void acceptUser(User user) throws DAOException {
        final User finalUser = user;

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("update Users set confirmed=?, deleted=? " +
                            " where personId=?");
                    statement.setBoolean(1, true);
                    statement.setBoolean(2, false);
                    statement.setInt(3, finalUser.getPersonID());

                    statement.executeUpdate();
                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }
    }

    @Override
    public void declineUser(User user) throws DAOException {
        final User finalUser = user;

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("update Users set confirmed=?, deleted=?" +
                            " where personId=?");
                    statement.setBoolean(1, false);
                    statement.setBoolean(2, true);
                    statement.setInt(3, finalUser.getPersonID());

                    statement.executeUpdate();
                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }
    }

    @Override
    public void lockJourney(Journey journey) throws DAOException {
        final Journey finalJourney = journey;

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("update Journeys set locked=? " +
                            " where journeyId=?");
                    statement.setBoolean(1, true);
                    statement.setInt(2, finalJourney.getJourneyID());

                    statement.executeUpdate();
                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }
    }

    @Override
    public void switchChatToPrivate(Chat chat) throws DAOException {
        final Chat finalChat = chat;

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("update Chat set publicChat=? " +
                            " where chatId=?");
                    statement.setBoolean(1, true);
                    statement.setInt(2, finalChat.getChatID());

                    statement.executeUpdate();
                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }
    }

    @Override
    public void cancelJourney(Journey journey) throws DAOException {
        final Journey finalJourney = journey;

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("update Journeys set canceled=? " +
                            " where journeyId=?");
                    statement.setBoolean(1, true);
                    statement.setInt(2, finalJourney.getJourneyID());

                    statement.executeUpdate();
                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }
    }

    @Override
    public void cancelJourneyRequest(JourneyRequest request) throws DAOException {
        final JourneyRequest finalRequest = request;

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("update JourneyRequests " +
                            " set senderCanceled=? " +
                            " where journeyRequestId=?");
                    statement.setBoolean(1, true);
                    statement.setInt(2, finalRequest.getJourneyRequestID());

                    statement.executeUpdate();
                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }
    }

    @Override
    public JourneyRequest getJourneyRequestForId(int journeyRequestId) throws DAOException {
        final int finalJourneyRequestId = journeyRequestId;

        final JourneyRequest request = new JourneyRequest();

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("select * from JourneyRequests " +
                            " where journeyRequestId=?");
                    statement.setInt(1, finalJourneyRequestId);

                    ResultSet rs = statement.executeQuery();

                    if (rs.next()){
                        request.setJourneyRequestID(rs.getInt(1));
                        request.setJourney(getJourneyForID(rs.getInt(2)));
                        request.setSender((User) getPersonForID(rs.getInt(3)));
                        request.setAccepted(rs.getBoolean(4));
                        request.setSenderCanceled(rs.getBoolean(5));
                        request.setChecked(rs.getBoolean(6));
                    }

                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }

        return request;
    }

    @Override
    public List<Cancellation> getCancellationsForUser(User user) throws DAOException {
        final User finalUser = user;

        final List<Cancellation> cancellations = new ArrayList<>();

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("select * from Cancellations " +
                            " where checked=? and reviewerId=?");
                    statement.setBoolean(1, false);
                    statement.setInt(2, finalUser.getPersonID());

                    ResultSet rs = statement.executeQuery();

                    while (rs.next()){
                        Cancellation cancellation = new Cancellation();

                        cancellation.setCancellationId(rs.getInt(1));
                        cancellation.setJourney(getJourneyForID(rs.getInt(2)));
                        cancellation.setUser((User) getPersonForID(rs.getInt(3)));
                        cancellation.setCancelMessage(rs.getString(4));
                        cancellation.setReviewer(finalUser);

                        cancellations.add(cancellation);
                    }

                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }

        return cancellations;
    }

    @Override
    public void addCancellation(Cancellation cancellation) throws DAOException {
        final Cancellation finalCancellation = cancellation;

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("insert into Cancellations values (?,?,?,?,?)");
                    statement.setInt(1, finalCancellation.getJourney().getJourneyID());
                    statement.setInt(2, finalCancellation.getUser().getPersonID());
                    statement.setString(3, finalCancellation.getCancelMessage());
                    statement.setBoolean(4, false);
                    statement.setInt(5, finalCancellation.getReviewer().getPersonID());

                    statement.executeUpdate();
                } catch (Exception e) {
                    Log.d("greska", e.getMessage());
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }
    }

    @Override
    public void checkCancellation(final Cancellation cancellation) throws DAOException {
        final Cancellation finalCancellation = cancellation;

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("update Cancellations set checked=? " +
                            " where cancellationId=?");
                    statement.setBoolean(1, true);
                    statement.setInt(2, cancellation.getCancellationId());

                    statement.executeUpdate();
                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }
    }

    @Override
    public List<Review> getReviewsForModeratorToCheck() throws DAOException {
        final List<Review> reviews = new ArrayList<>();

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("select grade, comment, username " +
                            " from Reviews join Users on reviewerId = personId " +
                            " where resultOfCancellation=? and deletedBy=? order by reviewId DESC");
                    statement.setBoolean(1, true);
                    statement.setInt(2, 0);

                    ResultSet rs = statement.executeQuery();

                    while (rs.next()) {
                        Review review = new Review();

                        review.setGrade(rs.getInt(1));
                        review.setComment(rs.getString(2));

                        User reviewer = new User();
                        reviewer.setUsername(rs.getString(3));
                        review.setReviewer(reviewer);
                        review.setResultOfCancellation(true);

                        reviews.add(review);
                    }

                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }

        return reviews;
    }

    @Override
    public void deleteReview(Review review) throws DAOException {
        final Review finalReview = review;

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("delete from Reviews " +
                            " where reviewId=?");
                    statement.setInt(1, finalReview.getReviewID());

                    statement.executeUpdate();
                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }
    }

    @Override
    public void editReview(Review review) throws DAOException {
        final Review finalReview = review;

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("update Reviews " +
                            " set deletedBy=? where reviewId=?");
                    statement.setInt(1, finalReview.getDeletedBy());
                    statement.setInt(2, finalReview.getReviewID());

                    statement.executeUpdate();
                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }
    }

    @Override
    public List<Review> getReviewsAsAResultOfCancellation() throws DAOException {
        final List<Review> reviews = new ArrayList<>();

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("select * from Reviews" +
                            " where resultOfCancellation=? and deletedBy=?");
                    statement.setBoolean(1, true);
                    statement.setInt(2, 0);

                    ResultSet rs = statement.executeQuery();

                    while(rs.next()){
                        Review review = new Review();

                        review.setReviewID(rs.getInt(1));
                        review.setUser((User) getPersonForID(rs.getInt(2)));
                        review.setReviewer((User) getPersonForID(rs.getInt(3)));
                        review.setGrade(rs.getInt(4));
                        review.setComment(rs.getString(5));
                        review.setDeletedBy(rs.getInt(6));
                        review.setResultOfCancellation(rs.getBoolean(7));

                        reviews.add(review);
                    }

                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }

        return reviews;
    }

    @Override
    public void changeAuthority(User user, final PersonType newPersonType) throws DAOException {
        final User finalUser = user;
        final PersonType finalNewPersonType = newPersonType;

        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Connection con = SQLConnectionProvider.getConnection();

                    PreparedStatement statement = con.prepareStatement("update Users" +
                            " set personType=?" +
                            " where personId=?");
                    statement.setString(1, newPersonType.toString());
                    statement.setInt(2, finalUser.getPersonID());

                    statement.executeUpdate();

                } catch (Exception e) {
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            throw new DAOException();
        }
    }
}