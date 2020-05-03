package com.thesingleapp.thesingleapp.model;

public class Orders_model {


    private String referenceno;
    private String noofdays;
    private String amount;
    private String dayofpayment;
    private String timeofpayment;
    private String dateofvalid;
    private String timeofvalid;
    private String paymentstatus;


    public Orders_model(String referenceno, String noofdays,String amount, String dayofpayment, String timeofpayment,String dateofvalid,String timeofvalid,String paymentstatus) {
        this.referenceno = referenceno;
        this.noofdays = noofdays;
        this.amount = amount;
        this.dayofpayment = dayofpayment;
        this.timeofpayment = timeofpayment;
        this.dateofvalid = dateofvalid;
        this.paymentstatus = paymentstatus;
        this.timeofvalid = timeofvalid;

    }

    public String getReferenceno() {
        return referenceno;
    }

    public void setReferenceno(String referenceno) {
        this.referenceno = referenceno;
    }

    public String getNoofdays() {
        return noofdays;
    }

    public void setNoofdays(String noofdays) {
        this.noofdays = noofdays;
    }

    public String getDayofpayment() {
        return dayofpayment;
    }

    public void setDayofpayment(String dayofpayment) {
        this.dayofpayment = dayofpayment;
    }


    public String getPaymentstatus() {
        return paymentstatus;
    }

    public void setPaymentstatus(String paymentstatus) {
        this.paymentstatus = paymentstatus;
    }
    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTimeofpayment() {
        return timeofpayment;
    }

    public void setTimeofpayment(String timeofpayment) {
        this.timeofpayment = timeofpayment;
    }

    public String getDateofvalid() {
        return dateofvalid;
    }

    public void setDateofvalid(String dateofvalid) {
        this.dateofvalid = dateofvalid;
    }

    public String getTimeofvalid() {
        return timeofvalid;
    }

    public void setTimeofvalid(String timeofvalid) {
        this.timeofvalid = timeofvalid;
    }
}
