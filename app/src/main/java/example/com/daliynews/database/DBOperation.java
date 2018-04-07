package example.com.daliynews.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import example.com.daliynews.R;

public class DBOperation {
    private static SQLiteHelper dbhelper;
    private Context context;

    //要操作数据库操作实例首先得得到数据库操作实例
    public DBOperation(Context context) {
        this.context=context;
        this.dbhelper = SQLiteHelper.getInstance(context);
    }


    /**
     * save ImgData in database
     *
     * @param imgData
     * @param name
     */
    public static void saveImage( byte[] imgData ,String name){
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("name", name);
        cv.put("avatar", imgData);//图片转为二进制
        db.insert("Img", null, cv);
        db.close();
    }

    /**
     * read img data from database
     *
     * @param name
     * @return
     */
    public static byte[] readImage(String name){
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        //Cursor cur=db.query("Img", new String[]{"_id","avatar"}, null, null, null, null, null);
        Cursor cur=db.rawQuery("select * from Img where name=?", new String[]{name});
        byte[] imgData=null;
        if(cur.moveToFirst()){
            Log.d("tag","读取数据时 cursor size "+cur.getCount());
            //将Blob数据转化为字节数组
            imgData=cur.getBlob(cur.getColumnIndex("avatar"));
            cur.close();
            //db.close();
        }
        return imgData;
    }
//    //图片转为二进制数据
//    public static byte[] bitmapToBytes(Context context){
//        //将图片转化为位图
//        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
//        int size = bitmap.getWidth() * bitmap.getHeight() * 4;
//        //创建一个字节数组输出流,流的大小为size
//        ByteArrayOutputStream baos= new ByteArrayOutputStream(size);
//        try {
//            //设置位图的压缩格式，质量为100%，并放入字节数组输出流中
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//            //将字节数组输出流转化为字节数组byte[]
//            byte[] imagedata = baos.toByteArray();
//            return imagedata;
//        }catch (Exception e){
//        }finally {
//            try {
//                bitmap.recycle();
//                baos.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return new byte[0];
//    }




    /**
     * judge if the img has saved in database
     *
     * @param selectionArgs
     * @return
     */
    public  boolean isEmpty(String selectionArgs ){

        SQLiteDatabase db = dbhelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from Img where name=?",new String[]{selectionArgs});
        if(cursor.getCount()!=0){
            boolean hasmovetofirst = cursor.moveToFirst();

            Log.d("tag","查询是否已经存在 cursor size is "+cursor.getCount());
            Log.d("tag","has move to first msg "+hasmovetofirst);
            Log.d("tag","is the first msg "+cursor.isFirst());

            String name = cursor.getString(cursor.getColumnIndex("name"));
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            Log.d("tag","img has restored,id is  " + id);
            Log.d("tag","img has restored,name is  " + name);
            cursor.close();
            //db.close();
            return  false;
        }else {
            cursor.close();
            //db.close();
            Log.d("tag","nothing find return");
        }
        return true;
    }

    public void clearCache(){
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        db.execSQL("delete from Img where 1=1");
    }

}

