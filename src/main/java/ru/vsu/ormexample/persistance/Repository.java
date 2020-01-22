package ru.vsu.ormexample.persistance;

import java.util.Collection;

public interface Repository<E, K> {
    E find(K id);
    Collection<E> findAll();
    E create(E e);
    boolean update(E e);
    boolean delete(E e);
}
