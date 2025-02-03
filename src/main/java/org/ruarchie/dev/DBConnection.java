package org.ruarchie.dev;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    static public Connection getConnection() {

        try (InputStream inputStream = new FileInputStream("target/classes/resources/connect.properties")) {

            Properties props = new Properties();
            props.load(inputStream);

            String h2Url = props.getProperty("h2Url");
            String h2UserId = props.getProperty("h2UserId");
            String h2PassCode = props.getProperty("h2PassCode");

            Connection h2conn = DriverManager.getConnection(h2Url, h2UserId, h2PassCode);

            h2conn.setAutoCommit(true);
            return h2conn;

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);

        } catch (IOException e) {
            throw new RuntimeException(e);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
