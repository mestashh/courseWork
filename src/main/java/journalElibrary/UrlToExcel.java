package journalElibrary;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;

public class UrlToExcel {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("URLs");
        int rowNum = 0;

        System.out.println("Введите ссылку на веб-страницу:");

        String url = scanner.nextLine();
        try {
            Document document = Jsoup.connect(url).get();
            Elements links = document.select("a[href]");

            for (Element link : links) {
                String linkHref = link.absUrl("href");

                Row row = sheet.createRow(rowNum++);
                Cell cell = row.createCell(0);
                cell.setCellValue(linkHref);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при получении ссылок: " + e.getMessage());
        }

        for (int i = 0; i < rowNum; i++) {
            sheet.autoSizeColumn(i);
        }

        try (OutputStream outputStream = new FileOutputStream("urls.xlsx")) {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении файла: " + e.getMessage());
        }

        System.out.println("Файл сохранен: urls.xlsx");
    }
}
