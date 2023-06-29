package main;

import main.model.*;
//import org.apache.poi.hssf.util.AreaReference;
//import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.hssf.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableStyle;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

@Service
public class ExelService {
    @Autowired
    CustomSaleRepository customSaleRepository;
    @Autowired
    CustomOrderRepository customOrderRepository;
    @Autowired
    CustomStocksRepository customStocksRepository;
    @Autowired
    StockService stockService;



    public ArrayList<SaleOrder> getStatisticsArray(String api, ZonedDateTime zdt1, ZonedDateTime zdt2){
        ArrayList<SaleOrder> saleOrders = new ArrayList<>();
        ArrayList<Order> orders = (ArrayList<Order>) customOrderRepository.getOrderByApiKeyAndDateBetweenOrderByDate(api,zdt1,zdt2);
        ArrayList<Sale> sales = (ArrayList<Sale>) customSaleRepository.getSaleByApiKeyAndDateBetweenOrderByDate(api,zdt1,zdt2);
        HashSet<String> bcodes = new HashSet<>();
        for(Order order : orders){
            bcodes.add(order.getBarcode());
        }
        for(Sale sale : sales){
            bcodes.add(sale.getBarcode());
        }
        for(String s : bcodes){
                SaleOrder saleOrder = new SaleOrder();
                saleOrder.setBarcode(s);
                if(orders.stream().anyMatch(order -> order.getBarcode().equals(s))){
                    Order oder = orders.stream().filter(order -> order.getBarcode().equals(s)).findAny().get();
                    saleOrder.setNmId(oder.getNmId());
                    saleOrder.setBrand(oder.getBrand());
                    saleOrder.setCategory(oder.getCategory());
                    saleOrder.setSupplierArticle(oder.getSupplierArticle());
                    saleOrder.setSubject(oder.getSubject());
                    saleOrder.setTechSize(oder.getTechSize());
                    saleOrder.setWbLink(oder.getWBLink());
                    saleOrder.setOrderCancel((int) orders.stream().filter(order -> order.getBarcode().equals(s)).
                        filter(order -> order.getIsCancel().equals("true")).count());
                    saleOrder.setOrdersCount((int) orders.stream().filter(order -> order.getBarcode().equals(s)).count());
                } else {
                    Sale sl = sales.stream().filter(order -> order.getBarcode().equals(s)).findAny().get();
                    saleOrder.setNmId(sl.getNmId());
                    saleOrder.setBrand(sl.getBrand());
                    saleOrder.setCategory(sl.getCategory());
                    saleOrder.setSupplierArticle(sl.getSupplierArticle());
                    saleOrder.setSubject(sl.getSubject());
                    saleOrder.setTechSize(sl.getTechSize());
                    saleOrder.setWbLink(sl.getWBLink());
                }
            saleOrder.setTotalLogistic(orders.stream().filter(order -> order.getBarcode().equals(s)).
                    map(Order::getLogisticPrice).reduce(BigDecimal.ZERO,BigDecimal::add));
                saleOrder.setForPay(sales.stream().filter(sale -> sale.getBarcode().equals(s)).map(Sale::getForPay).
                        filter(forPay -> forPay.doubleValue() > 0).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP));
                saleOrder.setCancelPay(sales.stream().filter(sale -> sale.getBarcode().equals(s)).map(Sale::getForPay).
                        filter(forPay -> forPay.doubleValue() < 0).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP));
                saleOrder.setSaleCancel((int) sales.stream().filter(order -> order.getBarcode().equals(s)).filter(order -> order.getForPay().doubleValue() < 0).count());
            saleOrder.setSaleCount((int) sales.stream().filter(order -> order.getBarcode().equals(s)).filter(order -> order.getForPay().doubleValue() > 0).count());

