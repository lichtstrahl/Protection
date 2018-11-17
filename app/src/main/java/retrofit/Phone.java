package retrofit;

import com.google.gson.annotations.SerializedName;

public class Phone{

	@SerializedName("id")
	private int id;

	@SerializedName("android_id")
	private String androidId;

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setAndroidId(String androidId){
		this.androidId = androidId;
	}

	public String getAndroidId(){
		return androidId;
	}

	@Override
 	public String toString(){
		return 
			"Phone{" + 
			"id = '" + id + '\'' + 
			",android_id = '" + androidId + '\'' + 
			"}";
		}
}