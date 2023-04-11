package com.panaceasoft.pswallpaper.ui.mainwalkthrough;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.panaceasoft.pswallpaper.databinding.MainWalkthroughSlider1Binding;
import com.panaceasoft.pswallpaper.databinding.MainWalkthroughSlider2Binding;
import com.panaceasoft.pswallpaper.databinding.MainWalkthroughSlider3Binding;
import com.panaceasoft.pswallpaper.databinding.MainWalkthroughSlider4Binding;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.PagerAdapter;

public class MainWalkthroughAdapter extends PagerAdapter {

    private LayoutInflater layoutInflater;
    private int[] welcomeScreen;
    private Context context;
    private androidx.databinding.DataBindingComponent dataBindingComponent;
    public MainWalkthroughAdapter() {
    }

    public MainWalkthroughAdapter(int[] welcomeScreen, Context context, androidx.databinding.DataBindingComponent dataBindingComponent ) {

        this.welcomeScreen = welcomeScreen;
        this.context = context;
        this.dataBindingComponent = dataBindingComponent;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
//        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(position == 0) {
            MainWalkthroughSlider1Binding binding = DataBindingUtil.inflate(LayoutInflater.from(container.getContext()),
                    welcomeScreen[position], container, false,
                    dataBindingComponent);
            container.addView(binding.getRoot());

//        if(layoutInflater != null) {
//            View view = layoutInflater.inflate(welcomeScreen[position], container, false);
//            container.addView(view);
//
//            return view;
//        }else {
//            return container.getRootView();
//        }
            return binding.getRoot();
        }else if(position == 1) {
            MainWalkthroughSlider2Binding binding = DataBindingUtil.inflate(LayoutInflater.from(container.getContext()),
                    welcomeScreen[position], container, false,
                    dataBindingComponent);
            container.addView(binding.getRoot());
//        if(layoutInflater != null) {
//            View view = layoutInflater.inflate(welcomeScreen[position], container, false);
//            container.addView(view);
//
//            return view;
//        }else {
//            return container.getRootView();
//        }
            return binding.getRoot();
        }else if(position == 2) {
            MainWalkthroughSlider3Binding binding = DataBindingUtil.inflate(LayoutInflater.from(container.getContext()),
                    welcomeScreen[position], container, false,
                    dataBindingComponent);
            container.addView(binding.getRoot());
//        if(layoutInflater != null) {
//            View view = layoutInflater.inflate(welcomeScreen[position], container, false);
//            container.addView(view);
//
//            return view;
//        }else {
//            return container.getRootView();
//        }
            return binding.getRoot();
        }else {
            MainWalkthroughSlider4Binding binding = DataBindingUtil.inflate(LayoutInflater.from(container.getContext()),
                    welcomeScreen[position], container, false,
                    dataBindingComponent);
            container.addView(binding.getRoot());
//        if(layoutInflater != null) {
//            View view = layoutInflater.inflate(welcomeScreen[position], container, false);
//            container.addView(view);
//
//            return view;
//        }else {
//            return container.getRootView();
//        }
            return binding.getRoot();
        }

    }

    @Override
    public int getCount() {
        return welcomeScreen.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
        return view == obj;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}