            saleOrder.setTotalPrice(orders.stream().filter(order -> order.getBarcode().equals(s)).
                    map(Order::getTotalPriceWithDisc).reduce(BigDecimal.ZERO,BigDecimal::add));
                saleOrders.add(saleOrder);
            }
        return saleOrders;
        }


        public File getExelFile(String api,ZonedDateTime start , ZonedDateTime end) throws IOException {

        ArrayList<SaleOrder> array = getStatisticsArray(api,start,end);
            // Создаем рабочую книгу Эксель
            XSSFWorkbook workbook = new XSSFWorkbook();
            // Внешний вид и стиль таблицы
            CellStyle cellStyle = workbook.createCellStyle();
            CellStyle cellDataStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            cellDataStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));
            XSSFFont font = workbook.createFont();
            font.setBold(true);
            font.setFontName("Arial");
            font.setFontHeight(12);
            cellStyle.setFont(font);
            cellStyle.setBorderBottom(CellStyle.BORDER_MEDIUM);
            cellStyle.setWrapText(true);
            cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
            cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

            CellStyle simpleCellStyle = workbook.createCellStyle();
            Font simpleFont = workbook.createFont();
            simpleFont.setFontHeightInPoints((short) 11);
            simpleFont.setFontName("Arial");
            simpleCellStyle.setFont(simpleFont);
            simpleCellStyle.setAlignment(CellStyle.ALIGN_CENTER);

            XSSFCellStyle linkStyle = workbook.createCellStyle();
            XSSFFont linkFont = workbook.createFont();
            linkFont.setUnderline(XSSFFont.U_SINGLE);
            linkFont.setColor(HSSFColor.DARK_YELLOW.index);
            linkStyle.setFont(linkFont);
            // Создаем лист в книге
            XSSFSheet sheet = workbook.createSheet("Заказы и выкупы");

            //Создаем строку заголовков
            XSSFRow mainRow = sheet.createRow(0);
            //Создание заголовков в ячейках
            XSSFCell mainCell0 = mainRow.createCell(0);
            mainCell0.setCellValue("БРЕНД");
            mainCell0.setCellStyle(cellStyle);

            XSSFCell mainCell1 = mainRow.createCell(1);
            mainCell1.setCellValue("Предмет");
            mainCell1.setCellStyle(cellStyle);

            XSSFCell mainCell2 = mainRow.createCell(2);
            mainCell2.setCellValue("Артикул \nПоставщика");
            mainCell2.setCellStyle(cellStyle);

            XSSFCell mainCell3 = mainRow.createCell(3);
            mainCell3.setCellValue("Размер");
            mainCell3.setCellStyle(cellStyle);

            XSSFCell mainCell4 = mainRow.createCell(4);
            mainCell4.setCellValue("Баркод");
            mainCell4.setCellStyle(cellStyle);

            XSSFCell mainCell5 = mainRow.createCell(5);
            mainCell5.setCellValue("Артикул ВБ");
            mainCell5.setCellStyle(cellStyle);

            XSSFCell mainCell6 = mainRow.createCell(6);
            mainCell6.setCellValue("Кол-во \nзаказов");
            mainCell6.setCellStyle(cellStyle);

            XSSFCell mainCell7 = mainRow.createCell(7);
            mainCell7.setCellStyle(cellStyle);
            mainCell7.setCellValue("Кол-во\nвыкупов");

            XSSFCell mainCell8 = mainRow.createCell(8);
            mainCell8.setCellStyle(cellStyle);
            mainCell8.setCellValue("Сумма \nзаказов");

            XSSFCell mainCell9 = mainRow.createCell(9);
            mainCell9.setCellStyle(cellStyle);
            mainCell9.setCellValue("Сумма \nвыкупа");

            XSSFCell mainCell10 = mainRow.createCell(10);
            mainCell10.setCellStyle(cellStyle);
            mainCell10.setCellValue("Логистика");

            XSSFCell mainCell11 = mainRow.createCell(11);
            mainCell11.setCellStyle(cellStyle);
            mainCell11.setCellValue("Отменено \nЗАКАЗОВ");

            XSSFCell mainCell12 = mainRow.createCell(12);
            mainCell12.setCellStyle(cellStyle);
            mainCell12.setCellValue("Возвраты");

            XSSFCell mainCell13 = mainRow.createCell(13);
            mainCell13.setCellStyle(cellStyle);
            mainCell13.setCellValue("Сумма \nвозвратов");

            XSSFCell mainCell14 = mainRow.createCell(14);
            mainCell14.setCellStyle(cellStyle);
            mainCell14.setCellValue("% отмены\nзаказов");

            XSSFCell mainCell15 = mainRow.createCell(15);
            mainCell15.setCellStyle(cellStyle);
            mainCell15.setCellValue("Остаток \nнаскладах");

            XSSFCell mainCell16 = mainRow.createCell(16);
            mainCell16.setCellStyle(cellStyle);
            mainCell16.setCellValue("Товаров \nв пути");



            int cols = 17;
            int startRow = 1;
            int endRow = array.size()-1;



            array.sort(Comparator.comparing(SaleOrder::getOrdersCount));
            Collections.reverse(array);

            Font fontGreen = sheet.getWorkbook().createFont();
            fontGreen.setFontName("Arial");
            fontGreen.setFontHeightInPoints((short) 12);
            fontGreen.setColor(HSSFColor.GREEN.index);
            fontGreen.setBoldweight(Font.BOLDWEIGHT_BOLD);
            CellStyle greenStyle = workbook.createCellStyle();
            greenStyle.setFont(fontGreen);

            Font fontLtBlue = sheet.getWorkbook().createFont();
            fontLtBlue.setFontName("Arial");
            fontLtBlue.setFontHeightInPoints((short) 12);
            fontLtBlue.setColor(HSSFColor.LIGHT_BLUE.index);
            fontLtBlue.setBoldweight(Font.BOLDWEIGHT_BOLD);
            CellStyle LtBLueStyle = workbook.createCellStyle();
            LtBLueStyle.setFont(fontLtBlue);

            Font musthave = sheet.getWorkbook().createFont();
            musthave.setFontName("Arial");
            musthave.setFontHeightInPoints((short) 11);
            musthave.setColor(HSSFColor.GREEN.index);
            CellStyle mhStyle = sheet.getWorkbook().createCellStyle();
            mhStyle.setFont(musthave);

            Font kids = sheet.getWorkbook().createFont();
            kids.setColor(HSSFColor.CORAL.index);
            kids.setFontName("Arial");
            kids.setFontHeightInPoints((short) 11);
            CellStyle kidsStyle = sheet.getWorkbook().createCellStyle();
            kidsStyle.setFont(kids);

