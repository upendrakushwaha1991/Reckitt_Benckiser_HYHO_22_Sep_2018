package com.cpm.reckitt_benckiser_gt.getterSetter;

import java.util.ArrayList;

/**
 * Created by deepakp on 8/14/2017.
 */

public class WindowGetterSetter {

    String storeId;
    private String location_id = "";
    private String key_id = "0";
    private String brand;
    private String window;
    private String image = "";
    private String location = "";
    private String common_id;
    private String window_id;
    private String brand_group_id;

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    private String length;
    private String width;
    ArrayList<WindowBrandGetterSetter> window_brand = new ArrayList<>();

    public String getKey_id() {
        return key_id;
    }

    public void setKey_id(String key_id) {
        this.key_id = key_id;
    }

    public String getCommon_id() {
        return common_id;
    }

    public void setCommon_id(String common_id) {
        this.common_id = common_id;
    }


    public String getBrand_group_id() {
        return brand_group_id;
    }

    public void setBrand_group_id(String brand_group_id) {
        this.brand_group_id = brand_group_id;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getPlanogram_image() {
        return planogram_image;
    }

    public void setPlanogram_image(String planogram_image) {
        this.planogram_image = planogram_image;
    }

    private String planogram_image = "";


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isExist() {
        return isExist;
    }

    public void setExist(boolean exist) {
        isExist = exist;
    }

    boolean isExist = false;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getWindow_id() {
        return window_id;
    }

    public void setWindow_id(String window_id) {
        this.window_id = window_id;
    }

    public String getWindow() {
        return window;
    }

    public void setWindow(String window) {
        this.window = window;
    }


    public ArrayList<WindowBrandGetterSetter> getWindow_brand() {
        return window_brand;
    }

    public void setWindow_brand(ArrayList<WindowBrandGetterSetter> window_brand) {
        this.window_brand = window_brand;
    }
}
