package com.offerwallcompanion;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.offerwallcompanion.offermodels.AppAdsById;
import com.offerwallcompanion.offermodels.AppDetail;
import com.offerwallcompanion.offermodels.GsonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import cz.msebera.android.httpclient.Header;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class OffersFragment extends Fragment {


    SharedPreferences offerPref;
    int id;
    Button btn_offerwallsubmit;
    TextView app_name, txt_2, txt_6;
    ImageView app_icon, copy_app_link;
    String name, account_name, package_name, imageTitle, download_link;
    LinearLayout download_link_card;
    RelativeLayout no_layout;
    List<AppDetail> appDetailArrayList = new ArrayList<>();
    ProgressDialog progressDialog;
    GsonUtils gsonUtils;
    SharedPreferences.Editor offreEditor;
    Gson gson;
    Activity activity;
    String url;
    Callable<Void> mathodToCall;

    public  OffersFragment(Activity activity,String url,Callable<Void> mathodToCall){
        this.activity = activity;
        this.mathodToCall = mathodToCall;
        this.url = url;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_offers, container, false);
        progressDialog = new ProgressDialog(getContext(), R.style.DialogTheme_offer);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Getting Offers..Please Wait");
        gson= new Gson();
        offerPref = activity.getSharedPreferences("offerPref",Context.MODE_PRIVATE);
        offreEditor = offerPref.edit();

        if (isDataOnOffer(activity)){
            getProductDataonCreate();
        }else {
            showNoInternetDialog(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    getProductDataonCreate();
                    return null;
                }
            });
        }

        app_name = view.findViewById(R.id.app_name);
        no_layout = view.findViewById(R.id.no_layout);
        app_icon = view.findViewById(R.id.app_icon);
        copy_app_link = view.findViewById(R.id.copy_app_link);
        txt_2 = view.findViewById(R.id.txt_2);
        txt_6 = view.findViewById(R.id.txt_6);
        download_link_card = view.findViewById(R.id.download_link_card);
        btn_offerwallsubmit = view.findViewById(R.id.btn_offerwallsubmit);

        return view;
    }

    public void getProductDataonCreate() {

        progressDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();

        gsonUtils = GsonUtils.getInstance();

        try {
            client.setConnectTimeout(50000);
            client.post(url, new BaseJsonHttpResponseHandler<AppAdsById>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, AppAdsById response) {

                    if (response.getSuccess() == 1) {
                        setAppAdsDetails(response);
                        progressDialog.dismiss();

                        List<AppAdsById> appResponses = Collections.singletonList(getAppAdsDetails());
                        id = getAppAdsId();
                        for (int i = 0; i < appResponses.size(); i++) {
                            appDetailArrayList = checkIsAppInstalledOrNot(appResponses.get(i).getAppDetail());
                        }

                        for (int j = 0; j < appDetailArrayList.size(); j++) {
                            boolean isAppInstalled = appInstalledOrNot(appDetailArrayList.get(j).getPackageName());
                            if (isAppInstalled) {
                                no_layout.setVisibility(View.VISIBLE);
                            } else {
                                no_layout.setVisibility(View.GONE);
                                if (id == 0) {
                                    progressDialog.show();
                                    if (isDataOnOffer(activity)){
                                        getProductData(appDetailArrayList);
                                    }else {
                                        showNoInternetDialog(new Callable<Void>() {
                                            @Override
                                            public Void call() throws Exception {
                                                getProductData(appDetailArrayList);
                                                return null;
                                            }
                                        });
                                    }
                                } else {
                                    int numMoved = appResponses.size();
                                    if (numMoved > id) {
                                        name = appDetailArrayList.get(id).getAppName();
                                        account_name = appDetailArrayList.get(id).getAccountName();
                                        package_name = appDetailArrayList.get(id).getPackageName();
                                        imageTitle = appDetailArrayList.get(id).getImageUrl();
                                        download_link = appDetailArrayList.get(id).getDownloadLink();
                                        id++;
                                        setAppAdsId(id);
                                        initView(package_name);

                                    } else {

                                        id = 0;
                                        setAppAdsId(id);
                                        name = appDetailArrayList.get(id).getAppName();
                                        account_name = appDetailArrayList.get(id).getAccountName();
                                        package_name = appDetailArrayList.get(id).getPackageName();
                                        imageTitle = appDetailArrayList.get(id).getImageUrl();
                                        download_link = appDetailArrayList.get(id).getDownloadLink();
                                        initView(package_name);
                                    }
                                }
                            }
                        }

                    } else {
                        progressDialog.dismiss();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, AppAdsById errorResponse) {
                    Toast.makeText(getContext(), "Please Try Again after Sometime", Toast.LENGTH_SHORT).show();
                }


                @Override
                protected AppAdsById parseResponse(String rawJsonData, boolean isFailure) throws Throwable {

                    try {
                        if (!isFailure && !rawJsonData.isEmpty()) {
                            return gsonUtils.getGson().fromJson(rawJsonData, AppAdsById.class);
                        }
                    } catch (Exception e) {

                    }
                    return null;
                }
            });

        } catch (Exception ignored) {

        }
    }

    private void getProductData(final List<AppDetail> appDetailArrayList) {
        AsyncHttpClient client = new AsyncHttpClient();

        gsonUtils = GsonUtils.getInstance();

        try {
            client.setConnectTimeout(50000);
            client.post( url, new BaseJsonHttpResponseHandler<AppAdsById>() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, AppAdsById response) {
//                    AppAdsById appResponse = response.body();

                    if (response.getSuccess() == 1) {

                        id++;
                        setAppAdsDetails(response);
                        name = appDetailArrayList.get(0).getAppName();
                        account_name = appDetailArrayList.get(0).getAccountName();
                        package_name = appDetailArrayList.get(0).getPackageName();
                        imageTitle = appDetailArrayList.get(0).getImageUrl();
                        download_link = appDetailArrayList.get(0).getDownloadLink();
                        setAppAdsId(id);
                        initView(package_name);
                        progressDialog.dismiss();

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(),"Please Try Again After Sometime", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, AppAdsById errorResponse) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Please Try Again After Sometime", Toast.LENGTH_SHORT).show();
                }


                @Override
                protected AppAdsById parseResponse(String rawJsonData, boolean isFailure) throws Throwable {

                    try {
                        if (!isFailure && !rawJsonData.isEmpty()) {
                            return gsonUtils.getGson().fromJson(rawJsonData, AppAdsById.class);
                        }
                    } catch (Exception e) {

                    }
                    return null;
                }
            });

        } catch (Exception ignored) {

        }
    }

    public void showNoInternetDialog(final Callable<Void> mathodtoFollow) {
            final Dialog d = new Dialog(activity);
            d.setContentView(R.layout.no_connection_offer);
            Button btn_retry = d.findViewById(R.id.btn_retry);
            d.setCancelable(false);
            btn_retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isDataOnOffer(activity)){
                        try {
                            mathodtoFollow.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        d.dismiss();
                    }else {
                        Toast.makeText(activity, "No Internet Detected", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            d.show();
    }

    public void initView(final String package_name) {
        app_name.setText(name);
        txt_2.setText("2. Search " + name + ", For that type or \n     copy this name.");
        txt_6.setText("6. Give Feedback(review) to " + name + ".");
        Glide.with(this).load(imageTitle).into(app_icon);


        if (download_link.equals("null1")) {
            copy_app_link.setVisibility(View.VISIBLE);
        } else {

            download_link_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(download_link)));
                }
            });
        }


        copy_app_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(account_name);
                Toast.makeText(getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });


        btn_offerwallsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isDataOnOffer(activity)) {
                    // Use package name which we want to check
                    boolean isAppInstalled = appInstalledOrNot(package_name);


                    if (isAppInstalled) {
                        //This intent will help you to launch if the package is already installed

//                        Intent intent = new Intent(getContext(), nextActivity);
//                        startActivity(intent);

                        try {
                            mathodToCall.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        activity.finish();

                    } else {
                        Toast.makeText(activity, name + " " + "App is not installed in your Device.", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    showNoInternetDialog(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            return null;
                        }
                    });
                }


            }
        });
    }

    public boolean appInstalledOrNot(String uri) {
        PackageManager pm = activity.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    private List<AppDetail> checkIsAppInstalledOrNot(List<AppDetail> appDetails) {
        List<AppDetail> appDetailList = new ArrayList<>();
        for (int i = 0; i < appDetails.size(); i++) {
            boolean isAppInstalled = appInstalledOrNot(appDetails.get(i).getPackageName().toString());
            if (isAppInstalled) {
                no_layout.setVisibility(View.VISIBLE);
            } else {
                AppDetail appDetail = new AppDetail();
                appDetail.setPackageName(appDetails.get(i).getPackageName());
                appDetail.setAccountName(appDetails.get(i).getAccountName());
                appDetail.setAppName(appDetails.get(i).getAppName());
                appDetail.setDownloadLink(appDetails.get(i).getDownloadLink());
                appDetail.setId(appDetails.get(i).getId());
                appDetail.setImageUrl(appDetails.get(i).getImageUrl());
                appDetailList.add(appDetail);
            }
        }
        return appDetailList;
    }

    public AppAdsById getAppAdsDetails() {
        String json = offerPref.getString("AppAdsData", "");
        AppAdsById appResponse = gson.fromJson(json, AppAdsById.class);
        return appResponse;
    }

    public void setAppAdsDetails(AppAdsById user) {
        String json = gson.toJson(user);
        offreEditor.putString("AppAdsData", json);
        offreEditor.commit();
    }

    public void setAppAdsId(int AppAdsId) {
        if (offerPref != null) {
            offreEditor = offerPref.edit();
            offreEditor.putInt("AppAdsId", AppAdsId);
            offreEditor.apply();
        }
    }

    public int getAppAdsId() {
        int var = 0;
        if (offerPref != null) {
            var = offerPref.getInt("AppAdsId", 0);
        }
        return var;
    }

    public static boolean isDataOnOffer(Context context) {
        NetworkInfo netinfo = ((ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return netinfo != null && netinfo.isConnectedOrConnecting();
    }



}