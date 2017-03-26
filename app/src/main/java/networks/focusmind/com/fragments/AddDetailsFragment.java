package networks.focusmind.com.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;

import networks.focusmind.com.VolleyConstants.VolleyConstants;
import networks.focusmind.com.minion.R;
import networks.focusmind.com.model.BaseDataModel;
import networks.focusmind.com.model.EventTypeDetails;
import networks.focusmind.com.model.EventTypeModel;
import networks.focusmind.com.request.HandleVolleyRequest;
import networks.focusmind.com.storage.SharedPreferenceManager;
import networks.focusmind.com.utils.Utils;

public class AddDetailsFragment extends BaseFragment {

    private HashMap<String, String> mDataMap;
    private static final String EDIT_TEXT_LIST = "editTextList";
    private static final String EDIT_TEXT_DATA = "editTextData";
    private static final String STRING_DATA_TYPE = "String";
    private static final String BOOLEAN_DATA_TYPE = "Boolean";
    private static final String INTEGER_DATA_TYPE = "Integer";
    private static final String CALENDER_DATA_TYPE = "DateTime";
    private LinearLayout mLinearLayout;
    String mDialogTitle = "";
    int amcId;
    private SharedPreferenceManager mSharedPref;


    public static AddDetailsFragment newInstance(HashMap<String, String> editTextMap, BaseDataModel baseDataModel, String title) {
        AddDetailsFragment fragment = new AddDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EDIT_TEXT_LIST, editTextMap);
        bundle.putSerializable(EDIT_TEXT_DATA, baseDataModel);
        fragment.setArguments(bundle);
        fragment.mDialogTitle = title;
        return fragment;
    }

    public static AddDetailsFragment newInstance(HashMap<String, String> editTextMap, BaseDataModel baseDataModel, String title, int amcId) {
        AddDetailsFragment fragment = new AddDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EDIT_TEXT_LIST, editTextMap);
        bundle.putSerializable(EDIT_TEXT_DATA, baseDataModel);
        fragment.setArguments(bundle);
        fragment.mDialogTitle = title;
        fragment.amcId = amcId;
        return fragment;
    }

    public static AddDetailsFragment newInstance(HashMap<String, String> editTextMap, String title) {
        AddDetailsFragment fragment = new AddDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EDIT_TEXT_LIST, editTextMap);
        fragment.setArguments(bundle);
        fragment.mDialogTitle = title;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_add_facility, null, false);
        mLinearLayout = (LinearLayout) rootView.findViewById(R.id.linearLayout);
        mLinearLayout.setPadding(0, 100, 0, 0);
        mSharedPref = new SharedPreferenceManager(getContext());
        if (null != getArguments() && getArguments().containsKey(EDIT_TEXT_LIST) && getArguments().getSerializable(EDIT_TEXT_LIST) instanceof HashMap) {
            mDataMap = (HashMap<String, String>) getArguments().getSerializable(EDIT_TEXT_LIST);
            if (null != mDataMap && mDataMap.size() > 0) {

                if (null != getArguments() && getArguments().containsKey(EDIT_TEXT_DATA) && getArguments().getSerializable(EDIT_TEXT_DATA) instanceof HashMap) {
                    for (HashMap.Entry<String, String> hintText : mDataMap.entrySet()) {
                        String dispName = "";
                        dispName = hintText.getKey();
                        mDataMap.get(dispName);
                    }
                } else {
                    for (HashMap.Entry<String, String> hintText : mDataMap.entrySet()) {
                        final String dispName = hintText.getKey();
                        TextInputLayout stringInputLayout = new TextInputLayout(getActivity());
                        stringInputLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        LinearLayout.LayoutParams stringLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        EditText stringEditText = new EditText(getContext());
                        stringEditText.setSingleLine(true);
                        stringInputLayout.setHint(dispName);
                        stringInputLayout.addView(stringEditText, stringLayoutParams);
                        if(dispName.equals("dummy")){
                            stringEditText.setText(mSharedPref.getUserName());
                        }
                        if(dispName.equals("Facility Id")){
                            stringEditText.setText("1");
                            stringEditText.setEnabled(false);
                        }
                        if(dispName.equals("Asset Movability")){
                            stringEditText.setText("false");
                            stringEditText.setEnabled(false);
                        }
                        if(dispName.equals("AssetId")){
                            stringEditText.setText(amcId+"");
                            stringEditText.setEnabled(false);
                        }

                        /**
                         * only for event type in add event
                         */
                        if(dispName.equalsIgnoreCase("Event Type")) {
                            // stringEditText.setActivated(false);
                            stringEditText.setFocusable(false);
                        }
                        stringEditText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if(dispName.equalsIgnoreCase("Event Type")){

                                    if(eventTypeModel == null)
                                        eventListAPI(v);
                                    else
                                        showEventTypeDialog((EditText) v,eventTypeModel.getEventtypedetails() );

                                }
                            }
                        });
                        mLinearLayout.addView(stringInputLayout);
                    }
                }
            }
        }

        Button cancelButton = (Button) rootView.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != getActivity()) {
                    getActivity().onBackPressed();
                }
            }
        });

        Button saveButton = (Button) rootView.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSaveButton();
            }
        });

        return rootView;
    }


    private void eventListAPI(final View v) {

        showProgressDialog("Fetching..");
        if (VolleyConstants.IS_OFFLINE) {
            cancelProgressDialog();

        } else {
            HandleVolleyRequest.getEventsTypeData("amsuser", new JSONObject(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    cancelProgressDialog();
                    Type type = new TypeToken<EventTypeModel>() {}.getType();
                    eventTypeModel = new Gson().fromJson(response.toString(),type);
                    showEventTypeDialog((EditText) v,eventTypeModel.getEventtypedetails());

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    cancelProgressDialog();
                    Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    EventTypeModel eventTypeModel;
    String eventTypeId;

    private void showEventTypeDialog(final EditText edtView, final EventTypeDetails[] eventTypeDetailsList){

        final String[] popupEventTypeNameList = new String[eventTypeDetailsList.length];
        final String[] popupEventTypeIdList = new String[eventTypeDetailsList.length];

        for (int i = 0; i < eventTypeDetailsList.length; i++){

            EventTypeDetails eventTypeDetails = eventTypeDetailsList[i];

            popupEventTypeNameList[i] = eventTypeDetails.getEventTypeName();
            popupEventTypeIdList[i] = eventTypeDetails.getEventTypeID();

        }

        final Dialog dialog = new Dialog(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.popup_list_dialog, null);
        ((TextView)view.findViewById(R.id.txtPopupHeader)).setText(getString(R.string.event_type));
        ListView lvPopup = (ListView) view.findViewById(R.id.lstPopUp);
        Button close = (Button) view.findViewById(R.id.popupClose);
        lvPopup.setAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, popupEventTypeNameList){
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                TextView v = (TextView) super.getView(position, convertView, parent);
                v.setTextSize(18);
                v.setPadding(16,10,0,10);
                return v;
            }
        });
        lvPopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                edtView.setText(popupEventTypeNameList[position].toString());
                dialog.cancel();
                dialog.dismiss();

                eventTypeId = popupEventTypeIdList[position];

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                dialog.dismiss();
            }
        });
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.show();
    }

    private void handleSaveButton() {
        JSONObject postObject = new JSONObject();

        for (int idx = 0; idx < mLinearLayout.getChildCount(); idx++) {
            TextInputLayout textInputLayout = (TextInputLayout) mLinearLayout.getChildAt(idx);
            EditText editText = (EditText) ((FrameLayout) textInputLayout.getChildAt(0)).getChildAt(0);
            String jsonKey = mDataMap.get(textInputLayout.getHint().toString());
            String inputData = editText.getText().toString();
            if(TextUtils.isEmpty(inputData)){
                Toast.makeText(getContext(), "Please fill all fields.", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                if (null != jsonKey && jsonKey.contains("-")) {
                    String[] splitKey = jsonKey.split("-");
                    String key = splitKey[0];
                    String innerKey = "";
                    if (splitKey.length > 1) {
                        innerKey = splitKey[1];
                    }
                    if (postObject.has(key)) {
                        JSONObject existingObject = (JSONObject) postObject.get(key);
                        if (!TextUtils.isEmpty(innerKey)) {
                            existingObject.put(innerKey, editText.getText().toString());
                        }
                    } else {
                        if (!TextUtils.isEmpty(innerKey)) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put(innerKey, editText.getText().toString());
                            postObject.put(key, jsonObject);
                        }
                    }
                } else {
                    if(textInputLayout.getHint().toString().trim().equals("Event Type"))
                        postObject.put(mDataMap.get(textInputLayout.getHint().toString()),eventTypeId);
                    else
                        postObject.put(mDataMap.get(textInputLayout.getHint().toString()), editText.getText().toString());
                    Log.d(TAG, "postObject: "+postObject);

                }
            } catch (JSONException e) {
                Log.e("PostData", "Something went wrong!!");
            }
        }
        if (postObject.length() > 0) {
            HandleVolleyRequest.postDataToServer(getUrl(), postObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    cancelProgressDialog();
                    updateFragment(response);
                    getActivity().onBackPressed();
                    Log.d(TAG, "Success");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error");
                    cancelProgressDialog();
                    Toast.makeText(getContext(), "Save Failed..", Toast.LENGTH_LONG).show();
                    getActivity().onBackPressed();
                }
            });
        }
    }

    private void updateFragment(JSONObject response) {
        Gson gson = new Gson();
        BaseDataModel baseDataModel = gson.fromJson(response.toString(), BaseDataModel.class);
        if (mDialogTitle.equals("Add Users")) {
            if (null != response && response.has("id")) {
                try {
                    showSetPasswordDialog(response.getString("id"));
                } catch (JSONException exception) {

                }
            }
        }
    }

    String TAG = this.getClass().getSimpleName();

    private void showSetPasswordDialog(final String userId) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Set Password");
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (null != input.getText() && !TextUtils.isEmpty(input.getText().toString())) {
                            makeSetPasswordApiCall(userId, input.getText().toString());
                        } else {
                            Toast.makeText(getContext(), "Please enter a valid password", Toast.LENGTH_LONG).show();
                            showSetPasswordDialog(userId);
                        }
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void makeSetPasswordApiCall(String userId, String password) {
        showProgressDialog("Saving..");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "password");
            jsonObject.put("value", password);
            jsonObject.put("temporary", true);
        } catch (JSONException exception) {

        }
        HandleVolleyRequest.makeSetPasswordCall(userId, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                cancelProgressDialog();
                getActivity().onBackPressed();
                Toast.makeText(getContext(), "Saved Successfully..", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                cancelProgressDialog();
                Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getUrl() {
        String url = "";
        switch (mDialogTitle) {

            case "Add Facility":
                url = "facility/api/ams/facility/v1/facility";
                break;
            case "Add Event":
                url = "events/api/ams/events/v1/events";
                break;
            case "Add Asset":
                url = "assetmgmt/api/ams/assets/v1/";
                break;
            case "Add Users":
                url = "um/api/ams/um/v1/users";
                break;

            case "Attach AMC":
                url = "assetmgmt/api/ams/assets/v1/amc";
                break;

            case "Add AMC Report":
                url = "assetmgmt/api/ams/assets/v1/amc/report";
                break;
        }

        return url;
    }
}
