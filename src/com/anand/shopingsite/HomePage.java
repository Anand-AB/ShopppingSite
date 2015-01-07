package com.anand.shopingsite;



import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class HomePage extends ActionBarActivity {
	public static String name="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


		try {
			((ImageView)findViewById(R.id.registration_userImage)).setImageBitmap(BitmapFactory.decodeFile(sp.getString("userimagepath","")));;	
		} catch (Exception e) {
			System.out.println("Unable to set the image from preferences");
			e.printStackTrace();

		}

		((TextView)findViewById(R.id.name_text)).setText(name);


	}
	//	public void login(View v)
	//	{
	//		//startActivity(new Intent(this, LoginActivity.class));
	//	}
	//	public void login(View v)
	//	{
	//	//	startActivity(new Intent(this, RegisterActivity.class));
	//	}
}
