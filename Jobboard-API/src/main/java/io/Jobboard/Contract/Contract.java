package io.Jobboard.Contract;

import java.time.LocalDateTime;

public class Contract {

    private int jobID;
    private String contractor;
    private int days;
    private String desc;

    public Contract(int jobID, String desc, String contractor, int days){

        this.jobID = jobID;
        this.contractor = contractor;
        this.days = days;
        this.desc = desc;

    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public void setContractor(String contractor) {
        this.contractor = contractor;
    }

    public String getContractor() {
        return contractor;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public final int getJobID() {
        return jobID;
    }

    public final void setJobID(int jobID) {
        this.jobID = jobID;
    }


}
