/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cloud_zoro_client;

import java.io.InputStream;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author Kitty,m563
 * 
 */
public class ClientFetchThread implements Runnable, Constants {
     static ArrayList<String> client_list;
    InputStream in = null;
    BufferedReader br=null;
    String Received_Msg=null;

     public ClientFetchThread()
     {
         client_list=new ArrayList<String>();
     }

    public void run() {


        InputStream in = null;

        try {
            while(true)
            {
            Thread.sleep(CLIENT_LIST_FETCH_TIMER);
            HttpClient client = new HttpClient();
            PostMethod method = new PostMethod(CLIENTS_LIST_FETCH_URL);

            //Add any parameter if u want to send it with Post req.
            method.addParameter("project_id", CLOUD_PROJECT_NUMBER);

            int statusCode = client.executeMethod(method);

            if (statusCode != -1)
            {
              in = method.getResponseBodyAsStream();
                br=new BufferedReader(new InputStreamReader(in));
                Received_Msg="";
                String msg=br.readLine();
                while(msg!=null)
                {
                    Received_Msg=Received_Msg+msg;
                    msg=br.readLine();
                }
                br.close();


                if(Received_Msg.contains("^"))
                {
                    StringTokenizer stt=new StringTokenizer(Received_Msg,"~");
                    while(stt.hasMoreTokens())
                    {
                         if(client_list.size()<stt.countTokens())
                       {
                             String token=stt.nextToken();
                             if(!client_list.contains(token))
                       {
                                  client_list.add(token);
                   StringTokenizer st=new StringTokenizer(token,"^");

                   while(st.hasMoreTokens())
                   {
                      
                               
                          
                      
                       Frame1.Connection_Table_Model.insertRow(Frame1.Connection_Table_Model.getRowCount(),new String[]{st.nextToken(),st.nextToken()});
                           
                       
                       
                   }
                       }
                        }
                }
                }
               else
                {
                   System.out.println(Received_Msg);
                }
            }
            else
            {
                 System.out.println("Invalid Message");
            }
           
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

  
}
