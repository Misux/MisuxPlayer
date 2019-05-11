package com.example.francesco.masterplayer.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.example.francesco.masterplayer.R;

public class InfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);


        //collegamento linkedIn Autor
        ImageView linkedIn = (ImageView)findViewById(R.id.iconLinke);
        linkedIn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://www.linkedin.com/in/francesco-misuraca-640231100/"));
                startActivity(intent);
            }
        });

        ImageView iconMail = (ImageView) findViewById(R.id.iconMail);

        ImageView iconGithub = (ImageView) findViewById(R.id.iconGithub);
        iconGithub.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://github.com/Misux?tab=repositories"));
                startActivity(intent);
            }
        });

        ImageView imageIcon = (ImageView) findViewById(R.id.imageIcon);
        imageIcon.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://material.io/icons/"));
                startActivity(intent);
            }
        });
    }

    public void emailClick(View v) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setData(Uri.parse("email"));
        String s[] = {"fra.misu11@gmail.com"};
        i.putExtra(Intent.EXTRA_EMAIL, s);
        i.putExtra(Intent.EXTRA_SUBJECT,"subject");
        i.putExtra(Intent.EXTRA_TEXT, "Hii this is the Email Body");
        i.setType("message/rfc822");
        Intent chooser = Intent.createChooser(i, "Launch Email");
        startActivity(chooser);
    }
}
