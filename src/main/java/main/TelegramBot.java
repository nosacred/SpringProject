package main;

import com.google.errorprone.annotations.concurrent.LazyInit;
import com.vdurmont.emoji.EmojiParser;
import lombok.SneakyThrows;
import main.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    GetOrder getGetOrder;
    @Autowired
    CustomOrderRepository customOrderRepository;
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



    private ZonedDateTime today = ZonedDateTime.now(ZoneId.systemDefault());
    private ZonedDateTime yesterDay = today.minusDays(1);
    private BigDecimal sumToday = BigDecimal.ZERO;

    private ArrayList<Order> todayOrders = new ArrayList<>();
    private ArrayList<Order> yestOrders = new ArrayList<>();
    @Autowired
    final BotConfig botConfig;

    public TelegramBot(BotConfig botConfig){
        this.botConfig = botConfig;
//        List<BotCommand> listofCommands = new ArrayList<>();
//        listofCommands.add(new BotCommand("/newOrder","get new orders"));
//        listofCommands.add(new BotCommand("/help","help"));
//        listofCommands.add(new BotCommand("/mydata","mydata"));
//        try {
//            this.execute(new SetMyCommands(listofCommands,new BotCommandScopeDefault(),null));
//        }
//        catch (TelegramApiException e){
//            e.printStackTrace();
//        }
    }

    private void startCommandReceived(long chatId, String name) throws TelegramApiException {
        String answer = EmojiParser.parseToUnicode( "Hi, " + name + " nice to meet you!"+ " :smile_cat:");

        sendMessage(chatId, answer);

    }

    private void sendMessage(long chatId, String textToSend) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);
        execute(message);
}

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (messageText){
                case "/start":
                    TgUser tgUser = new TgUser();
                    tgUser.setChatId(chatId);
                    tgUser.setFirstName(update.getMessage().getChat().getFirstName());
                    tgUserRepository.save(tgUser);
                    startCommandReceived(chatId,update.getMessage().getChat().getFirstName());
                    firstMesseges(chatId);
                    break;
                case "/today":
                    allOrdersToDay(chatId);
                    break;
            }

    }

}


private void allOrdersToDay (long chatId) throws IOException, InterruptedException, TelegramApiException {

        ZonedDateTime znd = ZonedDateTime.now(ZoneId.systemDefault()).minusDays(0).withHour(0).withMinute(0).withSecond(0);
    ZonedDateTime zndYesterday = ZonedDateTime.now(ZoneId.systemDefault()).minusDays(1).withHour(0).withMinute(0).withSecond(0);
    ZonedDateTime znd2 = ZonedDateTime.now(ZoneId.systemDefault()).minusDays(0).withHour(23).withMinute(59).withSecond(59);
//        getGetOrder.getAllOrdersAtDate(znd);
        ArrayList<Order> orders = (ArrayList< Order >)customOrderRepository.findOrderByDateBetweenOrderByDate(znd, znd2);
//        if(orders.size()> 0) {
//            int i = 1;
//            BigDecimal sum = BigDecimal.ZERO;
//            for (Order order : orders) {
//                sum = sum.add(order.getTotalPriceWithDisc());
//                SendMessage message = new SendMessage();
//                message.setChatId(chatId);
//                message.setText("Заказ №" + i +"\n"+
//                        order.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss ")) + "\n" +
//                        "Бренд : " + order.getBrand() + "\n" +
//                        "Cклад: " + order.getWarehouseName() + " в " + order.getOblast() + "\n" +
//                        order.getCategory() + "/" + order.getSubject() + "/Размер " + order.getTechSize() + "\n" +
//                        "Артикул продавца: " + order.getSupplierArticle() + "\n" +
//                        "Сумма заказа: " + order.getTotalPriceWithDisc() + "руб" + "\n" +
//                        "Всего заказов - " + i + " на сумму: " + sum+ " рублей");
//                execute(message);
//                i++;
//                TimeUnit.SECONDS.sleep(1);
//            }
//        }
//        else {
//            SendMessage message = new SendMessage();
//            message.setChatId(chatId);
//            message.setText("Новых заказов нет");
//            execute(message);
//        }
    ArrayList<OrderSum> sumsToday = orderService.ordersSumInDay(znd);
    ArrayList<OrderSum> sumsYesterDay = orderService.ordersSumInDay(zndYesterday);
    for( OrderSum orderSum : sumsToday){

        List<OrderSum> yestSumList = sumsYesterDay.stream().filter(orderSum1 -> orderSum1.getBarcode().equals(orderSum.getBarcode())).collect(Collectors.toList());
        BigDecimal yestSum = yestSumList.size()!=0 ? yestSumList.get(0).getTotalOrdersPriceSum() : BigDecimal.ZERO;
        int k = yestSumList.size()!=0 ? yestSumList.get(0).getOrderQuantity() : 0;



        SendMessage message = new SendMessage();

        SendPhoto sendPhoto = new SendPhoto();

//        InputFile photo = new InputFile("http://basket-06.wb.ru/vol1049/part104906/104906047/images/c246x328/1.jpg");
        InputFile photo = new InputFile(photoBaseService.getPhotoLink(orderSum.getWBArticle()));
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(photo);
                message.setChatId(chatId);

                String text = EmojiParser.parseToUnicode("\n"+
                        ":loudspeaker:Заказы за " + orderSum.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+"\n"+
                        ":scroll:Артикул - "+ orderSum.getName()+ "\n"+
                        " :green_book:"+ orderSum.getCategory()+"\n"+
                        ":open_book:"+ orderSum.getSubject()+"\n"
                        +":straight_ruler:"+ orderSum.getTechSize() +"\n"+
                        orderSum.getBrand()+ "\n"+
                        "Количество: "+orderSum.getOrderQuantity() + " штук на сумму :dollar:"+ orderSum.getTotalOrdersPriceSum() + "рублей\n"+
                        ":timer_clock:вчера таких "+k
                        + " на сумму :dollar:"+
                        yestSum + " рублей");
        message.setText(text+ photo);
        sendPhoto.setCaption(text);
        execute(sendPhoto);
    }
}


     void startArrays() {
//        getGetOrder.getAllOrdersAtDate(yesterDay);
        todayOrders= (ArrayList<Order>) customOrderRepository.findOrderByDateBetweenOrderByDate(today.withHour(0).
                withMinute(0).withSecond(0),ZonedDateTime.now(ZoneId.systemDefault()));
         todayOrders.sort(Comparator.comparing(Order::getDate));
        yestOrders = (ArrayList<Order>) customOrderRepository.findOrderByDateBetweenOrderByDate(yesterDay.withHour(0).
                withMinute(0).withSecond(0),yesterDay.withHour(23).
                withMinute(59).withSecond(59));
        sumToday = BigDecimal.ZERO;
         todayOrders.sort(Comparator.comparing(Order::getDate));
        for(Order order : todayOrders) {
            sumToday = sumToday.add(order.getTotalPriceWithDisc());
        }

         System.out.println("Cегодняшнее кол-во заказов - "+ todayOrders.size());
         System.out.println("Вчерашнее кол-во заказов - "+ yestOrders.size());
}

