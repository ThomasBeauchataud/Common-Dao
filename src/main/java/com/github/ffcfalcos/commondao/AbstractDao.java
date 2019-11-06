package com.github.ffcfalcos.commondao;

import com.github.ffcfalcos.logger.LoggerInterface;
import com.github.ffcfalcos.logger.formatter.LogDaoFormatterInterface;
import org.json.simple.JSONObject;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Beauchataud
 * @since 03.11.2019
 * @version 2.1.0
 * This class offer multiple generic SQL commands to easily execute hard SQL query
 * If you want to edit the logger, you have to do it on the class extending this one
 * If you are using Beans, create a @PostConstructed method to set Parameters
 * Advice: Create an abstract class where you declare the getConnection method for all Dao of the same database
 * @param <T> The entity manage with the table
 */
@SuppressWarnings({"WeakerAccess","unused","Duplicates"})
public abstract class AbstractDao<T> {

    @Inject
    protected LoggerInterface logger;
    @Inject
    protected LogDaoFormatterInterface logDaoFormatter;

    /**
     * Execute an Insert Query with the SQL query and his parameters
     * @param query String
     * @param parameters Object[] parameters must be indexed with the same of order as the SQL Query
     */
    protected void insert(String query, Object[] parameters) {
        List<JSONObject> logContent = logDaoFormatter.init();
        try {
            PreparedStatement preparedStatement = this.generateStatement(query, parameters);
            logDaoFormatter.addRequest(logContent, preparedStatement.toString());
            preparedStatement.execute();
        } catch (Exception e) {
            logDaoFormatter.addException(logContent, e);
        }
        logger.log(logDaoFormatter.close(logContent));
    }

    /**
     * Execute an Update Query with the SQL query and his parameters
     * @param query String
     * @param parameters Object[] parameters must be indexed with the same of order as the SQL Query
     */
    protected void update(String query, Object[] parameters) {
        List<JSONObject> logContent = logDaoFormatter.init();
        try {
            PreparedStatement preparedStatement = this.generateStatement(query, parameters);
            logDaoFormatter.addRequest(logContent, preparedStatement.toString());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            logDaoFormatter.addException(logContent, e);
        }
        logger.log(logDaoFormatter.close(logContent));
    }

