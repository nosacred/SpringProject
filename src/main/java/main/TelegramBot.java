package main;

import com.vdurmont.emoji.EmojiParser;
import lombok.SneakyThrows;
import main.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    @Autowired
    GetOrder getGetOrder;
    @Autowired
    CustomOrderRepository customOrderRepository;
    @Autowired
    CustomSaleRepository customSaleRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    GetSale getGetSale;
    @Autowired
    OrderService orderService;
    @Autowired
    PhotoBaseService photoBaseService;
    @Autowired
    TgUserRepository tgUserRepository;
    @Autowired
    PhotoBaseRepository photoBaseRepository;
    @Autowired
    CustomStocksRepository customStocksRepository;
    @Autowired
    StockService stockService;
    @Autowired
    GetStock getStock;
    @Autowired
    ExelService exelService;
    @Autowired
    CustomTgUserRepository customTgUserRepository;
    @Autowired
    ComissionService comissionService;
    @Autowired
    ComissionRepository comissionRepository;


    DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private ZonedDateTime today = ZonedDateTime.now(ZoneId.systemDefault());
    private ZonedDateTime yesterDay = today.minusDays(1);

    @Autowired
    final BotConfig botConfig;

    public TelegramBot(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    private void startCommandReceived(long chatId, String name) throws TelegramApiException {
        String answer = EmojiParser.parseToUnicode("Привет , " + name + " рад познакомиться с тобой " + " :smile_cat: \n"+
                "для начала работы нам потребуется API-ключ СТАТИСТИКИ из личного кабинета главного пользователя в Wildberries\n"+
                "Скопируй этот ключ и отправь его мне, предварительно написав мне /api");
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);
        execute(message);
    }
    int customId=0;
    boolean apiRequestFlag = false;
    boolean customStatFlag = false;
    boolean startDateFlag = false;
    String regexDate = "^\\d{4}\\S\\d{2}\\S\\d{2}";
    Pattern pattern = Pattern.compile(regexDate, Pattern.CASE_INSENSITIVE);
    ZonedDateTime st = ZonedDateTime.now();
    ZonedDateTime en = ZonedDateTime.now();
    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        int upId = 0;
