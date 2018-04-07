package example.com.daliynews.until;


import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import example.com.daliynews.database.DBOperation;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 *
 * this class are used for download img from Internet and set source file of ImageView
 */

public class DownLoadImgUtil extends AsyncTask<String ,Void,BitmapDrawable> {
    private ImageView mImageView;
    private String url;
    private Application mContext;
    private boolean mCache =false;
    private String mName;


    /**
     * constructor
     * @param imageView
     * @param context
     */
    public DownLoadImgUtil(ImageView imageView , Application context){
        mImageView = imageView;
        mContext = context;

    }

    /**
     * Constructor
     * @param imageView
     * @param context
     * @param cache   true is keep  img in database
     * @param name
     */
    public DownLoadImgUtil(ImageView imageView , Application context , boolean cache, String name){
        mImageView = imageView;
        mContext = context;
        mCache = cache;
        mName = name;

    }


    @Override
    protected BitmapDrawable doInBackground(String... params) {
        url = params[0];
        Bitmap bitmap = downLoadBitmap(url,mCache,new DBOperation(mContext),mName);
        BitmapDrawable drawable = new BitmapDrawable(mContext.getResources(),bitmap);
        return  drawable;
    }


    /**
     * dowmload img
     *
     * @param url
     * @param cache
     * @param dbOperation
     * @param name
     * @return
     */

    public static Bitmap downLoadBitmap(String url , boolean cache, DBOperation dbOperation, String name) {
        Bitmap bitmap = null;


        // if we have down this img, and we can return just from the data base
//        if(dbOperation.isEmpty("img"+name)==false){
//            byte[] img = dbOperation.readImage("img"+url);
//            bitmap = BitmapFactory.decodeByteArray(img,0,img.length);
//            return bitmap;
//        }

        // download the img from internet
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            byte[] img = response.body().bytes();

            //bitmap = BitmapFactory.decodeStream(response.body().byteStream());
            bitmap = BitmapFactory.decodeByteArray(img,0,img.length);

            //if need cache , save in database
            if(cache){
                //if database don't have record, save the picture
                if(dbOperation.isEmpty(name)){
                    dbOperation.saveImage(compressImage(bitmap) ,name);
                    Log.d("tag","insert img data");
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }




    /**
     * 质量压缩方法
     * @param image
     * @return
     */
    public static byte[]  compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;
        while (baos.toByteArray().length / 1024 > 500) { // 循环判断如果压缩后图片是否大于500kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        return baos.toByteArray();
    }


    @Override
    protected void onPostExecute(BitmapDrawable drawable) {
        super.onPostExecute(drawable);

        if ( mImageView != null && drawable != null){
            mImageView.setImageDrawable(drawable);
        }
    }
}
