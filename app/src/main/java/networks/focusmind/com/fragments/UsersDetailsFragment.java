package networks.focusmind.com.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import networks.focusmind.com.VolleyConstants.VolleyConstants;
import networks.focusmind.com.activity.HomeActivity;
import networks.focusmind.com.minion.R;
import networks.focusmind.com.model.UserDetailDataModel;
import networks.focusmind.com.request.HandleVolleyRequest;
import networks.focusmind.com.utils.Utils;

import static android.support.v7.widget.helper.ItemTouchHelper.LEFT;
import static android.support.v7.widget.helper.ItemTouchHelper.RIGHT;


public class UsersDetailsFragment extends BaseFragment {


    private Context mContext;
    private String mPageDataUrl;
    private RecyclerView mRecyclerView;


    public static UsersDetailsFragment newInstance(Context context, String pageDataUrl) {
        UsersDetailsFragment fragment = new UsersDetailsFragment();
        fragment.mContext = context;
        fragment.mPageDataUrl = pageDataUrl;
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_event_layout, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.event_page_recycler_view);
        handleDataCall();
        return rootView;
    }

    private void handleDataCall() {
        showProgressDialog("Fetching..");
        if (VolleyConstants.IS_OFFLINE) {
            cancelProgressDialog();
            JSONObject responseContainer = Utils.loadJSONFromAsset(mContext, R.raw.user_detail_response);
            onPageResponseReceived(responseContainer);
        } else {
            HandleVolleyRequest.getAllUsersData("amsuser", new JSONObject(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    cancelProgressDialog();
                    onPageResponseReceived(response);
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


    private void onPageResponseReceived(JSONObject responseContainer) {

        if (null != responseContainer) {
            try {
                JSONArray dataArray = responseContainer.getJSONArray("userDetails");
                ArrayList<UserDetailDataModel> userDetailDataModelList = new ArrayList<>();
                for (int idx = 0; idx < dataArray.length(); idx++) {
                    Gson gson = new Gson();
                    JSONObject jsonObject = dataArray.getJSONObject(idx);
                    userDetailDataModelList.add(gson.fromJson(jsonObject.toString(), UserDetailDataModel.class));
                }
                setUpRecyclerView(userDetailDataModelList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setUpRecyclerView(final ArrayList<UserDetailDataModel> userDetailDataModelArrayList) {
        final UserDetailRecyclerAdapter userDetailRecyclerAdapter = new UserDetailRecyclerAdapter(userDetailDataModelArrayList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        VerticalSpaceItemDecoration verticalSpaceItemDecoration = new VerticalSpaceItemDecoration(25);
        mRecyclerView.setAdapter(userDetailRecyclerAdapter);
        mRecyclerView.addItemDecoration(verticalSpaceItemDecoration);
    }


    private class UserDetailRecyclerAdapter extends RecyclerView.Adapter<UserDetailViewHolder> {

        ArrayList<UserDetailDataModel> mUserDetailDataModelList;

        private UserDetailRecyclerAdapter(ArrayList<UserDetailDataModel> userDetailDataModelList) {
            mUserDetailDataModelList = userDetailDataModelList;
        }

        @Override
        public UserDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_detail_list_item, parent, false);
            return new UserDetailViewHolder(view);
        }

        @Override
        public void onBindViewHolder(UserDetailViewHolder holder, final int position) {
            holder.userName.setText(mUserDetailDataModelList.get(position).getFirstName());
            final View currentView = holder.itemView;

            holder.deleteFacility.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteUser(mUserDetailDataModelList, position);
                }
            });

            holder.editFacility.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editUser(mUserDetailDataModelList.get(position));
                }
            });

            currentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDetails(currentView, mUserDetailDataModelList.get(position));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mUserDetailDataModelList.size();
        }

    }

    private void deleteUser(ArrayList<UserDetailDataModel> userDetailDataModelArrayList, final int position) {
        showProgressDialog("Deleting..");
        HandleVolleyRequest.deleteDataFromServer(VolleyConstants.GET_USERS, userDetailDataModelArrayList.get(position).getId());
        userDetailDataModelArrayList.remove(position);
        UserDetailRecyclerAdapter assetDetailRecyclerAdapter = new UserDetailRecyclerAdapter(userDetailDataModelArrayList);
        mRecyclerView.setAdapter(assetDetailRecyclerAdapter);
        cancelProgressDialog();
        Toast.makeText(getContext(), "Deleted successfully", Toast.LENGTH_LONG).show();
    }

    private void editUser(UserDetailDataModel userDetailDataModel) {
        HashMap<String, String> editTextMap = Utils.getArrayListFromJson(getContext(), R.raw.post_data, "postUser");
        Fragment frag = AddDetailsFragment.newInstance(editTextMap, userDetailDataModel, "Edit Facility");
        HomeActivity homeActivity = (HomeActivity) getActivity();
        homeActivity.openAddEditFragment(frag, "Edit Facility");
    }

    private void showDetails(View view, UserDetailDataModel userDetailDataModel) {
        if (view.findViewById(R.id.collapsed_user_card).getVisibility() == View.VISIBLE) {
            view.findViewById(R.id.detail_card).setVisibility(View.VISIBLE);
            view.findViewById(R.id.collapsed_user_card).setVisibility(View.GONE);
            TextView eventDetailName = (TextView) view.findViewById(R.id.contact_detail_name);
            eventDetailName.setText(userDetailDataModel.getFirstName());
            TextView eventDetailDesc = (TextView) view.findViewById(R.id.contact_detail_number);
            eventDetailDesc.setText(userDetailDataModel.getContactNo());
            TextView eventDetailDate = (TextView) view.findViewById(R.id.contact_detail_unit);
            eventDetailDate.setText(userDetailDataModel.getEmail());
        } else {
            view.findViewById(R.id.detail_card).setVisibility(View.GONE);
            view.findViewById(R.id.collapsed_user_card).setVisibility(View.VISIBLE);
        }
    }

    private class UserDetailViewHolder extends RecyclerView.ViewHolder {

        private TextView userName;
        public ImageView deleteFacility;
        public ImageView editFacility;


        private UserDetailViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.contact_name);
            deleteFacility = (ImageView) itemView.findViewById(R.id.delete_user);
            editFacility = (ImageView) itemView.findViewById(R.id.edit_user);
        }
    }

    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int verticalSpaceHeight;

        private VerticalSpaceItemDecoration(int verticalSpaceHeight) {
            this.verticalSpaceHeight = verticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = verticalSpaceHeight;
        }
    }


}
