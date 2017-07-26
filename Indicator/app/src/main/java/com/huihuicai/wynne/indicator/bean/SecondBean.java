package com.huihuicai.wynne.indicator.bean;

import java.util.List;

/**
 * Created by ybm on 2017/7/26.
 */

public class SecondBean {
    public int level = 1;
    private int id;
    private String name;
    private List<ThirdBean> list;

    public SecondBean() {

    }

    public SecondBean(int id, String name, List<ThirdBean> list) {
        this.id = id;
        this.name = name;
        this.list = list;
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

    public List<ThirdBean> getList() {
        return list;
    }

    public void setList(List<ThirdBean> list) {
        this.list = list;
    }
}
