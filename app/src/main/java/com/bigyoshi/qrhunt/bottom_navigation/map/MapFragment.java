package com.bigyoshi.qrhunt.bottom_navigation.map;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bigyoshi.qrhunt.R;
import com.bigyoshi.qrhunt.databinding.FragmentMapBinding;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MapFragment extends Fragment {

    private MapView map = null;
    private FragmentMapBinding binding;
    private MyLocationNewOverlay mLocationOverlay;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        //Load/Initialize osmdroid configuration
        Context ctx = getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //inflate and create the map
        //setContentView(R.layout.fragment_home);
        map = (MapView) root.findViewById(R.id.mapview);
        map.setTileSource(TileSourceFactory.MAPNIK);

        //Map Zoom Controls
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        //Map Controller stuff to move the map on a default view point
        IMapController mapController = map.getController();
        mapController.setZoom(9.5);
        GeoPoint startPoint = new GeoPoint(53.5461, -113.4938);
        mapController.setCenter(startPoint);

        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(ctx),map);
        this.mLocationOverlay.enableMyLocation();
        map.getOverlays().add(this.mLocationOverlay);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
//        Configuration.getInstance().load(this.getContext(), PreferenceManager.getDefaultSharedPreferences(this.getContext()));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
//        Configuration.getInstance().save(this.getContext(), prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }
}