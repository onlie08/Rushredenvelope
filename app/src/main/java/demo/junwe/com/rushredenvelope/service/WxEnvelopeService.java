package demo.junwe.com.rushredenvelope.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.Instrumentation;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import android.widget.Toast;

import java.util.List;

public class WxEnvelopeService extends AccessibilityService {


    static final String TAG = "caohai";

    /**
     * 微信的包名
     */
    static final String WECHAT_PACKAGENAME = "com.tencent.mm";
    /**
     * 红包消息的关键字
     */
    static final String ENVELOPE_TEXT_KEY = "[微信红包]";

    Handler handler = new Handler();
    AccessibilityNodeInfo mParent;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        final int eventType = event.getEventType();

        Log.d(TAG, "事件---->" + event);

        //通知栏事件
        if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            List<CharSequence> texts = event.getText();
            if (!texts.isEmpty()) {
                for (CharSequence t : texts) {
                    String text = String.valueOf(t);
                    if (text.contains(ENVELOPE_TEXT_KEY)) {
                        openNotification(event);
                        break;
                    }
                }
            }
        } else if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            openEnvelope(event);
        }
//            else {
//                openEnvelope(event);
//            }
    }

    /*@Override
    protected boolean onKeyEvent(KeyEvent event) {
        //return super.onKeyEvent(event);
        return true;
    }*/

    @Override
    public void onInterrupt() {
        Toast.makeText(this, "中断抢红包服务", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Toast.makeText(this, "连接抢红包服务", Toast.LENGTH_SHORT).show();
    }

    private void sendNotificationEvent() {
        AccessibilityManager manager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
        if (!manager.isEnabled()) {
            return;
        }
        AccessibilityEvent event = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED);
        event.setPackageName(WECHAT_PACKAGENAME);
        event.setClassName(Notification.class.getName());
        CharSequence tickerText = ENVELOPE_TEXT_KEY;
        event.getText().add(tickerText);
        manager.sendAccessibilityEvent(event);
    }

    /**
     * 打开通知栏消息
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void openNotification(AccessibilityEvent event) {
        if (event.getParcelableData() == null || !(event.getParcelableData() instanceof Notification)) {
            return;
        }
        //以下是精华，将微信的通知栏消息打开
        Notification notification = (Notification) event.getParcelableData();
        PendingIntent pendingIntent = notification.contentIntent;
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void openEnvelope(final AccessibilityEvent event) {
//            if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI".equals(event.getClassName())) {
        if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI".equals(event.getClassName())) {
//            if ("android.widget.LinearLayout".equals(event.getClassName())) {
            //点中了红包，下一步就是去拆红包
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
                    checkKey1();
//                    new ThreadClass(540,1300).run();
//                    AccessibilityNodeInfo nodeInfo = event.getSource();
//                    if (nodeInfo == null) {
//                        Log.w(TAG, "event.getSource()为空");
//                    }else {
//                        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/dan");
//                        List<AccessibilityNodeInfo> list1 = nodeInfo.findAccessibilityNodeInfosByText("开");
//                        List<AccessibilityNodeInfo> list2 = nodeInfo.findAccessibilityNodeInfosByText("恭喜发财");
//                        List<AccessibilityNodeInfo> list3 = nodeInfo.findAccessibilityNodeInfosByText("微信红包");
//                        Log.w(TAG, "list.size():"+list.size()+"list1.size():"+list1.size()+"list2.size():"+list2.size()+"list3.size():"+list3.size());
//
//                        for (AccessibilityNodeInfo n : list) {
//                            n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                        }
//                    }
//                }
//            },1000);
        } else if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI".equals(event.getClassName())) {
            //拆完红包后看详细的纪录界面
            //nonething
        } else if ("com.tencent.mm.ui.LauncherUI".equals(event.getClassName())) {
            //在聊天界面,去点中红包
            checkKey2();
        }else if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyNotHookReceiveUI".equals(event.getClassName())) {
            checkKey1();
        }
//            else{
////                checkKey1();
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        checkKey1();
//                    }
//                },1000);
//            }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void checkKey1() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Instrumentation inst = new Instrumentation();
//                    inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),
//                            MotionEvent.ACTION_DOWN, 540,1450, 0));
//                    inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),
//                            MotionEvent.ACTION_UP, 540,1450, 0));
//                    Log.d("点击位置", 540+","+1450);
//                }catch(Exception e) {
//                    Log.e(" when ", e.toString());
//                }
//            }
//        }).start();

//        if(null != mParent){
//
//            List<AccessibilityNodeInfo> list = mParent.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/dan");
//            List<AccessibilityNodeInfo> list1 = mParent.findAccessibilityNodeInfosByText("开");
//            List<AccessibilityNodeInfo> list2 = mParent.findAccessibilityNodeInfosByText("恭喜发财");
//            List<AccessibilityNodeInfo> list3 = mParent.findAccessibilityNodeInfosByText("微信红包");
//            Log.w(TAG, "list.size():"+list.size()+"list1.size():"+list1.size()+"list2.size():"+list2.size()+"list3.size():"+list3.size());
//        }

        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            Log.w(TAG, "rootWindow为空3");
            return;
        }
//            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("开");
//        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/cnu");
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/dan");
//        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("恭喜");

        for (AccessibilityNodeInfo n : list) {
            n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }


//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                }).start();
//            }
//        },10000);

//            List<AccessibilityNodeInfo> listS = mParent.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/dan");
//            Log.w(TAG, "ByViewId(com.tencent.mm:id/dan)"+ listS.size());
//            List<AccessibilityWindowInfo> nodeInfos = getWindows();
//            if(nodeInfos.isEmpty()){
//                Log.w(TAG, "rootWindow为空1");
//                return;
//            }
//            for(AccessibilityWindowInfo accessibilityWindowInfo : nodeInfos){
//                if(null != accessibilityWindowInfo.getRoot()){
//                    Log.w(TAG, "rootWindow为空2");
//                }
//
//            }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void checkKey2() {

        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            Log.w(TAG, "rootWindow为空");
            return;
        }
//        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("领取红包");
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("微信红包");
        Log.w(TAG, "微信红包为"+list.size());
//            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/asl");
//            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/asm");
//            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("领取红包");
        if (list.isEmpty()) {
//                list = nodeInfo.findAccessibilityNodeInfosByText(ENVELOPE_TEXT_KEY);
            list = nodeInfo.findAccessibilityNodeInfosByText("微信红包");
            for (AccessibilityNodeInfo n : list) {
                Log.i(TAG, "-->微信红包:" + n);
                n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            }
        } else {
            //最新的红包领起
//            AccessibilityNodeInfo parent = list.get(list.size() - 1).getParent();
//            Log.i(TAG, "-->领取红包:" + parent);
//            if (parent != null) {
//                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//            }
//            mParent = parent;
            for (int i = list.size() - 1; i >= 0; i--) {
                AccessibilityNodeInfo parent = list.get(i).getParent();
                Log.i(TAG, "-->领取红包:" + parent);
                if (parent != null) {
                    parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    break;
                }
            }
            nodeInfo.recycle();
//                AccessibilityNodeInfo parent = list.get(list.size() - 1).getParent();
//                Log.i(TAG, "-->领取红包:" + parent);
//                if (parent != null) {
//                    parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
////                    break;
//                }
//                mParent = parent;
//                for (int i = list.size() - 1; i >= 0; i--) {
//                    AccessibilityNodeInfo parent = list.get(i).getParent();
//                    Log.i(TAG, "-->领取红包:" + parent);
//                    if (parent != null) {
//                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                        break;
//                    }
//                }
        }
    }

    private void setSimulateClick(View view, float x, float y)
    {
        long downTime = SystemClock.uptimeMillis();
        final MotionEvent downEvent = MotionEvent.obtain(downTime, downTime,    MotionEvent.ACTION_DOWN, x, y, 0);
        downTime += 1000;
        final MotionEvent upEvent = MotionEvent.obtain(downTime, downTime,    MotionEvent.ACTION_UP, x, y, 0);
        view.onTouchEvent(downEvent);
        view.onTouchEvent(upEvent);
        downEvent.recycle();
        upEvent.recycle();
    }


}