    /**
     * Execute a Select Query to get an Entity by his id with the SQL query and his id
     * @param query String
     * @param id int
     * @return T
     */
    protected T getById(String query, int id) {
        List<JSONObject> logContent = logDaoFormatter.init();
        try {
            PreparedStatement preparedStatement = this.getConnection().prepareStatement(query);
            preparedStatement.setInt(1, id);
            logDaoFormatter.addRequest(logContent, preparedStatement.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            T result = generateEntity(resultSet);
            logDaoFormatter.addResponse(logContent, result);
            return result;
        } catch (Exception e) {
            logDaoFormatter.addException(logContent, e);
            logger.log(logDaoFormatter.close(logContent));
            return null;
        }
    }

    /**
     * Select an object by id his
     * This method can only be used if your Dao class has the {@link DaoBindSelect} annotation
     * @param id int
     * @return T
     */
    protected T getById(int id) {
        List<JSONObject> logContent = logDaoFormatter.init();
        try {
            PreparedStatement preparedStatement = this.getConnection().prepareStatement(selectByIdQuery());
            preparedStatement.setInt(1, id);
            logDaoFormatter.addRequest(logContent, preparedStatement.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            T result = generateEntity(resultSet);
            logDaoFormatter.addResponse(logContent, result);
            return result;
        } catch (Exception e) {
            logDaoFormatter.addException(logContent, e);
            logger.log(logDaoFormatter.close(logContent));
            return null;
        }
    }

    /**
     * Execute a Select Query to get a Result with one Entity with the SQL query and his parameters
     * @param query String
     * @param parameters Object[] parameters must be indexed with the same of order as the SQL Query
     * @return T
     */
    protected T getOne(String query, Object[] parameters) {
        List<JSONObject> logContent = logDaoFormatter.init();
        try {
            PreparedStatement preparedStatement = this.generateStatement(query, parameters);
            logDaoFormatter.addRequest(logContent, preparedStatement.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            T result = generateEntity(resultSet);
            logDaoFormatter.addResponse(logContent, result);
            return result;
        } catch (Exception e) {
            logDaoFormatter.addException(logContent, e);
            logger.log(logDaoFormatter.close(logContent));
            return null;
        }
    }

    /**
     * Execute a Select Query to get a Result with multiple Entities with the SQL query and his parameters
     * @param query String
     * @param parameters Object[] parameters must be indexed with the same of order as the SQL Query
     * @return T[]
     */
    protected List<T> getMultiple(String query, Object[] parameters) {
        List<JSONObject> logContent = logDaoFormatter.init();
        List<T> list = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = this.generateStatement(query, parameters);
            logDaoFormatter.addRequest(logContent, preparedStatement.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                list.add(generateEntity(resultSet));
            }
        } catch (Exception e) {
            logDaoFormatter.addException(logContent, e);
        }
        logDaoFormatter.addResponse(logContent, list);
        return list;
    }

    /**
     * Select all objects
     * This method can only be used if your Dao class has the {@link DaoBindSelect} annotation
     * @return T[]
     */
    protected List<T> getAll() {
        List<JSONObject> logContent = logDaoFormatter.init();
        List<T> list = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = this.getConnection().prepareStatement(selectAllQuery());
            logDaoFormatter.addRequest(logContent, preparedStatement.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                list.add(generateEntity(resultSet));
            }
        } catch (Exception e) {
            logDaoFormatter.addException(logContent, e);
        }
        logDaoFormatter.addResponse(logContent, list);
        return list;
    }

    /**
     * Execute a Delete Query with the SQL query and the id
     * @param query String
     * @param id int
     */
    protected void deleteById(String query, int id) {
        List<JSONObject> logContent = logDaoFormatter.init();
        try {
            PreparedStatement preparedStatement = this.getConnection().prepareStatement(query);
            preparedStatement.setInt(1, id);
            logDaoFormatter.addRequest(logContent, preparedStatement.toString());
            preparedStatement.execute();
        } catch (Exception e) {
            logDaoFormatter.addException(logContent, e);
        }
        logger.log(logDaoFormatter.close(logContent));
    }

    /**
     * Delete an object by id his
     * This method can only be used if your Dao class has the {@link DaoBindSelect} annotation
     * @param id int
     */
    protected void deleteById(int id) {
        List<JSONObject> logContent = logDaoFormatter.init();
        try {
            PreparedStatement preparedStatement = this.getConnection().prepareStatement(deleteByIdQuery());
            preparedStatement.setInt(1, id);
            logDaoFormatter.addRequest(logContent, preparedStatement.toString());
            preparedStatement.execute();
        } catch (Exception e) {
            logDaoFormatter.addException(logContent, e);
        }
        logger.log(logDaoFormatter.close(logContent));
    }

    /**
     * Execute a Delete Query with the SQL query and his parameters
     * @param query String
     * @param parameters Object[] parameters must be indexed with the same of order as the SQL Query
     */
    protected void delete(String query, Object[] parameters) {
        insert(query, parameters);
    }

    /**
     * Create a SQL Connection which must stay open
     * Make sur that this method doesn't throw any Exception cause it may be catch and hidden
     * Dont forget to load the Driver
     * @return {@link java.sql.Connection}
     */
    protected abstract Connection getConnection();

    /**
     * Generate an Entity T with the resultSet
     * @param resultSet {@link java.sql.ResultSet}
     * @return T
     * @throws SQLException if an index of resultSet doesn't exists
     */
    protected abstract T generateEntity(ResultSet resultSet) throws SQLException;

    /**
     * Generate a Statement with an array parameters and a SQL Query
     * @param query String the SQL Query
     * @param parameters Object[]
     * @return {@link java.sql.PreparedStatement}
     * @throws SQLException if there is an error with the connexion
     */
    private PreparedStatement generateStatement(String query, Object[] parameters) throws SQLException {
        PreparedStatement preparedStatement = this.getConnection().prepareStatement(query);
        for (int i = 0; i < parameters.length; i++) {
            Object parameter = parameters[i];
            if (parameter.getClass() == Double.class) {
                preparedStatement.setDouble(i + 1, (double) parameter);
                continue;
            }
            if (parameter.getClass() == Integer.class) {
                preparedStatement.setInt(i + 1, (int) parameter);
                continue;
            }
            if (parameter.getClass() == String.class) {
                preparedStatement.setString(i + 1, (String) parameter);
                continue;
            }
            if (parameter.getClass() == Boolean.class) {
                preparedStatement.setBoolean(i + 1, (boolean) parameter);
                continue;
            }
            if (parameter.getClass() == Long.class) {
                preparedStatement.setLong(i + 1, (long) parameter);
            }
        }
        return preparedStatement;
    }

    /**
     * Create a SQL query to select an object by his id with the {@link DaoBindSelect} annotation
     * @return String the SQL query
     * @throws Exception if the class doesn't have the required annotation
     */
    private String selectByIdQuery() throws Exception {
        DaoBind daoBind = getClass().getAnnotation(DaoBind.class);
        if(daoBind == null) {
            throw new Exception("Impossible to use this method because you Dao class doesn't @DaoBind annotation");
        }
        return "SELECT * FROM " + daoBind.tableName() + " WHERE id = ?";
    }

    /**
     * Create a SQL query to select all object with the {@link DaoBindSelect} annotation
     * @return String the SQL query
     * @throws Exception if the class doesn't have the required annotation
     */
    private String selectAllQuery() throws Exception {
        DaoBind daoBind = getClass().getAnnotation(DaoBind.class);
        if(daoBind == null) {
            throw new Exception("Impossible to use this method because you Dao class doesn't @DaoBind annotation");
        }
        return "SELECT * FROM " + daoBind.tableName();
    }

    /**
     * Create a SQL query to delete an object by his id with the {@link DaoBindSelect} annotation
     * @return String the SQL query
     * @throws Exception if the class doesn't have the required annotation
     */
    private String deleteByIdQuery() throws Exception {
        DaoBind daoBind = getClass().getAnnotation(DaoBind.class);
        if(daoBind == null) {
            throw new Exception("Impossible to use this method because you Dao class doesn't @DaoBind annotation");
        }
        return "DELETE FROM " + daoBind.tableName() + " WHERE id = ?";
    }

}
