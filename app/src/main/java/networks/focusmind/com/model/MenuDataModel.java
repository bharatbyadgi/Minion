package networks.focusmind.com.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class MenuDataModel extends BaseDataModel {


    private int noOfWidgets;
    private JsonArray widgetType;
    private JsonObject widgets;

    public int getNoOfWidgets() {
        return noOfWidgets;
    }

    public void setNoOfWidgets(int noOfWidgets) {
        this.noOfWidgets = noOfWidgets;
    }

    public JsonArray getWidgetType() {
        return widgetType;
    }

    public void setWidgetType(JsonArray widgetType) {
        this.widgetType = widgetType;
    }

    public JsonObject getWidgets() {
        return widgets;
    }

    public void setWidgets(JsonObject widgets) {
        this.widgets = widgets;
    }


}
