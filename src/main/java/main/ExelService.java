package main;

import main.model.*;
import org.apache.poi.hssf.util.AreaReference;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumns;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;

@Service
public class ExelService {
    @Autowired
    CustomSaleRepository customSaleRepository;
    @Autowired
    CustomOrderRepository customOrderRepository;

    public ArrayList<SaleOrder> getStatisticsArray(ZonedDateTime zdt1, ZonedDateTime zdt2){
        ArrayList<SaleOrder> saleOrders = new ArrayList<>();
        ArrayList<Order> orders = (ArrayList<Order>) customOrderRepository.findOrderByDateBetweenOrderByDate(zdt1,zdt2);
        ArrayList<Sale> sales = (ArrayList<Sale>) customSaleRepository.getSaleByDateBetweenOrderByDate(zdt1,zdt2);
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

        public File getExelFile(ZonedDateTime start , ZonedDateTime end) throws IOException {

        ArrayList<SaleOrder> array = getStatisticsArray(start,end);

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Заказы и выкупы");

            CellStyle cellStyle = workbook.createCellStyle();
            CellStyle cellDataStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            cellDataStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));
            XSSFFont font = workbook.createFont();
            font.setBold(true);
            font.setFontName("Arial");
            font.setFontHeight(11);
            cellStyle.setFont(font);
            cellStyle.setBorderBottom((short) 2);

            XSSFCellStyle linkStyle = workbook.createCellStyle();
            XSSFFont linkFont = workbook.createFont();
            linkFont.setUnderline(XSSFFont.U_SINGLE);
            linkFont.setColor(Font.COLOR_RED);
            linkStyle.setFont(linkFont);


            XSSFRow mainRow = sheet.createRow(0);

            XSSFCell mainCell0 = mainRow.createCell(0);
            mainCell0.setCellValue("БРЕНД");
            mainCell0.setCellStyle(cellStyle);

            XSSFCell mainCell1 = mainRow.createCell(1);
            mainCell1.setCellValue("Предмет");
            mainCell1.setCellStyle(cellStyle);

            XSSFCell mainCell2 = mainRow.createCell(2);
            mainCell2.setCellValue("Артикул Поставщика");
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
            mainCell6.setCellValue("Количество заказов");
            mainCell6.setCellStyle(cellStyle);

            XSSFCell mainCell7 = mainRow.createCell(7);
            mainCell7.setCellStyle(cellStyle);
            mainCell7.setCellValue("сумма заказов");

            XSSFCell mainCell8 = mainRow.createCell(8);
            mainCell8.setCellStyle(cellStyle);
            mainCell8.setCellValue("Логистика");

            XSSFCell mainCell9 = mainRow.createCell(9);
            mainCell9.setCellStyle(cellStyle);
            mainCell9.setCellValue("Кол-во выкупа");

            XSSFCell mainCell10 = mainRow.createCell(10);
            mainCell10.setCellStyle(cellStyle);
            mainCell10.setCellValue("Сумма выкупа");

            XSSFCell mainCell11 = mainRow.createCell(11);
            mainCell11.setCellStyle(cellStyle);
            mainCell11.setCellValue("Кол-во возврата");

            XSSFCell mainCell12 = mainRow.createCell(12);
            mainCell12.setCellStyle(cellStyle);
            mainCell12.setCellValue("Сумма возврата");

            int cols = 13;
            int startRow = 1;
            int endRow = array.size()-1;

            for(SaleOrder saleOrder : array){
                XSSFRow row = sheet.createRow(startRow);

                XSSFCell cell0 = row.createCell(0);
                cell0.setCellValue(saleOrder.getBrand());

                XSSFCell cell1 = row.createCell(1);
                cell1.setCellValue(saleOrder.getSubject());

                XSSFCell cell2 = row.createCell(2);
                cell2.setCellValue(saleOrder.getSupplierArticle());

                XSSFCell cell3 = row.createCell(3);
                cell3.setCellValue(saleOrder.getTechSize());

                XSSFCell cell4 = row.createCell(4);
                cell4.setCellValue(saleOrder.getBarcode());

                XSSFCell cell5 = row.createCell(5);
                cell5.setCellValue(saleOrder.getNmId());
                XSSFHyperlink link1
                        = (XSSFHyperlink)createHelper.createHyperlink(Hyperlink.LINK_URL);
                link1.setAddress(saleOrder.getWbLink());
                cell5.setHyperlink(link1);
                cell5.setCellStyle(linkStyle);

                XSSFCell cell6 = row.createCell(6);
                cell6.setCellValue(saleOrder.getOrdersCount());
                cell6.setCellType(Cell.CELL_TYPE_NUMERIC);

                XSSFCell cell7 = row.createCell(7);
                cell7.setCellValue(saleOrder.getTotalPrice().setScale(2,RoundingMode.HALF_UP).doubleValue());
                cell7.setCellType(Cell.CELL_TYPE_NUMERIC);

                XSSFCell cell8 = row.createCell(8);
                cell8.setCellValue(saleOrder.getTotalLogistic().setScale(2,RoundingMode.HALF_UP).doubleValue());
                cell8.setCellType(Cell.CELL_TYPE_NUMERIC);

                XSSFCell cell9 = row.createCell(9);
                cell9.setCellValue( saleOrder.getSaleCount());
                cell9.setCellType(Cell.CELL_TYPE_NUMERIC);

                XSSFCell cell10 = row.createCell(10);
                cell10.setCellValue( saleOrder.getForPay().setScale(2,RoundingMode.HALF_UP).doubleValue());
                cell10.setCellType(Cell.CELL_TYPE_NUMERIC);

                XSSFCell cell11 = row.createCell(11);
                cell11.setCellValue( saleOrder.getSaleCancel());
                cell11.setCellType(Cell.CELL_TYPE_NUMERIC);

                XSSFCell cell12 = row.createCell(12);
                cell12.setCellValue(saleOrder.getCancelPay().setScale(2,RoundingMode.HALF_UP).doubleValue());
                cell12.setCellType(Cell.CELL_TYPE_NUMERIC);

                startRow++;
            }

            workbook.write(new FileOutputStream("test.xlsx"));
            return new File("test.xlsx");
        }

    }
