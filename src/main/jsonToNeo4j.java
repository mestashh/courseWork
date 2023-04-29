import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class jsonToNeo4j {
    public static void main(String[] args) throws IOException {
        File jsonFile = new File("output.json");
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> papers = objectMapper.readValue(jsonFile, new TypeReference<List<Map<String, Object>>>() {});

        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "Zeuszeus1973"));

        try (Session session = driver.session()) {
            for (Map<String, Object> paper : papers) {
                String title = (String) paper.get("title");
                String journal = (String) paper.get("journal");
                String volume = (String) paper.get("volume");
                String issue = (String) paper.get("issue");
                String pages = (String) paper.get("pages");
                String year = (String) paper.get("year");
                String pissn = (String) paper.get("pissn");
                String eissn = (String) paper.get("eissn");
                String abstractt = (String) paper.get("abstract");

                // Check for null values
                if (title == null || journal == null || volume == null || issue == null || pages == null || year == null || pissn == null || eissn == null || abstractt == null) {
                    continue;
                }

                String query = "CREATE (p:Paper {title: $title, journal: $journal, volume: $volume, issue: $issue, pages: $pages, year: $year, pissn: $pissn, eissn: $eissn, abstractt: $abstractt})";
                session.run(query, Map.of("title", title, "journal", journal, "volume", volume, "issue", issue, "pages", pages, "year", year, "pissn", pissn, "eissn", eissn, "abstractt", abstractt));

                List<String> authors = (List<String>) paper.get("authors");
                List<Map<String, String>> affiliations = objectMapper.convertValue(paper.get("Affiliations"), new TypeReference<List<Map<String, String>>>() {});

                for (String authorName : authors) {
                    if (authorName == null) {
                        continue;
                    }

                    query = "MERGE (a:Author {name: $authorName})";
                    session.run(query, Map.of("authorName", authorName));

                    query = "MATCH (p:Paper {title: $title}), (a:Author {name: $authorName}) CREATE (a)-[:AUTHORED]->(p)";
                    session.run(query, Map.of("title", title, "authorName", authorName));
                }

                for (Map<String, String> affiliation : affiliations) {
                    String authorAffiliation = affiliation.get("affiliation");
                    String authorName = affiliation.get("author");

                    if (authorAffiliation == null || authorName == null) {
                        continue;
                    }

                    query = "MERGE (af:Affiliation {name: $authorAffiliation})";
                    session.run(query, Map.of("authorAffiliation", authorAffiliation));

                    query = "MATCH (a:Author {name: $authorName}), (af:Affiliation {name: $authorAffiliation}) CREATE (a)-[:AFFILIATED_WITH]->(af)";
                    session.run(query, Map.of("authorName", authorName, "authorAffiliation", authorAffiliation));
                }

                // Extract and create reference nodes, and their relationships to the paper node
                List<String> references = (List<String>) paper.get("references");

                if (references != null) {
                    for (String reference : references) {
                        // Check for null values
                        if (reference == null) {
                            continue;
                        }

                        query = "MERGE (r:Reference {citation: $reference})";
                        session.run(query, Map.of("reference", reference));

                        query = "MATCH (p:Paper {title: $title}), (r:Reference {citation: $reference}) CREATE (p)-[:REFERENCES]->(r)";
                        session.run(query, Map.of("title", title, "reference", reference));
                    }
                }
            }
        }
        System.out.println("Данные записаны в базу данных Neo4j");
        driver.close();
    }
}

