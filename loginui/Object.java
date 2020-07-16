package com.shashank.platform.loginui;
public class Object {
    private int id;
    private String name=null;
    private String date=null;
    private String price=null;
    private String sort=null;
    private String position=null;
    private String codevalue=null;
    private String imagepath=null;
    private String table=null;
    public  Object(){}
    public Object(int id, String name, String sort, String date,String price , String position, String codevalue,String table,String imagepath) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.price = price;
        this.sort = sort;
        this.position = position;
        this.codevalue = codevalue;
        this.imagepath = imagepath;
        this.table = table;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCodevalue() {
        return codevalue;
    }

    public void setCodevalue(String codevalue) {
        this.codevalue = codevalue;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
}
