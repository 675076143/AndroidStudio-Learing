package cn.edu.fjzzit.weatherforecast;

import org.litepal.crud.LitePalSupport;

public class LifeStyle extends LitePalSupport {
    private String type;//生活指数类型
    private String brf;//生活指数简介
    private String txt;//生活指数详细描述

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBrf() {
        return brf;
    }

    public void setBrf(String brf) {
        this.brf = brf;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public LifeStyle() {
    }

    public LifeStyle(String type, String brf, String txt) {
        this.type = type;
        this.brf = brf;
        this.txt = txt;
    }
}
