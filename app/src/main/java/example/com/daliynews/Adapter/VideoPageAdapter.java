package example.com.daliynews.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import example.com.daliynews.R;
import example.com.daliynews.interfaces.OnItemClickListener;


/**
 * Created by CJ on 2018/3/27.
 */

public class VideoPageAdapter extends RecyclerView.Adapter<VideoPageAdapter.VideoViewHolder> {

    private OnItemClickListener onItemClickListener;//声明接口变量

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View childView = inflater.inflate(R.layout.video_page_layout, parent, false);

        VideoViewHolder view = new VideoViewHolder(childView);
        return view;
    }


    @Override
    public void onBindViewHolder(final VideoViewHolder holder, int position) {
        //判断是否设置了监听器
        if (onItemClickListener != null) {

            //为ItemView设置监听器
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.onItemClickListener = mOnItemClickListener;
    }


    @Override
    public int getItemCount() {
        return 15;
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {

//        VideoView videoView;

        ImageView imgView;
        TextView title;

        public VideoViewHolder(View itemView) {
            super(itemView);
            //videoView = (VideoView) itemView.findViewById(R.id.videoView);
            imgView = itemView.findViewById(R.id.img_video);
            title = itemView.findViewById(R.id.tv_title);
        }
    }
}
