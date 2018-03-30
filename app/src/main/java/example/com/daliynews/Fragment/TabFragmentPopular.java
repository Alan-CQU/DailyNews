package example.com.daliynews.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import example.com.daliynews.Adapter.PopularPageAdapter;
import example.com.daliynews.NewsContent;
import example.com.daliynews.OnRecyclerItemClickListener;
import example.com.daliynews.R;

/**
 * Created by CJ on 2018/3/27.
 */

public class TabFragmentPopular extends Fragment{
    public static Fragment newInstance() {
        TabFragmentPopular fragment = new TabFragmentPopular();
        return fragment;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_tab,container,false);
        RecyclerView recyclerView =(RecyclerView) rootView.findViewById(R.id.recycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        PopularPageAdapter mPopularPageAdapter = new PopularPageAdapter();
        mPopularPageAdapter.setOnRecyclerItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getActivity(),  "sssssssss", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), NewsContent.class);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(mPopularPageAdapter);
        return  rootView;
    }
}
