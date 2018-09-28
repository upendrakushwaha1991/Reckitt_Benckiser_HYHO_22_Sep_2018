
package com.cpm.reckitt_benckiser_gt.getterSetter;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MappingCategoryChecklistGetterSetter {

    @SerializedName("Mapping_Category_Checklist")
    @Expose
    private List<MappingCategoryChecklist> mappingCategoryChecklist = null;

    public List<MappingCategoryChecklist> getMappingCategoryChecklist() {
        return mappingCategoryChecklist;
    }

    public void setMappingCategoryChecklist(List<MappingCategoryChecklist> mappingCategoryChecklist) {
        this.mappingCategoryChecklist = mappingCategoryChecklist;
    }

}
