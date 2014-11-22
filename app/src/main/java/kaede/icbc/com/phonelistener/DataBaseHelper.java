package kaede.icbc.com.phonelistener;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by han on 2014/11/22.
 */
public class DataBaseHelper {
    private static final String TAG = "DataBaseHelper";
    SQLiteDatabase datebase;
    public DataBaseHelper(SQLiteDatabase datebase)
    {
        this.datebase = datebase;
    }

    public void createTable()
    {
        datebase.execSQL("CREATE TABLE IF NOT EXISTS incoming (id integer primary key AutoIncrement," +
                "name varchar(20),phonenum varchar(20),calltime date,sendsms smallint)");

    }

    public void insert(String phonenum,String name,int sendsms)
    {
        datebase.execSQL("insert into incoming(name,phonenum,calltime,sendsms) values(?,?,date('now'),?)",new Object[]{name,phonenum,sendsms});
        Log.i(TAG,"insert "+phonenum+" "+name+" "+sendsms);
    }

    public boolean sendToday(String phonenum)
    {
        Cursor cursor = datebase.rawQuery("select * from incoming where sendsms = 1 and phonenum = ? and calltime = date('now')",new String[]{phonenum});
        if(cursor.moveToNext())
            return true;
        else
            return false;
    }

    public long countUnknowPhone(String phonenum)
    {
        Cursor cursor = datebase.rawQuery("select count(*) from incoming where sendsms = 0 and phonenum = ? and calltime = date('now')",new String[]{phonenum});
        cursor.moveToFirst();
        long count = cursor.getLong(0);
        return count;
    }
}
