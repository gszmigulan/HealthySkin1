package com.healthy.skincare;

import com.healthy.skincare.data.IngredientRepository;
import com.healthy.skincare.web.Ingredient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;



import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import java.util.List;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {
    private final IngredientRepository ingredientRepository;

    @Autowired
    public  DemoApplication(IngredientRepository ingredientRepository){
        this.ingredientRepository = ingredientRepository;
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String FILE_NAME = "C:\\Users\\gszmi\\Desktop\\aplikacja\\skincare\\src\\main\\resources\\static\\data\\skladniki\\produkty.xlsx";

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
    @Override
    public void run(String... args) throws Exception {
        //jdbcTemplate.execute("DROP TABLE IF EXISTS typ_skladnika");
        // dodaje jeszcze raz tablice
        //createDatabaseEmpty();
       /* otwieranie pliku .xlsx  */
        //addIngredients(new FileInputStream(new File(FILE_NAME)));
    }
     /*   public void createDatabaseEmpty(){
        // TABLICA SKŁADNIKÓW
        jdbcTemplate.execute("DROP TABLE IF EXISTS skladniki");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS skladniki(name VARCHAR(100) , " +
                " type VARCHAR(100) ," +
                " comed_max INTEGER , comed_min INTEGER, irr_max INTEGER, irr_min INTEGER, " +
                " safety_max INTEGER , safety_min INTEGER )");

        // TABLICA NAZW SKŁADNIKÓW
        jdbcTemplate.execute("DROP TABLE IF EXISTS skladniki_nazwy");
        jdbcTemplate.execute("CREATE  TABLE IF NOT EXISTS skladniki_nazwy (name VARCHAR (100) ," +
                " id_ingredient INTEGER )");

        // TABLICA PRODUKTÓW
        jdbcTemplate.execute("DROP TABLE IF EXISTS produkty ");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS produkty(brand VARCHAR(100), name VARCHAR(100)," +
                " comed_max INTEGER , comed_min INTEGER, irr_max INTEGER, irr_min INTEGER, " +
                " safety_max INTEGER , safety_min INTEGER )");

        //TABLICA SKŁADÓW PRODUKTÓW
        jdbcTemplate.execute("DROP TABLE IF EXISTS  produkty_sklad");
        jdbcTemplate.execute("CREATE  TABLE IF NOT EXISTS produkty_sklad(id_product INTEGER , id_ingredient INTEGER )");

        // TABLICA UŻYTKOWNIKÓW
        jdbcTemplate.execute("drop table if exists user ");
        // e-mail
        jdbcTemplate.execute("create table if not exists user(username VARCHAR(100) , " +
                " password VARCHAR(100) , fullname varchar(100) , comed integer , irr INTEGER , safety integer , enabled integer ) ");

        //TABLICA AUTORYZACYJNA URZYTKOWNIKÓW
        jdbcTemplate.execute("drop table if exists user_autoryzacja");
        jdbcTemplate.execute("create table if not exists user_autoryzacja(username VARCHAR(100), autoryzacja VARCHAR(100) )");


        //WANTED I UNWANTED
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS user_wanted(id_user INTEGER ," +
                " id_ingredient INTEGER )");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS user_unwanted( id_user INTEGER ," +
                " id_ingredient INTEGER )");

    }
    public void addIngredients(FileInputStream excelFile){
        try {

            //FileInputStream excelFile = new FileInputStream(new File(FILE_NAME));
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();

            while (iterator.hasNext()) {
                // zamienić to na obiekt obiekt comed_irr_safe z polami dodatkowymi
                String nazwa = " ";
                String zastosowanie = " ";
                int com_min = -1;
                int com_max = -1;
                int irr_min = -1;
                int irr_max = -1;
                int safe_min = -1;
                int safe_max = -1;
                int pole = 0;
                Row currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();

                while (cellIterator.hasNext()) {

                    Cell currentCell = cellIterator.next();
                    pole++;
                    //getCellTypeEnum shown as deprecated for version 3.15
                    //getCellTypeEnum ill be renamed to getCellType starting from version 4.0
                    if (currentCell.getCellTypeEnum() == CellType.STRING) {
                        String tmp = currentCell.getStringCellValue().trim();
                        // tmp.replaceAll("\\n", "");
                        tmp.replaceAll("\\s+", "");
                        String[] elements = tmp.split("[\\n,]");
                        String file = "";

                        for(int i =0;i < elements.length ;i++){
                            file = file + elements[i].trim();
                            //System.out.print(pole +". " + elements[i].trim() + ",");

                        }
                        System.out.print(pole + ". " + file + " ");
                        if(pole == 1){
                            nazwa = file.toLowerCase();

                        }
                        if(pole == 2){
                            zastosowanie= file.toLowerCase();
                        }
                        if(pole == 3){
                            try{
                                String[] range = file.split("-");
                                if(range.length > 1){
                                    com_min = Integer.parseInt(range[0]);
                                    com_max = Integer.parseInt(range[1]);
                                }
                                else {
                                    com_min = Integer.parseInt(range[0]);
                                    com_max = Integer.parseInt(range[0]);
                                }

                            }
                            catch (NumberFormatException e ){
                                System.out.print("błąd w podanycm numerze");
                            }
                        }
                        if(pole == 4){
                            try{
                                String[] range = file.split("-");
                                if(range.length > 1){
                                    irr_min = Integer.parseInt(range[0]);
                                    irr_max = Integer.parseInt(range[1]);
                                }
                                else {
                                    irr_min = Integer.parseInt(range[0]);
                                    irr_max = Integer.parseInt(range[0]);
                                }

                            }
                            catch (NumberFormatException e ){
                                System.out.print("błąd wpodanycm numerze");
                            }
                        }
                        if(pole == 5){
                            try{
                                String[] range = file.split("-");
                                if(range.length > 1){
                                    safe_min = Integer.parseInt(range[0]);
                                    safe_max = Integer.parseInt(range[1]);
                                }
                                else {
                                    safe_min = Integer.parseInt(range[0]);
                                    safe_max = Integer.parseInt(range[0]);
                                }

                            }
                            catch (NumberFormatException e ){
                                System.out.print("błąd wpodanycm numerze");
                            }
                        }

                        //System.out.print(tmp + "; ");
                    } else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                        System.out.print(currentCell.getNumericCellValue() + "--");
                    }

                }
                System.out.println( "\n"+ nazwa + ", " + zastosowanie + ", " + com_max + " " + com_min + " " + irr_max + " " + irr_min + " " + safe_max + " " + safe_min + "\n" );
                Ingredient ingr = new Ingredient(
                        nazwa,
                        zastosowanie,
                        com_max,
                        com_min,
                        irr_max,
                        irr_min,
                        safe_max,
                        safe_min);
                if(nazwa.length() > 2){
                    // tylko jeśli takiej nazwy jeszcze nie ma w bazie
                    //try{
                    //   Ingredient ingredient2 = ingredientRepository.findByName(nazwa);
                    //}
                    //catch (NullPointerException e){

                    //if(ingredientRepository.isInBase(nazwa) == false){
                    //try {
                        ingredientRepository.save(ingr);//}
                    //} catch (IllegalStateException e){
                     //   System.out.println( "jest już taki");
                    //}
                   // else {
                   //     System.out.print("już istnieje: "+ nazwa);
                    //}


                    //}
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/


}
