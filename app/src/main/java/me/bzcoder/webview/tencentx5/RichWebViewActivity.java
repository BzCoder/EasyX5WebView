package me.bzcoder.webview.tencentx5;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bzcoder.webview.sample.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import cc.shinichi.library.ImagePreview;
import cc.shinichi.library.view.listener.OnOriginProgressListener;
import me.bzcoder.easywebview.rich.RichJavascriptInterface;
import me.bzcoder.easywebview.rich.RichWebView;

public class RichWebViewActivity extends AppCompatActivity {
    private FrameLayout videoFullView;

    private TextView tvGunTitle;

    private Toolbar titleToolBar;

    private RichWebView webView;

    private ProgressBar pbProgress;

    private List<String> images;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rich_webview_x5);
        initView();
        initToolBar();
        initWebView();
        initData();
    }



    private void initToolBar() {
        setSupportActionBar(titleToolBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //去除默认Title显示
            actionBar.setDisplayShowTitleEnabled(false);
        }
        titleToolBar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.actionbar_more));
        tvGunTitle.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvGunTitle.setSelected(true);
            }
        }, 1900);
    }


    private void initWebView() {
        RichJavascriptInterface javaScriptLog = new RichJavascriptInterface(this);
        javaScriptLog.setClickImageCallBack((src, position) -> {
            showPreviewPhoto(position);
        });

        javaScriptLog.setImageListCallBack(imagesUrls -> {
            images = Arrays.asList(imagesUrls);
        });
        webView.addJavascriptInterface(javaScriptLog, "control");
    }


    private void initView() {
        videoFullView = findViewById(R.id.video_fullView);
        tvGunTitle = findViewById(R.id.tv_gun_title);
        titleToolBar = findViewById(R.id.title_tool_bar);
        webView = findViewById(R.id.webview);
        pbProgress = findViewById(R.id.pb_progress);
    }

    private void initData() {
        webView.setShow(getHtmlData("data.txt"));
        //获取所有图片
        webView.getImageList();
        //设置图片点击回调
        webView.setImageClickListener();
        //设置错误图片
        webView.setLoadImgError();
        //设置字体
        webView.setTagFontFamily("p");
        webView.setTagFontFamily("span");
        //设置字体大小
        webView.setFontSize(6);
    }


    /**
     * 获取文件
     *
     * @param file
     * @return
     */
    private String getHtmlData(String file) {
        StringBuffer sb = new StringBuffer();
        try {
            InputStream is = getAssets().open(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String temp = "";
            while ((temp = br.readLine()) != null) {
                sb.append(temp);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return sb.toString();
    }


    /**
     * 显示图片预览器
     *
     * @param position
     */
    private void showPreviewPhoto(int position) {
        ImagePreview.getInstance()
                .setContext(this)
                .setIndex(position)
                .setImageList(images)
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 返回键
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
