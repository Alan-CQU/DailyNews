package example.com.daliynews.Adapter;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import example.com.daliynews.R;
import example.com.daliynews.database.DBOperation;
import example.com.daliynews.interfaces.OnItemClickListener;
import example.com.daliynews.until.DownLoadImgUtil;

/**
 * Created by CJ on 2018/3/27.
 *
 * this is for homepage's recyclerView adapter
 */


/**
 *Adapter for TabFragmentHome's recyclerView
 *
 */
public class HomePageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public static final int ONE_ITEM = 1;
    public static final int TWO_ITEM = 2;
    public static final int TYPE_FOOTER =3;


    private static int numOfNews = 10;

    public FootBar footBar;


    //datas from the internet
    ArrayList<String> mTitleList;
    ArrayList<String> mPicUrlList;
    ArrayList<String> mDateList;
    private Application mContext;
    private DBOperation mDbOperation;


    private OnItemClickListener onItemClickListener;//声明接口变量

    /**
     * Constructor
     * @param context
     * @param list  datas from internet
     */
   public HomePageAdapter(Application context, ArrayList<ArrayList<String>> list){
        this.mContext = context;

        if(list!=null){
            mTitleList = (ArrayList<String>) list.get(0);
            mDateList = (ArrayList<String>) list.get(3);
            mPicUrlList = (ArrayList<String>) list.get(4);

            mDbOperation = new DBOperation(context);

        } else {
            Log.d("tag","Container is  empty " );
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

        if(ONE_ITEM == viewType){
            View v = inflater.inflate(R.layout.home_page_layout_one,parent,false);
            holder = new OneViewHolder(v);
        }else if(viewType == TWO_ITEM) {
            View v = inflater.inflate(R.layout.home_page_layout_two,parent,false);
            holder = new TwoViewHolder(v);
        } else {
            View childView = inflater.inflate(R.layout.foot_recyclerview_layout,parent,false);
            //save one instance of foot bar
            footBar =  new FootBar(childView);
            holder =footBar;
        }

        return holder;
    }

    /**
     * Bind data with widget
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {


        if(holder instanceof OneViewHolder){
            ((OneViewHolder)holder).mTv.setText(mTitleList.get(position));
            String sTitle = mTitleList.get(position).substring(0,10).trim();

            if(!mDbOperation.isEmpty("img"+sTitle)){
                byte[] img = mDbOperation.readImage("img"+sTitle);
                Bitmap bitmap = BitmapFactory.decodeByteArray(img,0,img.length);
                BitmapDrawable drawable = new BitmapDrawable(mContext.getResources(),bitmap);
                ((OneViewHolder)holder).mImg.setImageDrawable(drawable);

            }else {
                //异步下载图片并且加载
                DownLoadImgUtil task = new DownLoadImgUtil(((OneViewHolder) holder).mImg,mContext,true,"img"+sTitle);
                task.execute(mPicUrlList.get(position));
            }



        } else if(holder instanceof TwoViewHolder) {

            String sTitle1 = mTitleList.get(position).substring(0,10).trim();
            ((TwoViewHolder)holder).mTitle.setText(mTitleList.get(position));
            ((TwoViewHolder)holder).mTime.setText(mDateList.get(position));


            if(!mDbOperation.isEmpty("img"+sTitle1)){

                byte[] img = mDbOperation.readImage("img"+sTitle1);
                Bitmap bitmap = BitmapFactory.decodeByteArray(img,0,img.length);
                BitmapDrawable drawable = new BitmapDrawable(mContext.getResources(),bitmap);
                ((TwoViewHolder)holder).mImgItem.setImageDrawable(drawable);

            }else {

                //异步下载图片并且加载
                DownLoadImgUtil task = new DownLoadImgUtil(((TwoViewHolder) holder).mImgItem,mContext,true,"img"+sTitle1);
                task.execute(mPicUrlList.get(position));
            }

        }


       //添加监听事件  add click listener
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(holder.itemView,holder.getLayoutPosition());
                }
            });
        }
    }


    //监听事件
    /**
     * set clickListener
     * @param mOnItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.onItemClickListener = mOnItemClickListener;
    }

    /**
     * return view type
     *
     * @param position  current view's  position
     * @return
     */
    @Override
    public int getItemViewType(int position){
        if(position==0){
            return ONE_ITEM;
        }else if(position + 1 == getItemCount()){
            return TYPE_FOOTER;
        }else {
            return TWO_ITEM;
        }
    }


    /**
     * updata data from internet
     * @param list
     */
    public void updateData( ArrayList<ArrayList<String>> list){
        mTitleList = (ArrayList<String>) list.get(0);
        mDateList = (ArrayList<String>) list.get(3);
        mPicUrlList = (ArrayList<String>) list.get(4);
        if(footBar!=null){
            footBar.initFootMsg();
            initNumOfNews();

        }
    }


    /**
     *
     * @return the number of view in RecyclerView
     */
    @Override
    public int getItemCount() {
        return mTitleList.size() == 0 ? 0 : numOfNews + 1;
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
     * this class just a view use for showing the first news in HomePage
     */
    class OneViewHolder extends RecyclerView.ViewHolder{

        ImageView mImg;
        TextView  mTv;
        public OneViewHolder(View view){
            super(view);
            mImg = (ImageView) view.findViewById(R.id.image_lead_story);
            mTv = (TextView) view.findViewById(R.id.tv_lead_story);
        }

    }


    /**
     * this class just a view use for showing the other news in HomePage
     */
    class TwoViewHolder extends RecyclerView.ViewHolder {

       //TODO list 每一行的内容
        ImageView mImgItem;
        TextView mTitle;
        TextView mTime;

        public TwoViewHolder(View itemView){
            super(itemView);
            //TODO list 每一行内容初始化
            mImgItem = (ImageView) itemView.findViewById(R.id.iv_portrait_home);
            mTitle = (TextView) itemView.findViewById(R.id.tv_title_home);
            mTime = (TextView) itemView.findViewById(R.id.tv_time_home);
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
