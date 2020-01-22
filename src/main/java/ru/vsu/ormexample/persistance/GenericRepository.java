package ru.vsu.ormexample.persistance;

import ru.vsu.ormexample.entities.annotations.*;
import ru.vsu.ormexample.utils.ReflectionUtils;
import ru.webgrozny.iql.IQL;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

public class GenericRepository<E, K> extends AbstractRepository<E, K> {
    private Class<E> clazz;
    private String tableName;

    public GenericRepository(Class<E> clazz){
        this.clazz = clazz;
        connectionPoolInit();

        //check clazz
        if (!clazz.isAnnotationPresent(Entity.class)){
            throw new RuntimeException(String.format("Class '%s' is not annotated as entity", clazz.getName()));
        } else{
            if (!clazz.isAnnotationPresent(Table.class)){
                tableName = clazz.getName();
            } else{
                tableName = clazz.getAnnotation(Table.class).name();
            }
        }
        //create table if not exists
        createTable();

    }

    @Override
    public E find(K id) {
        try{
            Connection conn = connectionPool.getConnection();
            Statement state = conn.createStatement();
            String s = "";
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
        return null;
    }

    @Override
    public Collection<E> findAll() {
        return null;
    }

    @Override
    public E create(E e) {
        return null;
    }

    @Override
    public boolean update(E e) {
        return false;
    }

    @Override
    public boolean delete(E e) {
        return false;
    }


    //get data from db by query
    private ResultSet getResultSet(String query){
        try{
            Connection conn = connectionPool.getConnection();
            Statement state = conn.createStatement();
            if(state.execute(query)){
                return state.getResultSet();
            } else{
                return null;
            }
        } catch(SQLException ex){
            throw new RuntimeException(ex);
        }


    }

    private void createTable(){
        try {
            Connection conn = connectionPool.getConnection();
            Statement state = conn.createStatement();
            StringBuilder qb = new StringBuilder();
            qb.append("CREATE TABLE IF NOT EXISTS ");
            qb.append(tableName);
            qb.append('(');
            //field processing
            Field lastField = clazz.getDeclaredFields()[0];
            for (Field f : clazz.getDeclaredFields()){
                //if not annotated
               if (!f.isAnnotationPresent(Column.class) || !f.isAnnotationPresent(OneToMany.class) ||
                       !f.isAnnotationPresent(PrimaryKey.class) || !f.isAnnotationPresent(ForeignKey.class)){
                   continue;
               }
               //if just column or foreign
               if (f.isAnnotationPresent(Column.class)){
                    String colName;
                    if (f.getAnnotation(Column.class).name().equals("")){
                        colName = f.getName();
                    } else{
                        colName = f.getAnnotation(Column.class).name();
                    }
                    //add column to query builder
                    qb.append(colName);
                    //type detecting
                    qb.append(' ');
                    qb.append(ReflectionUtils.getTypeIdentifier(f.getType()));
                    continue;
               }
               //if primary key
                if (f.isAnnotationPresent(PrimaryKey.class)){
                    String colName;
                    if (f.getAnnotation(PrimaryKey.class).name().equals("")){
                        colName = f.getName();
                    } else{
                        colName = f.getAnnotation(PrimaryKey.class).name();
                    }
                    //add column to query builder
                    qb.append(colName);
                    //type detecting
                    qb.append(' ');
                    qb.append(ReflectionUtils.getTypeIdentifier(f.getType()));
                    qb.append(" NOT NULL AUTO_INCREMENT ");
                    continue;
                }
                //if foreign key
                if (f.isAnnotationPresent(ForeignKey.class)){
                    String colName = f.getAnnotation(ForeignKey.class).foreignTable().getName();
                    //add column to query builder
                    qb.append(colName);
                    //type detecting
                    qb.append(' ');
                    qb.append(ReflectionUtils.getTypeIdentifier(f.getType()));
                    continue;
                }
                if (f != lastField){
                     qb.append(", ");
                }

                //set primary and foreign keys
            }
            //query close
            qb.append(");");
            //run query
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