//        customId=update.getUpdateId();

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (messageText) {
                case "/start":
                    TgUser tgUser = new TgUser();
                    tgUser.setChatId(chatId);
                    tgUser.setFirstName(update.getMessage().getChat().getFirstName());
                    tgUserRepository.save(tgUser);
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
//                    firstMesseges(chatId);
                    break;
                case "/upPhoto": {
                    upPhoto(chatId,tgUserRepository.findById(chatId).get().getApi());
                }
                    break;
                case "/stocks":
                    getStock.getAllStocks(tgUserRepository.findById(chatId).get().getApi());
                    break;
                case "/weekstat" : getWeekStats(tgUserRepository.findById(chatId).get().getApi(), chatId);
                break;
                case "/monthstat" :getMonthStats(tgUserRepository.findById(chatId).get().getApi(),chatId);
                break;
                case "/lastmonthstat" : getLastMonthStats(tgUserRepository.findById(chatId).get().getApi(),chatId);
                break;
                case "/thismonthstat" : getThisMonthStats(tgUserRepository.findById(chatId).get().getApi(),chatId);
                break;
                case "/minus90" : {
                    String api = tgUserRepository.findById(chatId).get().getApi();
                    getGetSale.getNewSalessMinus90(api);
                    getGetOrder.getNewOrdersMinus90(api);
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId);
                    sendMessage.setText("Данные обновлены!");
                    execute(sendMessage);

                }
                break;
                case "/minusYear" : {
                    String api = tgUserRepository.findById(chatId).get().getApi();
                    getGetSale.getNewSalessMinusYear(api);
                    getGetOrder.getNewOrdersMinusYear(api);
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId);
                    sendMessage.setText("Данные обновлены!");
                    execute(sendMessage);
                }
                break;
                case "/api" : {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId);
                    sendMessage.setText("Введите Ваш API-Ключ статистики");
                    execute(sendMessage);
                    upId = update.getUpdateId();
                    System.out.println(upId);
                    apiRequestFlag = true;
                    break;
                }
                case "/customstat" : {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId);
                    sendMessage.setText("Введите дату начала периода в формате: yyyy-MM-dd ");
                    customId = update.getUpdateId();
                    System.out.println(customId);
                    customStatFlag = true;
                    execute(sendMessage);
                    break;
                }
                case "/comission":{
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId);
                    sendMessage.setText(comissionService.setCommission() ? "Комиссия успешно обновлена": "ERROR CANNOT UPDATE COMISSION" );
                    execute(sendMessage);
                }
            }

            if (customStatFlag && update.getUpdateId()==customId+1){
                String stDt =  update.getMessage().getText();
                if(Pattern.matches(regexDate,stDt)){
                    Pattern ptNum = Pattern.compile("\\D");
                    String[] numbers = ptNum.split(stDt,3);
                    if(Integer.parseInt(numbers[1])>0 && Integer.parseInt(numbers[1]) <=12 &&
                            Integer.parseInt(numbers[2])>0 && Integer.parseInt(numbers[1]) <=31){
                        LocalDate ld = LocalDate.parse(stDt);
                        st = ld.atStartOfDay(ZoneId.systemDefault());
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(chatId);
                        sendMessage.setText("Введите дату конца периода в формате: гггг-MM-дд\nПРИМЕР: 2023-02-01 ");
                        execute(sendMessage);
                        startDateFlag = true;
                        customId = update.getUpdateId();
                        customStatFlag = false;
                        System.out.println(customId);
                        chatId = update.getUpdateId();
                        System.out.println(startDateFlag);
                    } else {
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(chatId);
                        sendMessage.setText("Вы ввели не корректную дату!\nВведите дату конца периода в формате: гггг-MM-дд\n" +
                                "ПРИМЕР: 2023-02-01 ");
                        execute(sendMessage);
                        customId = update.getUpdateId();
                    }
                } else {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId);
                    sendMessage.setText("Вы ввели не корректную дату!\nВведите дату конца периода в формате: гггг-MM-дд\n" +
                            "ПРИМЕР: 2023-02-01 ");
                    execute(sendMessage);
                    customId = update.getUpdateId();
                }

            }

            if (startDateFlag && update.getUpdateId()==customId+1){
                String end =  update.getMessage().getText();
                LocalDate ld = LocalDate.parse(end);
                en = ld.atStartOfDay(ZoneId.systemDefault());
                customStatFlag = false;
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText("Ваш отчет с "+ st.format(formatterDate) + " по " + en.format(formatterDate) +
                        " выполняется");
                execute(sendMessage);
                if(en.isBefore(st)) {
                    getCustomStats(tgUserRepository.findById((long) chatId).get().getApi(),chatId, en, st);
                } else getCustomStats(tgUserRepository.findById((long) chatId).get().getApi(),chatId, st,en);
            }
            if(apiRequestFlag && update.getUpdateId() != upId){
                System.out.println(update.getUpdateId());
                takeUserApi(chatId,messageText);
                apiRequestFlag = false;
            }
        }

    }

    private void getWeekStats(String api, long chatId) throws IOException, TelegramApiException {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(chatId);
        ZonedDateTime start = ZonedDateTime.now().minusDays(7);
        ZonedDateTime end = ZonedDateTime.now();
        String fileName  = "Отчет с "+ start.format(formatterDate)+ " по " + end.format(formatterDate)+".xlsx";
        InputFile inputFile = new InputFile(exelService.getExelFile(api,start,end), fileName);
        sendDocument.setDocument(inputFile);
        execute(sendDocument);
    }

    private void getMonthStats(String api,long chatId) throws IOException, TelegramApiException {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(chatId);
        ZonedDateTime start = ZonedDateTime.now().minusDays(30);
        ZonedDateTime end = ZonedDateTime.now();
        String fileName  = "Отчет с "+ start.format(formatterDate)+ " по " + end.format(formatterDate)+".xlsx";
        InputFile inputFile = new InputFile(exelService.getExelFile(api,start,end), fileName);
        sendDocument.setDocument(inputFile);
        execute(sendDocument);
    }

    private void getLastMonthStats(String api,long chatId) throws IOException, TelegramApiException {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(chatId);
        ZonedDateTime start = ZonedDateTime.now().minusMonths(1).withDayOfMonth(1);
        ZonedDateTime end = start.plusMonths(1).minusDays(1);
        String fileName  = "Отчет с "+ start.format(formatterDate)+ " по " + end.format(formatterDate)+".xlsx";
        InputFile inputFile = new InputFile(exelService.getExelFile(api,start,end), fileName);
        sendDocument.setDocument(inputFile);
        execute(sendDocument);
    }

    private void getThisMonthStats(String api,long chatId) throws IOException, TelegramApiException {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(chatId);
        ZonedDateTime start = ZonedDateTime.now().withDayOfMonth(1);
        ZonedDateTime end = ZonedDateTime.now();
        String fileName  = "Отчет с "+ start.format(formatterDate)+ " по " + end.format(formatterDate)+".xlsx";
        InputFile inputFile = new InputFile(exelService.getExelFile(api,start,end), fileName);
        sendDocument.setDocument(inputFile);
        execute(sendDocument);
    }

    private void getCustomStats(String api, long chatId, ZonedDateTime start, ZonedDateTime end) throws IOException, TelegramApiException {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(chatId);
        String fileName  = "Отчет с "+ start.format(formatterDate)+ " по " + end.format(formatterDate)+".xlsx";
        InputFile inputFile = new InputFile(exelService.getExelFile(api,start,end), fileName);
        sendDocument.setDocument(inputFile);
        execute(sendDocument);
    }

    private void takeUserApi(long chatId, String api) throws TelegramApiException, IOException, InterruptedException {
        if (getGetOrder.checkApi(api)) {

            List<TgUser> users = (List<TgUser>) tgUserRepository.findAll();
            for (TgUser u : users) {
                if (u.getChatId() == chatId) {
                    u.setApi(api);
                    tgUserRepository.save(u);
                }
            }
            SendMessage msg = new SendMessage();
            msg.setChatId(chatId);
            msg.setText("Ваш API ключ принят\n" +
                    "Статистика начнет работать в течение нескольких минут");
            execute(msg);
            firstMesseges(api,chatId);
        }else {
            SendMessage msg = new SendMessage();
            msg.setChatId(chatId);
            msg.setText("Не правильный ключ!попробуйте снова!\nВведите команду /api , а затем введите Апи ключ");
            execute(msg);
            apiRequestFlag = true;
        }
    }



    @Scheduled(initialDelay = 10000, fixedRate = 31 * 60 * 1000)
    private void sendNewOrders() throws IOException, InterruptedException, TelegramApiException {
        List<TgUser> users = (List<TgUser>) tgUserRepository.findAll();
        HashSet<String> apies = new HashSet<>();
        for (TgUser tgUser : users) {
            if (tgUser.getApi() != null) {
                apies.add(tgUser.getApi());
            }

        }
        for (String api : apies) {
            ArrayList<TgUser> tgUsers = customTgUserRepository.getByApi(api);

            ArrayList<Order> todayOrders = (ArrayList<Order>) customOrderRepository.getOrderByApiKeyAndDateBetweenOrderByDate(api, today.withHour(0).
                    withMinute(0).withSecond(0), ZonedDateTime.now(ZoneId.systemDefault()).minusMinutes(15));
            todayOrders.sort(Comparator.comparing(Order::getDate));
            ArrayList<Order> yestOrders = (ArrayList<Order>) customOrderRepository.getOrderByApiKeyAndDateBetweenOrderByDate(api, yesterDay.withHour(0).
                    withMinute(0).withSecond(0), yesterDay.withHour(23).
                    withMinute(59).withSecond(59));
            BigDecimal sumToday = BigDecimal.ZERO;
            todayOrders.sort(Comparator.comparing(Order::getDate));
            for (Order order : todayOrders) {
                sumToday = sumToday.add(order.getTotalPriceWithDisc());
            }

            System.out.println("Cегодняшнее кол-во заказов - " + todayOrders.size());
            System.out.println("Вчерашнее кол-во заказов - " + yestOrders.size());
            boolean nextDayChek = false;
            // Обновляем данные если наступили новые сутки
            int ordersCountoday = todayOrders.size();
            System.out.println("Размер архива заказов за сегодня: " + ordersCountoday);
            if (LocalDate.now().isEqual(today.toLocalDate().plusDays(1))) {
                getGetOrder.getAllOrdersAtDate(api, today);
//            TimeUnit.MINUTES.sleep(2);
                getGetSale.getAllSalesAtDate(api, today);
                yesterDay = today;
                today = today.plusDays(1);
                todayOrders.clear();
                todayOrders = (ArrayList<Order>) customOrderRepository.getOrderByApiKeyAndDateBetweenOrderByDate(api, today.withHour(0).
                        withMinute(0).withSecond(0), ZonedDateTime.now(ZoneId.systemDefault()).minusMinutes(15));
                todayOrders.sort(Comparator.comparing(Order::getDate));
                yestOrders = (ArrayList<Order>) customOrderRepository.getOrderByApiKeyAndDateBetweenOrderByDate(api, yesterDay.withHour(0).
                        withMinute(0).withSecond(0), yesterDay.withHour(23).
                        withMinute(59).withSecond(59));
                sumToday = BigDecimal.ZERO;
                todayOrders.sort(Comparator.comparing(Order::getDate));
                nextDayChek = true;
            }

            // Обновление складских остатков в промежутке с 10 до 11 утра каждый день!
            if (ZonedDateTime.now().isAfter(today.withHour(10).withMinute(0)) && ZonedDateTime.now().isBefore(today.withHour(11).withMinute(0))) {
                getStock.getAllStocks(api);
                TimeUnit.MINUTES.sleep(1);
            }

            if (nextDayChek) {
                TimeUnit.MINUTES.sleep(1);
            }
            sumToday = BigDecimal.ZERO;
            todayOrders.sort(Comparator.comparing(Order::getDate));

            for (Order order : todayOrders) {
                sumToday = sumToday.add(order.getTotalPriceWithDisc());
            }
            System.out.println("Сумма заказов из архива: " + sumToday);


            getGetSale.getAllSalesAtDate(api, today);

            ArrayList<Sale> sales = (ArrayList<Sale>) customSaleRepository.getSaleByApiKeyAndDateBetweenOrderByDate(api, today.withHour(0).
                    withMinute(0).withSecond(0), ZonedDateTime.now(ZoneId.systemDefault()).minusMinutes(15));

            int salesCount = sales.size();
            DecimalFormat df = new DecimalFormat("### ###,000");
            BigDecimal salesSum = sales.stream().map(Sale::getForPay).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
            System.out.println("Выкупили на " + df.format(salesSum));

            ArrayList<Order> orders = getGetOrder.getNewOrdersNow(api);
            orders.sort(Comparator.comparing(Order::getDate));
            orderRepository.saveAll(orders);
            for (Order order : orders) {
                BigDecimal yesterDayAllOrdSum;
//            for (Order order1 : yestOrders) {
//                yesterDayAllOrdSum = yesterDayAllOrdSum.add(order1.getTotalPriceWithDisc());
//            }
                yesterDayAllOrdSum = yestOrders.stream().map(Order::getTotalPriceWithDisc)
                        .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                ArrayList<Stock> stocks = (ArrayList<Stock>) customStocksRepository.findAllByBarcode(order.getBarcode());

                int sendCount = 0;

                //Установка фото заказа
                SendPhoto sendPhoto = new SendPhoto();
                InputFile photo = new InputFile(photoBaseService.getPhotoLink(order.getNmId()) != null ? photoBaseService.getPhotoLink(order.getNmId()) : "https://diamed.ru/wp-content/uploads/2020/11/nophoto.png");
                sendPhoto.setPhoto(photo);

                //Обработка вчерашних заказов по артикулу!
                int yestCount = (int) yestOrders.stream().filter(order1 -> order1.getBarcode().equals(order.getBarcode())).count();
                BigDecimal yestSum = yestOrders.stream().filter(order1 -> order1.getBarcode().equals(order.getBarcode()))
                        .map(Order::getTotalPriceWithDisc)
                        .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                //Кол-во и сумма заказа за сегодня по артикулу
                int todayCount = (int) todayOrders.stream().
                        filter(order1 -> order1.getBarcode().equals(order.getBarcode())).count() + 1;
                BigDecimal todaySum = order.getTotalPriceWithDisc();
                List<Order> qnSumtoday = todayOrders.stream().filter(order1 -> order1.getBarcode().equals(order.getBarcode())).collect(Collectors.toList());
                for (Order os : qnSumtoday) {
                    todaySum = todaySum.add(os.getTotalPriceWithDisc());
                }


                String dt = "";
                if (order.getDate().toLocalDate().isBefore(today.toLocalDate())) {
                    dt = ":calendar:<b>Заказ за прошедшую дату!</b>\n" +
                            ":calendar:<b><i>" + order.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) + "</i></b>\n";


                } else if (todayOrders.contains(order) || yestOrders.contains(order)) {
                    continue;
                } else {
                    ordersCountoday++;
                    dt = ":calendar:<b>" + order.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) + "</b>\n" +
                            ":chart_with_upwards_trend:<b>Заказ: [" + ordersCountoday + "]</b>\n";
                    sumToday = sumToday.add(order.getTotalPriceWithDisc());
                    System.out.println(" Сумма заказов с заказом [" + ordersCountoday + "] = " + sumToday);
                }

                int commisonPercent = comissionRepository.findById(order.getSubject()).get().getPercent();
                String cancel = "";
                String orderPrice = ":dollar:<b>" + df.format(order.getTotalPriceWithDisc()) + "\u20BD" + "</b>" + "\n"+
                        ":credit_card:<b>Комиссия "+ commisonPercent+"% -"+df.format(order.getTotalPriceWithDisc().
                        multiply(BigDecimal.valueOf(commisonPercent).divide(BigDecimal.valueOf(100),2,RoundingMode.HALF_UP)))+ "\u20BD" + "</b>" + "\n" ;
                if (order.getIsCancel().equals("true")) {
                    cancel = ":name_badge: <b>ЗАКАЗ ОТМЕНЕН!</b>:name_badge:\n";
                    orderPrice = ":stop_sign:<b>-" + df.format(order.getTotalPriceWithDisc()) + "\u20BD" + "</b>" + "\n";
                }

                String text = EmojiParser.parseToUnicode(dt + cancel + orderPrice +
                        "Тип заказа: "+ order.getOrderType()+"\n"+
                        ":package:<b>" + order.getSupplierArticle() + "</b>\n" +
                        ":id:<a href=\"" + order.getWBLink() + "\">" + order.getNmId() + "</a>\n" +
                        ":gear:" + order.getBrand() + "\n" +
                        ":green_book:" + order.getCategory() + " / " +
                        order.getSubject() + "\n" +
                        ":triangular_ruler:размер: <b>" + order.getTechSize() + "</b>\n" +
                        "UNIQ-ID: " + order.getOdid() + "\n" +
                        ":truck:Логистика(min): <b>" + order.getLogisticPrice() + "\u20BD" + "</b>" + "\n" +
                        ":warning:Товаров на складах:  <b>" + stockService.getQuantity(stocks) +
                        "</b> / В пути: " + stockService.getQuantityInWay(stocks) + "\n" +
                        ":watch:Последнее обновление остатков: " + (stocks.stream().findFirst().isPresent() ? stocks.stream().findFirst().get().getLastChangeDate() : "нет данных ") + "\n" +
                        ":rocket:<b>Сегодня таких " + (order.getDate().toLocalDate().isBefore(today.toLocalDate()) ? 0 : todayCount) +
                        " шт на  " +  (order.getDate().toLocalDate().isBefore(today.toLocalDate()) ? 0 : df.format(todaySum)) + "</b>" + "\u20BD" + "\n" +
                        "Вчера таких <b>" + yestCount +
                        " шт на " + df.format(yestSum) + "</b>" + "\u20BD" + "\n" +
                        ":factory:<b>" + order.getWarehouseName() + "</b>:arrow_right:" + order.getOblast() + "\n" +
                        ":dart:<strong>ЗАКАЗАЛИ сегодня: " +  ordersCountoday + "&#128293;" + "</strong> на СУММУ" +
                        ":moneybag: <b>" + df.format(sumToday) + "\u20BD" + "</b>" + "\n" +
                        ":money_with_wings:ВЫКУПИЛИ сегодня <b>" + salesCount + " шт.</b> на <b>" + df.format(salesSum) + "\u20BD" + "</b>" +
                        "\n<i> Вчера заказано " + yestOrders.size() + " штук на " + df.format(yesterDayAllOrdSum) + "</i>" + "\u20BD");
                sendPhoto.setCaption(text);
                sendPhoto.setParseMode(ParseMode.HTML);

                for (TgUser user : tgUsers) {
                    sendPhoto.setChatId(user.getChatId());
                    try {
                        execute(sendPhoto);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                        System.out.println("Проблема с фото " + order.getNmId());
                    }
                }
                sendCount++;
                if (sendCount == 10) {
                    TimeUnit.SECONDS.sleep(2);
                    sendCount = 0;
                }
            }
                todayOrders.clear();
                sumToday = BigDecimal.ZERO;
                if (nextDayChek) {
                    nextDayChek = false;
                }
                System.out.println("ЦИКЛ ОКОНЧЕН");

        }
    }






