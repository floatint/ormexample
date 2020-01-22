package ru.vsu.ormexample.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.vsu.ormexample.entities.annotations.*;

import java.sql.Date;

@Data
@NoArgsConstructor
@Entity
@Table
public class Transaction {
    @PrimaryKey
    private Long id;
    @ForeignKey(foreignTable = User.class)
    private User user;
    @Column
    private double value;
    @Column
    private Date date;
}
