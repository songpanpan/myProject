# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-dontwarn android.support.**
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.**
-keep public class com.android.vending.licensing.ILicensingService
-ignorewarnings
-dontwarn com.tencent.**
-keep class com.squareup.okhttp.** { *;}
-dontwarn com.squareup.okhttp.**
-dontwarn okio.**
-dontwarn com.ipaynow.**
-dontwarn com.alipay.**
-dontwarn com.bumptech.**
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule
-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context);
}
-keep class com.ipaynow.** { *; }
#-keep class com.yueyou.adreader.activity.** {
#    public <fields>;
#    public <methods>;
#}

#-keep public class com.yueyou.adreader.R$*{
#    public static final int *;
#}
#
#-keep public class com.yueyou.adreaderwp.R$*{
#    public static final int *;
#}

-keep class com.yueyou.adreader.wxapi.** {
      *;
}
-keep class com.yueyou.adreader.service.analytics.model.** {
    *;
}
-keep class com.yueyou.adreader.service.model.** {
    *;
}
-keep class com.yueyou.adreader.service.advertisement.** {
    *;
}
-keep class com.yueyou.adreader.view.dlg.AlertWindow {
    public void loginSuccess();
    public void rechargeSuccess();
    public void bindSuccess();
    public void closeView();
    public void buy();
    public void goBack();
}
-keep class com.yueyou.adreader.view.dlg.WebViewDlg {
    public void loginSuccess();
    public void rechargeSuccess();
    public void bindSuccess();
    public void closeView();
    public void buy();
    public void goBack();
}
-keep class com.yueyou.adreader.frament.PersionalFrament {
    public void loginSuccess();
    public void rechargeSuccess();
    public void bindSuccess();
    public void closeView();
    public void closeView();
    public void buy();
    public void goBack();
}
-keep class com.yueyou.adreader.activity.ViewPagerView.PersionalView {
    public void loginSuccess();
    public void rechargeSuccess();
    public void bindSuccess();
    public void closeView();
    public void buy();
    public void goBack();
}
-keep class com.yueyou.adreader.activity.WebViewActivity {
    public void loginSuccess();
    public void rechargeSuccess();
    public void bindSuccess();
    public void closeView();
    public void buy();
    public void goBack();
}

-keep class com.hz.yl.** {
    <fields>;
    <methods>;
}

-keep class com.glide.** {
     <fields>;
     <methods>;
 }

-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.qq.e.** {
        public protected *;
    }
-keep class MTT.ThirdAppInfoNew {
        *;
    }
-keep class com.tencent.** {
        *;
    }
 -keep class android.support.v4.**{
        public *;
    }
-keep class android.support.v7.**{
    public *;
}

# 百度联盟

-keepclassmembers class * extends android.app.Activity {
 public void *(android.view.View);
}
-keepclassmembers enum * {
 public static **[] values();
 public static ** valueOf(java.lang.String);
}
-keep class com.baidu.mobads.*.** { *; }

#穿山甲联盟
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
-keep class com.bytedance.sdk.openadsdk.** { *; }
-keep class com.androidquery.callback.** {*;}
-keep public interface com.bytedance.sdk.openadsdk.downloadnew.** {*;}
-keep class com.bytedance.sdk.openadsdk.service.TTDownloadProvider

# 信鸽
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep class com.tencent.android.tpush.** {* ;}
-keep class com.tencent.mid.** {* ;}
-keep class com.qq.taf.jce.** {*;}
-keep class com.tencent.bigdata.** {* ;}

# Bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

-keep class com.umeng.** {*;}

# umeng
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#sogou
-keep class com.sogou.feedads.**{*;}

#wechatpay
-keep class com.tencent.** {*;}
-keep class com.yueyou.adreaderwp.wxapi.WXPayEntryActivity
-keep class com.yueyou.adreader.wxapi.WXPayEntryActivity
-keep class com.yueyou.adreader.service.bean.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keep public interface com.tencent.**
-keep class com.yueyou.adreader.service.WechatPay

#OAID
-keep class com.bun.miitmdid.core.** {*;}

#个推混淆
-dontwarn com.igexin.**
-keep class com.igexin.** { *; }
-keep class org.json.** { *; }

#华为厂商推送
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-dontwarn com.huawei.**
-dontwarn com.hianalytics.android.**
-keep class com.hianalytics.android.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}
-keep class com.huawei.android.** { *; }
-keep interface com.huawei.android.hms.agent.common.INoProguard {*;}
-keep class * extends com.huawei.android.hms.agent.common.INoProguard {*;}

#OPPO厂商混淆
-keep class com.coloros.mcssdk.** { *; }
-dontwarn com.coloros.mcssdk.**

#VIVO厂商混淆
-keep class com.vivo.push.** { *; }
-dontwarn com.vivo.push.**

#小米厂商混淆
-dontwarn com.xiaomi.**
-keep class com.xiaomi.** { *; }
-keep class org.apache.thrift.** { *; }

#kedaxunfei
-keep class com.iflytek.** {* ;}
-keep class android.support.v4.**{public * ;}