package com.konsung.ui.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.konsung.network.Stateful;
import com.konsung.presenter.BasePresenter;



/**
 * Activity的基类
 * @param <P> 泛型
 */
public abstract class BaseActivity<P extends BasePresenter> extends Activity implements Stateful {

    protected P presenter;


    protected View contentView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //让屏幕保持常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // 当滑动屏幕时，弹出状态栏和导航栏后，让其自动隐藏
        Window window = getWindow();
        window.getDecorView().setOnSystemUiVisibilityChangeListener(new View
                .OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                hideNavigationBar();
            }
        });
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        presenter = initPresenter();
    }


    @Override
    public void setState(int state) {

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    /**
     * 隐藏导航栏
     */
    private void hideNavigationBar() {
        int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            uiFlags |= 0x00001000;
        } else {
            uiFlags |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }

        getWindow().getDecorView().setSystemUiVisibility(uiFlags);
    }

    /**
     * 实例逻辑处理
     * @return 逻辑处理
     */
    public abstract P initPresenter();
}
