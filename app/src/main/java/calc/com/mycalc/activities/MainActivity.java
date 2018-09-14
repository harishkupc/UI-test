package calc.com.mycalc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import calc.com.mycalc.R;
import calc.com.mycalc.adapters.CashListAdapter;
import calc.com.mycalc.utils.AppConfig;

public class MainActivity extends BaseActivity {

    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nf)
    LinearLayout nf;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    /*@BindView(R.id.filter)
    TextView filter;*/
    @BindView(R.id.in)
    TextView in;
    @BindView(R.id.out)
    TextView out;
    @BindView(R.id.piechart)
    PieChart piechart;
    @BindView(R.id.remaining)
    TextView remaining;
    @BindView(R.id.remove)
    Button remove;
    @BindView(R.id.add)
    Button add;
    @BindView(R.id.addFilter)
    TextView addFilter;
    @BindView(R.id.linear)
    LinearLayout linear;

    List<String> filtered = new ArrayList<>();
    List<String> filterList = new ArrayList<>();

    String LAST_7_DAYS;
    String LAST_30_DAYS;
    String MONTHLY;
    String YEARLY;
    String INCOME;
    String OUTCOME;

    private List<HashMap<String, Object>> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        LAST_7_DAYS = getString(R.string.last_7_days);
        LAST_30_DAYS = getString(R.string.last_30_days);
        MONTHLY = getString(R.string.monthly);
        YEARLY = getString(R.string.yearly);
        INCOME = getString(R.string.income);
        OUTCOME = getString(R.string.outcome);

        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        setSupportActionBar(toolbar);

        populateList();

        add.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), AddTransaction.class).putExtra(AppConfig.TYPE, 0)));
        remove.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), AddTransaction.class).putExtra(AppConfig.TYPE, 1)));

        addFilter.setOnClickListener(view -> {

            if (filtered.contains(LAST_7_DAYS)) {
                showFilterList(
                        LAST_30_DAYS,
                        MONTHLY,
                        YEARLY,
                        INCOME,
                        OUTCOME
                );
            } else if (filtered.contains(LAST_30_DAYS)) {
                showFilterList(
                        LAST_7_DAYS,
                        MONTHLY,
                        YEARLY,
                        INCOME,
                        OUTCOME
                );
            } else if (filtered.contains(MONTHLY)) {
                showFilterList(
                        LAST_7_DAYS,
                        LAST_30_DAYS,
                        YEARLY,
                        INCOME,
                        OUTCOME
                );
            } else if (filtered.contains(YEARLY)) {
                showFilterList(
                        LAST_7_DAYS,
                        LAST_30_DAYS,
                        MONTHLY,
                        INCOME,
                        OUTCOME
                );
            } else if (filtered.contains(INCOME)) {
                showFilterList(
                        LAST_7_DAYS,
                        LAST_30_DAYS,
                        MONTHLY,
                        YEARLY,
                        OUTCOME
                );
            } else if (filtered.contains(OUTCOME)) {
                showFilterList(
                        LAST_7_DAYS,
                        LAST_30_DAYS,
                        MONTHLY,
                        YEARLY,
                        INCOME
                );
            } else {
                showFilterList(
                        LAST_7_DAYS,
                        LAST_30_DAYS,
                        MONTHLY,
                        YEARLY,
                        INCOME,
                        OUTCOME
                );
            }

        });

    }

    private void showFilterList(String... s) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_filter);
        builder.setItems(s, (dialog, which) -> {
            // the user clicked on colors[which]
            attachFilter(s, which);
        });
        builder.show();

    }

    private void attachFilter(String[] filters, int which) {
        if (filters[which].equals(getString(R.string.last_7_days))) {
            addValueInFilterList(LAST_7_DAYS);
        } else if (filters[which].equals(getString(R.string.last_30_days))) {
            addValueInFilterList(LAST_30_DAYS);
        } else if (filters[which].equals(getString(R.string.monthly))) {
            addValueInFilterList(MONTHLY);
        } else if (filters[which].equals(getString(R.string.yearly))) {
            addValueInFilterList(YEARLY);
        } else if (filters[which].equals(getString(R.string.income))) {
            if (filtered.contains(INCOME) | filtered.contains(OUTCOME)) {
                filtered.remove(INCOME);
                filtered.remove(OUTCOME);
            } else {
                filtered.add(INCOME);
            }
        } else {
            if (filtered.contains(INCOME) | filtered.contains(OUTCOME)) {
                filtered.remove(INCOME);
                filtered.remove(OUTCOME);
            } else {
                filtered.add(OUTCOME);
            }
        }

        TextView view = (TextView) LayoutInflater.from(this).inflate(R.layout.item_filter, linear, false);

        view.setText("Last 7 days");
        linear.addView(view, 1);
    }

    private void addValueInFilterList(String s) {
        if (filtered.contains(LAST_7_DAYS) | filtered.contains(LAST_30_DAYS) |
                filtered.contains(MONTHLY) | filtered.contains(YEARLY)) {
            filtered.remove(LAST_7_DAYS);
            filtered.remove(LAST_30_DAYS);
            filtered.remove(MONTHLY);
            filtered.remove(YEARLY);
        } else {
            filtered.add(0, s);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!userPreference.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            populateList();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.filter:
                String[] colors = {"Daily", getString(R.string.monthly), getString(R.string.yearly)};

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Pick a color");
                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // the user clicked on colors[which]
                    }
                });
                builder.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/

    private void populateList() {
        Query myMostViewedPostsQuery = database.getReference(AppConfig.USERS).child(firebaseAuth.getUid())
                .child(AppConfig.TRANSACTIONS).orderByChild(AppConfig.DATE);
        /*Query myMostViewedPostsQuery = database.getReference(AppConfig.USERS).child(firebaseAuth.getUid())
                .child(AppConfig.TRANSACTIONS).orderByChild(AppConfig.DATE).startAt(0).endAt(200261632);*/
        myMostViewedPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    list.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot != null) {
                            HashMap<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                            list.add(map);
                        }
                    }

                    if (list.size() > 0) {

                        Collections.sort(list, (obj1, obj2) -> ((Long) obj2.get(AppConfig.DATE)).compareTo((Long) obj1.get(AppConfig.DATE)));

                        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
                        recycler.setLayoutManager(manager);
                        CashListAdapter adapter = new CashListAdapter(list);
                        recycler.setAdapter(adapter);
                        nf.setVisibility(View.GONE);
                        recycler.setVisibility(View.VISIBLE);
                        updateCount(list);
                    } else {
                        nf.setVisibility(View.VISIBLE);
                        recycler.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (databaseError != null) {

                }
            }
        });
    }

    private void updateCount(List<HashMap<String, Object>> list) {
        int totalIn = 0, totalOut = 0;
        for (HashMap<String, Object> map : list) {
            if ((Long) map.get(AppConfig.TYPE) == 0) {
                totalIn += (Long) map.get(AppConfig.AMOUNT);
            } else {
                totalOut += (Long) map.get(AppConfig.AMOUNT);
            }
        }
        in.setText("" + totalIn);
        out.setText("" + totalOut);
        remaining.setText("" + (totalIn - totalOut));

        long total = totalIn + totalOut;

        piechart.setUsePercentValues(true);

        List<PieEntry> entries = new ArrayList<>();

        float inPercent = (totalIn * 100) / total;
        float outPercent = (totalOut * 100) / total;

        entries.add(new PieEntry(inPercent, ""));
        entries.add(new PieEntry(outPercent, ""));

        PieDataSet set = new PieDataSet(entries, "");
        set.setColors(ContextCompat.getColor(this, R.color.green),
                ContextCompat.getColor(this, R.color.red));

        PieData data = new PieData(set);
        piechart.setData(data);
        piechart.setDrawSliceText(false);
        piechart.getDescription().setEnabled(false);
        piechart.getLegend().setEnabled(false);
        piechart.setEntryLabelColor(getResources().getColor(R.color.white));
        piechart.invalidate(); // refresh
    }
}
