package networks.focusmind.com.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import networks.focusmind.com.VolleyConstants.VolleyConstants;
import networks.focusmind.com.fragments.AddDetailsFragment;
import networks.focusmind.com.fragments.AssetDetailsFragment;
import networks.focusmind.com.fragments.ConversationFragment;
import networks.focusmind.com.fragments.EventDetailsFragment;
import networks.focusmind.com.fragments.FacilityDetailsFragment;
import networks.focusmind.com.fragments.HomePageFragment;
import networks.focusmind.com.fragments.SelfDetailFragment;
import networks.focusmind.com.fragments.UsersDetailsFragment;
import networks.focusmind.com.minion.R;
import networks.focusmind.com.model.MenuDataModel;
import networks.focusmind.com.request.HandleVolleyRequest;
import networks.focusmind.com.storage.SharedPreferenceManager;
import networks.focusmind.com.utils.Utils;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private NavigationView mNavigationView;
    private MenuDataModel mMenuModelData;
    private JsonArray mOrderedMenuItemsList;
    private Toolbar mToolbar;
    private Fragment mFragment;
    private FloatingActionButton mFloatingActionButton;
    private ActionBarDrawerToggle mDrawerToggle;
    private SharedPreferenceManager mSharedpref;
    private boolean mDoubleBackToExitPressedOnce;
    private Bundle mBundle;
    private View mHeader;


    private static final String HOME_PAGE = "HomePage";
    private static final String VENDOR_PAGE = "FindVendor";
    private static final String MINTENANCE_DETAIL_PAGE = "MaintainenceInfo";
    private static final String FACILITY_PAGE = "Facility";
    private static final String EVENT_PAGE = "Event";
    private static final String PHOTOS_PAGE = "Photos";
    private static final String ASSETS_PAGE = "AssetManagement";
    private static final String ACCOUNTING_PAGE = "AccountManagement";
    private static final String USER_MANAGEMENT_PAGE = "UserManagement";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mSharedpref = new SharedPreferenceManager(this);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mHeader = mNavigationView.getHeaderView(0);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.actionButton);
        mFloatingActionButton.setOnClickListener(this);
        handleDataCall();
