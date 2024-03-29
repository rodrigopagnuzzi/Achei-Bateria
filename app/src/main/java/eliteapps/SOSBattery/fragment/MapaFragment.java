package eliteapps.SOSBattery.fragment;


import android.app.Fragment;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;
import eliteapps.SOSBattery.R;
import eliteapps.SOSBattery.adapter.CustomViewPager;
import eliteapps.SOSBattery.application.App;
import eliteapps.SOSBattery.domain.Estabelecimentos;
import eliteapps.SOSBattery.domain.ListaDeCoordenadas;
import eliteapps.SOSBattery.domain.ListaDeEstabelecimentos;
import eliteapps.SOSBattery.domain.ListaMarker;
import eliteapps.SOSBattery.eventBus.MessageEB;
import eliteapps.SOSBattery.util.CalendarUtil;
import eliteapps.SOSBattery.util.NavigationDrawerUtil;
import eliteapps.SOSBattery.util.PrefManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapaFragment extends Fragment {

    static boolean aqui = true;
    static HashMap<String, Estabelecimentos> mHashMap;
    private final String TAG = this.getClass().getSimpleName();
    MapView mMapView;
    RelativeLayout relativeLayout;
    DrawerLayout mDrawerLayout;
    List<Estabelecimentos> estabelecimentosList;
    double lat;
    double lon;
    Tracker mTracker;
    boolean not_first_time_showing_info_window = false;
    boolean not_first_time_showing_info_window_2 = false;
    PrefManager prefManager;
    private GoogleMap googleMap;
    private View view;


    public MapaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        App application = (App) getActivity().getApplication();
        mTracker = application.getDefaultTracker();


        prefManager = new PrefManager(getActivity(), "MainActivity");

        Log.println(Log.ASSERT, TAG, "onCreateView");


        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_mapa, container, false);


        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        relativeLayout = (RelativeLayout) v.findViewById(R.id.mapa_layout);
        mMapView.onResume();// needed to get the map to display immediately


        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }


        googleMap = mMapView.getMap();
        googleMap.setMyLocationEnabled(true);

        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);


        lat = getArguments().getDouble("lat");
        lon = getArguments().getDouble("lon");


        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(lat, lon)).zoom(15).build();
        googleMap.moveCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Build and send an Event.
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Marker")
                        .setAction("Marker Selected")
                        .setLabel("Marker " + marker.getId())
                        .build());

                return false;
            }
        });


        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(final Marker marker) {


                estabelecimentosList = ListaDeEstabelecimentos.getInstance().getListaDeEstabelecimentos();

                Location l = new Location("");
                l.setLatitude(marker.getPosition().latitude);
                l.setLongitude(marker.getPosition().longitude);


                view = getActivity().getLayoutInflater().inflate(R.layout.marker_info, null);


                TextView txtNome = (TextView) view.findViewById(R.id.txtNomeMapa);
                TextView hrFunc = (TextView) view.findViewById(R.id.txtHrFuncMapa);

                final ImageView image = (ImageView) view.findViewById(R.id.imgLojaMapa);


                Estabelecimentos e = mHashMap.get(l.getLatitude() + "_" + l.getLongitude());


                txtNome.setText(e.getNome());

                Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Leelawadee.ttf");
                txtNome.setTypeface(type);
                String hrFunc2 = CalendarUtil.HrFuncionamento(e);
                hrFunc.setText(hrFunc2);


                if (!e.getImgURL().isEmpty()) {

                    // set image view like this:
                    if (not_first_time_showing_info_window) {
                        not_first_time_showing_info_window = false;
                        Picasso.with(getActivity())

                                .load(e.getImgURL())
                                .resize(100, 100)
                                .error(R.drawable.no_image)
                                .into(image);

                    } else { // if it's the first time, load the image with the callback set
                        not_first_time_showing_info_window = true;
                        Picasso.with(getActivity())

                                .load(e.getImgURL())
                                .resize(100, 100)
                                .error(R.drawable.no_image)
                                .into(image, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {

                                        marker.showInfoWindow();
                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });
                    }

                } else {
                    if (not_first_time_showing_info_window_2) {
                        not_first_time_showing_info_window_2 = false;
                        Picasso.with(getActivity())

                                .load(R.drawable.no_image)
                                .resize(100, 100)
                                .error(R.drawable.no_image)
                                .into(image);

                    } else { // if it's the first time, load the image with the callback set
                        not_first_time_showing_info_window_2 = true;
                        Picasso.with(getActivity())

                                .load(R.drawable.no_image)
                                .resize(100, 100)
                                .error(R.drawable.no_image)
                                .into(image, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {

                                        marker.showInfoWindow();
                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });
                    }


                }


                if (!e.getWifi())
                    view.findViewById(R.id.imgWifiMapa).setVisibility(View.GONE);

                if (!e.getCabo().getAndroid())
                    view.findViewById(R.id.imgCaboMapa).setVisibility(View.GONE);


                System.gc();


                return view;
            }
        });

        final CustomViewPager customViewPager = (CustomViewPager) getActivity().findViewById(R.id.pager);
        customViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mDrawerLayout = NavigationDrawerUtil.getDrawer().getDrawerLayout();

                if (position == 1) {

                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    googleMap.setMyLocationEnabled(true);
                    mTracker.setScreenName("MapView");
                    mTracker.send(new HitBuilders.ScreenViewBuilder().build());
                } else {

                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    googleMap.setMyLocationEnabled(false);
                    mTracker.setScreenName("ListView");
                    mTracker.send(new HitBuilders.ScreenViewBuilder().build());
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mDrawerLayout = NavigationDrawerUtil.getDrawer().getDrawerLayout();

        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Drawer")
                        .setAction("Open Drawer")
                        .setLabel("Open Drawer")
                        .build());

                if (customViewPager.getCurrentItem() == 1)
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);


            }

            @Override
            public void onDrawerClosed(View drawerView) {

                if (customViewPager.getCurrentItem() == 1)
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

            }

            @Override
            public void onDrawerStateChanged(int newState) {


            }
        });

        // Perform any camera updates here
        return v;
    }


    @Override
    public void onResume() {

        super.onResume();
        EventBus.getDefault().register(this);
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        mMapView.onDestroy();

        EventBus.getDefault().unregister(this);


    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public void onEvent(MessageEB event) {

        if (event.getData().equals(TAG)) {
            String ID;
            Location location;

            int pos = event.getPos();

            List<Estabelecimentos> lFechados = event.getEstabelecimentosList();

            Log.println(Log.ASSERT, TAG, "pos1: " + pos);
            estabelecimentosList = ListaDeEstabelecimentos.getInstance().getListaDeEstabelecimentos();

            if (pos < estabelecimentosList.size()) {
                ID = estabelecimentosList.get(pos).getId();
                location = ListaDeCoordenadas.getInstance().getListaDeCoordenadas().get(ID);
                ListaMarker.getInstance().getListaMarker().get(pos).showInfoWindow();

            } else {
                int posicao = pos - estabelecimentosList.size();

                ID = lFechados.get(posicao).getId();

                location = ListaDeCoordenadas.getInstance().getListaDeCoordenadas().get(ID);
                ListaMarker.getInstance().getListaMarker().get(pos).showInfoWindow();


            }


            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(15).build();
            googleMap.moveCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

        } else if (event.getData().equals("ListaLojasFragment")) {


            googleMap.clear();
            ListaMarker.getInstance().getListaMarker().clear();


            List<Estabelecimentos> l = ListaDeEstabelecimentos.getInstance().getListaDeEstabelecimentos();

            List<Estabelecimentos> lFechados = event.getEstabelecimentosList();


            mHashMap = new HashMap<>();

            populaMapa(l, true);
            populaMapa(lFechados, false);


            if (relativeLayout.getVisibility() != View.VISIBLE)
                relativeLayout.setVisibility(View.VISIBLE);

        } else if (event.getData().equals("All")) {

            lat = event.getLocation().getLatitude();
            lon = event.getLocation().getLongitude();


            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(lat, lon)).zoom(15).build();
            googleMap.moveCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

        }

    }


    private void populaMapa(List<Estabelecimentos> l, boolean isStoreOpen) {
        MarkerOptions marker2;

        for (int i = 0; i < l.size(); i++) {

            String ID = l.get(i).getId();

            Location location = ListaDeCoordenadas.getInstance().getListaDeCoordenadas().get(ID);

            mHashMap.put(location.getLatitude() + "_" + location.getLongitude(), l.get(i));
            // create marker

            marker2 = new MarkerOptions().position(
                    new LatLng(location.getLatitude(), location.getLongitude()))
                    .title(l.get(i).getNome())
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_battery_charging_full_34_dp));

            if (!isStoreOpen)
                marker2.alpha((float) .6);

            Marker marker = googleMap.addMarker(marker2);

            ListaMarker.getInstance().addMarker(marker);

        }

    }


}
