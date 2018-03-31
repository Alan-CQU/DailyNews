package example.com.daliynews;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

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


public class NewsActivity extends AppCompatActivity {
    //从上个页面获取要分析的news 网址
    private Intent intentReceiveURL;
    private String newsTitle;
    private String newsTime;
    private String newsContent;

    private TextView tvTitle;
    private TextView tvTime;
    private TextView tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_layout);

        tvTitle = (TextView) findViewById(R.id.tv_newsTitle);
        tvContent = (TextView) findViewById(R.id.tv_newsContent);


        //获取网址
        intentReceiveURL = getIntent();
        final String urlNews = intentReceiveURL.getStringExtra("URL");
        if (!urlNews.isEmpty()) {
            Log.d("tag", "url is :" + urlNews);
        }

        if (NetWorkUtil.isNetworkConnected(this)) {
            //下载html文件，分析
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
                        Log.d("tag", "得到html文件");
                        //Log.d("tag",xmlResult);
                        e.onNext(xmlResult);
                        saveToTxt(xmlResult, "news.txt");

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

                    if (doc != null) {
                        Log.d("html", "Jsoup实例化成功");
                    } else {

                    }

                    Elements elements = doc.getElementsByClass("story-body");

                    //截取部分后重新实例化document
                    doc = Jsoup.parseBodyFragment(elements.html());

                    //Log.d("html",doc.toString());
                    //saveToTxt(doc.toString(),"story_body.txt");
                    //获取标题
                    Elements title = doc.getElementsByClass("story-body__h1");
                    Log.d("html", "title of news =  " + title.text());

                    tvTitle.setText(title.text());

                    //获取时间
                    Elements time = doc.select("div.mini-info-list-wrap ul li div");
                    if (time != null) {
                        Log.d("html", "size of time is  " + time.size());
                        Log.d("html", "time is :" + time.text());
                    } else {
                        Log.d("html", "don't get time element");
                    }

                    //获取网页图片
                    Elements elementsSpanImg = doc.select("span.image-and-copyright-container");
                    Element[] img = new Element[elementsSpanImg.size()];
                    Log.d("html", "size of Img = " + elementsSpanImg.size());
//                for(int i=0;i<elementsSpanImg.size();i++){
//                    img[i] = elementsSpanImg.get(i).child(0);
//
//                    String srcUrl="";
//                    if(img[i].hasAttr("src")){
//                        srcUrl = img[i].attr("src");
//                    } else if(img[i].hasAttr("data-src")){
//                        srcUrl = img[i].attr("data-src");
//                    }
//
//                    Log.d("html",img[i].toString());
//                    Log.d("html","img url = " +srcUrl);
//                }


                    //获取正文内容
                    Elements elementsP = doc.select("div.story-body__inner p");
                    Log.d("html", "P size = " + elementsP.size());

                    for (Element e : elementsP) {
                        //Log.d("tag",e.text());
                        stringBuilderContent.append(e.text());
                    }

                    tvContent.setText(stringBuilderContent.toString());

                }
            };

            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(consumer);
        } else {
            Log.d("tag", "news activity, no network");
        }

    }


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
