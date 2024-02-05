package ru.javawebinar.topjava.repository;

import java.util.Collection;
import java.util.Optional;

public interface Repository<T, I> {
    Collection<T> findAll();

    Optional<T> findById(I id);

    void save(T entity);

    void delete(I id);
}
