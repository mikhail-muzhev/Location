package com.example.mikhail.location;

import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.TelephonyManager;

import java.util.List;




public class Location {

    private static List<CellInfo> neighboringCellInfoList;

    public static String makeURLFromLocation(TelephonyManager TM){


        StringBuilder s = new StringBuilder();
        // New Method -----------------------------------------
        neighboringCellInfoList = TM.getAllCellInfo();

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
        return urlGETRequest.toString();
    }

}
