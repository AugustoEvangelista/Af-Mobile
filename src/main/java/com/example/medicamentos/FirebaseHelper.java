package com.example.medicamentos;


import android.util.Log;


import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseHelper {
    private static final String COLLECTION = "medicamentos";
    private FirebaseFirestore db;


    public FirebaseHelper() {
        db = FirebaseFirestore.getInstance();
    }
    public void addMedicine(final Medicine m, final Callback callback) {
        CollectionReference col = db.collection(COLLECTION);
        Map<String,Object> map = new HashMap<>();
        map.put("name", m.getName());
        map.put("description", m.getDescription());
        map.put("timeMillis", m.getTimeMillis());
        map.put("taken", m.isTaken());
        col.add(map).addOnSuccessListener(docRef -> {
            callback.onSuccess(docRef.getId());
        }).addOnFailureListener(e -> callback.onFailure(e));
    }
    public void updateMedicine(String id, Medicine m, final Callback callback) {
        Map<String,Object> map = new HashMap<>();
        map.put("name", m.getName());
        map.put("description", m.getDescription());
        map.put("timeMillis", m.getTimeMillis());
        map.put("taken", m.isTaken());
        db.collection(COLLECTION).document(id).set(map)
                .addOnSuccessListener(aVoid -> callback.onSuccess(id))
                .addOnFailureListener(e -> callback.onFailure(e));
    }
    public void deleteMedicine(String id, final Callback callback) {
        db.collection(COLLECTION).document(id).delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess(id))
                .addOnFailureListener(e -> callback.onFailure(e));
    }
    public void listenAll(final OnListChanged listener) {
        db.collection(COLLECTION).addSnapshotListener((value, error) -> {
            if (error != null) { listener.onError(error); return; }
            List<Medicine> list = new ArrayList<>();
            for (DocumentSnapshot ds : value.getDocuments()) {
                Medicine m = new Medicine();
                m.setId(ds.getId());
                m.setName(ds.getString("name"));
                m.setDescription(ds.getString("description"));
                Object t = ds.get("timeMillis");
                m.setTimeMillis(t == null ? 0L : (long) (ds.getDouble("timeMillis") == null ? ds.getLong("timeMillis") : ds.getDouble("timeMillis").longValue()));
                Boolean taken = ds.getBoolean("taken");
                m.setTaken(taken != null && taken);
                list.add(m);
            }
            listener.onChanged(list);
        });
    }

    public interface Callback {
        void onSuccess(String id);
        void onFailure(Exception e);
    }


    public interface OnListChanged {
        void onChanged(List<Medicine> list);
        void onError(Exception e);
    }
}

