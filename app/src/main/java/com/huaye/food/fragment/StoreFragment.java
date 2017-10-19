package com.huaye.food.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huaye.food.Const;
import com.huaye.food.MenuActivity;
import com.huaye.food.R;
import com.huaye.food.WebViewActivity;

/**
 * A fragment with a Google +1 button.
 * Use the {@link StoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StoreFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // The request code must be 0 or greater.
    private static final int PLUS_ONE_REQUEST_CODE = 0;
    // The URL to +1.  Must be a valid URL.
    private final String PLUS_ONE_URL = "http://developer.android.com";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button btn1, btn2, btn3, btn4, btn5;
    private LinearLayout ln1, ln2, ln3, ln4;

    public StoreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StoreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StoreFragment newInstance(String param1, String param2) {
        StoreFragment fragment = new StoreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_store, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ln1 = (LinearLayout) view.findViewById(R.id.lin1);
        ln2 = (LinearLayout) view.findViewById(R.id.lin2);
        ln3 = (LinearLayout) view.findViewById(R.id.lin3);
        ln4 = (LinearLayout) view.findViewById(R.id.lin4);

        ln1.setOnClickListener(this);
        ln2.setOnClickListener(this);
        ln3.setOnClickListener(this);
        ln4.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
//        mPlusOneButton.initialize(PLUS_ONE_URL, PLUS_ONE_REQUEST_CODE);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        if (v instanceof LinearLayout) {
            LinearLayout viewGroup = (LinearLayout) v;
            TextView name = (TextView) viewGroup.getChildAt(1);
            intent.putExtra("name", name.getText().toString());
        }
        switch (v.getId()) {
            case R.id.lin1:
                Const.currentR = 0;
                intent.setClass(getContext(), MenuActivity.class);
                startActivity(intent);
                break;
            case R.id.lin2:
                Const.currentR = 1;
                intent.setClass(getContext(), MenuActivity.class);
                startActivity(intent);
                break;
            case R.id.lin3:
                Const.currentR = 2;
                intent.setClass(getContext(), MenuActivity.class);
                startActivity(intent);
                break;
            case R.id.lin4:
                Const.currentR = 3;
                intent.setClass(getContext(), MenuActivity.class);
                startActivity(intent);
                break;
            case R.id.btn5:
                intent.setClass(getContext(), WebViewActivity.class);
                startActivity(intent);
                break;
        }
    }
}