//        getUserDetails();
        openSelectedFragment(HOME_PAGE, "", "Home Page");
    }

    private void openUserDetailScreen() {
        onBackPressed();
        mFloatingActionButton.setVisibility(View.GONE);
        SelfDetailFragment selfDetailFragment = SelfDetailFragment.newInstance(HomeActivity.this, mBundle);
        openFragment(selfDetailFragment, "My Profile");
    }


    private void getUserDetails() {

        if (VolleyConstants.IS_OFFLINE) {
            JSONObject responseContainer = Utils.loadJSONFromAsset(this, R.raw.user_detail_response);
            onPageResponseReceived(responseContainer);
        } else {
            HandleVolleyRequest.getLoggedInUserData(mSharedpref.getUserName(), new JSONObject(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (null != response) {

                        TextView name = (TextView) mHeader.findViewById(R.id.user_name_nav_header);
                        TextView email = (TextView) mHeader.findViewById(R.id.user_mail_id_nav_header);
                        name.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openUserDetailScreen();
                            }
                        });
                        email.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openUserDetailScreen();
                            }
                        });
                        mHeader.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                        try {
                            if (response.has("firstName") && response.has("lastName") && response.has("email")) {
                                name.setText(response.getString("firstName") + " " + response.getString("lastName"));
                                email.setText(response.getString("email"));

                                mBundle = new Bundle();
                                mBundle.putString("firstName", response.getString("firstName"));
                                mBundle.putString("lastName", response.getString("lastName"));
                                mBundle.putString("email", response.getString("email"));
                                mBundle.putString("createdOn", response.getString("createdTimestamp"));
                                mBundle.putString("username", response.getString("username"));
                                mBundle.putString("enabled", response.getString("enabled"));
                                mBundle.putString("emailVerified", response.getString("emailVerified"));
                            }
                        } catch (JSONException exception) {

                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(HomeActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void handleDataCall() {
        if (VolleyConstants.IS_OFFLINE) {
            JSONObject responseContainer = Utils.loadJSONFromAsset(this, R.raw.menu_response);
            onPageResponseReceived(responseContainer);
        } else {
            HandleVolleyRequest.getFlyoutData("amsuser", new JSONObject(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    onPageResponseReceived(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }
    }


    private void onPageResponseReceived(JSONObject responseContainer) {

        if (null != responseContainer) {
            Gson gson = new Gson();
            mMenuModelData = gson.fromJson(responseContainer.toString(), MenuDataModel.class);
            if (null != mMenuModelData) {
                mOrderedMenuItemsList = sortMenuOnRank(mMenuModelData.getWidgetType());
                setUpNavigationView();
            }
        }
    }

    private void setUpNavigationView() {
        Menu menu = mNavigationView.getMenu();
        for (int idx = 0; idx < mOrderedMenuItemsList.size(); idx++) {
            String dataKey = mOrderedMenuItemsList.get(idx).getAsJsonObject().get("dataKey").getAsString();
            JsonObject widgetObject = mMenuModelData.getWidgets();
            String menuName = widgetObject.get(dataKey).getAsJsonObject().get("name").getAsString();
            menu.add(menuName);
        }
        mNavigationView.invalidate();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.actionButton:
                handleActionButton();
                break;
        }
    }

    private void handleActionButton() {
        HashMap<String, String> editTextMap = new HashMap<>();
        String title = "";
        if (mFragment instanceof FacilityDetailsFragment) {
            title = "Add Facility";
            editTextMap = Utils.getArrayListFromJson(this, R.raw.post_data, "postFacility");
        } else if (mFragment instanceof EventDetailsFragment) {
            title = "Add Event";
            editTextMap = Utils.getArrayListFromJson(this, R.raw.post_data, "postEvent");
        } else if (mFragment instanceof AssetDetailsFragment) {
            title = "Add Asset";
            editTextMap = Utils.getArrayListFromJson(this, R.raw.post_data, "postAsset");
        } else if (mFragment instanceof UsersDetailsFragment) {
            title = "Add Users";
            editTextMap = Utils.getArrayListFromJson(this, R.raw.post_data, "postUser");
        }
        if (null != editTextMap && editTextMap.size() > 0) {
            Fragment frag = AddDetailsFragment.newInstance(editTextMap, title);
            openAddEditFragment(frag, title);
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (mFloatingActionButton.getVisibility() == View.GONE) {
                showBackIcon(false);
                mFloatingActionButton.setVisibility(View.VISIBLE);
                openFragment(new HomePageFragment().newInstance(), "Home Page");
                return;
            }

            if (mDoubleBackToExitPressedOnce) {
                Log.i("MainActivity", "nothing on backstack, calling super");
                super.onBackPressed();
            }
            this.mDoubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    mDoubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        for (int idx = 0; idx < mNavigationView.getMenu().size(); idx++) {
            if (mNavigationView.getMenu().getItem(idx).toString().equalsIgnoreCase(item.getTitle().toString())) {
                String dataKey = mOrderedMenuItemsList.get(idx).getAsJsonObject().get("dataKey").getAsString();
                JsonObject widgetObject = mMenuModelData.getWidgets();
                String pageName = widgetObject.get(dataKey).getAsJsonObject().get("widgetName").getAsString();
                String pageUrl = widgetObject.get(dataKey).getAsJsonObject().get("widgetUrl").getAsString();
                openSelectedFragment(pageName, pageUrl, item.getTitle().toString());
            }
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showBackIcon(boolean enableBack) {
        if (null != getSupportActionBar()) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(enableBack);
        }
    }

    public void openConversationFragment(int eventId, String pageTitle) {
        mFloatingActionButton.setVisibility(View.GONE);
        showBackIcon(true);
        Fragment fragment = ConversationFragment.newInstance(eventId);
        openFragment(fragment, pageTitle);
    }

    public void openAddEditFragment(Fragment fragment, String pageTitle) {
        showBackIcon(true);
        mFloatingActionButton.setVisibility(View.GONE);
        openFragment(fragment, pageTitle);
    }

    private void openFragment(Fragment fragment, String pageTitle) {
        mToolbar.setTitle(pageTitle);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.contentFragment, fragment, fragment.getTag());
        transaction.show(fragment);
        transaction.commit();
    }

    private void openSelectedFragment(String pageName, String dataUrl, String pageTitle) {
        showBackIcon(false);
        mFloatingActionButton.setVisibility(View.VISIBLE);
        switch (pageName) {
            case HOME_PAGE:
                mFragment = HomePageFragment.newInstance();
                break;
            case VENDOR_PAGE:
                mFragment = HomePageFragment.newInstance();
                break;
            case MINTENANCE_DETAIL_PAGE:
                mFragment = HomePageFragment.newInstance();
                break;
            case FACILITY_PAGE:
                mFragment = FacilityDetailsFragment.newInstance(this, dataUrl);
                break;
            case EVENT_PAGE:
                mFragment = EventDetailsFragment.newInstance(this, dataUrl);
                break;
            case PHOTOS_PAGE:
                mFragment = HomePageFragment.newInstance();
                break;
            case ASSETS_PAGE:
                mFragment = AssetDetailsFragment.newInstance(this, dataUrl);
                break;
            case ACCOUNTING_PAGE:
                mFragment = HomePageFragment.newInstance();
                break;
            case USER_MANAGEMENT_PAGE:
                mFragment = UsersDetailsFragment.newInstance(this, dataUrl);
                break;
            default:
                mFragment = HomePageFragment.newInstance();
                break;
        }
        if (null != mFragment) {
            openFragment(mFragment, pageTitle);
        }

    }

    private JsonArray sortMenuOnRank(JsonArray widgetList) {
        JsonArray sortedJsonArray = new JsonArray();
        if (null == widgetList) return null;
        List<JsonObject> jsonValues = new ArrayList<>();
        for (int i = 0; i < widgetList.size(); i++) {
            jsonValues.add(widgetList.get(i).getAsJsonObject());
        }
        Collections.sort(jsonValues, new Comparator<JsonObject>() {
            private static final String KEY_NAME = "rank";

            @Override
            public int compare(JsonObject a, JsonObject b) {
                int valA;
                int valB;

                valA = a.get(KEY_NAME).getAsInt();
                valB = b.get(KEY_NAME).getAsInt();

                return valA - valB;
                //if you want to change the sort order, simply use the following:
                //return -valA.compareTo(valB);
            }
        });

        for (int i = 0; i < widgetList.size(); i++) {
            sortedJsonArray.add(jsonValues.get(i));
        }
        return sortedJsonArray;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

}
