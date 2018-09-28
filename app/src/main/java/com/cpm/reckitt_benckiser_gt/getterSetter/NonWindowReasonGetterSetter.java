
package com.cpm.reckitt_benckiser_gt.getterSetter;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NonWindowReasonGetterSetter {

    @SerializedName("Non_Window_Reason")
    @Expose
    private List<NonWindowReason> nonWindowReason = null;

    public List<NonWindowReason> getNonWindowReason() {
        return nonWindowReason;
    }

    public void setNonWindowReason(List<NonWindowReason> nonWindowReason) {
        this.nonWindowReason = nonWindowReason;
    }

}
