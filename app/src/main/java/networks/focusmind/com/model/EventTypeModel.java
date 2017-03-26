package networks.focusmind.com.model;

/**
 * Created by bharat on 25/3/17.
 */

public class EventTypeModel extends BaseDataModel {

    private EventTypeDetails[] eventtypedetails;

    public EventTypeDetails[] getEventtypedetails ()
    {
        return eventtypedetails;
    }

    public void setEventtypedetails (EventTypeDetails[] eventtypedetails)
    {
        this.eventtypedetails = eventtypedetails;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [eventtypedetails = "+eventtypedetails+"]";
    }
}
