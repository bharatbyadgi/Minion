package networks.focusmind.com.fragments;

import android.content.Context;
import android.content.Intent;
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
import com.tubb.smrv.SwipeMenuLayout;
import com.tubb.smrv.SwipeMenuRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import networks.focusmind.com.VolleyConstants.VolleyConstants;
import networks.focusmind.com.activity.EventDetailActivity;
import networks.focusmind.com.activity.HomeActivity;
import networks.focusmind.com.minion.R;
import networks.focusmind.com.model.EventDetailDataModel;
import networks.focusmind.com.request.HandleVolleyRequest;
import networks.focusmind.com.utils.StringConstants;
import networks.focusmind.com.utils.Utils;

import static android.support.v7.widget.helper.ItemTouchHelper.LEFT;
import static android.support.v7.widget.helper.ItemTouchHelper.RIGHT;


public class EventDetailsFragment extends BaseFragment {


    private Context mContext;
    private String mPageDataUrl;
    private SwipeMenuRecyclerView mRecyclerView;


    public static EventDetailsFragment newInstance(Context context, String pageDataUrl) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        fragment.mContext = context;
        fragment.mPageDataUrl = pageDataUrl;
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_event_layout, container, false);
        mRecyclerView = (SwipeMenuRecyclerView) rootView.findViewById(R.id.event_page_recycler_view);
        handleDataCall();
        return rootView;
    }

    private void handleDataCall() {
        showProgressDialog("Fetching..");
        if (VolleyConstants.IS_OFFLINE) {
            cancelProgressDialog();
            JSONObject responseContainer = Utils.loadJSONFromAsset(mContext, R.raw.event_detail_response);
            onPageResponseReceived(responseContainer);
        } else {
            HandleVolleyRequest.getEventsData("amsuser", new JSONObject(), new Response.Listener<JSONObject>() {
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
                JSONArray dataArray = responseContainer.getJSONArray("eventdetails");
                ArrayList<EventDetailDataModel> eventDetailDataModelList = new ArrayList<>();
                for (int idx = 0; idx < dataArray.length(); idx++) {
                    Gson gson = new Gson();
                    JSONObject jsonObject = dataArray.getJSONObject(idx);
                    eventDetailDataModelList.add(gson.fromJson(jsonObject.toString(), EventDetailDataModel.class));
                }
                setUpRecyclerView(eventDetailDataModelList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    EventDetailRecyclerAdapter eventDetailRecyclerAdapter;
    private void setUpRecyclerView(final ArrayList<EventDetailDataModel> eventDetailDataModelList) {
        eventDetailRecyclerAdapter = new EventDetailRecyclerAdapter(eventDetailDataModelList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        VerticalSpaceItemDecoration verticalSpaceItemDecoration = new VerticalSpaceItemDecoration(25);
        mRecyclerView.setAdapter(eventDetailRecyclerAdapter);
        mRecyclerView.addItemDecoration(verticalSpaceItemDecoration);

        /*ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    // Get RecyclerView item from the ViewHolder
                    View itemView = viewHolder.itemView;

                    Paint p = new Paint();
                    if (dX > 0) {
                        p.setColor(getResources().getColor(R.color.off_white));
                        Bitmap icon = BitmapFactory.decodeResource(
                                getContext().getResources(), android.R.drawable.ic_menu_edit);
                        c.drawBitmap(icon,
                                (float) itemView.getRight() - 20 - icon.getWidth(),
                                (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - icon.getHeight()) / 2,
                                p);
                        c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                                (float) itemView.getBottom(), p);
                    } else {
                        p.setColor(getResources().getColor(R.color.off_white));
                        Bitmap icon = BitmapFactory.decodeResource(
                                getContext().getResources(), android.R.drawable.ic_menu_delete);
                        c.drawBitmap(icon,
                                (float) itemView.getLeft() + 20,
                                (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - icon.getHeight()) / 2,
                                p);
                        c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                                (float) itemView.getRight(), (float) itemView.getBottom(), p);
                    }
                    final float alpha = 1 - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);
                } else {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                if (swipeDir == LEFT) {
                    HandleVolleyRequest.deleteDataFromServer(VolleyConstants.GET_EVENTS, eventDetailDataModelList.get(viewHolder.getAdapterPosition()).getEventID());
                    eventDetailDataModelList.remove(viewHolder.getAdapterPosition());
                    eventDetailRecyclerAdapter.notifyDataSetChanged();
                    mRecyclerView.setAdapter(eventDetailRecyclerAdapter);
                } else if (swipeDir == RIGHT) {
                    mRecyclerView.setAdapter(eventDetailRecyclerAdapter);
                    HashMap<String, String> editTextMap = Utils.getArrayListFromJson(getContext(), R.raw.post_data, "postEvent");
                    Fragment frag = AddDetailsFragment.newInstance(editTextMap, eventDetailDataModelList.get(viewHolder.getAdapterPosition()), "Edit Event");
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.openAddEditFragment(frag, "Edit Event");
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
*/
    }


    private class EventDetailRecyclerAdapter extends RecyclerView.Adapter<EventsDetailViewHolder> {

        ArrayList<EventDetailDataModel> mEventDetailDataModelList;

        public EventDetailRecyclerAdapter(ArrayList<EventDetailDataModel> eventDetailDataModelList) {
            mEventDetailDataModelList = eventDetailDataModelList;
        }

        @Override
        public EventsDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_detail_list_item, parent, false);
            return new EventsDetailViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final EventsDetailViewHolder holder, final int position) {

            SwipeMenuLayout itemView = (SwipeMenuLayout) holder.itemView;
            final EventDetailDataModel eventDetailDataModel = mEventDetailDataModelList.get(position);
            itemView.setSwipeEnable(true);
            itemView.setOpenInterpolator(mRecyclerView.getOpenInterpolator());
            itemView.setCloseInterpolator(mRecyclerView.getCloseInterpolator());

            holder.eventName.setText(eventDetailDataModel.getEventName());
            holder.eventDate.setText(eventDetailDataModel.getEventCreatedTime());
            if (eventDetailDataModel.getHasConversation()) {
                // holder.conversationTv.setText("View Conversation");
            }
            holder.btDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEventDetailDataModelList.remove(holder.getAdapterPosition());
                    eventDetailRecyclerAdapter.notifyItemRemoved(holder.getAdapterPosition());
                }
            });

            holder.conversationTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.openConversationFragment(eventDetailDataModel.getEventID(), "Conversation");
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //  showDetails(currentView, mEventDetailDataModelList.get(position));
                    Intent intentEventDetails = new Intent(getActivity(),EventDetailActivity.class);
                    intentEventDetails.putExtra(StringConstants.PARAM_EVENT_DETAIL,
                            eventDetailDataModel);
                    startActivity(intentEventDetails);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mEventDetailDataModelList.size();
        }

    }


  /*  private void showDetails(View view, EventDetailDataModel eventDetailDataModel) {
        View detailView = view.findViewById(R.id.detail_card);
        View collapsedView = view.findViewById(R.id.collapsed_event_card);
        if (view.findViewById(R.id.collapsed_event_card).getVisibility() == View.VISIBLE) {
//            Utils.setAnimation(detailView, collapsedView);
            detailView.setVisibility(View.VISIBLE);
            collapsedView.setVisibility(View.GONE);
            TextView eventDetailName = (TextView) view.findViewById(R.id.event_detail_name);
            eventDetailName.setText(eventDetailDataModel.getEventName());
            TextView eventDetailDesc = (TextView) view.findViewById(R.id.event_detail_description);
            eventDetailDesc.setText(eventDetailDataModel.getDescription());
            TextView eventDetailDate = (TextView) view.findViewById(R.id.event_detail_date);
            eventDetailDate.setText(eventDetailDataModel.getEventCreatedTime());
            TextView eventDetailTime = (TextView) view.findViewById(R.id.event_detail_time);
        } else {
//            Utils.setAnimation(collapsedView, detailView);
            detailView.setVisibility(View.GONE);
            collapsedView.setVisibility(View.VISIBLE);
        }
    }*/

    private class EventsDetailViewHolder extends RecyclerView.ViewHolder {

        TextView eventName;
        TextView eventDate;
        ImageView conversationTv;
        View btDelete;
        EventsDetailViewHolder(View itemView) {
            super(itemView);
            eventName = (TextView) itemView.findViewById(R.id.event_name);
            eventDate = (TextView) itemView.findViewById(R.id.event_date);
            conversationTv = (ImageView) itemView.findViewById(R.id.img_chat);
            btDelete = itemView.findViewById(R.id.btDelete);
        }
    }

    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int verticalSpaceHeight;

        VerticalSpaceItemDecoration(int verticalSpaceHeight) {
            this.verticalSpaceHeight = verticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = verticalSpaceHeight;
        }
    }


}
