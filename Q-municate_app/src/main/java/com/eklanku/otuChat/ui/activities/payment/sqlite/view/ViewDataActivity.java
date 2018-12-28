package com.eklanku.otuChat.ui.activities.payment.sqlite.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.payment.sqlite.database.DatabaseHelper;
import com.eklanku.otuChat.ui.activities.payment.sqlite.database.model.History;
import com.eklanku.otuChat.ui.activities.payment.sqlite.utils.MyDividerItemDecoration;
import com.eklanku.otuChat.ui.activities.payment.sqlite.utils.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;


public class ViewDataActivity extends AppCompatActivity {
    private HistoryAdapter mAdapter;
    private List<History> historyList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private TextView noHistoryView;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sqlite_activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        recyclerView = findViewById(R.id.recycler_view);
        noHistoryView = findViewById(R.id.empty_history_view);

        db = new DatabaseHelper(this);

        historyList.addAll(db.getAllHistory());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHistoryDialog(false, null, -1);
            }
        });

        mAdapter = new HistoryAdapter(this, historyList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);

        toggleEmptyNotesHistory();

        /**
         * On long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit and Delete
         * */
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));
    }

    /**
     * Inserting new number in db
     * and refreshing the list
     */
    private void createHistory(String number, String nominal, String trans) {
        // inserting number in db and getting
        // newly inserted number id
        long id = db.insertHistory(number, nominal, trans);

        // get the newly inserted number from db
        History n = db.getHistory(id);

        if (n != null) {
            // adding new number to array list at 0 position
            historyList.add(0, n);

            // refreshing the list
            mAdapter.notifyDataSetChanged();

            toggleEmptyNotesHistory();
        }
    }

    /**
     * Updating number in db and updating
     * item in the list by its position
     */
    private void updateHistory(String number, int position) {
        History n = historyList.get(position);
        // updating number text
        n.setNumber(number);

        // updating number in db
        db.updateHistory(n);

        // refreshing the list
        historyList.set(position, n);
        mAdapter.notifyItemChanged(position);

        toggleEmptyNotesHistory();
    }

    /**
     * Deleting number from SQLite and removing the
     * item from the list by its position
     */
    private void deleteHistory(int position) {
        // deleting the number from db
        db.deleteHistory(historyList.get(position));

        // removing the number from the list
        historyList.remove(position);
        mAdapter.notifyItemRemoved(position);

        toggleEmptyNotesHistory();
    }

    /**
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 0
     */
    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showHistoryDialog(true, historyList.get(position), position);
                } else {
                    deleteHistory(position);
                }
            }
        });
        builder.show();
    }


    /**
     * Shows alert dialog with EditText options to enter / edit
     * a number.
     * when shouldUpdate=true, it automatically displays old number and changes the
     * button text to UPDATE
     */
    private void showHistoryDialog(final boolean shouldUpdate, final History history, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.sqlite_history_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(ViewDataActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputNumber = view.findViewById(R.id.number);
        final EditText inputNominal = view.findViewById(R.id.nominal);
        final EditText inputTrans = view.findViewById(R.id.trans);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_history_title) : getString(R.string.lbl_edit_history_title));

        if (shouldUpdate && history != null) {
            inputNumber.setText(history.getNumber());
            inputNominal.setText(history.getNominal());
            inputTrans.setText(history.getTrans());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(inputNumber.getText().toString())) {
                    Toast.makeText(ViewDataActivity.this, "Enter number!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating number
                if (shouldUpdate && history != null) {
                    // update number by it's id
                    updateHistory(inputNumber.getText().toString(), position);
                } else {
                    // create new number
                    createHistory(inputNumber.getText().toString(), inputNominal.getText().toString(), inputTrans.getText().toString());
                }
            }
        });
    }

    /**
     * Toggling list and empty notes view
     */
    private void toggleEmptyNotesHistory() {
        // you can check historyList.size() > 0

        if (db.getHistoryCount() > 0) {
            noHistoryView.setVisibility(View.GONE);
        } else {
            noHistoryView.setVisibility(View.VISIBLE);
        }
    }
}
