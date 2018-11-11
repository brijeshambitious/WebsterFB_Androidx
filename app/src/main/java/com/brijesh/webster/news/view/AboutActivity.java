package com.brijesh.webster.news.view;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.brijesh.webster.news.R;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;





public class AboutActivity extends AppCompatActivity  {


    private TextView textView;
    private Typeface montserrat_regular;
    private Typeface montserrat_semiBold;
    private Button startVideoAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);



        createToolbar();

        /*
        ** Action of the Floating Action Button ( FAB )
        **/
        floatingButton();













        /*
        ** Custom Toolbar ( App Bar )
        **/


        /*
        ** Action of the Floating Action Button ( FAB )
        **/






        /*
        ** Customising animations of the AppBar Layout
        **/


        AssetManager assetManager = this.getApplicationContext().getAssets();
        Typeface montserrat_regular = Typeface.createFromAsset(assetManager, "fonts/Montserrat-Regular.ttf");
        Typeface montserrat_semiBold = Typeface.createFromAsset(assetManager, "fonts/Montserrat-SemiBold.ttf");

        TextView aboutHeaderAppName = (TextView) findViewById(R.id.about_header_app_name);
        aboutHeaderAppName.setTypeface(montserrat_semiBold);

        TextView aboutHeaderAppDescription = (TextView) findViewById(R.id.about_header_app_description);
        aboutHeaderAppDescription.setTypeface(montserrat_regular);

        TextView cardInfo = (TextView) findViewById(R.id.tv_card_info);
        cardInfo.setTypeface(montserrat_regular);

        TextView madeWithLove = (TextView) findViewById(R.id.tv_made_with_love);
        madeWithLove.setTypeface(montserrat_regular);







       /* CardView cardViewLibrary1 = (CardView) findViewById(R.id.cardView1);
        cardViewLibrary1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/mikepenz/MaterialDrawer"));
                startActivity(browserIntent);
            }
        });

        CardView cardViewLibrary2 = (CardView) findViewById(R.id.cardView2);
        cardViewLibrary2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/square/retrofit"));
                startActivity(browserIntent);
            }
        });

        CardView cardViewLibrary3 = (CardView) findViewById(R.id.cardView3);
        cardViewLibrary3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/square/okhttp"));
                startActivity(browserIntent);
            }
        });

        CardView cardViewLibrary4 = (CardView) findViewById(R.id.cardView4);
        cardViewLibrary4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/bumptech/glide"));
                startActivity(browserIntent);
            }
        });*/
    }

    private void floatingButton() {
        FloatingActionButton fab = findViewById(R.id.fab_about);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });
    }

    private void createToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_layout_about);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final TextView toolbarTitle = findViewById(R.id.toolbar_title_about);

        /*
        ** Customising animations of the AppBar Layout
        **/
        AppBarLayout appBarLayout = findViewById(R.id.app_bar_about);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    toolbarTitle.setVisibility(View.VISIBLE);
                    toolbarTitle.setTypeface(montserrat_regular);
                    toolbarTitle.setText("About");
                    isShow = true;
                } else if (isShow) {
                    toolbarTitle.setVisibility(View.GONE);
                    isShow = false;
                }
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            /*
            * Override the Up/Home Button
            * */
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto: brijeshambitious@gmail.com"));
        startActivity(Intent.createChooser(emailIntent, "Send feedback"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }







}
