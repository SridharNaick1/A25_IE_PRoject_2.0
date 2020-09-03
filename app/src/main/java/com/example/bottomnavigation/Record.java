package com.example.bottomnavigation;

import java.util.Date;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Record {
    @PrimaryKey(autoGenerate = true)
    public int recordId;

    @ColumnInfo(name = "record_date")
    public String recordDate;

    @ColumnInfo(name = "type")
    public String type;

    @ColumnInfo(name = "carbon_Name")
    public String carbonName;

    @ColumnInfo(name = "carbon")
    public Double carbon;

    public Record( String recordDate, String type, String carbonName, Double carbon){
        this.recordDate = recordDate;
        this.type = type;
        this.carbonName = carbonName;
        this.carbon = carbon;
    }

    public int getRecordId(){
        return recordId;
    }

    public String getRecordDate(){
        return recordDate;
    }

    public void setRecordDate(String recordDate){
        this.recordDate = recordDate;
    }

    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getCarbonName(){
        return carbonName;
    }

    public void setCarbonName(String carbonName){
        this.carbonName = carbonName;
    }

    public double getCarbon(){
        return carbon;
    }

    public void setCarbon(Double carbon){
        this.carbon = carbon;
    }
}

