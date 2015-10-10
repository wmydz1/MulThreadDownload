package com.logoocc.multhreaddownload.download;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.logoocc.multhreaddownload.R;
import com.logoocc.multhreaddownload.net.download.DownloadProgressListener;
import com.logoocc.multhreaddownload.net.download.FileDownloader;
import com.logoocc.multhreaddownload.service.DownloadReceiver;

import java.io.File;

public class DownloadActivity extends Activity {

	private ProgressBar downloadbar;
	private EditText pathText,pathText2;
	private TextView resultView;

	Intent intent;
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					int size = msg.getData().getInt("size");
					downloadbar.setProgress(size);
					float result = (float)downloadbar.getProgress()/ (float)downloadbar.getMax();
					int p = (int)(result*100);
					//要通知的notification中进度条的id
					int notifityId=msg.arg1;
					resultView.setText(p+"%");
					intent.putExtra("pro", p);
					intent.putExtra("id", notifityId);

					// 发送Intent 更新状态栏的下载情况
					DownloadActivity.this.sendBroadcast(intent);


					if(downloadbar.getProgress()==downloadbar.getMax())
						Toast.makeText(DownloadActivity.this, R.string.success, Toast.LENGTH_SHORT).show();
					break;

				case -1:
					Toast.makeText(DownloadActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
					break;
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		intent=new Intent(this,DownloadReceiver.class);
		Button button = (Button)this.findViewById(R.id.button);
		downloadbar = (ProgressBar)this.findViewById(R.id.downloadbar);
		pathText = (EditText)this.findViewById(R.id.path);
		pathText2 = (EditText)this.findViewById(R.id.xiazai2);
		resultView = (TextView)this.findViewById(R.id.result);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String path = pathText.getText().toString();
				String path2 = pathText2.getText().toString();
				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
					File dir = Environment.getExternalStorageDirectory();//文件保存目录
					download(path, dir,1);
					download(path2, dir,2);
				}else{
					Toast.makeText(DownloadActivity.this, R.string.sdcarderror, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	//对于UI控件的更新只能由主线程(UI线程)负责，如果在非UI线程更新UI控件，更新的结果不会反映在屏幕上，某些控件还会出错
	private void download(final String path, final File dir,final int softid){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					FileDownloader loader = new FileDownloader(DownloadActivity.this, path, dir, 3,softid);
					final FileDownloader loader2=loader;
					int length = loader.getFileSize();//获取文件的长度
					downloadbar.setMax(length);
					loader.download(new DownloadProgressListener(){
						@Override
						public void onDownloadSize(int size) {//可以实时得到文件下载的长度
							Message msg = new Message();
							msg.what = 1;
							msg.arg1=loader2.notifityid;
							msg.getData().putInt("size", size);
							handler.sendMessage(msg);
						}});
				} catch (Exception e) {
					Message msg = new Message();
					msg.what = -1;
					msg.getData().putString("error", "下载失败");
					handler.sendMessage(msg);
				}
			}
		}).start();

	}
}