package ru.vsu.ormexample.persistance.simple;

import org.junit.Test;
import ru.vsu.ormexample.entities.Transaction;

import java.util.Collection;

import static org.junit.Assert.*;

public class TransactionRepositoryTest {

    @Test
    public void lazyFindAll() {
        TransactionRepository tranRep = new TransactionRepository();
        Collection<Transaction> lazyLoad = tranRep.findAll();
        int a = 0;
    }
}