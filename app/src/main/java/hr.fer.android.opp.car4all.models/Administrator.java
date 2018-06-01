package hr.fer.android.opp.car4all.models;

import android.annotation.TargetApi;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import hr.fer.android.opp.car4all.dao.DAO;
import hr.fer.android.opp.car4all.dao.DAOException;
import hr.fer.android.opp.car4all.dao.DAOProvider;

public class Administrator extends Person implements Serializable {

    private List<User> activeUsers;

    private Moderator moderator;

    public Administrator() {
        moderator = new Moderator();
    }

    public Administrator(Person person) {
        super(person);
        moderator = new Moderator();
    }

    @Override
    public void setPersonID(int personID) {
        super.setPersonID(personID);
        moderator.setPersonID(getPersonID());
    }

    public void changeAuthority(User user, PersonType newPersonType) throws DAOException{
        user.type = newPersonType;
        DAOProvider.getDao().changeAuthority(user, newPersonType);
    }

    public void removeUser(User user) throws DAOException{
        user.setDeleted(true);
        DAOProvider.getDao().editPerson(user);
    }

    public List<User> loadActiveUsers() throws DAOException{
        List<User> users = new ArrayList<>();

        for (Person person : DAOProvider.getDao().getPersonsForType(PersonType.MODERATOR)){
            if (person.isConfirmed() && !person.isDeleted()) {
                users.add((Moderator) person);
            }
        }

        for (Person person : DAOProvider.getDao().getPersonsForType(PersonType.USER)){
            if (person.isConfirmed() && !person.isDeleted()) {
                users.add((User) person);
            }
        }

        return users;
    }

    public List<User> getActiveUsers() throws DAOException{
        return loadActiveUsers();
    }

    public void setActiveUsers(List<User> activeUsers) {
        this.activeUsers = activeUsers;
    }

    public Moderator getModerator() {
        return moderator;
    }
}
