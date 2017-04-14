package com.brucetoo.listvideoplay.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.brucetoo.listvideoplay.MainActivity;
import com.brucetoo.listvideoplay.R;
import com.brucetoo.videoplayer.IViewTracker;
import com.brucetoo.videoplayer.Tracker;
import com.brucetoo.videoplayer.VisibleChangeListener;
import com.brucetoo.videoplayer.scrolldetector.ListScrollDetector;
import com.brucetoo.videoplayer.videomanage.interfaces.PlayerItemChangeListener;
import com.brucetoo.videoplayer.videomanage.interfaces.SingleVideoPlayerManager;
import com.brucetoo.videoplayer.videomanage.interfaces.VideoPlayerListener;
import com.brucetoo.videoplayer.videomanage.player.RatioImageView;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;
import com.squareup.picasso.Picasso;

/**
 * Created by Bruce Too
 * On 05/04/2017.
 * At 09:28
 */

public class ListSupportFragment extends Fragment implements View.OnClickListener, VisibleChangeListener, PlayerItemChangeListener, VideoPlayerListener {

    public static final String TAG = "ListSupportFragment";
    private ListView mListView;
    private ImageView mImageTop;
    private TextView mTextCalculator;
    private static final float VISIBLE_THRESHOLD = 0.5f;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_support, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (ListView) view.findViewById(R.id.list_view);
        QuickAdapter<VideoModel> adapter = new QuickAdapter<VideoModel>(getActivity(), R.layout.item_list_view_new, ListDataGenerater.datas) {
            @Override
            protected void convert(BaseAdapterHelper helper, VideoModel item) {
                RatioImageView imageCover = (RatioImageView) helper.getView(R.id.img_cover);
                Picasso.with(getActivity())
                    .load(item.coverImage)
                    .into(imageCover);
                imageCover.setOriginalSize(16,9);
                imageCover.setTag(R.id.tag_tracker_view,item.videoUrl);
                imageCover.setOnClickListener(ListSupportFragment.this);
            }
        };
        mListView.setAdapter(adapter);
        mListView.setRecyclerListener(new AbsListView.RecyclerListener() {
            @Override
            public void onMovedToScrapHeap(View view) {
                //the tracker view is moved to scrap,and be re-used,so we need detach view in decor
                Log.e(TAG, "onMovedToScrapHeap -> " + view.findViewById(R.id.img_cover));
                IViewTracker tracker = Tracker.getViewTracker(getActivity());
                if(tracker != null) {
                    View trackerView = tracker.getTrackerView();
                    if (trackerView != null && trackerView.equals(view.findViewById(tracker.getTrackerViewId()))) {
                        //TODO Configuration Changed may cause problem
//                        Tracker.detach(getActivity());

                    }
                }
            }
        });
        SingleVideoPlayerManager.getInstance().addPlayerItemChangeListener(this);
        SingleVideoPlayerManager.getInstance().addVideoPlayerListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.e(TAG, "onMovedToScrapHeap onClick -> " + v);
        if(!Tracker.isSameTrackerView(getActivity(),v)) {
            Tracker.attach(getActivity()).trackView(v).into(new ListScrollDetector(mListView)).visibleListener(this);
        }
        ((MainActivity) getActivity()).addDetailFragment();
    }


    @Override
    public void onVisibleChange(float visibleRatio, IViewTracker tracker) {
        Log.e(TAG, "onVisibleChange : edge -> " + tracker.getEdgeString());
        //only care about vertical scroll
        if(tracker.getEdge() != IViewTracker.LEFT_EDGE || tracker.getEdge() != IViewTracker.RIGHT_EDGE) {
//            if (visibleRatio <= 0.8) {
//                tracker.getFloatLayerView().setVisibility(View.INVISIBLE);
//            } else {
//                tracker.getFloatLayerView().setVisibility(View.VISIBLE);
//            }
        }
    }

    @Override
    public void onPlayerItemChanged(IViewTracker viewTracker) {
        Log.i(TAG, "onPlayerItemChanged ");
    }

    @Override
    public void onVideoSizeChangedMainThread(IViewTracker viewTracker,int width, int height) {
        Log.e(TAG, "onVideoSizeChangedMainThread");
    }

    @Override
    public void onVideoPreparedMainThread(IViewTracker viewTracker) {
        Log.e(TAG, "onVideoPreparedMainThread");
    }

    @Override
    public void onVideoCompletionMainThread(IViewTracker viewTracker) {
        Log.e(TAG, "onVideoCompletionMainThread");
    }

    @Override
    public void onErrorMainThread(IViewTracker viewTracker,int what, int extra) {
        Log.e(TAG, "onErrorMainThread");
    }

    @Override
    public void onBufferingUpdateMainThread(IViewTracker viewTracker,int percent) {
        Log.e(TAG, "onBufferingUpdateMainThread");
    }

    @Override
    public void onVideoStoppedMainThread(IViewTracker viewTracker) {
        Log.e(TAG, "onVideoStoppedMainThread");
    }

    @Override
    public void onInfoMainThread(IViewTracker viewTracker,int what) {
        Log.e(TAG, "onInfoMainThread");
    }
}
