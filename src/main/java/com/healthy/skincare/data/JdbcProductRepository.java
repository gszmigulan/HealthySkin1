package com.healthy.skincare.data;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

import com.healthy.skincare.Product;
import com.healthy.skincare.SafeProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.source.IterableConfigurationPropertySource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;
import com.healthy.skincare.web.Ingredient;

@Repository
public class JdbcProductRepository implements ProductRepository {
    private JdbcTemplate jdbc;

    @Autowired
    public JdbcProductRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;

    }

    @Override
    public Product save(Product product){
        long ProductId = saveProductInfo(product);
        product.setId(ProductId);
        for (Ingredient ingredient: product.getIngredients()){
            saveIngredientToProduct(ingredient, ProductId);
        }

        return product;
    }

    @Override
    public Product findByBrandAndName(String brand, String name){
        Product prod = new Product("-1", "-1", -1, -1, -1, -1, -1, -1);
        try {
            return jdbc.queryForObject("select rowid, brand, name, comed_max, comed_min, " +
                            "irr_max, irr_min, safety_max, safety_min from produkty where brand= ? and name = ? ",
                    this::mapRowToProduct, brand, name);
        }
        catch (EmptyResultDataAccessException e){
            return prod;
        }
    }

    @Override
    public Product findById(Long id){
        Product prod = new Product("-1", "-1", -1, -1, -1, -1, -1, -1);
        try {
            return jdbc.queryForObject("select rowid, brand, name, comed_max, comed_min, " +
                            "irr_max, irr_min, safety_max, safety_min from produkty where rowid = ? ",
                    this::mapRowToProduct,id);
        }
        catch (EmptyResultDataAccessException e){
            return prod;
        }
    }

    @Override
    public  Iterable<Product> findByInciW(SafeProduct safeProduct){
        String wn = IngrListToString(safeProduct.getWanted());
        int list_length = safeProduct.getWanted().size();
        String szukane_marka = "'%" +safeProduct.getBrand() + "%'";
        szukane_marka.toLowerCase();
        String szukane_nazwa = "'%" +safeProduct.getName() + "%'";
        szukane_nazwa.toLowerCase();


        return jdbc.query("select rowid, brand, name, comed_max, comed_min," +
                "irr_max, irr_min, safety_max, safety_min from produkty " +
                "where rowid in ( select id_product from produkty_sklad where " +
                "id_ingredient in " + wn + "group by id_product having count(id_product) = " +
                list_length +
                ") and rowid not in ( select id_product from produkty_sklad where id_ingredient in " +
                "( select id_ingredient from skladniki where comed_max > " + safeProduct.getComedogenic() +
                 " and irr_max > "+ safeProduct.getIrritation()+
                " and safety_max > "+ safeProduct.getSafety() +
                " and id_ingredient not in"+ wn +") ) " + " and brand like " + szukane_marka +
                " and name like " + szukane_nazwa + " ", this::mapRowToProduct);
    }
    @Override
    public Iterable<Product> findByInciU(SafeProduct safeProduct){ // uwanted
        //System.out.println("to jest strona fundbyy Un");
        // z tego un to lista niechcianych do wstawienia do query
        String un = IngrListToString(safeProduct.getUnwanted());
        //System.out.println("lista unwanted : " + un);
        String szukane_nazwa = "'%" +safeProduct.getName() + "%'";
        szukane_nazwa.toLowerCase();
        String szukane_marka = "'%" +safeProduct.getBrand() + "%'";
        szukane_marka.toLowerCase();

        return jdbc.query("select rowid, brand, name, comed_max, comed_min," +
                        " irr_max, irr_min, safety_max, safety_min  from produkty " +
                        "where comed_max <= " + safeProduct.getComedogenic() +
                        " and irr_max <= " +  safeProduct.getIrritation() +
                        " and safety_max <= " + safeProduct.getSafety() +
                        " and rowid not in (select id_product from produkty_sklad where id_ingredient in " +
                        un + " )" + " and brand like " + szukane_marka + " and name like " + szukane_nazwa + " ",
                this::mapRowToProduct);
    }
    @Override
    public Iterable<Product> findByInciWU(SafeProduct safeProduct){
        String un = IngrListToString(safeProduct.getUnwanted());
        String wn = IngrListToString(safeProduct.getWanted());
        int list_length = safeProduct.getWanted().size();
        String szukane_nazwa = "'%" +safeProduct.getName() + "%'";
        szukane_nazwa.toLowerCase();
        String szukane_marka = "'%" +safeProduct.getBrand() + "%'";
        szukane_marka.toLowerCase();

        return jdbc.query("select rowid, brand, name, comed_max, comed_min," +
                " irr_max, irr_min, safety_max, safety_min from produkty " +
                "where rowid in ( select id_product from produkty_sklad where " +
                "id_ingredient in " + wn + "group by id_product having count(id_product) = " +
                Integer.toString(list_length)+
                ") and rowid not in ( select id_product from produkty_sklad where id_ingredient in "+ un+ " ) " +
                " and rowid not in ( select id_product from produkty_sklad where id_ingredient in " +
                "( select id_ingredient from skladniki where comed_max > " + Integer.toString(safeProduct.getComedogenic()) +
                " and irr_max > "+ Integer.toString(safeProduct.getIrritation())+
                " and safety_max > "+ Integer.toString(safeProduct.getSafety()) +
                " and id_ingredient not in "+ wn +" ) ) and brand like " + szukane_marka +
                " and name like " + szukane_nazwa + " " , this::mapRowToProduct);
    }

    public String IngrListToString(List<Ingredient> ingr){
        String un = "(";
        System.out.println("ingrsiee: "+ ingr.size());
        String[] lista = new String[ingr.size()];
        int i =0;
        try {
            for (Ingredient unwant: ingr){
                lista[i] = Long.toString(unwant.getRowid());
                i++;
            }
            int j= 0;
            while( j < lista.length -1){
                un = un + lista[j] + ", ";
                j++;
            }
            un = un + lista[j] + ")";
            //System.out.println( "un: " + un);
        } catch (NullPointerException e){ System.out.println("error");}
        //System.out.println("unwanted: "+ safeProduct.setUnwante);
        return un;
    }

    @Override
    public Iterable<Product> findByInci(SafeProduct safeProduct){
        String szukane_nazwa = "'%" +safeProduct.getName() + "%'";
        szukane_nazwa.toLowerCase();
        String szukane_marka = "'%" +safeProduct.getBrand() + "%'";
        szukane_marka.toLowerCase();
        // szukane muszę dodać jeszcze do U, W  i UW
        //System.out.println( "name: " + szukane);
        return jdbc.query("select rowid, brand, name, comed_max, comed_min," +
                " irr_max, irr_min, safety_max, safety_min  from produkty " +
                "where comed_max <= " + safeProduct.getComedogenic() +
                " and irr_max <= " +  safeProduct.getIrritation() +
                " and safety_max <= " + safeProduct.getSafety() +
                " and  brand like " + szukane_marka + " and name like " + szukane_nazwa +" ",
                this::mapRowToProduct );
    }

    @Override
    public Iterable<Product> findAll(){
        return jdbc.query("select rowid, brand, name, comed_max, comed_min," +
                " irr_max, irr_min, safety_max, safety_min  from produkty", this::mapRowToProduct);

    }

    @Override
    public  Boolean isLiked(Long id_user, Long id_product){
        try{
            Product p = findById(jdbc.queryForObject("select id_product from user_liked where id_user = ? and id_product = ? ", this::mapRowToId, id_user, id_product));
            return true;
        }
        catch (EmptyResultDataAccessException e ){
            return false;
        }
    }

    @Override
    public Iterable<Product> getUserFaves(Long id){
        return jdbc.query("Select rowid, brand, name, comed_max, comed_min," +
                " irr_max, irr_min, safety_max, safety_min  from produkty where rowid in " +
                " ( select id_product from user_liked where id_user = ? ) ", this::mapRowToProduct, id );
    }

    private Product mapRowToProduct(ResultSet rs, int rowNum) throws SQLException {

        Product p =  new Product(
                rs.getString("brand"),
                rs.getString("name"),
                Integer.parseInt(rs.getString("comed_max")),
                Integer.parseInt(rs.getString("comed_min")),
                Integer.parseInt(rs.getString("irr_max")),
                Integer.parseInt(rs.getString("irr_min")),
                Integer.parseInt(rs.getString("safety_max")),
                Integer.parseInt(rs.getString("safety_min"))
        );
        p.setId(Long.parseLong(rs.getString("rowid")));
        return p;
    }

    private Long mapRowToId(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong("id_product");
    }

    public long saveProductInfo(Product product){
        PreparedStatementCreator psc =
                new PreparedStatementCreatorFactory(
                        "insert into produkty(brand,name, comed_max, comed_min, irr_max, irr_min, safety_max, safety_min)" +
                                "values(?,?, ?, ?, ?, ?, ?, ?)",
                        Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER,Types.INTEGER, Types.INTEGER)
                        .newPreparedStatementCreator(
                                Arrays.asList(
                                        product.getBrand(),
                                        product.getName(),
                                        product.getComed_max(),
                                        product.getComed_min(),
                                        product.getIrr_max(),
                                        product.getIrr_min(),
                                        product.getSafety_max(),
                                        product.getSafety_min()
                                )
                        );
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(psc, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public void saveIngredientToProduct(Ingredient ingredient, long productId){
        jdbc.update(
                "insert into produkty_sklad(id_product, id_ingredient)" +
                        "values (?, ?)",
                productId, ingredient.getRowid()
        );
    }
    @Override
    public void deleteProduct(String brand, String name){
        Product product = findByBrandAndName(brand, name);
        jdbc.update("delete from produkty where brand = ? and name = ?", brand, name);
        jdbc.update("delete from produkty_sklad where id_product = ? ", product.getId());
        jdbc.update("delete from user_comments where id_product = ? ", product.getId() );
        jdbc.update("delete from user_liked where id_product = ?", product.getId());
    }


}



