package com.eklanku.otuChat.ui.activities.payment.laporannew;

public class ItemHistoryPenarikan {

    private String tgl_penarikan, status_penarikan, jml_penarikan;

    public String getTgl_penarikan() {
        return tgl_penarikan;
    }

    public void setTgl_penarikan(String tgl_penarikan) {
        this.tgl_penarikan = tgl_penarikan;
    }

    public String getStatus_penarikan() {
        return status_penarikan;
    }

    public void setStatus_penarikan(String status_penarikan) {
        this.status_penarikan = status_penarikan;
    }

    public String getJml_penarikan() {
        return jml_penarikan;
    }

    public void setJml_penarikan(String jml_penarikan) {
        this.jml_penarikan = jml_penarikan;
    }

    public ItemHistoryPenarikan(String tgl_penarikan, String status_penarikan, String jml_penarikan) {
        this.tgl_penarikan = tgl_penarikan;
        this.status_penarikan = status_penarikan;
        this.jml_penarikan = jml_penarikan;
    }
}
