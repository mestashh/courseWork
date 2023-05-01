package journalAkademi;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.util.ArrayList;
import java.util.List;

public class fromNeo4jToList {

    private static final String NEO4J_URI = "bolt://localhost:7687";
    private static final String NEO4J_USERNAME = "neo4j";
    private static final String NEO4J_PASSWORD = "Zeuszeus1973";

    public static void main(String[] args) {
        Driver driver = GraphDatabase.driver(NEO4J_URI, AuthTokens.basic(NEO4J_USERNAME, NEO4J_PASSWORD));

        List<String> affiliations = fetchAffiliations(driver);
        System.out.println("Affiliations: " + affiliations);

        driver.close();
    }

    private static List<String> fetchAffiliations(Driver driver) {
        String query = "MATCH (a:Affiliation) RETURN a.name AS affiliationName";
        List<String> affiliations = new ArrayList<>();

        try (Session session = driver.session()) {
            Result result = session.run(query);

            while (result.hasNext()) {
                Record record = result.next();
                String affiliationName = record.get("affiliationName").asString();
                affiliations.add(affiliationName);
            }
        }

        return affiliations;
    }
}
