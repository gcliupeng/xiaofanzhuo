package com.xiaolong.xiaofanzhuo.dataoperations;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Adapts the database to deal with the front end.
 * 
 * @author Andrei
 * 
 */
public class DatabaseAdapter {
	// Table name
	@SuppressWarnings("unused")
	private static final String CART_TABLE = "cart";
	// Table unique id
	public static final String COL_ID = "id";
	// Table food and quantity columns
	public static final String COL_FOOD_ID = "food";
	public static final String COL_QUANTITY = "quantity";
	public static final String COL_PRICE = "price";

	private Context context;
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;
	private String dbName = null;

	/**
	 * The adapter constructor.
	 * 
	 * @param context
	 */
	public DatabaseAdapter(Context context, String db_name) {
		this.context = context;
		this.dbName = db_name;
	}

	/**
	 * Creates the database helper and gets the database.
	 * 
	 * @return
	 * @throws SQLException
	 */
	public DatabaseAdapter open() throws SQLException {
		dbHelper = new DatabaseHelper(context, dbName);
		database = dbHelper.getWritableDatabase();
		return this;
	}

	/**
	 * Closes the database.
	 */
	public void close() {
		dbHelper.close();
	}

	/**
	 * 清空表中的数据
	 */
	public void clean() {
		database.execSQL("DROP TABLE IF EXISTS " + dbName);
		System.out.println("DROP TABLE: " + dbName);
	}

	/**
	 * Inserts the food and quantity.
	 * 
	 * @param food
	 *            The food.
	 * @param quantity
	 *            The quantity.
	 * @return
	 */
	public long insertFood(String food, String quantity, String price) {
		ContentValues initialValues = createFoodTableContentValues(food,
				quantity, price);
		return database.insert(dbName, null, initialValues);
	}

	/**
	 * Removes a user's details given an id.
	 * 
	 * @param rowId
	 *            Column id.
	 * @return
	 */
	public boolean deleteFood(long rowId) {
		return database.delete(dbName, COL_ID + "=" + rowId, null) > 0;
	}

	/**
	 * 
	 * @param food
	 * @return false: not exist in database, true: deleted
	 */
	@SuppressWarnings("deprecation")
	public boolean deleteFood(String food) {
		Cursor cur = fetchFood(food);
		if (cur == null) {
			System.out.println("Database query error");
			return false;
		} else {
			((Activity) context).startManagingCursor(cur);
			int rowId = cur.getInt(cur.getColumnIndex("id"));
			if (!deleteFood(rowId))
				System.out.println("Database delete error");
			((Activity) context).stopManagingCursor(cur);
			cur.close();
			return true;
		}
	}

	/**
	 * 
	 * @param rowId
	 * @param food
	 * @param quantity
	 * @return
	 */
	public boolean updateFoodTable(long rowId, String food, String quantity,
			String price) {
		ContentValues updateValues = createFoodTableContentValues(food,
				quantity, price);
		return database
				.update(dbName, updateValues, COL_ID + "=" + rowId, null) > 0;
	}

