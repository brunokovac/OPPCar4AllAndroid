package hr.fer.android.opp.car4all.models;

import java.io.Serializable;


public class Person implements Serializable {

    private int personID;

    protected String username;
    protected String password;

    protected String name;
    protected String surname;
    private String address;
    protected String OIB;
    protected String email;
    protected String profilePicture;

    protected String webpage = "-";
    protected String facebook = "-";
    protected String LinkedIn = "-";

    private boolean confirmed;
    private boolean deleted;

    protected PersonType type;

    public Person() {
    }

    public Person(Person person) {
        this.personID = person.personID;
        this.username = person.username;
        this.name = person.name;
        this.surname = person.surname;
        this.address = person.address;
        this.OIB = person.OIB;
        this.email = person.email;
        this.profilePicture = person.profilePicture;
        this.webpage = person.webpage;
        this.facebook = person.facebook;
        this.LinkedIn = person.LinkedIn;
        this.confirmed = person.confirmed;
        this.deleted = person.deleted;
        this.type = person.type;
    }

    public Person(int personID, String username, String password, String name, String surname, String address, String OIB, String email, String webpage
    , String facebook, String linkedIn, boolean confirmed, boolean deleted, PersonType type, String profilePicture) {

        this.personID = personID;
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.OIB = OIB;
        this.email = email;

        this.webpage = webpage;
        this.facebook = facebook;
        this.LinkedIn = linkedIn;
        this.confirmed = confirmed;
        this.deleted = deleted;
        this.type = type;
        this.profilePicture = profilePicture;
    }

    public int getPersonID() {
        return personID;
    }

    public void setPersonID(int personID) {
        this.personID = personID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOIB() {
        return OIB;
    }

    public void setOIB(String OIB) {
        this.OIB = OIB;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getWebpage() {
        return webpage;
    }

    public void setWebpage(String webpage) {
        this.webpage = webpage;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getLinkedIn() {
        return LinkedIn;
    }

    public void setLinkedIn(String linkedIn) {
        LinkedIn = linkedIn;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public PersonType getType() {
        return type;
    }

    public void setType(PersonType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Person)){
            return false;
        }

        return this.username.equals(((Person) obj).username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
}
