package com.sofacity.laichushu.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.sofacity.laichushu.db.Cache_Json;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CACHE__JSON".
*/
public class Cache_JsonDao extends AbstractDao<Cache_Json, Long> {

    public static final String TABLENAME = "CACHE__JSON";

    /**
     * Properties of entity Cache_Json.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Inter = new Property(1, String.class, "inter", false, "INTER");
        public final static Property Json = new Property(2, String.class, "json", false, "JSON");
    };


    public Cache_JsonDao(DaoConfig config) {
        super(config);
    }
    
    public Cache_JsonDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CACHE__JSON\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"INTER\" TEXT," + // 1: inter
                "\"JSON\" TEXT);"); // 2: json
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CACHE__JSON\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Cache_Json entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String inter = entity.getInter();
        if (inter != null) {
            stmt.bindString(2, inter);
        }
 
        String json = entity.getJson();
        if (json != null) {
            stmt.bindString(3, json);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Cache_Json readEntity(Cursor cursor, int offset) {
        Cache_Json entity = new Cache_Json( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // inter
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2) // json
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Cache_Json entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setInter(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setJson(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Cache_Json entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Cache_Json entity) {
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
