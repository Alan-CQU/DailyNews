# DailyNews

![news](text_justify.gif)

My Changes:

- Move the splash screen do be within the main activity, enabling preload of the content while the splash is still running. Here is an example:

```java
 setContentView(R.layout.activity_main);

        //Change views visibility after
        new Handler().postDelayed(new Runnable() {
            public void run() {
                findViewById(R.id.splash_screen).setVisibility(View.GONE);
                findViewById(R.id.main_content).setVisibility(View.VISIBLE);
            }
        }, SPLASH_DELAY_MILLIS);
```

- Indexing issue

```
example.com.daliynews E/AndroidRuntime: FATAL EXCEPTION: main
    Process: example.com.daliynews, PID: 6586
    java.lang.IndexOutOfBoundsException: Invalid index 20, size is 20
        at java.util.ArrayList.throwIndexOutOfBoundsException(ArrayList.java:255)
        at java.util.ArrayList.get(ArrayList.java:308)
        at example.com.daliynews.Fragment.TabFragmentHome$3.onItemClick(TabFragmentHome.java:192)
        at example.com.daliynews.Adapter.HomePageAdapter$1.onClick(HomePageAdapter.java:159)
        at android.view.View.performClick(View.java:4781)
        at android.view.View$PerformClick.run(View.java:19873)
        at android.os.Handler.handleCallback(Handler.java:739)
        at android.os.Handler.dispatchMessage(Handler.java:95)
        at android.os.Looper.loop(Looper.java:135)
        at android.app.ActivityThread.main(ActivityThread.java:5289)
        at java.lang.reflect.Method.invoke(Native Method)
        at java.lang.reflect.Method.invoke(Method.java:372)
        at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:904)
        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:699)

```

The temporary fix for the above which I use for further testing the application

```java
 //Todo: Temporary fix, to update
                if (position  <= containerList.size() -1 ){
                    Intent intent = new Intent(getActivity(),NewsActivity.class);
                    intent.putExtra("URL",((ArrayList<String>)containerList.get(2)).get(position));
                    intent.putExtra("IMG_URL",((ArrayList<String>)containerList.get(4)).get(position));
                    startActivity(intent);
                }else{
                    Log.e("TAG", "Index issue: "+position);
                }


```

- Memory issue:

