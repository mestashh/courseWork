package finalStep;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class affiliationCorrection {
    public static void main(String[] args) throws IOException {
        // Чтение JSON файла
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Добавлено для форматирования вывода
        List<Map<String, Object>> articles = objectMapper.readValue(new File("input.json"), new TypeReference<List<Map<String, Object>>>() {});

        // Обработка и исправление опечаток в аффилиациях
        for (Map<String, Object> article : articles) {
            List<Map<String, String>> affiliations = (List<Map<String, String>>) article.get("Affiliations");
            for (int i = 0; i < affiliations.size(); i++) {
                Map<String, String> currentAffiliation = affiliations.get(i);
                String currentAffiliationName = currentAffiliation.get("affiliation");
                for (int j = i + 1; j < affiliations.size(); j++) {
                    Map<String, String> otherAffiliation = affiliations.get(j);
                    String otherAffiliationName = otherAffiliation.get("affiliation");
                    int levenshteinDistance = StringUtils.getLevenshteinDistance(currentAffiliationName, otherAffiliationName);
                    // Задаем порог схожести между строками (например, 3)
                    if (levenshteinDistance <= 3) {
                        // Исправляем опечатки, заменяя название аффилиации с меньшим индексом
                        otherAffiliation.put("affiliation", currentAffiliationName);
                    }
                }
            }
        }

        // Запись результатов в исходный JSON файл
        objectMapper.writeValue(new File("neo4j.json"), articles);
    }
}

