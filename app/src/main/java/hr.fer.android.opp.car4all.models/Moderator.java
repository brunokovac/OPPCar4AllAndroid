package hr.fer.android.opp.car4all.models;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import hr.fer.android.opp.car4all.dao.DAO;
import hr.fer.android.opp.car4all.dao.DAOException;
import hr.fer.android.opp.car4all.dao.DAOProvider;

public class Moderator extends User implements Serializable {

    private List<User> newPotentialUsers;

    private List<User> allNormalUsers;

    public Moderator() {
    }

    public Moderator(Person person) {
        super(person);
    }

    public List<User> loadNewPotentionalUsers() throws DAOException {
        return DAOProvider.getDao().getNewPotentialUsers();
    }

    public List<User> getNewPotentialUsers() throws DAOException {
        return loadNewPotentionalUsers();
    }

    public void setNewPotentialUsers(List<User> newPotentialUsers) {
        this.newPotentialUsers = newPotentialUsers;
    }

    public List<User> loadAllNormalUsers() throws DAOException{
        List<Person> persons = DAOProvider.getDao().getPersonsForType(PersonType.USER);
        List<User> users = new ArrayList<>();

        for(Person p : persons){
            if (!p.isConfirmed() && !p.isDeleted()){
                continue;
            }

            users.add((User) p);
        }

        return users;
    }

    public List<User> getAllNormalUsers() {
        return loadAllNormalUsers();
    }

    public void acceptUser(User user) throws DAOException {
        user.setConfirmed(true);
        user.setDeleted(false);
        DAOProvider.getDao().acceptUser(user);
    }

    public void declineUser(User user) throws DAOException {
        user.setConfirmed(false);
        user.setDeleted(true);
        DAOProvider.getDao().declineUser(user);
    }

    public void removeUser(User user) throws DAOException {
        user.setDeleted(true);
        DAOProvider.getDao().editPerson(user);
    }

    public void removeReview(Review review) throws DAOException {
        review.setDeletedBy(this.getPersonID());
        DAOProvider.getDao().editReview(review);
    }

    public void checkReview(Review review) throws DAOException {
        review.setDeletedBy(-this.getPersonID());
        DAOProvider.getDao().editReview(review);
    }

}
