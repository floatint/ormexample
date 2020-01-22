package ru.vsu.ormexample.entities.annotations;

import java.util.ArrayList;

public @interface OneToMany {
    Class<?> foreignTable();
    Class<?> enumerationType() default ArrayList.class;
}
