package me.bzcoder.easywebview.rich;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;


/**
 * 用于JavaScript交互
 * @author : BaoZhou
 * @date : 2019/3/29 17:39
 */
public class RichJavascriptInterface {
    private final String TAG = "JavaScriptLog";

    private Context context;

    private JavaScriptCallBack callBack = null;

    private ClickImageCallBack clickImageCallBack = null;

    private CheckStyleCallBack checkStyleCallBack = null;

    private ClickEditLinkCallBack clickEditLinkCallBack = null;

    private  GetImageListCallBack ImageListCallBack = null;

    public RichJavascriptInterface(Context context, JavaScriptCallBack callBack){
        this.callBack = callBack;
        this.context = context;
    }

    public RichJavascriptInterface(Context context, ClickImageCallBack callBack){
        this.clickImageCallBack = callBack;
        this.context = context;
    }

    /**
     * 样式识别回调
     * @param callBack
     */
    public void setCheckStyleCallBack(CheckStyleCallBack callBack){
        this.checkStyleCallBack = callBack;
    }

    /**
     * 点击图片回调
     * @param callBack
     */
    public void setClickImageCallBack(ClickImageCallBack callBack){
        this.clickImageCallBack = callBack;
    }

    /**
     * 编辑页面点击超链接回调
     * @param callBack
     */
    public void setClickEditLinkCallBack(ClickEditLinkCallBack callBack){
        this.clickEditLinkCallBack = callBack;
    }

    /**
     * 获取图片数组回调
     * @param imageListCallBack
     */
    public void setImageListCallBack(GetImageListCallBack imageListCallBack) {
        ImageListCallBack = imageListCallBack;
    }


    public void setCallBack(JavaScriptCallBack callBack) {
        this.callBack = callBack;
    }

    public RichJavascriptInterface(){
    }



    /**
     * 点击图片
     */
    public interface JavaScriptCallBack{
        public void clickWebView();
    }

    /**
     * 样式识别回调
     */
    public interface CheckStyleCallBack{
         void checkStyle(String json);
    }

    /**
     * 点击图片
     */
    public interface ClickImageCallBack{
         void clickImage(String src, int position);
    }


    public interface ClickEditLinkCallBack{
         void clickEditLink(String url, String title);
    }

    /**
     * 获取图片数组
     */
    public interface GetImageListCallBack{
        void getImageList(String[] imagesUrls);
    }

    public RichJavascriptInterface(Context context){
        this.context = context;
    }

    @JavascriptInterface
    public void toast(String message) {
        Toast.makeText(context,"js:"+message, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void log(String result) {
        Log.v(TAG,"js:"+result);
    }

    /**
     * 点击富文本
     */
    @JavascriptInterface
    public void clickWebView() {
        if (callBack!=null){
            callBack.clickWebView();
        }
    }

    /**
     * 点击图片
     */
    @JavascriptInterface
    public void clickImage(String src,String position){
        Log.v(TAG,"webview点击图片:"+src+"position:"+position);
        if (clickImageCallBack!=null){
            clickImageCallBack.clickImage(src,Integer.parseInt(position));
        }
    }

    /**
     * 传递样式识别数据回来
     * @param json
     */
    @JavascriptInterface
    public void checkStyle(String json){
       Log.v(TAG,"checkStyle:"+json);
        if (checkStyleCallBack!=null){
            checkStyleCallBack.checkStyle(json);
        }
    }

    /**
     * 编辑时候点击超链接
     * @param link
     * @param title
     */
    @JavascriptInterface
    public void clickEditLink(String link, String title){
       Log.v(TAG,"clickEditLink:"+link+"  "+title);
        if (clickEditLinkCallBack!=null){
            clickEditLinkCallBack.clickEditLink(link,title);
        }
    }

    @JavascriptInterface
    public void getImageList(String[]  urls){
       Log.v(TAG,"getImageList:"+urls.length);
        if (ImageListCallBack!=null){
            ImageListCallBack.getImageList(urls);
        }
    }

}
