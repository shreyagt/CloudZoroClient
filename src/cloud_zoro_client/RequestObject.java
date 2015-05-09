/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cloud_zoro_client;

/**
 *
 * @author Kitty
 */
public class RequestObject
{
    public String REGISTRATION_ID;
    public String FILENAME;
    public String EMAIL_ID;

    public RequestObject(String registration_id,String email_id,String filename)
    {
        this.REGISTRATION_ID=registration_id;
        this.EMAIL_ID=email_id;
        this.FILENAME=filename;
       
    }

}
