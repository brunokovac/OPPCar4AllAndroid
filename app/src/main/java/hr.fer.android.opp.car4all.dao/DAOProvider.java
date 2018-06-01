package hr.fer.android.opp.car4all.dao;

public class DAOProvider {

    private static DAO dao = new SQLDAO();

    public static DAO getDao() {
        return dao;
    }

}