import java.sql.*;
import java.util.Properties;
import java.util.Random;

public class JDBCPostgres {

    private long benchmarkStart, benchmarkStop;

    private final String TABLE_PERSONS = "nodes";
    private final String TABLE_ENDORSEMENTS = "edges";

    public static void main(String[] args) throws SQLException {

        JDBCPostgres start = new JDBCPostgres();

        start.benchmark();
    }

    private void getTwentyRandom() throws SQLException {

        Connection connection = setupConnection();
        Random random = new Random();
        int count = 20;
        while (count != 0) {

            PreparedStatement preparedStatement =
                    connection.prepareStatement("SELECT count(*) FROM "+ TABLE_PERSONS + "AS a \n" +
                    "INNER JOIN "+ TABLE_ENDORSEMENTS +" as b \n" +
                    "ON a.node_id=b.source_node_id::INTEGER \n" +
                    "WHERE a.node_id = " + random.nextInt(500000) + ";");

            ResultSet set = preparedStatement.executeQuery();
            while (set.next()) {
                System.out.println(set.getString(1));
            }
            count--;
        }
    }

    private void benchmark() throws SQLException {

        Connection connection = setupConnection();
        Random random = new Random();
        int count = 20;
        double benchmark = 0.0;
        double median = 0.0;
        while (count != 0) {
            benchmarkStart = System.currentTimeMillis();
            PreparedStatement preparedStatement =
                    connection.prepareStatement("SELECT count(*) FROM "+ TABLE_PERSONS +" AS a \n" +
                    "INNER JOIN "+ TABLE_ENDORSEMENTS +" as b \n" +
                    "ON a.node_id=b.source_node_id::INTEGER \n" +
                    "WHERE a.node_id = " + random.nextInt(500000) + ";");

            preparedStatement.executeQuery();

            benchmarkStop = System.currentTimeMillis();
            double tmp = benchmarkStop - benchmarkStart;
            tmp = tmp / 1000;
            if(count == 10) {
                median = tmp;
            }
            System.out.println("# " + count + " - Time spent: " + tmp);
            benchmark = benchmark + tmp;
            count--;
        }

        benchmark = benchmark / 20;
        System.out.println("Median: " + median);
        System.out.println("Average Time: " + benchmark);
    }



    private Connection setupConnection() throws SQLException {
        String url = "jdbc:postgresql://localhost/appdev";
        Properties props = new Properties();
        props.setProperty("user","appdev");
        props.setProperty("password","");
        props.setProperty("ssl","false");
        Connection conn = DriverManager.getConnection(url, props);

        return conn;
    }
}
