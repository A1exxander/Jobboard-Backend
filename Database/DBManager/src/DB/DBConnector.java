package DB;
import java.sql.*;

public class DBConnector {

        private static DBConnector instance;
        private static Connection connection;

        public static DBConnector getInstance() {

            if (instance == null){
                instance = new DBConnector();
            }

            return instance;

        }

        private DBConnector() { // Thought of overriding this with a function that takes 3 args but useless as our class is a singleton - There can only be 1 DB.

            try {
                final String URL = "jdbc:mysql://127.0.0.1:3306/jobboard";
                final String username = "API";
                final String password = "password";
                connection = DriverManager.getConnection(URL, username, password);
            } catch (Exception E){
                E.printStackTrace();
            }

        }

        public ResultSet executeQuery(String query) throws SQLException {

            if (connection == null){
                return null;
            }

            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery(query);

            if (rs.next()){
                return rs;
            }
            else {
                return null;
            }

        }

        public void executeUpdate(String query) throws SQLException {

            if (connection == null){
                return;
            }

            Statement s = connection.createStatement();
            s.executeUpdate(query);

        }

    }

