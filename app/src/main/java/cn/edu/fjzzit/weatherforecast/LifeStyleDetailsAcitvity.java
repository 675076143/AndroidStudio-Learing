package cn.edu.fjzzit.weatherforecast;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class LifeStyleDetailsAcitvity extends AppCompatActivity {

    public static final String LIFE_STYLE_TYPE = "life_style_type";
    public static final String LIFE_STYLE_IMAGE = "life_style_image";
    public static final String LIFE_STYLE_TXT = "life_style_txt";
    public static final String LIFE_STYLE_BRF = "life_style_brf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_life_style_details_acitvity);
        Intent intent = getIntent();
        String lifeStyleType = intent.getStringExtra(LIFE_STYLE_TYPE);
        int lifeStyleImageId = intent.getIntExtra(LIFE_STYLE_IMAGE, 0);
        Toolbar toolbar = findViewById(R.id.toolbar_life_style);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collasping_toolbar);
        ImageView lifeStyleImageView = findViewById(R.id.image_view_lifestyle);
        TextView lifeStyleText = findViewById(R.id.text_view_lifestyle_details);
        TextView lifeStyleBrf = findViewById(R.id.text_view_lifestyle_details_brf);
        FloatingActionButton floatingActionButton = findViewById(R.id.fab_lifeStyle);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbarLayout.setTitle(lifeStyleType);
        Glide.with(this).load(lifeStyleImageId).into(lifeStyleImageView);
        lifeStyleText.setText(intent.getStringExtra(LIFE_STYLE_TXT));
        lifeStyleBrf.setText(intent.getStringExtra(LIFE_STYLE_BRF));
        floatingActionButton.setBackgroundResource(R.drawable.cond_icon_heweather_100);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
