package com.anand.shopingsite;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.anand.shopingsite.R;
import com.anand.shopingsite.R.id;
import com.anand.shopingsite.R.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class CameraActivity extends Activity {
	public static byte[] scaledData;
	private static int rotationAngle=0;
	private Camera camera;
	private boolean frontCamera=true;
	private SurfaceHolder holder;
	private byte[] imageDataArray;
	private ProgressDialog pd = null;
	private boolean inPreview=false;
	private int currentCameraId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.camera);

		int cam_count=Camera.getNumberOfCameras();
		//if phone has only one camera, hide "switch camera" button
		if(cam_count >= 1 && camera == null) {
			((ImageButton) findViewById(R.id.camera_switch_button)).setVisibility(View.INVISIBLE);
			try {
				camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
				currentCameraId=Camera.CameraInfo.CAMERA_FACING_BACK;
				frontCamera=false;

				if(camera==null){
					camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
					currentCameraId=Camera.CameraInfo.CAMERA_FACING_FRONT;
					frontCamera=true;					
				}
			} catch (Exception e) {
				Toast.makeText(CameraActivity.this, "Unable to Start camera!\n Locked.", Toast.LENGTH_LONG).show();
			}
		}else if(cam_count<1){
			Toast.makeText(getApplicationContext(), "No cameras found!!", Toast.LENGTH_LONG).show();
			setResult(RESULT_CANCELED);
			finish();
		}

		holder = ((SurfaceView) findViewById(R.id.camera_surface_view)).getHolder();
		holder.addCallback(new Callback() {

			public void surfaceCreated(SurfaceHolder holder) {
				try {
					if (camera != null) {
						setCameraDisplayOrientation(CameraActivity.this,currentCameraId, camera);
						camera.setPreviewDisplay(holder);
						camera.startPreview();
						inPreview=true;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				// nothing to do here
			}

			public void surfaceDestroyed(SurfaceHolder holder) {
				// nothing here
			}

		});
		super.onCreate(savedInstanceState);
	}


	@Override
	public void onResume() {
		super.onResume();
		if (camera == null) {
			try {
				camera = Camera.open(currentCameraId);
				if(currentCameraId==Camera.CameraInfo.CAMERA_FACING_FRONT)
					frontCamera=true;
				else
					frontCamera=false;

			} catch (Exception e) {
				Toast.makeText(CameraActivity.this, "Unable to reinitialise camera", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onPause() {
		if (camera != null) {
			camera.stopPreview();
			camera.release();
		}
		super.onPause();
	}

	public void cameraClicked(View v) {
		if (camera == null)
			return;
		camera.takePicture(new Camera.ShutterCallback() {

			@Override
			public void onShutter() {
				// nothing to do
			}

		}, null, new Camera.PictureCallback() {

			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				imageDataArray=data;
				new AlertDialog.Builder(CameraActivity.this)
				.setTitle("Camera Photo")
				.setMessage("Select This Image as Profile Photo?")
				.setPositiveButton("YES", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Show the ProgressDialog on this thread
						CameraActivity.this.pd = ProgressDialog.show(CameraActivity.this, "Saving Image..", "Please Wait...", true, false);
						new SavePhotoToParseFile().execute();
					}
				}).setNegativeButton("NO", 	new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {	
						try {
							//this step is critical or preview on new camera will no know where to render to
							CameraActivity.this.camera.setPreviewDisplay(holder);
						} catch (IOException e) {
							e.printStackTrace();
						}
						CameraActivity.this.camera.startPreview();
					}
				}).show();
			}
		});
	}

	public void cameraChangeClicked(View v) {
		System.out.println("Switching Camera");
		if (inPreview) {
			camera.stopPreview();
		}
		//NB: if you don't release the current camera before switching, your app will crash
		camera.release();

		//swap the id of the camera to be used
		if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
			currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
			frontCamera=true;
		}else {
			currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
			frontCamera=false;
		}
		camera = Camera.open(currentCameraId);
		setCameraDisplayOrientation(CameraActivity.this,currentCameraId, camera);
		try {
			//this step is critical or preview on new camera will no know where to render to
			camera.setPreviewDisplay(holder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		camera.startPreview();
		System.out.println("Finished Switching Camera");

	}

	public static void setCameraDisplayOrientation(Activity activity,int cameraId, android.hardware.Camera camera) {
		android.hardware.Camera.CameraInfo info =
				new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0: degrees = 0; break;
		case Surface.ROTATION_90: degrees = 90; break;
		case Surface.ROTATION_180: degrees = 180; break;
		case Surface.ROTATION_270: degrees = 270; break;
		}
		int result;



		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {

			result = (info.orientation + degrees) % 360;
			// compensate the mirror
			result = (360 - result) % 360; 


		} else {
			// back-facing
			result = (info.orientation - degrees + 360) % 360;
		}

		rotationAngle=result;
		camera.setDisplayOrientation(result);
	}


	/*
	 * ParseQueryAdapter loads ParseFiles into a ParseImageView at whatever size
	 * they are saved. Since we never need a full-size image in our app, we'll
	 * save a scaled one right away.
	 */
	private void saveScaledPhoto(byte[] data) {

		try {
			// Resize photo from camera byte array
			Bitmap realImage = BitmapFactory.decodeByteArray(data, 0, data.length);

			// Override Android default landscape orientation and save portrait
			Matrix matrix = new Matrix();
			if (frontCamera) {
				matrix.postRotate(-90);
			}else{
				matrix.postRotate(rotationAngle);				
			}

			Bitmap rotatedScaledMealImage = Bitmap.createBitmap(
					Bitmap.createScaledBitmap(realImage, 200, 200 * realImage.getHeight() / realImage.getWidth(), false), 
					0,0,
					200, 200 * realImage.getHeight() / realImage.getWidth(),
					matrix, true);

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			rotatedScaledMealImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);

			scaledData = bos.toByteArray();
			bos.flush();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private class SavePhotoToParseFile extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			saveScaledPhoto(imageDataArray);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if(CameraActivity.this.pd!=null){
				CameraActivity.this.pd.dismiss();
			}
			setResult(RESULT_OK);
			finish();
		}
	}
}