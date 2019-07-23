# EasyX5WebView
EasyX5WebView是基于腾讯X5浏览器v4.3.0的轻量级封装，主要为了易于使用，功能够用基本上也是够用的阶段。这里同样站在了前人的肩膀上。
本库很多内容是来自于[WebViewStudy](https://github.com/BzCoder/WebViewStudy)，并以此为基础进行结构优化后的改造。

示例工程引用以下两个库来完善图片的展示。
- [EasyGlide](https://github.com/BzCoder/EasyGlide)
- [BigImageViewPager](https://github.com/BzCoder/BigImageViewPager)

## 演示
![](https://github.com/BzCoder/EasyX5WebView/blob/master/art/GIF.gif)

## 模块
LibEasyWebview封装类中实现了以下两个功能：
- 通用展示页BaseX5WebView，封装了常用的WebView功能。
- 新闻详情页RichWebView，用来展示新闻资讯，封装了大量JS方法。

## 使用方法

### Application中初始化X5浏览器

```java

  private void initX5() {
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(),  cb);
    }
 ```
    
###  通用展示页WebViews
参照[X5WebViewActivity](https://github.com/BzCoder/EasyX5WebView/blob/master/app/src/main/java/me/bzcoder/webview/tencentx5/X5WebViewActivity.java),实现IWebViewActivity接口。

###  新闻详情页RichWebView
参照[RichWebViewActivity](https://github.com/BzCoder/EasyX5WebView/blob/master/app/src/main/java/me/bzcoder/webview/tencentx5/RichWebViewActivity.java)
其中核心为以下方法。

```
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
```
 
## 建议
由于WebView需求繁多，一套JS不能满足所有需求，所以推荐直接Clone到本地使用该库，JS方法可以在rich_editor中查看，根据实际需求对js进行编辑。Lib中还放入了字体文件font.tff，根据实际需求替换或者删除。
