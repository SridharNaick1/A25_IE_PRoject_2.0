package com.example.bottomnavigation;

import java.util.Date;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface RecordDao {

    @Query("SELECT * FROM record")
    List<Record> getAll();

    @Query("SELECT * FROM record WHERE record_date LIKE :recordDate")
    Record findByRecordDate(String recordDate);

    @Query("SELECT * FROM record WHERE type LIKE :type")
    Record findByType(String type);

    @Query("SELECT * FROM record WHERE carbon_Name LIKE :carbonName")
    Record findByAppliances(String carbonName);

    @Insert
    void insertAll(Record... records);

    @Insert
    long insert(Record record);

    @Delete
    void delete(Record record);

    @Update(onConflict = REPLACE)
    public void updateRecords(Record... records);


    @Query("DELETE FROM record")
    void deleteAll();
}
