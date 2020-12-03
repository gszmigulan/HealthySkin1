package com.healthy.skincare.data;

import com.healthy.skincare.User;
import com.healthy.skincare.web.Comment;
import com.healthy.skincare.web.Ingredient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@Repository
public class JdbcUserRepository implements UserRepository {

    private JdbcTemplate jdbc;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbc){
        this.jdbc = jdbc;
    }


    @Override
    public User findByUsername(String username){

        User u = new User("-1", "-1","",-1,-1,-1);
        try {
            return jdbc.queryForObject("select rowid, username, password, fullname, comed, " +
                    " irr, safety from user where username = ?", this::mapRowToUser, username);
        }
        catch (EmptyResultDataAccessException e){
            return u;
        }
    }

    @Override
    public void addToLiked(Long id_user, Long id_product){
        jdbc.update("insert into user_liked values(?,?) ", id_user, id_product);
    }
    @Override
    public void deleteFromLiked(Long id_user, Long id_product){
        jdbc.update("DELETE  from user_liked where id_user = ? and id_product = ?", id_user, id_product);
    }

    @Override
    public List<Comment> getComments(Long id_user, Long id_product){
        return jdbc.query("select rowid, id_user, id_product, text, score, add_date from user_comments where "+
                " id_user = ? and id_product = ? ", this::mapRowToComment, id_user, id_product);
    }

    @Override
    public void deleteComment(Long id){
        jdbc.update("DELETE from user_comments where rowid = ?", id);
    }

    @Override
    public  void deleteUser(String username)
    {   User user = findByUsername(username);
        jdbc.update("delete from user where username = ?", username);
        jdbc.update("delete from user_autoryzacja where username = ?", username);
        jdbc.update("delete from user_comments where id_user = ?", user.getId());
        jdbc.update("delete from user_liked where id_user = ? ", user.getId());
        jdbc.update("delete from user_wanted where id_user = ? ", user.getId());
        jdbc.update("delete from user_unwanted where id_user = ? ", user.getId());
    }

    @Override
    public void addComment(Long id_user, Long id_product, String text, int score){
        Date data = new Date(Calendar.getInstance().getTime().getTime());
        jdbc.update("insert into user_comments values(?,?,?,?,?)", id_user, id_product, text, score, data);

    }

    @Override
    public void setUserCIS(Long id, int comed, int irr, int safety){
        jdbc.update("update user set comed = ? where rowid = ? ", comed, id);
        jdbc.update("update user set irr = ? where rowid = ? ", irr, id);
        jdbc.update("update user set safety = ? where rowid = ? ", safety, id);
    }

    @Override
    public  void deleteUserUnwanted(Long id, Long id_ingredient){

        jdbc.update("delete from user_unwanted where id_user = ? and id_ingredient = ? ", id, id_ingredient);

    }
    @Override
    public void  deleteUserWanted(Long id, Long id_ingredient){
        jdbc.update("delete from user_wanted where id_user = ? and id_ingredient = ? ", id, id_ingredient);
    }


    @Override
    public void setUserWanted(Long id_user, String id_wanted){
        jdbc.update("insert into user_wanted values(?,?) ", id_user, id_wanted);
    }

    @Override
    public void setUserUnwanted(Long id_user, String id_unwanted){
        jdbc.update("insert into user_unwanted values(?,?) ", id_user, id_unwanted);
    }


    @Override
    public User save(User user){
        long userId = saveUserInfo(user);
        user.setId(userId);
        jdbc.update(" insert into user_autoryzacja values(?,?) ", user.getUsername(), "ROLE_USER" );
        return user;
    }

    public long saveUserInfo(User user){
        PreparedStatementCreatorFactory preparedStatementCreatorFactory =
                new PreparedStatementCreatorFactory(
                        "insert into user( username ,password , fullname, comed, irr, safety, enabled) " +
                                "values(?,?,?, ?, ?, ?,?)",
                        Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER

                );
        preparedStatementCreatorFactory.setReturnGeneratedKeys(Boolean.TRUE);

        PreparedStatementCreator psc =
                preparedStatementCreatorFactory.newPreparedStatementCreator(
                        Arrays.asList(
                                user.getUsername(),
                                user.getPassword(),
                                user.getFullname(),
                                user.getComed(),
                                user.getIrr(),
                                user.getSafety(),
                                1
                        )
                );
        /*PreparedStatementCreator psc =
                new PreparedStatementCreatorFactory(
                        "insert into user( username ,password , fullname, comed, irr, safety, enabled) " +
                                "values(?,?,?, ?, ?, ?,?)",
                        Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER)
                        .newPreparedStatementCreator(
                                Arrays.asList(
                                        user.getUsername(),
                                        user.getPassword(),
                                        user.getFullname(),
                                        user.getComed(),
                                        user.getIrr(),
                                        user.getSafety(),
                                        1
                                )
                        );*/
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(psc, keyHolder);
        return keyHolder.getKey().longValue();
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {

        User u =  new User(
                /*Long.parseLong(rs.getString("rowid")),*/
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("fullname"),
                rs.getInt("comed"),
                rs.getInt("irr"),
                rs.getInt("safety")

        );
        u.setId(Long.parseLong(rs.getString("rowid")));
       // u.setComed(Integer.parseInt(rs.getString("comed")));
       // u.setIrr(Integer.parseInt(rs.getString("irr")));
       /// u.setSafety(Integer.parseInt(rs.getString("safety")));



        // add wanted and unwanet list
        //CHWILOWO
        /*List<String> wanted = getWantedList(u.getId());
        List<String> unwanted = getUnwantedList(u.getId());
        u.setWanted(wanted);
        u.setUnwanted(unwanted);*/


        //u.getAuthorities(rs.getType("auth"));
        // jeszcze dodam listę wanted i unwanted // w interejcie getunwanted(usrid) , getwanted(id)

        return u;
    }

    private Comment mapRowToComment(ResultSet rs, int rowNum) throws SQLException {

        Comment comment = new Comment(
                rs.getLong("rowid"),
                rs.getInt("score"),
                rs.getString("text"),
                rs.getDate("add_date"));

        return comment;
    }

    @Override
    public List<String> getWantedList(Long id){
        return jdbc.query( "select id_ingredient from user_wanted "+
                "where id_user = ?", this::mapRowToIngr, id);
    }

    @Override
    public List<String> getUnwantedList(Long id){
        return jdbc.query( "select id_ingredient from user_unwanted "+
                "where id_user = ?", this::mapRowToIngr, id);
    }

    @Override
    public void saveWantedList(Long id, List<Ingredient> wanted){
        // z listy wanted usówam wszystko co było pod id = $id i zapisuje nowe
        jdbc.update("delete * from wanted where id_user = ?", id);
        // dodaje wszystie id składników z nowej listy wanted do bazy
        for(Ingredient ingr: wanted){
            jdbc.update("insert into wanted(id_user, id_ingredient) values(?,?)", id, ingr.getRowid());
        }
    }

    @Override
    public void saveUnwantedList(Long id, List<Ingredient> wanted){
        // z listy wanted usówam wszystko co było pod id = $id i zapisuje nowe
        jdbc.update("delete * from unwanted where id_user = ?", id);
        // dodaje wszystie id składników z nowej listy wanted do bazy
        for(Ingredient ingr: wanted){
            jdbc.update("insert into unwanted(id_user, id_ingredient) values(?,?)", id, ingr.getRowid());
        }
    }

    private String mapRowToIngr(ResultSet rs, int rowNum) throws SQLException {
        return rs.getString("id_ingredient");
    }
}
