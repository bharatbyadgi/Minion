package networks.focusmind.com.model;

public class EventDetailDataModel extends BaseDataModel {

    private int eventID;
    private String eventName;
    private String description;
    private String eventCreator;
    private String eventCreatedTime;
    private int eventExpiryInDays;
    private int eventTypeID;
    private boolean hasConversation;
    private boolean isCancelled;

    public String getEventScheduleTime() {
        return eventScheduleTime;
    }

    public void setEventScheduleTime(String eventScheduleTime) {
        this.eventScheduleTime = eventScheduleTime;
    }

    private String eventScheduleTime;

    public boolean getHasConversation() {
        return hasConversation;
    }

    public void setHasConversation(boolean hasConversation) {
        this.hasConversation = hasConversation;
    }

    public boolean getIsCancelled() {
        return isCancelled;
    }

    public void setIsCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
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

    public String getEventCreatedTime() {
        return eventCreatedTime;
    }

    public void setEventCreatedTime(String eventCreatedTime) {
        this.eventCreatedTime = eventCreatedTime;
    }

    public int getEventExpiryInDays() {
        return eventExpiryInDays;
    }

    public void setEventExpiryInDays(int eventExpiryInDays) {
        this.eventExpiryInDays = eventExpiryInDays;
    }

    public int getEventTypeID() {
        return eventTypeID;
    }

    public void setEventTypeID(int eventTypeID) {
        this.eventTypeID = eventTypeID;
    }


}
