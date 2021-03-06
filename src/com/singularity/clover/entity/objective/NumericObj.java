package com.singularity.clover.entity.objective;

import java.util.NoSuchElementException;

import android.content.ContentValues;
import android.database.Cursor;

import com.singularity.clover.Global;
import com.singularity.clover.database.DBAdapter;
import com.singularity.clover.entity.EntityPool;
import com.singularity.clover.entity.Persisable;
import com.singularity.clover.entity.task.Task;

public class NumericObj extends AbstractObjective {
	
	/*命名契约：小写所有代词，单词之间通过_连接.如果为Persisable，则作为表名*/
	public final static String TAG = "numeric_obj";
	
	private int vMax = Global.INITIAL_NUMERIC_MAX;
	private int numericValue = Global.INITIAL_NUMERIC_VALUE;
	private String numericUnit = Global.INITIAL_NUMERIC_UNIT;
	
	private static final String NUMERICOBJ_TABLE = 
		"CREATE TABLE IF NOT EXISTS "
		+ "numeric_obj("
		+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
		+ "owener_id INTEGER NOT NULL,"
		+ "name TEXT,"
		+ "max_value INTEGER NOT NULL,"
		+ "numeric_value INTEGER NOT NULL,"
		+ "numeric_unit TEXT NOT NULL,"
		+ "x INTEGER,"
		+ "y INTEGER"
		+ ")";
	
	static{
		NumericObj prototype = new NumericObj("Prototype");
		EntityPool.instance().register(TAG,prototype);
		OBJFactory.register(TAG, prototype);
	}
	
	public NumericObj(Task parent) {
		super(parent);
	}
	
	private NumericObj(String prototype) {super(prototype);}

	public Persisable load(long dbId){	
		NumericObj numeric = new NumericObj("Prototype");
		Cursor cursor = DBAdapter.instance().retrieveById(TAG,dbId);
		if(cursor.moveToFirst()){
			numeric.id = dbId;
			numeric.owener_id = cursor.getLong(1);
			numeric.name = cursor.getString(2);
			numeric.vMax = cursor.getInt(3);
			numeric.numericValue = cursor.getInt(4);
			numeric.numericUnit = cursor.getString(5);
			numeric.x = cursor.getInt(6);
			numeric.y = cursor.getInt(7);
		}else{
			//throw new NoSuchElementException("No match row found");
			cursor.close();
			return null;
		}
		EntityPool.instance().add(numeric.id,numeric,TAG);
		cursor.close();
		return numeric;
	}
	
	public void set(int max,String unit){
		vMax = max;
		numericUnit = unit;
	}
	
	public void setValue(int value){
		if(value < 0){
			value = 0;
		}else if(value >= vMax){
			if(vMax != 0){
				value = vMax;
			}
		}else{
		}
		numericValue = value;
	}
		
	public int getValue(){
		return numericValue;
	}
	
	public int getMax(){
		return vMax;
	}
	
	public String getUnit(){
		return numericUnit;
	}

	@Override
	public void store() {	
		ContentValues content = new ContentValues();
		content.put("name", name);
		content.put("max_value", vMax);
		content.put("numeric_value", numericValue);
		content.put("numeric_unit", numericUnit);
		content.put("owener_id", owener_id);
		content.put("x",x);
		content.put("y",y);
		
		storeHelper(content);
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getTAG() {
		return TAG;
	}

	@Override
	public void processValidate() {
		if(vMax != 0){
			getParent().setProcess(100, (long) (numericValue/(float)vMax*100));
			if(numericValue >= vMax){
				bDone = true;
			}else{
				bDone = false;
			}
		}else{
			getParent().setProcess(100, 10);
			bDone = false;
		}
	}

	@Override
	public String getSchema() {
		return NUMERICOBJ_TABLE;
	}

	@Override
	public Persisable create() {
		// TODO Auto-generated method stub
		return null;
	}

}
