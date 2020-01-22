package ru.vsu.ormexample.entities.annotations;

public @interface PrimaryKey {
    String name() default "";
}
