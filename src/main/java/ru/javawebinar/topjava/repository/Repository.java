package ru.javawebinar.topjava.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T> {
    List<T> findAll();

    Optional<T> findById(int id);

    T create(T entity);

    T update(T entity);

    void delete(int id);
}
