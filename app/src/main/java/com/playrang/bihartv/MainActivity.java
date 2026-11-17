package com.playrang.bihartv;
import android.app.Activity; import android.content.Intent; import android.content.pm.ActivityInfo;
import android.graphics.Color; import android.net.Uri; import android.os.Bundle;
import android.view.View; import android.view.ViewGroup; import android.view.WindowManager;
import android.webkit.*; import android.widget.FrameLayout;
public class MainActivity extends Activity {
  static final String HOME="https://mannukumarbank222-cmd.github.io/bihar-tv/app/";
  WebView w; FrameLayout root; View custom; WebChromeClient.CustomViewCallback customCb; WebChromeClient chrome;
  @Override protected void onCreate(Bundle b){ super.onCreate(b);
    getWindow().setStatusBarColor(Color.parseColor("#B71C1C"));
    root=new FrameLayout(this); w=new WebView(this); root.addView(w); setContentView(root);
    WebSettings s=w.getSettings(); s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true);
    s.setMediaPlaybackRequiresUserGesture(false); s.setCacheMode(WebSettings.LOAD_DEFAULT);
    s.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
    w.setWebViewClient(new WebViewClient(){
      public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r){
        Uri u=r.getUrl(); String h=u.getHost()==null?"":u.getHost();
        if(h.endsWith("github.io")) return false; String sc=u.getScheme()==null?"":u.getScheme(); if(!sc.startsWith("http")||h.contains("youtube")||h.contains("youtu.be")||h.contains("google")) return true;
        try{ startActivity(new Intent(Intent.ACTION_VIEW,u)); }catch(Exception e){} return true; }
      public void onReceivedError(WebView v, WebResourceRequest r, WebResourceError e){
        if(r.isForMainFrame()) v.loadUrl("file:///android_asset/offline.html"); }
    });
    chrome=new WebChromeClient(){
      public void onShowCustomView(View view, CustomViewCallback cb){
        if(custom!=null){ cb.onCustomViewHidden(); return; }
        custom=view; customCb=cb; root.addView(view,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        w.setVisibility(View.GONE); getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE); }
      public void onHideCustomView(){
        if(custom==null) return; root.removeView(custom); custom=null; w.setVisibility(View.VISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED); if(customCb!=null) customCb.onCustomViewHidden(); }
    }; w.setWebChromeClient(chrome);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    w.loadUrl(HOME);
  }
  @Override public void onBackPressed(){
    if(custom!=null){ chrome.onHideCustomView(); return; }
    w.evaluateJavascript("window.btvBack?btvBack():false", v -> { if("true".equals(v)) return;
      if(w.canGoBack()) w.goBack(); else finish(); });
  }
  @Override protected void onPause(){ super.onPause(); w.onPause(); }
  @Override protected void onResume(){ super.onResume(); w.onResume(); }
}
