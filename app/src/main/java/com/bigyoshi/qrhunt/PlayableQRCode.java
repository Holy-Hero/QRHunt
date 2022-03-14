package com.bigyoshi.qrhunt;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;


import java.util.HashMap;
import java.util.Objects;

/**
 * Definition:
 *
 *
 */
public class PlayableQRCode {
    private String id; // Hash of the actual data from the scan
    private int score; // The score of the QR code
    private QRLocation location;
    private int numScanned;
    private Bitmap image;
    private HashMap<String, Object> qrStuff;

    /**
     *
     * @param id
     * @param score
     */
    // We need to distinguish QRCodes already scanned and those who have not been scanned yet
    //  Since initialization of numScanned would either be an update OR just 1
    public PlayableQRCode(String id, int score){
        this.score = score;
        this.id = id;
        this.numScanned = 1;
    }

    /**
     *
     * @return
     */
    // Just a bunch of getters and setters, delete if unneeded
    public int getNumScanned() { return this.numScanned; }

    /**
     *
     * @return
     */
    public int getScore() { return this.score; }

    /**
     *
     * @return
     */
    public QRLocation getLocation() { return this.location; }

    /**
     *
     * @return
     */
    public Bitmap getImage() { return this.image; }

    /**
     *
     * @return
     */
    public String getId() { return id; }

    /**
     *
     * @param lat
     * @param lon
     */
    public void setLocation(double lat, double lon) { this.location = new QRLocation(lat, lon); }

    /**
     *
     * @param image
     */
    public void setImage(Bitmap image) { this.image = image; }

    /**
     *
     * @return
     */
    public boolean isLocation() { return this.location != null; }

    /**
     *
     * @param db
     */
    public void grabNumScanned(FirebaseFirestore db){
        // Pulls the total number scanned from the db
        Task<DocumentSnapshot> qrData = db.collection("qrCodes").document(this.id).get();
        this.numScanned = Integer.parseInt((Objects.requireNonNull(Objects.requireNonNull(qrData.getResult()).getString("numScanned"))));
    }

    /**
     *
     * @param db
     * @param playerId
     */
    public void DeleteFromDB(FirebaseFirestore db, String playerId) {
        db.collection("qrCodes").document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        db.collection("user").document(playerId)
                                .collection("qrCodes").document(id).delete();
                        Log.d("Delete_QR", "Successfully removed QR from data base");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Delete_QR", "Error removing QR from data base", e);
                    }
                });

    }

    /**
     *
     * @param db
     * @param playerId
     */
    public void AddToQRLibrary(FirebaseFirestore db, String playerId) {
        qrStuff = new HashMap<>();
        // todo check size of image
        // not adding yet so we don't go over the free usage amount for firestore
        //playerQrStuff.put("image", image);
        qrStuff.put("score", score);
        qrStuff.put("latitude", location.getLat());
        qrStuff.put("longitude", location.getLong());
        qrStuff.put("geoHash", location.getId());

        db.collection("users").document(playerId)
                .collection("qrCodes").document(id).set(qrStuff, SetOptions.merge());
    }
}