package kaede.icbc.com.phonelistener;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class PhoneListenerActivity extends Activity {
    private static final String TAG = "PhoneListenerActivity";
    Button startBtn;
    Button stopBtn;
//    Button button;
//    EditText editText;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_listener);
        intent = new Intent(this,PhoneMonitorService.class);
        startBtn = (Button)this.findViewById(R.id.button);
        stopBtn = (Button)this.findViewById(R.id.button2);
//        button = (Button)this.findViewById(R.id.button3);
//        editText = (EditText)this.findViewById(R.id.editText);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneListenerActivity.this.startService(intent);
                Toast.makeText(PhoneListenerActivity.this,"start service",Toast.LENGTH_SHORT).show();
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneListenerActivity.this.stopService(intent);
                Toast.makeText(PhoneListenerActivity.this,"stop service",Toast.LENGTH_SHORT).show();
            }
        });
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getPeople(editText.getText().toString());
//            }
//        });
    }

    public void getPeople(String mNumber) {
        String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};

        Log.d(TAG, "getPeople ---------");

        // 将自己添加到 msPeers 中
        Cursor cursor = this.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,    // Which columns to return.
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = '" + mNumber + "'", // WHERE clause.
                null,          // WHERE clause value substitution
                null);   // Sort order.

        if( cursor == null ) {
            Log.d(TAG, "getPeople null");
            return;
        }
        Log.d(TAG, "getPeople cursor.getCount() = " + cursor.getCount());
        for( int i = 0; i < cursor.getCount(); i++ )
        {
            cursor.moveToPosition(i);

            // 取得联系人名字
            int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
            String name = cursor.getString(nameFieldColumnIndex);
            Log.i("Contacts", "" + name + " .... " + nameFieldColumnIndex); // 这里提示 force close
            Toast.makeText(this,name,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.phone_listener, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
