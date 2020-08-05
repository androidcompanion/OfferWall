package com.offerwallcompanion.offermodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppDetail {

@SerializedName("id")
@Expose
private String id;
@SerializedName("accountName")
@Expose
private String accountName;
@SerializedName("appName")
@Expose
private String appName;
@SerializedName("packageName")
@Expose
private String packageName;
@SerializedName("downloadLink")
@Expose
private String downloadLink;
@SerializedName("imageUrl")
@Expose
private String imageUrl;

public String getId() {
return id;
}

public void setId(String id) {
this.id = id;
}

public String getAccountName() {
return accountName;
}

public void setAccountName(String accountName) {
this.accountName = accountName;
}

public String getAppName() {
return appName;
}

public void setAppName(String appName) {
this.appName = appName;
}

public String getPackageName() {
return packageName;
}

public void setPackageName(String packageName) {
this.packageName = packageName;
}

public String getDownloadLink() {
return downloadLink;
}

public void setDownloadLink(String downloadLink) {
this.downloadLink = downloadLink;
}

public String getImageUrl() {
return imageUrl;
}

public void setImageUrl(String imageUrl) {
this.imageUrl = imageUrl;
}

}