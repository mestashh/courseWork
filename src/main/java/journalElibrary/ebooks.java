package journalElibrary;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class ebooks {
    public static void main(String[] args) throws IOException {
        JSONArray jsonArray = new JSONArray();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Введите информацию о новой статье или введите 'exit' для выхода:");

            JSONObject jsonEntry = new JSONObject();

            System.out.print("Название статьи: ");
            String title = scanner.nextLine();
            if (title.equalsIgnoreCase("exit")) break;
            jsonEntry.put("title", title);

            System.out.print("Журнал: ");
            jsonEntry.put("journal", scanner.nextLine());

            System.out.print("Том: ");
            jsonEntry.put("volume", scanner.nextLine());

            System.out.print("Номер выпуска: ");
            jsonEntry.put("issue", scanner.nextLine());

            System.out.print("Страницы: ");
            jsonEntry.put("pages", scanner.nextLine());

            System.out.print("Год: ");
            jsonEntry.put("year", scanner.nextLine());

            System.out.println("Авторы (вводите по одному, введите 'end' для завершения):");
            JSONArray authors = new JSONArray();
            while (true) {
                String author = scanner.nextLine();
                if (author.equalsIgnoreCase("end")) break;
                authors.put(author);
            }
            jsonEntry.put("authors", authors);

            System.out.println("Аффилиации (введите 'end' для завершения):");
            JSONArray affiliations = new JSONArray();
            while (true) {
                System.out.print("Автор аффилиации: ");
                String author = scanner.nextLine();
                if (author.equalsIgnoreCase("end")) break;

                JSONArray authorAffiliations = new JSONArray();
                System.out.println("Аффилиации автора (вводите по одному, введите 'end' для завершения):");
                while (true) {
                    String affiliation = scanner.nextLine();
                    if (affiliation.equalsIgnoreCase("end")) break;
                    authorAffiliations.put(affiliation);
                }

                JSONObject affiliationEntry = new JSONObject();
                affiliationEntry.put("author", author);
                affiliationEntry.put("affiliations", authorAffiliations);
                affiliations.put(affiliationEntry);
            }
            jsonEntry.put("affiliations", affiliations);

            System.out.print("PISSN: ");
            jsonEntry.put("pissn", scanner.nextLine());

            System.out.print("EISSN: ");
            jsonEntry.put("eissn", scanner.nextLine());

            System.out.print("Аннотация: ");
            jsonEntry.put("abstract", scanner.nextLine());

            jsonArray.put(jsonEntry);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output2.json"))) {
            writer.write(jsonArray.toString(4)); // Записываем данные в файл с отступом в 4 пробела
        }

        System.out.println("Данные успешно сохранены в файл Akademi.json.");
    }
}


