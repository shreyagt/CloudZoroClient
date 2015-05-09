/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cloud_zoro_client;


import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import java.util.ArrayList;
import java.util.List;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.InputStream;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;

/**
 *
 * @author Rolden
 */
public class MyMailSendingThread extends Thread implements Runnable,Constants {

    String email_id,filename,reg_id;
    
    public MyMailSendingThread(String email_id,String filename,String cloud_reg_id)
    {
        this.email_id=email_id;
        this.filename=filename;
        this.reg_id=cloud_reg_id;
    }

    @Override
    public void run()
    {

               
synchronized(this)
        {
		try {
                        javax.mail.Message message = new MimeMessage(GmailAuthentication.authenticateGmailServer());
			message.setFrom(new InternetAddress(this.email_id));
			message.setRecipients(javax.mail.Message.RecipientType.TO,InternetAddress.parse( this.email_id));
			message.setSubject(this.filename);
			//message.setText(txt3.getText());

                                // Create the message part
  MimeBodyPart messageBodyPart = new MimeBodyPart();

  // Fill the message
  messageBodyPart.setText("Chk Attachment");


  

  // Part two is attachment
  MimeBodyPart messageBodyPart2 = new MimeBodyPart();
  FileDataSource source = new FileDataSource(FILE_CUTTER_PATH+"\\"+this.filename);
  messageBodyPart2.setDataHandler(new DataHandler(source));
  messageBodyPart2.setFileName(this.filename);
  
  Multipart multipart = new MimeMultipart();
  multipart.addBodyPart(messageBodyPart);
  multipart.addBodyPart(messageBodyPart2);

  // Put parts in message
  message.setContent(multipart);
  System.out.println("Received_Msg");



			Transport.send(message);
                        
                         System.out.println("Received_Msg");

			System.out.println("Done");
                         sendAlerttoCloud(this.reg_id,this.filename);
                        // sendMessage(this.REGISTRATION_ID);
                           System.out.println("Received_Msg");
            deleteRequestFromServer();
             System.out.println("Received_Msg");

		} catch (MessagingException e) {
			System.out.println("Mail Sending Error:"+e.getLocalizedMessage());
		}
    }
    }



 public void deleteRequestFromServer()
    {

        InputStream in = null;

        try {
            while(true)
            {
            Thread.sleep(CLIENT_LIST_FETCH_TIMER);
            HttpClient client = new HttpClient();
            PostMethod method = new PostMethod(CLIENTS_LIST_FETCH_URL);

            //Add any parameter if u want to send it with Post req.
            method.addParameter("project_id",CLOUD_PROJECT_NUMBER);
            method.addParameter("reg_id",this.reg_id);
            method.addParameter("email",this.email_id);

            int statusCode = client.executeMethod(method);

            if (statusCode != -1)
            {
             System.out.println("Data Deleted from Server");

            }
            else
            {
                 System.out.println("Invalid Response from the Server");
            }

            }
        } catch (Exception e) {
             System.out.println("Error Deleting Data from Server");
        }
    }

      public void sendAlerttoCloud(String reg_id,String filename) {
        try {
            System.out.println("Filename:"+filename);
            Sender sender = new Sender(SERVER_ACCESS_KEY);
            Message message = new Message.Builder().collapseKey(""+System.currentTimeMillis()).timeToLive(2419200).delayWhileIdle(true).addData("message",filename).build();
            ArrayList<String> arr = new ArrayList<String>();
            arr.add(reg_id);
            MulticastResult result = sender.send(message, arr, 5);
            List<Result> results = result.getResults();
            for (int i = 0; i < results.size(); i++) {
                Result result1 = results.get(i);

                if (result1.getMessageId() != null) {
                    // Success
                    String canonicalRegId = result1.getCanonicalRegistrationId();
                    if (canonicalRegId != null) {
                        // same device has more than one registration id.Update it
                        System.out.println("canonical:" + canonicalRegId);
                    }
                } else {
                    // Error occurred
                    String error = result1.getErrorCodeName();
                    System.out.println("Error:" + error);
                }
            }
        } catch (Exception e) {
            System.out.println("Error Sending Alert to Cloud"+e.getLocalizedMessage());
        }
    }
}
