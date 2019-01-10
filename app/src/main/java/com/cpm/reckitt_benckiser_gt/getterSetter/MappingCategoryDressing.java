
package com.cpm.reckitt_benckiser_gt.getterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MappingCategoryDressing {

    @SerializedName("State_Id")
    @Expose
    private Integer stateId;
    @SerializedName("Distributor_Id")
    @Expose
    private Integer distributorId;
    @SerializedName("Store_Type_Id")
    @Expose
    private Integer storeTypeId;
    @SerializedName("Category_Id")
    @Expose
    private Integer categoryId;
    @SerializedName("Category_Planogram_Imageurl")
    @Expose
    private String categoryPlanogramImageurl;

    public String getCategoryPlanogramImageurl() {
        return categoryPlanogramImageurl;
    }

    public void setCategoryPlanogramImageurl(String categoryPlanogramImageurl) {
        this.categoryPlanogramImageurl = categoryPlanogramImageurl;
    }

    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }

    public Integer getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(Integer distributorId) {
        this.distributorId = distributorId;
    }

    public Integer getStoreTypeId() {
        return storeTypeId;
    }

    public void setStoreTypeId(Integer storeTypeId) {
        this.storeTypeId = storeTypeId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

}
