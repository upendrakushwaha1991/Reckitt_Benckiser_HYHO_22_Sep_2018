
package com.cpm.reckitt_benckiser_gt.getterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class JourneyPlan implements Serializable {

    @SerializedName("Store_Id")
    @Expose
    private Integer storeId;
    @SerializedName("Visit_Date")
    @Expose
    private String visitDate;

    @SerializedName("Distributor")
    @Expose
    private String distributorN;

    @SerializedName("Store_Name")
    @Expose
    private String storeName;
    @SerializedName("Address1")
    @Expose
    private String address1;
    @SerializedName("Address2")
    @Expose
    private String address2;
    @SerializedName("Landmark")
    @Expose
    private String landmark;
    @SerializedName("Pincode")
    @Expose
    private String pincode;
    @SerializedName("Contact_Person")
    @Expose
    private String contactPerson;
    @SerializedName("Contact_No")
    @Expose
    private String contactNo;
    @SerializedName("City")
    @Expose
    private String city;
    @SerializedName("Store_Type")
    @Expose
    private String storeType;
    @SerializedName("Store_Category")
    @Expose
    private String storeCategory;

    @SerializedName("State_Id")
    @Expose
    private String stateId;

    @SerializedName("Store_Type_Id")
    @Expose
    private Integer storeTypeId;

    @SerializedName("Store_Category_Id")
    @Expose
    private Integer storeCategoryId;


    @SerializedName("Reason_Id")
    @Expose
    private Integer reasonId;

    @SerializedName("Upload_Status")
    @Expose
    private String uploadStatus;

    @SerializedName("Geo_Tag")
    @Expose
    private String geoTag;

    @SerializedName("Distributor_Id")
    @Expose
    private Integer distributorId;

    @SerializedName("Weekly_Upload")
    @Expose
    private String weeklyUpload;

    @SerializedName("Classification_Id")
    @Expose
    private Integer classificationId;
    @SerializedName("Latitude")
    @Expose
    private Double latitude;
    @SerializedName("Longitude")
    @Expose
    private Double longitude;
    @SerializedName("GeoFencing")
    @Expose
    private Integer geoFencing;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getGeoFencing() {
        return geoFencing;
    }

    public void setGeoFencing(Integer geoFencing) {
        this.geoFencing = geoFencing;
    }

    public Integer getClassificationId() {
        return classificationId;
    }

    public void setClassificationId(Integer classificationId) {
        this.classificationId = classificationId;
    }

    public int getColourCode() {
        return colourCode;
    }

    public void setColourCode(int colourCode) {
        this.colourCode = colourCode;
    }

    private int colourCode;

    public String getWeeklyUpload() {
        return weeklyUpload;
    }

    public void setWeeklyUpload(String weeklyUpload) {
        this.weeklyUpload = weeklyUpload;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStoreType() {
        return storeType;
    }

    public void setStoreType(String storeType) {
        this.storeType = storeType;
    }

    public String getStoreCategory() {
        return storeCategory;
    }

    public void setStoreCategory(String storeCategory) {
        this.storeCategory = storeCategory;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }


    public Integer getStoreTypeId() {
        return storeTypeId;
    }

    public void setStoreTypeId(Integer storeTypeId) {
        this.storeTypeId = storeTypeId;
    }


    public Integer getStoreCategoryId() {
        return storeCategoryId;
    }

    public void setStoreCategoryId(Integer storeCategoryId) {
        this.storeCategoryId = storeCategoryId;
    }

    public Integer getReasonId() {
        return reasonId;
    }

    public void setReasonId(Integer reasonId) {
        this.reasonId = reasonId;
    }

    public String getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(String uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public String getGeoTag() {
        return geoTag;
    }

    public void setGeoTag(String geoTag) {
        this.geoTag = geoTag;
    }

    public Integer getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(Integer distributorId) {
        this.distributorId = distributorId;
    }

    public String getDistributorN() {
        return distributorN;
    }

    public void setDistributorN(String distributorN) {
        this.distributorN = distributorN;
    }


}
