package org.example;

import org.example.model.PhotoBase;
import org.example.model.PhotoBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PhotoBaseService {
    @Autowired
    PhotoBaseRepository photoBaseRepository;

    public void addPhoto(PhotoBase photoBase){
        photoBaseRepository.save(photoBase);
    }

    public String getPhotoLink(PhotoBase pb){
        Optional<PhotoBase> photoBase = photoBaseRepository.findById(pb.getNmId());
       if( photoBase.isPresent()){
            return  photoBase.get().getPhotoLink();
        }
       else {
           addPhoto(pb);
           return pb.getPhotoLink();
       }
    }

    public String getPhotoLink(String  nmId) throws InterruptedException {
        Optional<PhotoBase> photoBase = photoBaseRepository.findById(nmId);
        if (photoBase.isEmpty()) {
            setPhotos(nmId);
        }
        return  photoBase.get().getPhotoLink();
    }



    public  void setPhotos(String nmId) throws InterruptedException {
        if (photoBaseRepository.findById(nmId).isEmpty()) {
//            System.setProperty("webdriver.chrome.driver", "selenium\\chromedriver.exe");
////            ChromeOptions options = new ChromeOptions();
//            ChromeOptions options = new ChromeOptions();
//            options.addArguments("--remote-allow-origins=*");
//            options.addArguments("--disable-notifications");
//            options.addArguments("--disable-gpu");
//
//            options.addArguments("--disable-extensions");
//
//            options.addArguments("--no-sandbox");
//
//            options.addArguments("--disable-dev-shm-usage");
//            options.setCapability("browserVersion","111.0.5563.64");
//            options.setCapability("acceptInsecureCerts",true);
//            WebDriver webDriver = new ChromeDriver(options);
//
//            try {
//                webDriver.get("https://www.wildberries.ru/catalog/" + nmId + "/detail.aspx?targetUrl=BP");
//            } catch (Exception e) {
//                e.printStackTrace();
//                System.out.println("Ошибка браузера!");
//            }
//            Thread.sleep(1500);
////        webDriver.quit();
////        System.out.println(webDriver.getPageSource());
//            Document document = Jsoup.parse(webDriver.getPageSource());
////        System.out.println(document.select("img"));
//            webDriver.quit();
//            Elements elem = document.select("img");
//            String photoUrl="";
//            try {
//                 photoUrl =
//                        elem.stream().filter(element -> element.attr("src").contains(nmId)).
//                                filter(element -> element.attr("src").contains("big/1.jpg")).
//                                findFirst().get().attr("src");
//            }catch (NoSuchElementException e){
//                e.printStackTrace();
//                System.out.println("НЕТУ ФОТО ЗАКАЗА");
//            }
            PhotoBase photoBase = new PhotoBase();
            photoBase.setNmId(nmId);
//            if(photoUrl.isEmpty()){
//                photoUrl="//diamed.ru/wp-content/uploads/2020/11/nophoto.png";
//            }
            photoBase.setPhotoLink(getphotoLink(Integer.parseInt(nmId)));
            addPhoto(photoBase);
        }
    }

    public  void updatePhoto(String nmId) throws InterruptedException {
//            System.setProperty("webdriver.chrome.driver", "selenium\\chromedriver.exe");
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--remote-allow-origins=*");
//        WebDriver webDriver = new ChromeDriver(options);
//            webDriver.get("https://www.wildberries.ru/catalog/" + nmId + "/detail.aspx?targetUrl=BP");
//            Thread.sleep(2500);
//        System.out.println(" ПОлучение фото артикула "+ nmId);
//            Document document = Jsoup.parse(webDriver.getPageSource());
////        System.out.println(document.select("img"));
//            webDriver.quit();
//            Elements elem = document.select("img");
//
//                    if(elem.stream().filter(element -> element.attr("src").contains(nmId)).
//                            anyMatch(element -> element.attr("src").contains("big/1.jpg"))){
//                        String photoUrl = elem.stream().filter(element -> element.attr("src").contains(nmId)).
//                                filter(element -> element.attr("src").contains("big/1.jpg")).
//                                findFirst().get().attr("src");
                        PhotoBase photoBase = new PhotoBase();
                        photoBase.setNmId(nmId);
                        photoBase.setPhotoLink(getphotoLink(Integer.parseInt(nmId)));
                        addPhoto(photoBase);
                        System.out.println("Фото добавлено в базу");
//                    }
//                    else System.out.println("Ошибка! Нет ссылки на фото!");

    }
    public static String getphotoLink(int itemId){

        String basketNumber;

        int part = itemId/1000;
        int vol = itemId/100000;

        if (vol != 0 == vol >= 0 && vol <= 143) {
            basketNumber = "01";
        } else if (vol != 0 == vol >= 144 && vol <= 287) {
            basketNumber = "02";
        } else if (vol != 0 == vol >= 288 && vol <= 431) {
            basketNumber = "03";
        } else if (vol != 0 == vol >= 432 && vol <= 719) {
            basketNumber = "04";
        } else if (vol != 0 == vol >= 720 && vol <= 1007) {
            basketNumber = "05";
        } else if (vol != 0 == vol >= 1008 && vol <= 1061) {
            basketNumber = "06";
        } else if (vol != 0 == vol >= 1062 && vol <= 1115) {
            basketNumber = "07";
        } else if (vol != 0 == vol >= 1116 && vol <= 1169) {
            basketNumber = "08";
        } else if (vol != 0 == vol >= 1170 && vol <= 1313) {
            basketNumber = "09";
        } else if (vol != 0 == vol >= 1314 && vol <= 1601) {
            basketNumber = "10";
        } else if (vol != 0 == vol >= 1602 && vol <= 1655) {
            basketNumber = "11";
        } else if (vol != 0 == vol >= 1656 && vol <= 1919) {
            basketNumber = "12";
        } else if (vol != 0 == vol >= 1920 && vol <= 2045) {
            basketNumber = "13";
        } else if (vol != 0 == vol >= 2046 && vol <= 2189) {
            basketNumber ="14";
        } else if (vol != 0 == vol >= 2190 && vol <= 2405) {
            basketNumber ="15";
        } else if (vol != 0 == vol >= 2406 && vol <= 2641) {
            basketNumber = "16";
        } else {
            basketNumber ="17";
        }
        String url ="https://basket-"+basketNumber+".wbbasket.ru/vol"+vol+"/part"+part+"/"+itemId+"/images/big/1.webp";
        System.out.println(url);
        return url;
    }
}
