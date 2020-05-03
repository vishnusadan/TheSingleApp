package com.thesingleapp.thesingleapp.model;

public class Premium_model {

    private String id;
    private String noofdays;
    private String amount;
    private String month;
    private String year;

    public Premium_model(String id,String noofdays, String amount, String month, String year) {
        this.id = id;
        this.noofdays = noofdays;
        this.amount = amount;
        this.month = month;
        this.month = month;
        this.year = year;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNoofdays() {
        return noofdays;
    }

    public void setNoofdays(String noofdays) {
        this.noofdays = noofdays;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }


}
