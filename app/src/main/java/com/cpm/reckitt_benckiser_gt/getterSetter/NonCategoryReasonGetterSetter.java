
package com.cpm.reckitt_benckiser_gt.getterSetter;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NonCategoryReasonGetterSetter {

    @SerializedName("Non_Category_Reason")
    @Expose
    private List<NonCategoryReason> nonCategoryReason = null;

    public List<NonCategoryReason> getNonCategoryReason() {
        return nonCategoryReason;
    }

    public void setNonCategoryReason(List<NonCategoryReason> nonCategoryReason) {
        this.nonCategoryReason = nonCategoryReason;
    }

}
