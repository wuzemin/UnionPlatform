package com.min.smalltalk.wedget.location;

import android.content.Context;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.min.smalltalk.bean.LocationEntity;
import com.min.smalltalk.listener.OnLocationGetListener;

/**
 * Created by Min on 2016/12/22.
 * 封装 地理编码
 */
public class RegeocodeTask implements GeocodeSearch.OnGeocodeSearchListener {
    private static final float SEARCH_RADIUS = 50;
    private OnLocationGetListener mOnLocationGetListener;

    private GeocodeSearch mGeocodeSearch;

    public RegeocodeTask(Context context) {
        mGeocodeSearch = new GeocodeSearch(context);
        mGeocodeSearch.setOnGeocodeSearchListener(this);
    }

    public void search(double latitude, double longitude) {
        RegeocodeQuery regecodeQuery = new RegeocodeQuery(new LatLonPoint(
                latitude, longitude), SEARCH_RADIUS, GeocodeSearch.AMAP);
        mGeocodeSearch.getFromLocationAsyn(regecodeQuery);
    }

    public void setOnLocationGetListener(
            OnLocationGetListener onLocationGetListener) {
        mOnLocationGetListener = onLocationGetListener;
    }

    @Override
    public void onGeocodeSearched(GeocodeResult arg0, int arg1) {

    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeReult, int resultCode) {
        if (resultCode == 0) {
            if (regeocodeReult != null
                    && regeocodeReult.getRegeocodeAddress() != null
                    && mOnLocationGetListener != null) {
                String address = regeocodeReult.getRegeocodeAddress()
                        .getFormatAddress();
                LocationEntity entity = new LocationEntity();
                entity.address = address;
                mOnLocationGetListener.onRegecodeGet(entity);

            }
        }
    }
}
