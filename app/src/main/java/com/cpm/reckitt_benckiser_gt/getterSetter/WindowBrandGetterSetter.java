package com.cpm.reckitt_benckiser_gt.getterSetter;

/**
 * Created by upendrak on 14-09-2017.
 */

public class WindowBrandGetterSetter {
    private  String brand_id="";
    private  String brand="";
    private  String stock="";
    private  String Common_id;
    private  String key_id="0";

    public String getKey_id() {
        return key_id;
    }

    public void setKey_id(String key_id) {
        this.key_id = key_id;
    }

    public String getCommon_id() {
        return Common_id;
    }

    public void setCommon_id(String common_id) {
        Common_id = common_id;
    }

    public String getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(String brand_id) {
        this.brand_id = brand_id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }
}