@Scheduled(initialDelay = 10000,fixedRate = 15*60*1000 )
private void sendNewOrders() throws IOException, InterruptedException, TelegramApiException {
    List<TgUser> users = (List<TgUser>) tgUserRepository.findAll();
    yestOrders = (ArrayList<Order>) customOrderRepository.
            findOrderByDateBetweenOrderByDate(yesterDay.withHour(0).withMinute(0).withSecond(0),
                    yesterDay.withHour(23).withMinute(59).withSecond(59));
//    todayOrders.clear();
//    startArrays();
    // Обновляем данные если наступили новые сутки
    int ordersCountoday= todayOrders.size();
    if (LocalDate.now().isEqual(today.toLocalDate().plusDays(1))) {
        getGetOrder.getAllOrdersAtDate(today);
        yesterDay = today;
        today = today.plusDays(1);
        todayOrders.clear();
        ordersCountoday = 0;
        sumToday = BigDecimal.ZERO;
        startArrays();

    }
    ArrayList<Order> orders = getGetOrder.getNewOrdersNow();
    orders.sort(Comparator.comparing(Order::getDate));

    for (Order order : orders) {
        BigDecimal yesterDayAllOrdSum = BigDecimal.ZERO;
        for (Order order1 : yestOrders) {
            yesterDayAllOrdSum = yesterDayAllOrdSum.add(order1.getTotalPriceWithDisc());
        }
//        if (!todayOrders.contains(order) || order.getLastChangeDate().isAfter(ZonedDateTime.now(ZoneId.systemDefault()).minusMinutes(41))) {
        int sendCount = 0;

        SendPhoto sendPhoto = new SendPhoto();
        InputFile photo = new InputFile(photoBaseService.getPhotoLink(order.getNmId()));
        sendPhoto.setPhoto(photo);
        int yestCount = (int) yestOrders.stream().filter(order1 -> order1.getBarcode().equals(order.getBarcode())).count();
        BigDecimal yestSum = BigDecimal.ZERO;
        List<Order> qnSumYest = yestOrders.stream().filter(order1 -> order1.getBarcode().equals(order.getBarcode())).collect(Collectors.toList());
        for (Order os : qnSumYest) {
            yestSum = yestSum.add(os.getTotalPriceWithDisc());
        }


        int todayCount = (int) todayOrders.stream().filter(order1 -> order1.getBarcode().equals(order.getBarcode())).count() + 1;
        BigDecimal todaySum = order.getTotalPriceWithDisc();
        List<Order> qnSumtoday = todayOrders.stream().filter(order1 -> order1.getBarcode().equals(order.getBarcode())).collect(Collectors.toList());
        for (Order os : qnSumtoday) {
            todaySum = todaySum.add(os.getTotalPriceWithDisc());
        }


        String dt = "";
        if (order.getDate().toLocalDate().isBefore(today.toLocalDate())) {
            dt = ":calendar:<b>Вчерашний заказ</b>\n" +
                    ":calendar:<b><i>" + order.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "</i></b>\n";


        } else if(todayOrders.contains(order)) {
            dt = ":calendar:<b>" + order.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "</b>\n" +
                    ":chart_with_upwards_trend:<b>Заказ: [" + todayOrders.indexOf(order)+1 + "]</b>\n";
//            sumToday = sumToday.add(order.getTotalPriceWithDisc());
        }
        else {
            ordersCountoday++;
            dt = ":calendar:<b>" + order.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "</b>\n" +
                    ":chart_with_upwards_trend:<b>Заказ: [" + ordersCountoday + "]</b>\n";
            sumToday = sumToday.add(order.getTotalPriceWithDisc());
        }


        DecimalFormat df = new DecimalFormat("### ###,000");
        String cancel = "";
        String orderPrice = ":dollar:<b>" + df.format(order.getTotalPriceWithDisc()) + "\u20BD" + "</b>" + "\n" ;
        if(order.getIsCancel().equals("true")){
            cancel = ":name_badge: <b>ЗАКАЗ ОТМЕНЕН!</b>:name_badge:\n";
            orderPrice = ":stop_sign:<b>-" + df.format(order.getTotalPriceWithDisc()) + "\u20BD" + "</b>" + "\n" ;
        }

        String text = EmojiParser.parseToUnicode(dt + cancel + orderPrice +
                ":package:<b>" + order.getSupplierArticle() + "</b>\n" +
                ":id:<a href=\"" + order.getWBLink() + "\">" + order.getNmId() + "</a>\n" +
                ":gear:" + order.getBrand() + "\n" +
                ":green_book:" + order.getCategory() + " / " +
                order.getSubject() + "\n" +
                ":triangular_ruler:" + order.getTechSize() + "\n" +
                ":truck:Логистика: <b>" + order.getLogisticPrice() + "\u20BD" + "</b>" + "\n" +
                ":rocket:<b>Сегодня таких " + todayCount +
                " шт на  " + df.format(todaySum) + "</b>" + "\u20BD" + "\n" +
                "Вчера таких <b>" + yestCount +
                " шт на " + df.format(yestSum) + "</b>" + "\u20BD" + "\n" +
                ":house:<b>" + order.getWarehouseName() + "</b>:arrow_right:" + order.getOblast() + "\n" +
                ":dart:<strong>СЕГОДНЯ заказов: " + (ordersCountoday - 1) + "&#128293;" + "</strong> на СУММУ" +
                ":moneybag: <b>" + df.format(sumToday) + "\u20BD" + "</b>" +
                "\n<i> Вчера заказано " + yestOrders.size() + " штук на " + df.format(yesterDayAllOrdSum) + "</i>" + "\u20BD");
        sendPhoto.setCaption(text);
        sendPhoto.setParseMode(ParseMode.HTML);

        sendPhoto.setChatId(Long.valueOf(668797978));
        execute(sendPhoto);
//        for(TgUser user : users){
//            sendPhoto.setChatId(user.getChatId());
//            execute(sendPhoto);
//        }

        sendCount++;
        if (sendCount == 10) {
            TimeUnit.SECONDS.sleep(2);
            sendCount = 0;
        }
//        for (Order o : orders) {
//            if (o.getDate().toLocalDate().equals(today.toLocalDate()) && !todayOrders.contains(o)) {
//                todayOrders.add(o);
//            } else if (o.getDate().toLocalDate().equals(yesterDay.toLocalDate()) && !yestOrders.contains(o)) {
//                yestOrders.add(o);
//                System.out.println("Добавлен вчерашний заказ");
//            }

//            } else System.out.println("Заказ уже был обработан: " + order.getOdid() + " " + order.getSupplierArticle() +
//                    " " + order.getDate());
//        }
        orderRepository.saveAll(orders);
        todayOrders.clear();
    }
}