	/**
	 * 
	 * @param food
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String fetchFoodQuantity(String food) {
		String ret = "";
		Cursor cur = fetchFood(food);
		if (cur == null) {
			System.out.println("Database query error");
		} else {
			((Activity) context).startManagingCursor(cur);
			ret = cur.getString(cur.getColumnIndex("quantity"));
			((Activity) context).stopManagingCursor(cur);
			cur.close();
		}
		return ret;

	}

	/**
	 * 
	 * @param food
	 * @param symbol
	 *            true +1 false -1
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public boolean updateFoodTable(String food, String price, boolean symbol) {

		boolean ret = false;

		// check exist
		Cursor foodCur = fetchFood(food);
		if (foodCur == null) {
			System.out.println("Database query error");
		} else {
			((Activity) context).startManagingCursor(foodCur);

			if (foodCur.getCount() > 0) {
				System.out.println("The food is already exist in cart!");
				System.out.println("点击前Database中的数据："
						+ foodCur.getInt(foodCur.getColumnIndex("id")) + " | "
						+ foodCur.getString(foodCur.getColumnIndex("food"))
						+ " | "
						+ foodCur.getString(foodCur.getColumnIndex("quantity"))
						+ " | "
						+ foodCur.getString(foodCur.getColumnIndex("price")));

				int count = Integer.valueOf(foodCur.getString(foodCur
						.getColumnIndex("quantity"))) + (symbol ? 1 : -1);
				if (price == "")
					price = foodCur.getString(foodCur.getColumnIndex("price"));

				// delete
				if (count <= 0) {
					System.out
							.println("The food is already removed from cart!");
					return deleteFood(foodCur.getInt(foodCur
							.getColumnIndex("id")));
				}

				// update
				ContentValues updateValues = createFoodTableContentValues(food,
						String.valueOf(count), price);
				ret = database.update(dbName, updateValues, COL_ID + "="
						+ foodCur.getInt(foodCur.getColumnIndex("id")), null) > 0;
				((Activity) context).stopManagingCursor(foodCur);
				foodCur.close();
				return ret;
			}
		}

		// Create the new food
		long id = insertFood(food, "1", price);
		if (id > 0) {
			ret = true;
			System.out.println("The food is already inserted to cart!");
		} else {
			System.out.println("Failed to inserted to cart!");
		}

		return ret;
	}

	/**
	 * Retrieves the details of all the users stored in the login table.
	 * 
	 * @return
	 */
	public Cursor fetchAllFoods() {
		database.execSQL("create table if not exists "
				+ dbName
				+ " (id integer primary key autoincrement, food text not null, quantity text not null, price text not null)");
		return database.query(dbName, new String[] { COL_ID, COL_FOOD_ID,
				COL_QUANTITY, COL_PRICE }, null, null, null, null, null);
	}

	/**
	 * Retrieves the details of a specific user, given a food and quantity.
	 * 
	 * @param food
	 * @param quantity
	 * @return
	 */
	public Cursor fetchFood(String food, String quantity) {
		Cursor myCursor = database.query(dbName, new String[] { COL_ID,
				COL_FOOD_ID, COL_QUANTITY, COL_PRICE }, COL_FOOD_ID + "='"
				+ food + "' AND " + COL_QUANTITY + "='" + quantity + "'", null,
				null, null, null);

		if (myCursor != null) {
			myCursor.moveToFirst();
		}
		return myCursor;
	}

	public Cursor fetchFood(String food) {
		Cursor myCursor = database.query(dbName, new String[] { COL_ID,
				COL_FOOD_ID, COL_QUANTITY, COL_PRICE }, COL_FOOD_ID + "='"
				+ food + "'", null, null, null, null);

		if (myCursor != null) {
			myCursor.moveToFirst();
		}
		return myCursor;
	}

	/**
	 * Returns the table details given a row id.
	 * 
	 * @param rowId
	 *            The table row id.
	 * @return
	 * @throws SQLException
	 */
	public Cursor fetchFoodById(long rowId) throws SQLException {
		Cursor myCursor = database.query(dbName, new String[] { COL_ID,
				COL_FOOD_ID, COL_QUANTITY, COL_PRICE }, COL_ID + "=" + rowId,
				null, null, null, null);
		if (myCursor != null) {
			myCursor.moveToFirst();
		}
		return myCursor;
	}

	/**
	 * Stores the food and quantity upon creation of new login details.
	 * 
	 * @param food
	 *            The user name.
	 * @param quantity
	 *            The quantity.
	 * @return The entered values.
	 */
	private ContentValues createFoodTableContentValues(String food,
			String quantity, String price) {
		ContentValues values = new ContentValues();
		values.put(COL_FOOD_ID, food);
		values.put(COL_QUANTITY, quantity);
		values.put(COL_PRICE, price);
		return values;
	}
}