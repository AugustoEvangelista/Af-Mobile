package com.example.medicamentos;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class FirebaseHelper {

    private static final String COLLECTION = "medicamentos";
    private FirebaseFirestore db;

    public FirebaseHelper() {
        db = FirebaseFirestore.getInstance();
    }

    // -------------------------------
    //  CREATE
    // -------------------------------
    public void addMedicine(final Medicine m, final Callback callback) {

        // SE NÃO TIVER ID → GERAR
        if (m.getId() == null || m.getId().trim().isEmpty()) {
            m.setId(db.collection(COLLECTION).document().getId());
        }

        Map<String, Object> map = new HashMap<>();
        map.put("id", m.getId());
        map.put("name", m.getName());
        map.put("description", m.getDescription());
        map.put("timeMillis", m.getTimeMillis());
        map.put("taken", m.isTaken());

        db.collection(COLLECTION)
                .document(m.getId())
                .set(map)
                .addOnSuccessListener(aVoid -> callback.onSuccess(m.getId()))
                .addOnFailureListener(callback::onFailure);
    }

    // -------------------------------
    //  UPDATE
    // -------------------------------
    public void updateMedicine(String id, Medicine m, final Callback callback) {

        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", m.getName());
        map.put("description", m.getDescription());
        map.put("timeMillis", m.getTimeMillis());
        map.put("taken", m.isTaken());

        db.collection(COLLECTION)
                .document(id)
                .set(map)
                .addOnSuccessListener(aVoid -> callback.onSuccess(id))
                .addOnFailureListener(callback::onFailure);
    }

    // -------------------------------
    //  DELETE
    // -------------------------------
    public void deleteMedicine(String id, final Callback callback) {

        db.collection(COLLECTION)
                .document(id)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess(id))
                .addOnFailureListener(callback::onFailure);
    }

    // -------------------------------
    //  READ + ESCUTA EM TEMPO REAL
    // -------------------------------
    public void listenAll(final OnListChanged listener) {

        db.collection(COLLECTION).addSnapshotListener((value, error) -> {

            if (error != null) {
                listener.onError(error);
                return;
            }

            List<Medicine> list = new ArrayList<>();

            if (value == null) {
                listener.onChanged(list);
                return;
            }

            for (DocumentSnapshot ds : value.getDocuments()) {

                Medicine m = new Medicine();

                // ----- ID -----
                m.setId(ds.getId());

                // ----- NAME / DESC -----
                m.setName(ds.getString("name"));
                m.setDescription(ds.getString("description"));

                // ----- TIME MILLIS -----

                /*
                 Firestore às vezes salva long como Double!
                 Por isso temos que tentar os dois formatos.
                 Esse bloco elimina 100% dos crashes.
                */

                Double tDouble = ds.getDouble("timeMillis");
                Long tLong = ds.getLong("timeMillis");

                if (tDouble != null) {
                    m.setTimeMillis(tDouble.longValue());
                } else if (tLong != null) {
                    m.setTimeMillis(tLong);
                } else {
                    m.setTimeMillis(0L);
                }

                // ----- TAKEN -----
                Boolean taken = ds.getBoolean("taken");
                m.setTaken(taken != null && taken);

                list.add(m);
            }

            listener.onChanged(list);
        });
    }

    // -------------------------------
    //  INTERFACES DE CALLBACK
    // -------------------------------
    public interface Callback {
        void onSuccess(String id);
        void onFailure(Exception e);
    }

    public interface OnListChanged {
        void onChanged(List<Medicine> list);
        void onError(Exception e);
    }
}
