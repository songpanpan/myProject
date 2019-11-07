package com.yueyou.adreader.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.blankj.utilcode.util.NotificationUtils;
import com.google.gson.Gson;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.message.BindAliasCmdMessage;
import com.igexin.sdk.message.FeedbackCmdMessage;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.igexin.sdk.message.SetTagCmdMessage;
import com.igexin.sdk.message.UnBindAliasCmdMessage;
import com.yueyou.adreader.R;
import com.yueyou.adreader.activity.MainActivity;
import com.yueyou.adreader.service.bean.TouChuanBean;

import java.util.Random;

/**
 * 继承 GTIntentService 接收来自个推的消息, 所有消息在线程中回调, 如果注册了该服务, 则务必要在 AndroidManifest中声明, 否则无法接受消息<br>
 * onReceiveMessageData 处理透传消息<br>
 * onReceiveClientId 接收 cid <br>
 * onReceiveOnlineState cid 离线上线通知 <br>
 * onReceiveCommandResult 各种事件处理回执 <br>
 */
public class YYGTIntentService extends GTIntentService {

    private static final String TAG = "GetuiSdkDemo";
    private com.yueyou.adreader.service.advertisement.partner.ChuangShen.NotificationUtils notificationUtils;
    private com.yueyou.adreader.service.advertisement.partner.ChuangShen.NotificationUtils.ChannelBuilder channelBuilder;
    private PendingIntent pendingIntent;
    /**
     * 为了观察透传数据变化.
     */
    private static int cnt;

    public YYGTIntentService() {

    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
        Log.d(TAG, "onReceiveServicePid -> " + pid);
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        String appid = msg.getAppid();
        String taskid = msg.getTaskId();
        String messageid = msg.getMessageId();
        byte[] payload = msg.getPayload();
        String pkg = msg.getPkgName();
        String cid = msg.getClientId();

        // 第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
        boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
        Log.d(TAG, "call sendFeedbackMessage = " + (result ? "success" : "failed"));

        Log.d(TAG, "onReceiveMessageData -> " + "appid = " + appid + "\ntaskid = " + taskid + "\nmessageid = " + messageid + "\npkg = " + pkg
                + "\ncid = " + cid);

        if (payload == null) {
            Log.e(TAG, "receiver payload = null");
        } else {
            try {
                String data = new String(payload);
                Log.d(TAG, "receiver payload = " + data);
                TouChuanBean touChuanBean = new Gson().fromJson(data, TouChuanBean.class);
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("t", touChuanBean.getT());
                intent.putExtra("d", touChuanBean.getD());

                channelBuilder = new com.yueyou.adreader.service.advertisement.partner.ChuangShen.NotificationUtils.ChannelBuilder("channelGroupOne", "channelTwo", "channelTwoName", NotificationManager.IMPORTANCE_HIGH)
                        .setChannelName("channelTwoName").setByPassDnd(true).setLightColor(Color.GREEN)
                        .setShowBadge(false).setEnableLight(false).setEnableSound(false).setEnableVibrate(false)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                Random random = new Random();

                int requestCode = random.nextInt(10000);
                notificationUtils = new com.yueyou.adreader.service.advertisement.partner.ChuangShen.NotificationUtils(this, channelBuilder);
                notificationUtils.init("channelTwo", "channelTwoName", "channelGroupOne", "channelGroupOneName");
                pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                notificationUtils.notifyMessage(requestCode, pendingIntent, R.mipmap.logo_300, R.mipmap.logo_300, "ticker", "", touChuanBean.getTitle(), touChuanBean.getContent(), 1, false, false, false);

                // 测试消息为了观察数据变化
//            if (data.equals(getResources().getString(R.string.push_transmission_data))) {
//                data = data + "-" + cnt;
//                cnt++;
//            }
                sendMessage(data, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        Log.d(TAG, "----------------------------------------------------------------------------------------------");
    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        Log.e(TAG, "onReceiveClientId -> " + "clientid = " + clientid);

        sendMessage(clientid, 1);
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
        Log.d(TAG, "onReceiveOnlineState -> " + (online ? "online" : "offline"));
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
        Log.d(TAG, "onReceiveCommandResult -> " + cmdMessage);

        int action = cmdMessage.getAction();

        if (action == PushConsts.SET_TAG_RESULT) {
            setTagResult((SetTagCmdMessage) cmdMessage);
        } else if (action == PushConsts.BIND_ALIAS_RESULT) {
            bindAliasResult((BindAliasCmdMessage) cmdMessage);
        } else if (action == PushConsts.UNBIND_ALIAS_RESULT) {
            unbindAliasResult((UnBindAliasCmdMessage) cmdMessage);
        } else if ((action == PushConsts.THIRDPART_FEEDBACK)) {
            feedbackResult((FeedbackCmdMessage) cmdMessage);
        }
    }

    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage message) {
        Log.d(TAG, "onNotificationMessageArrived -> " + "appid = " + message.getAppid() + "\ntaskid = " + message.getTaskId() + "\nmessageid = "
                + message.getMessageId() + "\npkg = " + message.getPkgName() + "\ncid = " + message.getClientId() + "\ntitle = "
                + message.getTitle() + "\ncontent = " + message.getContent());
    }

    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage message) {
        Log.d(TAG, "onNotificationMessageClicked -> " + "appid = " + message.getAppid() + "\ntaskid = " + message.getTaskId() + "\nmessageid = "
                + message.getMessageId() + "\npkg = " + message.getPkgName() + "\ncid = " + message.getClientId() + "\ntitle = "
                + message.getTitle() + "\ncontent = " + message.getContent());
    }

    private void setTagResult(SetTagCmdMessage setTagCmdMsg) {
        String sn = setTagCmdMsg.getSn();
        String code = setTagCmdMsg.getCode();


//        Log.d(TAG, "settag result sn = " + sn + ", code = " + code + ", text = " + getResources().getString(text));
    }

    private void bindAliasResult(BindAliasCmdMessage bindAliasCmdMessage) {
        String sn = bindAliasCmdMessage.getSn();
        String code = bindAliasCmdMessage.getCode();


//        Log.d(TAG, "bindAlias result sn = " + sn + ", code = " + code + ", text = " + getResources().getString(text));

    }

    private void unbindAliasResult(UnBindAliasCmdMessage unBindAliasCmdMessage) {
        String sn = unBindAliasCmdMessage.getSn();
        String code = unBindAliasCmdMessage.getCode();


    }


    private void feedbackResult(FeedbackCmdMessage feedbackCmdMsg) {
        String appid = feedbackCmdMsg.getAppid();
        String taskid = feedbackCmdMsg.getTaskId();
        String actionid = feedbackCmdMsg.getActionId();
        String result = feedbackCmdMsg.getResult();
        long timestamp = feedbackCmdMsg.getTimeStamp();
        String cid = feedbackCmdMsg.getClientId();

        Log.d(TAG, "onReceiveCommandResult -> " + "appid = " + appid + "\ntaskid = " + taskid + "\nactionid = " + actionid + "\nresult = " + result
                + "\ncid = " + cid + "\ntimestamp = " + timestamp);
    }

    private void sendMessage(String data, int what) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = data;
//        DemoApplication.sendMessage(msg);
    }
}
