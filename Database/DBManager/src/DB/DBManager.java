package DB;

import java.lang.Thread;
import java.sql.SQLException;

public class DBManager { // Unsure if this should be apart of our API or a script that runs on MSWB, but it really doesn't matter

    private static DBManager instance = null;
    private DBConnector db = DBConnector.getInstance();

    private DBManager() throws InterruptedException, SQLException {

        new Thread(() -> {
            try {
                keepActiveContractsUpdated();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).start();
        new Thread(() -> {
            try {
                deletePasswordResetTokens();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }


    public static DBManager getInstance() throws SQLException, InterruptedException {

        if (instance == null){
            instance = new DBManager();
        }
        return instance;

    }

    void keepActiveContractsUpdated() throws InterruptedException, SQLException {

        while (true){
            db.executeUpdate("INSERT INTO COMPLETED_CONTRACTS SELECT JobID, Contractee, CURDATE(), 'NULL' FROM ACTIVE_CONTRACTS WHERE CURDATE() > Deadline;");
            db.executeUpdate("DELETE FROM ACTIVE_CONTRACTS WHERE CURDATE() > Deadline;");
            Thread.sleep(6000000);
        }

    }

    void deletePasswordResetTokens() throws SQLException, InterruptedException {

        while (true) { // Not ideal but okay for now
            db.executeUpdate("TRUNCATE PASSWORD_RESET_TOKENS;");
            Thread.sleep(600000);
        }

    }

}
