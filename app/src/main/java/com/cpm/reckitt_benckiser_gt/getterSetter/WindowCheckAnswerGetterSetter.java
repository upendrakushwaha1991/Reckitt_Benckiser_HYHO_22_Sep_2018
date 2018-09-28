package com.cpm.reckitt_benckiser_gt.getterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jeevanp on 7/9/2018.
 */

public class WindowCheckAnswerGetterSetter {
    @SerializedName("Window_Checklist_Answer")
    @Expose
    private List<WindowChecklistAnswer> windowChecklistAnswer = null;

    public List<WindowChecklistAnswer> getWindowChecklistAnswer() {
        return windowChecklistAnswer;
    }

    public void setWindowChecklistAnswer(List<WindowChecklistAnswer> windowChecklistAnswer) {
        this.windowChecklistAnswer = windowChecklistAnswer;
    }
}
