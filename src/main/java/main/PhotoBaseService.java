package main;

import main.model.PhotoBase;
import main.model.PhotoBaseRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
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
        if( photoBase.isPresent()){
            return  photoBase.get().getPhotoLink();
        }
        else {
            setPhotos(nmId);
            return photoBase.get().getPhotoLink();
        }
    }



    public  void setPhotos(String nmId) throws InterruptedException {
        if (photoBaseRepository.findById(nmId).isEmpty()) {
            System.setProperty("webdriver.chrome.driver", "selenium\\chromedriver.exe");
            WebDriver webDriver = new ChromeDriver();
            webDriver.get("https://www.wildberries.ru/catalog/" + nmId + "/detail.aspx?targetUrl=BP");

            Thread.sleep(1000);
//        webDriver.quit();
//        System.out.println(webDriver.getPageSource());
            Document document = Jsoup.parse(webDriver.getPageSource());
//        System.out.println(document.select("img"));
            webDriver.quit();
            Elements elem = document.select("img");
            String photoUrl =
                    elem.stream().filter(element -> element.attr("src").contains(nmId)).
                            filter(element -> element.attr("src").contains("big/1.jpg")).
                            findFirst().get().attr("src");
            PhotoBase photoBase = new PhotoBase();
            photoBase.setNmId(nmId);
            photoBase.setPhotoLink(photoUrl);
            addPhoto(photoBase);
        }
    }
}
