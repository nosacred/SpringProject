package main;

import main.model.Comission;
import main.model.ComissionRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

@Service
public class ComissionService {

    @Autowired
    ComissionRepository comissionRepository;
    public boolean setCommission() throws IOException {
//        HashMap<String, Integer> comMap = new HashMap<>();

        String path = "src/main/resources/Комиссия.xlsx";
        try {
            FileInputStream file = new FileInputStream(new File(path));
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
                Comission comission = new Comission();
                String subj = row.getCell(1).toString();
                int value = (int) row.getCell(2).getNumericCellValue();
                comission.setSubject(subj);
                comission.setPercent(value);
                comissionRepository.save(comission);
//                comMap.put(subj, value);

            }
        }catch (FileNotFoundException exception){
            exception.printStackTrace();
        }
        return true;
    }

}
