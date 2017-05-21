package com.example.mikhail.location;

import android.app.Activity;
import android.content.Context;
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



    private static MainActivity activity;
    private TextView mTextView;
    private TextView mTextView2;
    public static TelephonyManager TM;
    private List<CellInfo> neighboringCellInfoList;


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
        SmsListener listener = new SmsListener();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       TelephonyManager mTelephMgr=(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        List<CellInfo> mList = new ArrayList<>();
        mTextView = (TextView) findViewById(R.id.text1 );
        mTextView2 = (TextView) findViewById(R.id.text2 );
        mTextView.setMovementMethod(new ScrollingMovementMethod());

        mTextView2.setMovementMethod(new ScrollingMovementMethod());


        Button getCellsInfoBtn = (Button)findViewById(R.id.button1);
        TM = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        final CellLocation cellLocation=TM.getCellLocation();

        getCellsInfoBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                // Old method------------------------------------------
                List<NeighboringCellInfo> listNeighbors=TM.getNeighboringCellInfo();
                StringBuilder s = new StringBuilder();
                // New Method -----------------------------------------
                neighboringCellInfoList = TM.getAllCellInfo();

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
                    urlGETRequest.append("http://xinit.ru/bs/#!?mcc=");

                    urlGETRequest.append(cellInfoLte.getCellIdentity().getMcc());
                    urlGETRequest.append("&mnc=");
                    urlGETRequest.append(cellInfoLte.getCellIdentity().getMnc());
                    urlGETRequest.append("&lac=");
                    urlGETRequest.append(cellInfoLte.getCellIdentity().getTac());
                    urlGETRequest.append("&cid=");
                    urlGETRequest.append(cellInfoLte.getCellIdentity().getCi());
                    urlGETRequest.append("&networkType=lte");

                }
                else if (neighboringCellInfoList.get(0).getClass().getName().equals("android.telephony.CellInfoGsm")){
                    CellInfoGsm cellInfoGsm = (CellInfoGsm) neighboringCellInfoList.get(0);
                    urlGETRequest.append("http://xinit.ru/bs/#!?mcc=");
                    cellInfoGsm.getCellSignalStrength();
                    urlGETRequest.append(cellInfoGsm.getCellIdentity().getMcc());
                    urlGETRequest.append("&mnc=");
                    urlGETRequest.append(cellInfoGsm.getCellIdentity().getMnc());
                    urlGETRequest.append("&lac=");
                    urlGETRequest.append(cellInfoGsm.getCellIdentity().getLac());
                    urlGETRequest.append("&cid=");
                    urlGETRequest.append(cellInfoGsm.getCellIdentity().getCid());
                    urlGETRequest.append("&networkType=gsm");
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
}
