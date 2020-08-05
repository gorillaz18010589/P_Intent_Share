package com.example.p_intent_share;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button btnShare;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareTo("這是測試",
                        "http://blog.tonycube.com/2013/01/android-app.html",
                        "google"
                        );
            }
        });
    }


    /* 分享功能傳送訊息給其他app
    *@param subject      1.訊息標題
    *@param body         2.訊息內容
    *@param chooserTitle 3.選擇器彈窗標題
    * */
    private void shareTo(String subject, String body, String chooserTitle) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(sharingIntent, chooserTitle));
    }

    public void shareTo(View view) {
    }
}
