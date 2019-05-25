package com.wowloltech.politicalsandbox;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.wowloltech.politicalsandbox.models.Army;
import com.wowloltech.politicalsandbox.models.Map;
import com.wowloltech.politicalsandbox.models.Player;
import com.wowloltech.politicalsandbox.models.Province;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

public class DatabaseHelper extends SQLiteOpenHelper {
    private String MAP_NAME = "testmap.db";
    private String SAVE_NAME = "testsave.db";
    public static String DB_PATH = "";
    private static final int DB_VERSION = 1;
    private static final String LOG_TAG = "polsandbox";

    private SQLiteDatabase mDataBase;
    private final Context mContext;
    private boolean mNeedUpdate = false;

    public SQLiteDatabase getDb() {
        return mDataBase;
    }

    DatabaseHelper(Context context, String MAP_NAME, String SAVE_NAME) {
        super(context, SAVE_NAME, null, DB_VERSION);
        this.MAP_NAME = MAP_NAME;
        this.SAVE_NAME = SAVE_NAME;
        DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        this.mContext = context;

        copyDataBase();

        this.getReadableDatabase();
    }

    private void updateDataBase() {
        if (mNeedUpdate) {
            File dbFile = new File(DB_PATH + SAVE_NAME);
            if (dbFile.exists())
                if (dbFile.delete())
                    copyDataBase();
            mNeedUpdate = false;
        }
    }

    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + SAVE_NAME);
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

    public void exportDatabase(String databaseName, String newSaveName, boolean copyToSdcard) {
        try {
            getDb().close();
            File sd = Environment.getExternalStorageDirectory();
            String data = mContext.getApplicationInfo().dataDir + "/databases/";

            if (sd.canWrite()) {
                File dir = new File(sd.getPath() + "/Political Sandbox saves");
                dir.mkdir();
                File currentDB = new File(data + databaseName);
                File backupDB = new File(dir, newSaveName);
                File newDb = null;
                if (!(data + databaseName).equals(data + newSaveName))
                    newDb = new File(data + newSaveName);

                if (currentDB.exists()) {
                    FileChannel src;
                    FileChannel dst;
                    if (copyToSdcard) {
                        src = new FileInputStream(currentDB).getChannel();
                        dst = new FileOutputStream(backupDB).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                    }
                    if (newDb != null) {
                        src = new FileInputStream(currentDB).getChannel();
                        dst = new FileOutputStream(newDb).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void copyDBFile() throws IOException {
        InputStream mInput = mContext.getAssets().open(MAP_NAME);
        //InputStream mInput = mContext.getResources().openRawResource(R.raw.info);
        OutputStream mOutput = new FileOutputStream(DB_PATH + SAVE_NAME);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0)
            mOutput.write(mBuffer, 0, mLength);
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    public boolean openDataBase() throws SQLException {
        mDataBase = SQLiteDatabase.openDatabase(DB_PATH + SAVE_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
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
        updateDataBase();
        mDataBase = getWritableDatabase();
        Log.d(LOG_TAG, mDataBase.toString());
        Cursor c;
        c = mDataBase.query("game", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int widthColIndex = c.getColumnIndex("map_width");
            int heightColIndex = c.getColumnIndex("map_height");
            int idCounterColIndex = c.getColumnIndex("id_counter");
            int turnCounterColIndex = c.getColumnIndex("turn_counter");


            game.setIdCounter(c.getInt(idCounterColIndex));
            game.setTurnCounterFromDb(c.getInt(turnCounterColIndex));
            Map.setWidth(c.getInt(widthColIndex));
            Map.setHeight(c.getInt(heightColIndex));
            Map.setProvinces(new Province[Map.getHeight()][Map.getWidth()]);
//            Log.d("myLog", "" + Map.getWidth() + " " + Map.getHeight() + " " + Map.getProvinces().length + " " + Map.getProvinces()[0].length);
            c.close();
        }
        c = mDataBase.query("players", null, null, null, null, null, null);
//        int player_id = game.getActivity().getSharedPreferences("save", Context.MODE_PRIVATE).getInt("player_id", -1);
        if (c.moveToFirst()) {
            int moneyColIndex = c.getColumnIndex("money");
            int recruitsColIndex = c.getColumnIndex("recruits");
            int idColIndex = c.getColumnIndex("_id");
            int colorColIndex = c.getColumnIndex("color");
            int nameColIndex = c.getColumnIndex("name");
            do {
                int id = c.getInt(idColIndex) - 1;
                int recruits = c.getInt(recruitsColIndex);
                String name = c.getString(nameColIndex);
                double money = c.getDouble(moneyColIndex);
                int color = c.getInt(colorColIndex);
                int isHuman = 0;
//                if (player_id == -1) {
//                    isHuman = c.getInt(isHumanColIndex);
//                    game.getPlayers().add(game.addPlayerFromDb(id, money, recruits, isHuman, color, name));
//                }
                game.getPlayers().add(game.addPlayerFromDb(id, money, recruits, color, name));
            } while (c.moveToNext());
        }
        c.close();
        Log.d(LOG_TAG, game.getPlayers().toString());
        c = mDataBase.query("map", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("_id");
            int incomeColIndex = c.getColumnIndex("incomeLevel");
            int ownerColIndex = c.getColumnIndex("owner");
            int typeColIndex = c.getColumnIndex("type");
            do {
                Province.Type type = Province.Type.valueOf(c.getInt(typeColIndex));
                if (type != Province.Type.VOID) {
                    int id = c.getInt(idColIndex) - 1;
                    double income = c.getInt(incomeColIndex);
                    int ownerID = c.getInt(ownerColIndex);
                    Map.getProvinces()[id / Map.getWidth()][id % Map.getWidth()]
                            = new Province(id % Map.getWidth(), id / Map.getWidth(), id, (int) income * 30, income, game.findPlayerByID(ownerID), type, game);
                    game.findPlayerByID(ownerID).getProvinces().add(Map.getProvinces()[id / Map.getWidth()][id % Map.getWidth()]);
                } else {
                    int id = c.getInt(idColIndex) - 1;
                    Map.getProvinces()[id / Map.getWidth()][id % Map.getWidth()] = new Province(id % Map.getWidth(), id / Map.getWidth(), id, game);
                }
            } while (c.moveToNext());
            for (int i = 0; i < Map.getProvinces().length; i++)
                for (int j = 0; j < Map.getProvinces()[0].length; j++)
                    if (Map.getProvinces()[i][j].getType() != Province.Type.VOID) {
                        for (Province p : Map.getNeighbours(Map.getProvinces()[i][j]))
                            Map.getProvinces()[i][j].getNeighbours().add(p);
                    }
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
                Army army = new Army(strength, province, p, id, speed, game);
                p.getArmies().add(army);
                assert province != null;
                province.getArmies().add(army);
            } while (c.moveToNext());
            localC.close();
        }
        c.close();
        try {
            c = mDataBase.query("bridges", null, null, null, null, null, null);
            if (c.moveToFirst()) do {
                int x1 = c.getInt(c.getColumnIndex("posx1"));
                int y1 = c.getInt(c.getColumnIndex("posy1"));
                int x2 = c.getInt(c.getColumnIndex("posx2"));
                int y2 = c.getInt(c.getColumnIndex("posy2"));
                Map.getProvinces()[y1][x1].getNeighbours().add(Map.getProvinces()[y2][x2]);
                Map.getProvinces()[y2][x2].getNeighbours().add(Map.getProvinces()[y1][x1]);
            } while (c.moveToNext());
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

