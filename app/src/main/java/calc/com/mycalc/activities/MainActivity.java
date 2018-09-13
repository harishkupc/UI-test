package calc.com.mycalc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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
    @BindView(R.id.filter)
    TextView filter;
    @BindView(R.id.in)
    TextView in;
    @BindView(R.id.out)
    TextView out;
    private List<HashMap<String, Object>> list = new ArrayList<>();
   /* @BindView(R.id.add)
    FloatingActionButton add;
    @BindView(R.id.remove)
    FloatingActionButton remove;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        setSupportActionBar(toolbar);

        populateList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!userPreference.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                startActivity(new Intent(getApplicationContext(), AddTransaction.class).putExtra(AppConfig.TYPE, 0));
                break;

            case R.id.remove:
                startActivity(new Intent(getApplicationContext(), AddTransaction.class).putExtra(AppConfig.TYPE, 1));
                break;

            case R.id.filter:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateList() {
        Query myMostViewedPostsQuery = database.getReference(AppConfig.USERS).child(firebaseAuth.getUid())
                .child(AppConfig.TRANSACTIONS).orderByChild(AppConfig.FIRE_ID);
        myMostViewedPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    list.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot != null) {
                            HashMap<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                            list.add(map);
                        }
                    }


                    if (list.size() > 0) {
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
    }
}
