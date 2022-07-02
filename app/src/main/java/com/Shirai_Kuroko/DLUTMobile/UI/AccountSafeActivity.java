package com.Shirai_Kuroko.DLUTMobile.UI;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.Shirai_Kuroko.DLUTMobile.Helpers.ConfigHelper;
import com.Shirai_Kuroko.DLUTMobile.R;
import com.Shirai_Kuroko.DLUTMobile.Widgets.LoadingView;

public class AccountSafeActivity extends AppCompatActivity {

    private WebView webView;
    private LoadingView loadingView;
    @SuppressLint({"SetJavaScriptEnabled", "NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_safe);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle("账号安全");
        }
        loadingView= new LoadingView(this,R.style.CustomDialog);
        loadingView.show();
        webView = findViewById(R.id.SecurityWebView);
        webView.loadUrl("https://portal.dlut.edu.cn/tp_core/h5?act=sys/uacm/profileResetPwd");
        webView.addJavascriptInterface(this,"android");//添加js监听 这样html就能调用客户端
        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(webViewClient);
        WebSettings webSettings=webView.getSettings();
        if (ConfigHelper.GetThemeType(this)) { //判断如果系统是深色主题
            webSettings.setForceDark(WebSettings.FORCE_DARK_ON);//强制开启webview深色主题模式
        } else {
            webSettings.setForceDark(WebSettings.FORCE_DARK_OFF);
        }
        webSettings.setJavaScriptEnabled(true);//允许使用js
        /*
          LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
          LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
          LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
          LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
         */
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存，只从网络获取数据.
        //支持屏幕缩放
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
    }

    //WebViewClient主要帮助WebView处理各种通知、请求事件
    private final WebViewClient webViewClient=new WebViewClient(){
        @Override
        public void onPageFinished(WebView view, String url) {//页面加载完成
            Log.i("加载完成", url);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String Un = prefs.getString("Username","");
            String Pd = prefs.getString("Password","");
            if(url.contains("https://sso.dlut.edu.cn/cas/login?service="))
            {
                if(Un.length()*Pd.length()!=0)
                {
                    Toast.makeText(getBaseContext(),"正在执行认证，请稍候喵",Toast.LENGTH_SHORT).show();
                    view.evaluateJavascript("un.value='"+Un+"';pd.value='"+Pd+"';login()", value -> {
                    });
                }
                else
                {
                    AlertDialog.Builder localBuilder = new AlertDialog.Builder(webView.getContext());
                    localBuilder.setMessage("个人信息未配置完全，集成认证失败，请手动认证").setPositiveButton("确定",null);
                    localBuilder.setCancelable(false);
                    localBuilder.create().show();
                }
            }
            else
            {
                loadingView.dismiss();
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {//页面开始加载
            loadingView.show();
            Log.i("开始加载", url);
        }
    };

    //WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
    private final WebChromeClient webChromeClient=new WebChromeClient(){
        //不支持js的alert弹窗，需要自己监听然后通过dialog弹窗
        @Override
        public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(webView.getContext());
            localBuilder.setMessage(message).setPositiveButton("确定",null);
            localBuilder.setCancelable(false);
            localBuilder.create().show();
            //注意:
            //必须要这一句代码:result.confirm()表示:
            //处理结果为确定状态同时唤醒WebCore线程
            //否则不能继续点击按钮
            result.confirm();
            return true;
        }

        //获取网页标题
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }

        //加载进度回调
        @Override
        public void onProgressChanged(WebView view, int newProgress) {

        }
    };

    /**
     * JS调用android的方法
     */
    @JavascriptInterface //仍然必不可少
    public void  getClient(String str){

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        webView.destroy();
        webView=null;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (webView.canGoBack()){//点击返回按钮的时候判断有没有上一页
            webView.goBack(); // goBack()表示返回webView的上一页面
        }
        super.onBackPressed();
    }
}