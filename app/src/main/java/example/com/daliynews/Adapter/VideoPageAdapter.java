package example.com.daliynews.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import cn.jzvd.JZVideoPlayerStandard;
import example.com.daliynews.R;



/**
 * Created by CJ on 2018/3/27.
 */

public class VideoPageAdapter extends RecyclerView.Adapter<VideoPageAdapter.VideoViewHolder>{

    private Context mContext;
    private String url = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    public VideoPageAdapter(Context context){
        mContext = context;
    }
    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View childView = inflater.inflate(R.layout.video_page_layout,parent,false);

        VideoViewHolder view = new VideoViewHolder(childView);
        return view;
    }


    @Override
    public void onBindViewHolder(final VideoViewHolder holder, int position) {
        JZVideoPlayerStandard player = holder.videoPlayer;

        player.setUp(url, JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "DailyNews");

    }



    @Override
    public int getItemCount() {
        return 15;
    }

    class VideoViewHolder extends RecyclerView.ViewHolder{
        TextView tv;
        JZVideoPlayerStandard videoPlayer;
        public VideoViewHolder(View itemView){
            super(itemView);
            tv = (TextView)itemView.findViewById(R.id.tv_item_videotitle);
            videoPlayer =  (JZVideoPlayerStandard) itemView.findViewById(R.id.player_item_video);
        }
    }
}
