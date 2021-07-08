package me.bzcoder.webview.tencentx5;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bzcoder.webview.sample.R;

import cc.shinichi.library.ImagePreview;
import cc.shinichi.library.view.listener.OnOriginProgressListener;
import me.bzcoder.easyglide.EasyGlide;
import me.bzcoder.easywebview.base.BaseX5WebView;
import me.bzcoder.easywebview.common.X5WebChromeClient;
import me.bzcoder.easywebview.common.X5WebViewClient;
import me.bzcoder.easywebview.common.CommonJavascriptInterface;
import me.bzcoder.easywebview.utils.FullscreenHolder;
import me.bzcoder.easywebview.webinterface.IWebViewActivity;
import me.bzcoder.webview.MainActivity;
import me.bzcoder.webview.utils.BaseTools;
import me.bzcoder.webview.utils.StatusBarUtil;

/**
 * 使用 tencent x5 内核处理网页
 * 1、放入对应jar
 * 2、application 初始化
 * 3、gradle ndk配置
 * 4、jniLibs 配置
 * 5、添加权限 READ_PHONE_STATE
 * 6、getWindow().setFormat(PixelFormat.TRANSLUCENT);
 */
public class X5WebViewActivity extends AppCompatActivity implements IWebViewActivity {

    // 进度条
    private ProgressBar mProgressBar;
    private BaseX5WebView webView;
    // 全屏时视频加载view
    private FrameLayout videoFullView;
    // 加载视频相关
    private X5WebChromeClient mWebChromeClient;
    // 网页链接
    private String mUrl;
    private Toolbar mTitleToolBar;
    // 可滚动的title 使用简单 没有渐变效果，文字两旁有阴影
    private TextView tvGunTitle;
    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_x5);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getIntentData();
        initTitle();
        initWebView();
        webView.loadUrl(mUrl);
        getDataFromBrowser(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
        // 支付宝网页版在打开文章详情之后,无法点击按钮下一步
        webView.resumeTimers();
        // 设置为横屏
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onDestroy() {
        try {
            videoFullView.removeAllViews();
            if (webView != null) {
                webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
                webView.stopLoading();
                webView.setWebChromeClient(null);
                webView.setWebViewClient(null);
                webView.destroy();
                webView = null;
            }
        } catch (Exception e) {
            Log.e("X5WebViewActivity", e.getMessage());
        }
        super.onDestroy();
    }

    private void getIntentData() {
        mUrl = getIntent().getStringExtra("mUrl");
        mTitle = getIntent().getStringExtra("mTitle");
    }


    private void initTitle() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.colorPrimary), 0);
        mProgressBar = findViewById(R.id.pb_progress);
        webView = findViewById(R.id.webview_detail);
        videoFullView = findViewById(R.id.video_fullView);
        mTitleToolBar = findViewById(R.id.title_tool_bar);
        tvGunTitle = findViewById(R.id.tv_gun_title);
        initToolBar();
    }

    private void initToolBar() {
        setSupportActionBar(mTitleToolBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //去除默认Title显示
            actionBar.setDisplayShowTitleEnabled(false);
        }
        mTitleToolBar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.actionbar_more));
        tvGunTitle.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvGunTitle.setSelected(true);
            }
        }, 1900);
        setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_webview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 返回键
                handleFinish();
                break;
            case R.id.actionbar_share:// 分享到
                String shareText = webView.getTitle() + webView.getUrl();
                BaseTools.share(X5WebViewActivity.this, shareText);
                break;
            case R.id.actionbar_cope:// 复制链接
                if (!TextUtils.isEmpty(webView.getUrl())) {
                    BaseTools.copy(webView.getUrl());
                    Toast.makeText(this, "复制成功", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.actionbar_open:// 打开链接
                BaseTools.openLink(X5WebViewActivity.this, webView.getUrl());
                break;
            case R.id.actionbar_webview_refresh:// 刷新页面
                if (webView != null) {
                    webView.reload();
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    protected void initWebView() {
        mProgressBar.setVisibility(View.VISIBLE);
        mWebChromeClient = new X5WebChromeClient(this);
        webView.setWebChromeClient(mWebChromeClient);
        // 与js交互
        webView.addJavascriptInterface(new CommonJavascriptInterface(this), "injectedObject");
        webView.setWebViewClient(new X5WebViewClient(this));
        webView.setOnLongClickListener(v -> handleLongImage());
    }

    @Override
    public void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showWebView() {
        webView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideWebView() {
        webView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void fullViewAddView(View view) {
        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
        videoFullView = new FullscreenHolder(X5WebViewActivity.this);
        videoFullView.addView(view);
        decor.addView(videoFullView);
    }

    @Override
    public void showVideoFullView() {
        videoFullView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideVideoFullView() {
        videoFullView.setVisibility(View.GONE);
    }

    @Override
    public void startProgress(int newProgress) {
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setProgress(newProgress);
        if (newProgress == 100) {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    public void setTitle(String mTitle) {
        tvGunTitle.setText(mTitle);
    }


    @Override
    public FrameLayout getVideoFullView() {
        return videoFullView;
    }

    /**
     * 全屏时按返加键执行退出全屏方法
     */
    public void hideCustomView() {
        mWebChromeClient.onHideCustomView();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * 上传图片之后的回调
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == X5WebChromeClient.FILE_CHOOSER_RESULT_CODE) {
            mWebChromeClient.mUploadMessage(intent, resultCode);
        } else if (requestCode == X5WebChromeClient.FILE_CHOOSER_RESULT_CODE_FOR_ANDROID_5) {
            mWebChromeClient.mUploadMessageForAndroid5(intent, resultCode);
        }
    }


    /**
     * 使用singleTask启动模式的Activity在系统中只会存在一个实例。
     * 如果这个实例已经存在，intent就会通过onNewIntent传递到这个Activity。
     * 否则新的Activity实例被创建。
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getDataFromBrowser(intent);
    }

    /**
     * 作为三方浏览器打开传过来的值
     * Scheme: https
     * host: www.jianshu.com
     * path: /p/1cbaf784c29c
     * url = scheme + "://" + host + path;
     */
    private void getDataFromBrowser(Intent intent) {
        Uri data = intent.getData();
        if (data != null) {
            try {
                String scheme = data.getScheme();
                String host = data.getHost();
                String path = data.getPath();
                String text = "Scheme: " + scheme + "\n" + "host: " + host + "\n" + "path: " + path;
                Log.e("data", text);
                String url = scheme + "://" + host + path;
                webView.loadUrl(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 直接通过三方浏览器打开时，回退到首页
     */
    public void handleFinish() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
        }
        if (!MainActivity.isLaunch) {
            MainActivity.start(this);
        }
    }

    /**
     * 长按图片事件处理
     */
    private boolean handleLongImage() {
        final com.tencent.smtt.sdk.WebView.HitTestResult hitTestResult = webView.getHitTestResult();
        // 如果是图片类型或者是带有图片链接的类型
        if (hitTestResult.getType() == com.tencent.smtt.sdk.WebView.HitTestResult.IMAGE_TYPE ||
                hitTestResult.getType() == com.tencent.smtt.sdk.WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
            // 弹出保存图片的对话框
            new AlertDialog.Builder(X5WebViewActivity.this)
                    .setItems(new String[]{"查看大图", "保存图片到相册"}, (dialog, which) -> {
                        String picUrl = hitTestResult.getExtra();
                        //获取图片
                        Log.e("picUrl", picUrl);
                        switch (which) {
                            case 0:
                                showPreviewPhoto(picUrl);
                                break;
                            case 1:
                                EasyGlide.downloadImageToGallery(X5WebViewActivity.this,picUrl);
                                break;
                            default:
                                break;
                        }
                    })
                    .show();
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //全屏播放退出全屏
            if (mWebChromeClient.inCustomView()) {
                hideCustomView();
                return true;

                //返回网页上一页
            } else if (webView.canGoBack()) {
                webView.goBack();
                return true;

                //退出网页
            } else {
                handleFinish();
            }
        }
        return false;
    }

    /**
     * 打开网页:
     *
     * @param mContext 上下文
     * @param mUrl     要加载的网页url
     * @param mTitle   标题
     */
    public static void loadUrl(Context mContext, String mUrl, String mTitle) {
        Intent intent = new Intent(mContext, X5WebViewActivity.class);
        intent.putExtra("mUrl", mUrl);
        intent.putExtra("mTitle", mTitle == null ? "加载中..." : mTitle);
        mContext.startActivity(intent);
    }

    public static void loadUrl(Context mContext, String mUrl) {
        Intent intent = new Intent(mContext, X5WebViewActivity.class);
        intent.putExtra("mUrl", mUrl);
        intent.putExtra("mTitle", "详情");
        mContext.startActivity(intent);
    }


    @Override
    public Activity getActivity() {
        return this;
    }

    private void showPreviewPhoto(String url) {
        ImagePreview.getInstance()
                .setContext(this)
                .setImage(url)
                .setFolderName("BigImageView/Download")
                .setZoomTransitionDuration(300)
                .setEnableClickClose(true)
                .setEnableDragClose(true)
                .setShowCloseButton(false)
                .setShowDownButton(true)
                .setShowIndicator(true)
                // 设置失败时的占位图，默认为库中自带R.drawable.load_failed，设置为 0 时不显示
                .setErrorPlaceHolder(R.drawable.load_failed)
                // 设置查看原图时的百分比样式：库中带有一个样式：ImagePreview.PROGRESS_THEME_CIRCLE_TEXT，使用如下：
                .setProgressLayoutId(ImagePreview.PROGRESS_THEME_CIRCLE_TEXT, new OnOriginProgressListener() {
                    @Override
                    public void progress(View parentView, int progress) {
                        // 需要找到进度控件并设置百分比，回调中的parentView即传入的布局的根View，可通过parentView找到控件：
                        ProgressBar progressBar = parentView.findViewById(R.id.sh_progress_view);
                        TextView textView = parentView.findViewById(R.id.sh_progress_text);
                        progressBar.setProgress(progress);
                        String progressText = progress + "%";
                        textView.setText(progressText);
                    }

                    @Override
                    public void finish(View parentView) {

                    }
                })
                .start();
    }
}