``` 
example.com.daliynews E/AndroidRuntime: FATAL EXCEPTION: main
    Process: example.com.daliynews, PID: 6827
    java.lang.OutOfMemoryError: Failed to allocate a 9437196 byte allocation with 1510318 free bytes and 1474KB until OOM
        at dalvik.system.VMRuntime.newNonMovableArray(Native Method)
        at android.graphics.BitmapFactory.nativeDecodeByteArray(Native Method)
        at android.graphics.BitmapFactory.decodeByteArray(BitmapFactory.java:522)
        at android.graphics.BitmapFactory.decodeByteArray(BitmapFactory.java:545)
        at example.com.daliynews.Adapter.HomePageAdapter.onBindViewHolder(HomePageAdapter.java:140)
        at android.support.v7.widget.RecyclerView$Adapter.onBindViewHolder(RecyclerView.java:6482)
        at android.support.v7.widget.RecyclerView$Adapter.bindViewHolder(RecyclerView.java:6515)
        at android.support.v7.widget.RecyclerView$Recycler.tryBindViewHolderByDeadline(RecyclerView.java:5458)
        at android.support.v7.widget.RecyclerView$Recycler.tryGetViewHolderForPositionByDeadline(RecyclerView.java:5724)
        at android.support.v7.widget.RecyclerView$Recycler.getViewForPosition(RecyclerView.java:5563)
        at android.support.v7.widget.RecyclerView$Recycler.getViewForPosition(RecyclerView.java:5559)
        at android.support.v7.widget.LinearLayoutManager$LayoutState.next(LinearLayoutManager.java:2229)
        at android.support.v7.widget.LinearLayoutManager.layoutChunk(LinearLayoutManager.java:1556)
        at android.support.v7.widget.LinearLayoutManager.fill(LinearLayoutManager.java:1516)
        at android.support.v7.widget.LinearLayoutManager.onLayoutChildren(LinearLayoutManager.java:608)
        at android.support.v7.widget.RecyclerView.dispatchLayoutStep2(RecyclerView.java:3693)
        at android.support.v7.widget.RecyclerView.dispatchLayout(RecyclerView.java:3410)
        at android.support.v7.widget.RecyclerView.onLayout(RecyclerView.java:3962)
        at android.view.View.layout(View.java:15678)
        at android.view.ViewGroup.layout(ViewGroup.java:5039)
        at android.support.v4.widget.SwipeRefreshLayout.onLayout(SwipeRefreshLayout.java:610)
        at android.view.View.layout(View.java:15678)
        at android.view.ViewGroup.layout(ViewGroup.java:5039)
        at android.widget.RelativeLayout.onLayout(RelativeLayout.java:1077)
        at android.view.View.layout(View.java:15678)
        at android.view.ViewGroup.layout(ViewGroup.java:5039)
        at android.support.v4.view.ViewPager.onLayout(ViewPager.java:1767)
        at android.view.View.layout(View.java:15678)
        at android.view.ViewGroup.layout(ViewGroup.java:5039)
        at android.widget.LinearLayout.setChildFrame(LinearLayout.java:1703)
        at android.widget.LinearLayout.layoutVertical(LinearLayout.java:1557)
        at android.widget.LinearLayout.onLayout(LinearLayout.java:1466)
        at android.view.View.layout(View.java:15678)
        at android.view.ViewGroup.layout(ViewGroup.java:5039)
        at android.support.v4.widget.DrawerLayout.onLayout(DrawerLayout.java:1172)
        at android.view.View.layout(View.java:15678)
        at android.view.ViewGroup.layout(ViewGroup.java:5039)
        at android.widget.LinearLayout.setChildFrame(LinearLayout.java:1703)
        at android.widget.LinearLayout.layoutVertical(LinearLayout.java:1557)
        at android.widget.LinearLayout.onLayout(LinearLayout.java:1466)
        at android.view.View.layout(View.java:15678)
        at android.view.ViewGroup.layout(ViewGroup.java:5039)
        at android.widget.FrameLayout.layoutChildren(FrameLayout.java:579)
        at android.widget.FrameLayout.onLayout(FrameLayout.java:514)
        at android.view.View.layout(View.java:15678)
        at android.view.ViewGroup.layout(ViewGroup.java:5039)
        at android.widget.LinearLayout.setChildFrame(LinearLayout.java:1703)
        at android.widget.LinearLayout.layoutVertical(LinearLayout.java:1557)
        at android.widget.LinearLayout.onLayout(LinearLayout.java:1466)
        at android.view.View.layout(View.java:15678)
        at android.view.ViewGroup.layout(ViewGroup.java:5039)
        at android.widget.FrameLayout.layoutChildren(FrameLayout.java:579)
        at android.widget.FrameLayout.onLayout(FrameLayout.java:514)
        at android.view.View.layout(View.java:15678)
        at android.view.ViewGroup.layout(ViewGroup.java:5039)
        at android.widget.LinearLayout.setChildFrame(LinearLayout.java:1703)
    	at android.widget.LinearLayout.l
```



```
example.com.daliynews I/art: Clamp target GC heap from 110MB to 96MB
    Alloc concurrent mark sweep GC freed 3(96B) AllocSpace objects, 0(0B) LOS objects, 1% free, 94MB/96MB, paused 1.787ms total 26.926ms
04-07 14:44:18.827 7039-7039/example.com.daliynews E/art: Throwing OutOfMemoryError "Failed to allocate a 2143308 byte allocation with 1644339 free bytes and 1605KB until OOM"

```



