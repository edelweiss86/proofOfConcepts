package com.various.techniques;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

public final class NetworkInterfaces {

    public List<String> getInterfaces()  {

        try {
            NetworkInterface nf;
            List<String> stringList = new LinkedList<String>();
            for (Enumeration e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements(); ) {
                nf = (NetworkInterface) e.nextElement();
                if (nf.getHardwareAddress() != null) {
                    byte[] mac = nf.getHardwareAddress();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));
                    }
                    stringList.add(nf.getDisplayName()+ " -> " +sb.toString());
                }

            }
            return  stringList;
        }
        catch (Exception e){e.printStackTrace();}
        return  null;

    }
}
