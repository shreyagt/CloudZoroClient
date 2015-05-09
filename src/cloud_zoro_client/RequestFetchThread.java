/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cloud_zoro_client;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;


/**
 *
 * @author Kitty
 */
public class RequestFetchThread implements Runnable,Constants {

    ArrayList<RequestObject> Request_List;
    InputStream in = null;
    BufferedReader br=null;
    String Received_Msg=null;
    RequestObject robject;



    public RequestFetchThread()
    {
        this.Request_List=new ArrayList<RequestObject>();
    }


    public void run() 
    {
       try 
       {
           while(true)
           {

               Thread.sleep(REQUEST_FETCH_TIMER);
            HttpClient client = new HttpClient();
            PostMethod method = new PostMethod(REQUEST_FETCH_URL);

            //Add any parameter if u want to send it with Post req.
            method.addParameter("project_id",CLOUD_PROJECT_NUMBER);

            int statusCode = client.executeMethod(method);

            if (statusCode != -1)
            {
                in = method.getResponseBodyAsStream();
                br=new BufferedReader(new InputStreamReader(in));
                Received_Msg="";
                int entered=0;
                String msg=br.readLine();
                while(msg!=null)
                {
                    if(entered==0)
                    {
                    Received_Msg=msg+"\n";
                    entered=1;
                    }
                    else
                    {
                    Received_Msg=Received_Msg+msg+"\n";
                    }
                    msg=br.readLine();
                }

                br.close();
                Received_Msg=Received_Msg.substring(0,Received_Msg.lastIndexOf("\n"));
                System.out.println(Received_Msg);
                if(Received_Msg.contains("^"))
                {
                    
                   StringTokenizer st=new StringTokenizer(Received_Msg,"~");
                   while(st.hasMoreTokens())
                   {
                      if(Request_List.size()<st.countTokens())
                       {
                          
                      String token=st.nextToken();
                      StringTokenizer stt1=new StringTokenizer(token,"^");
                      String reg_id=stt1.nextToken();
                      String email_id=stt1.nextToken();
                      String filenames=stt1.nextToken();
                      StringTokenizer stt2=new StringTokenizer(filenames,",");
                      while(stt2.hasMoreTokens())
                      {
                          
                        String filename=stt2.nextToken();
                       robject=new RequestObject(reg_id,email_id,filename);
                           
                       if(!Request_List.contains(robject))
                       {
                           
                       Frame1.Request_Table_Model.insertRow(Frame1.Request_Table_Model.getRowCount(),new String[]{robject.REGISTRATION_ID,robject.EMAIL_ID,robject.FILENAME});
                       new MyMailSendingThread(email_id,filename,reg_id).start();
                       Request_List.add(robject);
                      
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
                 System.out.println("Server Not Found");
            }

           }

        } 
        catch (Exception e)
        {
            System.out.println("Error in Receiving Thread:"+e.getLocalizedMessage());
        }
        
    }

   

}
