
package com.cpm.reckitt_benckiser_gt.getterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WindowChecklistGetterSetter {

    @SerializedName("Window_Checklist")
    @Expose
    private List<WindowChecklist> windowChecklist = null;

    public List<WindowChecklist> getWindowChecklist() {
        return windowChecklist;
    }

    public void setWindowChecklist(List<WindowChecklist> windowChecklist) {
        this.windowChecklist = windowChecklist;
    }

}
