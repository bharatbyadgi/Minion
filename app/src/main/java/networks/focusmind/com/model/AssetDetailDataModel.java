package networks.focusmind.com.model;

import com.google.gson.JsonObject;

public class AssetDetailDataModel extends BaseDataModel {

    private int assetid;
    private String name;
    private String description;
    private String make;
    private String model;
    private JsonObject addlProperties;
    private int amcid;
    private String movable;

    public AssetDetailDataModel(int id, String name, String description, String make, String model, JsonObject addlProperties, int amcId, String movable) {
        this.assetid = id;
        this.name = name;
        this.description = description;
        this.make = make;
        this.model = model;
        this.addlProperties = addlProperties;
        this.amcid = amcId;
        this.movable = movable;
    }

    public String getMovable() {
        return movable;
    }

    public void setMovable(String movable) {
        this.movable = movable;
    }

    public int getAssetid() {
        return assetid;
    }

    public void setAssetid(int assetid) {
        this.assetid = assetid;
    }

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

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public JsonObject getAddlProperties() {
        return addlProperties;
    }

    public void setAddlProperties(JsonObject addlProperties) {
        this.addlProperties = addlProperties;
    }

    public int getAmcid() {
        return amcid;
    }

    public void setAmcDetailDataModel(int amcId) {
        this.amcid = amcId;
    }

}
