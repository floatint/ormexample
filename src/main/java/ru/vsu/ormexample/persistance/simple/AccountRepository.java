package ru.vsu.ormexample.persistance.simple;

import com.sun.org.apache.bcel.internal.generic.ACONST_NULL;
import ru.vsu.ormexample.entities.Account;
import ru.vsu.ormexample.persistance.AbstractRepository;
import ru.vsu.ormexample.persistance.DbMapper;
import ru.vsu.ormexample.persistance.Repository;
import ru.vsu.ormexample.utils.ResourcesUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AccountRepository extends AbstractRepository<Account, Long> {

    public AccountRepository(){
        connectionPoolInit();
        try{
            Connection conn = connectionPool.getConnection();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet rs = dbm.getTables(null, null, "accounts", null);
            if (!rs.next()){
                createTable();
            }
            conn.close();
        }catch(SQLException ex){
            throw new RuntimeException(ex);
        }
    }

    //lazy load
    @Override
    public Account find(Long id) {
        //lazy mapper
        DbMapper<Account, ResultSet> mapper = data ->{
            Account account = null;
            try {
                while (data.next()) {
                    account = new Account();
                    account.setId(data.getLong("id"));
                    account.setBalance(data.getDouble("balance"));
                };
            }catch(SQLException ex){
                throw new RuntimeException(ex);
            }
            return account;
        };
        Account acc = null;
        //try select
        try{
            Connection conn = connectionPool.getConnection();
            PreparedStatement state = conn.prepareStatement("SELECT * FROM accounts WHERE id = ?;");
            state.setLong(1, id);
            state.execute();
            acc = mapper.map(state.getResultSet());
            conn.close();
        }catch(SQLException ex){
            throw new RuntimeException(ex);
        }
        return  acc;
    }

    @Override
    public Collection<Account> findAll() {
        //lazy mapper
        DbMapper<List<Account>, ResultSet> mapper = data ->{
          List<Account> accounts = new ArrayList<>();
          try{
              while(data.next()){
                  Account acc = new Account();
                  acc.setId(data.getLong("id"));
                  acc.setBalance(data.getDouble("balance"));
                  accounts.add(acc);
              }
          }catch(SQLException ex){
              throw new RuntimeException(ex);
          }
          return accounts;
        };

        List<Account> accs = new ArrayList<>();
        try{
            Connection conn = connectionPool.getConnection();
            Statement state = conn.createStatement();
            state.execute("SELECT * FROM accounts;");
            accs = mapper.map(state.getResultSet());
            conn.close();
        }catch(SQLException ex){
            throw new RuntimeException(ex);
        }
        return accs;
    }

    @Override
    public Account create(Account account) {
        //insert query
        StringBuilder insertBuilder = new StringBuilder();
        insertBuilder.append("INSERT INTO accounts(balance)\n");
        insertBuilder.append("VALUES(?);");
        try{
            Connection conn = connectionPool.getConnection();
            PreparedStatement state = conn.prepareStatement(insertBuilder.toString(),Statement.RETURN_GENERATED_KEYS);
            state.setDouble(1, account.getBalance());
            state.executeUpdate();
            ResultSet rs = state.getGeneratedKeys();
            //get generated id
            if (rs.next()){
                account.setId(rs.getLong(1));
            }
            conn.close();
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
        return account;
    }

    @Override
    public boolean update(Account account) {
        return false;
    }

    @Override
    public boolean delete(Account account) {
        StringBuilder deleteBuilder = new StringBuilder();
        deleteBuilder.append("DELETE FROM accounts WHERE id = ?;");
        try{
            Connection conn = connectionPool.getConnection();
            PreparedStatement state = conn.prepareStatement(deleteBuilder.toString());
            state.setLong(1, account.getId());
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
