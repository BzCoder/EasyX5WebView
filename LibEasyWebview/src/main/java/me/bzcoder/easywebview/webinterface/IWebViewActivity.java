package me.bzcoder.easywebview.webinterface;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;

import com.tencent.smtt.sdk.WebView;

/**
 * @author : BaoZhou
 * @date : 2019/7/23 15:54
 */

public interface IWebViewActivity {

    /**
     * 隐藏进度条
     */
    void hideProgressBar();

    /**
     * 显示webview
     */
    void showWebView();

    /**
     * 隐藏webview
     */
    void hideWebView();

    /**
     * 进度条变化时调用
     *
     * @param newProgress 进度0-100
     */
    void startProgress(int newProgress);

    /**
     * 监听网页加载完毕
     */
    default void onPageFinished(WebView view, String url) {
    }

    ;

    /**
     * 当前页面Activity
     *
     * @return
     */
    Activity getActivity();

    /**
     * 全屏播放器容器
     *
     * @return
     */
    FrameLayout getVideoFullView();

    /**
     * 播放网络视频全屏调用
     * @param view
     */
    void fullViewAddView(View view);


    /**
     * 增加图片文字点击回调
     * @param webView
     */
    default void addImageClickListener(WebView webView) {
        // 这段js函数的功能就是，遍历所有的img节点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
        webView.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\");" +
                "for(var i=0;i<objs.length;i++)" +
                "{" +
                "objs[i].onclick=function(){window.injectedObject.imageClick(this.getAttribute(\"src\"));}" +
                "}" +
                "})()");

        // 遍历所有的<li>节点,将节点里的属性传递过去(属性自定义,用于页面跳转)
        webView.loadUrl("javascript:(function(){" +
                "var objs =document.getElementsByTagName(\"li\");" +
                "for(var i=0;i<objs.length;i++)" +
                "{" +
                "objs[i].onclick=function(){" +
                "window.injectedObject.textClick(this.getAttribute(\"type\"),this.getAttribute(\"item_pk\"));}" +
                "}" +
                "})()");
    }

    void showVideoFullView();

    void hideVideoFullView();


}
