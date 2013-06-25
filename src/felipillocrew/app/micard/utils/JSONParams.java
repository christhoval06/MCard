package felipillocrew.app.micard.utils;

import java.util.ArrayList;

import org.apache.http.NameValuePair;

public class JSONParams {
    public String url, type;
    public ArrayList<NameValuePair> data;
    public JSONParams(String url, ArrayList<NameValuePair> data) {
		this.url=url;
    	this.data=data;
    	this.type="json";
	}
    public JSONParams(String url, ArrayList<NameValuePair> data, String type) {
		this.url=url;
    	this.data=data;
    	this.type=type;
	}
}
