/**
 * This activity is used for presenting detail news
 */

package example.com.daliynews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import example.com.daliynews.database.DBOperation;
import example.com.daliynews.until.DownLoadImgUtil;
import example.com.daliynews.until.NetWorkUtil;
import example.com.daliynews.until.SharedPreferenceCacheUtil;
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
 * present detail news
 */
public class NewsActivity extends AppCompatActivity {
    //从上个页面获取要分析的news 网址
    private Intent mIintentReceiveURL;
    private String mFormerPageImgUrl;
    private TextView mTitle;
    private WebView mContent;
    private ImageView mImg;
    private DBOperation mDbOperation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_layout);

        mDbOperation = new DBOperation(getApplication());
        mImg = (ImageView) findViewById(R.id.img_newPicture);
        mTitle = (TextView) findViewById(R.id.tv_newsTitle);
        mContent = findViewById(R.id.tv_newsContent);

        //获取网址  get the url of this news from the intent
        mIintentReceiveURL = getIntent();
        final String urlNews = mIintentReceiveURL.getStringExtra("URL");
        mFormerPageImgUrl = mIintentReceiveURL.getStringExtra("IMG_URL");

        //悬浮按钮
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        //If connected to a network
        if (NetWorkUtil.isNetworkConnected(this)) {

            //if we has a record in database
            if (!mDbOperation.isEmpty("img" + urlNews)) {
                //get data restored in preference
                SharedPreferences pref = getSharedPreferences("NewsDetailData", MODE_PRIVATE);
                mTitle.setText(pref.getString("title" + urlNews, "error"));

                String content = pref.getString("content" + urlNews, "error");
                String partOne = "<html><head></head><body style=\"text-align:justify;color:gray;\">";
                String partTwo = "</body></html>";
                String ready = partOne + content + partTwo;

                mContent.setVerticalScrollBarEnabled(false);

                mContent.loadData(ready, "text/html; charset=utf-8", "utf-8");

                byte[] img = mDbOperation.readImage("img" + urlNews);
                Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
                BitmapDrawable drawable = new BitmapDrawable(getApplication().getResources(), bitmap);
                mImg.setImageDrawable(drawable);

            } else {

                //下载html文件，分析  download html file and analyse
                Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> e) throws Exception {
                        //连接网络，获得xml数据
                        OkHttpClient okHttpClient = new OkHttpClient();
                        Request requset = new Request.Builder()
                                .url(urlNews)
                                .build();
                        final Call call = okHttpClient.newCall(requset);
                        Response response = null;
                        try {
                            response = call.execute();
                        } catch (IOException ee) {
                            ee.printStackTrace();
                        }

                        if (response != null) {
                            String xmlResult = response.body().string();
                            //Log.d("tag","得到html文件");
                            //Log.d("tag",xmlResult);
                            e.onNext(xmlResult);
                            //saveToTxt(xmlResult,"news.txt");

                        } else {
                            Log.d("tag", "没有获取到html文件");
                        }

                    }
                });

                Consumer<String> consumer = new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {

                        StringBuilder stringBuilderContent = new StringBuilder();
                        //doc实例化
                        Document doc = Jsoup.parse(s);

                        Elements elements = doc.getElementsByClass("story-body");
                        //截取部分后重新实例化document
                        doc = Jsoup.parseBodyFragment(elements.html());

                        //Log.d("html",doc.toString());
                        //saveToTxt(doc.toString(),"story_body.txt");
                        //获取标题
                        Elements title = doc.getElementsByClass("story-body__h1");
                        //Log.d("html","title of news =  "+ title.text());

                        //update  news title
                        mTitle.setText(title.text());
                        SharedPreferenceCacheUtil.editor.putString("title" + urlNews, title.text());

                        //获取正文内容
                        Elements elementsP = doc.select("div.story-body__inner p");
                        //Log.d("html","P size = "+ elementsP.size());
                        for (Element e : elementsP) {
                            //Log.d("tag",e.text());
                            stringBuilderContent.append(e.text() + "\n\n");
                        }

                        //set new content
                        String content = stringBuilderContent.toString();
                        String partOne = "<html><head></head><body style=\"text-align:justify;color:gray;\">";
                        String partTwo = "</body></html>";
                        String ready = partOne + content + partTwo;
                        mContent.setVerticalScrollBarEnabled(false);
                        mContent.loadData(ready, "text/html; charset=utf-8", "utf-8");

                        SharedPreferenceCacheUtil.editor.putString("content" + urlNews, stringBuilderContent.toString());
                        SharedPreferenceCacheUtil.editor.commit();

                        //获取网页图片
                        String srcUrl = "";
                        Elements elementsSpanImg = doc.select("span.image-and-copyright-container");
                        if (elementsSpanImg.size() != 0) {
                            srcUrl = elementsSpanImg.get(0).child(0).attr("src");
                            Log.d("tag", "img url is " + srcUrl);

                            if (!srcUrl.isEmpty()) {
                                DownLoadImgUtil task = new DownLoadImgUtil(mImg, getApplication(), true, "img" + urlNews);
                                task.execute(srcUrl);
                            } else {
//                            Log.d("tag","获取图片失败");
//                            Log.d("tag",elementsSpanImg.toString());
                                srcUrl = elementsSpanImg.get(0).child(0).attr("data-src");
                                Log.d("tag", "img url is" + srcUrl);
                                DownLoadImgUtil task = new DownLoadImgUtil(mImg, getApplication(), true, "img" + urlNews);
                                task.execute(srcUrl);
                            }

                        } else {  //this is for a news html that contains video

                            DownLoadImgUtil task = new DownLoadImgUtil(mImg, getApplication(), true, "img" + urlNews);
                            task.execute(mFormerPageImgUrl);
                        }


                    }
                };

                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(consumer);

            }

        } else {
            Log.d("tag", "news activity, no network");

            //get data restored in preference
            SharedPreferences pref = getSharedPreferences("NewsDetailData", MODE_PRIVATE);
            mTitle.setText(pref.getString("title" + urlNews, "error"));

            String partOne = "<html><head></head><body style=\"text-align:justify;color:gray;\">";
            String partTwo = "</body></html>";
            String ready = partOne + pref.getString("content" + urlNews, "error") + partTwo;
            mContent.setVerticalScrollBarEnabled(false);
            mContent.loadData(ready, "text/html; charset=utf-8", "utf-8");



            //get data restored in database
            boolean result = mDbOperation.isEmpty("img" + urlNews);
            Log.d("tag", "database has record? " + result);
            if (result == false) {
                byte[] img = mDbOperation.readImage("img" + urlNews);
                Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
                BitmapDrawable drawable = new BitmapDrawable(getApplication().getResources(), bitmap);
                mImg.setImageDrawable(drawable);
            }


        }

    }


    /**
     *
     * save some string into txt file
     *
     * @param inputText
     * @param fileName
     */
    public void saveToTxt(String inputText, String fileName) {
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            out = openFileOutput(fileName, Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(inputText);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.d("tag", fileName + " has saved");
    }
}
