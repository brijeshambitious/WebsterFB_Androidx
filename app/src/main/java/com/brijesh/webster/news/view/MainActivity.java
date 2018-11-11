package com.brijesh.webster.news.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brijesh.webster.news.Application;
import com.brijesh.webster.news.R;
import com.brijesh.webster.news.adapter.DataAdapter;
import com.brijesh.webster.news.model.ArticleStructure;
import com.brijesh.webster.news.model.Constants;
import com.brijesh.webster.news.model.NewsResponse;
import com.brijesh.webster.news.network.ApiClient;
import com.brijesh.webster.news.network.ApiInterface;
import com.brijesh.webster.news.network.interceptors.OfflineResponseCacheInterceptor;
import com.brijesh.webster.news.network.interceptors.ResponseCacheInterceptor;
import com.brijesh.webster.news.util.UtilityMethods;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAdListener;



import com.google.firebase.messaging.FirebaseMessaging;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private String[] SOURCE_ARRAY = {"google-news-in", "bbc-news", "google-news", "the-hindu", "the-times-of-india",
            "buzzfeed", "mashable", "mtv-news", "bbc-sport", "espn", "espn-cric-info", "talksport", "medical-news-today",
            "national-geographic", "crypto-coins-news", "engadget", "the-next-web", "the-verge", "techcrunch", "techradar",
            "hacker-news", "ign", "polygon","the-huffington-post","the-wall-street-journal","the-new-york-times",
            "next-big-future","new-scientist","ars-technica","wired","recode","new-york-magazine","cnn","bloomberg",
            "cnbc","business-insider","the-guardian-uk","the-washington-post","cbs-news","entertainment-weekly",};

    private String SOURCE;

    private ArrayList<ArticleStructure> articleStructure = new ArrayList<>();
    private DataAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Drawer result;
    private AccountHeader accountHeader;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private Parcelable listState;
    private Typeface montserrat_regular;
    private TextView mTitle;
    private AdView adView;
    private com.facebook.ads.InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("all");


        adView = new AdView(this, "829630740550452_829658390547687", AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        // Request an ad
        adView.loadAd();


        adView.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback

            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad loaded callback
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        });


        // Request an ad
        adView.loadAd();



// Prepare the Interstitial Ad
        interstitialAd = new com.facebook.ads.InterstitialAd(this, "829630740550452_829632350550291");
