package com.bigyoshi.qrhunt.qr;

import android.location.Location;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bigyoshi.qrhunt.player.FragmentProfile;
import com.bigyoshi.qrhunt.player.Player;
import com.bigyoshi.qrhunt.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Definition: Fragment used when the player wishes to delete their QR code
 * Note: NA
 * Issues: NA
 */
public class FragmentQrProfile extends DialogFragment {

    private int pos;
    private PlayableQrCode currentQR;
    private Player player;

    /**
     * Constructor method
     *  @param i int
     * @param currentQR QR to remove
     * @param player
     */
    public FragmentQrProfile(int i, PlayableQrCode currentQR, Player player) {
        this.pos = pos;
        this.currentQR = currentQR;
        this.player = player;
    }

    /**
     * Creates the view for deleting a QR code
     *
     * @param inflater           Inflater
     * @param container          Where the fragment is contained
     * @param savedInstanceState SavedInstanceState
     * @return View
     */
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_player_profile, container, false);

        // Display score
        TextView showScore = view.findViewById(R.id.qr_profile_qr_score);
        showScore.setText(String.valueOf(currentQR.getScore())+" Points");

        // Display numScan
        TextView showNumScanned = view.findViewById(R.id.qr_profile_num_scanned);
        showNumScanned.setText("01"); // HARD CODED FOR NOW

        // Display location
        TextView showLatLong = view.findViewById(R.id.qr_profile_qr_location);
        QrLocation qrLocation = currentQR.getLocation();
        if (qrLocation != null) {
            String strLatitude = Location.convert(qrLocation.getLatitude(), Location.FORMAT_DEGREES);
            String strLongitude = Location.convert(qrLocation.getLongitude(), Location.FORMAT_DEGREES);
            showLatLong.setText(strLatitude + ", " + strLongitude);
        } else {
            showLatLong.setText("LOCATION NOT GIVEN");
        }

        // Attach Image
        ImageView showPic = view.findViewById(R.id.qr_profile_image_placeholder);
        if (currentQR.getImageUrl() != null) {
            Picasso.get().load(currentQR.getImageUrl()).into(showPic);
        }
        showPic.setCropToPadding(true);

        // Display Username
        TextView userName = view.findViewById(R.id.qr_profile_player_username);
        userName.setText(player.getUsername());

        // Delete QR Button, Only visible when the player own the QR or they are an admin
        Button deleteButton = view.findViewById(R.id.button_delete);
        if (player.isAdmin() || player.getPlayerId().equals(currentQR.getPlayerId())) {
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setText("BRUH");
        }
        deleteButton.setOnClickListener(view1 -> {
            FragmentProfile parentFrag = ((FragmentProfile) this.getParentFragment());
            parentFrag.libraryRemoveQR(pos, currentQR);
            getFragmentManager().beginTransaction().remove(this).commit();
        });

        // Back Button
        ImageButton backButton = view.findViewById(R.id.qr_profile_back_button);
        backButton.setOnClickListener(view2 -> {
            getFragmentManager().beginTransaction().remove(FragmentQrProfile.this).commit();
        });

        // Display Comments
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ListView commentList = view.findViewById(R.id.qr_profile_comment_list);
        ArrayList<QRComment> comments = new ArrayList();
        QRCommentAdapter commentAdapter = new QRCommentAdapter(view.getContext(), comments);
        commentList.setAdapter(commentAdapter);
        db.collection("users").document(player.getPlayerId()).collection("qrCodes").document(currentQR.getId()).collection("comments")
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    if (doc.exists()) {
                        QRComment comment = new QRComment(doc.getData().get("comment").toString(), doc.getData().get("username").toString());
                        comments.add(comment);
                    }
                }
            }
        });
        commentAdapter.notifyDataSetChanged();


        // Add QRComment
        ImageButton commentButton = view.findViewById(R.id.qr_profile_send_comment_button);
        commentButton.setOnClickListener(view3 -> {
            EditText newCommentText = view.findViewById(R.id.qr_profile_add_comment);
            HashMap<String, String> map = new HashMap<>();
            map.put("comment", newCommentText.getText().toString());
            map.put("username", player.getUsername());

            QRComment newComment = new QRComment(
                    newCommentText.getText().toString(), player.getUsername());
            comments.add(newComment);
            db.collection("users").document(player.getPlayerId()).collection("qrCodes").document(currentQR.getId())
                    .collection("comments")
                    .document(newCommentText.getText().toString()).set(map);
            commentAdapter.notifyDataSetChanged();
        });

        return view;
    }

    // Me being stupid and using a DialogFragment as the base early on
    // Makes it full screen rather than windowed view
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,
                android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }
}
