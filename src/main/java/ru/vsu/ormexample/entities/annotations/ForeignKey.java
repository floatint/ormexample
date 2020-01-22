package ru.vsu.ormexample.entities.annotations;

public @interface ForeignKey {
    Class<?> foreignTable();
    String name() default "";
}
