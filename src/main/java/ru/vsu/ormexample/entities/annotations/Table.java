package ru.vsu.ormexample.entities.annotations;

public @interface Table {
    String name() default "";
}
