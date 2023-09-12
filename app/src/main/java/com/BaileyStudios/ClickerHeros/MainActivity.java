package com.BaileyStudios.ClickerHeros;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.JavascriptInterface;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private InterstitialAd mInterstitialAd;
    private RewardedAd rewardedAd;
    private WebView mywebView;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mRunnable;
    private ConsentInformation consentInformation;
    private final Integer FILE_CHOOSER_REQUEST_CODE = 1;
    private ValueCallback<Uri[]> fPathCallback;

    @Override
    protected void onPause() {
        super.onPause();
        mywebView.post(() -> {
            mywebView.evaluateJavascript("PauseSounds();", null);
            Log.d(TAG, "Paused all audio");
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mywebView.post(() -> mywebView.evaluateJavascript("ResumeSounds();", null));
        if (fPathCallback == null)
            return;

        fPathCallback.onReceiveValue(new Uri[]{});
        fPathCallback = null;
    }


    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit the app?");
        builder.setPositiveButton("Yes", (dialog, which) -> finishAffinity());
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showRewardedAd(int id) {
        if (rewardedAd != null) {
            rewardedAd.show(
                    MainActivity.this,
                    new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            //Grants generator frenzy
                            loadRewardedAd();
                            mywebView.post(() -> {
                                if (id == 1) {
                                    mywebView.evaluateJavascript("GeneratorFrenzy();", null);
                                } else if (id == 2) {
                                    mywebView.evaluateJavascript("doubleDaMoneyFunc();", null);
                                }
                            });
                        }
                    });
        } else {
            Log.d(TAG, "The rewarded ad wasnt ready yet.");
        }
    }

    private void showInterstitialAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(MainActivity.this);
        } else {
            Log.d(TAG, "The interstitial ad wasn't ready yet.");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mywebView = (WebView) findViewById(R.id.webview);
        mywebView.setWebViewClient(new mywebClient());


        WebSettings webSettings = mywebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mywebView.clearCache(true); // Clear the cache so update doesn't stay the same
        mywebView.requestFocus(View.FOCUS_DOWN);
        mywebView.addJavascriptInterface(new AppInterface(this), "AndroidApp");
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        if (savedInstanceState == null) {
            mywebView.loadUrl("file:///android_asset/Menu.html");
        }
        mywebView.setInitialScale(250);
        mywebView.setWebChromeClient(new WebChromeClient() {
            // For Android 4.1+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("audio/*");
                startActivityForResult(Intent.createChooser(intent, "Select Audio"), FILE_CHOOSER_REQUEST_CODE);

            }

            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                fPathCallback = filePathCallback;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("audio/*");
                startActivityForResult(Intent.createChooser(intent, "Select Audio"), FILE_CHOOSER_REQUEST_CODE);

                return true;
            }
        });


        ConsentRequestParameters params = new ConsentRequestParameters
                .Builder()
                .setTagForUnderAgeOfConsent(false)
                .build();

        consentInformation = UserMessagingPlatform.getConsentInformation(this);
        consentInformation.requestConsentInfoUpdate(
                this,
                params,
                (ConsentInformation.OnConsentInfoUpdateSuccessListener) () -> {
                    // TODO: Load and show the consent form.
                },
                (ConsentInformation.OnConsentInfoUpdateFailureListener) requestConsentError -> {
                    // Consent gathering failed.
                    Log.w(TAG, String.format("%s: %s",
                            requestConsentError.getErrorCode(),
                            requestConsentError.getMessage()));
                });

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        loadAd();
        loadRewardedAd();


        mRunnable = new Runnable() {
            @Override
            public void run() {
                showInterstitialAd();

            }
        };

        mHandler.postDelayed(mRunnable, 3 * 60 * 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable);
    }

    public void loadRewardedAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, "ca-app-pub-9119300254012063/9365526262",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.toString());
                        rewardedAd = null;
                        loadRewardedAd();
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                        Log.d(TAG, "Ad was loaded.");
                    }
                });

    }

    public void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, "ca-app-pub-9119300254012063/1616617503", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                Log.d(TAG, "Ad dismissed fullscreen content.");
                                mInterstitialAd = null;
                                loadAd();
                                mHandler.postDelayed(mRunnable, 5 * 60 * 1000); // mins < seconds < milliseconds
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                Log.e(TAG, "Ad failed to show fullscreen content.");
                                mInterstitialAd = null;
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });
    }
    //public void presentForm() {
    //consentForm.show(
    //MainActivity.this,
    // new ConsentForm.OnConsentFormDismissedListener() {
    //@Override
    //public void onConsentFormDismissed(@Nullable FormError formError) {
    // Handle dismissal by reloading form.
    //consentInformation.loadForm();
    //}
    //});
    //}

    public class AppInterface {
        private Context context;

        public AppInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void closeApp() {
            if (context instanceof Activity) {
                ((Activity) context).finishAffinity();
            }
        }

        @JavascriptInterface
        public void loadReward(int rewardType) {
            if (context instanceof MainActivity) {
                ((MainActivity) context).runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        ((MainActivity) context).showRewardedAd(rewardType);
                    }
                });
            }
        }
    }

    public class mywebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onBackPressed() {

        showExitConfirmationDialog();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mywebView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mywebView.restoreState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_CHOOSER_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                Uri selectedFileUri = data.getData();
                if (fPathCallback != null) {
                    fPathCallback.onReceiveValue(new Uri[]{selectedFileUri});
                    fPathCallback = null;
                    mywebView.post(new Runnable() {
                        @Override
                        public void run() {

                            String javascriptCode = "readData('" + selectedFileUri + "');";
                            mywebView.evaluateJavascript(javascriptCode, null);

                        }
                    });
                } else {
                    ;
                }
            }
        }
    }
}