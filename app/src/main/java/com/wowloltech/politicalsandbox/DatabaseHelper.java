package com.wowloltech.politicalsandbox;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String DB_NAME = "testmap.db";
    private static String DB_PATH = "";
    private static final int DB_VERSION = 1;
    private static final String LOG_TAG = "polsandbox";

    private SQLiteDatabase mDataBase;
    private final Context mContext;
    private boolean mNeedUpdate = false;

    public SQLiteDatabase getDb() {
        return mDataBase;
    }

    public DatabaseHelper(Context context, String DB_NAME) {
        super(context, DB_NAME, null, DB_VERSION);
        DatabaseHelper.DB_NAME = DB_NAME;
        DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        this.mContext = context;

        copyDataBase();

        this.getReadableDatabase();
    }

    public void updateDataBase() throws IOException {
        if (mNeedUpdate) {
            File dbFile = new File(DB_PATH + DB_NAME);
            if (dbFile.exists())
                dbFile.delete();

            copyDataBase();

            mNeedUpdate = false;
        }
    }

    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    private void copyDataBase() {
        if (!checkDataBase()) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDBFile();
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    private void copyDBFile() throws IOException {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        //InputStream mInput = mContext.getResources().openRawResource(R.raw.info);
        OutputStream mOutput = new FileOutputStream(DB_PATH + DB_NAME);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0)
            mOutput.write(mBuffer, 0, mLength);
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    public boolean openDataBase() throws SQLException {
        mDataBase = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return mDataBase != null;
    }

    @Override
    public synchronized void close() {
        if (mDataBase != null)
            mDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            mNeedUpdate = true;
    }

    public void readDatabase(Game game) {
        try {
            updateDataBase();
        } catch (IOException ioe) {

        }
        try {
            mDataBase = getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }
        Log.d(LOG_TAG, mDataBase.toString());
        Cursor c;
        c = mDataBase.query("game", null, null, null, null, null, null);
        c.moveToFirst();
        int widthColIndex = c.getColumnIndex("map_width");
        int heightColIndex = c.getColumnIndex("map_height");
        int currentPlayerColIndex = c.getColumnIndex("current_player");
        int idCounterColIndex = c.getColumnIndex("id_counter");
        int turnCounterColIndex = c.getColumnIndex("turn_counter");


        game.setIdCounterFromDb(c.getInt(idCounterColIndex));
        game.setTurnCounterFromDb(c.getInt(turnCounterColIndex));
        Map.setWidth(c.getInt(widthColIndex));
        Map.setHeight(c.getInt(heightColIndex));
        Map.setProvinces(new Province[Map.getWidth()][Map.getHeight()]);
        int currentPlayerId = c.getInt(currentPlayerColIndex);
        c.close();
        c = mDataBase.query("players", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int moneyColIndex = c.getColumnIndex("money");
            int recruitsColIndex = c.getColumnIndex("recruits");
            int isHumanColIndex = c.getColumnIndex("is_human");
            int idColIndex = c.getColumnIndex("_id");
            do {
                int id = c.getInt(idColIndex) - 1;
                int recruits = c.getInt(recruitsColIndex);
                double money = c.getDouble(moneyColIndex);
                int isHuman = c.getInt(isHumanColIndex);
                game.getPlayers().add(game.addPlayerFromDb(id, money, recruits, isHuman));
            } while (c.moveToNext());
        }
        game.setCurrentPlayerFromDb((HumanPlayer) game.findPlayerByID(currentPlayerId));
        c.close();
        Log.d(LOG_TAG, game.getPlayers().toString());
        c = mDataBase.query("map", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("_id");
            int incomeColIndex = c.getColumnIndex("incomeLevel");
            int ownerColIndex = c.getColumnIndex("owner");
            do {
                int id = c.getInt(idColIndex) - 1;
                double income = c.getInt(incomeColIndex);
                int ownerID = c.getInt(ownerColIndex);

                Log.d(LOG_TAG, "_id = " + id + ", income = " + income + ", owner = " + ownerID);
                Map.getProvinces()[id / Map.getWidth()][id % Map.getWidth()] = new Province(id % Map.getWidth(), id / Map.getWidth(), id, (int) income * 20, income, game.findPlayerByID(ownerID));
                game.findPlayerByID(ownerID).getProvinces().add(Map.getProvinces()[id / Map.getWidth()][id % Map.getWidth()]);
            } while (c.moveToNext());
        }
        c.close();
        c = mDataBase.query("armies", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int strengthColIndex = c.getColumnIndex("strength");
            int idLocationColIndex = c.getColumnIndex("location");
            int speedColIndex = c.getColumnIndex("speed");
            int idColIndex = c.getColumnIndex("_id");
            int ownerIdColIndex = c.getColumnIndex("owner");
            Cursor localC;
            do {
                int strength = c.getInt(strengthColIndex);
                int locationId = c.getInt(idLocationColIndex);
                int speed = c.getInt(speedColIndex);
                int id = c.getInt(idColIndex);
                int ownerId = c.getInt(ownerIdColIndex);
                Player p = game.findPlayerByID(ownerId);
                localC = mDataBase.query("map", null, "_id = ?", new String[]{String.valueOf(locationId + 1)}, null, null, null);
                localC.moveToFirst();
                Province province = Map.findProvinceByID(localC.getInt(localC.getColumnIndex("_id")) - 1);
                p.getArmies().add(new Army(strength, province, p, id, speed));
            } while (c.moveToNext());
            localC.close();
        }
        c.close();

    }
}