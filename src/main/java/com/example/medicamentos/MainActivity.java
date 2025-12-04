package com.example.medicamentos;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Firebase;


import java.util.List;


public class MainActivity extends AppCompatActivity implements FirebaseHelper.OnListChanged {


    private RecyclerView recyclerView;
    private MedicineAdapter adapter;
    private FirebaseHelper helper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        helper = new FirebaseHelper();
        recyclerView = findViewById(R.id.recyclerMedicines);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MedicineAdapter(this, (m, action) -> {
// action: 0 = edit, 1 = toggleTaken, 2 = delete
            if (action == 0) {
                Intent it = new Intent(MainActivity.this, AddEditActivity.class);
                it.putExtra("medicine", m);
                startActivity(it);
            } else if (action == 1) {
                m.setTaken(!m.isTaken());
                helper.updateMedicine(m.getId(), m, new FirebaseHelper.Callback() {
                    @Override
                    public void onSuccess(String id) { Toast.makeText(MainActivity.this, R.string.saved, Toast.LENGTH_SHORT).show(); }
                    @Override
                    public void onFailure(Exception e) { Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show(); }
                });
            } else if (action == 2) {
                helper.deleteMedicine(m.getId(), new FirebaseHelper.Callback() {
                    @Override
                    public void onSuccess(String id) { Toast.makeText(MainActivity.this, R.string.deleted, Toast.LENGTH_SHORT).show(); }
                    @Override
                    public void onFailure(Exception e) { Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show(); }
                });
            }});
        recyclerView.setAdapter(adapter);


        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddEditActivity.class)));


        helper.listenAll(this);
    }
    @Override
    public void onChanged(List<Medicine> list) {
        adapter.setList(list);
// (re)schedule alarms for upcoming medicines
        scheduleAlarms(list);
    }
    @Override
    public void onError(Exception e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void scheduleAlarms(List<Medicine> list) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;
        for (Medicine m : list) {
            Intent intent = new Intent(this, NotificationReceiver.class);
            intent.putExtra("name", m.getName());
            intent.putExtra("id", m.getId());
            PendingIntent pi = PendingIntent.getBroadcast(this, m.getId().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            long when = m.getTimeMillis();
            if (when > System.currentTimeMillis() && !m.isTaken()) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, when, pi);
            } else {
                am.cancel(pi);
            }
        }
    }
}

