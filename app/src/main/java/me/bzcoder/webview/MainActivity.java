package me.bzcoder.webview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.bzcoder.webview.sample.R;

import me.bzcoder.webview.tencentx5.RichWebViewActivity;
import me.bzcoder.webview.tencentx5.X5WebViewActivity;
import me.bzcoder.webview.utils.StatusBarUtil;

/**
 * Link to: https://github.com/youlookwhat/WebViewStudy
 * contact me: https://www.jianshu.com/u/e43c6e979831
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // 是否开启了主页，没有开启则会返回主页
    public static boolean isLaunch = false;
    private AppCompatEditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.colorPrimary), 0);
        initView();
        isLaunch = true;
    }

    private void initView() {
        findViewById(R.id.bt_deeplink).setOnClickListener(this);
        findViewById(R.id.bt_openUrl).setOnClickListener(this);
        findViewById(R.id.bt_x5test).setOnClickListener(this);
        findViewById(R.id.bt_baidu).setOnClickListener(this);
        findViewById(R.id.bt_movie).setOnClickListener(this);
        findViewById(R.id.bt_upload_photo).setOnClickListener(this);
        findViewById(R.id.bt_call).setOnClickListener(this);
        findViewById(R.id.bt_java_js).setOnClickListener(this);
        findViewById(R.id.bt_news_detail).setOnClickListener(this);

        etSearch = findViewById(R.id.et_search);
        /** 处理键盘搜索键 */
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                openUrl();
            }
            return false;
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_openUrl:
                openUrl();
                break;
            case R.id.bt_x5test:// 百度一下
                String x5testUrl = "http://debugtbs.qq.com";
                loadUrl(x5testUrl, "X5测试页");
                break;
            case R.id.bt_baidu:// 百度一下
                String baiDuUrl = "http://www.baidu.com";
                loadUrl(baiDuUrl, "百度一下");
                break;
            case R.id.bt_movie:// 网络视频
                String movieUrl = "https://sv.baidu.com/videoui/page/videoland?context=%7B%22nid%22%3A%22sv_5861863042579737844%22%7D&pd=feedtab_h5";
                loadUrl(movieUrl, "网络视频");
                break;
            case R.id.bt_upload_photo:// 上传图片
                String uploadUrl = "file:///android_asset/upload_photo.html";
                loadUrl(uploadUrl, "上传图片测试");
                break;
            case R.id.bt_call:// 打电话、发短信、发邮件、JS
                String callUrl = "file:///android_asset/callsms.html";
                loadUrl(callUrl, "电话短信邮件测试");
                break;
            case R.id.bt_java_js://  js与android原生代码互调
                String javaJs = "file:///android_asset/java_js.html";
                loadUrl(javaJs, "js与android原生代码互调");
                break;
            case R.id.bt_deeplink:// DeepLink通过网页跳入App
                String deepLinkUrl = "file:///android_asset/deeplink.html";
                loadUrl(deepLinkUrl, "DeepLink测试");
                break;
            case R.id.bt_news_detail:// 新闻详情网页跳入App
                String filePath = "data.txt";
                startActivity(new Intent(this, RichWebViewActivity.class));
                break;
            default:
                break;
        }
    }

    /**
     * 打开网页
     */
    private void openUrl() {
        String url = etSearch.getText().toString().trim();
        if (TextUtils.isEmpty(url)) {
            // 空url
            url = "https://github.com/BzCoder";

        } else if (!url.startsWith("http") && url.contains("http")) {
            // 有http且不在头部
            url = url.substring(url.indexOf("http"), url.length());

        } else if (url.startsWith("www")) {
            // 以"www"开头
            url = "http://" + url;

        } else if (!url.startsWith("http") && (url.contains(".me") || url.contains(".com") || url.contains(".cn"))) {
            // 不以"http"开头且有后缀
            url = "http://www." + url;

        } else if (!url.startsWith("http") && !url.contains("www")) {
            // 输入纯文字 或 汉字的情况
            url = "http://m5.baidu.com/s?from=124n&word=" + url;
        }
        loadUrl(url, "详情");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadUrl(String mUrl, String mTitle) {
        X5WebViewActivity.loadUrl(this, mUrl, mTitle);
    }


    public static void start(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isLaunch = false;
    }
}
