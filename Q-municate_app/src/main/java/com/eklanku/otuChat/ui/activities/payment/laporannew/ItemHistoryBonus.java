package com.eklanku.otuChat.ui.activities.payment.laporannew;

public class ItemHistoryBonus {

    private String tgl_perolehan;
    private String jenis_bonus;

    public String getTgl_perolehan() {
        return tgl_perolehan;
    }

    public void setTgl_perolehan(String tgl_perolehan) {
        this.tgl_perolehan = tgl_perolehan;
    }

    public String getJenis_bonus() {
        return jenis_bonus;
    }

    public void setJenis_bonus(String jenis_bonus) {
        this.jenis_bonus = jenis_bonus;
    }

    public String getStatus_bonus() {
        return status_bonus;
    }

    public void setStatus_bonus(String status_bonus) {
        this.status_bonus = status_bonus;
    }

    public String getJml_Bonus() {
        return jml_bonus;
    }

    public void setJml_Bonus(String jml_status) {
        this.jml_bonus = jml_status;
    }

    private String status_bonus;
    private String jml_bonus;

    public ItemHistoryBonus(String tgl_perolehan, String jenis_bonus, String status_bonus, String jml_bonus) {
        this.tgl_perolehan = tgl_perolehan;
        this.jenis_bonus = jenis_bonus;
        this.status_bonus = status_bonus;
        this.jml_bonus = jml_bonus;
    }


}
