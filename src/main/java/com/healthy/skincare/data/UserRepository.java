package com.healthy.skincare.data;

import com.healthy.skincare.Product;
import com.healthy.skincare.User;
import com.healthy.skincare.web.Comment;
import com.healthy.skincare.web.Ingredient;

import java.sql.Date;
import java.util.List;

public interface UserRepository {
    User findByUsername(String username);
    User save(User user);
    void setUserCIS(Long id, int comed, int irr, int safety);
    //void deleteUserUW(Long id);
    void deleteUserWanted(Long id, Long id_ingredient);
    void deleteUserUnwanted(Long id,Long id_ingredient);
    void setUserWanted(Long id_user, String id_wanted);
    void setUserUnwanted(Long id_user, String id_unwanted);
    public void deleteComment(Long id);

    List<String> getWantedList(Long id);
    List<String> getUnwantedList(Long id);
    /*List<String>*/ void saveWantedList(Long id, List<Ingredient> wanted);
    /*List<String>*/ void saveUnwantedList(Long id, List<Ingredient> unwanted);
    void addToLiked(Long id_user, Long id_product);
    void deleteFromLiked(Long id_user, Long id_product);
    List<Comment> getComments(Long id_user, Long id_product);
    void addComment(Long id_user, Long id_product, String text, int score);

    //change
    //void saveCIS(Long id, int comed, int irr, int safety);
    // List<String> getFavorites(Long id);
    // List<String> getComments(Long id_user, id_product, String text);//
    // void addFavorites(Long id, Product product);
    // void deleteFavorites(Long id, Product product);
    // void addComment(Long id_user, id_product, String text);
    // void deleteComment(Long id_user, id_product);
}
