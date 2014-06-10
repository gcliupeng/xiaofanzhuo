package com.xiaolong.xiaofanzhuo.dataoperations;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This class creates the relation with the SQLite Database Helper through which
 * queries can be SQL called.
 * 
 * @author Andrei
 * 
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	// The database name and version
	@SuppressWarnings("unused")
	private static final String DB_NAME = "cart";
	// The database user table
	private String mDbName = null;
	private static final int DB_VERSION = 1;

	public String getDatabaseName() {
		return this.mDbName;
	}

	private String getDatabaseTableSQL() {
		return "create table if not exists "
				+ getDatabaseName()
				+ " (id integer primary key autoincrement, food text not null, quantity text not null, price text not null)";
	}

	/**
	 * Database Helper constructor.
	 * 
	 * @param context
	 */
	public DatabaseHelper(Context context, String db_name) {
		super(context, db_name, null, DB_VERSION);
		this.mDbName = db_name;
	}

	/**
	 * Creates the database tables.
	 */
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(getDatabaseTableSQL());
	}

	/**
	 * Handles the table version and the drop of a table.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(DatabaseHelper.class.getName(), "Upgrading databse from version"
				+ oldVersion + "to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS cart");
		onCreate(database);
	}
}