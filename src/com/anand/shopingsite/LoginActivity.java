package com.anand.shopingsite;



import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

	}
	public void sign(View v)
	{
		EditText edit_user=(EditText)findViewById(R.id.username_edit);
		EditText edit_pass=(EditText)findViewById(R.id.password_edit);

		String username_entered=edit_user.getText().toString();
		String password_entered=edit_pass.getText().toString();

		SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String username_saved=sp.getString("username", "");
		String password_saved=sp.getString("password", "");
		String name_saved=sp.getString("name","");

		if(username_saved.equalsIgnoreCase(username_entered))
		{
			if(password_saved.equals(password_entered))
			{
				HomePage.name=name_saved;
				startActivity(new Intent(this,HomePage.class));
			}
			else {

				Toast.makeText(getApplicationContext(),"Password Error",Toast.LENGTH_LONG).show();

			}
		}
		else{

			Toast.makeText(getApplicationContext(),"Invalid user please Register First",Toast.LENGTH_LONG).show();
			startActivity(new Intent(this,RegisterActivity.class));
		}

	}
	//	public void login(View v)
	//	{
	//	//	startActivity(new Intent(this, RegisterActivity.class));
	//	}
}
