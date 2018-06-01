package hr.fer.android.opp.car4all.dao;

/**
 * Created by Bruno on 7.1.2018..
 */

public class DAOException extends RuntimeException {

    public DAOException(){
        super();
    }

    public DAOException(String message){
        super(message);
    }
}
