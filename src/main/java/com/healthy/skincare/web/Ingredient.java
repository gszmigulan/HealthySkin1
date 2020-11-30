package com.healthy.skincare.web;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import java.lang.String;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Type;
import java.util.List;

@Data
//@RequiredArgsConstructor
//powinno być private
public class Ingredient {
    /*private*/ public /*final*/ long rowid;
    /*private*/public final String name;
    /*private*/public final  String type;
    /*private*/ public final int comed_max;
    /*private*/ public final int comed_min;
    /*private*/ public final int irr_max;
    /*private*/ public final int irr_min;
    /*private*/ public final int safety_max;
    /*private*/ public final int safety_min;


}

