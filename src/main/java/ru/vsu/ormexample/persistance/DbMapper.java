package ru.vsu.ormexample.persistance;

public interface DbMapper<R, I> {
    R map(I data);
}
