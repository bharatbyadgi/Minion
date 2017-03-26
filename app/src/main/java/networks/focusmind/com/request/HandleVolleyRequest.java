package networks.focusmind.com.request;


import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import networks.focusmind.com.VolleyConstants.VolleyConstants;
import networks.focusmind.com.application.MinionApplication;


public class HandleVolleyRequest {
    static String TAG = "HandleVolleyRequest";

    public static void getEventsData(String uname, JSONObject jsonObject, Response.Listener<JSONObject> succesListener, Response.ErrorListener errorListener) {
        String tag_json_obj = "getEventsData";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, VolleyConstants.SERVER_URL + VolleyConstants.GET_EVENTS + "/" + uname, jsonObject, succesListener, errorListener);
        MinionApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }


    public static void getEventsTypeData(String uname, JSONObject jsonObject, Response.Listener<JSONObject> succesListener, Response.ErrorListener errorListener) {
        String tag_json_obj = "getEventsTypeData";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, VolleyConstants.SERVER_URL + VolleyConstants.GET_EVENT_TYPE + "/" + uname, jsonObject, succesListener, errorListener);
        MinionApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public static void deleteEvent(int eventId, JSONObject jsonObject, Response.Listener<JSONObject> succesListener, Response.ErrorListener errorListener) {
        String tag_json_obj = "deleteEvent";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.DELETE,
                VolleyConstants.SERVER_URL + VolleyConstants.DELETE_EVENT + "/" + eventId, jsonObject, succesListener, errorListener);
        MinionApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }


    public static void editEvent(int eventId, JSONObject jsonObject, Response.Listener<JSONObject> succesListener, Response.ErrorListener errorListener) {
        String tag_json_obj = "editEvent";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, VolleyConstants.SERVER_URL + VolleyConstants.EDIT_EVENT + "/" + eventId, jsonObject, succesListener, errorListener);
        MinionApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }


    public static void getFacilityData(String uname, JSONObject jsonObject, Response.Listener<JSONObject> succesListener, Response.ErrorListener errorListener) {
        String tag_json_obj = "getFacilityData";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, VolleyConstants.SERVER_URL + VolleyConstants.GET_FACILITY + "/facilitybyuser/" + uname, jsonObject, succesListener, errorListener);
        MinionApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public static void getAllUsersData(String uname, JSONObject jsonObject, Response.Listener<JSONObject> succesListener, Response.ErrorListener errorListener) {
        String tag_json_obj = "getAllUsersData";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, VolleyConstants.SERVER_URL + VolleyConstants.GET_USERS, jsonObject, succesListener, errorListener);
        MinionApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public static void getLoggedInUserData(String uname, JSONObject jsonObject, Response.Listener<JSONObject> succesListener, Response.ErrorListener errorListener) {
        String tag_json_obj = "getLoggedInUserData";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, VolleyConstants.SERVER_URL + VolleyConstants.GET_USERS + "/" + uname, jsonObject, succesListener, errorListener);
        MinionApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public static void getAssetData(String uname, JSONObject jsonObject, Response.Listener<JSONObject> succesListener, Response.ErrorListener errorListener) {
        String tag_json_obj = "getAssetData";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, VolleyConstants.SERVER_URL + VolleyConstants.GET_ASSETS, jsonObject, succesListener, errorListener);
        MinionApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public static void getAmcData(int amcId, Response.Listener<JSONObject> succesListener, Response.ErrorListener errorListener) {
        String tag_json_obj = "getAmcData";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, VolleyConstants.SERVER_URL + VolleyConstants.GET_AMC_DETAILS + "/" + amcId, null, succesListener, errorListener);
        MinionApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }


    public static void getConversationData(int eventId, int startIndex, Response.Listener<JSONObject> succesListener, Response.ErrorListener errorListener) {
        Log.d(TAG,"url: "+VolleyConstants.SERVER_URL + VolleyConstants.GET_CONVERSATION_DETAILS + "/" + eventId + "/" + startIndex);
        String tag_json_obj = "getConversationData";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                VolleyConstants.SERVER_URL + VolleyConstants.GET_CONVERSATION_DETAILS + "/" + eventId + "/" + startIndex, null, succesListener, errorListener);
        MinionApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public static void getFlyoutData(String uname, JSONObject jsonObject, Response.Listener<JSONObject> succesListener, Response.ErrorListener errorListener) {
        String tag_json_obj = "getFlyoutData";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, VolleyConstants.SERVER_URL + VolleyConstants.HOME_PAGE_URL + "/" + uname, jsonObject, succesListener, errorListener);
        MinionApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public static void makeSetPasswordCall(String userId, JSONObject params, Response.Listener<JSONObject> succesListener, Response.ErrorListener errorListener) {
        String tag_json_obj = "setPassword";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, VolleyConstants.SERVER_URL + VolleyConstants.GET_USERS + "/resetpassword/" + userId, params, succesListener, errorListener);
        MinionApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public static void makeResetPasswordCall(String userId, JSONObject params, Response.Listener<JSONObject> succesListener, Response.ErrorListener errorListener) {
        String tag_json_obj = "setPassword";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT, VolleyConstants.SERVER_URL + VolleyConstants.GET_USERS + "/resetpassword/" + userId, params, succesListener, errorListener);
        MinionApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public static void postDataToServer(String url, JSONObject params, Response.Listener<JSONObject> succesListener, Response.ErrorListener errorListener) {
        String tag_json_obj = "postData";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, VolleyConstants.SERVER_URL + url, params, succesListener, errorListener);
        MinionApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public static void postConversationData(JSONObject conversationJsonObject, Response.Listener<JSONObject> succesListener, Response.ErrorListener errorListener) {
        Log.d(TAG,"url: "+VolleyConstants.SERVER_URL + VolleyConstants.ADD_CONVERSATION_DETAILS );
        Log.d(TAG,"conversationJsonObject: "+conversationJsonObject );

        String tag_json_obj = "postData";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, VolleyConstants.SERVER_URL + VolleyConstants.ADD_CONVERSATION_DETAILS, conversationJsonObject, succesListener, errorListener);
        MinionApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public static void deleteDataFromServer(String url, int idToDelete) {
        String tag_json_obj = "postData";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.DELETE, VolleyConstants.SERVER_URL + url + "/" + idToDelete, null, null, null) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        MinionApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public static void deleteDataFromServer(String url, String idToDelete) {
        String tag_json_obj = "postData";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.DELETE, VolleyConstants.SERVER_URL + url + "/" + idToDelete, null, null, null) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application");
                return headers;
            }
        };
        MinionApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }


}
