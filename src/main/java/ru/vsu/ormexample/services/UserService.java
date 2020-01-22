package ru.vsu.ormexample.services;

import ru.vsu.ormexample.entities.User;
import ru.vsu.ormexample.persistance.DbMapper;
import ru.vsu.ormexample.persistance.simple.UserRepository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    private UserRepository rep;

    public UserService(){
        rep = new UserRepository();
    }

    public List<User> lazyLoad(){
        return null;
    }

    public List<User> eagerLoad(){
        return null;
    }
}
