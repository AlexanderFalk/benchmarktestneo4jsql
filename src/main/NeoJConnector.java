import org.neo4j.driver.v1.*;

import java.util.Random;

import static org.neo4j.driver.v1.Values.parameters;

public class NeoJConnector implements AutoCloseable{

    private final Driver driver;
    private long benchmarkStart, benchmarkStop;

    public NeoJConnector( String uri, String user, String password )
    {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    @Override
    public void close() throws Exception
    {
        driver.close();
    }

    public void benchmark( final String message )
    {
        double benchmark = 0.0;
        double median = 0.0;

        Random random = new Random();
        int count = 20;
        while (count != 0) {
            try (Session session = driver.session()) {
                benchmarkStart = System.currentTimeMillis();
                session.writeTransaction(tx -> {
                    StatementResult result = tx.run("" +
                                    "MATCH(p)-[:ENDORSES]->(endorsement) " +
                                    "WHERE ID(p) = " + random.nextInt(500000) + " RETURN count(p);",
                            parameters("message", message));
                    return result.single().get(0).asInt();
                });
                benchmarkStop = System.currentTimeMillis();
                double tmp = benchmarkStop - benchmarkStart;
                tmp = tmp / 1000;
                if (count == 10) {
                    median = tmp;
                }

                benchmark = benchmark + tmp;
                System.out.println("Time Spent: " + tmp);
                count--;
            }
        }

        benchmark = benchmark / 20;
        System.out.println("Median Time: " + median);
        System.out.println("Average Time: " + benchmark);
    }

    public void twentyRandomRetards(String message) {
        Random random = new Random();
        int count = 20;
        while (count != 0) {
            try (Session session = driver.session()) {
                int greeting = session.writeTransaction(tx -> {
                    StatementResult result = tx.run("" +
                                    "MATCH(p)-[:ENDORSES]->(endorsement) " +
                                    "WHERE ID(p) = " + random.nextInt(500000) + " RETURN count(p);",
                            parameters("message", message));
                    return result.single().get(0).asInt();
                });
                count--;
                System.out.println(greeting);
            }
        }
    }

    public static void main( String... args ) throws Exception
    {
        try ( NeoJConnector greeter =
                      new NeoJConnector( "bolt://localhost:7687", "neo4j", "class" ) )
        {
            greeter.benchmark( "hello, world" );
        }
    }
}
