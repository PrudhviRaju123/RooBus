package com.example.roobusapp;
/**
 * Created by Prudhvi on 12/15/2014.
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DBActivity extends SQLiteOpenHelper {

    private static final String Database_Name = "RooBus.db";
    private static final String create_tab_bus_table = "CREATE TABLE IF NOT EXISTS BUS_TABLE ( BUS_NAME TEXT PRIMARY KEY NOT NULL ,BUS_CODE  TEXT NOT NULL , BUS_ST_TIME TEXT NOT NULL , BUS_END_TIME TEXT NOT NULL , WEEK_DAY INTEGER NOT NULL,SAT INTEGER NOT NULL,SUN INTEGER NOT NULL)";
    
    private static final String total_data ="CREATE TABLE IF NOT EXISTS TOTAL_DATA (ID INTEGER PRIMARY KEY NOT NULL ,LOCATION_NAME TEXT  NOT NULL , LAT REAL NOT NULL , LONG REAL NOT NULL)";
    private static final int DATABASE_VERSION = 5;
    private Context context;
    private String routes[] ={"CHAPEL_HILL_ROUTE","DAVES_ROUTE","DOWNTOWN_ROUTE","NORTHEAST_ROUTE","SOUTH_ROUTE","WEEKEND_ROUTE","WEST_ROUTE"};

    DBActivity(Context context) {
        super(context, Database_Name, null, DATABASE_VERSION);
        this.context = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        try {
            db.execSQL(create_tab_bus_table);
            insert_data(db,"BUS_TABLE","BUS_TABLE.txt");

            db.execSQL(total_data);
            insert_data(db,"TOTAL_DATA","TOTAL_DATA.txt");

            for(int i=0;i<routes.length;i++)
            {
                Log.e("create table query",routes[i]);
                db.execSQL("CREATE TABLE IF NOT EXISTS "+routes[i]+" ( ROUTE_NO INTEGER PRIMARY KEY NOT NULL , LOCATION_NAME TEXT  NOT NULL , LAT REAL NOT NULL , LONG REAL NOT NULL ,ID INTEGER NOT NULL, TIME REAL NOT NULL)");
                insert_data(db, routes[i],routes[i]+".txt");
               Log.e("table data inserted" ,routes[i]);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insert_data(SQLiteDatabase db, String table_name, String txt_file ) {

        try {
            // SQLiteDatabase sqllitedb =dbActivity.getWritableDatabase();
            BufferedReader br = new BufferedReader(new InputStreamReader(context.getAssets().open(txt_file)));
            // storing the value of length of the first line of maze
            String curr_line =br.readLine();
            String column_data[] = curr_line.split(",");
            while ((curr_line = br.readLine()) != null) {
                String row_data[] = curr_line.split(",");
                ContentValues values = new ContentValues();
                for (int i=0;i<column_data.length;i++)
                {
                    values.put(column_data[i], row_data[i]);
                }
                long id = db.insert(table_name, null, values);
                if (id < 0) {
                    Toast.makeText(context, "messed upsomewhere in "+table_name, Toast.LENGTH_LONG).show();
                    Log.e("id","messed up somewhere "+table_name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, " terrible exception", Toast.LENGTH_LONG).show();
        }
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        try {
            db.execSQL("DROP TABLE IF EXISTS BUS_TABLE ");
            db.execSQL("DROP TABLE IF EXISTS CHAPEL_ROUTE");
            db.execSQL("DROP TABLE IF EXISTS DAVES_ROUTE");
            db.execSQL("DROP TABLE IF EXISTS DOWNTOWN_ROUTE");
            db.execSQL("DROP TABLE IF EXISTS NORTHEAST_ROUTE");
            db.execSQL("DROP TABLE IF EXISTS SOUTH_ROUTE");
            db.execSQL("DROP TABLE IF EXISTS WEEKEND_ROUTE");
            db.execSQL("DROP TABLE IF EXISTS WEST_ROUTE");
            db.execSQL("DROP TABLE IF EXISTS TOTAL_DATA");


            // create new tables
            onCreate(db);

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

}
