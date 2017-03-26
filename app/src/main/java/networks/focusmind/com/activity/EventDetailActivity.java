package networks.focusmind.com.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import networks.focusmind.com.VolleyConstants.VolleyConstants;
import networks.focusmind.com.minion.R;
import networks.focusmind.com.model.EventDetailDataModel;
import networks.focusmind.com.request.HandleVolleyRequest;
import networks.focusmind.com.utils.StringConstants;

public class EventDetailActivity extends BaseActivity {


    EventDetailDataModel eventDetailDataModel;

    EditText mEventName,mEventDesc;

    TextView mEventDate;
    TextView mSaveButton;
    ImageView mEditButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        eventDetailDataModel = (EventDetailDataModel)
                getIntent().getSerializableExtra(StringConstants.PARAM_EVENT_DETAIL);

        initViews();

    }

    private void initViews() {


        mEventName = (EditText) findViewById(R.id.et_name);
        mEventDesc = (EditText) findViewById(R.id.et_description);
        mEventDate = (TextView) findViewById(R.id.tv_date);
        mEventDate.setOnClickListener(this);

        if(!TextUtils.isEmpty(eventDetailDataModel.getEventName()))
            mEventName.setText(eventDetailDataModel.getEventName().trim());
        if(!TextUtils.isEmpty(eventDetailDataModel.getDescription()))
            mEventDesc.setText(eventDetailDataModel.getDescription().trim());
        if(!TextUtils.isEmpty(eventDetailDataModel.getEventCreatedTime()))
            mEventDate.setText(eventDetailDataModel.getEventCreatedTime().trim());

        mSaveButton = (TextView) findViewById(R.id.tv_save);
        mSaveButton.setOnClickListener(this);

        mEditButton = (ImageView) findViewById(R.id.iv_edit);
        mEditButton.setOnClickListener(this);

        mEventName.setEnabled(false);
        mEventDesc.setEnabled(false);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.tv_save:
                saveEventData();
                break;

            case R.id.iv_edit:
                enableEditViews();
                break;

            case R.id.tv_date:
                showDate();
                break;

            default:
                break;

        }
    }

    private void enableEditViews() {

        mEditButton.setVisibility(View.INVISIBLE);
        mSaveButton.setVisibility(View.VISIBLE);
        mEventName.setEnabled(true);
        mEventDesc.setEnabled(true);
        mEventName.requestFocus();
        mEventName.setSelection(mEventName.getText().length());

    }

    private void disableEditViews() {

        mEventName.setEnabled(false);
        mEventDesc.setEnabled(false);
        mEditButton.setVisibility(View.VISIBLE);
        mSaveButton.setVisibility(View.INVISIBLE);

    }


    private void saveEventData() {

        showProgressDialog("updating..");

        if (VolleyConstants.IS_OFFLINE) {
            cancelProgressDialog();
        }
        else {
            JSONObject editEventObject = new JSONObject();
            try {
                editEventObject.put("eventID", eventDetailDataModel.getEventID());
                editEventObject.put("eventTypeID", eventDetailDataModel.getEventTypeID());
                editEventObject.put("eventName", mEventName.getText());
                editEventObject.put("description", mEventDesc.getText());
                editEventObject.put("eventCreator", eventDetailDataModel.getEventCreator());
                editEventObject.put("eventCreatedTime", eventDetailDataModel.getEventCreatedTime() );
                editEventObject.put("eventExpiryInDays", eventDetailDataModel.getEventExpiryInDays());
                editEventObject.put("eventScheduledTime",  System.currentTimeMillis()/*mEventDate.getText()*/);
                editEventObject.put("hasConversation", false);
                editEventObject.put("isCancelled", false);
            } catch (JSONException exception) {

            }

            HandleVolleyRequest.editEvent(eventDetailDataModel.getEventID(),editEventObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            cancelProgressDialog();
                            disableEditViews();
                            onResponseReceived(response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            cancelProgressDialog();
                            Toast.makeText(EventDetailActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    });
        }
    }

    private void onResponseReceived(JSONObject response) {

        Toast.makeText(EventDetailActivity.this, getString(R.string.msg_event_updated), Toast.LENGTH_SHORT).show();
        onBackPressed();
    }


    public void showProgressDialog(String message) {
        if (null != mProgressDialog) {
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        }
    }

    public void cancelProgressDialog() {
        if (null != mProgressDialog && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    private void showDate() {

    }

}
