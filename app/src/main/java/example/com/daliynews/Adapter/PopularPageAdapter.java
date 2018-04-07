package example.com.daliynews.Adapter;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import example.com.daliynews.R;
import example.com.daliynews.database.DBOperation;
import example.com.daliynews.interfaces.OnItemClickListener;
import example.com.daliynews.until.DownLoadImgUtil;

/**
 * Created by CJ on 2018/3/26.
 *
 * this is for homepage's recyclerView adapter
 */


/**
 *Adapter for TabFragmentPopular's recyclerView
 *
 */
public class PopularPageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


    //接受来popular fragment 的数据源
    //receive data from  popular fragment
    private ArrayList<String> titleList;
    private ArrayList<String> picUrlList;
    private ArrayList<String> dateList;
    private Application context;

    private final int TYPE_FOOTER =1;
    private final int TYPE_ITEM =0;
    private static int numOfNews = 10;
    public FootBar footBar;
    private DBOperation mDbOperation;

    //实例化点击时间接口
    private OnItemClickListener onItemClickListener;

    //构造函数  接受context参数 和 数据源  constructor

    /**
     *  constructor
     * @param context
     * @param list
     */
    public PopularPageAdapter(Application context, ArrayList<ArrayList<String>> list ){

        this.context = context;
        mDbOperation = new DBOperation(context);

        if(list!=null){
            Log.d("tag","container is not empty, size = " + list.size());

            titleList = (ArrayList<String>) list.get(0);
            dateList = (ArrayList<String>) list.get(3);
            picUrlList = (ArrayList<String>) list.get(4);

            Log.d("tag","titleList is not empty, size = " + titleList.size());
            //Log.d("tag","descripeList is not empty, size = " + descripeList.size());
           // Log.d("tag","picUrlList is not empty, size = " + picUrlList.size());
        } else {
            Log.d("tag","Container is  empty " );
        }
    }


    /**
     * updata data from internet
     * @param list
     */
    public void updateData( ArrayList<ArrayList<String>> list){
        titleList = (ArrayList<String>) list.get(0);
        dateList = (ArrayList<String>) list.get(3);
        picUrlList = (ArrayList<String>) list.get(4);
        if(footBar!=null){
            footBar.initFootMsg();
            initNumOfNews();

        }


    }


    /**
     * return a view used for show my news
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder holder =null;

        if (viewType == TYPE_ITEM) {
            View childView = inflater.inflate(R.layout.popular_page_layout,parent,false);

            holder = new AuthorViewHolder(childView);
        } else if (viewType == TYPE_FOOTER) {
            View childView = inflater.inflate(R.layout.foot_recyclerview_layout,parent,false);

            //save one instance of foot bar
            footBar =  new FootBar(childView);
            holder =footBar;
        }
        return  holder;
    }

    /**
     * Bind data with widget
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {


        if(holder instanceof AuthorViewHolder){

            ((AuthorViewHolder)holder).mTitleView.setText(titleList.get(position));
            ((AuthorViewHolder)holder).mTimeView.setText(dateList.get(position));

            String sTitle = titleList.get(position).substring(0,10).trim();

            if(!mDbOperation.isEmpty("img"+sTitle)){
                byte[] img = mDbOperation.readImage("img"+sTitle);
                Bitmap bitmap = BitmapFactory.decodeByteArray(img,0,img.length);
                BitmapDrawable drawable = new BitmapDrawable(context.getResources(),bitmap);
                ((AuthorViewHolder) holder).imgItem.setImageDrawable(drawable);
            }else {
                //异步下载图片并且加载  download Img
                DownLoadImgUtil task = new DownLoadImgUtil( ((AuthorViewHolder)holder).imgItem,context,true,"img"+sTitle);
                task.execute(picUrlList.get(position));
            }





            //添加监听事件
            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClickListener.onItemClick(holder.itemView,holder.getLayoutPosition());
                    }
                });
            }
        } else {

            //we can do something  for foot view, but not now
        }




    }


    //监听事件
    /**
     * set clickListener
     * @param onItemClickListner
     */
    public  void setOnItemClickListner(OnItemClickListener onItemClickListner){

        this.onItemClickListener=onItemClickListner;
    }


    /**
     *
     * @return the number of view in RecyclerView
     */
    @Override
    public int getItemCount() {
        return titleList.size() == 0 ? 0 : numOfNews + 1;
    }


    public void setNumOfNews(){
        numOfNews = 20;
        //update data
        notifyDataSetChanged();
    }

    public void initNumOfNews(){
        numOfNews = 10;
        //update data
        notifyDataSetChanged();
    }

    /**
     * get the view type
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }


    /**
     * this class just a view use for showing the  news in popularPage
     */
    class AuthorViewHolder extends RecyclerView.ViewHolder{

        //TODO list 每一行的内容
        ImageView imgItem;
        TextView mTitleView;
        TextView mTimeView;

        public AuthorViewHolder(View itemView){
            super(itemView);
            //TODO list 每一行内容初始化
            imgItem = (ImageView) itemView.findViewById(R.id.iv_portrait_popular);
            mTitleView = (TextView) itemView.findViewById(R.id.tv_title_popular);
            mTimeView = (TextView) itemView.findViewById(R.id.tv_time_popular);
        }
    }


    /**
     * user for show "Loading" or "It's the End"
     */
    public class FootBar extends RecyclerView.ViewHolder{

        TextView footMsg;
        ProgressBar progressBar;
        public FootBar(View itemView){
            super(itemView);
            footMsg = itemView.findViewById(R.id.foot_message);
            progressBar = itemView.findViewById(R.id.progressBar);
        }

        public void setFootMsg(){
            progressBar.setVisibility(View.GONE);
            footMsg.setText("It's the End!");
        }
        public void initFootMsg(){
            progressBar.setVisibility(View.VISIBLE);
            footMsg.setText("Loading");
        }
    }
}
