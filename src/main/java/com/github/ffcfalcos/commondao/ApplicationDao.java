package com.github.ffcfalcos.commondao;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @author Thomas Beauchataud
 * @since 03.11.2019
 * @version 2.0.0
 * This abstract class override the abstract method getConnection to create a SQLConnection with env-entry parameters
 *      db-url, the database url
 *      db-user, the database user
 *      db-password, the database password
 * @param <T> Object, The entity manage with the table
 */
public abstract class ApplicationDao<T> extends CommonDao<T> {

    private Connection connection;

    @Override
    protected Connection getConnection() {
        if(connection != null) {
            return connection;
        }
        try {
            Class.forName( "com.mysql.cj.jdbc.Driver" );
            Context env = (Context)new InitialContext().lookup("java:comp/env");
            String db_url = "jdbc:mysql:" + env.lookup("db-url") + "?autoReconnect=true&useSSL=false";
            String db_login = (String)env.lookup("db-user");
            String db_password = (String)env.lookup("db-password");
            this.connection = DriverManager.getConnection(db_url, db_login, db_password);
            return this.connection;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}
