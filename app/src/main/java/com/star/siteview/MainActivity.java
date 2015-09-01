package com.star.siteview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
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

    public static final float ONE_HUNDRED_METER = 0.001f;
    public static final float LNG_LAT_RATIO = 1.15625f; //111 / 96 即0.001在上海对应经度110米，纬度96米
    public static final float HALF_ANGLE = 15;

    private MapView mMapView;
    private BaiduMap mBaiduMap;

    private BitmapDescriptor mBitmapDescriptor;

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

    }

    private List<LatLng> calculatePoints(LatLng baiduLatLng, float rotate) {
        List<LatLng> points = new ArrayList<>();

        LatLng left = new LatLng(
                baiduLatLng.latitude +
                        ONE_HUNDRED_METER * Math.cos(Math.toRadians(-rotate - HALF_ANGLE)),
                baiduLatLng.longitude +
                        ONE_HUNDRED_METER * Math.sin(Math.toRadians(-rotate - HALF_ANGLE)) *
                                LNG_LAT_RATIO);

        LatLng right = new LatLng(
                baiduLatLng.latitude +
                        ONE_HUNDRED_METER * Math.sin(Math.toRadians(90 - (-rotate) - HALF_ANGLE)),
                baiduLatLng.longitude +
                        ONE_HUNDRED_METER * Math.cos(Math.toRadians(90 - (-rotate) - HALF_ANGLE)) *
                                LNG_LAT_RATIO);

        points.add(baiduLatLng);
        points.add(left);
        points.add(right);

        return points;
    }

}
