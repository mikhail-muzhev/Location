package com.example.mikhail.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private static final int ACCESS_COARSE_LOCATION = 1500;
    private static final int SMS_PERMISSION_CODE = 1501;

    private static MainActivity activity;
    private TextView mTextView;
    private TextView mTextView2;
    public static TelephonyManager tm;
    private List<CellInfo> neighboringCellInfoList;
    private SmsListener listener;


    public static MainActivity instance() {
        return activity;
    }

    @Override
    protected void onStart() {
        super.onStart();
        activity = this;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestReadAndSendSmsPermission();
        int permissionCheck = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_COARSE_LOCATION);
        } else {
            startEverything();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startEverything();
                } else {
                    System.out.println("Waisted");
                }
                return;
            }
            case SMS_PERMISSION_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    listener = new SmsListener();
                } else {
                    System.out.println("Waisted");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void startEverything() {
        TelephonyManager mTelephMgr=(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        List<CellInfo> mList = new ArrayList<>();
        mTextView = (TextView) findViewById(R.id.text1 );
        mTextView2 = (TextView) findViewById(R.id.text2 );
        mTextView.setMovementMethod(new ScrollingMovementMethod());

        mTextView2.setMovementMethod(new ScrollingMovementMethod());

        Button getCellsInfoBtn = (Button)findViewById(R.id.button1);
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);



        final CellLocation cellLocation= tm.getCellLocation();

        getCellsInfoBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                // Old method------------------------------------------
                List<NeighboringCellInfo> listNeighbors= tm.getNeighboringCellInfo();
                StringBuilder s = new StringBuilder();
                // New Method -----------------------------------------
                neighboringCellInfoList = tm.getAllCellInfo();

                for(int i = 0; i<listNeighbors.size(); i++){
                    NeighboringCellInfo cell = listNeighbors.get(i);

                    s.append("mCid = " + cell.getCid() +
                            "\nLac = " + cell.getLac() +
                            "\nNetworkType = " + cell.getNetworkType() +
                            "\nPsc = " + cell.getPsc() +
                            "\nRssi = " + cell.getRssi() + "\n\n");
                }
                mTextView2.setText(s);





                String className = neighboringCellInfoList.get(0).getClass().getName();
                StringBuilder urlGETRequest = new StringBuilder();
                if( neighboringCellInfoList.get(0).getClass().getName().equals("android.telephony.CellInfoLte") ) {
                    CellInfoLte cellInfoLte = (CellInfoLte) neighboringCellInfoList.get(0);
                    urlGETRequest.append("http://xinit.ru/bs/");

                    urlGETRequest.append(cellInfoLte.getCellIdentity().getMcc());
                    urlGETRequest.append("-");
                    urlGETRequest.append(cellInfoLte.getCellIdentity().getMnc());
                    urlGETRequest.append("-");
                    urlGETRequest.append(cellInfoLte.getCellIdentity().getTac());
                    urlGETRequest.append("-");
                    urlGETRequest.append(cellInfoLte.getCellIdentity().getCi());
                   // urlGETRequest.append("&networkType=lte");

                }
                else if (neighboringCellInfoList.get(0).getClass().getName().equals("android.telephony.CellInfoGsm")){
                    CellInfoGsm cellInfoGsm = (CellInfoGsm) neighboringCellInfoList.get(0);
                    urlGETRequest.append("http://xinit.ru/bs/#!?mcc=");
                    cellInfoGsm.getCellSignalStrength();
                    urlGETRequest.append(cellInfoGsm.getCellIdentity().getMcc());
                    urlGETRequest.append("-");
                    urlGETRequest.append(cellInfoGsm.getCellIdentity().getMnc());
                    urlGETRequest.append("-");
                    urlGETRequest.append(cellInfoGsm.getCellIdentity().getLac());
                    urlGETRequest.append("-");
                    urlGETRequest.append(cellInfoGsm.getCellIdentity().getCid());
                }

                String url = urlGETRequest.toString();

                createWebView(url);


                mTextView.setText( cellLocation.toString() + "\n");


                for(CellInfo info : neighboringCellInfoList){
                    StringBuilder result = new StringBuilder();
                    result.append(info.isRegistered());
                    mTextView.append(info + "\n\n");

                }

            }
        });
    }

    private WebView mWebview ;

    private void createWebView(String url){
        mWebview  = new WebView(this);

        mWebview.getSettings().setJavaScriptEnabled(true); // enable javascript

        mWebview.getSettings().setDisplayZoomControls(true);

        mWebview.getSettings().setBuiltInZoomControls(true);
        final Activity activity = this;


        mWebview.setWebViewClient(new WebViewClient());


        mWebview.loadUrl(url);
        setContentView(mWebview );
    }

    private void requestReadAndSendSmsPermission() {

        boolean readnigRequired = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED;
        boolean sendnigRequired = ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED;
        boolean receivngRequired = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED;

        List<String> permissionsList = new ArrayList<String>();

        if (readnigRequired) {
            permissionsList.add(Manifest.permission.READ_SMS);
        }
        if (sendnigRequired) {
            permissionsList.add(Manifest.permission.SEND_SMS);
        }
        if (receivngRequired) {
            permissionsList.add(Manifest.permission.RECEIVE_SMS);
        }
        String[] permissions = permissionsList.toArray(new String[0]);

        if (readnigRequired || sendnigRequired || receivngRequired) {
            ActivityCompat.requestPermissions(this, permissions, SMS_PERMISSION_CODE);
        }
    }
}
