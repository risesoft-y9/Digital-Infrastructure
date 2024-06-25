package net.risesoft.model;

public class StatusResult extends Result {

    private String examine_content;

    private String examine_status;

    private String examine_remarks;

    public String getExamine_content() {
        return examine_content;
    }

    public void setExamine_content(String examine_content) {
        this.examine_content = examine_content;
    }

    public String getExamine_status() {
        return examine_status;
    }

    public void setExamine_status(String examine_status) {
        this.examine_status = examine_status;
    }

    public String getExamine_remarks() {
        return examine_remarks;
    }

    public void setExamine_remarks(String examine_remarks) {
        this.examine_remarks = examine_remarks;
    }
}
