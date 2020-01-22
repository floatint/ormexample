package ru.vsu.ormexample.views;

import ru.vsu.ormexample.entities.User;
import ru.vsu.ormexample.persistance.GenericRepository;
import ru.vsu.ormexample.persistance.simple.UserRepository;

import java.util.Collection;
import java.util.List;

public class Main {
    public static void main(String[] argv){
        //User usr = new User();
        //usr.setId((long)1);
        //usr.setFirstName("Client 1");
        //usr.setLastName("las ");
        //usr.setPhone(32434);
        UserRepository rep = new UserRepository();
        //rep.delete(usr);
        //rep.create(usr);
        //Collection<User> users = rep.findAll();
        System.out.println("ssdad");
    }
}
