package example.com.daliynews.Fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import example.com.daliynews.Adapter.HomePageAdapter;
import example.com.daliynews.Adapter.PopularPageAdapter;
import example.com.daliynews.NewsActivity;
import example.com.daliynews.R;
import example.com.daliynews.interfaces.EndLessOnScrollListener;
import example.com.daliynews.interfaces.OnItemClickListener;
import example.com.daliynews.until.NetWorkUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by CJ on 2018/3/27.
 *
 * a fragment used for showing HomePage news
 */

public class TabFragmentHome extends Fragment {

    private View mRootView;
    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private boolean isLoading;


    private ArrayList<ArrayList<String>> containerList = new ArrayList<ArrayList<String>>();
    private HomePageAdapter mAdapter;


    /**
     * get new Fragment
     * @return
     */
    public static Fragment newInstance() {
        TabFragmentHome fragment = new TabFragmentHome();
        return fragment;

    }

    /**
     * download xmlFile
     *
     * @return  List of data
     */

    public ArrayList<ArrayList<String>> downLoadXmlFile(){

        ArrayList<ArrayList<String>> resultList =new ArrayList<ArrayList<String>>();

        //连接网络，获得xml数据
        OkHttpClient okHttpClient = new OkHttpClient();
        Request requset = new Request.Builder()
                .url("http://feeds.bbci.co.uk/news/world/rss.xml?edition=uk")
                .build();
        Call call = okHttpClient.newCall(requset);
        Response response=null;
        try{
            response= call.execute();
            if(response!=null){
                String xmlResult = response.body().string();
                resultList  = parseXMLWithPull(xmlResult);
                //make a copy
                containerList = resultList;
            }else {
                Log.d("tag","response is null");
            }

        } catch (IOException e){
            e.printStackTrace();
            Log.d("tag","xml解析失败");
        }
        return resultList;
    }




