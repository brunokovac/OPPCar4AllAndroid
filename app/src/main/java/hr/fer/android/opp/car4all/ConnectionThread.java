package hr.fer.android.opp.car4all;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

import hr.fer.android.opp.car4all.dao.SQLConnectionProvider;

/**
 * Created by Bruno on 12.12.2017..
 */

public class ConnectionThread extends Thread {

    @Override
    public void run() {
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            String hostName = "car4all.database.windows.net";
            String dbName = "Car4All";
            String user = "admin4all";
            String password = "oppcar4all!";
            String url = String.format(
                    "jdbc:jtds:sqlserver://%s:1433/%s;encrypt=fasle;user=%s;password=%s;instance=SQLEXPRESS;",
                    hostName, dbName, user, password);
            Connection con = DriverManager.getConnection(url);

            SQLConnectionProvider.setConnection(con);

            Log.d("spajanje", "spojeno");
            Log.d("spajanje", SQLConnectionProvider.getConnection() == null ? "true" : "false");
        } catch (Exception e) {
        }
    }

}
