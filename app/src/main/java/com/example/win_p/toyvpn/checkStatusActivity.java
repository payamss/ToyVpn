package com.example.win_p.toyvpn;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.RouteInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class checkStatusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_status);

        ArrayAdapter adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,getItems());
        ListView listView = (ListView)findViewById(R.id.list);
        listView.setAdapter(adapter);
    }

    public ArrayList<String> getItems(){
        ArrayList<String> displayMessage = new ArrayList<>();

        /* for get IPs */
        displayMessage.add("IPs");
        try{
            for(Enumeration<NetworkInterface> networkInterfaceEnum = NetworkInterface.getNetworkInterfaces();
                    networkInterfaceEnum.hasMoreElements();)
            {
                NetworkInterface networkInterface = networkInterfaceEnum.nextElement();
                String ifName = networkInterface.getName();
                String ifDispName = networkInterface.getDisplayName();
                for(Enumeration<InetAddress> ipAddressEnum = networkInterface.getInetAddresses();
                        ipAddressEnum.hasMoreElements();)
                {
                    InetAddress inetAddress = (InetAddress) ipAddressEnum.nextElement();
                    displayMessage.add(ifName + ":" + ifDispName + ":" + inetAddress.getHostAddress());
                }
            }
        }catch (SocketException ex){
            Log.d("getItems",ex.toString());
        }

        displayMessage.add("Routes");
        /* for get Routes */
        /* for over API level 20
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = cm.getAllNetworks();
        for(Network network: networks)
        {
            LinkProperties linkProperties = cm.getLinkProperties(network);
            List<RouteInfo> routeInfoList = linkProperties.getRoutes();
            for(RouteInfo ri: routeInfoList)
            {
                displayMessage.add(ri.toString());
            }
        }*/

        /* for under API level 19 */
        try { //for IPv4
            // Executes the command.
            Process process = Runtime.getRuntime().exec("/system/bin/ip r");

            // Reads stdout.
            // NOTE: You can write to stdin of the command using
            //       process.getOutputStream().
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();

            String outputs;
            while ((outputs = reader.readLine()) != null)//1行単位で取り出す繰り返し
            {
                displayMessage.add(outputs);//1行表示
                Log.d("route",outputs);
            }

            reader.close();

            // Waits for the command to finish.
            process.waitFor();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try { //for IPv6
            // Executes the command.
            Process process = Runtime.getRuntime().exec("/system/bin/ip -6 r");

            // Reads stdout.
            // NOTE: You can write to stdin of the command using
            //       process.getOutputStream().
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();

            String outputs;
            while ((outputs = reader.readLine()) != null)//1行単位で取り出す繰り返し
            {
                displayMessage.add(outputs);//1行表示
                Log.d("route",outputs);
            }
            /*
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }*/
            reader.close();

            // Waits for the command to finish.
            process.waitFor();

            //return output.toString();
            //displayMessage.add(output.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        displayMessage.add("Rule and Routes");
        displayMessage.addAll(getRoutes());
        displayMessage.add("Rule6 and Routes6");
        displayMessage.addAll(getRoutes6());
        return displayMessage;
    }

    public ArrayList<String> getRoutes(){
        ArrayList<String> ruleList = new ArrayList<>();
        ArrayList<String> routeList = new ArrayList<>();

        try { //ip rule list
            // Executes the command.
            Process process = Runtime.getRuntime().exec("/system/bin/ip rule list");

            // Reads stdout.
            // NOTE: You can write to stdin of the command using
            //       process.getOutputStream().
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String outputs;
            while ((outputs = reader.readLine()) != null)//1行単位で取り出す繰り返し
            {
                routeList.add(outputs);//1行表示
                Log.d("rule",outputs);
                String[] result = outputs.split("[\\s]+");
                //Log.d("rule",result[result.length - 1]);
                ruleList.add(result[result.length -1 ]);
            }

            reader.close();

            // Waits for the command to finish.
            process.waitFor();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        try { //ip route show table xxxx
            // Executes the command.
            for(String rule: ruleList)
            {
                routeList.add("Rule Table " + rule);

                Process process = Runtime.getRuntime().exec("/system/bin/ip route show table " + rule);

                // Reads stdout.
                // NOTE: You can write to stdin of the command using
                //       process.getOutputStream().
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
                String outputs;
                while ((outputs = reader.readLine()) != null)//1行単位で取り出す繰り返し
                {
                    routeList.add(outputs);//1行表示
                    Log.d("rule",outputs);
                }

                reader.close();

                // Waits for the command to finish.
                process.waitFor();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return routeList;
    }
    public ArrayList<String> getRoutes6(){
        ArrayList<String> ruleList = new ArrayList<>();
        ArrayList<String> routeList = new ArrayList<>();

        try { //ip rule list
            // Executes the command.
            Process process = Runtime.getRuntime().exec("/system/bin/ip -6 rule list");

            // Reads stdout.
            // NOTE: You can write to stdin of the command using
            //       process.getOutputStream().
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String outputs;
            while ((outputs = reader.readLine()) != null)//1行単位で取り出す繰り返し
            {
                routeList.add(outputs);//1行表示
                Log.d("rule",outputs);
                String[] result = outputs.split("[\\s]+");
                //Log.d("rule",result[result.length - 1]);
                ruleList.add(result[result.length -1 ]);
            }

            reader.close();

            // Waits for the command to finish.
            process.waitFor();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        try { //ip route show table xxxx
            // Executes the command.
            for(String rule: ruleList)
            {
                routeList.add("Rule Table " + rule);

                Process process = Runtime.getRuntime().exec("/system/bin/ip -6 route show table " + rule);

                // Reads stdout.
                // NOTE: You can write to stdin of the command using
                //       process.getOutputStream().
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
                String outputs;
                while ((outputs = reader.readLine()) != null)//1行単位で取り出す繰り返し
                {
                    routeList.add(outputs);//1行表示
                    Log.d("rule",outputs);
                }

                reader.close();

                // Waits for the command to finish.
                process.waitFor();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return routeList;
    }
}
