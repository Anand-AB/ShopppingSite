package com.anand.shopingsite;





import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class RegisterActivity extends ActionBarActivity {

	ImageView profileImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rgister);

	}

	public void cameraCapture(View v)
	{
		startActivity(new Intent(this, LoginActivity.class));
	}

	public void galleryCapture(View v)
	{
		startActivity(new Intent(this, LoginActivity.class));
	}

	public void chooseCameraClicked(View v) {
		startActivityForResult(new Intent(this, CameraActivity.class), 1);
	}

	public void choosePictureClicked(View v) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Choose Profile Image"), 2);


	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			File userImageFile=new File(Environment.getExternalStorageDirectory(),""+new Date().getSeconds()+".JPG");//CameraActivity.scaledData

			PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("userimagepath", userImageFile.getAbsolutePath()).commit();

			System.out.println("Result OK Got");
			profileImageView=(ImageView) findViewById(R.id.registration_userImage);
			//profileImageView.setPlaceholder(getResources().getDrawable(R.drawable.ic_launcher));

			if (requestCode == 1) {
				try {
					if(!userImageFile.exists()){
						userImageFile.createNewFile();
					}

					FileOutputStream fos = new FileOutputStream(userImageFile);
					fos.write(CameraActivity.scaledData);
					fos.flush();
					fos.close();

					profileImageView.setImageBitmap(new BitmapDrawable(userImageFile.getPath()).getBitmap());

					System.out.println("Image Set from camera");
				} catch (Exception e) {
					System.out.println("Exception from Register Activity Result - From Camera");
					e.printStackTrace();
				}
			}else if (requestCode == 2) {
				try {
					UserPicture usrPic=new UserPicture(data.getData(), getContentResolver());
					if(!userImageFile.exists()){
						userImageFile.createNewFile();
					}

					//write data to file
					FileOutputStream fos = new FileOutputStream(userImageFile);
					fos.write(bitmapToByteArray(usrPic.getBitmap()));
					fos.flush();
					fos.close();

					profileImageView.setImageBitmap(usrPic.getBitmap());
					System.out.println("Image Set from Gallery");

				} catch (Exception e) {
					System.out.println("Exception from Register Activity Result - From Gallery");
					e.printStackTrace();
				}
			} else{
				System.out.println("Invalid request Code");
			}
		}
	}
	public static byte[] bitmapToByteArray(Bitmap bmp){
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		return stream.toByteArray();
	}



	public void register(View v)

	{
		boolean registrationOk=true;
		//User details entered by user in Regration page
		EditText e=((EditText)findViewById(R.id.username_edit));
		String username_entered=e.getText().toString();

		EditText pass=((EditText)findViewById(R.id.password_edit));
		EditText repass=((EditText)findViewById(R.id.reenterpassword_edit));
		EditText name=((EditText)findViewById(R.id.name_edit));

		String password_entered=pass.getText().toString();
		String repassword_entered=repass.getText().toString();
		String name_entered=name.getText().toString();
		EditText email=((EditText)findViewById(R.id.email_edit));

		String email_entered=email.getText().toString();
		EditText phn=((EditText)findViewById(R.id.phone_edit));

		String phone_entered=phn.getText().toString();

		if(email_entered.contains("@") && email_entered.contains(".")){
			if(! email_entered.matches("[A-Z a-z _]+@*.*")){
				Toast.makeText(getApplicationContext(), "Invalid Email id", Toast.LENGTH_LONG).show();
				registrationOk = false;
				return;
			}
		}else{
			Toast.makeText(getApplicationContext(), "No proper Email id", Toast.LENGTH_LONG).show();
			registrationOk = false;
			return;
		}


		if(username_entered.equals("")){
			Toast.makeText(getApplicationContext(), "Enter username", Toast.LENGTH_LONG).show();
			e.setError("Required");
			registrationOk = false;
			return;
		}
		if(password_entered.equals("")){
			Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_LONG).show();
			pass.setError("Required");
			registrationOk = false;
			return;
		}
		if(repassword_entered.equals("")||!repassword_entered.equals(password_entered)){
			Toast.makeText(getApplicationContext(), "Incorrect Re enter password", Toast.LENGTH_LONG).show();
			repass.setError("Required");
			registrationOk = false;
			return;
		}
		if(name_entered.equals("")){
			Toast.makeText(getApplicationContext(), "Enter the Name", Toast.LENGTH_LONG).show();
			name.setError("Required");

			registrationOk = false;
			return;
		}

		int len=phone_entered.length(); 
		if(phone_entered.equals("")||len<10||len>=11) 
		{
			Toast.makeText(getApplicationContext(), "Enter the correct Phone number", Toast.LENGTH_LONG).show();
			registrationOk = false;
			return;
		}


		if(registrationOk){

			Editor ed=PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
			ed.putString("username", username_entered);
			ed.putString("password", password_entered);
			ed.putString("name", name_entered);
			ed.putString("email", email_entered);
			ed.putLong("phone", Long.parseLong(phone_entered));
			ed.putBoolean("login_status", false);
			ed.commit();

			Toast.makeText(getApplicationContext(), "Registered Succesfully. \n Please login to continue", Toast.LENGTH_LONG).show();
			startActivity(new Intent(this, LoginActivity.class));
			finish();
		}

	}
	//	public void login(View v)
	//	{
	//	//	startActivity(new Intent(this, RegisterActivity.class));
	//	}
}
