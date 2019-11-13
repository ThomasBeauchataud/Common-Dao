package com.github.ffcfalcos.commondao;

import java.util.List;

@SuppressWarnings("unused")
public interface AbstractDaoInterface<T> {

    void insert(T object);

    void update(T object);

    T getById(int id);

    List<T> getAll();

    void deleteById(int id);

}
