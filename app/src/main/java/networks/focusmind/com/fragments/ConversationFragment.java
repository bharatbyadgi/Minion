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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.github.siyamed.shapeimageview.mask.PorterShapeImageView;
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
        // VerticalSpaceItemDecoration verticalSpaceItemDecoration = new VerticalSpaceItemDecoration(25);
        // mRecyclerView.addItemDecoration(verticalSpaceItemDecoration);

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
        ImageView sendButton = (ImageView) rootView.findViewById(R.id.send_message);
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

    private class ConversationRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        ConversationRecyclerAdapter() {
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            /*View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_list_item, parent, false);
            return new ConversationViewHolder(view);*/
            switch (viewType) {
               /* case ROW_TYPE_LOAD_EARLIER_MESSAGES:
                    return new LoadEarlierMsgsViewHolder(mLayoutInflater.inflate(R.layout
                            .chat_load_more_messaages, parent, false));*/
                case ROW_TYPE_SENDER:
                    return new SenderMsgViewHolder(LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.oneonone_chat_bubble_right,
                                    parent, false));
                case ROW_TYPE_RECEIVER:
                    return new ReceiverMsgViewHolder(LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.oneonone_chat_bubble_left, parent, false));
                default:
                    return null;
            }

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
           /* holder.conversationMsg.setText(mConversationDataModelArrayList.get(position).getLatestMessage());
            holder.conversationCreator.setText(mConversationDataModelArrayList.get(position).getCreator());
            if (!TextUtils.isEmpty(mConversationDataModelArrayList.get(position).getCreatedTime())) {
                holder.conversationCreatedTime.setText((new Date(Long.parseLong(mConversationDataModelArrayList.get(position).getCreatedTime()))).toString());
            }
            if (!TextUtils.isEmpty(mConversationDataModelArrayList.get(position).getCreator())) {
                holder.firstCharTv.setText(mConversationDataModelArrayList.get(position).getCreator().charAt(0) + "");
            }*/


            switch (getItemViewType(position)) {

                case ROW_TYPE_SENDER:
                    final SenderMsgViewHolder senderMsgViewHolder = (SenderMsgViewHolder) holder;
                    final ConversationDataModel message = mConversationDataModelArrayList.get(position );

                    senderMsgViewHolder.message.setText(message.getLatestMessage());
                    senderMsgViewHolder.timestamp.setText(Utils.convertTimestampToDate(
                            Long.parseLong(message.getCreatedTime()),getContext()));

                    if (!TextUtils.isEmpty(mConversationDataModelArrayList.get(position).getCreator())) {
                        senderMsgViewHolder.firstCharTv.setText(mConversationDataModelArrayList.get(position).getCreator().charAt(0) + "");
                    }
                    break;


                case ROW_TYPE_RECEIVER:
                    final ReceiverMsgViewHolder receiverMsgViewHolder = (ReceiverMsgViewHolder)holder;
                    final ConversationDataModel messageReceiver = mConversationDataModelArrayList.get(position);

                    receiverMsgViewHolder.message.setText(messageReceiver.getLatestMessage());
                    receiverMsgViewHolder.timestamp.setText(Utils.convertTimestampToDate(
                            Long.parseLong(messageReceiver.getCreatedTime()),getContext()));

                    if (!TextUtils.isEmpty(mConversationDataModelArrayList.get(position).getCreator())) {
                        receiverMsgViewHolder.firstCharTv.setText(mConversationDataModelArrayList.get(position).getCreator().charAt(0) + "");
                    }

                    break;

            }
        }

        @Override
        public int getItemCount() {
            return mConversationDataModelArrayList.size();
        }


        @Override
        public int getItemViewType(int position) {

           /* if (position ==  0) {
                return ROW_TYPE_LOAD_EARLIER_MESSAGES; // row load earlier messages
            } else*/
            //if (mConversationDataModelArrayList.get(position).getCreator()
            //  .equalsIgnoreCase(mSharedPref.getUserName())) {
            if(position % 2 == 0)
            {
                return ROW_TYPE_SENDER; // sender row;
            } else {
                return ROW_TYPE_RECEIVER; // receiver row;
            }
        }
    }


    static class SenderMsgViewHolder extends RecyclerView.ViewHolder {

        TextView message;

        TextView timestamp;

        ImageView image;

        ImageView msgtick;
        PorterShapeImageView imageProfile;

        TextView fileName;
        LinearLayout fileView;
        ImageView fileIcon;
        TextView fileExtension;
        ImageView fileDownload;
        ProgressBar progress;
        ProgressBar progressBackground;
        public TextView firstCharTv;


        public SenderMsgViewHolder(View view) {
            super(view);
            // ButterKnife.bind(this, view);

            message = (TextView) view.findViewById(R.id.textViewMessage);
            timestamp = (TextView) view.findViewById(R.id.textViewTime);
            image = (ImageView) view.findViewById(R.id.imageViewImageMessage);
            // msgtick = (ImageView) view.findViewById(R.id.imageViewmessageTicks);
            imageProfile = (PorterShapeImageView) view.findViewById(R.id.ivProfile);
            firstCharTv = (TextView) itemView.findViewById(R.id.first_char_tv);

            fileName = (TextView) view.findViewById(R.id.fileName);
            fileView = (LinearLayout) view.findViewById(R.id.fileView);
            fileIcon = (ImageView) view.findViewById(R.id.iv_file_icon);
            fileExtension = (TextView) view.findViewById(R.id.tv_file_extension);
            fileDownload = (ImageView) view.findViewById(R.id.iv_file_download);
            progress = (ProgressBar) view.findViewById(R.id.progress);
            progressBackground = (ProgressBar) view.findViewById(R.id.progress_background);

        }
    }


    static class ReceiverMsgViewHolder extends RecyclerView.ViewHolder {

        //@BindView(R.id.textViewMessage)
        TextView message;

        // @BindView(R.id.textViewTime)
        TextView timestamp;

        // @BindView(R.id.imageViewImageMessage)
        ImageView image;

        //  @BindView(R.id.imageViewmessageTicks)
        ImageView msgtick;

        //@BindView(R.id.ivProfile)
        PorterShapeImageView imageProfile;

        TextView fileName;
        LinearLayout fileView;
        ImageView fileIcon;
        public TextView firstCharTv;

        TextView fileExtension;
        ImageView fileDownload;
        ProgressBar progress;
        ProgressBar progressBackground;

        public ReceiverMsgViewHolder(View view) {
            super(view);
            //ButterKnife.bind(this, view);

            message = (TextView) view.findViewById(R.id.textViewMessage);
            timestamp = (TextView) view.findViewById(R.id.textViewTime);
            image = (ImageView) view.findViewById(R.id.imageViewImageMessage);
            msgtick = (ImageView) view.findViewById(R.id.imageViewmessageTicks);
            imageProfile = (PorterShapeImageView) view.findViewById(R.id.ivProfile);
            firstCharTv = (TextView) itemView.findViewById(R.id.first_char_tv);

            fileName = (TextView) view.findViewById(R.id.fileName);
            fileView = (LinearLayout) view.findViewById(R.id.fileView);
            fileIcon = (ImageView) view.findViewById(R.id.iv_file_icon);
            fileExtension = (TextView) view.findViewById(R.id.tv_file_extension);
            fileDownload = (ImageView) view.findViewById(R.id.iv_file_download);
            progress = (ProgressBar) view.findViewById(R.id.progress);
            progressBackground = (ProgressBar) view.findViewById(R.id.progress_background);

        }
    }



    private static final int ROW_TYPE_LOAD_EARLIER_MESSAGES = 0;
    private static final int ROW_TYPE_SENDER = 1;
    private static final int ROW_TYPE_RECEIVER = 2;

   /* private class ConversationViewHolder extends RecyclerView.ViewHolder {

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
    }*/

  /*  public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

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
*/
}
