package com.lignede5;

import android.view.Gravity;
import android.widget.FrameLayout;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.Plugin;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

// TODO: les ID de test Google utilisés ici (constantes ci-dessous) doivent être
// remplacés par les véritables ID AdMob une fois le compte créé, avant toute
// publication réelle. Ne jamais publier avec des ID de test.
@CapacitorPlugin(name = "AdMob")
public class AdMobPlugin extends Plugin {

    private static final String TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111";
    private AdView adView;
    private boolean initialized = false;

    @PluginMethod
    public void initialize(PluginCall call) {
        if (!initialized) {
            MobileAds.initialize(getActivity(), status -> {});
            initialized = true;
        }
        call.resolve();
    }

    @PluginMethod
    public void showBanner(PluginCall call) {
        String adUnitId = call.getString("adUnitId", TEST_BANNER_AD_UNIT_ID);
        getActivity()
            .runOnUiThread(() -> {
                if (adView != null) {
                    adView.setVisibility(android.view.View.VISIBLE);
                    call.resolve();
                    return;
                }
                adView = new AdView(getActivity());
                adView.setAdUnitId(adUnitId);
                adView.setAdSize(AdSize.BANNER);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                );
                params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                getActivity().addContentView(adView, params);

                adView.loadAd(new AdRequest.Builder().build());
                call.resolve();
            });
    }

    @PluginMethod
    public void hideBanner(PluginCall call) {
        getActivity()
            .runOnUiThread(() -> {
                if (adView != null) {
                    adView.setVisibility(android.view.View.GONE);
                }
                call.resolve();
            });
    }
}
