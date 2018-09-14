package calc.com.mycalc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import calc.com.mycalc.R;
import calc.com.mycalc.utils.AppConfig;

public class AddTransaction extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.amount)
    EditText amount;
    @BindView(R.id.description)
    EditText description;
    @BindView(R.id.date)
    EditText date;
    @BindView(R.id.done)
    FloatingActionButton done;

    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    private String key;
    private int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            type = intent.getIntExtra(AppConfig.TYPE, 0);
        } else {
            finish();
        }

        key = database.getReference(AppConfig.USERS).push().getKey();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        done.setOnClickListener(this);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.done:
                int i = 100;
                while (i > 0) {
                    key = database.getReference(AppConfig.USERS).push().getKey();
                    Map<String, Object> object = new HashMap<>();
                    object.put(AppConfig.DATE, (i * 106400000));
                    object.put(AppConfig.DESCRIPTION, description.getText().toString().trim());
                    if (i % 2 == 0) {
                        type = 0;
                    } else {
                        type = 1;
                    }
                    object.put(AppConfig.TYPE, type);
                    object.put(AppConfig.AMOUNT, Integer.parseInt(amount.getText().toString().trim()));
                    object.put(AppConfig.FIRE_ID, -1 * new Date().getTime());

                    database.getReference(AppConfig.USERS).child(firebaseAuth.getUid()).child(AppConfig.TRANSACTIONS).child(key).setValue(object);
                    finish();
                    i--;
                }

//            break;
        }
    }
}
