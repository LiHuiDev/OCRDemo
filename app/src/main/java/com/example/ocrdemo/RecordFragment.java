package com.example.ocrdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ocrdemo.adapter.RecondAdapter;
import com.example.ocrdemo.model.Recond;

import org.litepal.crud.DataSupport;

import java.util.List;

public class RecordFragment extends Fragment {

    private View rootView;
    private Context context;

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_record, container, false);
        context = inflater.getContext();

        initView();
        setRecyclerView();

        return rootView;
    }

    private void initView(){
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);
    }

    private void setRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        List<Recond> recondList = DataSupport.findAll(Recond.class);

        if(recondList.size() > 0){
            recyclerView.setAdapter(new RecondAdapter(recondList));
        }

        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                List<Recond> recondList = DataSupport.findAll(Recond.class);
                if(recondList.size()>0){
                    recyclerView.setAdapter(new RecondAdapter(recondList));
                }

                refreshLayout.setRefreshing(false);
            }
        });
    }

}
