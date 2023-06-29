package main;

import main.model.PhotoBase;
import main.model.PhotoBaseRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;
import java.util.NoSuchElementException;
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
            System.setProperty("webdriver.chrome.driver", "selenium\\chromedriver.exe");
//            ChromeOptions options = new ChromeOptions();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--disable-notifications");
            options.addArguments("--disable-gpu");

            options.addArguments("--disable-extensions");

            options.addArguments("--no-sandbox");

            options.addArguments("--disable-dev-shm-usage");
            options.setCapability("browserVersion","111.0.5563.64");
            options.setCapability("acceptInsecureCerts",true);
            WebDriver webDriver = new ChromeDriver(options);

            try {
                webDriver.get("https://www.wildberries.ru/catalog/" + nmId + "/detail.aspx?targetUrl=BP");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Ошибка браузера!");
            }
            Thread.sleep(1500);
//        webDriver.quit();
//        System.out.println(webDriver.getPageSource());
            Document document = Jsoup.parse(webDriver.getPageSource());
//        System.out.println(document.select("img"));
            webDriver.quit();
            Elements elem = document.select("img");
            String photoUrl="";
            try {
                 photoUrl =
                        elem.stream().filter(element -> element.attr("src").contains(nmId)).
                                filter(element -> element.attr("src").contains("big/1.jpg")).
                                findFirst().get().attr("src");
            }catch (NoSuchElementException e){
                e.printStackTrace();
                System.out.println("НЕТУ ФОТО ЗАКАЗА");
            }
            PhotoBase photoBase = new PhotoBase();
            photoBase.setNmId(nmId);
            if(photoUrl.isEmpty()){
                photoUrl="https://diamed.ru/wp-content/uploads/2020/11/nophoto.png";
            }
            photoBase.setPhotoLink(photoUrl);
            addPhoto(photoBase);
        }
    }

    public  void updatePhoto(String nmId) throws InterruptedException {
            System.setProperty("webdriver.chrome.driver", "selenium\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        WebDriver webDriver = new ChromeDriver(options);
            webDriver.get("https://www.wildberries.ru/catalog/" + nmId + "/detail.aspx?targetUrl=BP");
            Thread.sleep(2500);
        System.out.println(" ПОлучение фото артикула "+ nmId);
            Document document = Jsoup.parse(webDriver.getPageSource());
//        System.out.println(document.select("img"));
            webDriver.quit();
            Elements elem = document.select("img");

                    if(elem.stream().filter(element -> element.attr("src").contains(nmId)).
                            anyMatch(element -> element.attr("src").contains("big/1.jpg"))){
                        String photoUrl = elem.stream().filter(element -> element.attr("src").contains(nmId)).
                                filter(element -> element.attr("src").contains("big/1.jpg")).
                                findFirst().get().attr("src");
                        PhotoBase photoBase = new PhotoBase();
                        photoBase.setNmId(nmId);
                        photoBase.setPhotoLink(photoUrl);
                        addPhoto(photoBase);
                        System.out.println("Фото добавлено в базу");
                    }
                    else System.out.println("Ошибка! Нет ссылки на фото!");

    }
}
