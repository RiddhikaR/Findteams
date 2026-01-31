package com.findteams.findteams.scraper;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;

import org.openqa.selenium.support.ui.*;
import org.springframework.stereotype.Component;

import com.findteams.findteams.model.StudentProfileDetails;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;
import java.util.List;
@Component
public class VtopScrapper {

    private WebDriver driver;
    private WebDriverWait wait;

    public VtopScrapper() {
    WebDriverManager.chromedriver().setup();  // automatically downloads compatible ChromeDriver

    ChromeOptions options = new ChromeOptions();
    options.addArguments("--start-maximized");
    options.addArguments("--headless");
    options.addArguments("--disable-gpu");
    options.addArguments("--window-size=1920,1080");

    driver = new ChromeDriver(options);
    wait = new WebDriverWait(driver, Duration.ofSeconds(15));
}
    // Disable JS alerts, confirms, and prompts
    public void disableAlerts() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
            "window.alert = function(){};" +
            "window.confirm = function(){return true};" +
            "window.prompt = function(){return null};"
        );
    }

    public String loadLoginPageAndFillCredentials(String username, String password) {
        driver.get("https://vtopcc.vit.ac.in/vtop/login/");
        disableAlerts(); // Prevent any JS popup from blocking

        try {
            WebElement studentBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(@onclick,'submitForm')]")
                )
            );
            studentBtn.click();

            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
            driver.findElement(By.id("username")).sendKeys(username);
            driver.findElement(By.id("password")).sendKeys(password);

            java.util.List<WebElement> captchaImgs = driver.findElements(
                By.cssSelector("img.form-control.img-fluid.bg-light.border-0")
            );

            if (!captchaImgs.isEmpty()) {
                return captchaImgs.get(0).getAttribute("src");
            }
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean submitCaptchaAndLogin(String captchaValue) {
        try {
            if (captchaValue != null && !captchaValue.isEmpty()) {
                WebElement captchaBox = driver.findElement(By.id("captchaStr"));
                captchaBox.clear();
                captchaBox.sendKeys(captchaValue);
            }

            driver.findElement(By.id("submitBtn")).click();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public String checkLoginError() {
    try {
        List<WebElement> errors = driver.findElements(By.cssSelector(".text-danger.text-center strong"));
        if (!errors.isEmpty()) {
            return errors.get(0).getText();
        }
    } catch (Exception e) {
        // optional: log error
    }
    return null; // no error found
}



    // Navigate to My Info (like Android GitHub) and fetch reg no
    public StudentProfileDetails goToStudentProfile() {
        
            

            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                wait.until(ExpectedConditions.presenceOfElementLocated(By.id("MyPopup")));

                ((JavascriptExecutor) driver).executeScript(
                    " document.getElementById('MyPopup').style.display='none';" 
                    
                
                );
            } 
            catch (TimeoutException e) {
                // Popup never showed up, safe to ignore
            }

            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(
                    "document.querySelectorAll('.btn-group.dropend')[1]" +
                    ".querySelector('.dropdown-menu')" +
                    ".querySelector('a.dropdown-item.menuFontStyle.systemBtnMenu').click();"
            );
            String regNo=null;
            String course=null;
            String hostellerOrDayscholar=null;
            String email=null;
            try{
                WebDriverWait wait1=new WebDriverWait(driver,Duration.ofSeconds(5));
                wait1.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("label[for='no']")));

                JavascriptExecutor js2=(JavascriptExecutor)driver;
                regNo=(String)js2.executeScript(
                         "return document.querySelector('label[for=\"no\"]').textContent;"
                );
                course=(String)js2.executeScript(
                    "return document.querySelector('label[for=\"branchno\"]').textContent;"
                );
                hostellerOrDayscholar=(String)js2.executeScript(
                    "return document.getElementsByTagName('table')[0].getElementsByTagName('tr')[12].getElementsByTagName('td')[1].textContent;"
                );
                email=(String)js2.executeScript(
                         "return document.querySelector('label[for=\"vmail\"]').textContent;"
                );
            }
            catch(Exception e){
                e.printStackTrace();
            }
             JavascriptExecutor js3 = (JavascriptExecutor) driver;
            js3.executeScript(
                    "document.querySelectorAll('.btn-group.dropend')[7]" +
                    ".querySelector('.dropdown-menu')" +
                    ".querySelectorAll('a.dropdown-item.menuFontStyle.systemBtnMenu')[4].click();"
            );
            String cgpa=null;
            String name=null;
            try{
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
                wait.until(driver ->
                    (Long) ((JavascriptExecutor) driver).executeScript(
                        "return document.getElementsByClassName('col-md-12').length;"
                    ) == 5
                );


                

                JavascriptExecutor js4=(JavascriptExecutor)driver;
                cgpa=(String)js4.executeScript(
                    "return document.getElementsByClassName('col-md-12')[4].getElementsByTagName('tr')[1].getElementsByTagName('td')[2].textContent;"
                );
                name=(String)js4.executeScript(
                    "return document.getElementsByClassName('col-md-12')[0].getElementsByTagName('tr')[1].getElementsByTagName('td')[1].textContent;"
                );

            }
            catch(Exception e){
                e.printStackTrace();
            }



            
            
            StudentProfileDetails studentProfileDetails = new StudentProfileDetails();
            studentProfileDetails.setRegNo(regNo);
            studentProfileDetails.setCourse(course);
            studentProfileDetails.setHostellerOrDayscholar(hostellerOrDayscholar);
            studentProfileDetails.setEmail(email);
            studentProfileDetails.setCgpa(cgpa);
            studentProfileDetails.setName(name);
            return studentProfileDetails;

        
    }
}
