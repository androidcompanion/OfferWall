package com.offerwallcompanion.offermodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AppAdsById {

    @SerializedName("AppDetail")
    @Expose
    private List<AppDetail> appDetail = null;
    @SerializedName("success")
    @Expose
    private Integer success;

    public List<AppDetail> getAppDetail() {
        return appDetail;
    }

    public void setAppDetail(List<AppDetail> appDetail) {
        this.appDetail = appDetail;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

}