package ru.vsu.ormexample.persistance.simple;

import ru.vsu.ormexample.entities.Account;
import ru.vsu.ormexample.entities.Transaction;
import ru.vsu.ormexample.entities.User;
import ru.vsu.ormexample.persistance.AbstractRepository;
import ru.vsu.ormexample.persistance.DbMapper;
import ru.vsu.ormexample.utils.ResourcesUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class UserRepository extends AbstractRepository<User, Long> {

    public UserRepository(){
        connectionPoolInit();
        try{
            Connection conn = connectionPool.getConnection();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet rs = dbm.getTables(null, null, "users", null);
            if (!rs.next()){
                createTable();
                //fillData();
            }
            conn.close();
        }catch(SQLException ex){
            throw new RuntimeException(ex);
        }
    }

    //lazy load
    @Override
    public User find(Long id) {
        DbMapper<User, ResultSet> mapper = data -> {
            User user = new User();

            try{
                while (data.next()){
                    user.setId(data.getLong("id"));
                    user.setFirstName(data.getString("first_name"));
                    user.setLastName(data.getString("last_name"));
                    user.setPhone(data.getInt("phone"));
                }
            } catch(SQLException ex){
                throw new RuntimeException(ex);
            }
            return user;
        };

        User user = null;
        try {
            Connection conn = connectionPool.getConnection();
            PreparedStatement state = conn.prepareStatement("SELECT * FROM users WHERE id = ?;");
            state.setLong(1, id);
            state.execute();
            user =  mapper.map(state.getResultSet());
            conn.close();
        }catch(SQLException ex){
            throw new RuntimeException(ex);
        }
        return user;
    }

    @Override
    public Collection<User> findAll(){
        return lazyFindAll();
    }

    //lazy load
    public Collection<User> lazyFindAll() {
        DbMapper<List<User>, ResultSet> mapper = data -> {
            List<User> users = new ArrayList<>();

            try{
                while (data.next()){
                    User user = new User();
                    user.setId(data.getLong("id"));
                    user.setFirstName(data.getString("first_name"));
                    user.setLastName(data.getString("last_name"));
                    user.setPhone(data.getInt("phone"));
                    users.add(user);
                }
            } catch(SQLException ex){
                throw new RuntimeException(ex);
            }
            return users;
        };

        List<User> users = new ArrayList<>();
        try {
            Connection conn = connectionPool.getConnection();
            Statement state = conn.createStatement();
            state.execute("SELECT * FROM users;");
            users = mapper.map(state.getResultSet());
            conn.close();
        }catch(SQLException ex){
            throw new RuntimeException(ex);
        }
        return users;
    }

    //eager load
    public Collection<User> eagerFindAll(){
        DbMapper<List<User>, ResultSet> mapper = data -> {
            List<User> users = new ArrayList<>();
            try{
                Connection conn = connectionPool.getConnection();
                PreparedStatement state = conn.prepareStatement("SELECT * FROM user_accounts WHERE user_id = ?;");
                PreparedStatement state2 = conn.prepareStatement("SELECT * FROM accounts WHERE id = ?;");
                PreparedStatement state3 = conn.prepareStatement("SELECT * FROM account_transactions WHERE account_id = ?;");
                PreparedStatement state4 = conn.prepareStatement("SELECT * FROM transactions WHERE id = ?;");
                while(data.next()){
                    User usr = new User();
                    usr.setId(data.getLong("id"));
                    usr.setFirstName(data.getString("first_name"));
                    usr.setLastName(data.getString("last_name"));
                    usr.setPhone(data.getInt("phone"));
                    //user's accounts
                    List<Account> accs = new ArrayList<>();
                    try{
                        state.setLong(1, usr.getId());
                        state.execute();
                        ResultSet accsSet = state.getResultSet();
                        while(accsSet.next()){
                            Account acc = new Account();
                            acc.setId(accsSet.getLong("id"));
                            state2.setLong(1, acc.getId());
                            state2.execute();
                            ResultSet accSet = state2.getResultSet();
                            while(accSet.next()){
                                acc.setBalance(accSet.getDouble("balance"));
                                List<Transaction> transs = new ArrayList<>();

                                state3.setLong(1, acc.getId());
                                state3.execute();
                                ResultSet transsSet = state3.getResultSet();
                                while(transsSet.next()){
                                    Transaction t = new Transaction();
                                    state4.setLong(1, transsSet.getLong("transaction_id"));
                                    state4.execute();
                                    ResultSet transSet = state4.getResultSet();
                                    while(transSet.next()){
                                        t.setId(transSet.getLong("id"));
                                        t.setValue(transSet.getDouble("value"));
                                        t.setDate(transSet.getDate("date"));
                                        t.setUser(null);
                                    }
                                    transs.add(t);
                                }
                                acc.setTransactions(transs);
                            }
                           accs.add(acc);
                        }
                    }catch(SQLException ex){
                        throw new RuntimeException(ex);
                    }
                    usr.setAccounts(accs);
                    users.add(usr);
                }
                conn.close();
            }catch(SQLException ex){
                throw new RuntimeException(ex);
            }
            return users;
        };

        List<User> users = new ArrayList<>();
        //get dataset
        try{
            Connection conn = connectionPool.getConnection();
            Statement state = conn.createStatement();
            users = mapper.map(state.executeQuery("SELECT * FROM users;"));
            conn.close();
        }catch(SQLException ex){
            throw new RuntimeException(ex);
        }
        return users;
    }



    @Override
    public User create(User user) {
        //insert query
        StringBuilder insertBuilder = new StringBuilder();
        insertBuilder.append("INSERT INTO users(first_name, last_name, phone)\n");
        insertBuilder.append("VALUES(?, ?, ?);");
        try{
            Connection conn = connectionPool.getConnection();
            PreparedStatement state = conn.prepareStatement(insertBuilder.toString(), Statement.RETURN_GENERATED_KEYS);

            state.setString(1, user.getFirstName());
            state.setString(2, user.getLastName());
            state.setInt(3, user.getPhone());
            state.executeUpdate();
            ResultSet rs = state.getGeneratedKeys();
            //get generated id
            if (rs.next()){
                user.setId(rs.getLong(1));
            }
            conn.close();
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
        return user;
    }

    //lazy update
    @Override
    public boolean update(User user) {
        //check account by null
        return false;
    }

    @Override
    public boolean delete(User user) {
        StringBuilder deleteBuilder = new StringBuilder();
        deleteBuilder.append("DELETE FROM users WHERE id = ?;");
        try{
            Connection conn = connectionPool.getConnection();
            PreparedStatement state = conn.prepareStatement(deleteBuilder.toString());
            state.setLong(1, user.getId());
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


    //fill db
    private void fillData(){
        AccountRepository accRep = new AccountRepository();
        TransactionRepository transRep = new TransactionRepository();
        int usersCount = 1024;
        int maxAccountsPerUser = 32;
        int maxTransactionsPerAccount = 2048;
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder();

        //init users
        for (int i = 1; i <= usersCount; i++){
            User usr = new User();
            usr.setFirstName("Name_" + i);
            usr.setLastName("LastName_" + i);
            usr.setPhone(rnd.nextInt(899999999));
            usr = create(usr);
            int userAccountsCount = rnd.nextInt(maxAccountsPerUser);
            //init accounts
            for (int j = 0; j <= userAccountsCount; j++){
                Account acc = new Account();
                acc.setBalance(rnd.nextInt(120000));
                acc = accRep.create(acc);
                //link table init
                sb.setLength(0);
                sb.append("INSERT INTO user_accounts(user_id, account_id)\n");
                sb.append("VALUES(?,?);");
                try {
                    Connection conn = connectionPool.getConnection();
                    PreparedStatement state = conn.prepareStatement(sb.toString());
                    state.setLong(1, usr.getId());
                    state.setLong(2, acc.getId());
                    state.executeUpdate();
                    conn.close();
                }catch(SQLException ex){
                    throw new RuntimeException(ex);
                }
            }
        }
        //transactions init
        int accTransactionsCount = rnd.nextInt(maxTransactionsPerAccount);
        for (int k = 0; k < accTransactionsCount; k++){
            Transaction trans = new Transaction();
            trans.setValue(rnd.nextInt(45738945));
            trans.setDate(new Date(System.currentTimeMillis()));
            //tmp
            User u = new User();
            u.setId((long)rnd.nextInt(usersCount) + 1);
            trans.setUser(u);
            trans = transRep.create(trans);

            //account link
            sb.setLength(0);
            sb.append("INSERT INTO account_transactions(account_id, transaction_id)\n");
            sb.append("VALUES (?, ?);");
            try{
                Connection conn = connectionPool.getConnection();
                PreparedStatement state = conn.prepareStatement(sb.toString());
                state.setLong(1, accRep.find((long)rnd.nextInt(usersCount) + 1).getId());
                state.setLong(2, trans.getId());
                state.executeUpdate();
                conn.close();
            }catch(SQLException ex){
                throw new RuntimeException(ex);
            }
        }


    }
}