//            Font home = sheet.getWorkbook().createFont();
//            home.setColor(HSSFColor.BROWN.index);
//            home.setFontHeight((short) 11);
//            CellStyle homeStyle = sheet.getWorkbook().createCellStyle();
//            homeStyle.setFont(home);


            CellStyle stylePercentage = workbook.createCellStyle();
            stylePercentage.setFont(simpleFont);
            stylePercentage.setDataFormat(workbook.createDataFormat()
                    .getFormat(BuiltinFormats.getBuiltinFormat( 10 )));
            CellStyle styleMoney = workbook.createCellStyle();
            styleMoney.setDataFormat(workbook.createDataFormat()
                    .getFormat("#,##0.00 ₽;-#,##0.00 ₽"));

            CellStyle styleAttancion = workbook.createCellStyle();
            Font fontAttencion = sheet.getWorkbook().createFont();
            fontAttencion.setBoldweight(Font.BOLDWEIGHT_BOLD);
            fontAttencion.setColor(Font.COLOR_RED);
            fontAttencion.setFontHeightInPoints((short) 15);
            styleAttancion.setFont(fontAttencion);

            for(SaleOrder saleOrder : array){
                ArrayList<Stock> stocks = (ArrayList<Stock>) customStocksRepository.findAllByBarcode(saleOrder.getBarcode());
                XSSFRow row = sheet.createRow(startRow);

                XSSFCell cell0 = row.createCell(0);
                cell0.setCellValue(saleOrder.getBrand());
                cell0.setCellStyle(simpleCellStyle);


                XSSFCell cell1 = row.createCell(1);
                cell1.setCellValue(saleOrder.getSubject());
                cell1.setCellStyle(simpleCellStyle);

                XSSFCell cell2 = row.createCell(2);
                cell2.setCellValue(saleOrder.getSupplierArticle());
                cell2.setCellStyle(simpleCellStyle);

                XSSFCell cell3 = row.createCell(3);
                cell3.setCellValue(saleOrder.getTechSize());
                cell3.setCellStyle(simpleCellStyle);

                XSSFCell cell4 = row.createCell(4);
                cell4.setCellValue(saleOrder.getBarcode());
                cell4.setCellStyle(simpleCellStyle);

                switch (saleOrder.getBrand()){
                    case "MUSTHAVE" :
                        cell0.setCellStyle(mhStyle);
                        cell1.setCellStyle(mhStyle);
                        cell2.setCellStyle(mhStyle);
                        cell3.setCellStyle(mhStyle);
                        cell4.setCellStyle(mhStyle);
                        break;
                    case "MUSTHAVE KIDS":
                        cell0.setCellStyle(kidsStyle);
                        cell1.setCellStyle(kidsStyle);
                        cell2.setCellStyle(kidsStyle);
                        cell3.setCellStyle(kidsStyle);
                        cell4.setCellStyle(kidsStyle);
                        break;
//                    case "MUSTHAVE HOME":
//                        cell0.setCellStyle(homeStyle);
//                        cell1.setCellStyle(homeStyle);
//                        cell2.setCellStyle(homeStyle);
//                        cell3.setCellStyle(homeStyle);
//                        cell4.setCellStyle(homeStyle);
//                        break;
                    default:
                        cell0.setCellStyle(simpleCellStyle);
                        cell1.setCellStyle(simpleCellStyle);
                        cell2.setCellStyle(simpleCellStyle);
                        cell3.setCellStyle(simpleCellStyle);
                        cell4.setCellStyle(simpleCellStyle);
                        break;
                }

                XSSFCell cell5 = row.createCell(5);
                cell5.setCellValue(saleOrder.getNmId());
                XSSFHyperlink link1
                        = (XSSFHyperlink)createHelper.createHyperlink(Hyperlink.LINK_URL);
                link1.setAddress(saleOrder.getWbLink());
                cell5.setHyperlink(link1);
                cell5.setCellStyle(linkStyle);
                //Кол-во заказов
                XSSFCell cell6 = row.createCell(6);
                cell6.setCellValue(saleOrder.getOrdersCount());
                cell6.setCellType(Cell.CELL_TYPE_NUMERIC);
                cell6.setCellStyle(greenStyle);
                //Кол-во выкупов
                XSSFCell cell7 = row.createCell(7);
                cell7.setCellValue(saleOrder.getSaleCount());
                cell7.setCellType(Cell.CELL_TYPE_NUMERIC);
                cell7.setCellStyle(LtBLueStyle);
                //Сумма заказов
                XSSFCell cell8 = row.createCell(8);
                cell8.setCellValue(saleOrder.getTotalPrice().setScale(2,RoundingMode.HALF_UP).doubleValue());
                cell8.setCellType(Cell.CELL_TYPE_NUMERIC);
                cell8.setCellStyle(greenStyle);
                cell8.setCellStyle(styleMoney);
                //Сумма выкупов
                XSSFCell cell9 = row.createCell(9);
                cell9.setCellValue( saleOrder.getForPay().setScale(2,RoundingMode.HALF_UP).doubleValue());
                cell9.setCellType(Cell.CELL_TYPE_NUMERIC);
                cell9.setCellStyle(LtBLueStyle);
                cell9.setCellStyle(styleMoney);
                //Логистика
                XSSFCell cell10 = row.createCell(10);
                cell10.setCellValue( saleOrder.getTotalLogistic().setScale(2,RoundingMode.HALF_UP).doubleValue());
                cell10.setCellType(Cell.CELL_TYPE_NUMERIC);
                cell10.setCellStyle(styleMoney);
                //Отменено ЗАКАЗОВ
                XSSFCell cell11 = row.createCell(11);
                cell11.setCellValue(saleOrder.getOrderCancel());
                cell11.setCellType(Cell.CELL_TYPE_NUMERIC);
                cell11.setCellStyle(simpleCellStyle);
                //ВОЗВРАТЫ
                XSSFCell cell12 = row.createCell(12);
                cell12.setCellValue(saleOrder.getSaleCancel());
                cell12.setCellType(Cell.CELL_TYPE_NUMERIC);
                cell12.setCellStyle(simpleCellStyle);
                //Сумма возвратов
                XSSFCell cell13 = row.createCell(13);
                cell13.setCellValue(saleOrder.getCancelPay().setScale(2,RoundingMode.HALF_UP).doubleValue());
                cell13.setCellType(Cell.CELL_TYPE_NUMERIC);
                cell13.setCellStyle(styleMoney);
                cell13.setCellStyle(simpleCellStyle);
                if(saleOrder.getCancelPay().doubleValue() < 0){
                    cell13.setCellStyle(styleAttancion);
                }
                //% отмены заказов
                XSSFCell cell14 = row.createCell(14);
                cell14.setCellFormula( "L"+(startRow+1)+"/"+"G"+(startRow+1));
                cell14.setCellType(Cell.CELL_TYPE_FORMULA);
                cell14.setCellStyle(stylePercentage);
                //Остаток на складах
                XSSFCell cell15 = row.createCell(15);
                cell15.setCellValue(stockService.getQuantity(stocks));
                cell15.setCellType(Cell.CELL_TYPE_NUMERIC);
                cell15.setCellStyle(simpleCellStyle);
                if(stockService.getQuantity(stocks) <=5){
                    cell15.setCellStyle(styleAttancion);
                }
                //Товаров впути
                XSSFCell cell16 = row.createCell(16);
                cell16.setCellValue(stockService.getQuantityInWay(stocks));
                cell16.setCellType(Cell.CELL_TYPE_NUMERIC);
                cell16.setCellStyle(simpleCellStyle);
                startRow++;
            }
            for (int i = 0; i < cols;i++){
                sheet.autoSizeColumn(i);
            }


