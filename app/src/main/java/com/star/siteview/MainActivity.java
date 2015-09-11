package com.star.siteview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final float ONE_HUNDRED_METER = 100;
    public static final float LATITUDE_PER_METER = 0.001f / 110;//上海，纬度0.001为110米
    public static final float LONGITUDE_PER_METER = 0.001f / 95; //上海，经度0.001为95米
    public static final float HALF_ANGLE = 15;

    private MapView mMapView;
    private BaiduMap mBaiduMap;

    private BitmapDescriptor mBitmapDescriptor;

    /** Unknown network class. {@hide} */
    public static final int NETWORK_CLASS_UNKNOWN = 0;
    /** Class of broadly defined "2G" networks. {@hide} */
    public static final int NETWORK_CLASS_2_G = 1;
    /** Class of broadly defined "3G" networks. {@hide} */
    public static final int NETWORK_CLASS_3_G = 2;
    /** Class of broadly defined "4G" networks. {@hide} */
    public static final int NETWORK_CLASS_4_G = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.activity_main);

        mMapView = (MapView) findViewById(R.id.map_view);
        mBaiduMap = mMapView.getMap();

        initOverlay();
    }

    private void initOverlay() {
        mBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.sector);

        LatLng latLng = new LatLng(31.337937, 121.220206);

        CoordinateConverter coordinateConverter = new CoordinateConverter();

        LatLng baiduLatLng = coordinateConverter
                .coord(latLng)
                .from(CoordinateConverter.CoordType.GPS)
                .convert();

        MapStatus mapStatus = new MapStatus.Builder()
                .target(baiduLatLng)
                .zoom(16)
                .build();

        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);

        mBaiduMap.setMapStatus(mapStatusUpdate);
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);

        OverlayOptions overlayOptionsA = new MarkerOptions()
                .position(baiduLatLng)
                .icon(mBitmapDescriptor)
                .rotate(0);

//        mBaiduMap.addOverlay(overlayOptionsA);

        OverlayOptions overlayOptionsB = new MarkerOptions()
                .position(baiduLatLng)
                .icon(mBitmapDescriptor)
                .rotate(-60);

//        mBaiduMap.addOverlay(overlayOptionsB);

        OverlayOptions overlayOptionsC = new MarkerOptions()
                .position(baiduLatLng)
                .icon(mBitmapDescriptor)
                .rotate(-120);

//        mBaiduMap.addOverlay(overlayOptionsC);

        OverlayOptions overlayOptionsD = new PolygonOptions()
                .fillColor(Color.RED)
                .points(calculatePoints(baiduLatLng, -30));

        mBaiduMap.addOverlay(overlayOptionsD);

        OverlayOptions overlayOptionsE = new PolygonOptions()
                .fillColor(Color.RED)
                .points(calculatePoints(baiduLatLng, -90));

        mBaiduMap.addOverlay(overlayOptionsE);

        OverlayOptions overlayOptionsF = new PolygonOptions()
                .fillColor(Color.BLUE)
                .points(calculatePoints(baiduLatLng, -280));

        mBaiduMap.addOverlay(overlayOptionsF);

        OverlayOptions overlayOptionsG = new TextOptions()
                .position(baiduLatLng)
                .fontColor(Color.YELLOW)
                .fontSize(36)
                .text("vip俱乐部");

        mBaiduMap.addOverlay(overlayOptionsG);

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

                int networkType = telephonyManager.getNetworkType();

                CellLocation cellLocation = telephonyManager.getCellLocation();

                List<CellInfo> cellInfos = telephonyManager.getAllCellInfo();

                int lac = 0;
                int ci = 0;
                int dbm = 0;

                switch (getNetworkClass(networkType)) {
                    case NETWORK_CLASS_2_G:
                        ci = ((GsmCellLocation) cellLocation).getCid();
                        lac = ((GsmCellLocation) cellLocation).getLac();

                        CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfos.get(0);
                        dbm = cellInfoGsm.getCellSignalStrength().getDbm();
                        break;

                    case NETWORK_CLASS_4_G:
                        ci = ((GsmCellLocation) cellLocation).getCid();
                        ci = Integer.valueOf(Integer.toHexString(ci).substring(3), 16);
                        lac = ((GsmCellLocation) cellLocation).getLac();

                        CellInfoLte cellInfoLte = (CellInfoLte) cellInfos.get(0);
                        dbm = cellInfoLte.getCellSignalStrength().getDbm();
                        break;

                    default:
                        ci = ((GsmCellLocation) cellLocation).getCid();
                        lac = ((GsmCellLocation) cellLocation).getLac();

                        cellInfoGsm = (CellInfoGsm) cellInfos.get(0);
                        dbm = cellInfoGsm.getCellSignalStrength().getDbm();
                        break;
                }

                Toast.makeText(getBaseContext(), "lac: " + lac + "\n" + "ci: " + ci + "\n" +
                        "dbm: " + dbm, Toast.LENGTH_LONG).show();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });



    }

    private List<LatLng> calculatePoints(LatLng baiduLatLng, float rotate) {
        List<LatLng> points = new ArrayList<>();

        LatLng left = new LatLng(
                baiduLatLng.latitude +
                        ONE_HUNDRED_METER *
                                Math.cos(Math.toRadians(-rotate - HALF_ANGLE)) *
                                LATITUDE_PER_METER,
                baiduLatLng.longitude +
                        ONE_HUNDRED_METER *
                                Math.sin(Math.toRadians(-rotate - HALF_ANGLE)) *
                                LONGITUDE_PER_METER);

        LatLng right = new LatLng(
                baiduLatLng.latitude +
                        ONE_HUNDRED_METER *
                                Math.sin(Math.toRadians(90 - (-rotate) - HALF_ANGLE)) *
                                LATITUDE_PER_METER,
                baiduLatLng.longitude +
                        ONE_HUNDRED_METER *
                                Math.cos(Math.toRadians(90 - (-rotate) - HALF_ANGLE)) *
                                LONGITUDE_PER_METER);

        points.add(baiduLatLng);
        points.add(left);
        points.add(right);

        return points;
    }

    private int getNetworkClass(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NETWORK_CLASS_2_G;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return NETWORK_CLASS_3_G;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return NETWORK_CLASS_4_G;
            default:
                return NETWORK_CLASS_UNKNOWN;
        }
    }

}