// Insert the Ad Unit ID
        // interstitial.setAdUnitId(getString(R.string.admob_interstitial_id));

        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                //displayInterstitial();
                // Interstitial displayed callback
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                interstitialAd.loadAd();


                // Interstitial dismissed callback
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback

            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Show the ad when it's done loading.

                displayInterstitial();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        });

       AdSettings.addTestDevice("97f3646eed23d49406f1fef2a8c59941");

        interstitialAd.loadAd();

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown







        AssetManager assetManager = this.getApplicationContext().getAssets();
        montserrat_regular = Typeface.createFromAsset(assetManager, "fonts/Montserrat-Regular.ttf");

        createToolbar();
        createRecyclerView();

        /*
        ** show loader and fetch messages.
        **/
        SOURCE = SOURCE_ARRAY[0];
        mTitle.setText(R.string.toolbar_default_text);
        onLoadingSwipeRefreshLayout();

        createDrawer(savedInstanceState, toolbar, montserrat_regular);

    }

    private void createToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_main_activity);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mTitle = (TextView) findViewById(R.id.toolbar_title);
        mTitle.setTypeface(montserrat_regular);
    }

    private void createRecyclerView() {
        recyclerView = findViewById(R.id.card_recycler_view);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }



    private void createDrawer(Bundle savedInstanceState, final Toolbar toolbar, Typeface montserrat_regular) {
        PrimaryDrawerItem item0 = new PrimaryDrawerItem().withIdentifier(0).withName("GENERAL / BUSINESS (GLOBAL)")
                .withTypeface(montserrat_regular).withSelectable(false);
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("Google News (India)")
                .withIcon(R.drawable.ic_googlenews).withTypeface(montserrat_regular);
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName("BBC News")
                .withIcon(R.drawable.ic_bbcnews).withTypeface(montserrat_regular);

        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(3).withName("Google News")
                .withIcon(R.drawable.ic_googlenews).withTypeface(montserrat_regular);



        PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(4).withName("The Hindu (India)")
                .withIcon(R.drawable.ic_thehindu).withTypeface(montserrat_regular);


        PrimaryDrawerItem item5 = new PrimaryDrawerItem().withIdentifier(5).withName("The Times of India")
                .withIcon(R.drawable.ic_timesofindia).withTypeface(montserrat_regular);

        SectionDrawerItem item48 = new SectionDrawerItem().withIdentifier(48).withName("INDIAN NEWS")
                .withTypeface(montserrat_regular);



        SectionDrawerItem item6 = new SectionDrawerItem().withIdentifier(6).withName("ENTERTAINMENT")
                .withTypeface(montserrat_regular);
        PrimaryDrawerItem item7 = new PrimaryDrawerItem().withIdentifier(7).withName("Buzzfeed")
                .withIcon(R.drawable.ic_buzzfeednews).withTypeface(montserrat_regular);
        PrimaryDrawerItem item8 = new PrimaryDrawerItem().withIdentifier(8).withName("Mashable")
                .withIcon(R.drawable.ic_mashablenews).withTypeface(montserrat_regular);
        PrimaryDrawerItem item9 = new PrimaryDrawerItem().withIdentifier(9).withName("MTV News")
                .withIcon(R.drawable.ic_mtvnews).withTypeface(montserrat_regular);

        SectionDrawerItem item10 = new SectionDrawerItem().withIdentifier(10).withName("SPORTS")
                .withTypeface(montserrat_regular);
        PrimaryDrawerItem item11 = new PrimaryDrawerItem().withIdentifier(11).withName("BBC Sports")
                .withIcon(R.drawable.ic_bbcsports).withTypeface(montserrat_regular);

        PrimaryDrawerItem item12 = new PrimaryDrawerItem().withIdentifier(12).withName("ESPN")
                .withIcon(R.drawable.ic_espn).withTypeface(montserrat_regular);

        PrimaryDrawerItem item13 = new PrimaryDrawerItem().withIdentifier(13).withName("ESPN Cric Info")
                .withIcon(R.drawable.ic_espncricinfo).withTypeface(montserrat_regular);

        PrimaryDrawerItem item14 = new PrimaryDrawerItem().withIdentifier(14).withName("TalkSport")
                .withIcon(R.drawable.ic_talksport).withTypeface(montserrat_regular);
        SectionDrawerItem item15 = new SectionDrawerItem().withIdentifier(15).withName("SCIENCE")
                .withTypeface(montserrat_regular);

        PrimaryDrawerItem item16 = new PrimaryDrawerItem().withIdentifier(16).withName("Medical News Today")
                .withIcon(R.drawable.ic_medicalnewstoday).withTypeface(montserrat_regular);
        PrimaryDrawerItem item17 = new PrimaryDrawerItem().withIdentifier(17).withName("National Geographic")
                .withIcon(R.drawable.ic_nationalgeographic).withTypeface(montserrat_regular);
        SectionDrawerItem item18 = new SectionDrawerItem().withIdentifier(18).withName("TECHNOLOGY")
                .withTypeface(montserrat_regular);
        PrimaryDrawerItem item19 = new PrimaryDrawerItem().withIdentifier(19).withName("Crypto Coins News")
                .withIcon(R.drawable.ic_ccnnews).withTypeface(montserrat_regular);
        PrimaryDrawerItem item20 = new PrimaryDrawerItem().withIdentifier(20).withName("Engadget")
                .withIcon(R.drawable.ic_engadget).withTypeface(montserrat_regular);

        PrimaryDrawerItem item21 = new PrimaryDrawerItem().withIdentifier(21).withName("The Next Web")
                .withIcon(R.drawable.ic_thenextweb).withTypeface(montserrat_regular);
        PrimaryDrawerItem item22 = new PrimaryDrawerItem().withIdentifier(22).withName("The Verge")
                .withIcon(R.drawable.ic_theverge).withTypeface(montserrat_regular);
        PrimaryDrawerItem item23 = new PrimaryDrawerItem().withIdentifier(23).withName("TechCrunch")
                .withIcon(R.drawable.ic_techcrunch).withTypeface(montserrat_regular);
        PrimaryDrawerItem item24 = new PrimaryDrawerItem().withIdentifier(24).withName("TechRadar")
                .withIcon(R.drawable.ic_techradar).withTypeface(montserrat_regular);

        PrimaryDrawerItem item25 = new PrimaryDrawerItem().withIdentifier(25).withName("Hacker News")
                .withIcon(R.drawable.ic_hackernews).withTypeface(montserrat_regular);

        SectionDrawerItem item26 = new SectionDrawerItem().withIdentifier(26).withName("GAMING")
                .withTypeface(montserrat_regular);
        PrimaryDrawerItem item27 = new PrimaryDrawerItem().withIdentifier(27).withName("IGN")
                .withIcon(R.drawable.ic_ignnews).withTypeface(montserrat_regular);
        PrimaryDrawerItem item28 = new PrimaryDrawerItem().withIdentifier(28).withName("Polygon")
                .withIcon(R.drawable.ic_polygonnews).withTypeface(montserrat_regular);
        SectionDrawerItem item29 = new SectionDrawerItem().withIdentifier(29).withName("MORE INFO")
                .withTypeface(montserrat_regular);

        SecondaryDrawerItem item39 = new SecondaryDrawerItem().withIdentifier(60).withName("Logos Powered by Clearbit API")
                .withIcon(R.drawable.ic_power).withTypeface(montserrat_regular);

        SecondaryDrawerItem item30 = new SecondaryDrawerItem().withIdentifier(32).withName("About the app")
                .withIcon(R.drawable.ic_info).withTypeface(montserrat_regular);


        SecondaryDrawerItem item31 = new SecondaryDrawerItem().withIdentifier(33).withName("Contact us")
                .withIcon(R.drawable.ic_mail).withTypeface(montserrat_regular);

        PrimaryDrawerItem item32 = new PrimaryDrawerItem().withIdentifier(34).withName("The Huffington Post")
                .withIcon(R.drawable.ic_post).withTypeface(montserrat_regular);


        PrimaryDrawerItem item33 = new PrimaryDrawerItem().withIdentifier(35).withName("The Wall Street Journal")
                .withIcon(R.drawable.ic_wsj).withTypeface(montserrat_regular);


        PrimaryDrawerItem item34 = new PrimaryDrawerItem().withIdentifier(36).withName("The New York Times")
                .withIcon(R.drawable.ic_newyork).withTypeface(montserrat_regular);


        PrimaryDrawerItem item35 = new PrimaryDrawerItem().withIdentifier(37).withName("Next Big Future")
                .withIcon(R.drawable.ic_nextbig).withTypeface(montserrat_regular);


        PrimaryDrawerItem item36 = new PrimaryDrawerItem().withIdentifier(38).withName("New Scientist")
                .withIcon(R.drawable.ic_newscientist).withTypeface(montserrat_regular);


        PrimaryDrawerItem item37 = new PrimaryDrawerItem().withIdentifier(39).withName("Ars Technica")
                .withIcon(R.drawable.ic_ars).withTypeface(montserrat_regular);


        PrimaryDrawerItem item38 = new PrimaryDrawerItem().withIdentifier(40).withName("Wired")
                .withIcon(R.drawable.ic_wired).withTypeface(montserrat_regular);

        PrimaryDrawerItem item42 = new PrimaryDrawerItem().withIdentifier(42).withName("Recode")
                .withIcon(R.drawable.ic_recode).withTypeface(montserrat_regular);


        PrimaryDrawerItem item43 = new PrimaryDrawerItem().withIdentifier(43).withName("New York Magazine")
                .withIcon(R.drawable.ic_yorkmag).withTypeface(montserrat_regular);

        PrimaryDrawerItem item44 = new PrimaryDrawerItem().withIdentifier(44).withName("CNN")
                .withIcon(R.drawable.ic_cnn).withTypeface(montserrat_regular);

        PrimaryDrawerItem item45 = new PrimaryDrawerItem().withIdentifier(45).withName("Bloomberg")
                .withIcon(R.drawable.ic_bloomberg).withTypeface(montserrat_regular);

        PrimaryDrawerItem item46 = new PrimaryDrawerItem().withIdentifier(46).withName("CNBC")
                .withIcon(R.drawable.ic_cnbc).withTypeface(montserrat_regular);

        PrimaryDrawerItem item47 = new PrimaryDrawerItem().withIdentifier(47).withName("Business Insider")
                .withIcon(R.drawable.ic_bi).withTypeface(montserrat_regular);

        PrimaryDrawerItem item49 = new PrimaryDrawerItem().withIdentifier(49).withName("The Guardian")
                .withIcon(R.drawable.ic_theguardian).withTypeface(montserrat_regular);

        PrimaryDrawerItem item50 = new PrimaryDrawerItem().withIdentifier(50).withName("The Washington Post")
                .withIcon(R.drawable.ic_washingtonpost).withTypeface(montserrat_regular);

        PrimaryDrawerItem item51 = new PrimaryDrawerItem().withIdentifier(51).withName("CBS News")
                .withIcon(R.drawable.cbs).withTypeface(montserrat_regular);

        PrimaryDrawerItem item52 = new PrimaryDrawerItem().withIdentifier(52).withName("Entertainment Weekly")
                .withIcon(R.drawable.ew).withTypeface(montserrat_regular);









        accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.ic_back)
                .withSavedInstance(savedInstanceState)
                .build();

        result = new DrawerBuilder()
                .withAccountHeader(accountHeader)
                .withActivity(this)
                .withToolbar(toolbar)
                .withSelectedItem(1)
                .addDrawerItems(item0, item2, item3,item44,item45,item46, item47,item32,item51,item34,
                        item33,item49,item50 ,item48, item1,item4, item5, item6,
                        item7, item8, item43, item9, item52,item10, item11,
                        item12, item13, item14, item15, item16, item17,
                        item35,item36, item18, item19, item20, item21, item22, item23, item24, item25,item42,
                        item37,item38,item26, item27 ,item28 , item29, item30,item31,item39)


                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        int selected = (int) (long) drawerItem.getIdentifier();
                        switch (selected) {
                            case 1:
                                SOURCE = SOURCE_ARRAY[0];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;
                            case 2:
                                SOURCE = SOURCE_ARRAY[1];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;
                            case 3:
                                SOURCE = SOURCE_ARRAY[2];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;
                            case 4:
                                SOURCE = SOURCE_ARRAY[3];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;



                            case 5:
                                SOURCE = SOURCE_ARRAY[4];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;

                            case 7:
                                SOURCE = SOURCE_ARRAY[5];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;
                            case 8:
                                SOURCE = SOURCE_ARRAY[6];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;
                            case 9:
                                SOURCE = SOURCE_ARRAY[7];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;
                            case 11:
                                SOURCE = SOURCE_ARRAY[8];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;
                            case 12:
                                SOURCE = SOURCE_ARRAY[9];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;
                            case 13:
                                SOURCE = SOURCE_ARRAY[10];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;
                            case 14:
                                SOURCE = SOURCE_ARRAY[11];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;

                            case 16:
                                SOURCE = SOURCE_ARRAY[12];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;
                            case 17:
                                SOURCE = SOURCE_ARRAY[13];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;
                            case 19:
                                SOURCE = SOURCE_ARRAY[14];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;
                            case 20:
                                SOURCE = SOURCE_ARRAY[15];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;
                            case 21:
                                SOURCE = SOURCE_ARRAY[16];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;
                            case 22:
                                SOURCE = SOURCE_ARRAY[17];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;
                            case 23:
                                SOURCE = SOURCE_ARRAY[18];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;
                            case 24:
                                SOURCE = SOURCE_ARRAY[19];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;
                            case 25:
                                SOURCE = SOURCE_ARRAY[20];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;

                            case 27:
                                SOURCE = SOURCE_ARRAY[21];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;





                            case 28:
                                SOURCE = SOURCE_ARRAY[22];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;



                            case 30:
                                SOURCE = SOURCE_ARRAY[23];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;















                            case 32:
                                openAboutActivity();
                                break;


                            case 33:
                                sendEmail();
                                break;
                            default:
                                break;


                            case 34:
                                SOURCE = SOURCE_ARRAY[23];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;


                            case 35:
                                SOURCE = SOURCE_ARRAY[24];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;


                            case 36:
                                SOURCE = SOURCE_ARRAY[25];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;

                            case 37:
                                SOURCE = SOURCE_ARRAY[26];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;


                            case 38:
                                SOURCE = SOURCE_ARRAY[27];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;



                            case 39:
                                SOURCE = SOURCE_ARRAY[28];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;


                            case 40:
                                SOURCE = SOURCE_ARRAY[29];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;

                            case 42:
                                SOURCE = SOURCE_ARRAY[30];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;


                            case 43:
                                SOURCE = SOURCE_ARRAY[31];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;

                            case 44:
                                SOURCE = SOURCE_ARRAY[32];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;


                            case 45:
                                SOURCE = SOURCE_ARRAY[33];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;

                            case 46:
                                SOURCE = SOURCE_ARRAY[34];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;


                            case 47:
                                SOURCE = SOURCE_ARRAY[35];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;


                            case 49:
                                SOURCE = SOURCE_ARRAY[36];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;


                            case 50:
                                SOURCE = SOURCE_ARRAY[37];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;


                            case 51:
                                SOURCE = SOURCE_ARRAY[38];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;



                            case 52:
                                SOURCE = SOURCE_ARRAY[39];
                                onLoadingSwipeRefreshLayout();
                                mTitle.setText(((Nameable) drawerItem).getName().getText(MainActivity.this));
                                break;








                            case 60:
                                Intent browserAPI = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("https://clearbit.com/"));
                                startActivity(browserAPI);
                                break;



                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();
    }


    private void loadJSON() {
        swipeRefreshLayout.setRefreshing(true);

        /*
        ** Used to show Log files of the HTTP GET REQUESTS and what is fetched
        ** and the status codes and the body of the requests etc.
        **/
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(Level.BODY);

        /*
        ** OkHttp is added as a default in Retrofit so it is added here for adding the
        ** different interceptors which handles the offline caching etc.
        **/
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        // add your other interceptors …
        httpClient.addNetworkInterceptor(new ResponseCacheInterceptor());
        httpClient.addInterceptor(new OfflineResponseCacheInterceptor());
        httpClient.cache(new Cache(new File(Application.getMyTimesApplicationInstance()
                .getCacheDir(), "ResponsesCache"), 10 * 1024 * 1024));
        httpClient.readTimeout(60, TimeUnit.SECONDS);
        httpClient.connectTimeout(60, TimeUnit.SECONDS);

        // add logging as last interceptor
        httpClient.addInterceptor(logging);

        /*
        ** Calls the Retrofit client (ApiClient) and passes the OkHTTP client
        ** (httpCLient declared above) and creates the call with the help of ApiInterface.
        **/
        ApiInterface request = ApiClient.getClient(httpClient).create(ApiInterface.class);


        Call<NewsResponse> call = request.getHeadlines(SOURCE, Constants.API_KEY);
        call.enqueue(new Callback<NewsResponse>() {
            /*
            ** The response is build with the Call and the Response while using the Article Response
             * POJO class to construct the responses .
            **/
            @Override
            public void onResponse(@NonNull Call<NewsResponse> call, @NonNull Response<NewsResponse> response) {

                if (response.isSuccessful() && response.body().getArticles() != null) {

                    if (!articleStructure.isEmpty()) {
                        articleStructure.clear();
                    }

                    articleStructure = response.body().getArticles();

                    adapter = new DataAdapter(MainActivity.this, articleStructure);
                    recyclerView.setAdapter(adapter);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }


            @Override
            public void onFailure(@NonNull Call<NewsResponse> call, @NonNull Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onRefresh() {
        loadJSON();
    }

    /*
    ** TODO: APP INDEXING(App is not indexable by Google Search; consider adding at least one Activity with an ACTION-VIEW) .
    ** TODO: ADDING ATTRIBUTE android:fullBackupContent
    **/
    private void onLoadingSwipeRefreshLayout() {
        if (!UtilityMethods.isNetworkAvailable()) {
            Toast.makeText(MainActivity.this, "Could not load latest News. Please turn on the Internet.", Toast.LENGTH_SHORT).show();
        }
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        loadJSON();
                    }
                }
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_menu:
                openAboutActivity();
                break;

            case R.id.action_privacy:
                Intent browserAPI = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://sites.google.com/site/appwsternews/privacy-policy"));
                startActivity(browserAPI);
                break;

            case R.id.action_search:
                openSearchActivity();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openAboutActivity() {
        Intent aboutIntent = new Intent(this, AboutActivity.class);
        startActivity(aboutIntent);
        this.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }


    private void openSearchActivity() {
        Intent searchIntent = new Intent(this, SearchActivity.class);
        startActivity(searchIntent);
        this.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);

    }

    private void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto: brijeshambitious@gmail.com"));
        startActivity(Intent.createChooser(emailIntent, "Send feedback"));
    }

    public void onBackPressed() {
        if (result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);
            builder.setTitle(R.string.app_name);
            builder.setIcon(R.mipmap.ic_launcher_round);
            builder.setMessage("Do you want to Exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {

        //add the values which need to be saved from the drawer to the bundle
        bundle = result.saveInstanceState(bundle);
        //add the values which need to be saved from the accountHeader to the bundle
        bundle = accountHeader.saveInstanceState(bundle);

        super.onSaveInstanceState(bundle);
        listState = recyclerView.getLayoutManager().onSaveInstanceState();
        bundle.putParcelable(Constants.RECYCLER_STATE_KEY, listState);
        bundle.putString(Constants.SOURCE, SOURCE);
        bundle.putString(Constants.TITLE_STATE_KEY, mTitle.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {

            SOURCE = savedInstanceState.getString("SOURCE");
            createToolbar();
            mTitle.setText(savedInstanceState.getString(Constants.TITLE_STATE_KEY));
            listState = savedInstanceState.getParcelable(Constants.RECYCLER_STATE_KEY);
            createDrawer(savedInstanceState, toolbar, montserrat_regular);
        }
    }



    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        

        if(listState!=null){
            recyclerView.getLayoutManager().onRestoreInstanceState(listState);
        }
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }



    private void displayInterstitial() {
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

// If Ads are loaded, show Interstitial else show nothing.

//                    if (interstitialAd.isAdLoaded()) {
//                        interstitialAd.show();
//                    }
//                }
                    if(interstitialAd == null || !interstitialAd.isAdLoaded()) {
                        return;
                    }
                    // Check if ad is already expired or invalidated, and do not show ad if that is the case. You will not get paid to show an invalidated ad.
                    if(interstitialAd.isAdInvalidated()) {
                        return;
                    }
                    // Show the ad
                    interstitialAd.show();
                }

            }, 1000 * 60 * 5);

        } catch (Exception e) {
            e.printStackTrace();
            //return null;
        }

    }

}
