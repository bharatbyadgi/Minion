package networks.focusmind.com.fragments;


import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import networks.focusmind.com.VolleyConstants.VolleyConstants;
import networks.focusmind.com.minion.R;
import networks.focusmind.com.model.ConversationDataModel;
import networks.focusmind.com.request.HandleVolleyRequest;
import networks.focusmind.com.storage.SharedPreferenceManager;
import networks.focusmind.com.utils.Utils;

public class ConversationFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private ArrayList<ConversationDataModel> mConversationDataModelArrayList;
    private boolean mIsLoading = false;
    private boolean mAllDataFetched = false;
    private ConversationRecyclerAdapter mConversationRecyclerAdapter;
    private SharedPreferenceManager mSharedPref;

    public static ConversationFragment newInstance(int eventId) {
        Bundle args = new Bundle();
        ConversationFragment fragment = new ConversationFragment();
        args.putInt("eventId", eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_conversation_layout, container, false);
        mSharedPref = new SharedPreferenceManager(getContext());
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.conversation_page_recycler_view);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        VerticalSpaceItemDecoration verticalSpaceItemDecoration = new VerticalSpaceItemDecoration(25);

        mRecyclerView.addItemDecoration(verticalSpaceItemDecoration);

        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (mIsLoading)
                    return;
                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();
                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    handleDataCall(getArguments().getInt("eventId"), mConversationDataModelArrayList.size());
                }
            }
        };

        mRecyclerView.setOnScrollListener(scrollListener);
        mConversationRecyclerAdapter = new ConversationRecyclerAdapter();
        mRecyclerView.setAdapter(mConversationRecyclerAdapter);

        final EditText mMessageText = (EditText) rootView.findViewById(R.id.add_message);
        Button sendButton = (Button) rootView.findViewById(R.id.send_message);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(mMessageText.getText().toString())) {
                    sendMessage(mMessageText.getText().toString());
                    mMessageText.setText("");
                }
            }
        });

        mConversationDataModelArrayList = new ArrayList<>();
        if (null != getArguments() && getArguments().containsKey("eventId")) {
            handleDataCall(getArguments().getInt("eventId"), 0);
        }
        return rootView;
    }

    private void sendMessage(String message) {
        showProgressDialog("Sending..");
        int eventId = -1;
        if (null != getArguments() && getArguments().containsKey("eventId")) {
            eventId = getArguments().getInt("eventId");
        }
        if (VolleyConstants.IS_OFFLINE) {
            cancelProgressDialog();
        } else {
            JSONObject conversationJsonObject = new JSONObject();
            try {
                conversationJsonObject.put("createdTime", System.currentTimeMillis() + "");
                conversationJsonObject.put("creator", mSharedPref.getUserName());
                conversationJsonObject.put("eventID", eventId);
                conversationJsonObject.put("latestMessage", message);
            } catch (JSONException exception) {

            }
            HandleVolleyRequest.postConversationData(conversationJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mIsLoading = false;
                    cancelProgressDialog();
                    onAddConversationResponseReceived(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mIsLoading = false;
                    cancelProgressDialog();
                    Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void handleDataCall(int eventId, int index) {
        showProgressDialog("Fetching..");
        if (!mIsLoading) {
            mIsLoading = true;
            if (!mAllDataFetched) {
                if (VolleyConstants.IS_OFFLINE) {
                    cancelProgressDialog();
                    JSONObject responseContainer = Utils.loadJSONFromAsset(getContext(), R.raw.conversation_response);
                    onPageResponseReceived(responseContainer);
                } else {
                    HandleVolleyRequest.getConversationData(eventId, index, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            mIsLoading = false;
                            cancelProgressDialog();
                            onPageResponseReceived(response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mIsLoading = false;
                            cancelProgressDialog();
                            Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        }
    }

    private void onAddConversationResponseReceived(JSONObject responseContainer) {

        if (null != responseContainer) {
            Gson gson = new Gson();
            mConversationDataModelArrayList.add(0, gson.fromJson(responseContainer.toString(), ConversationDataModel.class));
            mConversationRecyclerAdapter.notifyDataSetChanged();
        }
    }

    private void onPageResponseReceived(JSONObject responseContainer) {

        if (null != responseContainer) {
            try {
                JSONArray dataArray = responseContainer.getJSONArray("conversationArray");
                if (dataArray.length() < 1) {
                    mAllDataFetched = true;
                    return;
                }
                for (int idx = 0; idx < dataArray.length(); idx++) {
                    Gson gson = new Gson();
                    JSONObject jsonObject = dataArray.getJSONObject(idx);
                    mConversationDataModelArrayList.add(gson.fromJson(jsonObject.toString(), ConversationDataModel.class));
                }
                mConversationRecyclerAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConversationRecyclerAdapter extends RecyclerView.Adapter<ConversationViewHolder> {

        public ConversationRecyclerAdapter() {
        }

        @Override
        public ConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_list_item, parent, false);
            return new ConversationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ConversationViewHolder holder, final int position) {
            holder.conversationMsg.setText(mConversationDataModelArrayList.get(position).getLatestMessage());
            holder.conversationCreator.setText(mConversationDataModelArrayList.get(position).getCreator());
            if (!TextUtils.isEmpty(mConversationDataModelArrayList.get(position).getCreatedTime())) {
                holder.conversationCreatedTime.setText((new Date(Long.parseLong(mConversationDataModelArrayList.get(position).getCreatedTime()))).toString());
            }
            if (!TextUtils.isEmpty(mConversationDataModelArrayList.get(position).getCreator())) {
                holder.firstCharTv.setText(mConversationDataModelArrayList.get(position).getCreator().charAt(0) + "");
            }
        }

        @Override
        public int getItemCount() {
            return mConversationDataModelArrayList.size();
        }

    }

    private class ConversationViewHolder extends RecyclerView.ViewHolder {

        public TextView conversationMsg;
        public TextView conversationCreator;
        public TextView conversationCreatedTime;
        public TextView firstCharTv;

        public ConversationViewHolder(View itemView) {
            super(itemView);
            conversationMsg = (TextView) itemView.findViewById(R.id.conversation_msg);
            conversationCreator = (TextView) itemView.findViewById(R.id.conversation_creator);
            conversationCreatedTime = (TextView) itemView.findViewById(R.id.conversation_created_time);
            firstCharTv = (TextView) itemView.findViewById(R.id.first_char_tv);

        }
    }

    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int verticalSpaceHeight;

        public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
            this.verticalSpaceHeight = verticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = verticalSpaceHeight;
        }
    }

}
