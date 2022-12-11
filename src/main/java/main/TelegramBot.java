package main;

import com.vdurmont.emoji.EmojiParser;
import lombok.SneakyThrows;
import main.model.CustomOrderRepository;
import main.model.Order;
import main.model.OrderSum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    GetOrder getGetOrder;
    @Autowired
    CustomOrderRepository customOrderRepository;
    @Autowired
    GetSale getGetSale;
    @Autowired
    OrderService orderService;
    @Autowired
    PhotoBaseService photoBaseService;



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
                    startCommandReceived(chatId,update.getMessage().getChat().getFirstName());
                    ZonedDateTime znd = ZonedDateTime.now(ZoneId.systemDefault()).minusDays(0);
                    getGetOrder.getAllOrdersAtDate(znd);
                    getGetSale.getAllSalesAtDate(znd);
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

    @Override
    public String getBotUsername() {
        return botConfig.botName;
    }

    @Override
    public String getBotToken() {
        return botConfig.token;
    }
}
