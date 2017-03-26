package networks.focusmind.com.VolleyConstants;

public class VolleyConstants {

    public static boolean IS_OFFLINE = false;
    public static String URL = "http://182.18.139.159";
    public static String SERVER_URL = URL + ":8080/";

    public static final String USER_PROFILE_URL = SERVER_URL + "";
    public static final String PERMITTED_EVENT_TYPES_URL = "events/api/ams/events/v1/eventtype";
    public static final String US_TOKEN_URL = SERVER_URL + "";
    public static final String ROLE_PERMISSION_MAP_URL = SERVER_URL + "";
    public static final String PERMITTED_SCREEN_LIST_URL = SERVER_URL + "";
    public static final String HOME_PAGE_URL = "home/api/ams/home/v1/display";
    public static final String SEND_EVENT = SERVER_URL + "events/api/ams/events/v1/events";
    public static final String ADD_ASSET = SERVER_URL + "assetmgmt/api/ams/assets/v1";
    public static final String UPDATE_ASSET = SERVER_URL + "assetmgmt/api/ams/assets/v1/100";
    public static final String GET_EVENTS = "events/api/ams/events/v1/events";
    public static final String GET_EVENT_TYPE = "events/api/ams/events/v1/eventtype";
    public static final String DELETE_EVENT = "events/api/ams/events/v1/events";

    public static final String EDIT_EVENT = "events/api/ams/events/v1/events";
    public static final String GET_AMC_DETAILS = "assetmgmt/api/ams/assets/v1/amc";
    public static final String GET_AMC_LIST = "";
    public static final String GET_FACILITY = "facility/api/ams/facility/v1";
    public static final String GET_USERS = "um/api/ams/um/v1/users";
    public static final String GET_ASSETS = "assetmgmt/api/ams/assets/v1/assets";
    public static final String DELETE_ASSET = "assetmgmt/api/ams/assets/v1";
    public static final String BOOK_FACILITY = "facility/api/ams/facility/v1/booking";
    public static final String GET_CONVERSATION_DETAILS = "events/api/ams/events/v1/events/conversation";
    public static final String ADD_CONVERSATION_DETAILS = "events/api/ams/events/v1/conversation";

}