    /**
     * do configuration of recyclerView
     *
     * @param container
     */
    public void setConfigOfRecyclerVIew(final ArrayList<ArrayList<String>> container){
        //SwipeRefreshLayout initial
        mRefreshLayout = (SwipeRefreshLayout)mRootView.findViewById(R.id.layout_swipe_refresh);
        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                //mRefreshLayout.setRefreshing(true);
            }
        });

        //up pull for refreshing
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            public void onRefresh() {

                if(NetWorkUtil.isNetworkConnected(getContext())){
                    //finish refresh operation
                    //数据重新加载完成后，提示数据发生改变，并且设置现在不再刷新
                    containerList.clear();
                    Log.d("tag","after clear ,contanerList size "+containerList.size());

                    Observable< ArrayList<ArrayList<String>>> observable = (Observable<ArrayList<ArrayList<String>>>) Observable.create(new ObservableOnSubscribe< ArrayList<ArrayList<String>>>() {
                        @Override
                        public void subscribe(ObservableEmitter<ArrayList<ArrayList<String>> > emitter) throws Exception {
                            emitter.onNext(downLoadXmlFile());
                            Log.d("tag","observable send it");
                        }
                    });
                    Consumer<ArrayList<ArrayList<String>>> consumer = new Consumer<ArrayList<ArrayList<String>>>() {
                        @Override
                        public void accept(ArrayList<ArrayList<String>> arrayLists) throws Exception {

                            if(arrayLists.size()!=0){
                                Log.d("tag","observer get it");
                                Log.d("tag","observer ,size of container "+arrayLists.size());
                                containerList = arrayLists;
                                Log.d("tag","after reload ,contanerList size "+containerList.size());

                                //update data
                                mAdapter.updateData(arrayLists);
                                mRefreshLayout.setRefreshing(false);
                            } else {
                                Log.d("tag","observer do not get it");
                            }

                        }
                    };
                    observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(consumer);

                    //上述两个函数 和 refresh函数线程不同步。emm 就很难受
                    //Log.d("tag","refresh funtion ,after reload ,contanerList size "+containerList.size());
                    //Toast.makeText(getContext(), "这里需要刷新", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(), "Sorry, there is no network for refreshing.", Toast.LENGTH_SHORT).show();
                }


            }
        });

        //RecycleView 的初始化
        mRecyclerView =(RecyclerView) mRootView.findViewById(R.id.recycler);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new HomePageAdapter(getActivity().getApplication(),containerList);

        //为Adpter 设置监听事件
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getActivity(), "position is "+ position, Toast.LENGTH_SHORT).show();
                //send the url for news activity
                Intent intent = new Intent(getActivity(),NewsActivity.class);
                intent.putExtra("URL",((ArrayList<String>)containerList.get(2)).get(position));
                intent.putExtra("IMG_URL",((ArrayList<String>)containerList.get(4)).get(position));
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        //set scroll listener  and realize load more content
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            //当前最后一个可见view的位置
            int lastVisibleItemPosition;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // Log.d("tag", "StateChanged = " + newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition + 1 == mAdapter.getItemCount()) {
                    Log.d("tag", "loading executed");

                    boolean isRefreshing = mRefreshLayout.isRefreshing();
                    Log.d("tag", "is refreshing state? " +isRefreshing);
                    if (isRefreshing) {
                        mAdapter.notifyItemRemoved(mAdapter.getItemCount());
                        return;
                    }
                    if (!isLoading) {
                        isLoading = true;

                        //we have 20 rows totally,but we just show 10 rows at the first time.
                        if(mAdapter.getItemCount()<20){
                            mAdapter.setNumOfNews();
                            Log.d("tag", " refreshing finish ");
                        }else {
                            //TODO: set the text of the buttom view
                            mAdapter.footBar.setFootMsg();
                        }

                        isLoading = false;
                    }
                }

            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //Log.d("tag", "onScrolled");
                lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();
            }
        });
    }


    /**
     * init widgets and get news data from internet ,then use the data to init it's adapter
     *
     * @param inflater
     * @param container
     * @param saveInstanceState
     * @return
     */

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle saveInstanceState){

        mRootView = inflater.inflate(R.layout.fragment_tab,container,false);

        if(NetWorkUtil.isNetworkConnected(getContext())){

            Observable<ArrayList<ArrayList<String>>> observable = (Observable<ArrayList<ArrayList<String>>>) Observable.create(new ObservableOnSubscribe<ArrayList<ArrayList<String>>>() {
                @Override
                public void subscribe(final ObservableEmitter<ArrayList<ArrayList<String>>> emitter ) throws Exception {
                    emitter.onNext(downLoadXmlFile());
                }
            });

            Consumer<ArrayList<ArrayList<String>>> consumer = new Consumer<ArrayList<ArrayList<String>>>() {
                @Override
                public void accept(ArrayList<ArrayList<String>> arrayLists) throws Exception {
                    setConfigOfRecyclerVIew(arrayLists);
                }
            };
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(consumer);

            //new DownloadTask().execute();

        } else {
            Log.d("tag","网络无连接");
            Toast.makeText(getContext(), "网络无连接", Toast.LENGTH_SHORT).show();
        }

        return  mRootView;
    }



    /**
     *
     * analyse the xml file and extract some information we need
     *
     * @param xmlData
     * @return useful information
     */
    private ArrayList<ArrayList<String>> parseXMLWithPull(String xmlData) {

        ArrayList<ArrayList<String>> container = new ArrayList<ArrayList<String>>();
        ArrayList<String> descriptionList = new ArrayList<String>();
        ArrayList<String> linkList = new ArrayList<String>();
        ArrayList<String> dateList = new ArrayList<String>();
        ArrayList<String> pictureUrlList = new ArrayList<String>();
        ArrayList<String> titleList = new ArrayList<String>();

        int count =0;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType = xmlPullParser.getEventType();

            String title = "";
            String description = "";
            String link = "";
            String date="";
            String pictureUrl="";

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = xmlPullParser.getName();
                switch (eventType) {
                    //  开始解析某个结点
                    case XmlPullParser.START_TAG: {
                        if ("title".equals(nodeName)) {
                            title = xmlPullParser.nextText();

                        } else if ("description".equals(nodeName)) {
                            description = xmlPullParser.nextText();

                        } else if ("link".equals(nodeName)) {
                            link = xmlPullParser.nextText();

                        } else if ("pubDate".equals(nodeName)) {
                            date = xmlPullParser.nextText();

                        } else if ("media:thumbnail".equals(nodeName)) {
                            pictureUrl = xmlPullParser.getAttributeValue(2).toString();

                        }
                        break;
                    }

                    //  完成解析某个结点
                    case XmlPullParser.END_TAG: {
                        if ("item".equals(nodeName)) {

                            titleList.add(title);
                            descriptionList.add(description);
                            linkList.add(link);
                            dateList.add(date);
                            pictureUrlList.add(pictureUrl);
                            count++;

                        }
                        break;
                    }
                    default:
                        break;
                }
                if(count==20){
                    break;
                }
                eventType = xmlPullParser.next();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        container.add(titleList);
        container.add(descriptionList);
        container.add(linkList);
        container.add(dateList);
        container.add(pictureUrlList);

        //Log.d("tag","解析结束");
        return  container;
    }




}
