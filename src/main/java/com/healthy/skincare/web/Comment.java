package com.healthy.skincare.web;

import lombok.Data;

import java.sql.Date;

@Data
public class Comment {
    public final Long id;
    public final int score;
    public final String text;
    public final Date date;
}
