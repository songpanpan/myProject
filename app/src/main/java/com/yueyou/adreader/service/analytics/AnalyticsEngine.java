package com.yueyou.adreader.service.analytics;

import android.content.Context;

import com.yueyou.adreader.service.HttpEngine;
import com.yueyou.adreader.service.db.DataSHP;
import com.yueyou.adreader.util.Utils;

import java.util.HashMap;
import java.util.Map;

public class AnalyticsEngine {
    public static void engineInit(Context context) {
        Bi.initHandler(context);
    }

    public static void activate(Context context, String siteId, String bookId, String bookName) {
        Bi.activate(context, siteId, bookId, bookName);
        ThirdAnalytics.onEventActivate(context, siteId, bookId, bookName);
    }

    public static void login(Context context) {
        String userId = DataSHP.getUserId(context);
        if (userId == null)
            return;
        Bi.login(context, userId);
        ThirdAnalytics.onEventLogin(context, userId, "");
    }

    public static void addBuildinBookFinish(Context context, boolean result, String msg) {
        Bi.addBuildinBookFinish(context, result, msg);
        Map<String, String> map = new HashMap<>();
        map.put("result", result ? "fail" : "success");
        map.put("msg", msg);
        ThirdAnalytics.onEvent(context, ThirdAnalytics.EventId.EVENT_READ_BUILD_BOOK, map);
    }

    public static void refreshVaild(Context context) {
        String userId = DataSHP.getUserId(context);
        if (userId == null)
            return;
        Bi.vaild(context, userId);
        ThirdAnalytics.onEventRefreshVaild(context,userId,"");
    }

    public static void read(Context context, int bookId, String bookName, int chapterId, boolean isLastChapter,int words) {
        String userId = DataSHP.getUserId(context);
        if (userId == null)
            return;
        Bi.read(context, userId, bookId, bookName, chapterId, isLastChapter,words);
        ThirdAnalytics.onEventRead(context, bookId, bookName, isLastChapter);
    }

    public static void advertisement(Context context, int siteId, String cp, boolean clicked) {
        Utils.logNoTag("advertisement siteId -> %d ,clicked -> %b cp -> %s", siteId, clicked, cp);
        Bi.advertisement(context, siteId, cp, clicked);
        ThirdAnalytics.onEventAdvertisement(context, siteId, cp, clicked);
    }

    public static void advertisementEnd(Context context, int siteId, String cp, HttpEngine.HttpEngineListener listener) {
        Bi.advertisementEnd(context, siteId, cp, listener);
    }

    public static void getDeviceInfo() {
//        NSString *deviceID = UMConfigure.deviceIDForIntegration;
//        NSData* jsonData = [NSJSONSerialization dataWithJSONObject:@{@"oid" : deviceID}
//        options:NSJSONWritingPrettyPrinted
//        error:nil];
//        NSLog(@"%@", [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding]);
    }

    public static void pageEnter(String pageName, Context context) {
//        if (pageName == nil || pageName.length == 0) {
//            pageName = NSStringFromClass([viewController class]);
//        }
//        [MobClick beginLogPageView:pageName];
    }

    public static void pageLeave(String pageName, Context context) {
//        if (pageName == nil || pageName.length == 0) {
//            pageName = NSStringFromClass([viewController class]);
//        }
//    [MobClick endLogPageView:pageName];
    }

    public static void bookRead(String bookName) {
//        NSDictionary *dict = @{@"bookName" : bookName};
//        [MobClick event:@"readEvent" attributes:dict];
    }

    public static void bookReadWithChapter(String bookName, String chapterName) {
//        NSDictionary *dict = @{@"bookName" : bookName, @"chapterName" : chapterName};
//        [MobClick event:@"readChapterEvent" attributes:dict];
    }

    public static void rechargePre(String type) {
//        NSRange range = [type rangeOfString:@"type="];
//        if (range.length == 0) {
//            return;
//        }
//        type = [type substringWithRange:NSMakeRange(range.location + range.length, 1)];
//        NSDictionary *dict = @{@"productType" : type};
//        [MobClick event:@"rechargePreEvent" attributes:dict];
    }

    public static void recharge(String productId) {
//        NSDictionary *dict = @{@"productId" : productId};
//        [MobClick event:@"rechargeEvent" attributes:dict];
    }

    public static void rechargeVerify(String productId) {
//        NSDictionary *dict = @{@"productId" : productId};
//        [MobClick event:@"rechargeVerifyEvent" attributes:dict];
    }

    public static void rechargeFailed(String productId, String msg) {
//        if (msg == nil) {
//            msg = @"";
//        }
//        NSDictionary *dict = @{@"productId" : productId, @"msg" : msg};
//        [MobClick event:@"rechargeFailedEvent" attributes:dict];
    }

    public static void rechargeSuccess(String productId) {
//        NSDictionary *dict = @{@"productId" : productId};
//        [MobClick event:@"rechargeSuccessEvent" attributes:dict];
    }
}
