package networks.focusmind.com.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import networks.focusmind.com.minion.R;
import networks.focusmind.com.model.EventDetailDataModel;
import networks.focusmind.com.utils.StringConstants;

public class EventDetailActivity extends AppCompatActivity implements View.OnClickListener {


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

        mEventName.setText(eventDetailDataModel.getEventName().trim());
        mEventDesc.setText(eventDetailDataModel.getDescription().trim());
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
                editViews();
                break;

            case R.id.tv_date:
                showDate();
                break;

            default:
                break;

        }
    }

    private void editViews() {

        mEditButton.setVisibility(View.INVISIBLE);
        mSaveButton.setVisibility(View.VISIBLE);
        mEventName.setEnabled(true);
        mEventDesc.setEnabled(true);
        mEventName.requestFocus();
    }

    private void saveEventData() {

        mEventName.setEnabled(false);
        mEventDesc.setEnabled(false);
        mEditButton.setVisibility(View.VISIBLE);
        mSaveButton.setVisibility(View.INVISIBLE);
    }


    private void showDate() {

    }

}
