package journalAkademi;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArticlesToExcel {
    private static List<String> getAllArticleLinks() {
        String baseUrl = "https://akjournals.com";
        String journalsUrl = baseUrl + "/collection/All?access=user&pageSize=50&sort=datedescending";
        List<String> allArticleLinks = new ArrayList<>();

        try {
            Document journalsPage = Jsoup.connect(journalsUrl).get();
            Elements journalLinks = journalsPage.select("a[href^='/view/journals/']");
            for (Element journalLink : journalLinks) {
                String journalUrl = baseUrl + journalLink.attr("href");
                Document journalPage = Jsoup.connect(journalUrl).get();
                int totalPages = 1;
                Element paginationElement = journalPage.selectFirst("ul.pagination");
                if (paginationElement != null) {
                    Element lastPageElement = paginationElement.select("a[aria-label^='Page']").last();
                    totalPages = Integer.parseInt(lastPageElement.text());
                }
                for (int i = 1; i <= totalPages; i++) {
                    if (i > 1) {
                        journalPage = Jsoup.connect(journalUrl + "?page=" + i).get();
                    }
                    Elements articleLinks = journalPage.select("a[href^='/view/journals/']");
                    for (Element articleLink : articleLinks) {
                        String articleUrl = baseUrl + articleLink.attr("href");
                        if (articleUrl.contains("/article")) {
                            allArticleLinks.add(articleUrl);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return allArticleLinks;
    }
    private static void writeUrlsToExcel(List<String> allArticleLinks, String filePath) {
        // Создаем файл Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Статьи");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Ссылка на статью");
        headerRow.createCell(1).setCellValue("Номер");

        // Записываем данные в файл Excel
        for (int i = 0; i < allArticleLinks.size(); i++) {
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(allArticleLinks.get(i));
            row.createCell(1).setCellValue(i + 1);
        }

        // Автоматическая настройка размера столбцов
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

        // Запись данных в файл
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Файл '" + filePath + "' успешно создан!");
    }
    public static void main(String[] args) {
        // Получение списка ссылок на статьи
        List<String> allArticleLinks = getAllArticleLinks();

        // Запись ссылок на статьи в файл Excel
        String outputFilePath = "articles.xlsx"; // Замените на путь, куда вы хотите сохранить файл
        writeUrlsToExcel(allArticleLinks, outputFilePath);
    }
}
