package ru.vsu.ormexample.persistance.simple;

import org.junit.Test;
import ru.vsu.ormexample.entities.Account;

import java.util.Collection;

import static org.junit.Assert.*;

public class AccountRepositoryTest {


    @Test
    public void lazyFindAll() {
        AccountRepository accRep = new AccountRepository();
        Collection<Account> lazyLoad = accRep.findAll();
        int a = 0;
    }
}