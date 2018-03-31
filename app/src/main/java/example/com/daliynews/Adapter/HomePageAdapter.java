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

public class HomePageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int ONE_ITEM = 1;
    public static final int TWO_ITEM = 2;

    //ArrayList<String> titleList;
    //ArrayList<String> descripeList;
    //ArrayList<String> picUrlList;
    //  private Context context;

    private OnItemClickListener onItemClickListener;//声明接口变量

   /* public HomePageAdapter(Context context, ArrayList<List<String>> list){
        this.context = context;

        if(list!=null){
            Log.d("tag","container is not empty, size = " + list.size());

            titleList = (ArrayList<String>) list.get(0);
            descripeList = (ArrayList<String>) list.get(1);
            picUrlList = (ArrayList<String>) list.get(4);

            Log.d("tag","titleList is not empty, size = " + titleList.size());
            Log.d("tag","descripeList is not empty, size = " + descripeList.size());
            Log.d("tag","picUrlList is not empty, size = " + picUrlList.size());
        } else {
            Log.d("tag","Container is  empty " );
        }
    }*/

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder holder = null;

        if (ONE_ITEM == viewType) {
            View v = inflater.inflate(R.layout.home_page_layout_one, parent, false);
            holder = new OneViewHolder(v);
        } else {
            View v = inflater.inflate(R.layout.popular_page_layout, parent, false);
            //holder = new OneViewHolder(v);
            holder = new TwoViewHolder(v);
        }


        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
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


    /*@Override
    public void onBindViewHolder(final HomePageAdapter.TwoViewHolder holder, int position){

        holder.mNickNameView.setText(titleList.get(position));
        holder.mMottoView.setText(descripeList.get(position));

        //添加监听事件
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(holder.itemView,holder.getLayoutPosition());
                }
            });
        }
    }*/
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.onItemClickListener = mOnItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ONE_ITEM;
        } else {
            return TWO_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return 15;
    }

    //item_one
    class OneViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        TextView tv;

        public OneViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.image_item_one);
            tv = view.findViewById(R.id.tv_item_one);
        }

    }

    //fragment1
    class TwoViewHolder extends RecyclerView.ViewHolder {
        /* ImageView img;
         TextView  tv,tv_time;
         public TwoViewHolder(View view){
             super(view);
             img = (ImageView) view.findViewById(R.id.img_item);
             tv = (TextView) view.findViewById(R.id.tv_item);
             tv_time = (TextView) view.findViewById(R.id.tv_item_time);
         }*/
        //TODO list 每一行的内容
        ImageView imgItem;
        TextView mNickNameView;
        TextView mMottoView;

        public TwoViewHolder(View itemView) {
            super(itemView);
            //TODO list 每一行内容初始化
            imgItem = itemView.findViewById(R.id.iv_portrait);
            mNickNameView = itemView.findViewById(R.id.tv_nickname);
            mMottoView = itemView.findViewById(R.id.tv_motto);
        }

    }
}
