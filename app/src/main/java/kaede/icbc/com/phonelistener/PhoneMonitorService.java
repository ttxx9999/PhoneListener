package kaede.icbc.com.phonelistener;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;
    import java.util.List;

    public class PhoneMonitorService extends Service {
        private static final String TAG = "PhoneMonitorService";
        TelephonyManager mTelephonyMgr;
        SQLiteDatabase database;
        DataBaseHelper helper;
    public PhoneMonitorService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        database = openOrCreateDatabase("incoming",MODE_PRIVATE,null);
        helper = new DataBaseHelper(database);
        helper.createTable();
        mTelephonyMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyMgr.listen(new TeleListener(),PhoneStateListener.LISTEN_CALL_STATE);
    }

        @Override
        public void onDestroy() {
            database.close();
            super.onDestroy();
        }

        private void endCall() {
        // 初始化iTelephony
        Class<TelephonyManager> c = TelephonyManager.class;
        Method getITelephonyMethod = null;
        try {
            // 获取所有public/private/protected/默认
            // 方法的函数，如果只需要获取public方法，则可以调用getMethod.
            getITelephonyMethod = c.getDeclaredMethod("getITelephony",(Class[]) null);
            // 将要执行的方法对象设置是否进行访问检查，也就是说对于public/private/protected/默认
            // 我们是否能够访问。值为 true 则指示反射的对象在使用时应该取消 Java 语言访问检查。值为 false
            // 则指示反射的对象应该实施 Java 语言访问检查。
            getITelephonyMethod.setAccessible(true);
            ITelephony iTelephony = (ITelephony) getITelephonyMethod.invoke(mTelephonyMgr, (Object[]) null);

            iTelephony.endCall();
            Log.v(this.getClass().getName(), "endCall......");
        } catch (Exception e) {
            Log.e(this.getClass().getName(), "endCallError", e);
        }
    }

    class TeleListener extends PhoneStateListener {
        private static final String TAG = "Telephony";

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state){
                case TelephonyManager.CALL_STATE_RINGING:
                    Toast.makeText(PhoneMonitorService.this,incomingNumber,Toast.LENGTH_SHORT).show();
                    endCall();
                    handleIncoming(incomingNumber);
                    Log.e(TAG, incomingNumber);
                    break;
            }
        }
    }

    private void handleIncoming(String incomingNumber)
    {
        if(incomingNumber.startsWith("+86"))
            incomingNumber = incomingNumber.substring(3);
        if(incomingNumber.length() == 11 || incomingNumber.length() == 6)
        {
            String name = getPerson(incomingNumber);
            if(!helper.sendToday(incomingNumber))
            {

                if(name != null || helper.countUnknowPhone(incomingNumber) >= 2)
                {
                    sendMes(incomingNumber);
                    helper.insert(incomingNumber,name,1);
                }
                else
                    helper.insert(incomingNumber,name,0);
            }
        }
    }

        public String getPerson(String number){
            String name = null;
            Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/"+number);
            ContentResolver resolver = getContentResolver();
            Cursor cursor = resolver.query(uri, new String[]{android.provider.ContactsContract.Data.DISPLAY_NAME}, null, null, null);
            if(cursor.moveToFirst()){
                name = cursor.getString(0);
                Log.i(TAG, name);
            }
            cursor.close();
            return name;
        }

    private void sendMes(String num){
        //直接调用短信接口发短信
        SmsManager smsManager = SmsManager.getDefault();
        List<String> divideContents = smsManager.divideMessage("机主此手机未携带，请联系新号：17005688815！");
        for (String text : divideContents) {
            smsManager.sendTextMessage(num, null, text, null, null);
            Log.i(TAG, "sendsms " + num+" "+text);
            Toast.makeText(this,"sendsms "+num,Toast.LENGTH_SHORT).show();
        }
    }
}
