package com.example.bottomnavigation;

import java.util.Date;


public class UserCarbonEmission {
    private String userEmail;
    private String recordDate;
    private String type;
    private String carbonName;
    private Double carbon;

    public UserCarbonEmission(){

    }

    public UserCarbonEmission(String userEmail, String recordDate, String type, String carbonName, Double carbon) {
        this.userEmail = userEmail;
        this.recordDate = recordDate;
        this.type = type;
        this.carbonName = carbonName;
        this.carbon = carbon;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }


    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCarbonName() {
        return carbonName;
    }

    public void setCarbonName(String carbonName) {
        this.carbonName = carbonName;
    }

    public Double getCarbon() {
        return carbon;
    }

    public void setCarbon(Double carbon) {
        this.carbon = carbon;
    }
}
