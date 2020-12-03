package com.healthy.skincare;

import com.healthy.skincare.data.IngredientRepository;
import com.healthy.skincare.data.JdbcIngredientRepository;
import com.healthy.skincare.data.JdbcProductRepository;
import com.healthy.skincare.data.JdbcUserRepository;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import  org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import  org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.Cookie;
import javax.sql.DataSource;
import java.security.Key;
import java.security.Principal;
import java.util.concurrent.TimeUnit;


@RunWith(SpringRunner.class)
//@WebMvcTest(HomeController.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HomeControllerTest {


    private static HtmlUnitDriver browser;

    @LocalServerPort
    private int port;

    @Autowired
    TestRestTemplate rest;

    @BeforeClass
    public static void setup() {
        browser = new HtmlUnitDriver();
        browser.manage().timeouts()
                .implicitlyWait(10, TimeUnit.SECONDS);
    }

    @AfterClass
    public static void closeBrowser() {
        browser.quit();
    }

    //@Autowired
    //private MockMvc mockMvc;



   // @MockBean
    @Autowired
    private JdbcTemplate jdbcTemplate;

   // @MockBean
    @Autowired
    private DataSource dataSource;

    //@MockBean
    @Autowired
    private JdbcIngredientRepository ingredientRepository;
    //@MockBean
    @Autowired
    private JdbcUserRepository userRepository;

    @Autowired
    private JdbcProductRepository productRepository;

    public void HomeControllerTest(JdbcUserRepository userRepository, JdbcIngredientRepository ingredientRepository, JdbcProductRepository productRepository){
        this.userRepository = userRepository;
        this.ingredientRepository = ingredientRepository;
        this.productRepository = productRepository;
        jdbcTemplate.setDataSource(dataSource);
    }


    @Autowired
    private WebApplicationContext wac;

    WebDriverWait wait;

    @Test
    public void RegisterTest() throws Exception{
        browser.get(homePageUrl());
        assertLandedOnLoginPage();
        //doLogin("aga1", "aga");
        String login = "testeruser1998";
        String password = "testpassword";
        doRegistration(login, password);
        assertLandedOnLoginPage();
        doLogin(login, password);
        assertLandedOnHomePage();

        browser.findElementByCssSelector("form#parametersForm").submit();
        assertLandedOnParametersPage();
        // docelowo chce dodawać nowy dodany składnik do wanted
        Select se = new Select(browser.findElement(By.xpath("//*[@id='dropOperatorU']")));
        se.selectByVisibleText("testowy");
        //System.out.println("lista" + se.getAllSelectedOptions());
        browser.findElementByName("addUnwanted").click();//sendKeys(Keys.ENTER);
        browser.findElementByName("save").click();
        browser.findElementByName("home").click();

        //System.out.println(browser.findElementByName("powitanie").getText());
        assertEquals("Witaj "+ login + " !", browser.findElementByName("powitanie").getText());
        browser.findElementByCssSelector("form#logoutForm").submit();
        userRepository.deleteUser(login);
        //browser.findElementByCssSelector("form#parametersForm").submit();
        //assertEquals(parametersPageUrl(), browser.getCurrentUrl());

    }


    @Test
    public void AddTest(){
        /*browser.get(homePageUrl());
        assertLandedOnLoginPage();
        doLogin("aga1", "aga");
        assertLandedOnHomePage();*/
        getOnHomePage();
        /*browser.findElementByCssSelector("form#addForm").submit();
        assertLandedOnAddingPage();
        fillAddingform("testowa", "testowa", "testowy1");
        browser.findElementByName("addIngr").click();
        assertLandedOnIngrAddPage();
        setNewIngredient("testowy_składnik", "0", "0", "1");
        assertLandedOnAddingPage();
        browser.findElementByName("add").click();*/
        addTestProduct();

        assertLandedOnHomePage();
        browser.findElementByCssSelector("form#logoutForm").submit();
        assertLandedOnLoginPage();
        ingredientRepository.deleteIngredient("testowy1");
        productRepository.deleteProduct("testowa", "testowa");
    }

    @Test
    public void ChangeParametersTest() throws Exception{

        browser.get(homePageUrl());
        assertLandedOnLoginPage();
        doLogin("aga1", "aga");
        assertLandedOnHomePage();
        /*browser.findElementByCssSelector("form#parametersForm").submit();
        assertLandedOnParametersPage();
        // docelowo chce dodawać nowy dodany składnik do wanted
        Select se = new Select(browser.findElement(By.xpath("//*[@id='dropOperatorU']")));
        se.selectByVisibleText("testowy");
        //System.out.println("lista" + se.getAllSelectedOptions());
        browser.findElementByName("addUnwanted").click();//sendKeys(Keys.ENTER);
        browser.findElementByName("home").click();*/
        browser.findElementByCssSelector("form#faveForm").submit();
        assertLandedOnFavePage();
        browser.get(homePageUrl());
        assertLandedOnHomePage();
        browser.findElementByCssSelector("form#logoutForm").submit();
        assertLandedOnLoginPage();

    }

    @Test
    public void FindProductAndLike() {
        /*browser.get(homePageUrl());
        assertLandedOnHomePage();
        browser.findElementByCssSelector("form#logoutForm").submit();
        assertLandedOnLoginPage();*/
        //ingredientRepository.deleteIngredient("testowy1");
        //productRepository.deleteProduct("testowa", "testowa");
        getOnHomePage();
        addTestProduct();
        assertLandedOnHomePage();
        browser.findElementByCssSelector("form#designForm").submit();
        assertLandedonDesign();
        Select se = new Select(browser.findElement(By.xpath("//*[@id='dropOperatorW']")));
        se.selectByVisibleText("testowy1");
        browser.findElementByName("addWanted").click();
        browser.findElementByName("save").click();
        //System.out.println("JESTEM TU " + browser.getCurrentUrl());
        assertLandedOnProductListPage();
        Product product = productRepository.findByBrandAndName("testowa", "testowa");
        browser.findElementById(Long.toString(product.getId())).click();

        assertLandedOnProductPage();
        browser.findElementByName("like").click();


        browser.get(homePageUrl());
        assertLandedOnHomePage();
        browser.findElementByCssSelector("form#logoutForm").submit();
        assertLandedOnLoginPage();
        ingredientRepository.deleteIngredient("testowy1");
        productRepository.deleteProduct("testowa", "testowa");

    }

    private  void getOnHomePage(){
        browser.get(homePageUrl());
        assertLandedOnLoginPage();
        doLogin("aga1", "aga");
        assertLandedOnHomePage();
    }

    private void addTestProduct(){
        browser.findElementByCssSelector("form#addForm").submit();
        assertLandedOnAddingPage();
        fillAddingform("testowa", "testowa", "testowy1");
        browser.findElementByName("addIngr").click();
        assertLandedOnIngrAddPage();
        setNewIngredient("testowy_składnik", "0", "0", "1");
        assertLandedOnAddingPage();
        browser.findElementByName("add").click();
    }


    private String parametersPageUrl(){
        return homePageUrl() + "parameters";
    }
    private String addingPageUrl(){
        return homePageUrl() + "add";
    }
    private String addingIngredientPageUrl(){
        return addingPageUrl() + "/ingredient";
    }
    private String productPageUrl(){
        return homePageUrl() + "productList/one";
    }

    private String productsFiltredUrl(){
        return homePageUrl() + "productList/filtred";
    }

    private String homePageUrl() {
        return "http://localhost:" + port + "/";
    }
    private String designPageUrl(){
        return homePageUrl()  + "design";
    }
    private void assertLandedOnAddingPage(){
        assertEquals(addingPageUrl(),browser.getCurrentUrl());
    }
    private void assertLandedOnProductPage(){
        assertEquals(productPageUrl(), browser.getCurrentUrl());
    }

    private void assertLandedonDesign(){
        assertEquals(designPageUrl(), browser.getCurrentUrl());
    }

    private void assertLandedOnIngrAddPage(){
        assertEquals(addingIngredientPageUrl(), browser.getCurrentUrl());
    }
    private void assertLandedOnProductListPage(){
        assertEquals(productsFiltredUrl(), browser.getCurrentUrl());
    }

    private void assertLandedOnHomePage() {
        assertEquals(homePageUrl(), browser.getCurrentUrl());
    }
    private void assertLandedOnParametersPage() {
        assertEquals(parametersPageUrl(), browser.getCurrentUrl());
    }

    private void assertLandedOnLoginPage() {
        assertEquals(loginPageUrl(), browser.getCurrentUrl());
    }
    private void assertLandedOnFavePage(){
        assertEquals(favePageUrl(), browser.getCurrentUrl());
    }

    private String loginPageUrl() {
        return homePageUrl() + "login";
    }
    private String favePageUrl(){
        return homePageUrl() + "productList/favorite";
    }

    private String registrationPageUrl() {
        return homePageUrl() + "register";
    }

    private void setNewIngredient(String type, String comed, String irr, String safety){
        browser.findElementByName("types").sendKeys(type);
        browser.findElementByName("comed").sendKeys(comed);
        browser.findElementByName("irr").sendKeys(irr);
        browser.findElementByName("safety").sendKeys(safety);
        browser.findElementByName("new").click();
    }

    private void fillAddingform(String brand, String name, String inci){
        browser.findElementByName("brand").sendKeys(brand);
        browser.findElementByName("name").sendKeys(name);
        browser.findElementByTagName("textarea").sendKeys(inci);
        browser.findElementByName("add").click();

    }

    private void doLogin(String username, String password) {
        browser.findElementByName("username").sendKeys(username);
        browser.findElementByName("password").sendKeys(password);
        browser.findElementByCssSelector("form#loginForm").submit();
    }

    private void doRegistration(String username, String password) throws Exception {

        browser.findElementByLinkText("here").click();
        assertEquals(registrationPageUrl(), browser.getCurrentUrl());
        browser.findElementByName("username").sendKeys(username);
        browser.findElementByName("password").sendKeys(password);
        browser.findElementByName("confirm").sendKeys(password);
        browser.findElementByName("fullname").sendKeys("Test McTest");
        /*browser.findElementByName("comed").sendKeys("TX");
        browser.findElementByName("irr").sendKeys("12345");
        browser.findElementByName("safety").sendKeys("123-123-1234");*/
        browser.findElementByCssSelector("form#registerForm").submit();
        //assertEquals("ERROR" , browser.findElementByName("alert").getText());
        //System.out.println(browser.findElementByName("alert").isDisplayed());
        //assertTrue(browser.findElementByName("alert").isDisplayed());

        //
        //assertEquals("ERROR" , browser.findElementByName("alert").getText());
    }

}
