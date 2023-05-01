package finalStep;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MergeJsonFiles {
    public static void main(String[] args) throws IOException {
        // Чтение JSON файлов
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Добавлено для форматирования вывода
        List<Map<String, Object>> articles1 = objectMapper.readValue(new File("Elibrary.json"), new TypeReference<List<Map<String, Object>>>() {});
        List<Map<String, Object>> articles2 = objectMapper.readValue(new File("Akademi.json"), new TypeReference<List<Map<String, Object>>>() {});

        // Объединение данных JSON файлов
        articles1.addAll(articles2);

        // Запись результатов в новый JSON файл
        objectMapper.writeValue(new File("output.json"), articles1);
    }
}

