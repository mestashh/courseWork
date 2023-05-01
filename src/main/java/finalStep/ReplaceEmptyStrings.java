package finalStep;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ReplaceEmptyStrings {
    public static void main(String[] args) throws IOException {
        // Чтение данных из исходного JSON файла
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Добавлено для форматирования вывода
        List<Map<String, Object>> originalData = objectMapper.readValue(new File("output.json"), new TypeReference<List<Map<String, Object>>>() {});

        // Замена пустых строк на "none"
        for (Map<String, Object> entry : originalData) {
            for (Map.Entry<String, Object> field : entry.entrySet()) {
                if (field.getValue() instanceof String && field.getValue().equals("")) {
                    field.setValue("none");
                } else if (field.getValue() instanceof List) {
                    List<?> list = (List<?>) field.getValue();
                    for (int i = 0; i < list.size(); i++) {
                        Object item = list.get(i);
                        if (item instanceof String && item.equals("")) {
                            ((List<String>) list).set(i, "none");
                        }
                    }
                } else if (field.getValue() instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) field.getValue();
                    for (Map.Entry<String, Object> subField : map.entrySet()) {
                        if (subField.getValue() instanceof String && subField.getValue().equals("")) {
                            subField.setValue("none");
                        }
                    }
                }
            }
        }

        // Запись измененных данных в новый JSON файл
        objectMapper.writeValue(new File("input.json"), originalData);
    }
}

