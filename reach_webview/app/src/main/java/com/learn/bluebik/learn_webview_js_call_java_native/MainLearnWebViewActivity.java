package com.learn.bluebik.learn_webview_js_call_java_native;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class MainLearnWebViewActivity extends AppCompatActivity {

    final int CAMERA_RESULT_CODE = 0;
    private String stringImage;
    WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_learn_web_view);

        initInstance();
    }

    private void initInstance() {
        myWebView = (WebView) findViewById(R.id.webview);
        myWebView.loadUrl("http://192.168.1.49:8080");

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        myWebView.addJavascriptInterface(MainLearnWebViewActivity.this, "Android");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CAMERA_RESULT_CODE && resultCode == RESULT_OK){
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            this.stringImage = bitmapToBase64(photo);
            myWebView.loadUrl("javascript:getImageData()");
        }
    }

    @JavascriptInterface
    public void openCamera(){
        if(checkCameraHardware(MainLearnWebViewActivity.this)){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(Intent.createChooser(intent
                    , "Take a picture with"), CAMERA_RESULT_CODE);
        }else{
            Toast.makeText(MainLearnWebViewActivity.this, "camera not active", Toast.LENGTH_SHORT).show();
        }
    }

    @JavascriptInterface
    public String getImageString(){
        return this.stringImage;
    }

    @JavascriptInterface
    public void showToast(String text){
        Toast.makeText(MainLearnWebViewActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        } else {
            return false;
        }
    }
}
