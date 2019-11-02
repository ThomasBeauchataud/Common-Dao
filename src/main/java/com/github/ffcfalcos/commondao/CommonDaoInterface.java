package com.github.ffcfalcos.commondao;

import java.util.List;

/**
 * Generic DAO Interface which offer basics command handle by the CommonDao
 * Advice: Created an interface specific to dao class and extends it with this interface
 * @param <T> Object, The entity manage with the table
 */
public interface CommonDaoInterface<T> {

    /**
     * Insert a new Entity T
     * @param object T
     */
    void insert(T object);

    /**
     * Update an Entity T
     * @param object T
     */
    void update(T object);

    /**
     * Return an Entity by his Id
     * @param id int
     * @return T
     */
    T getById(int id);

    /**
     * Return all Entities T
     * @return T[]
     */
    List<T> getAll();

    /**
     * Delete and Entity T by his Id
     * @param id int
     */
    void deleteById(int id);

}