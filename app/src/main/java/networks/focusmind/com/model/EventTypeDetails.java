package networks.focusmind.com.model;

/**
 * Created by bharat on 25/3/17.
 */

public class EventTypeDetails extends  BaseDataModel {

    private String[] associatedRoles;

    private String eventTypeDescription;

    private String eventTypeID;

    private String eventTypeName;

    public String[] getAssociatedRoles ()
    {
        return associatedRoles;
    }

    public void setAssociatedRoles (String[] associatedRoles)
    {
        this.associatedRoles = associatedRoles;
    }

    public String getEventTypeDescription ()
    {
        return eventTypeDescription;
    }

    public void setEventTypeDescription (String eventTypeDescription)
    {
        this.eventTypeDescription = eventTypeDescription;
    }

    public String getEventTypeID ()
    {
        return eventTypeID;
    }

    public void setEventTypeID (String eventTypeID)
    {
        this.eventTypeID = eventTypeID;
    }

    public String getEventTypeName ()
    {
        return eventTypeName;
    }

    public void setEventTypeName (String eventTypeName)
    {
        this.eventTypeName = eventTypeName;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [associatedRoles = "+associatedRoles+", eventTypeDescription = "+eventTypeDescription+", eventTypeID = "+eventTypeID+", eventTypeName = "+eventTypeName+"]";
    }
}
