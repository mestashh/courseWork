package finalStep;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.util.*;

public class fromNeo4jToList {
    public static void main(String[] args) {
        try (Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "Zeuszeus1973"))) {
            List<List<String>> affiliationsAndAuthors = getAffiliationsAndAuthors(driver);
            for (List<String> affiliationAndAuthors : affiliationsAndAuthors) {
                System.out.println("Аффилиация: " + affiliationAndAuthors.get(0));
                System.out.print("Авторы: ");
                for (int i = 1; i < affiliationAndAuthors.size(); i++) {
                    System.out.print(affiliationAndAuthors.get(i) + (i < affiliationAndAuthors.size() - 1 ? ", " : ""));
                }
                System.out.println("\n");
            }
        }
    }

    public static List<List<String>> getAffiliationsAndAuthors(Driver driver) {
        List<List<String>> affiliationsAndAuthors = new ArrayList<>();
        String query = "MATCH (a:Author)-[:AFFILIATED_WITH]->(af:Affiliation) WHERE af.name <> \"\" RETURN af.name as affiliation, a.name as author";

        try (Session session = driver.session()) {
            Result result = session.run(query);
            while (result.hasNext()) {
                Record record = result.next();
                String affiliation = record.get("affiliation").asString();
                String author = record.get("author").asString();

                if (affiliation.contains(author)) {
                    affiliation = affiliation.replace(author, "").trim();
                }

                boolean added = false;
                for (List<String> list : affiliationsAndAuthors) {
                    if (list.get(0).equals(affiliation)) {
                        list.add(author);
                        added = true;
                        break;
                    }
                }

                if (!added) {
                    List<String> newEntry = new ArrayList<>();
                    newEntry.add(affiliation);
                    newEntry.add(author);
                    affiliationsAndAuthors.add(newEntry);
                }
            }
        }
        return affiliationsAndAuthors;
    }
}
