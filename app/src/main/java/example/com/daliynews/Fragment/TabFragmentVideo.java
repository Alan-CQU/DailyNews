package example.com.daliynews.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import cn.jzvd.JZVideoPlayer;
import example.com.daliynews.Adapter.VideoPageAdapter;
import example.com.daliynews.NewsActivity;
import example.com.daliynews.R;


/**
 * Created by CJ on 2018/3/27.
 *
 * show video news
 */

public class TabFragmentVideo extends Fragment {
    public static Fragment newInstance() {
        TabFragmentVideo fragment = new TabFragmentVideo();
        return fragment;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_tab,container,false);
        RecyclerView recyclerView =(RecyclerView) rootView.findViewById(R.id.recycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        VideoPageAdapter mVideoPageAdapter = new VideoPageAdapter(getActivity());
        recyclerView.setAdapter(mVideoPageAdapter);
        return  rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    if (JZVideoPlayer.backPress()){
                        return true;
                    }
                    return false;
                }
                return false;
            }
        });
    }
    @Override
    public void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }

}
