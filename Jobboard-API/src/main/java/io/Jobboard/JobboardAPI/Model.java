package io.Jobboard.JobboardAPI;

import io.Jobboard.Contract.ActiveContract;
import io.Jobboard.Contract.CompletedContract;
import io.Jobboard.Contract.Contract;
import io.Jobboard.Mail.*;
import io.Jobboard.User.*;
import io.Jobboard.DB.JDBCUtil;
import javax.mail.MessagingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;


public final class Model {

    private JDBCUtil db = JDBCUtil.getInstance();

    public Model(){}

    public boolean authenticate(String username, String password) throws SQLException {

        ResultSet rs = db.executeQuery("SELECT COUNT(*) AS C FROM USERS WHERE EMAIL = '" + username + "' AND PASSWORD = '" + password + "';");

        if (rs.getInt("C") >= 1) {
            return true;
        }
        else {
            return false;
        }
    }

    public User getUserData(String email) throws SQLException {

        ResultSet rs = db.executeQuery("SELECT EMAIL, AccountType, Token FROM USERS WHERE EMAIL = '" + email + "';");
        if (rs != null) {
            User user = new User(email, rs.getString("Token"), rs.getString("AccountType"));
            return user;
        }
        else {
            return null;
        }

    }

    public void registerUser(final User user, String password) throws SQLException {

        db.executeUpdate("INSERT INTO `USERS` (`Email`, `Password`, `AccountType`, `Token`) VALUES (\"" + user.getEmail() + "\", \"" + password +"\", \"" + user.getAccountTypeString() + "\", \"" + user.getToken() + "\");");

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Mail m = null;
                try {
                    m = new Mail();
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
                finally {
                    if (m != null) {
                        try {
                            m.sendMessage(user.getEmail(), EmailTemplates.WELCOME);
                        } catch (MessagingException e) {
                            throw new RuntimeException(e);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }); // Email user in detached thread to improve responsiveness
        t1.start();

    }

    public boolean passwordResetTokenExists(String email) throws SQLException {
        ResultSet rs = db.executeQuery("SELECT COUNT(*) AS C FROM PASSWORD_RESET_TOKENS WHERE EMAIL = '" + email + "';");
        if (rs.getInt("C") >= 1) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean passwordResetTokenValid(String Token) throws SQLException {
        ResultSet rs = db.executeQuery("SELECT COUNT(*) AS C FROM PASSWORD_RESET_TOKENS WHERE Token = '" + Token + "';");
        if (rs.getInt("C") >= 1) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean emailExists(String email) throws SQLException {
        ResultSet rs = db.executeQuery("SELECT COUNT(*) AS C FROM USERS WHERE EMAIL = '" + email + "';");
        return rs.getInt("C") >= 1;
    }

    public String genPasswordResetToken(String email) throws SQLException {

        String token = "";

        for (int i = 0; i < 4; i++){
            int num = (int) (48 + ((Math.random() * 100) % 10));
            token += (char) num;
        }

        db.executeUpdate("INSERT INTO `PASSWORD_RESET_TOKENS` (`Email`, `Token`) VALUES (\"" + email + "\", \"" + token +"\" );");
        return token;

    }

    public AccountTypes getAccountTypeWithToken(String token) throws SQLException {
        ResultSet rs = db.executeQuery("SELECT AccountType FROM USERS WHERE TOKEN = \"" + token + "\" ;");
        AccountTypes accountType = AccountTypes.valueOf(rs.getString("AccountType"));
        return accountType;
    }

    public String getAccountEmailWithToken(String token) throws SQLException {
        ResultSet rs = db.executeQuery("SELECT Email FROM USERS WHERE TOKEN = \"" + token + "\" ;");
        String email = rs.getString("Email");
        return email;
    }

    public LinkedList<Contract> getContracts() throws SQLException {

        LinkedList<Contract> contracts = new LinkedList<>();
        ResultSet rs = db.executeQuery("SELECT * FROM CONTRACT WHERE JobID NOT IN (SELECT JobID FROM ACTIVE_CONTRACTS) AND JobID NOT IN (SELECT JobID FROM COMPLETED_CONTRACTS);"); // LJ faster than NOT IN
        if (rs == null){
            return null;
        }
        while (!rs.isAfterLast()){
            Contract temp = new Contract(rs.getInt("JobID"), rs.getString("Description"), rs.getString("Contractor"), rs.getInt("Length") );
            contracts.push(temp);
            rs.next();
        }
        return contracts;
    }

    public LinkedList<Contract> getContracts(String token, String email, String accountType) throws SQLException { // TODO : Remove all tokens, have controller deal with it!

        if (token == null){ // TODO : Change this to a function which validates token w email
            return null;
        }

        LinkedList<Contract> list = new LinkedList<>();

        if (accountType.equals("CONTRACTEE")) {

                ResultSet rs = db.executeQuery("SELECT AC.JobID, AC.Deadline, C.JobID, C.Description, C.Contractor FROM ACTIVE_CONTRACTS AC INNER JOIN CONTRACT C ON AC.JobID = C.JobID WHERE AC.CONTRACTEE = \"" + email + "\" ;");
                if (rs == null) {
                    return null;
                }

                while (!rs.isAfterLast()){
                    ActiveContract ac = new ActiveContract(rs.getInt("JobID"), rs.getString("Description"), rs.getString("Contractor"), email, 10);
                    list.push(ac);
                    rs.next();
                }

            }
        else {
                ResultSet rs = db.executeQuery("SELECT C.JobID, C.Description, AC.Deadline, AC.Contractee FROM CONTRACT C LEFT JOIN ACTIVE_CONTRACTS AC ON C.JobID = AC.JobID WHERE C.CONTRACTOR = \"" + email + "\" AND C.JobID NOT IN (SELECT JobID FROM COMPLETED_CONTRACTS) ;");
                if (rs == null) {
                    return null;
                }
                while (!rs.isAfterLast()){
                    ActiveContract ac = new ActiveContract(rs.getInt("JobID"), rs.getString("Description"), email, rs.getString("Contractee"), 10); // TODO : Add days remaing calculator, compare deadline - now()
                    list.push(ac);
                    rs.next();
                }

        }

        return list ;

    }

    public LinkedList<Contract> getCompletedContracts(String token, String email, String accountType) throws SQLException {

        if (token == null){ // TODO : Change this to a function which validates token w email
            return null;
        }

        ResultSet rs = null;
        LinkedList<Contract> list = new LinkedList<>();

        if (accountType.equals("CONTRACTEE")) {

                rs = db.executeQuery("SELECT CC.JobID, CC.CompletionDate, C.Contractor, C.Description FROM COMPLETED_CONTRACTS CC INNER JOIN CONTRACT C ON CC.JobID = C.JobID WHERE CC.CONTRACTEE = \"" + email + "\" ;");
                if (rs == null) {
                    return null;
                }
                while (!rs.isAfterLast()){
                    CompletedContract cc = new CompletedContract(rs.getInt("JobID"), rs.getString("Description"), rs.getString("Contractor"), email, "NULL");
                    list.push(cc);
                    rs.next();
                }

            }
        if (accountType.equals("CONTRACTOR")) {
                rs = db.executeQuery("SELECT CC.JobID, C.Description, CC.CompletionDate, CC.Contractee FROM COMPLETED_CONTRACTS CC INNER JOIN CONTRACT C ON CC.JobID = C.JobID WHERE C.CONTRACTOR = \"" + email + "\" ;");
                if (rs == null) {
                    return null;
                }
                while (!rs.isAfterLast()){
                    CompletedContract cc = new CompletedContract(rs.getInt("JobID"), rs.getString("Description"), email, rs.getString("Contractee"), "NULL"); // TODO : Add days remaining calculator, compare deadline - now()
                    list.push(cc);
                    rs.next();
                }

        }

        return list ;

    }

    void selectContract(String Token, String email, int jobID) throws SQLException {

        ResultSet rs = db.executeQuery("SELECT LENGTH FROM CONTRACT WHERE JobID = '" + jobID + "\';");
        int days = rs.getInt("LENGTH");
        String deadline = LocalDate.now().plusDays(days).toString();
        String fixedDeadline = "";
        int i = 0;
        while (i < deadline.length() && deadline.charAt(i) != ' '){
            fixedDeadline += deadline.charAt(i);
            ++i;
        }
        db.executeUpdate("INSERT INTO ACTIVE_CONTRACTS (JobID, Contractee, Deadline) VALUES ('" + jobID + "', '" + email + "', '" + fixedDeadline + "')");

    }

    void completeContract(String Token, String email, int jobID) throws SQLException {

        String deadline = LocalDate.now().toString();
        String fixedDeadline = "";
        int i = 0;
        while (i < deadline.length() && deadline.charAt(i) != ' '){
            fixedDeadline += deadline.charAt(i);
            ++i;
        }

        db.executeUpdate("INSERT INTO COMPLETED_CONTRACTS (JobID, Contractee, CompletionDate, Link) VALUES ('" + jobID + "', '" + email + "', '" + fixedDeadline + "', 'NULL')");
        db.executeUpdate("DELETE FROM ACTIVE_CONTRACTS WHERE JobID = " + jobID + ";");

    }

    int createContract(String Token, String email, int days, String description) throws SQLException {
        db.executeUpdate("INSERT INTO CONTRACT( Description, Contractor, Length) VALUES ('" + description + "', '" + email + "', '" + days + "')");
        ResultSet rs = db.executeQuery("SELECT JobID FROM CONTRACT WHERE Description = \"" + description + "\" ORDER BY JobID DESC LIMIT 1");
        if (rs != null){
            return rs.getInt("JobID");
        }
        else {
            return 0;
        }
    }

    void deleteContract(int jobID) throws SQLException {
        db.executeUpdate("DELETE FROM CONTRACT WHERE JobID = " + jobID + ";");
        db.executeUpdate("DELETE FROM ACTIVE_CONTRACT WHERE JobID = " + jobID + ";");
    }

}
