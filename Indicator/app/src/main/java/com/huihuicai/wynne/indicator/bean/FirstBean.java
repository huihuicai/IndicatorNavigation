package com.huihuicai.wynne.indicator.bean;

import java.util.List;

/**
 * Created by ybm on 2017/7/26.
 */

public class FirstBean {

    public int level = 0;
    private int id;
    private String name;
    private List<SecondBean> list;

    public FirstBean() {

    }

    public FirstBean(int id, String name, List<SecondBean> list) {
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

    public List<SecondBean> getList() {
        return list;
    }

    public void setList(List<SecondBean> list) {
        this.list = list;
    }
}
