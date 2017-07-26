package com.huihuicai.wynne.indicator.bean;

/**
 * Created by ybm on 2017/7/26.
 */

public class ThirdBean {

    public int level = 2;
    private int id;
    private String name;

    public ThirdBean() {

    }

    public ThirdBean(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
