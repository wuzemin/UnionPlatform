package com.min.smalltalk.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.min.smalltalk.AppContext;
import com.min.smalltalk.R;
import com.min.smalltalk.bean.LocationEntity;
import com.min.smalltalk.listener.OnLocationGetListener;
import com.min.smalltalk.wedget.location.LocationTask;
import com.min.smalltalk.wedget.location.RegeocodeTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.message.LocationMessage;

public class AMAPLocationActivity extends ActionBarActivity implements OnLocationGetListener {

    @BindView(R.id.map)
    MapView mapView;
    @BindView(R.id.location)
    TextView tv_location;
    @BindView(R.id.myLocation)
    ImageView iv_enter;

    private AMap aMap;
    private LatLng myLocation = null;
    private Marker centerMarker;
    static public final int REQUEST_CODE_ASK_PERMISSIONS = 101;
    private LocationMessage mMsg;
    private LocationTask mLocationTask;
    private RegeocodeTask mRegeocodeTask;
    private boolean model = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amaplocation);
        ButterKnife.bind(this);
        mapView.onCreate(savedInstanceState);

        //android6.0 打开位置权限
        initPermission();
        initAmap();
    }

    private void initAmap() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }

        /**
         * 从通话界面进入
         */
        if (getIntent().hasExtra("location")) {
            mMsg = getIntent().getParcelableExtra("location");
            tv_location.setVisibility(View.GONE);
            iv_enter.setVisibility(View.GONE);
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mMsg.getLat(), mMsg.getLng()), 15));
            aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                    .position(new LatLng(mMsg.getLat(), mMsg.getLng())).title(mMsg.getPoi())
                    .snippet(mMsg.getLat() + "," + mMsg.getLng()).draggable(true));
            return;
        }

        //定位
        mLocationTask = LocationTask.getInstance(this);
        mLocationTask.setOnLocationGetListener(this);
        mRegeocodeTask = new RegeocodeTask(this);
        mRegeocodeTask.setOnLocationGetListener(this);

        //显示定位按钮
        aMap.setLocationSource(mLocationTask);
        /*aMap.getUiSettings().setMyLocationButtonEnabled(true);*/
        aMap.setMyLocationEnabled(true);
        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);

        //比例尺
        aMap.getUiSettings().setScaleControlsEnabled(true);
    }


    @OnClick(R.id.myLocation)
    public void onClick() {
        if (mMsg != null) {
            initPermission();
            AppContext.getInstance().getLastLocationCallback().onSuccess(mMsg);
            AppContext.getInstance().setLastLocationCallback(null);
            finish();
        } else {
            AppContext.getInstance().getLastLocationCallback().onFailure("定位失败");
        }
    }

    //android6.0 打开位置权限
    private void initPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkPermission = this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_ASK_PERMISSIONS);
                } else {
                    new AlertDialog.Builder(this)
                            .setMessage("您需要在设置了打开位置权限!")
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.M)
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                            REQUEST_CODE_ASK_PERMISSIONS);
                                }
                            })
                            .setNegativeButton("取消", null)
                            .create().show();

                }
                return;
            }

        }
    }

    @Override
    public void onLocationGet(LocationEntity entity) {
        tv_location.setText(entity.address);
        double latitude = entity.getLatitue();
        double longitude = entity.getLongitude();
        mMsg = LocationMessage.obtain(latitude, longitude, entity.address, getMapUrl(latitude, longitude));
    }

    private Uri getMapUrl(double x, double y) {
        String url = "http://restapi.amap.com/v3/staticmap?location=" + y + "," + x +
                "&zoom=16&scale=2&size=408*240&markers=mid,,A:" + y + ","
                + x + "&key=" + "ee95e52bf08006f63fd29bcfbcf21df0";
        Log.e("getMapUrl", url);
        return Uri.parse(url);
    }

    @Override
    public void onRegecodeGet(LocationEntity entity) {

    }


    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
