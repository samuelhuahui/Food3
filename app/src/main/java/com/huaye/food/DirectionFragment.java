package com.huaye.food;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huaye.food.bean.Direction;

import java.util.ArrayList;

/**
 * Created by sunhuahui on 2017/10/11.
 */

public class DirectionFragment extends BottomSheetDialogFragment {
    private ArrayList<Direction.Route> mRoutes;
    private DirectionAdapter mAdapter;
    private RecyclerView mPlanRv;
    private TextView mCalorieTxt;
    private TextView mNavTxt;
    private Direction.LatLn mStart;
    private Direction.LatLn mEnd;
    private int mPosition;

    public static DirectionFragment newInstance(ArrayList<Direction.Route> routes, int position, Direction.LatLn start, Direction.LatLn end) {
        DirectionFragment fragment = new DirectionFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("start", start);
        bundle.putSerializable("end", end);
        bundle.putSerializable("routes", routes);
        bundle.putInt("position", position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_direction, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mRoutes = (ArrayList<Direction.Route>) getArguments().getSerializable("routes");
        mPosition = getArguments().getInt("position");
        mStart = (Direction.LatLn) getArguments().getSerializable("start");
        mEnd = (Direction.LatLn) getArguments().getSerializable("end");

        mNavTxt = (TextView) view.findViewById(R.id.nav);
        mCalorieTxt = (TextView) view.findViewById(R.id.calorie);
        mPlanRv = (RecyclerView) view.findViewById(R.id.plan);
        DisplayMetrics metric = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        mAdapter = new DirectionAdapter(mRoutes, width, mPosition);
        mPlanRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mPlanRv.setAdapter(mAdapter);
        float kcal = 1.036f * 50 * Integer.parseInt(mRoutes.get(mPosition).legs.get(0).distance.value) / 1000;
        mCalorieTxt.setText(kcal + " kcal");

        mNavTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?" + "saddr=" + mStart.lat + "," + mStart.lng + "&daddr=" + mEnd.lat + "," + mEnd.lng));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }
}
