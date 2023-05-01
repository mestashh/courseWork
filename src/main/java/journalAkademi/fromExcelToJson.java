package journalAkademi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;

public class fromExcelToJson {
    public static void main(String[] args) {
        String articlesFile = "articles.xlsx";
        String outputFile = "Akademi.json";

        List<String> urls = readUrlsFromExcel(articlesFile);
        List<Map<String, Object>> articlesInfo = extractArticlesInfo(urls);
        writeToJsonFile(articlesInfo, outputFile);
    }

    private static List<String> readUrlsFromExcel(String filePath) {
        List<String> urls = new ArrayList<>();

        try (InputStream inputStream = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            int numRows = sheet.getPhysicalNumberOfRows();

            // Начинаем с 1, чтобы пропустить первую строку numRows
            for (int rowIndex = 1; rowIndex < numRows; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                Cell cell = row.getCell(0);
                if (cell != null && cell.getCellType() == CellType.STRING) {
                    urls.add(cell.getStringCellValue());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return urls;
    }

    private static List<Map<String, Object>> extractArticlesInfo(List<String> urls) {
        List<Map<String, Object>> articlesInfo = new ArrayList<>();

        for (int i = 0; i < urls.size(); i++) {
            String url = urls.get(i);
            try {
                Map<String, Object> articleInfo = extractArticleInfo(url);
                articlesInfo.add(articleInfo);
            } catch (IOException e) {
                System.out.println("Не удалось обработать URL: " + url);
                e.printStackTrace();
            }
        }
        return articlesInfo;
    }

    private static Map<String, Object> extractArticleInfo(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        Map<String, Object> articleInfo = new LinkedHashMap<>(); // Используем LinkedHashMap для сохранения порядка полей

        // 1. Title
        Element articleTitle = document.select("meta[name=citation_title]").first();
        articleInfo.put("title", articleTitle.attr("content"));

        // 2. Журнал
        Element journalTitle = document.select("meta[name=citation_journal_title]").first();
        articleInfo.put("journal", journalTitle.attr("content"));

        // 4. Volume and Issue
        Elements issue = document.select("volumeissue.inline.c-List__item.c-List__item--secondary.text-metadata-value");
        if (issue != null) {
            String reg = document.select(".volumeissue .c-Button--link").text();
            String[] reg2 = reg.split("[/:]");
            if (reg.contains("/")) {
                articleInfo.put("volume", reg2[0]);
                articleInfo.put("issue", reg2[1]);
            } else {
                articleInfo.put("volume", reg2[0].split(" ")[1]);
                articleInfo.put("issue", reg2[1].split(" ")[2]);
            }

        }

        // 5. Страницы
        Elements pages = document.select(".pagerange.inline.c-Listitem.c-Listitem--secondary.text-metadata-value");
        if (pages != null) {
            articleInfo.put("pages", document.select(".pagerange.inline.c-List__item.c-List__item--secondary.text-metadata-value").text());
        } else {
            articleInfo.put("pages", "");
        }

        // 6. Год
        Element year = document.select("meta[name=citation_publication_date]").first();
        if (year != null) {
            articleInfo.put("year", year.attr("content").substring(0, 4));
        }

        // 7. Авторы
        Elements authors = document.select("meta[name=citation_author]");
        List<String> authorsList = new ArrayList<>();
        for (Element author : authors) {
            authorsList.add(author.attr("content"));
        }
        articleInfo.put("authors", authorsList);
        // 8. Аффилиации авторов
        List<String> affiliationsList = new ArrayList<>();
        Elements links = document.select(".institution");
        for (Element link : links) {
            affiliationsList.add(link.text());
        }

        // Создаем список авторов и аффилиаций
        List<Map<String, String>> authorsWithAffiliations = new ArrayList<>();
        for (int i = 0; i < authorsList.size(); i++) {
            Map<String, String> authorWithAffiliation = new LinkedHashMap<>();
            authorWithAffiliation.put("author", authorsList.get(i));
            authorWithAffiliation.put("affiliation", i < affiliationsList.size() ? affiliationsList.get(i) : "");
            authorsWithAffiliations.add(authorWithAffiliation);
        }

        articleInfo.put("Affiliations", authorsWithAffiliations);


        // 9. PISSN and EISSN
        Element pissn = document.selectFirst("meta[name=citation_issn]");
        if (pissn != null) {
            articleInfo.put("pissn",document.select("meta[name=citation_issn]").get(0).attr("content"));
            articleInfo.put("eissn",document.select("meta[name=citation_issn]").get(1).attr("content"));
        }

        // 11. Abstract
        Elements abstractEl = document.select(".abstract");
        if (abstractEl != null) {
            articleInfo.put("abstract", document.select(".abstract").text());
        } else {
            articleInfo.put("abstract", "");
        }

        // 12. Референсы
        List<String> referencesList = new ArrayList<>();
        Elements referenceElements = document.select(".citationText");
        for (Element referenceElement : referenceElements) {
            referencesList.add(referenceElement.text());
        }
        articleInfo.put("references", referencesList);

        return articleInfo;
    }

    private static void writeToJsonFile(List<Map<String, Object>> articlesInfo, String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(fileWriter, articlesInfo);
            System.out.println("Данные успешно записаны в файл '" + filePath + "'!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

