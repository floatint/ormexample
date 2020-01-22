package ru.vsu.ormexample.persistance;

import org.h2.jdbcx.JdbcConnectionPool;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class AbstractRepository<E, K> implements Repository<E, K>{
    protected static JdbcConnectionPool connectionPool;

    protected void connectionPoolInit(){
        if (connectionPool == null){
            //get db settings
            InputStream fis = getClass().getClassLoader().getResourceAsStream("app.properties");
            Properties props = new Properties();
            try {
                props.load(fis);
            } catch(IOException ex){
                throw new RuntimeException(ex);
            }

            String user = props.getProperty(props.getProperty("db.user"));
            String password = props.getProperty(props.getProperty("db.password"));
            String url = props.getProperty(props.getProperty("db.path"));

            connectionPool = JdbcConnectionPool.create(url, user, password);
        }
    }

}
