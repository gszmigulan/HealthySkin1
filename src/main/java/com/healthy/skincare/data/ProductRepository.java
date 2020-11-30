package com.healthy.skincare.data;

import com.healthy.skincare.Product;
import com.healthy.skincare.SafeProduct;

import java.util.List;

public interface ProductRepository {
    Product save(Product product);
    Iterable<Product> findAll();
    Product findByBrandAndName(String brand, String name);
    Product findById(Long id);
    Boolean isLiked(Long id_user, Long id_product);
    Iterable<Product> getUserFaves(Long id);
    // cis MAX
    Iterable<Product> findByInci(SafeProduct safeProduct); // na podstawie cis
    Iterable<Product> findByInciW(SafeProduct safeProduct); // cis + wanted
    Iterable<Product> findByInciU(SafeProduct safeProduct); // cis + unwanted
    Iterable<Product> findByInciWU(SafeProduct safeProduct); // cis + wanted + unwanted
    //cis MIN
    //Product findById(String id);
    //Iterable<Product> findByBrand(String brand);
    //Iterable<Product> findByName(String name);
    //Iterable<Product> findByINCImin(SafeProduct safeProduct);
}
