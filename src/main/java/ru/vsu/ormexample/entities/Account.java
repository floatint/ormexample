package ru.vsu.ormexample.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.vsu.ormexample.entities.annotations.*;

import java.util.Collection;

@Data
@NoArgsConstructor
@Entity
@Table
public class Account {
    @PrimaryKey
    private Long id;
    @Column
    private double balance;
    @OneToMany(foreignTable = Transaction.class)
    private Collection<Transaction> transactions;
}
