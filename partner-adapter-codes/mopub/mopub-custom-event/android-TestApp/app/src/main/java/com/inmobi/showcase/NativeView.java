package com.inmobi.showcase;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mopub.nativeads.InMobiNativeAdRenderer;
import com.mopub.nativeads.MoPubAdAdapter;
import com.mopub.nativeads.MoPubNative.MoPubNativeNetworkListener;
import com.mopub.nativeads.MoPubNativeAdPositioning;
import com.mopub.nativeads.NativeAd;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.RequestParameters;
import com.mopub.nativeads.ViewBinder;

public class NativeView extends Activity implements MoPubNativeNetworkListener {

    private static final String MY_AD_UNIT_ID = "9af9709550ba4d74a4421943b48a27f5";
    static final String TITLE = "SimpleNativeView";
    private ListView myListView;
    private MoPubAdAdapter mAdAdapter;
    private MoPubNativeAdPositioning.MoPubClientPositioning adPositioning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        myListView = (ListView) findViewById(R.id.actionlist);
        final ArrayAdapter<String> myAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1);
        for (int i = 0; i < 50; ++i) {
            myAdapter.add("Item " + i);
        }

        ViewBinder viewBinder = new ViewBinder.Builder(R.layout.native_ad_layout)
                .titleId(R.id.native_title)
                .iconImageId(R.id.native_icon_image)
                .textId(R.id.native_text)
                .mainImageId(R.id.native_main_image)
                .callToActionId(R.id.native_cta)
                //always add primary ad view layout(RelativeLayout) in Native Ad layout and add it to ViewBinder with
                //key "primary_ad_view_layout" as InMobiNative ad uses the same key to fetch this layout and attach
                //Native Ad View which can be static image/video ads
                .addExtra("primary_ad_view_layout",R.id.primary_ad_view_layout)
                .build();

        // Set up the positioning behavior your ads should have.
        adPositioning = new MoPubNativeAdPositioning.MoPubClientPositioning();
        adPositioning.addFixedPosition(2)
                .addFixedPosition(4)
                .addFixedPosition(8)
                .enableRepeatingPositions(3);

        //Instantiate InMobiNativeAdRenderer for showing native ads
        InMobiNativeAdRenderer inMobiNativeAdRenderer= new InMobiNativeAdRenderer(viewBinder);

        // Set up the MoPubAdAdapter
        mAdAdapter = new MoPubAdAdapter(this, myAdapter, adPositioning);

        //register inMobiNativeAdRenderer for InMobi Native Ads
        mAdAdapter.registerAdRenderer(inMobiNativeAdRenderer);
        myListView.setAdapter(mAdAdapter);
        mAdAdapter.loadAds(MY_AD_UNIT_ID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        myListView.destroyDrawingCache();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        RequestParameters myRequestParameters = new RequestParameters.Builder()
                .build();

        // Request ads when the user returns to this activity.
        mAdAdapter.loadAds(MY_AD_UNIT_ID, myRequestParameters);
        super.onResume();
    }

    @Override
    public void onNativeFail(NativeErrorCode arg0) {
        Log.v("NativeView", "Native failed to load:" + arg0.toString());
    }

    @Override
    public void onNativeLoad(NativeAd nativeAd) {
        Log.v("NativeView", "Native ad loaded");
        Log.i(TITLE, "Strand load successful");
        View adWrapper = nativeAd.createAdView(this,myListView);
        nativeAd.renderAdView(adWrapper);
        myListView.addView(adWrapper);
    }
}
