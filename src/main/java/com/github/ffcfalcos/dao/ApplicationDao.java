package com.github.ffcfalcos.dao;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.sql.Connection;
import java.sql.DriverManager;

@SuppressWarnings({"unused"})
public abstract class ApplicationDao<T> extends AbstractDao<T> {

    private Connection connection;
    private String databaseHost = loadDatabaseHost();
    private String databaseUser = loadDatabaseUser();
    private String databasePassword = loadDatabasePassword();

    public void setDatabaseHost(String databaseHost) {
        this.databaseHost = databaseHost;
    }

    public void setDatabaseUser(String databaseUser) {
        this.databaseUser = databaseUser;
    }

    public void setDatabasePassword(String databasePassword) {
        this.databasePassword = databasePassword;
    }

    @Override
    protected Connection getConnection() throws Exception {
        if(connection != null) {
            return connection;
        }
        Class.forName( "com.mysql.cj.jdbc.Driver" );
        this.connection = DriverManager.getConnection(databaseHost, databaseUser, databasePassword);
        return this.connection;
    }

    private String loadDatabaseHost() {
        try {
            Context env = (Context) new InitialContext().lookup("java:comp/env");
            return  "jdbc:mysql:" + env.lookup("db-url") + "?autoReconnect=true&useSSL=false";
        } catch (Exception e) {
            return null;
        }
    }

    private String loadDatabaseUser() {
        try {
            Context env = (Context) new InitialContext().lookup("java:comp/env");
            return (String)env.lookup("db-user");
        } catch (Exception e) {
            return null;
        }
    }

    private String loadDatabasePassword() {
        try {
            Context env = (Context) new InitialContext().lookup("java:comp/env");
            return  (String) env.lookup("db-password");
        } catch (Exception e) {
            return null;
        }
    }

}
