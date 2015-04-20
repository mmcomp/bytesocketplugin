import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import android.util.Log;
import android.provider.Settings;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import android.content.Context;
public class ByteSocketPlugin extends CordovaPlugin {
	public static IOClass ioclass;
	public static Boolean writeLogs = true; 
	public static String sent="";
	public static int[] b={};
	public static Boolean connected = false;
	public static String SockError = "";
	//	public static int[] sts;
	private static int SERVERPORT = 4514;
	private static  String SERVER_IP = "192.168.2.38";
	public static Boolean doConnect = true;
	public static String rec_data = "";
	public static CallbackContext cl;
/**
* Constructor.
*/
	public ByteSocketPlugin() {
	}
/**
* Sets the context of the Command. This can then be used to do things like
* get file paths associated with the Activity.
*
* @param cordova The context of the main Activity.
* @param webView The CordovaWebView Cordova is running in.
*/
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
	}
	public boolean execute(final String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		cl = callbackContext;
		cordova.getActivity().runOnUiThread(new Runnable() {
			public void run() {
				doConnect = false;
				String[] tmp = action.split(",");
				if(tmp[0].equals("send") && tmp.length>1)
				{
					ioclass = new IOClass(tmp[1],Integer.valueOf(tmp[2]));
					b = new int[tmp.length-3];
					for(int i = 3;i < tmp.length;i++)
						b[i-3] = Integer.valueOf(tmp[i]);
					new Thread(new ClientThread()).start();
				}
				else if(tmp[0].equals("send") && tmp.length==3)
				{
					ioclass = new IOClass(tmp[1],Integer.valueOf(tmp[2]));
					new Thread(new ClientThread()).start();
				}
			}
		});
		return true;
	}
	class ClientThread implements Runnable {
		@Override
		public void run() {
			try {
				ioclass.open();
				if(b.length > 0)
					rec_data = ioclass.sendSimpleData(b);
				SockError = ioclass.SockError;
				if(!SockError.equals(""))
					ioclass.close();
				cordova.getActivity().runOnUiThread(new Runnable() {
					public void run() {
						cl.success((SockError.equals(""))?"true|"+rec_data:SockError);
					}
				});
				connected = ioclass.connected;
				
			} catch (Exception e) {
				final String err = e.getMessage();
				cordova.getActivity().runOnUiThread(new Runnable() {
					public void run() {
						cl.success(err);
					}
				});			
			}
		}
	}



	class IOClass {
		Boolean isTcp = true;
		String SERVER_IP = "192.168.0.3";
		int SERVER_PORT = 43002;
		Boolean connected = false;
		Socket socket;
		String SockError="";
		Boolean crcok = false;
		int bfs = 65;
		Context mContext;
		
		public IOClass(String IPAddr,int port)
		{
			if(!IPAddr.equals(""))
				SERVER_IP = IPAddr;
			if(port > 0)
				SERVER_PORT = port;
		}
		public void open()
		{
			if(socket != null)
				connected = socket.isConnected();
			else
				connected = false;
			if(!connected)
			{
				try {
					InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
					socket = new Socket(serverAddr, SERVER_PORT);
					connected = true;
					SockError = "";
				} catch (UnknownHostException e) {
					SockError = "Open() UnknownHostException : "+e.getMessage();
				} catch (IOException e) {
					SockError = "Open() IOException : "+e.getMessage();
				} catch (Exception e) {
					SockError = "Open() Exception : "+e.getMessage();
				}
			}
		}
		public void close()
		{
			if(socket != null)
				connected = socket.isConnected();
			else
				connected = false;
			if(connected)
			{
				try {
					socket.close();
					connected = false;
					SockError = "";
				} catch (UnknownHostException e) {
					SockError = "close() UnknownHostException : "+e.getMessage();
				} catch (IOException e) {
					SockError = "close() IOException : "+e.getMessage();
				} catch (Exception e) {
					SockError = "close() Exception : "+e.getMessage();
				}
			}
		}
		public String sendSimpleData(int[] b)
		{
			byte[] tmp_sts = new byte[1];
			String tmp_sts_str = "";
			try {

				byte[] bb = new byte[b.length];
				for(int i = 0;i < b.length;i++)
					bb[i] = (byte)b[i];
				DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
				DataInputStream inFromServer = new DataInputStream(socket.getInputStream());
				outToServer.write(bb);
				int nread;
				while ((nread = inFromServer.read(tmp_sts)) >= 0) {
					//int intt = (int)tmp_sts[0];
					//tmp_sts_str += new String(new byte[] { tmp_sts[0] });
					tmp_sts_str += ((tmp_sts_str.equals(""))?"":",")+String.valueOf(tmp_sts[0]);
				}

			} catch (IOException e) {
				connected = false;
				SockError = "SendData() IOException : "+e.getMessage();
			} catch (Exception e) {
				connected = false;
				SockError = "SendData() Exception : "+e.getMessage();
			}
			return(tmp_sts_str);
		}
	}

}
