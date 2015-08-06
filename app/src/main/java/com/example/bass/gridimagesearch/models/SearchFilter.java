package com.example.bass.gridimagesearch.models;

import java.io.Serializable;

/**
 * Created by bass on 2015/8/5.
 */
public class SearchFilter implements Serializable {

    public String size  = "";
    public String color = "";
    public String type  = "";
    public String site  = "";
    public String query = "";
    public int    start = 0;
}