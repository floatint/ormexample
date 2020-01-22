package ru.vsu.ormexample.persistance.simple;

import org.junit.Before;
import org.junit.Test;
import ru.vsu.ormexample.entities.User;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class UserRepositoryTest {

    @Test
    public void lazyFindAll() {
        UserRepository rep = new UserRepository();
        Collection<User> users = rep.lazyFindAll();
        int a = 0;
    }

    @Test
    public void eagerFindAll(){
        UserRepository rep = new UserRepository();
        Collection<User> users = rep.eagerFindAll();
        int a = 0;
    }
}