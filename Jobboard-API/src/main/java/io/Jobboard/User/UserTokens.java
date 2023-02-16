package io.Jobboard.User;

import java.sql.*;
import java.util.HashMap;
import io.Jobboard.DB.JDBCUtil;

public class UserTokens {

    private static UserTokens instance = null;
    private HashMap<String, String> userTokens = new HashMap<>();

    public static UserTokens getInstance() throws SQLException, InterruptedException {

        if (instance == null){
            instance = new UserTokens();
        }

        return instance;
    }

    private UserTokens() throws SQLException, InterruptedException {

        fetchUserTokens();

    }

    private void fetchUserTokens() throws SQLException {

        JDBCUtil db = JDBCUtil.getInstance();
        ResultSet rs = db.executeQuery("SELECT Token, EMAIL FROM USERS;");

        while (!rs.isAfterLast()){

            userTokens.put(rs.getString("Token"), rs.getString("EMAIL"));
            rs.next();

        }



    }

    public void createTokenUserPair(String token, String user){
        userTokens.put(token, user);
    }

    public boolean tokenExists(String token){

        if (userTokens.containsKey(token)){
            return true;
        }
        else {
            return false;
        }

    }

    public String getUsername(String token){
        return userTokens.get(token);
    }

    public HashMap<String, String> getTokenMap() {
        return userTokens;
    }

}
