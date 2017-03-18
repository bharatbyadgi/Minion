package networks.focusmind.com.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import networks.focusmind.com.minion.R;

public class SelfDetailFragment extends BaseFragment {

    private Context mContext;
    private Bundle mBundle;

    public static SelfDetailFragment newInstance(Context context, Bundle bundle) {
        SelfDetailFragment fragment = new SelfDetailFragment();
        fragment.mContext = context;
        fragment.mBundle = bundle;
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.self_detail_layout, container, false);
        TextView firstName = (TextView) rootView.findViewById(R.id.first_name);
        firstName.setText("First Name : " + mBundle.getString("firstName"));
        TextView lastName = (TextView) rootView.findViewById(R.id.last_name);
        lastName.setText("Last Name : " + mBundle.getString("lastName"));
        TextView email = (TextView) rootView.findViewById(R.id.email);
        email.setText("Email Id : " + mBundle.getString("email"));
        TextView createdOn = (TextView) rootView.findViewById(R.id.created_on);
        createdOn.setText("Created On : " + mBundle.getString("createdOn"));
        TextView username = (TextView) rootView.findViewById(R.id.user_name);
        username.setText("Username : " + mBundle.getString("username"));
        TextView enabled = (TextView) rootView.findViewById(R.id.status);
        if (mBundle.getString("enabled").equalsIgnoreCase("true")) {
            enabled.setText("Account Status : Enabled");
        } else {
            enabled.setText("Account Status : Disabled");
        }
        TextView emailVerified = (TextView) rootView.findViewById(R.id.email_verified);
        if (mBundle.getString("emailVerified").equalsIgnoreCase("true")) {
            emailVerified.setText("Email Verification : Done");
        } else {
            emailVerified.setText("Email Verification : Pending");
        }
        return rootView;
    }

}
