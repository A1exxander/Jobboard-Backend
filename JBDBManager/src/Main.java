import DB.DBManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException, InterruptedException {
        DBManager db = DBManager.getInstance();
    }
}