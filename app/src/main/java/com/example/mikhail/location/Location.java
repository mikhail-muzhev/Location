package com.example.mikhail.location;

import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.TelephonyManager;

import java.util.List;




public class Location {

    private static List<CellInfo> neighboringCellInfoList;

    public static String makeURLFromLocation(TelephonyManager tm){


        StringBuilder s = new StringBuilder();
        // New Method -----------------------------------------
        neighboringCellInfoList = tm.getAllCellInfo();

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

        }
        else if (neighboringCellInfoList.get(0).getClass().getName().equals("android.telephony.CellInfoGsm")){
            CellInfoGsm cellInfoGsm = (CellInfoGsm) neighboringCellInfoList.get(0);
            urlGETRequest.append("http://xinit.ru/bs/");
            cellInfoGsm.getCellSignalStrength();
            urlGETRequest.append(cellInfoGsm.getCellIdentity().getMcc());
            urlGETRequest.append("-");
            urlGETRequest.append(cellInfoGsm.getCellIdentity().getMnc());
            urlGETRequest.append("-");
            urlGETRequest.append(cellInfoGsm.getCellIdentity().getLac());
            urlGETRequest.append("-");
            urlGETRequest.append(cellInfoGsm.getCellIdentity().getCid());
        }
        return urlGETRequest.toString();
    }

}
