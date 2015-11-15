import java.rmi.*;
import java.io.*;
import java.net.MalformedURLException;

import javax.crypto.*;
import javax.crypto.spec.*;

    
public class cwclient implements Serializable
{

    final static int STUDENT_ID = 33371415;
    final static int PASSWORD = 1070317006;
    final static String SERV_ADDR = "scc311-server.lancs.ac.uk";
    final static String SERV_NAME = "CW_server";
    final static String FILE_PATH = "Specification.doc";
    final static String KEY_PATH = "33371415.key";

    public static void main(String[] args)
    {
        
        CW_server_interface c;
        Client_request cq = new Client_request(STUDENT_ID, 8, PASSWORD);
        Server_response sr;
        Object obj;
        
        try{
            c = (CW_server_interface) Naming.lookup("rmi://" + SERV_ADDR + "/" + SERV_NAME);
            //Password Authentication
            // sr = c.getSpec(STUDENT_ID, cq);
            // write(FILE_PATH, sr);


            //Key based authentication
            try{
                FileInputStream fis = new FileInputStream(KEY_PATH);
                ObjectInputStream ois = new ObjectInputStream(fis);
                obj = (Object) ois.readObject();
                ois.close();  
                Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
                SecretKey skey = (SecretKey) obj;
                cipher.init(Cipher.ENCRYPT_MODE, skey);

                SealedObject so = new SealedObject(cq, cipher);
                so = c.getSpec(STUDENT_ID, so);

                sr = (Server_response) so.getObject(skey);
                write(FILE_PATH, sr);

            }catch(Exception e){
                System.out.println("Failed to read file " + e);
            }

        }
        catch (MalformedURLException murle) {
            System.out.println();
            System.out.println("MalformedURLException");
            System.out.println(murle);
        }
        catch (RemoteException re) {
            System.out.println();
            System.out.println("RemoteException");
            System.out.println(re);
        }
        catch (NotBoundException nbe) {
            System.out.println();
            System.out.println("NotBoundException");
            System.out.println(nbe);
        }
        catch (java.lang.ArithmeticException ae) {
            System.out.println();
            System.out.println("java.lang.ArithmeticException");
            System.out.println(ae);
        }
    }

    public static void write(String path, Server_response sr){
        File file;
        OutputStream stream;

        file = new File(path);
        try{
            stream = new FileOutputStream(file);
            sr.write_to(stream);
            stream.close();
        }catch(Exception ex){
            System.out.println("Failed to write file.");
        }

    }

}