package com.healthy.skincare.data;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
public class JdbcIngredientRepository implements IngredientRepository {
    private JdbcTemplate jdbc;

    @Autowired
    public JdbcIngredientRepository(JdbcTemplate jdbc){
        this.jdbc = jdbc;
    }

    @Override
    public void save(Ingredient ingredient){
        // dodawać tylko jeśli składnik o tej nazwie jeszcze nie istnieje
        try {
            long IngredientId = saveIngredientInfo(ingredient);
            if(IngredientId == -1 ){ return;}
            ingredient.setRowid(IngredientId);
            save_name(ingredient);
            //return ingredient;
        }
        catch (IllegalStateException e){
            System.out.println("nie dodano");
        }
    }

    @Override
    public void deleteIngredient(String name){
        Ingredient ingredient = findByName(name);
        jdbc.update("delete from skladniki where name = ?" ,name);
        jdbc.update("delete from produkty_sklad where id_ingredient = ? ", ingredient.getRowid());
        jdbc.update("delete from skladniki_nazwy where id_ingredient = ?", ingredient.getRowid());
        jdbc.update("delete from user_unwanted where id_ingredient = ?", ingredient.getRowid());
        jdbc.update("delete from user_wanted where id_ingredient = ?", ingredient.getRowid());
    }

    @Override
    public  Iterable<Ingredient> getProductIngredients(Long id){
        return jdbc.query("select skladniki.rowid, skladniki.name, skladniki.type, skladniki.comed_max,skladniki.comed_min, skladniki.irr_max, skladniki.irr_min, skladniki.safety_max, skladniki.safety_min  from skladniki, produkty_sklad where skladniki.rowid = produkty_sklad.id_ingredient\n" +
                "and produkty_sklad.id_product = ? order by produkty_sklad.rowid", this::mapRowToIngredient, id);
       /* return jdbc.query("select rowid, name, type, comed_max, comed_min, irr_max, irr_min, " +
                " safety_max, safety_min from skladniki where rowid in ( select id_ingredient from " +
                " produkty_sklad where id_product = ? order by rowid ) ", this::mapRowToIngredient, id);*/
    }

    @Override
    public  Iterable<Ingredient> findAll(){
        return jdbc.query("select rowid, name, type, comed_max, comed_min," +
                " irr_max, irr_min, safety_max, safety_min  from skladniki", this::mapRowToIngredient);
    }

    @Override
    public Ingredient findById(String id){
        return jdbc.queryForObject("select rowid, name, type, comed_max, comed_min," +
                " irr_max, irr_min, safety_max, safety_min  from skladniki where rowid = ?", this::mapRowToIngredient, id);
    }

    @Override
    public Ingredient findByName(String id) {
       // Ingredient ing = new Ingredient("-1","",-1,-1,-1,-1,-1,-1);
        //try {
            return jdbc.queryForObject("select rowid, name, type, comed_max, comed_min," +
                    " irr_max, irr_min, safety_max, safety_min  from skladniki where " +
                    "rowid is (select id_ingredient from skladniki_nazwy where name = ? )", this::mapRowToIngredient, id);
        //} catch (EmptyResultDataAccessException e ){
        //    return ing;
        //}
    }

    private Ingredient mapRowToIngredient(ResultSet rs, int rowNum) throws SQLException {

        Ingredient i =  new Ingredient(
                /*Long.parseLong(rs.getString("rowid")),*/
                rs.getString("name"),
                rs.getString("type"),
                Integer.parseInt(rs.getString("comed_max")),
                Integer.parseInt(rs.getString("comed_min")),
                Integer.parseInt(rs.getString("irr_max")),
                Integer.parseInt(rs.getString("irr_min")),
                Integer.parseInt(rs.getString("safety_max")),
                Integer.parseInt(rs.getString("safety_min"))
        );
        i.setRowid(Long.parseLong(rs.getString("rowid")));
        return i;
    }
 /// tego nie potrzebuje
    @Override
    public Boolean isInBase(String name){
        try {
            Ingredient i = jdbc.queryForObject("select * from ingredients where name = ?", this::mapRowToIngredient, name);
            return true;
        }catch (IllegalStateException e){
            return false;
        }
    }

    private long saveIngredientInfo(Ingredient ingredient){
        //ingredient.setCreatedAt(new Date());
        try {
            Ingredient i = findByName(ingredient.name);
            if (i.getName().equals(ingredient.name)) {
                System.out.println("Znaleziono istejący już w bazie składnik o tej nazwie:" + i.getName());
                return -1;
            }//System.out.println("i: "+ i.getName());
        }catch (EmptyResultDataAccessException e){

        }
        PreparedStatementCreator psc =
                new PreparedStatementCreatorFactory(
                        "insert into skladniki(name, type, comed_max, comed_min, irr_max, irr_min, safety_max, safety_min)" +
                                "values(?, ?, ?, ?, ?, ?, ?, ?)",
                        Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER,Types.INTEGER, Types.INTEGER)
                .newPreparedStatementCreator(
                        Arrays.asList(
                                ingredient.getName(),
                                ingredient.getType(),
                                ingredient.getComed_max(),
                                ingredient.getComed_min(),
                                ingredient.getIrr_max(),
                                ingredient.getIrr_min(),
                                ingredient.getSafety_max(),
                                ingredient.getSafety_min()
                        )
                );
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbc.update(psc, keyHolder);
            return keyHolder.getKey().longValue();
        } catch (IllegalStateException e){
            return -1;
        }


    }
    private void save_name(Ingredient ingredient){
        jdbc.update("insert into skladniki_nazwy (name, id_ingredient) values (?,?)",
                ingredient.getName(), ingredient.getRowid()
        );
    }
    @Override
    public void save_next_name(String name, Long id){
        jdbc.update("insert into skladniki_nazwy (name, id_ingredient) values (?,?)",
                name, id
        );
    }
}
