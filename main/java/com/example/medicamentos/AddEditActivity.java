package com.example.medicamentos;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddEditActivity extends AppCompatActivity {

    private EditText etName, etDescription;
    private TextView tvTime;
    private Button btnSave, btnPickTime;
    private FirebaseHelper helper;
    private Medicine editing;
    private long selectedMillis = 0L;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        tvTime = findViewById(R.id.tvTime);
        btnSave = findViewById(R.id.btnSave);
        btnPickTime = findViewById(R.id.btnPickTime);

        helper = new FirebaseHelper();

        // --- EDITAR ---
        if (getIntent().hasExtra("medicine")) {
            editing = (Medicine) getIntent().getSerializableExtra("medicine");
            etName.setText(editing.getName());
            etDescription.setText(editing.getDescription());
            selectedMillis = editing.getTimeMillis();
            tvTime.setText(android.text.format.DateFormat.getTimeFormat(this).format(selectedMillis));
        }

        // --- PICK TIME ---
        btnPickTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int h = c.get(Calendar.HOUR_OF_DAY);
            int m = c.get(Calendar.MINUTE);

            new TimePickerDialog(
                    this,
                    (view, hourOfDay, minute) -> {
                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        c.set(Calendar.MINUTE, minute);
                        c.set(Calendar.SECOND, 0);

                        selectedMillis = c.getTimeInMillis();
                        tvTime.setText(android.text.format.DateFormat.getTimeFormat(this).format(selectedMillis));
                    },
                    h, m,
                    android.text.format.DateFormat.is24HourFormat(this)
            ).show();
        });

        // --- SALVAR ---
        btnSave.setOnClickListener(v -> {

            String name = etName.getText().toString().trim();
            String desc = etDescription.getText().toString().trim();

            if (name.isEmpty() || selectedMillis == 0L) {
                Toast.makeText(this, R.string.fill_required, Toast.LENGTH_SHORT).show();
                return;
            }

            if (editing == null) {

                // Criando novo
                Medicine m = new Medicine();
                m.setName(name);
                m.setDescription(desc);
                m.setTimeMillis(selectedMillis);
                m.setTaken(false);

                helper.addMedicine(m, new FirebaseHelper.Callback() {
                    @Override
                    public void onSuccess(String id) {
                        mostrarPiadaEDepoisFechar();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(AddEditActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            } else {

                // Editando existente
                editing.setName(name);
                editing.setDescription(desc);
                editing.setTimeMillis(selectedMillis);

                helper.updateMedicine(editing.getId(), editing, new FirebaseHelper.Callback() {
                    @Override
                    public void onSuccess(String id) {
                        mostrarPiadaEDepoisFechar();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(AddEditActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }


    // ------------------------------------------------------------
    // MOSTRAR PIADA DA API ANTES DE FINALIZAR A TELA
    // ------------------------------------------------------------
    private void mostrarPiadaEDepoisFechar() {
        ChuckApi.getJoke(new ChuckApi.JokeCallback() {
            @Override
            public void onSuccess(String joke) {
                Toast.makeText(AddEditActivity.this, joke, Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AddEditActivity.this, "Não foi possível carregar piada", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
