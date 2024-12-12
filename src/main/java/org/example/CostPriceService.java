package org.example;

import org.example.model.CostPrice;
import org.example.model.CostPriceRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Document;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

@Service
public class CostPriceService {

    @Autowired
    CostPriceRepository costPriceRepository;

    public boolean setCommission(Document document) throws IOException {
//        HashMap<String, Integer> comMap = new HashMap<>();

        String path = "src/main/resources/comission" +
                ".xlsx";
        try {
            FileInputStream file = new FileInputStream(document.getFileName());
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            rowIterator.next();
            Row row;
            while (rowIterator.hasNext()) {
                row = rowIterator.next();
                if (row.getRowNum() == 0) {
                    continue;
                }
                CostPrice costPrice = new CostPrice();
                String nmId = row.getCell(1).toString();
                int value = (int) row.getCell(2).getNumericCellValue();
                costPrice.setNmId(nmId);
                costPrice.setCost(value);
                costPriceRepository.save(costPrice);
//                comMap.put(subj, value);

            }
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        }
        return true;
    }

}