public void firstMesseges(String api,long chatId) throws TelegramApiException, IOException, InterruptedException {
    getGetSale.getNewSalessMinus90(api);
    getGetOrder.getNewOrdersMinus90(api);
    int ordersCountoday=0;
    ArrayList<Order> todayOrders = (ArrayList<Order>) customOrderRepository.getOrderByApiKeyAndDateBetweenOrderByDate(api, today.withHour(0).
            withMinute(0).withSecond(0), ZonedDateTime.now(ZoneId.systemDefault()).minusMinutes(15));
    todayOrders.sort(Comparator.comparing(Order::getDate));
    ArrayList<Order> yestOrders = (ArrayList<Order>) customOrderRepository.getOrderByApiKeyAndDateBetweenOrderByDate(api, yesterDay.withHour(0).
            withMinute(0).withSecond(0), yesterDay.withHour(23).
            withMinute(59).withSecond(59));
    BigDecimal sumToday = BigDecimal.ZERO;
    todayOrders.sort(Comparator.comparing(Order::getDate));
    for (Order order : todayOrders) {
        sumToday = sumToday.add(order.getTotalPriceWithDisc());
    }

    System.out.println("Cегодняшнее кол-во заказов - " + todayOrders.size());
    System.out.println("Вчерашнее кол-во заказов - " + yestOrders.size());


    sumToday = BigDecimal.ZERO;
    todayOrders.sort(Comparator.comparing(Order::getDate));

    for (Order order : todayOrders) {
        sumToday = sumToday.add(order.getTotalPriceWithDisc());
    }
    System.out.println("Сумма заказов из архива: " + sumToday);

    ArrayList<Sale> sales = (ArrayList<Sale>) customSaleRepository.getSaleByApiKeyAndDateBetweenOrderByDate(api, today.withHour(0).
            withMinute(0).withSecond(0), ZonedDateTime.now(ZoneId.systemDefault()).minusMinutes(15));

    int salesCount = sales.size();
    DecimalFormat df = new DecimalFormat("### ###,000");
    BigDecimal salesSum = sales.stream().map(Sale::getForPay).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
    System.out.println("Выкупили на " + df.format(salesSum));

    todayOrders.sort(Comparator.comparing(Order::getDate));
    for (Order order : todayOrders) {
        BigDecimal yesterDayAllOrdSum;
//            for (Order order1 : yestOrders) {
//                yesterDayAllOrdSum = yesterDayAllOrdSum.add(order1.getTotalPriceWithDisc());
//            }
        yesterDayAllOrdSum = yestOrders.stream().map(Order::getTotalPriceWithDisc)
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

        ArrayList<Stock> stocks = (ArrayList<Stock>) customStocksRepository.findAllByBarcode(order.getBarcode());
        int sendCount = 0;

        //Установка фото заказа
        SendPhoto sendPhoto = new SendPhoto();
        InputFile photo = new InputFile(photoBaseService.getPhotoLink(order.getNmId()));
        sendPhoto.setPhoto(photo);

        //Обработка вчерашних заказов по артикулу!
        int yestCount = (int) yestOrders.stream().filter(order1 -> order1.getBarcode().equals(order.getBarcode())).count();
        BigDecimal yestSum = yestOrders.stream().filter(order1 -> order1.getBarcode().equals(order.getBarcode()))
                .map(Order::getTotalPriceWithDisc)
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
//            List<Order> qnSumYest = yestOrders.stream().filter(order1 -> order1.getBarcode().equals(order.getBarcode())).collect(Collectors.toList());
//            for (Order os : qnSumYest) {
//                yestSum = yestSum.add(os.getTotalPriceWithDisc());
//            }

        //Кол-во и сумма заказа за сегодня по артикулу
        int todayCount = (int) todayOrders.stream().filter(order1 -> order1.getBarcode().equals(order.getBarcode())).count() + 1;
        BigDecimal todaySum = order.getTotalPriceWithDisc();
        List<Order> qnSumtoday = todayOrders.stream().filter(order1 -> order1.getBarcode().equals(order.getBarcode())).collect(Collectors.toList());

        for (Order os : qnSumtoday) {
            todaySum = todaySum.add(os.getTotalPriceWithDisc());
        }
            ordersCountoday++;
            String dt = ":calendar:<b>" + order.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "</b>\n" +
                    ":chart_with_upwards_trend:<b>Заказ: [" + ordersCountoday + "]</b>\n";
            sumToday = sumToday.add(order.getTotalPriceWithDisc());
            System.out.println(" Сумма заказов с заказом [" + ordersCountoday + "] = " + sumToday);


        String cancel = "";
        String orderPrice = ":dollar:<b>" + df.format(order.getTotalPriceWithDisc()) + "\u20BD" + "</b>" + "\n";


        String text = EmojiParser.parseToUnicode(dt + cancel + orderPrice +
                ":package:<b>" + order.getSupplierArticle() + "</b>\n" +
                ":id:<a href=\"" + order.getWBLink() + "\">" + order.getNmId() + "</a>\n" +
                ":gear:" + order.getBrand() + "\n" +
                ":green_book:" + order.getCategory() + " / " +
                order.getSubject() + "\n" +
                ":triangular_ruler:размер: <b>" + order.getTechSize() + "</b>\n" +
                "UNIQ-ID: " + order.getOdid() + "\n" +
                ":truck:Логистика(min): <b>" + order.getLogisticPrice() + "\u20BD" + "</b>" + "\n" +
                ":warning:Товаров на складах:  <b>" + stockService.getQuantity(stocks) +
                "</b> / В пути: " + stockService.getQuantityInWay(stocks) + "\n" +
                ":watch:Последнее обновление остатков: " + stocks.stream().findFirst().get().getLastChangeDate() + "\n" +
                ":rocket:<b>Сегодня таких " + todayCount +
                " шт на  " + df.format(todaySum) + "</b>" + "\u20BD" + "\n" +
                "Вчера таких <b>" + yestCount +
                " шт на " + df.format(yestSum) + "</b>" + "\u20BD" + "\n" +
                ":factory:<b>" + order.getWarehouseName() + "</b>:arrow_right:" + order.getOblast() + "\n" +
                ":dart:<strong>ЗАКАЗАЛИ сегодня: " + ordersCountoday + "&#128293;" + "</strong> на СУММУ" +
                ":moneybag: <b>" + df.format(sumToday) + "\u20BD" + "</b>" + "\n" +
                ":money_with_wings:ВЫКУПИЛИ сегодня <b>" + salesCount + " шт.</b> на <b>" + df.format(salesSum) + "\u20BD" + "</b>" +
                "\n<i> Вчера заказано " + yestOrders.size() + " штук на " + df.format(yesterDayAllOrdSum) + "</i>" + "\u20BD");
        sendPhoto.setCaption(text);
        sendPhoto.setParseMode(ParseMode.HTML);
                sendPhoto.setChatId(chatId);
            try {
                execute(sendPhoto);
                sendCount++;
            } catch (TelegramApiException e) {
                e.printStackTrace();
                System.out.println("Проблема с фото " + order.getNmId());
            }
        if (sendCount == 10) {
            TimeUnit.SECONDS.sleep(2);
            sendCount = 0;
        }
        }



    }

public void upPhoto(Long chatId, String api) throws InterruptedException, TelegramApiException {

        ArrayList <PhotoBase> photoBases = (ArrayList<PhotoBase>) photoBaseRepository.findAll();
        ArrayList <Order> orders = (ArrayList<Order>) customOrderRepository.getOrderByApiKeyAndDateBetweenOrderByDate(api, ZonedDateTime.now().minusDays(15),ZonedDateTime.now());
        HashSet<String> nmIds = new HashSet<>();
   for(Order order : orders) {
       nmIds.add(order.getNmId());
   }
   for( String s : nmIds){
       photoBaseService.updatePhoto(s);
   }
    SendMessage sms = new SendMessage();
   sms.setChatId(chatId);
   sms.setText("Обновлено  "+ nmIds.size() + " фото");
   execute(sms);


}
    @Override
    public String getBotUsername() {
        return botConfig.botName;
    }

    @Override
    public String getBotToken() {
        return botConfig.token;
    }
}
