package ru.vsu.ormexample.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtils {
    public static String getTypeIdentifier(Class<?> clazz){
        //integer
        if (clazz.equals(int.class) || clazz.equals(long.class) ||
            clazz.equals(Integer.class) || clazz.equals(Long.class)){
            return "INTEGER";
        }
        //float
        if (clazz.equals(float.class) || clazz.equals(double.class) ||
            clazz.equals(Float.class) || clazz.equals(Double.class)){
            return "REAL";
        }
        //DATE
        if (clazz.equals(LocalDateTime.class)){
            return "DATE";
        }
        //other just string
        return "NVARCHAR(256)";
    }

    public static List<Field> getAnnotatedFields(Class<?> clazz, Class<? extends Annotation> annotation){
        List<Field> fields = new ArrayList<>();
        for (Field f : clazz.getDeclaredFields()){
            if (f.isAnnotationPresent(annotation)){
                fields.add(f);
            }
        }
        return fields;
    }
}
