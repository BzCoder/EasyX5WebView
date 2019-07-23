package me.bzcoder.easywebview.common;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import me.bzcoder.easywebview.utils.Tools;
import me.bzcoder.easywebview.webinterface.IWebViewActivity;

/**
 * WebViewClient
 *
 * @author : BaoZhou
 * @date : 2019/7/23 16:47
 */
public class X5WebViewClient extends com.tencent.smtt.sdk.WebViewClient {

    public static final String HTTP = "http:";
    public static final String HTTPS = "https:";
    public static final String APK = ".apk";

    private IWebViewActivity mIWebPageView;

    public X5WebViewClient(IWebViewActivity iWebViewActivity) {
        this.mIWebPageView = iWebViewActivity;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean shouldOverrideUrlLoading(com.tencent.smtt.sdk.WebView view, String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        if (url.startsWith(HTTP) || url.startsWith(HTTPS)) {
            // 可能有提示下载Apk文件
            if (url.contains(APK)) {
                handleOtherwise(url);
                return true;
            }
            return false;
        }

        handleOtherwise(url);
        return true;
    }


    @Override
    public void onPageFinished(com.tencent.smtt.sdk.WebView view, String url) {
        if (!Tools.isNetworkConnected(getWebPageView().getActivity())) {
            getWebPageView().hideProgressBar();
        }
        getWebPageView().addImageClickListener(view);
        // html加载完成之后，添加监听图片的点击js函数
        super.onPageFinished(view, url);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onReceivedError(com.tencent.smtt.sdk.WebView view, int errorCode, String description, String
            failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        if (errorCode == 404) {
            //用javascript隐藏系统定义的404页面信息
            String data = "Page NO FOUND！";
            view.loadUrl("javascript:document.body.innerHTML=\"" + data + "\"");
        }
    }

    // 视频全屏播放按返回页面被放大的问题
    @Override
    public void onScaleChanged(com.tencent.smtt.sdk.WebView view, float oldScale, float newScale) {
        super.onScaleChanged(view, oldScale, newScale);
        if (newScale - oldScale > 7) {
            view.setInitialScale((int) (oldScale / newScale * 100)); //异常放大，缩回去。
        }
    }

    /**
     * 网页里可能唤起其他的app
     */
    private void handleOtherwise(String url) {
        String appPackageName = "";
        // 支付宝支付
        if (url.contains("alipays")) {
            appPackageName = "com.eg.android.AlipayGphone";

            // 微信支付
        } else if (url.contains("weixin://wap/pay")) {
            appPackageName = "com.tencent.mm";

            // 京东产品详情
        } else if (url.contains("openapp.jdmobile")) {
            appPackageName = "com.jingdong.app.mall";
        } else {
            startActivity(url);
        }
        if (Tools.isApplicationInstall(getWebPageView().getActivity(), appPackageName)) {
            startActivity(url);
        }
    }

    private void startActivity(String url) {
        try {
            // 用于DeepLink测试
            if (url.startsWith("will://")) {
                Uri uri = Uri.parse(url);
                Log.e("---------scheme", uri.getScheme() + "；host: " + uri.getHost() + "；Id: " + uri.getPathSegments().get(0));
            }

            Intent intent1 = new Intent();
            intent1.setAction("android.intent.action.VIEW");
            Uri uri = Uri.parse(url);
            intent1.setData(uri);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getWebPageView().getActivity().startActivity(intent1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setWebPageView(IWebViewActivity mIWebPageView) {
        this.mIWebPageView = mIWebPageView;
    }

    private IWebViewActivity getWebPageView() {
        if (mIWebPageView == null) {
            throw new RuntimeException("you must set IWevPageView");
        } else {
            return mIWebPageView;
        }
    }


}
