package com.example.admin.myapplicationcxwy_bmw;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

/**
 * Created by ${Frank} on 2018/8/17.
 */

@SuppressWarnings("DefaultFileTemplate")
public class CustomView extends ViewGroup {
    public CustomView(Context context) {
        super(context);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }


    @Override
    protected void onFinishInflate() {
//        /*道不同不相为谋*/
        super.onFinishInflate();
    }


    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);






    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();

    }


    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {


        /*请求父控件不要拦截事件*/
        getParent().getParent().getParent().getParent().requestDisallowInterceptTouchEvent(true);
        return super.onInterceptTouchEvent(ev);



    }

    @Override
    public void dispatchSystemUiVisibilityChanged(int visible) {















    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();



    }

    @Override
    public void onRtlPropertiesChanged(int layoutDirection) {
        super.onRtlPropertiesChanged(layoutDirection);


        /*方法很多 你一辈子都用不完*/
    }



    /************************************************
     *
     *
     *
     *
     *
     *
     *
     * ****************************************************
     *
     *
     **********************************
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     * ********************************************************************
     *
     *
     *
     *
     *
     *
     *
     *
     *
     * */
}
