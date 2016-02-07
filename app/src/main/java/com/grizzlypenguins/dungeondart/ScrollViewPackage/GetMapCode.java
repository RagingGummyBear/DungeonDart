package com.grizzlypenguins.dungeondart.ScrollViewPackage;

import android.os.AsyncTask;
import android.util.Log;

import com.grizzlypenguins.dungeondart.Entities.DungeonDartDBHandler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;

/**
 * Created by Darko on 01.02.2016.
 */
public class GetMapCode extends AsyncTask {
    String msg;
    private int msgCount = 0;
    private LinkedList<String> stringBuffer = new LinkedList<>();

    public boolean finish = false;

    public synchronized void addString(String s)
    {
        stringBuffer.addLast(s);
        msgCount = stringBuffer.size();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            String saveMap = "saveMap-";
            int byteNumer = 16000 - saveMap.toCharArray().length;
//            char [] msgBytes = msg.toCharArray();
    //        float packetsNumber = msg.getBytes().length/byteNumer;
     //       int packetsNumber2 = msg.getBytes().length/byteNumer;

          //  if(packetsNumber > packetsNumber2)
           //     packetsNumber2++;
            StringBuilder bytes = new StringBuilder();
            String packet = "";
            Socket s = new Socket("testingout.ddns.net",25565);
            OutputStream os = s.getOutputStream();
            PrintWriter pw = new PrintWriter(os);


            while (true)
            {
               if(msgCount>0) {


                  if(stringBuffer.size()>0) pw.write(stringBuffer.removeFirst());
                   msgCount = stringBuffer.size();

               }
                if(finish && msgCount == 0){
                    pw.flush();
                    break;
                }
            }


       /*
            for(int i = 0 ;i < packetsNumber2 ; i++)

            {
                for(int y = 0; y < byteNumer; y++)
                {
                   if((i*byteNumer)+y < msgBytes.length ) bytes.append(msgBytes[(i*byteNumer)+y]);
                    else
                   {
                       System.out.println("Banana");
                   }
                }
                packet = saveMap + bytes.toString();
                pw.write(packet);
                bytes = new StringBuilder();
            }

          //  pw.write(msg);

            dout.writeUTF("endSaveMap-hey");
            */


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
