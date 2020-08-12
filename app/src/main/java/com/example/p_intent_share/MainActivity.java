package com.example.p_intent_share;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button btnShare;
    private boolean isQQ;


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

        findViewById(R.id.btnShareType).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareToType();
            }
        });

        findViewById(R.id.btnShareImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Uri uri = Uri.parse("https://s.yimg.com/ny/api/res/1.2/.EDssnwrmBOIF.hSQbREoQ--~A/YXBwaWQ9aGlnaGxhbmRlcjtzbT0xO3c9MTI4MDtoPTk2MA--/https://media.zenfs.com/zh-TW/ctwant_com_582/33dd8e3e4fdabe3438124e30a29b75a5");
                shareImg("這是內容",uri);
            }
        });

        findViewById(R.id.isQQ).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isQQ =   ShareUtils.getInstance(MainActivity.this).isQQClientAvailable();
                Log.v("hank","isQQ:" +isQQ);

                if(isQQ){
                    //有安裝ＱＱ
//                    final String qqUrl = "mqqwpa://im/chat?chat_type=wpa&uin=22486880&version=1";
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(qqUrl)));

                    Intent intent = getPackageManager().getLaunchIntentForPackage("com.tencent.mobileqq");
                    startActivity(intent);
                }else{
                    //沒安裝ＱＱ
                    Toast.makeText(MainActivity.this,"请安装QQ客户端",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /* 分享功能傳送訊息給其他app
     *@param subject      1.訊息標題
     *@param body         2.訊息內容
     *@param chooserTitle 3.選擇器彈窗標題
     */
    private void shareTo(String subject, String body, String chooserTitle) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(sharingIntent, chooserTitle));
    }

    //我的規範要求用戶能夠選擇電子郵件，Twitter，Facebook或SMS，並為每個電子郵件選擇自定義文本。 這是我實現的方式：
    public void shareToType() {
        Resources resources = getResources();

        Intent emailIntent = new Intent();
        emailIntent.setAction(Intent.ACTION_SEND);
        // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
        emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(resources.getString(R.string.share_email_native)));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.share_email_subject));
        emailIntent.setType("message/rfc822");

        PackageManager pm = getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");


        Intent openInChooser = Intent.createChooser(emailIntent, resources.getString(R.string.share_chooser_text));

        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
        for (int i = 0; i < resInfo.size(); i++) {
            // Extract the label, append it, and repackage it in a LabeledIntent
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if (packageName.contains("android.email")) {
                emailIntent.setPackage(packageName);
            } else if (packageName.contains("twitter") || packageName.contains("facebook") || packageName.contains("mms") || packageName.contains("android.gm")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                if (packageName.contains("twitter")) {
                    intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.share_twitter));
                } else if (packageName.contains("facebook")) {
                    // Warning: Facebook IGNORES our text. They say "These fields are intended for users to express themselves. Pre-filling these fields erodes the authenticity of the user voice."
                    // One workaround is to use the Facebook SDK to post, but that doesn't allow the user to choose how they want to share. We can also make a custom landing page, and the link
                    // will show the <meta content ="..."> text from that page with our link in Facebook.
                    intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.share_facebook));
                } else if (packageName.contains("mms")) {
                    intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.share_sms));
                } else if (packageName.contains("android.gm")) { // If Gmail shows up twice, try removing this else-if clause and the reference to "android.gm" above
                    intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(resources.getString(R.string.share_email_gmail)));
                    intent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.share_email_subject));
                    intent.setType("message/rfc822");
                }

                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }

        // convert intentList to array
        LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);

        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        startActivity(openInChooser);
    }


    private void shareImg(String content, Uri uri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        if (uri != null) {
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType("image/*");
        //當使用者選擇簡訊時使用sms_body取得文字
            shareIntent.putExtra("sms_body", content);
        } else {
            shareIntent.setType("text/plain");
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        //自定義選擇框的標題
        startActivity(Intent.createChooser(shareIntent, "邀請好友"));
        //系統預設標題
    }
}
