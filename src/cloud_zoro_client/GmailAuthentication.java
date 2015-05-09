/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cloud_zoro_client;

import java.util.Properties;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

/**
 *
 * @author Kitty
 */
public class GmailAuthentication implements Constants {

    static Session session;
    
    public static Session authenticateGmailServer()
    {
        if(session==null)
        {
       Properties props = new Properties();
    	props.put("mail.smtp.host", "smtp.gmail.com");
    	props.put("mail.smtp.socketFactory.port", "465");
	props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
    	props.put("mail.smtp.auth", "true");
	props.put("mail.smtp.port", "465");

	session = Session.getDefaultInstance(props,new javax.mail.Authenticator() {

                @Override
		protected PasswordAuthentication getPasswordAuthentication(){
                    return new PasswordAuthentication(SENDER_EMAIL_ADDRESS,SENDER_EMAIL_PASSWORD);	}
	});
        }
        return session;
    }
}
