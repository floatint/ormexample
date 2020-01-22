package ru.vsu.ormexample.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.vsu.ormexample.entities.annotations.*;

import java.util.ArrayList;
import java.util.Collection;

@Data
@NoArgsConstructor
@Entity
public class User {
    @PrimaryKey
    private Long id;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private Integer phone;
    @OneToMany(foreignTable = Account.class)
    Collection<Account> accounts;
}
