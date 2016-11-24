package com.laichushu.book.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.laichushu.book.db.City_Id;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CITY__ID".
*/
public class City_IdDao extends AbstractDao<City_Id, Long> {

    public static final String TABLENAME = "CITY__ID";

    /**
     * Properties of entity City_Id.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Province = new Property(1, String.class, "province", false, "PROVINCE");
        public final static Property City = new Property(2, String.class, "city", false, "CITY");
        public final static Property ProCode = new Property(3, String.class, "proCode", false, "PRO_CODE");
        public final static Property CityCode = new Property(4, String.class, "cityCode", false, "CITY_CODE");
    };


    public City_IdDao(DaoConfig config) {
        super(config);
    }
    
    public City_IdDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CITY__ID\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"PROVINCE\" TEXT," + // 1: province
                "\"CITY\" TEXT," + // 2: city
                "\"PRO_CODE\" TEXT," + // 3: proCode
                "\"CITY_CODE\" TEXT);"); // 4: cityCode
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CITY__ID\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, City_Id entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String province = entity.getProvince();
        if (province != null) {
            stmt.bindString(2, province);
        }
 
        String city = entity.getCity();
        if (city != null) {
            stmt.bindString(3, city);
        }
 
        String proCode = entity.getProCode();
        if (proCode != null) {
            stmt.bindString(4, proCode);
        }
 
        String cityCode = entity.getCityCode();
        if (cityCode != null) {
            stmt.bindString(5, cityCode);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public City_Id readEntity(Cursor cursor, int offset) {
        City_Id entity = new City_Id( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // province
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // city
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // proCode
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4) // cityCode
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, City_Id entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setProvince(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCity(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setProCode(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setCityCode(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(City_Id entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(City_Id entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
