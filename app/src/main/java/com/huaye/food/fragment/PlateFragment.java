package com.huaye.food.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huaye.food.FoodRvAdapter;
import com.huaye.food.R;
import com.huaye.food.bean.Food;

import java.util.ArrayList;

/**
 * Created by sunhuahui on 2017/10/21.
 */

public class PlateFragment extends BottomSheetDialogFragment {

    private RecyclerView plateRv;
    private FoodRvAdapter adapter;
    private ArrayList<Food> foods;

    public static PlateFragment newInstance(ArrayList<Food> list) {
        PlateFragment fragment = new PlateFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("foods", list);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_plate, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        foods = (ArrayList<Food>) getArguments().getSerializable("foods");
        adapter = new FoodRvAdapter(false);
        plateRv = (RecyclerView) view.findViewById(R.id.plate_rv);
        plateRv.setLayoutManager(new LinearLayoutManager(getContext()));
        plateRv.setAdapter(adapter);

        adapter.setNewData(foods);
    }

    public void addFood(Food food) {
        adapter.addData(food);
    }
}
