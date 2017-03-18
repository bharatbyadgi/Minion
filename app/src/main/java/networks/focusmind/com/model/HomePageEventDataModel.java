package networks.focusmind.com.model;


public class HomePageEventDataModel {

    private String eventID;
    private String eventName;
    private String description;
    private String eventCreator;
    private String eventScheduleTime;

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventCreator() {
        return eventCreator;
    }

    public void setEventCreator(String eventCreator) {
        this.eventCreator = eventCreator;
    }

    public String getEventScheduleTime() {
        return eventScheduleTime;
    }

    public void setEventScheduleTime(String eventScheduleTime) {
        this.eventScheduleTime = eventScheduleTime;
    }

}
