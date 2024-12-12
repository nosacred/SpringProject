package org.example;

import org.example.model.*;
import org.apache.poi.hssf.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.example.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    ComissionRepository comissionRepository;


    public ArrayList<SaleOrder> getStatisticsArray(String api, ZonedDateTime zdt1, ZonedDateTime zdt2) {
        ArrayList<SaleOrder> saleOrders = new ArrayList<>();
        ArrayList<Order> orders = (ArrayList<Order>) customOrderRepository.getOrderByApiKeyAndDateBetweenOrderByDate(api, zdt1, zdt2);
        ArrayList<Sale> sales = (ArrayList<Sale>) customSaleRepository.getSaleByApiKeyAndDateBetweenOrderByDate(api, zdt1, zdt2);
        HashSet<String> bcodes = new HashSet<>();
        for (Order order : orders) {
            bcodes.add(order.getBarcode());
        }
        for (Sale sale : sales) {
            bcodes.add(sale.getBarcode());
        }
        for (String s : bcodes) {
            SaleOrder saleOrder = new SaleOrder();
            saleOrder.setBarcode(s);
            if (orders.stream().anyMatch(order -> order.getBarcode().equals(s))) {
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
                    map(Order::getLogisticPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
            saleOrder.setForPay(sales.stream().filter(sale -> sale.getBarcode().equals(s)).map(Sale::getForPay).
                    filter(forPay -> forPay.doubleValue() > 0).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP));
            saleOrder.setCancelPay(sales.stream().filter(sale -> sale.getBarcode().equals(s)).map(Sale::getForPay).
                    filter(forPay -> forPay.doubleValue() < 0).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP));
            saleOrder.setSaleCancel((int) sales.stream().filter(order -> order.getBarcode().equals(s)).filter(order -> order.getForPay().doubleValue() < 0).count());
            saleOrder.setSaleCount((int) sales.stream().filter(order -> order.getBarcode().equals(s)).filter(order -> order.getForPay().doubleValue() > 0).count());

            saleOrder.setTotalPrice(orders.stream().filter(order -> order.getBarcode().equals(s)).
                    map(Order::getTotalPriceWithDisc).reduce(BigDecimal.ZERO, BigDecimal::add));
            saleOrders.add(saleOrder);
        }
        return saleOrders;
    }


    public File getExelFileSalesOrders(String api, ZonedDateTime start, ZonedDateTime end) throws IOException {

        ArrayList<SaleOrder> array = getStatisticsArray(api, start, end);
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
        XSSFSheet sheet = workbook.createSheet("Заказы и выкупы по всем складам");

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
        mainCell15.setCellValue("Остаток \nна складах");

        XSSFCell mainCell16 = mainRow.createCell(16);
        mainCell16.setCellStyle(cellStyle);
        mainCell16.setCellValue("Товаров \nК клиенту");

        XSSFCell mainCell17 = mainRow.createCell(17);
        mainCell17.setCellStyle(cellStyle);
        mainCell17.setCellValue("Товаров едет\nна склад");


        int cols = 18;
        int startRow = 1;
        int endRow = array.size() - 1;


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
                .getFormat(BuiltinFormats.getBuiltinFormat(10)));
        CellStyle styleMoney = workbook.createCellStyle();
        styleMoney.setDataFormat(workbook.createDataFormat()
                .getFormat("#,##0.00 ₽;-#,##0.00 ₽"));

        CellStyle styleAttancion = workbook.createCellStyle();
        Font fontAttencion = sheet.getWorkbook().createFont();
        fontAttencion.setBoldweight(Font.BOLDWEIGHT_BOLD);
        fontAttencion.setColor(Font.COLOR_RED);
        fontAttencion.setFontHeightInPoints((short) 15);
        styleAttancion.setFont(fontAttencion);

        for (SaleOrder saleOrder : array) {
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

            switch (saleOrder.getBrand()) {
                case "MUSTHAVE":
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
                    = (XSSFHyperlink) createHelper.createHyperlink(Hyperlink.LINK_URL);
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
            cell8.setCellValue(saleOrder.getTotalPrice().setScale(2, RoundingMode.HALF_UP).doubleValue());
            cell8.setCellType(Cell.CELL_TYPE_NUMERIC);
            cell8.setCellStyle(greenStyle);
            cell8.setCellStyle(styleMoney);
            //Сумма выкупов
            XSSFCell cell9 = row.createCell(9);
            cell9.setCellValue(saleOrder.getForPay().setScale(2, RoundingMode.HALF_UP).doubleValue());
            cell9.setCellType(Cell.CELL_TYPE_NUMERIC);
            cell9.setCellStyle(LtBLueStyle);
            cell9.setCellStyle(styleMoney);
            //Логистика
            XSSFCell cell10 = row.createCell(10);
            cell10.setCellValue(saleOrder.getTotalLogistic().setScale(2, RoundingMode.HALF_UP).doubleValue());
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
            cell13.setCellValue(saleOrder.getCancelPay().setScale(2, RoundingMode.HALF_UP).doubleValue());
            cell13.setCellType(Cell.CELL_TYPE_NUMERIC);
            cell13.setCellStyle(styleMoney);
            cell13.setCellStyle(simpleCellStyle);
            if (saleOrder.getCancelPay().doubleValue() < 0) {
                cell13.setCellStyle(styleAttancion);
            }
            //% отмены заказов
            XSSFCell cell14 = row.createCell(14);
            cell14.setCellFormula("L" + (startRow + 1) + "/" + "G" + (startRow + 1));
            cell14.setCellType(Cell.CELL_TYPE_FORMULA);
            cell14.setCellStyle(stylePercentage);
            //Остаток на складах
            XSSFCell cell15 = row.createCell(15);
            cell15.setCellValue(stockService.getQuantity(stocks));
            cell15.setCellType(Cell.CELL_TYPE_NUMERIC);
            cell15.setCellStyle(simpleCellStyle);
            if (stockService.getQuantity(stocks) <= 5) {
                cell15.setCellStyle(styleAttancion);
            }
            //Товаров впути к клиенту
            XSSFCell cell16 = row.createCell(16);
            cell16.setCellValue(stockService.getQuantityInWayToClientByBarcode(saleOrder.getBarcode()));
            cell16.setCellType(Cell.CELL_TYPE_NUMERIC);
            cell16.setCellStyle(simpleCellStyle);
            //Товаров впути jn клиенту
            XSSFCell cell17 = row.createCell(17);
            cell17.setCellValue(stockService.getQuantityInWayFromClientByBarcode(saleOrder.getBarcode()));
            cell17.setCellType(Cell.CELL_TYPE_NUMERIC);
            cell17.setCellStyle(simpleCellStyle);

            startRow++;
        }
        for (int i = 0; i < cols; i++) {
            sheet.autoSizeColumn(i);
        }

        //Создаем дополнительные страницы по каждому складу

        HashSet < String> warehouseSet = new HashSet<>();
        ArrayList<Order> orders = (ArrayList) customOrderRepository.getOrderByApiKeyAndDateBetweenOrderByDate(api, start, end);
        System.out.println("Кол-во заказов "+ orders.size());
        HashSet<String> barcodes = new HashSet<>();
        for(Order order : orders){
            warehouseSet.add(order.getWarehouseName());
            barcodes.add(order.getBarcode());
        }


        //Создание заголовков в ячейках

        ArrayList<Stock> stockArrayList = (ArrayList<Stock>) customStocksRepository.findAllByApiKey(api);
        System.out.println(stockArrayList.size());



        for(String wh : warehouseSet) {
            XSSFSheet whSheet = workbook.createSheet(wh);
            System.out.println(wh);

//            ArrayList<Order> whOrders = (ArrayList<Order>) orders.stream().filter(o-> o.getWarehouseName().equals(wh)).collect(Collectors.toList());
            //Создаем строку заголовков
            XSSFRow mainRow0 = whSheet.createRow(0);
            String[] colNames = new String[]{
                    "БРЕНД", //0
                    "Предмет",//1
                    "Артикул\nПоставщика",//2
                    "Размер",//3
                    "Баркод",//4
                    "Артикул ВБ",//5
                    "ЗАКАЗАНО",//6
                    "Остаток",//7
                    "В пути\nК клиенту",//8
                    "В пути\n на склад ",//9
                    "Последнее\nобновление\nостатков"};//10
            int colNums = colNames.length;

            for (int c = 0; c < colNums; c++) {
                XSSFCell mainCell = mainRow0.createCell(c);
                mainCell.setCellValue(colNames[c]);
                mainCell.setCellStyle(cellStyle);
            }

            int rowNum = 1;

            for (String barcode : barcodes){
                XSSFRow row = whSheet.createRow(rowNum);
                ArrayList<Stock> stocks = (ArrayList<Stock>) customStocksRepository.findAllByBarcode(barcode);
                if(orders.stream().filter(order -> order.getBarcode().equals(barcode)).
                        filter(o1-> o1.getWarehouseName().equals(wh)).count() <1 &&
                !stocks.stream().anyMatch(stock -> stock.getWarehouseName().equals(wh))){
                    continue;
                }

                for (int c = 0; c < colNums; c++) {
                    XSSFCell cell = row.createCell(c);
                    switch (c) {
                        case 0: { //бренд
                            cell.setCellValue(orders.stream().filter(order -> order.getBarcode().equals(barcode)).findAny().get().getBrand());
                            cell.setCellStyle(simpleCellStyle);
                            break;
                        }
                        case 1: { //предмет
                            cell.setCellValue(orders.stream().filter(order -> order.getBarcode().equals(barcode)).findAny().get().getSubject());
                            cell.setCellStyle(simpleCellStyle);
                            break;
                        }
                        case 2: { // артикул продавца
                            cell.setCellValue(orders.stream().filter(order -> order.getBarcode().equals(barcode)).findAny().get().getSupplierArticle());
                            cell.setCellStyle(simpleCellStyle);
                            break;
                        }
                        case 3: { // размер
                            cell.setCellValue(orders.stream().filter(order -> order.getBarcode().equals(barcode)).findFirst().get().getTechSize());
                            cell.setCellStyle(simpleCellStyle);
                            break;
                        }
                        case 4: { // баркод
                            cell.setCellValue(orders.stream().filter(order -> order.getBarcode().equals(barcode)).findFirst().get().getBarcode());
                            cell.setCellStyle(simpleCellStyle);
                            break;
                        }
                        case 5: { //артикул вб + линк
                            cell.setCellValue(orders.stream().filter(order -> order.getBarcode().equals(barcode)).findFirst().get().getNmId());
                            XSSFHyperlink link1
                                    = (XSSFHyperlink) createHelper.createHyperlink(Hyperlink.LINK_URL);
                            link1.setAddress(orders.stream().filter(order -> order.getBarcode().equals(barcode)).findFirst().get().getWBLink());
                            cell.setHyperlink(link1);
                            cell.setCellStyle(linkStyle);
                            break;
                        }
                        case 6: { //Заказы
                            cell.setCellValue(orders.stream().filter(order -> order.getBarcode().equals(barcode)).
                                    filter(order -> order.getWarehouseName().equals(wh)).count());
                            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                            cell.setCellStyle(simpleCellStyle);
                            break;
                        }
                        case 7: { //остаток
                            int i = stocks.stream().filter(stock -> stock.getWarehouseName().equals(wh)).findFirst().isPresent() ?
                                    stocks.stream().filter(stock -> stock.getWarehouseName().equals(wh)).findFirst().get().getQuantity() : 0;
                            cell.setCellValue(i);
                            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                            cell.setCellStyle(simpleCellStyle);
                            break;
                        }
                        case 8: { // в пути к клиенту
                            int i = stocks.stream().filter(stock -> stock.getWarehouseName().equals(wh)).findFirst().isPresent() ?
                                    stocks.stream().filter(stock -> stock.getWarehouseName().equals(wh)).findFirst().get().getInWayToClient() : 0;
                            cell.setCellValue(i);
                            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                            cell.setCellStyle(simpleCellStyle);
                            break;
                        }
                        case 9: { // в пути на склад
                            int i = stocks.stream().filter(stock -> stock.getWarehouseName().equals(wh)).findFirst().isPresent() ?
                                    stocks.stream().filter(stock -> stock.getWarehouseName().equals(wh)).findFirst().get().getInWayFromClient() : 0;
                            cell.setCellValue(i);
                            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                            cell.setCellStyle(simpleCellStyle);
                            break;
                        }
                        case 10: { // дата последнего обновления остатков
                            if(stocks.stream().filter(stock -> stock.getWarehouseName().equals(wh)).findFirst().isPresent() ) {
                                cell.setCellValue(stocks.stream().filter(stock -> stock.getWarehouseName().equals(wh)).findFirst().get().
                                        getLastChangeDateT().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                                cell.setCellStyle(cellDataStyle);
                            }
                            break;
                        }
                    }
                }
                rowNum++;
            }
            for (int col = 0; col <= colNums; col++) {
                whSheet.autoSizeColumn(col);
            }
        }

        workbook.write(new FileOutputStream("test.xlsx"));
        return new File("test.xlsx");
    }

    public File getExelFileOrdersByDay(String api, ZonedDateTime start, ZonedDateTime end) throws IOException {
        ArrayList<Order> orders = (ArrayList<Order>) customOrderRepository.getOrderByApiKeyAndDateBetweenOrderByDate(api, start, end);
        HashSet<String> barcodes = orders.stream().map(Order::getBarcode).collect(Collectors.toCollection(HashSet::new));

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
        XSSFSheet sheet0 = workbook.createSheet("Заказы по дням недели");

        //Создаем строку заголовков
        XSSFRow mainRow0 = sheet0.createRow(0);
        //Создание заголовков в ячейках
        int colNums = 0;
        XSSFCell mainCell0 = mainRow0.createCell(colNums);
        mainCell0.setCellValue("БРЕНД");
        mainCell0.setCellStyle(cellStyle);
        colNums++;
        XSSFCell mainCell1 = mainRow0.createCell(colNums);
        mainCell1.setCellValue("Предмет");
        mainCell1.setCellStyle(cellStyle);
        colNums++;

        XSSFCell mainCell2 = mainRow0.createCell(colNums);
        mainCell2.setCellValue("Артикул \nПоставщика");
        mainCell2.setCellStyle(cellStyle);
        colNums++;

        XSSFCell mainCell3 = mainRow0.createCell(colNums);
        mainCell3.setCellValue("Размер");
        mainCell3.setCellStyle(cellStyle);
        colNums++;

        XSSFCell mainCell4 = mainRow0.createCell(colNums);
        mainCell4.setCellValue("Баркод");
        mainCell4.setCellStyle(cellStyle);
        colNums++;

        XSSFCell mainCell5 = mainRow0.createCell(colNums);
        mainCell5.setCellValue("Артикул ВБ");
        mainCell5.setCellStyle(cellStyle);
        colNums++;

        XSSFCell mainCell6 = mainRow0.createCell(colNums);
        mainCell6.setCellValue("Пн");
        mainCell6.setCellStyle(cellStyle);
        colNums++;

        XSSFCell mainCell7 = mainRow0.createCell(colNums);
        mainCell7.setCellValue("Вт");
        mainCell7.setCellStyle(cellStyle);
        colNums++;

        XSSFCell mainCell8 = mainRow0.createCell(colNums);
        mainCell8.setCellValue("Ср");
        mainCell8.setCellStyle(cellStyle);
        colNums++;

        XSSFCell mainCell9 = mainRow0.createCell(colNums);
        mainCell9.setCellValue("Чт");
        mainCell9.setCellStyle(cellStyle);
        colNums++;

        XSSFCell mainCell10 = mainRow0.createCell(colNums);
        mainCell10.setCellValue("Пт");
        mainCell10.setCellStyle(cellStyle);
        colNums++;

        XSSFCell mainCell11 = mainRow0.createCell(colNums);
        mainCell11.setCellValue("Сб");
        mainCell11.setCellStyle(cellStyle);
        colNums++;

        XSSFCell mainCell12 = mainRow0.createCell(colNums);
        mainCell12.setCellValue("Вскр");
        mainCell12.setCellStyle(cellStyle);
        colNums++;

        XSSFCell mainCell13 = mainRow0.createCell(colNums);
        mainCell13.setCellValue("Общее кол-во\nзаказов");
        mainCell13.setCellStyle(cellStyle);

        int startRow = 1;
        for (String bcode : barcodes) {
            XSSFRow row = sheet0.createRow(startRow);
            for (int i = 0; i <= colNums; i++) {
                XSSFCell cell = row.createCell(i);

                switch (i) {
                    case 0: {
                        cell.setCellValue(orders.stream().filter(order -> order.getBarcode().equals(bcode)).findFirst().get().getBrand());
                        break;
                    }
                    case 1: {
                        cell.setCellValue(orders.stream().filter(order -> order.getBarcode().equals(bcode)).findFirst().get().getSubject());
                        break;
                    }
                    case 2: {
                        cell.setCellValue(orders.stream().filter(order -> order.getBarcode().equals(bcode)).findFirst().get().getSupplierArticle());
                        break;
                    }
                    case 3: {
                        cell.setCellValue(orders.stream().filter(order -> order.getBarcode().equals(bcode)).findFirst().get().getTechSize());
                        break;
                    }
                    case 4: {
                        cell.setCellValue(orders.stream().filter(order -> order.getBarcode().equals(bcode)).findFirst().get().getBarcode());
                        break;
                    }
                    case 5: {
                        cell.setCellValue(orders.stream().filter(order -> order.getBarcode().equals(bcode)).findFirst().get().getNmId());
                        XSSFHyperlink link1
                                = (XSSFHyperlink) createHelper.createHyperlink(Hyperlink.LINK_URL);
                        link1.setAddress(orders.stream().filter(order -> order.getBarcode().equals(bcode)).findFirst().get().getWBLink());
                        cell.setHyperlink(link1);
                        cell.setCellStyle(linkStyle);
                        break;
                    }
                    case 6: {
                        ArrayList<Order> orderArrayList = ((ArrayList<Order>) orders.stream().filter(order -> order.getBarcode().equals(bcode)).collect(Collectors.toList()));
                        cell.setCellValue(orderArrayList.stream().filter(order -> order.getDate().getDayOfWeek().getValue() == 1).count());
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        break;
                    }
                    case 7: {
                        ArrayList<Order> orderArrayList = ((ArrayList<Order>) orders.stream().filter(order -> order.getBarcode().equals(bcode)).collect(Collectors.toList()));
                        cell.setCellValue(orderArrayList.stream().filter(order -> order.getDate().getDayOfWeek().getValue() == 2).count());
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        break;
                    }
                    case 8: {
                        ArrayList<Order> orderArrayList = ((ArrayList<Order>) orders.stream().filter(order -> order.getBarcode().equals(bcode)).collect(Collectors.toList()));
                        cell.setCellValue(orderArrayList.stream().filter(order -> order.getDate().getDayOfWeek().getValue() == 3).count());
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        break;
                    }
                    case 9: {
                        ArrayList<Order> orderArrayList = ((ArrayList<Order>) orders.stream().filter(order -> order.getBarcode().equals(bcode)).collect(Collectors.toList()));
                        cell.setCellValue(orderArrayList.stream().filter(order -> order.getDate().getDayOfWeek().getValue() == 4).count());
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        break;
                    }
                    case 10: {
                        ArrayList<Order> orderArrayList = ((ArrayList<Order>) orders.stream().filter(order -> order.getBarcode().equals(bcode)).collect(Collectors.toList()));
                        cell.setCellValue(orderArrayList.stream().filter(order -> order.getDate().getDayOfWeek().getValue() == 5).count());
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        break;
                    }
                    case 11: {
                        ArrayList<Order> orderArrayList = ((ArrayList<Order>) orders.stream().filter(order -> order.getBarcode().equals(bcode)).collect(Collectors.toList()));
                        cell.setCellValue(orderArrayList.stream().filter(order -> order.getDate().getDayOfWeek().getValue() == 6).count());
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        break;
                    }
                    case 12: {
                        ArrayList<Order> orderArrayList = ((ArrayList<Order>) orders.stream().filter(order -> order.getBarcode().equals(bcode)).collect(Collectors.toList()));
                        cell.setCellValue(orderArrayList.stream().filter(order -> order.getDate().getDayOfWeek().getValue() == 7).count());
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        break;
                    }
                    case 13: {
                        cell.setCellValue(orders.stream().filter(order -> order.getBarcode().equals(bcode)).count());
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        break;
                    }
                }
            }
            startRow++;
        }
        for (int i = 0; i <= colNums; i++) {
            sheet0.autoSizeColumn(i);
        }


        //Создаем второйлист

        XSSFSheet sheet1 = workbook.createSheet("Заказы по времени");

        //Создаем строку заголовков
        XSSFRow mainRow = sheet1.createRow(0);
        //Создание заголовков в ячейках
        int colNum = 0;
        XSSFCell mainCellSheet0 = mainRow.createCell(colNum);
        mainCellSheet0.setCellValue("БРЕНД");
        mainCellSheet0.setCellStyle(cellStyle);
        colNum++;
        XSSFCell mainCellSheet1 = mainRow.createCell(colNum);
        mainCellSheet1.setCellValue("Предмет");
        mainCellSheet1.setCellStyle(cellStyle);
        colNum++;

        XSSFCell mainCellSheet2 = mainRow.createCell(colNum);
        mainCellSheet2.setCellValue("Артикул \nПоставщика");
        mainCellSheet2.setCellStyle(cellStyle);
        colNum++;

        XSSFCell mainCellSheet3 = mainRow.createCell(colNum);
        mainCellSheet3.setCellValue("Размер");
        mainCellSheet3.setCellStyle(cellStyle);
        colNum++;

        XSSFCell mainCellSheet4 = mainRow.createCell(colNum);
        mainCellSheet4.setCellValue("Баркод");
        mainCellSheet4.setCellStyle(cellStyle);
        colNum++;

        XSSFCell mainCellSheet5 = mainRow.createCell(colNum);
        mainCellSheet5.setCellValue("Артикул ВБ");
        mainCellSheet5.setCellStyle(cellStyle);
        colNum++;

        XSSFCell mainCellSheet6 = mainRow.createCell(colNum);
        mainCellSheet6.setCellValue("с 9-00 \nдо 12-00");
        mainCellSheet6.setCellStyle(cellStyle);
        colNum++;

        XSSFCell mainCellSheet7 = mainRow.createCell(colNum);
        mainCellSheet7.setCellValue("с 12-00 \nдо 15-00");
        mainCellSheet7.setCellStyle(cellStyle);
        colNum++;

        XSSFCell mainCellSheet8 = mainRow.createCell(colNum);
        mainCellSheet8.setCellValue("с 15-00 \nдо 18-00");
        mainCellSheet8.setCellStyle(cellStyle);
        colNum++;

        XSSFCell mainCellSheet9 = mainRow.createCell(colNum);
        mainCellSheet9.setCellValue("с 18-00 \nдо 21-00");
        mainCellSheet9.setCellStyle(cellStyle);
        colNum++;

        XSSFCell mainCellSheet10 = mainRow.createCell(colNum);
        mainCellSheet10.setCellValue("с 21-00 \nдо 00");
        mainCellSheet10.setCellStyle(cellStyle);
        colNum++;

        XSSFCell mainCellSheet11 = mainRow.createCell(colNum);
        mainCellSheet11.setCellValue("С 00-00 \nдо 3-00");
        mainCellSheet11.setCellStyle(cellStyle);
        colNum++;

        XSSFCell mainCellSheet12 = mainRow.createCell(colNum);
        mainCellSheet12.setCellValue("с 3-00 \nдо 6-00");
        mainCellSheet12.setCellStyle(cellStyle);
        colNum++;

        XSSFCell mainCellSheet13 = mainRow.createCell(colNum);
        mainCellSheet13.setCellValue("с 6-00 \nдо 9-00");
        mainCellSheet13.setCellStyle(cellStyle);
        colNum++;

        XSSFCell mainCellSheet14 = mainRow.createCell(colNum);
        mainCellSheet14.setCellValue("Общее кол-во\nзаказов");
        mainCellSheet14.setCellStyle(cellStyle);

        int startRowSheet1 = 1;
        for (String bcode : barcodes) {
            XSSFRow row = sheet1.createRow(startRowSheet1);
            for (int i = 0; i <= colNum; i++) {
                XSSFCell cell = row.createCell(i);

                switch (i) {
                    case 0: {
                        cell.setCellValue(orders.stream().filter(order -> order.getBarcode().equals(bcode)).findFirst().get().getBrand());
                        break;
                    }
                    case 1: {
                        cell.setCellValue(orders.stream().filter(order -> order.getBarcode().equals(bcode)).findFirst().get().getSubject());
                        break;
                    }
                    case 2: {
                        cell.setCellValue(orders.stream().filter(order -> order.getBarcode().equals(bcode)).findFirst().get().getSupplierArticle());
                        break;
                    }
                    case 3: {
                        cell.setCellValue(orders.stream().filter(order -> order.getBarcode().equals(bcode)).findFirst().get().getTechSize());
                        break;
                    }
                    case 4: {
                        cell.setCellValue(orders.stream().filter(order -> order.getBarcode().equals(bcode)).findFirst().get().getBarcode());
                        break;
                    }
                    case 5: {
                        cell.setCellValue(orders.stream().filter(order -> order.getBarcode().equals(bcode)).findFirst().get().getNmId());
                        XSSFHyperlink link1
                                = (XSSFHyperlink) createHelper.createHyperlink(Hyperlink.LINK_URL);
                        link1.setAddress(orders.stream().filter(order -> order.getBarcode().equals(bcode)).findFirst().get().getWBLink());
                        cell.setHyperlink(link1);
                        cell.setCellStyle(linkStyle);
                        break;
                    }
                    case 6: {
                        ArrayList<Order> orderArrayList = ((ArrayList<Order>) orders.stream().filter(order -> order.getBarcode().equals(bcode)).collect(Collectors.toList()));
                        cell.setCellValue(orderArrayList.stream().filter(order -> order.getDate().getHour() >= 9 && order.getDate().getHour() < 12).count());
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        break;
                    }
                    case 7: {
                        ArrayList<Order> orderArrayList = ((ArrayList<Order>) orders.stream().filter(order -> order.getBarcode().equals(bcode)).collect(Collectors.toList()));
                        cell.setCellValue(orderArrayList.stream().filter(order -> order.getDate().getHour() >= 12 && order.getDate().getHour() < 15).count());
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        break;
                    }
                    case 8: {
                        ArrayList<Order> orderArrayList = ((ArrayList<Order>) orders.stream().filter(order -> order.getBarcode().equals(bcode)).collect(Collectors.toList()));
                        cell.setCellValue(orderArrayList.stream().filter(order -> order.getDate().getHour() >= 15 && order.getDate().getHour() < 18).count());
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        break;
                    }
                    case 9: {
                        ArrayList<Order> orderArrayList = ((ArrayList<Order>) orders.stream().filter(order -> order.getBarcode().equals(bcode)).collect(Collectors.toList()));
                        cell.setCellValue(orderArrayList.stream().filter(order -> order.getDate().getHour() >= 18 && order.getDate().getHour() < 21).count());
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        break;
                    }
                    case 10: {
                        ArrayList<Order> orderArrayList = ((ArrayList<Order>) orders.stream().filter(order -> order.getBarcode().equals(bcode)).collect(Collectors.toList()));
                        cell.setCellValue(orderArrayList.stream().filter(order -> order.getDate().getHour() >= 21 && order.getDate().getHour() < 24).count());
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        break;
                    }
                    case 11: {
                        ArrayList<Order> orderArrayList = ((ArrayList<Order>) orders.stream().filter(order -> order.getBarcode().equals(bcode)).collect(Collectors.toList()));
                        cell.setCellValue(orderArrayList.stream().filter(order -> order.getDate().getHour() >= 0 && order.getDate().getHour() < 3).count());
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        break;
                    }
                    case 12: {
                        ArrayList<Order> orderArrayList = ((ArrayList<Order>) orders.stream().filter(order -> order.getBarcode().equals(bcode)).collect(Collectors.toList()));
                        cell.setCellValue(orderArrayList.stream().filter(order -> order.getDate().getHour() >= 3 && order.getDate().getHour() < 6).count());
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        break;
                    }
                    case 13: {
                        ArrayList<Order> orderArrayList = ((ArrayList<Order>) orders.stream().filter(order -> order.getBarcode().equals(bcode)).collect(Collectors.toList()));
                        cell.setCellValue(orderArrayList.stream().filter(order -> order.getDate().getHour() >= 6 && order.getDate().getHour() < 9).count());
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        break;
                    }

                    case 14: {
                        cell.setCellValue(orders.stream().filter(order -> order.getBarcode().equals(bcode)).count());
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        break;
                    }
                }
            }
            startRowSheet1++;
        }
        for (int i = 0; i <= colNum; i++) {
            sheet1.autoSizeColumn(i);
        }

        //Создаем 3й лист с заказами по датам

        XSSFSheet sheet2 = workbook.createSheet("Заказы по датам");

        //Создаем строку заголовков
        XSSFRow mainRowDays = sheet2.createRow(0);
        //Создание заголовков в ячейках
        int colNumDays = 0;
        XSSFCell mainCellSheetDays0 = mainRowDays.createCell(colNumDays);
        mainCellSheetDays0.setCellValue("БРЕНД");
        mainCellSheetDays0.setCellStyle(cellStyle);
        colNumDays++;
        XSSFCell mainCellSheetDays1 = mainRowDays.createCell(colNumDays);
        mainCellSheetDays1.setCellValue("Предмет");
        mainCellSheetDays1.setCellStyle(cellStyle);
        colNumDays++;

        XSSFCell mainCellSheetDays2 = mainRowDays.createCell(colNumDays);
        mainCellSheetDays2.setCellValue("Артикул \nПоставщика");
        mainCellSheetDays2.setCellStyle(cellStyle);
        colNumDays++;

        XSSFCell mainCellSheetDays3 = mainRowDays.createCell(colNumDays);
        mainCellSheetDays3.setCellValue("Размер");
        mainCellSheetDays3.setCellStyle(cellStyle);
        colNumDays++;

        XSSFCell mainCellSheetDays4 = mainRowDays.createCell(colNumDays);
        mainCellSheetDays4.setCellValue("Баркод");
        mainCellSheetDays4.setCellStyle(cellStyle);
        colNumDays++;

        XSSFCell mainCellSheetDays5 = mainRowDays.createCell(colNumDays);
        mainCellSheetDays5.setCellValue("Артикул ВБ");
        mainCellSheetDays5.setCellStyle(cellStyle);
        colNumDays++;

        XSSFCell mainCellSheetDays6 = mainRowDays.createCell(colNumDays);
        mainCellSheetDays6.setCellValue("Всего\nзаказов");
        mainCellSheetDays6.setCellStyle(cellStyle);
        colNumDays++;

        for (ZonedDateTime znd = start; znd.isBefore(end.plusDays(1)); ) {
            XSSFCell mainCellSheetDays = mainRowDays.createCell(colNumDays);
            mainCellSheetDays.setCellValue(znd.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "\n" + znd.getDayOfWeek());
            mainCellSheetDays.setCellStyle(cellStyle);
            colNumDays++;
            znd = znd.plusDays(1);
        }

        int startRowSheet2 = 1;
        for (String bcode : barcodes) {
            XSSFRow row = sheet2.createRow(startRowSheet2);
            for (int i = 0; i <= 6; i++) {
                XSSFCell cell = row.createCell(i);

                switch (i) {
                    case 0: {
                        cell.setCellValue(orders.stream().filter(order -> order.getBarcode().equals(bcode)).findFirst().get().getBrand());
                        break;
                    }
                    case 1: {
                        cell.setCellValue(orders.stream().filter(order -> order.getBarcode().equals(bcode)).findFirst().get().getSubject());
                        break;
                    }
                    case 2: {
                        cell.setCellValue(orders.stream().filter(order -> order.getBarcode().equals(bcode)).findFirst().get().getSupplierArticle());
                        break;
                    }
                    case 3: {
                        cell.setCellValue(orders.stream().filter(order -> order.getBarcode().equals(bcode)).findFirst().get().getTechSize());
                        break;
                    }
                    case 4: {
                        cell.setCellValue(orders.stream().filter(order -> order.getBarcode().equals(bcode)).findFirst().get().getBarcode());
                        break;
                    }
                    case 5: {
                        cell.setCellValue(orders.stream().filter(order -> order.getBarcode().equals(bcode)).findFirst().get().getNmId());
                        XSSFHyperlink link1
                                = (XSSFHyperlink) createHelper.createHyperlink(Hyperlink.LINK_URL);
                        link1.setAddress(orders.stream().filter(order -> order.getBarcode().equals(bcode)).findFirst().get().getWBLink());
                        cell.setHyperlink(link1);
                        cell.setCellStyle(linkStyle);
                        break;
                    }
                    case 6: {
                        cell.setCellValue(orders.stream().filter(order -> order.getBarcode().equals(bcode)).count());
                        break;
                    }
                }
            }
            ArrayList<Order> orderArrayList = ((ArrayList<Order>) orders.stream().filter(order -> order.getBarcode().equals(bcode)).collect(Collectors.toList()));
            int i = 7;
            for (ZonedDateTime znd = start; znd.isBefore(end); ) {
                ZonedDateTime finalZnd = znd;
                XSSFCell cell = row.createCell(i);
                cell.setCellValue(orderArrayList.stream().filter(order -> order.getDate().getDayOfYear() == finalZnd.getDayOfYear()).count());
                i++;
                znd = znd.plusDays(1);
            }
            startRowSheet2++;

        }
        workbook.write(new FileOutputStream("test1.xlsx"));
        return new File("test1.xlsx");

    }

    public File getExelFileStocks(String api) throws IOException {

        ZonedDateTime today = ZonedDateTime.now().withHour(23).withMinute(59).withSecond(59);
        ArrayList<Order> orderArrayList = (ArrayList<Order>) customOrderRepository.getOrderByApiKeyAndDateBetweenOrderByDate(api, today.minusDays(14).withHour(0).withMinute(0).withSecond(0), today);
//        HashSet<String> barcodes = orderArrayList.stream().map(Order::getBarcode).collect(Collectors.toCollection(HashSet::new));

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

        CellStyle stylePercentage = workbook.createCellStyle();
        stylePercentage.setFont(simpleFont);
        stylePercentage.setDataFormat(workbook.createDataFormat()
                .getFormat(BuiltinFormats.getBuiltinFormat(10)));
        CellStyle styleMoney = workbook.createCellStyle();
        styleMoney.setDataFormat(workbook.createDataFormat()
                .getFormat("#,##0.00 ₽;-#,##0.00 ₽"));
        // Создаем лист в книге
        XSSFSheet sheet0 = workbook.createSheet("Остатки");

        //Создаем строку заголовков
        XSSFRow mainRow0 = sheet0.createRow(0);
        //Создание заголовков в ячейках

        ArrayList<Stock> stockArrayList = (ArrayList<Stock>) customStocksRepository.findAllByApiKey(api);
        stockArrayList.sort(Comparator.comparing(Stock::getBarcode));
        HashSet<String> barcodes = stockArrayList.stream().map(Stock::getBarcode).collect(Collectors.toCollection(HashSet::new));
        String[] colNames = new String[]{
                "БРЕНД",
                "Предмет",
                "Артикул\nПоставщика",
                "Размер",
                "Баркод",
                "Артикул ВБ",
                "РРЦ",
                "Скидка",
                "Цена \nсо скидкой",
//                "Склад",
                "Остаток",
                "В пути\nК клиенту",
                "В пути\nОТ клиента ",
                "Последнее\nобновление\nостатков"};
        int colNums = colNames.length;

        for (int c = 0; c < colNums; c++) {
            XSSFCell mainCell = mainRow0.createCell(c);
            mainCell.setCellValue(colNames[c]);
            mainCell.setCellStyle(cellStyle);
        }



        int rowNum=1;

        for (String barcode : barcodes){
            XSSFRow row = sheet0.createRow(rowNum);

            for (int c = 0; c < colNums; c++) {
                XSSFCell cell = row.createCell(c);
                switch (c) {
                    case 0: {
                        cell.setCellValue(stockArrayList.stream().filter(stock -> stock.getBarcode().equals(barcode)).findFirst().get().getBrand());
                        cell.setCellStyle(simpleCellStyle);
                        break;
                    }
                    case 1: {
                        cell.setCellValue(stockArrayList.stream().filter(stock -> stock.getBarcode().equals(barcode)).findFirst().get().getSubject());
                        cell.setCellStyle(simpleCellStyle);
                        break;
                    }
                    case 2: {
                        cell.setCellValue(stockArrayList.stream().filter(stock -> stock.getBarcode().equals(barcode)).findFirst().get().getSupplierArticle());
                        cell.setCellStyle(simpleCellStyle);
                        break;
                    }
                    case 3: {
                        cell.setCellValue(stockArrayList.stream().filter(stock -> stock.getBarcode().equals(barcode)).findFirst().get().getTechSize());
                        cell.setCellStyle(simpleCellStyle);
                        break;
                    }
                    case 4: {
                        cell.setCellValue(stockArrayList.stream().filter(stock -> stock.getBarcode().equals(barcode)).findFirst().get().getBarcode());
                        cell.setCellStyle(simpleCellStyle);
                        break;
                    }
                    case 5: {
                        cell.setCellValue(stockArrayList.stream().filter(stock -> stock.getBarcode().equals(barcode)).findFirst().get().getNmId());
                        XSSFHyperlink link1
                                = (XSSFHyperlink) createHelper.createHyperlink(Hyperlink.LINK_URL);
                        link1.setAddress(stockArrayList.stream().filter(stock -> stock.getBarcode().equals(barcode)).findFirst().get().getWBLink());
                        cell.setHyperlink(link1);
                        cell.setCellStyle(linkStyle);
                        break;
                    }
                    case 6: {
                        cell.setCellValue(stockArrayList.stream().filter(stock -> stock.getBarcode().equals(barcode)).findFirst().get().getPrice().doubleValue());
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        cell.setCellStyle(styleMoney);
                        break;
                    }
                    case 7: {
                        cell.setCellValue(stockArrayList.stream().filter(stock -> stock.getBarcode().equals(barcode)).findFirst().get().getDiscount());
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        cell.setCellStyle(simpleCellStyle);
                        break;
                    }
                    case 8: {
                        cell.setCellValue(stockArrayList.stream().filter(stock -> stock.getBarcode().equals(barcode)).findFirst().get().getTotalPriceWithDisc().doubleValue());
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        cell.setCellStyle(styleMoney);
                        break;
                    }
//                    case 9: {
//                        cell.setCellValue(stockArrayList.get(i - 1).getWarehouseName());
//                        cell.setCellStyle(simpleCellStyle);
//                        break;
//                    }
                    case 9: {
                        cell.setCellValue(stockArrayList.stream().filter(stock -> stock.getBarcode().equals(barcode)).map(Stock::getQuantity).reduce(0,Integer::sum));
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        cell.setCellStyle(simpleCellStyle);
                        break;
                    }
                    case 10: {
                        cell.setCellValue(stockArrayList.stream().filter(stock -> stock.getBarcode().equals(barcode)).map(Stock::getInWayToClient).reduce(0,Integer::sum));
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        cell.setCellStyle(simpleCellStyle);
                        break;
                    }
                    case 11: {
                        cell.setCellValue(stockArrayList.stream().filter(stock -> stock.getBarcode().equals(barcode)).map(Stock::getInWayFromClient).reduce(0,Integer::sum));
                        cell.setCellStyle(simpleCellStyle);
                        break;
                    }
                    case 12: {

                        cell.setCellValue(stockArrayList.stream().filter(stock -> stock.getBarcode().equals(barcode)).
                                map(Stock::getLastChangeDateT).
                                max(LocalDateTime::compareTo).
                                get().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
                        cell.setCellStyle(cellDataStyle);
                        break;
                    }
                }
            }
            rowNum++;
        }
            for (int col = 0; col <= colNums; col++) {
                sheet0.autoSizeColumn(col);
            }


        workbook.write(new FileOutputStream("stock.xlsx"));
        return new File("stock.xlsx");
    }

    public File getExelUnit(String api) throws IOException{

        // Создаем рабочую книгу Эксель
        XSSFWorkbook workbook = new XSSFWorkbook();
        // Внешний вид и стиль таблицы
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
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

        CellStyle styleMoney = workbook.createCellStyle();
        styleMoney.setDataFormat(workbook.createDataFormat()
                .getFormat("#,##0.00 ₽;-#,##0.00 ₽"));

        CellStyle stylePercentage = workbook.createCellStyle();
        stylePercentage.setFont(simpleFont);
        stylePercentage.setDataFormat(workbook.createDataFormat()
                .getFormat(BuiltinFormats.getBuiltinFormat(10)));
        // Создаем лист в книге
        XSSFSheet sheet = workbook.createSheet("Рассчет юнит-экономики");

        //Создаем строку заголовков
        XSSFRow mainRow = sheet.createRow(0);
        //Создание заголовков в ячейках

        String[] colNames = new String[]{
                "БРЕНД", //0
                "Предмет",//1
                "Артикул\nПоставщика",//2
                "Размер",//3
                "Баркод",//4
                "Артикул ВБ",//5
                "Стоимость\n закупки",//6
                "Упаковка",//7
                "Прочие\n расходы",//8
                "Логистика",//9
                "Себестоимость \nс издержками",//10
                "РРЦ",//11
                "Скидка \nна ВБ %",//12
                "Цена \nпосле скидки %",//13
                "Комиссия WB %",//14
                "Налог %",//15
                "Чистая \nприбыль",//16
                "Маржинальность %"
        };
        int colNums = colNames.length;

        for (int c = 0; c < colNums; c++) {
            XSSFCell mainCell = mainRow.createCell(c);
            mainCell.setCellValue(colNames[c]);
            mainCell.setCellStyle(cellStyle);
        }

        ArrayList<Stock> stocks = (ArrayList<Stock>) customStocksRepository.findAllByApiKey(api);


        HashSet<String> barcodes = new HashSet<>();
        for(Stock stock : stocks){
            barcodes.add(stock.getBarcode());
        }
        int rowNum = 1;

        for(String  barcode : barcodes){
            XSSFRow row = sheet.createRow(rowNum);
            for (int c = 0; c < colNums; c++) {
                XSSFCell cell = row.createCell(c);
                Stock currentStock = stocks.stream().filter(stock -> stock.getBarcode().equals(barcode)).findAny().get();
                switch (c) {
                    case 0: { //бренд
                        cell.setCellValue(stocks.stream().filter(stock -> stock.getBarcode().equals(barcode)).findAny().get().getBrand());
                        cell.setCellStyle(simpleCellStyle);
                        break;
                    }
                    case 1: { //предмет
                        cell.setCellValue(stocks.stream().filter(stock -> stock.getBarcode().equals(barcode)).findAny().get().getSubject());
                        cell.setCellStyle(simpleCellStyle);
                        break;
                    }
                    case 2: { // артикул продавца
                        cell.setCellValue(stocks.stream().filter(stock -> stock.getBarcode().equals(barcode)).findAny().get().getSupplierArticle());
                        cell.setCellStyle(simpleCellStyle);
                        break;
                    }
                    case 3: { // размер
                        cell.setCellValue(stocks.stream().filter(stock -> stock.getBarcode().equals(barcode)).findFirst().get().getTechSize());
                        cell.setCellStyle(simpleCellStyle);
                        break;
                    }
                    case 4: { // баркод
                        cell.setCellValue(stocks.stream().filter(stock -> stock.getBarcode().equals(barcode)).findFirst().get().getBarcode());
                        cell.setCellStyle(simpleCellStyle);
                        break;
                    }
                    case 5: { //артикул вб + линк
                        cell.setCellValue(stocks.stream().filter(stock -> stock.getBarcode().equals(barcode)).findFirst().get().getNmId());
                        XSSFHyperlink link1
                                = (XSSFHyperlink) createHelper.createHyperlink(Hyperlink.LINK_URL);
                        link1.setAddress(stocks.stream().filter(stock -> stock.getBarcode().equals(barcode)).findFirst().get().getWBLink());
                        cell.setHyperlink(link1);
                        cell.setCellStyle(linkStyle);
                        break;
                    }
                    case 6: { //Стоимость закупки
                        cell.setCellValue(0);
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        cell.setCellStyle(styleMoney);
                        break;
                    }
                    case 7: { //Упаковка
                        cell.setCellValue(0);
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        cell.setCellStyle(styleMoney);
                        break;
                    }
                    case 8: { // Прочие Расходы
                        cell.setCellValue(0);
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        cell.setCellStyle(styleMoney);
                        break;
                    }
                    case 9: { // Логистика
                        cell.setCellValue(0);
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        cell.setCellStyle(styleMoney);
                        break;
                    }
                    case 10: { // Себестоимость с издержками
                        cell.setCellFormula(row.getCell(6).getReference()+ "+"+
                                row.getCell(7).getReference() + "+"+
                                row.getCell(8).getReference());
                            cell.setCellStyle(styleMoney);
                            cell.setCellType(Cell.CELL_TYPE_FORMULA);
                            evaluator.evaluate(cell);
                        break;
                        }

                    case 11: { // РРЦ
                        cell.setCellValue(stocks.stream().filter(stock -> stock.getBarcode().equals(barcode)).findFirst().get().getPrice().doubleValue());
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        cell.setCellStyle(styleMoney);
                        break;
                    }
                    case 12: { // Скидка на вб
                        cell.setCellValue(currentStock.getDiscount());
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        cell.setCellStyle(simpleCellStyle);
                        break;
                    }
                    case 13: { // Цена посе скидки
                        cell.setCellFormula(row.getCell(11).getReference()+ "*((100-"+
                                row.getCell(12).getReference() + ")/100)");

                        cell.setCellStyle(styleMoney);
                        cell.setCellType(Cell.CELL_TYPE_FORMULA);
                        evaluator.evaluate(cell);
                        break;
                    }

                    case 14: { // Comission WB

                        float commisonPercent = comissionRepository.findById(currentStock.getSubject()).isPresent()? comissionRepository.findById(currentStock.getSubject()).get().getPercent() : 0;
                        cell.setCellValue(commisonPercent);
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        cell.setCellStyle(simpleCellStyle);
                        break;
                    }
                    case 15: { // Налог
                        cell.setCellValue(7);
                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                        cell.setCellStyle(simpleCellStyle);
                        break;
                    }
                    case 16: { // ЧИстая прибыль
                        cell.setCellFormula(row.getCell(13).getReference()+"*((100-"+row.getCell(14).getReference()+"-"+
                                row.getCell(15).getReference()+")/100)-"+row.getCell(10).getReference() +"-"+ row.getCell(9).getReference());
                        cell.setCellStyle(styleMoney);
                        cell.setCellType(Cell.CELL_TYPE_FORMULA);
                        evaluator.evaluate(cell);
                        break;
                    }
                    case 17: { // Маржинальность
                        cell.setCellFormula(row.getCell(16).getReference()+ "/"+
                                row.getCell(10).getReference());
                        cell.setCellStyle(stylePercentage);
                        cell.setCellType(Cell.CELL_TYPE_FORMULA);
                        evaluator.evaluate(cell);
                        break;
                    }
                }
            }
            rowNum++;
        }
        for (int col = 0; col <= colNums; col++) {
            sheet.autoSizeColumn(col);
        }

        workbook.write(new FileOutputStream("unit.xlsx"));
        return new File("unit.xlsx");

    }



}

