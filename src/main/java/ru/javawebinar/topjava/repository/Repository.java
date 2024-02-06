package ru.javawebinar.topjava.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T> {
    List<T> findAll();

    Optional<T> findById(Integer id);

    T save(T entity);

    T update(T entity);

    void delete(Integer id);
}