public void firstMesseges(long chatId) throws TelegramApiException, IOException, InterruptedException {
//    if(LocalDate.now().isEqual(today.toLocalDate().plusDays(1))){
//        getGetOrder.getAllOrdersAtDate(today);
//        yesterDay = today;
//        today = today.plusDays(1);
//        sumToday = BigDecimal.ZERO;
//        todayOrders.clear();
//    }
    int firstCount= 1;
    BigDecimal sumTodayFirst = BigDecimal.ZERO;
    int sendCount=1;

    ArrayList<Order> firstOrders =(ArrayList< Order >)customOrderRepository.
            findOrderByDateBetweenOrderByDate(today.withHour(0).withMinute(0).withSecond(0),
                    today.minusMinutes(15));
    firstOrders.sort(Comparator.comparing(Order::getDate));
        for (Order order : firstOrders) {
                SendPhoto sendPhoto = new SendPhoto();
                InputFile photo = new InputFile(photoBaseService.getPhotoLink(order.getNmId()));
                sendPhoto.setChatId(chatId);
                sendPhoto.setPhoto(photo);
                int yestCount = (int) yestOrders.stream().filter(order1 -> order1.getBarcode().equals(order.getBarcode())).count();
                BigDecimal yestSum = BigDecimal.ZERO;
                List<Order> qnSum = yestOrders.stream().filter(order1 -> order1.getBarcode().equals(order.getBarcode())).collect(Collectors.toList());
                for (Order os : qnSum) {
                    yestSum = yestSum.add(os.getTotalPriceWithDisc());
                }
                int todayCount = (int) todayOrders.stream().filter(order1 -> order1.getBarcode().equals(order.getBarcode())).count()+1;
                BigDecimal todaySum = order.getTotalPriceWithDisc();
                List<Order> qnSumtoday = todayOrders.stream().filter(order1 -> order1.getBarcode().equals(order.getBarcode())).collect(Collectors.toList());
                for (Order os : qnSumtoday) {
                    todaySum = todaySum.add(os.getTotalPriceWithDisc());
                }
                String dt = "";
                if (order.getDate().toLocalDate().isBefore(today.toLocalDate())) {
                    dt = ":calendar:<b>Вчерашний заказ</b>\n";

                } else {
                    dt = ":calendar:<b>" + order.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "</b>\n" +
                            ":chart_with_upwards_trend:<b>Заказ: [" + firstCount + "]</b>\n";
                    sumTodayFirst = sumTodayFirst.add(order.getTotalPriceWithDisc());
                    firstCount++;
                }
                BigDecimal yesterDayAllOrdSum = BigDecimal.ZERO;
                for (Order order1 : yestOrders) {
                    yesterDayAllOrdSum = yesterDayAllOrdSum.add(order1.getTotalPriceWithDisc());
                }

            DecimalFormat df = new DecimalFormat("### ###,000");
                String cancel = "";
                String orderPrice = ":dollar:<b>" + df.format(order.getTotalPriceWithDisc()) + "\u20BD" + "</b>" + "\n" ;
                if(order.getIsCancel().equals("true")){
                    cancel = ":name_badge: <b>ЗАКАЗ ОТМЕНЕН!</b>:name_badge:\n";
                    orderPrice = ":stop_sign:<b>-" + df.format(order.getTotalPriceWithDisc()) + "\u20BD" + "</b>" + "\n" ;
                }



                String text = EmojiParser.parseToUnicode(dt + cancel + orderPrice +
                        ":package:<b>" + order.getSupplierArticle() + "</b>\n" +
                        "id:<a href=\"" + order.getWBLink() + "\">" + order.getNmId() + "</a>\n" +
                        ":gear:" + order.getBrand() + "\n" +
                        ":green_book:" + order.getCategory() + " / " +
                        order.getSubject() + "\n" +
                        ":triangular_ruler:" + order.getTechSize() + "\n" +
                        ":truck:Логистика: <b>" + order.getLogisticPrice() + "\u20BD" + "</b>" + "\n" +
                        ":rocket:<b>Сегодня таких " + todayCount +
                        " шт на  " + df.format(todaySum) + "</b>" + "\u20BD" + "\n" +
                        "Вчера таких <b>" + yestCount +
                        " шт на " + df.format(yestSum) + "</b>" + "\u20BD" + "\n" +
                        ":house:<b>" + order.getWarehouseName() + "</b>:arrow_right:" + order.getOblast() + "\n" +
                        ":dart:<strong>СЕГОДНЯ заказов: " + (firstCount - 1) + "&#128293;" + "</strong> на СУММУ" + "\n" +
                        ":moneybag: <b>" + df.format(sumTodayFirst) + "\u20BD" + "</b>" +
                        "\n<i> Вчера заказано <b>" + yestOrders.size() + "</b> штук на " + df.format(yesterDayAllOrdSum) + "</i>" + "\u20BD");
                sendPhoto.setCaption(text);

                sendPhoto.setParseMode(ParseMode.HTML);
                execute(sendPhoto);
                sendCount++;
                if(sendCount == 10){
                    TimeUnit.SECONDS.sleep(2);
                    sendCount = 0;
                }
        }
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
