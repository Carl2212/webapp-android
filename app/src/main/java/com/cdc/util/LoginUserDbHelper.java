package com.cdc.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cdc.model.UserEntity;

public class LoginUserDbHelper {
	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;
	private static final String tableName = "login_user";

	class DatabaseHelper extends SQLiteOpenHelper {

		private static final String DB_NAME = "cdc_moa_data.db"; // 数据库名称
		private static final int version = 1; // 数据库版本

		public DatabaseHelper(Context context) {
			super(context, DB_NAME, null, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			
			String sql = "create table " + tableName + "(user_id varchar(200) not null , user_name varchar(100) not null, last_update_time varchar(30) not null);";
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}

	}

	public LoginUserDbHelper(Context ctx) {
		dbHelper = new DatabaseHelper(ctx);
		db = dbHelper.getWritableDatabase();
		Log.d(DatabaseHelper.class.getName(), "打开数据库 cdc_moa_data.db");
	}

	public void close() {
		db.close();
		dbHelper.close();
	}

	/**
	 * 
	 * @param entity
	 * @return
	 */

	public long insert(UserEntity entity) {
		Log.d(DatabaseHelper.class.getName(), "插入表 login_user, "+ entity.toString());
		ContentValues values = new ContentValues();
		values.put("user_id", entity.getUserid());
		values.put("user_name", entity.getUsername());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String last_update_time = sdf.format(new Date());
		values.put("last_update_time", last_update_time);
		if(this.findById(entity.getUserid()) == null){
			long result = db.insert(tableName, null, values);
			return result;
		}else{
			return -100l;
		}
		

	}

	/**
	 * 依据用户id删除数据
	 * @param userId 用户ID
	 * @return
	 */
	public boolean delete(String userId) {
		Log.d(DatabaseHelper.class.getName(), "删除 login_user 数据, id="+ userId);
		String deleteCondition = "user_id=?";
		String[] deleteArgs = {userId};
		return db.delete(tableName, deleteCondition, deleteArgs) > 0;

	}
	
	/**
	 * 删除所有用户
	 * @param userId 用户ID
	 * @return
	 */
	public boolean deleteAll() {
		Log.d(DatabaseHelper.class.getName(), "删除所有数据");
		db.execSQL("delete from "+ tableName +"");
		return true;

	}
	
	/**
	 * 依据用户id查找数据
	 * @param userId 用户ID
	 * @return
	 */
	public UserEntity findById(String userId) {
		Cursor c = db.rawQuery("select * from "+ tableName +" where user_id=?",new String[]{userId});
		if(c.moveToFirst()) {
			UserEntity entity = new UserEntity();
			entity.setUserid(c.getString(c.getColumnIndex("user_id")));
			entity.setUsername(c.getString(c.getColumnIndex("user_name")));
			entity.setLastUpdateTime(c.getString(c.getColumnIndex("last_update_time")));
			Log.d(DatabaseHelper.class.getName(), "findById, login_user,id= " + userId + ",result="+ entity.toString());
			return entity;
		}
		return null;

	}

	
	public List<UserEntity> findAll() {
		Cursor c = db.query(tableName,null,null,null,null,null,null);//查询并获得游标
		List<UserEntity> userList = new ArrayList<UserEntity>();
		if(c.moveToFirst()){//判断游标是否为空
		    for(int i=0;i<c.getCount();i++){
		    	UserEntity entity = new UserEntity();
		        c.move(i);//移动到指定记录
		        entity.setUserid(c.getString(c.getColumnIndex("user_id")));
				entity.setUsername(c.getString(c.getColumnIndex("user_name")));
				entity.setLastUpdateTime(c.getString(c.getColumnIndex("last_update_time")));
				Log.d(DatabaseHelper.class.getName(), "查询所有,第" + i + "条数据是："+ entity.toString());
				userList.add(entity);
		    }
		}
		Log.d(DatabaseHelper.class.getName(), "查询所有 result:"+ userList.toString());
		return userList;
	}
	
	public void refreshLastUpdateTime(String last_update_time){
		db.execSQL("update "+ tableName +" set last_update_time='" + last_update_time+"'");
	}

	

}
