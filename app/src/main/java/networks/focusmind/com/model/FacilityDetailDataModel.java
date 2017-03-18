package networks.focusmind.com.model;

import com.google.gson.JsonObject;

public class FacilityDetailDataModel extends BaseDataModel {

    private String name;
    private String description;
    private int facilityID;
    private String facilityTypeID;
    private JsonObject technicalSpecs;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFacilityID() {
        return facilityID;
    }

    public void setFacilityID(int facilityID) {
        this.facilityID = facilityID;
    }

    public String getFacilityTypeID() {
        return facilityTypeID;
    }

    public void setFacilityTypeID(String facilityTypeID) {
        this.facilityTypeID = facilityTypeID;
    }

    public JsonObject getTechnicalSpecs() {
        return technicalSpecs;
    }

    public void setTechnicalSpecs(JsonObject technicalSpecs) {
        this.technicalSpecs = technicalSpecs;
    }


}
