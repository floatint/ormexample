package ru.vsu.ormexample.persistance.simple;

import ru.vsu.ormexample.entities.Transaction;
import ru.vsu.ormexample.entities.User;
import ru.vsu.ormexample.persistance.AbstractRepository;
import ru.vsu.ormexample.persistance.DbMapper;
import ru.vsu.ormexample.persistance.Repository;
import ru.vsu.ormexample.utils.ResourcesUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TransactionRepository extends AbstractRepository<Transaction, Long> {
    //DbMapper<Transaction, ResultSet> lazyFindMapper;

    public TransactionRepository(){
        connectionPoolInit();
        try{
            Connection conn = connectionPool.getConnection();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet rs = dbm.getTables(null, null, "transactions", null);
            if (!rs.next()){
                createTable();
            }
            conn.close();
        }catch(SQLException ex){
            throw new RuntimeException(ex);
        }
    }
    @Override
    public Transaction find(Long id) {
        //lazy
        DbMapper<Transaction, ResultSet> mapper = data -> {
            Transaction trans = new Transaction();

            try{
                while (data.next()){
                    trans.setId(data.getLong("id"));
                    trans.setValue(data.getDouble("value"));
                    trans.setDate(data.getDate("date"));
                }
            } catch(SQLException ex){
                throw new RuntimeException(ex);
            }
            return trans;
        };

        Transaction trans = null;
        try {
            Connection conn = connectionPool.getConnection();
            PreparedStatement state = conn.prepareStatement("SELECT * FROM transactions WHERE id = ?;");
            state.execute();
            trans = mapper.map(state.getResultSet());
            conn.close();
        }catch(SQLException ex){
            throw new RuntimeException(ex);
        }
        return trans;
    }

    @Override
    public Collection<Transaction> findAll() {
        DbMapper<List<Transaction>, ResultSet> mapper = data -> {
            List<Transaction> users = new ArrayList<>();

            try{
                while (data.next()){
                    Transaction trans = new Transaction();
                    trans.setId(data.getLong("id"));
                    trans.setValue(data.getDouble("value"));
                    trans.setDate(data.getDate("date"));
                    users.add(trans);
                }
            } catch(SQLException ex){
                throw new RuntimeException(ex);
            }
            return users;
        };

        List<Transaction> trans = new ArrayList<>();
        try {
            Connection conn = connectionPool.getConnection();
            Statement state = conn.createStatement();
            state.execute("SELECT * FROM transactions;");
            trans =  mapper.map(state.getResultSet());
            conn.close();
        }catch(SQLException ex){
            throw new RuntimeException(ex);
        }
        return trans;
    }

    @Override
    public Transaction create(Transaction transaction) {
        //insert query
        StringBuilder insertBuilder = new StringBuilder();
        insertBuilder.append("INSERT INTO transactions(user_id, value, date)\n");
        insertBuilder.append("VALUES(?, ?, ?);");
        try{
            Connection conn = connectionPool.getConnection();
            PreparedStatement state = conn.prepareStatement(insertBuilder.toString(),Statement.RETURN_GENERATED_KEYS);
            state.setLong(1, transaction.getUser().getId());
            state.setDouble(2, transaction.getValue());
            state.setDate(3, transaction.getDate());
            state.executeUpdate();
            ResultSet rs = state.getGeneratedKeys();
            //get generated id
            if (rs.next()){
                transaction.setId(rs.getLong(1));
            }
            conn.close();
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
        return transaction;
    }

    @Override
    public boolean update(Transaction transaction) {
        //update query
        StringBuilder updateBuilder = new StringBuilder();
        updateBuilder.append("UPDATE transactions\n");
        updateBuilder.append("SET user_id = ?, value = ?, date = ?\n");
        updateBuilder.append("WHERE id = ?;");
        try{
            Connection conn = connectionPool.getConnection();
            PreparedStatement state = conn.prepareStatement(updateBuilder.toString());
            state.setLong(1, transaction.getUser().getId());
            state.setDouble(2, transaction.getValue());
            state.setDate(3, transaction.getDate());
            state.setLong(4, transaction.getId());
            state.executeUpdate();
            conn.close();
        }catch (SQLException ex){
            throw new RuntimeException(ex);
        }
        return true;
    }

    @Override
    public boolean delete(Transaction transaction) {
        StringBuilder deleteBuilder = new StringBuilder();
        deleteBuilder.append("DELETE FROM transactions WHERE id = ?;");
        try{
            Connection conn = connectionPool.getConnection();
            PreparedStatement state = conn.prepareStatement(deleteBuilder.toString());
            state.setLong(1, transaction.getId());
            state.executeUpdate();
            conn.close();
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
        return true;
    }

    private void createTable(){
        try {
            Connection conn = connectionPool.getConnection();
            Statement state = conn.createStatement();
            //create tables
            String q = ResourcesUtils.getResourceFileAsString("deploy/create_tables.sql");
            state.execute(q);
            conn.close();
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
