
package com.cpm.reckitt_benckiser_gt.getterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WindowMasterGetterSetter {

    @SerializedName("Window_Master")
    @Expose
    private List<WindowMaster> windowMaster = null;

    public List<WindowMaster> getWindowMaster() {
        return windowMaster;
    }

    public void setWindowMaster(List<WindowMaster> windowMaster) {
        this.windowMaster = windowMaster;
    }
}
