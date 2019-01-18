package com.eklanku.otuChat.ui.activities.payment.laporannew;

/**
 * Created by AHMAD AYIK RIFAI on 10/3/2017.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransBpjs;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransESaldo_product;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransETool_product;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransEtool;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransMultiFinance;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransPaketData;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransPaketTelp;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransPdam;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransPln;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransPulsa;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransSMS;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransTagihan;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransTelkom;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransTv;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransVouchergame_product;
import com.eklanku.otuChat.ui.activities.payment.transaksi.TransWi;


import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;

public class HistoryTrxAdapter extends RecyclerView.Adapter<HistoryTrxAdapter.MyViewHolder> {
    private Context context;
    private List<ItemHistoryTrx> cartList;
    private String TAG = HistoryTrxAdapter.class.getSimpleName();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvKode, tvTanggal, tvStatus, tvNominal, tvJenis, tvDot, tvInvoice, tvTujuan;
        private View viewTrx;

        public MyViewHolder(View view) {
            super(view);
            viewTrx = view;
            tvKode = view.findViewById(R.id.tvKode);
            tvTanggal = view.findViewById(R.id.tvTanggal);
            tvStatus = view.findViewById(R.id.tvstatus);
            tvNominal = view.findViewById(R.id.tvNominal);
            tvJenis = view.findViewById(R.id.tvJnsTransaksi);
            //tvDot = view.findViewById(R.id.dot);
            tvInvoice = view.findViewById(R.id.tvInvoice);
            tvTujuan = view.findViewById(R.id.tvtujuan);

        }
    }

    public HistoryTrxAdapter(Context context, List<ItemHistoryTrx> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ItemHistoryTrx itemProduct = cartList.get(position);

        holder.tvKode.setText(itemProduct.getInvoice());
        holder.tvTanggal.setText(formatTgl(itemProduct.getTgl()));
        holder.tvNominal.setText(formatRupiah(Double.parseDouble(itemProduct.getHarga())));
        holder.tvJenis.setText(itemProduct.getType_product());
        holder.tvInvoice.setText(itemProduct.getInvoice());
        holder.tvTujuan.setText(itemProduct.getTujuan());

        if (itemProduct.getVstatus().equalsIgnoreCase("Gagal")) {
            holder.tvStatus.setTextColor(Color.RED);
        } else if (itemProduct.getVstatus().equalsIgnoreCase("Waiting")) {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.yellow_800));
        } else if (itemProduct.getVstatus().equalsIgnoreCase("Refund")) {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.grey_800));
        } else {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.colorTextOtuDark));
        }

        if (itemProduct.getVstatus().equalsIgnoreCase("Active")) {
            holder.tvStatus.setText("Sukses");
        } else {
            holder.tvStatus.setText(itemProduct.getVstatus());
        }

        holder.viewTrx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetail(itemProduct.getId_member(), itemProduct.getInvoice(), itemProduct.getTgl(), itemProduct.getVstatus(), itemProduct.getHarga(),
                        itemProduct.getTujuan(), itemProduct.getKeterangan(), itemProduct.getVsn(), itemProduct.getMbr_name(),
                        itemProduct.getTgl_sukses(), itemProduct.getProduct_kode(), itemProduct.getType_product(),
                        itemProduct.getProvider_name(), itemProduct.getProduct_name(), itemProduct.getTransaksi_id(),
                        itemProduct.getPtname(), itemProduct.getWaktu(), itemProduct.getStartdate(), itemProduct.getEnddate(),
                        itemProduct.getRef2(), itemProduct.getIdpelanggan1(), itemProduct.getCustomerid(),
                        itemProduct.getCustomername(), itemProduct.getSubscribername(),
                        itemProduct.getSubscribersegmentation(), itemProduct.getPowerconsumingcategory(),
                        itemProduct.getSwreferencenumber(), itemProduct.getBillercode(), itemProduct.getNoref1(), itemProduct.getNoref2(),
                        itemProduct.getCustomerphonenumber(), itemProduct.getLastpaidperiode(), itemProduct.getLastpaidduedate(),
                        itemProduct.getTenor(), itemProduct.getProductcategory(), itemProduct.getBillquantity(), itemProduct.getBillerrefnumber(),
                        itemProduct.getCarnumber(), itemProduct.getNominal(), itemProduct.getBiayaadmin(), itemProduct.getOdinstallmentamount(),
                        itemProduct.getOdpenaltyfee(), itemProduct.getBilleradminfee(), itemProduct.getItemmerktype(), itemProduct.getMinimumpayamount(),
                        itemProduct.getMiscfee(), itemProduct.getBranchname(), itemProduct.getInfoteks(), itemProduct.getServiceunitphone(),
                        itemProduct.getServiceunit(), itemProduct.getTarif_daya(), itemProduct.getWording(), itemProduct.getBl_th(), itemProduct.getAngsuran_ke(),
                        itemProduct.getAngsuran_pokok(), itemProduct.getJumlah_tagihan(), itemProduct.getTotal_tagihan(), itemProduct.getTerbilang(),
                        itemProduct.getHeader1(), itemProduct.getMiddle1(), itemProduct.getFooter1(), itemProduct.getFooter2(), itemProduct.getFooter3(), itemProduct.getFooter4());
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public void showDetail(String id_member, String invoice, String tgl, String vstatus, String harga,
                           String tujuan, String keterangan, String vsn, String mbr_name,
                           String tgl_sukses, String product_kode, String type_product,
                           String provider_name, String product_name, String transaksi_id,
                           String ptname, String waktu, String startdate, String enddate,
                           String ref2, String idpelanggan1, String customerid,
                           String customername, String subscribername,
                           String subscribersegmentation, String powerconsumingcategory,
                           String swreferencenumber, String billercode, String noref1, String noref2,
                           String customerphonenumber, String lastpaidperiode, String lastpaidduedate,
                           String tenor, String productcategory, String billquantity, String billerrefnumber,
                           String carnumber, String nominal, String biayaadmin, String odinstallmentamount,
                           String odpenaltyfee, String billeradminfee, String itemmerktype, String minimumpayamount,
                           String miscfee, String branchname, String infoteks, String serviceunitphone,
                           String serviceunit, String tarif_daya, String wording, String bl_th, String angsuran_ke,
                           String angsuran_pokok, String jumlah_tagihan, String total_tagihan, String terbilang,
                           String header1, String middle1, String footer1, String footer2, String footer3, String footer4) {

        final Dialog builder = new Dialog(context);
        builder.setContentView(R.layout.history_detail_trx);
        builder.setTitle("Jumlah");
        builder.setCancelable(false);

        final TextView tvProductType = builder.findViewById(R.id.tv_product_type);
        final TextView etTgl = builder.findViewById(R.id.et_tgl);
        final TextView etStatus = builder.findViewById(R.id.et_status);
        final TextView etNominal = builder.findViewById(R.id.et_harga);
        final TextView etInvoice = builder.findViewById(R.id.et_invoice);
        final TextView etTujuan = builder.findViewById(R.id.et_tujuan);
        final TextView etVsn = builder.findViewById(R.id.et_vsn);
        final TextView etKet = builder.findViewById(R.id.et_keterangan);
        final TextView tutup = builder.findViewById(R.id.tv_close);

        if (vstatus.equalsIgnoreCase("Active")) {
            etStatus.setText("Sukses");
        } else {
            etStatus.setText(vstatus);
        }

        if (vstatus.equalsIgnoreCase("Gagal")) {
            etStatus.setTextColor(Color.RED);
        } else if (vstatus.equalsIgnoreCase("Waiting")) {
            etStatus.setTextColor(context.getResources().getColor(R.color.yellow_800));
        } else if (vstatus.equalsIgnoreCase("Refund")) {
            etStatus.setTextColor(context.getResources().getColor(R.color.grey_800));
        } else {
            etStatus.setTextColor(context.getResources().getColor(R.color.colorTextOtuDark));
        }

        etTgl.setText(tgl);
        etNominal.setText(formatRupiah(Double.parseDouble(harga)));
        tvProductType.setText("Detail Transaksi " + type_product);
        etInvoice.setText(invoice);
        etTujuan.setText(tujuan);
        etVsn.setText(vsn);
        etKet.setText(keterangan);

        ImageView btnBuy = builder.findViewById(R.id.btn_buy);
        ImageView btnClose = builder.findViewById(R.id.btn_close);
        ImageView btnPrint = builder.findViewById(R.id.btn_print);
        ImageView btndownload = builder.findViewById(R.id.btndownload);

        ImageView btnCopyInv = builder.findViewById(R.id.btn_copy_inv);
        ImageView btnCopyTjn = builder.findViewById(R.id.btn_copy_tjn);

        btnCopyInv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Salin invoice", etInvoice.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Salin invoice", Toast.LENGTH_SHORT).show();
            }
        });

        btnCopyTjn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Salin tujuan", etTujuan.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Salin tujuan", Toast.LENGTH_SHORT).show();
            }
        });

        tutup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buy(type_product, harga, tujuan, provider_name);
            }
        });

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdf_tglcetak = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

                if (type_product.equals("PULSA") || type_product.equals("KUOTA") || type_product.contains("ETOOL") ||
                        type_product.equals("GAME") || type_product.equals("TELPONS") || type_product.equals("SMS") || type_product.equals("OJEK ONLINE")) {

                    String ket = "";
                    if (type_product.contains("ETOOL") || type_product.contains("TELPON") || type_product.contains("GAME")) {
                        ket = "Slip Pembelian " + provider_name;
                    } else if (type_product.contains("OJEK")) {
                        ket = "Slip Pembelian Saldo "+provider_name;
                    } else {
                        ket = "Slip Pembelian " + type_product.substring(0, 1).toUpperCase() + type_product.substring(1).toLowerCase() + " " + provider_name;
                    }

                    String voucher = "";
                    if (type_product.equals("KUOTA") || type_product.equals("TELPONS") || type_product.equals("GAME")) {
                        voucher = provider_name;
                    } else {
                        voucher = product_name;
                    }
                    printTransaksi(type_product, formatTgl(tgl), ket, voucher, tujuan, vsn, harga);
                } else {
                    Toast.makeText(context, "Coming soon...", Toast.LENGTH_SHORT).show();
                }

                /*else if (type_product.equals("BPJSKES")) {
                    printTransaksiBpjsKes(type_product, invoice, sdf_tglcetak.format(new Date()), header1, formatTgl(tgl), ref2, tujuan, customername, noref1, customerphonenumber,
                            billercode, billquantity, nominal, biayaadmin, total_tagihan, terbilang, footer1);

                }else if(type_product.equals("MULTY FINANCE")){
                    printTransaksiFinanceMAF(type_product, header1, sdf_tglcetak.format(new Date()), invoice, tgl, ref2, ptname, "", tujuan, customername,
                            angsuran_ke, lastpaidduedate, jumlah_tagihan, total_tagihan, terbilang, footer1);

                }else if(type_product.equals("PGN")){
                    printTransaksiPGN(type_product, header1, sdf_tglcetak.format(new Date()), invoice, tgl, "", customerid, customername, "",
                            "", "", "", "", "", "", "");
                }*/
            }
        });

        btndownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download(transaksi_id);
            }
        });

        builder.show();
        Window window = builder.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public String formatRupiah(double nominal) {
        String parseRp = "";
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        parseRp = formatRupiah.format(nominal);
        return parseRp;
    }

    public void printTransaksi(String jnstrx, String tanggal, String keterangan, String jenisvoucher,
                               String nomortujuan, String nomorseri, String harga) {


        String sn = nomorseri.replace("SN", "").replace(":", "").replace(" ", "");
        PrintTransaksi.start((Activity) context, jnstrx, tanggal, keterangan, jenisvoucher, nomortujuan, sn, harga);

    }

    public void printTransaksiBpjsKes(String jnstrx, String invoice, String tanggalcetak, String judul, String tanggalTransaksi, String nomorResi,
                                      String nomorPelanggan, String namaPeserta, String jumlahPeserta, String nomorTelepon, String nomorReferensi,
                                      String jumlahPremi, String jumlahTagihan, String biayaAdmin, String totalTagihan, String terbilang, String footer1) {

        PrintTransaksiBPJS.startBPJSKes((Activity) context, jnstrx, invoice, tanggalcetak, judul, tanggalTransaksi, nomorResi,
                nomorPelanggan, namaPeserta, jumlahPeserta, nomorTelepon, nomorReferensi,
                jumlahPremi, jumlahTagihan, biayaAdmin, totalTagihan, terbilang, footer1);
    }

    public void printTransaksiFinanceMAF(String jnstrx, String judul, String tglprint, String invoice, String tgltrx, String noresi, String namakredit,
                                         String cabang, String nokontrak, String nama, String angke, String jthtempo, String tagihan, String totaltagihan, String terbilang, String footer) {
        PrintTransaksiFinanceMAF.startFinanceMAF((Activity) context, jnstrx, judul, tglprint, invoice, tgltrx, noresi, namakredit, cabang,
                nokontrak, nama, angke, jthtempo, tagihan, totaltagihan, terbilang, footer);
    }

    public void printTransaksiPGN(String jnstrx, String judul, String tglprint, String invoice, String tgltrx,
                                  String idtransaksi, String idpelanggan, String nama, String periode, String usage, String ajref,
                                  String tagihan, String admin, String totaltagihan, String terbilang, String footer) {

        PrintTransaksiPGN.startFinancePGN((Activity) context, jnstrx, judul, tglprint, invoice, tgltrx,
                idtransaksi, idpelanggan, nama, periode, usage, ajref,
                tagihan, admin, totaltagihan, terbilang, footer);
    }

    public void buy(String trxJenis, String trxNominal, String trxTujuan, String trxProvideName) {
        Intent i = null;

        if (trxJenis.equalsIgnoreCase("PULSA")) {
            i = new Intent(context, TransPulsa.class);
            i.putExtra("nominal", trxNominal);
            i.putExtra("tujuan", trxTujuan);
            context.startActivity(i);
        } else if (trxJenis.equalsIgnoreCase("KUOTA")) {
            if (trxProvideName.contains("SMS")) {
                i = new Intent(context, TransSMS.class);
                i.putExtra("nominal", trxNominal);
                i.putExtra("tujuan", trxTujuan);
                context.startActivity(i);
            } else {
                i = new Intent(context, TransPaketData.class);
                i.putExtra("nominal", trxNominal);
                i.putExtra("tujuan", trxTujuan);
                context.startActivity(i);
            }
        } else if (trxJenis.equalsIgnoreCase("ETOOL MANDIRI") || trxJenis.equalsIgnoreCase("ETOOL BNI")) {
            i = new Intent(context, TransETool_product.class);
            i.putExtra("nominal", trxNominal);
            i.putExtra("tujuan", trxTujuan);
            i.putExtra("jenis", trxProvideName);
            context.startActivity(i);
        } else if (trxJenis.equalsIgnoreCase("GAME")) {
            i = new Intent(context, TransVouchergame_product.class);
            i.putExtra("nominal", trxNominal);
            i.putExtra("tujuan", trxTujuan);
            i.putExtra("jenis", trxProvideName);
            context.startActivity(i);
        } else if (trxJenis.equalsIgnoreCase("TELPONS")) {
            i = new Intent(context, TransPaketTelp.class);
            i.putExtra("nominal", trxNominal);
            i.putExtra("tujuan", trxTujuan);
            context.startActivity(i);
        } else if (trxJenis.equalsIgnoreCase("SMS")) {
            i = new Intent(context, TransSMS.class);
            i.putExtra("nominal", trxNominal);
            i.putExtra("tujuan", trxTujuan);
            context.startActivity(i);
        } else if (trxJenis.equalsIgnoreCase("PPOB PDAM")) {
            i = new Intent(context, TransPdam.class);
            i.putExtra("nominal", trxNominal);
            i.putExtra("tujuan", trxTujuan);
            context.startActivity(i);
            //============================================================
        } else if (trxJenis.equalsIgnoreCase("PPOB TELKOM")) {
            i = new Intent(context, TransTelkom.class);
            i.putExtra("nominal", trxNominal);
            i.putExtra("tujuan", trxTujuan);
            context.startActivity(i);
        } else if (trxJenis.equalsIgnoreCase("PPOB PLN")) {
            i = new Intent(context, TransPln.class);
            i.putExtra("nominal", "ppob");
            i.putExtra("tujuan", trxTujuan);
            context.startActivity(i);
        } else if (trxJenis.equalsIgnoreCase("PLNTOKEN")) {
            i = new Intent(context, TransPln.class);
            i.putExtra("nominal", "token");
            i.putExtra("tujuan", trxTujuan);
            context.startActivity(i);
        } else if (trxJenis.equalsIgnoreCase("BPJSKES")) {
            i = new Intent(context, TransBpjs.class);
            i.putExtra("nominal", trxNominal);
            i.putExtra("tujuan", trxTujuan);
            context.startActivity(i);
        } else if (trxJenis.equalsIgnoreCase("PPOB TV KABEL")) {
            i = new Intent(context, TransTv.class);
            i.putExtra("nominal", trxNominal);
            i.putExtra("tujuan", trxTujuan);
            context.startActivity(i);
        } else if (trxJenis.equalsIgnoreCase("WIFI ID")) {
            i = new Intent(context, TransWi.class);
            i.putExtra("nominal", trxNominal);
            i.putExtra("tujuan", trxTujuan);
            context.startActivity(i);
        } else if (trxJenis.equalsIgnoreCase("OJEK ONLINE")) {
            i = new Intent(context, TransESaldo_product.class);
            i.putExtra("nominal", trxNominal);
            i.putExtra("tujuan", trxTujuan);
            i.putExtra("jenis", trxProvideName);
            context.startActivity(i);
        } else if (trxJenis.equalsIgnoreCase("PPOB TELCO PASCA")) {
            i = new Intent(context, TransTagihan.class);
            i.putExtra("nominal", trxNominal);
            i.putExtra("tujuan", trxTujuan);
            context.startActivity(i);
        } else {
            Toast.makeText(context, "Coming soon...", Toast.LENGTH_SHORT).show();
        }
    }

    public String formatTgl(String tgl) {
        String format = "";
        if (!tgl.equals("") || !tgl.equals("null")) {
            String parsTgl[] = tgl.split(" ");
            String parsTgl2[] = parsTgl[0].split("-");

            String tanggal = parsTgl2[2];
            String bulan = parsTgl2[1];
            String tahun = parsTgl2[0];

            format = tanggal + "-" + bulan + "-" + tahun + " " + parsTgl[1];
        } else {
            format = "";
        }

        return format;
    }

    public void download(String trxid) {
        context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://demo.eklanku.com/invoice/GenerateInvoice/gen_pdf_download?trxID=" + trxid)));
    }

}