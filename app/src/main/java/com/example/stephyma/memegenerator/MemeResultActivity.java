package com.example.stephyma.memegenerator;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MemeResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme_result);

        String topText = "";
        String bottomText = "";
        String imagePath = "";

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            topText = extras.getString("topText");
            bottomText = extras.getString("bottomText");
            imagePath = extras.getString("imagePath");
        }

        TextView tvTopText = (TextView) findViewById(R.id.tv_top_text);
        TextView tvBottomText = (TextView) findViewById(R.id.tv_bottom_text);
        ImageView ivMemeResult = (ImageView) findViewById(R.id.iv_meme_result);
        if (topText != null && !topText.isEmpty()) {
            tvTopText.setText(topText.toUpperCase());
        }
        if (bottomText != null && !bottomText.isEmpty()) {
            tvBottomText.setText(bottomText.toUpperCase());
        }

        ivMemeResult.setImageURI(Uri.parse(imagePath));

        Toast.makeText(getApplicationContext(), topText, Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), bottomText, Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), imagePath, Toast.LENGTH_LONG).show();
    }
}
