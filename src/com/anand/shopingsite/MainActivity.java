package com.anand.shopingsite;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}
	public void login(View v)
	{
		startActivity(new Intent(this, LoginActivity.class));
	}
	public void registr(View v)
	{
		startActivity(new Intent(this, RegisterActivity.class));
	}
}
