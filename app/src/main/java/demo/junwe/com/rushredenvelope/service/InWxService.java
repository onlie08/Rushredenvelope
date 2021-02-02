package demo.junwe.com.rushredenvelope.service;

import android.accessibilityservice.AccessibilityService;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import demo.junwe.com.rushredenvelope.utils.SPUtils;

public class InWxService extends AccessibilityService
{
    private String TAG = this.getClass().getSimpleName();
    /**
     * 微信几个页面的包名+地址。用于判断在哪个页面
     * LAUCHER-微信聊天界面
     * LUCKEY_MONEY_RECEIVER-点击红包弹出的界面
     * LUCKEY_MONEY_DETAIL-红包领取后的详情界面
     */
    private String LAUCHER = "com.tencent.mm.ui.LauncherUI";
    private String LUCKEY_MONEY_DETAIL = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI";
    private String LUCKEY_MONEY_RECEIVER = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";
    private String LUCKEY_MONEY_RECEIVER1 = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyNotHookReceiveUI";


    /**
     * 用于判断是否屏幕是亮着的
     */
    private boolean isScreenOn;

    /**
     * 获取PowerManager.WakeLock对象
     */
    private PowerManager.WakeLock wakeLock;

    /**
     * KeyguardManager.KeyguardLock对象
     */
    private KeyguardManager.KeyguardLock keyguardLock;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event)
    {
        if (event.getClassName() != null)
            Log.e("caohai:  ", event.getClassName().toString() + " EventType:"+event.getEventType());
        int eventType = event.getEventType();
        switch (eventType)
        {
            //通知栏来信息，判断是否含有微信红包字样，是的话跳转
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                List<CharSequence> texts = event.getText();
                for (CharSequence text : texts)
                {
                    String content = text.toString();
                    if (!TextUtils.isEmpty(content))
                    {
                        //判断是否含有[微信红包]字样
                        if (content.contains("[微信红包]"))
                        {
                            if (!isScreenOn())
                            {
                                wakeUpScreen();
                            }
                            //如果有则打开微信红包页面
                            openWeChatPage(event);

                        }
                    }
                }
                break;
             case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String className = null;
                if (event.getClassName() != null)
                {
                    className = event.getClassName().toString();
                }
//                performBackClick(1000);
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                boolean reEnable = (boolean)SPUtils.get(this,"openEnable",false);
                if(reEnable){
                    AccessibilityNodeInfo rootNode = getRootInActiveWindow();
                    findRedPacket1(rootNode);
                    openRedPacket1(rootNode);
                    boolean back_enable = (boolean) SPUtils.get(this,"backEnable",false);
                    if(back_enable){
                        closeRedPacket(rootNode);
                    }
                }
                break;
        }

    }

    /**
     * 关闭红包页
     */
    private void closeRedPacket(AccessibilityNodeInfo rootNode) {
        if (rootNode == null){
            Log.e("caohai", "openRedPacket1为空");
            return;
        }
        List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/eh");
        for (AccessibilityNodeInfo n : list) {
            if(n.isClickable()){
                n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    /**
     * 开始打开红包
     */
    private void openRedPacket1(AccessibilityNodeInfo rootNode)
    {
        if (rootNode == null){
            Log.e("caohai", "openRedPacket1为空");
            return;
        }
        List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/f4f");
        Log.e("caohai", "拆红包"+list.size());
        for (AccessibilityNodeInfo n : list) {
            if(n.isClickable()){
                n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    /**
     * 开始找红包
     */
    private void findRedPacket1(AccessibilityNodeInfo rootNode)
    {
        if (rootNode == null){
            Log.e("caohai", "findRedPacket1");
            return;
        }
        List<AccessibilityNodeInfo> tt = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/tt"); //已领取
        List<AccessibilityNodeInfo> aum = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/u5"); //微信红包
        List<AccessibilityNodeInfo> bal = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/e7t"); //微信聊天列表里包含新发的微信红包
        if(!bal.isEmpty()){
            for(AccessibilityNodeInfo n : bal){
                if(n.getText().toString().contains("[微信红包]")){
                    AccessibilityNodeInfo parent = n.getParent();
                    while (parent != null)
                    {
                        if (parent.isClickable())
                        {
                            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            break;
                        }
                        parent = parent.getParent();
                    }
                }
            }
        }
        if(!aum.isEmpty()){
            for(int i= aum.size()-1;i>=0;i--){
                if(aum.get(i).getText().toString().contains("[微信红包]")){
                    AccessibilityNodeInfo parent = aum.get(i).getParent();
                    while (parent != null)
                    {
                        if (parent.isClickable())
                        {
                            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            break;
                        }
                        parent = parent.getParent();
                    }
                }
            }
        }
        Log.d(TAG,"aum.size():"+aum.size());
        Log.e("caohai", "找红包 已领取："+tt.size() + "微信红包："+aum.size());
        if(aum.isEmpty()){
            return;
        }
        if(tt.size() == aum.size()){
            return;
        }
        AccessibilityNodeInfo parent = aum.get(aum.size()-1).getParent();
        while (parent != null)
        {
            if (parent.isClickable())
            {
                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            }
            parent = parent.getParent();
        }
    }

    /**
     * 开启红包所在的聊天页面
     */
    private void openWeChatPage(AccessibilityEvent event)
    {
        if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification)
        {
            Notification notification = (Notification) event.getParcelableData();

            //打开对应的聊天界面
            PendingIntent pendingIntent = notification.contentIntent;
            try
            {
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e)
            {
                e.printStackTrace();
            }
        }
    }


    /**
     * 服务连接
     */
    @Override
    protected void onServiceConnected()
    {
        Toast.makeText(this, "抢红包服务开启", Toast.LENGTH_SHORT).show();
        super.onServiceConnected();
    }

    /**
     * 必须重写的方法：系统要中断此service返回的响应时会调用。在整个生命周期会被调用多次。
     */
    @Override
    public void onInterrupt()
    {
        Toast.makeText(this, "我快被终结了啊-----", Toast.LENGTH_SHORT).show();
    }

    /**
     * 服务断开
     */
    @Override
    public boolean onUnbind(Intent intent)
    {
        Toast.makeText(this, "抢红包服务已被关闭", Toast.LENGTH_SHORT).show();
        return super.onUnbind(intent);
    }

    /**
     * 判断是否处于亮屏状态
     *
     * @return true-亮屏，false-暗屏
     */
    private boolean isScreenOn()
    {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        isScreenOn = pm.isScreenOn();
        Log.e("isScreenOn", isScreenOn + "");
        return isScreenOn;
    }

    /**
     * 解锁屏幕
     */
    private void wakeUpScreen()
    {

        //获取电源管理器对象
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //后面的参数|表示同时传入两个值，最后的是调试用的Tag
        wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK, "red:bright");
        if (wakeLock == null)
        {
            return;
        }
        //点亮屏幕
        wakeLock.acquire();

        //得到键盘锁管理器
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        keyguardLock = km.newKeyguardLock("unlock");

        //解锁
        keyguardLock.disableKeyguard();
    }

    public void performBackClick(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        performGlobalAction(GLOBAL_ACTION_BACK);
    }
}

