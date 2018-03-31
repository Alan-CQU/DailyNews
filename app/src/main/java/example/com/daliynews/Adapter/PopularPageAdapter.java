package example.com.daliynews.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import example.com.daliynews.R;
import example.com.daliynews.interfaces.OnItemClickListener;

/**
 * Created by CJ on 2018/3/26.
 */

public class PopularPageAdapter extends RecyclerView.Adapter<PopularPageAdapter.AuthorViewHolder> {


    ArrayList<String> titleList;
    ArrayList<String> descripeList;
    ArrayList<String> picUrlList;
    private Context context;

    //实例化接口
    private OnItemClickListener onItemClickListener;


    public PopularPageAdapter(Context context, ArrayList<List<String>> list) {
        this.context = context;

        if (list != null) {
            Log.d("tag", "container is not empty, size = " + list.size());

            titleList = (ArrayList<String>) list.get(0);
            descripeList = (ArrayList<String>) list.get(1);
            picUrlList = (ArrayList<String>) list.get(4);

            Log.d("tag", "titleList is not empty, size = " + titleList.size());
            Log.d("tag", "descripeList is not empty, size = " + descripeList.size());
            Log.d("tag", "picUrlList is not empty, size = " + picUrlList.size());
        } else {
            Log.d("tag", "Container is  empty ");
        }
    }


    @Override
    public PopularPageAdapter.AuthorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View childView = inflater.inflate(R.layout.popular_page_layout, parent, false);

        PopularPageAdapter.AuthorViewHolder viewHolder = new AuthorViewHolder(childView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final PopularPageAdapter.AuthorViewHolder holder, int position) {

        holder.mNickNameView.setText(titleList.get(position));
        holder.mMottoView.setText(descripeList.get(position));

        //添加监听事件
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(holder.itemView, holder.getLayoutPosition());
                }
            });
        }

    }

    //监听函数
    public void setOnItemClickListner(OnItemClickListener onItemClickListner) {

        this.onItemClickListener = onItemClickListner;
    }


    @Override
    public int getItemCount() {
        return 10;
    }

    class AuthorViewHolder extends RecyclerView.ViewHolder {

        //TODO list 每一行的内容
        ImageView imgItem;
        TextView mNickNameView;
        TextView mMottoView;

        public AuthorViewHolder(View itemView) {
            super(itemView);
            //TODO list 每一行内容初始化
            imgItem = itemView.findViewById(R.id.iv_portrait);
            mNickNameView = itemView.findViewById(R.id.tv_nickname);
            mMottoView = itemView.findViewById(R.id.tv_motto);
        }
    }
}
