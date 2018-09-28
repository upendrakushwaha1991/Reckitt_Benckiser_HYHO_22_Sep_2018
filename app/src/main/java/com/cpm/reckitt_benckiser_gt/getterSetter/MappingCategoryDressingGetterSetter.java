
package com.cpm.reckitt_benckiser_gt.getterSetter;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MappingCategoryDressingGetterSetter {

    @SerializedName("Mapping_Category_Dressing")
    @Expose
    private List<MappingCategoryDressing> mappingCategoryDressing = null;

    public List<MappingCategoryDressing> getMappingCategoryDressing() {
        return mappingCategoryDressing;
    }

    public void setMappingCategoryDressing(List<MappingCategoryDressing> mappingCategoryDressing) {
        this.mappingCategoryDressing = mappingCategoryDressing;
    }

}
