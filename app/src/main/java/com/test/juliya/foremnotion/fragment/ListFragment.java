package com.test.juliya.foremnotion.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.test.juliya.foremnotion.R;
import com.test.juliya.foremnotion.controller.PagerAdapter;
import com.test.juliya.foremnotion.controller.PlaceListRvAdapter;
import com.test.juliya.foremnotion.model.CardModel;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by juliya on 22.04.2017.
 */

public class ListFragment extends Fragment {
    @Bind(R.id.rv_list)
    RecyclerView recyclerView;
    private PlaceListRvAdapter adapter;

    public static ListFragment getInstance() {
        return new ListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);
        ButterKnife.bind(this, view);
        initRecView();
        return view;
    }

    private void initRecView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new PlaceListRvAdapter(getActivity());
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void updateList(CardModel cardModel) {
        if(adapter == null) return;

        adapter.updateDataList(cardModel);
    }
}
