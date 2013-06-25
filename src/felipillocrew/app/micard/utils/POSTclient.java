package felipillocrew.app.micard.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class POSTclient extends AsyncTask<JSONParams, Void, JSONObject>{
	
	ProgressDialog progressDialog ;
    JSONListener getJSONListener;
    Context curContext;
    String msg = "Cargando...";
    
    public POSTclient(Context context, JSONListener listener){
        this.getJSONListener = listener;
        curContext = context;
    }
    
    public POSTclient(Context context, JSONListener listener, String msg){
        this.getJSONListener = listener;
        curContext = context;
        if (msg != null){
        	this.msg = msg;
        }
    }
    
    private static String convertStreamToString(InputStream is, String type) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
            	if (type.equalsIgnoreCase("JSON")) sb.append(line + "\n");
            	else sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
    
    public static JSONObject connect(String url, ArrayList<NameValuePair> data, String type) throws JSONException
    {
    	JSONObject json = null;
    	try {
    		
    		/*HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
            HttpConnectionParams.setSoTimeout(httpParams, 3000); */
            
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
                        
            httpPost.setEntity(new UrlEncodedFormEntity(data));
            
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            
            if (entity != null) {
            	 
                // A Simple JSON Response Read
                InputStream instream = entity.getContent();
                String result= convertStreamToString(instream, type);
 
                // A Simple JSONObject Creation
                if (type.equalsIgnoreCase("JSON")) json = new JSONObject(result);
                
                // Closing the input stream will trigger connection release
                instream.close();
                if (type.equalsIgnoreCase("JSON")) return json;
                else {
                	String all = result.toString();
                	json = new JSONObject();
                	json.put("success", true);
                	json.put("html", all);
                	return json;
                }
            }
 
        } catch (SocketTimeoutException e)
        {
            e.printStackTrace();
            return new JSONObject("{success: false, text:\"error de conexión\"}");
        }catch (ConnectTimeoutException e)
        {
            e.printStackTrace();
            return new JSONObject("{success: false, text:\"error de conexión\"}");
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new JSONObject("{success: false, text:\"No se puco conectar\"}");
        }
    	catch (HttpHostConnectException e) {
    		e.printStackTrace();
    		return new JSONObject("{success: false, text:\"No se puco conectar con el servidor\"}");
		}
    	catch (ClientProtocolException e) {
            e.printStackTrace();
            return new JSONObject("{success: false, text:\"Ocurrio un Error con el Protocolo\"}");
        } catch (UnknownHostException e) {
			e.printStackTrace();
			return new JSONObject("{success: false, text:\"Servidor Desconocido\"}");
		} catch (JSONException e) {
			e.printStackTrace();
			return new JSONObject("{success: false, text:\"Ocurrio un Error :JSON\"}");
		}catch (IOException e) {
			e.printStackTrace();
			return new JSONObject("{success: false, text:\"Ocurrio un Error\"}");
		}
        return null;
    }
    
    @Override
    public void onPreExecute() {
        progressDialog = new ProgressDialog(curContext);
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
 
    }
    
    @Override
    protected JSONObject doInBackground(JSONParams... data) {
    	String url=data[0].url;
    	String type=data[0].type;
    	ArrayList<NameValuePair> params = data[0].data;
        try {
			return connect(url, params, type);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
    }
    
    @Override
    protected void onPostExecute(JSONObject json ) {
    	getJSONListener.onRemoteCallComplete(json);
        progressDialog.dismiss();
    }

}
