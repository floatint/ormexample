package ru.vsu.ormexample.entities.annotations;

public @interface Column {
    String name() default "";
}