//            XSSFRow row = sheet.createRow(startRow);
//            Cell cell = row.createCell(6);
//            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
//            cell.setCellFormula("SUM(G2:"+ "G"+startRow+")");
//
//
//            Cell cell2 = row.createCell(7);
//            cell2.setCellType(Cell.CELL_TYPE_NUMERIC);
//            cell2.setCellFormula("SUM(H2:"+ "H"+startRow+")");
//
//            Cell cell3 = row.createCell(8);
//            cell3.setCellType(Cell.CELL_TYPE_NUMERIC);
//            cell3.setCellFormula("SUM(I2:"+ "I"+startRow+")");
//            cell3.setCellStyle(styleMoney);
//
//            Cell cell4 = row.createCell(9);
//            cell4.setCellType(Cell.CELL_TYPE_NUMERIC);
//            cell4.setCellFormula("SUM(J2:"+ "J"+startRow+")");
//            cell4.setCellStyle(styleMoney);
//
//            Cell cell5 = row.createCell(10);
//            cell5.setCellType(Cell.CELL_TYPE_NUMERIC);
//            cell5.setCellFormula("SUM(K2:"+ "K"+startRow+")");
//            cell5.setCellStyle(styleMoney);
//
//            Cell cell6 = row.createCell(11);
//            cell6.setCellType(Cell.CELL_TYPE_NUMERIC);
//            cell6.setCellFormula("SUM(L2:"+ "L"+startRow+")");
//
//            Cell cell7 = row.createCell(12);
//            cell7.setCellType(Cell.CELL_TYPE_NUMERIC);
//            cell7.setCellFormula("SUM(M2:"+ "M"+startRow+")");
//
//            Cell cell8 = row.createCell(13);
//            cell8.setCellType(Cell.CELL_TYPE_NUMERIC);
//            cell8.setCellFormula("SUM(N2:"+ "N"+startRow+")");
//            cell8.setCellStyle(styleMoney);
//
//            Cell cell9 = row.createCell(14);
//            cell9.setCellType(Cell.CELL_TYPE_NUMERIC);
//            cell9.setCellFormula("L"+(startRow+1)+"/G"+(startRow+1));
//            cell9.setCellStyle(stylePercentage);



            workbook.write(new FileOutputStream("test.xlsx"));
            return new File("test.xlsx");
        }

    }
