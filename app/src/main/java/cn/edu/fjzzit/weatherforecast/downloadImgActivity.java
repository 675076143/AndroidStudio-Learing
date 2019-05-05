package cn.edu.fjzzit.weatherforecast;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class downloadImgActivity extends AppCompatActivity {

    private static final int LOAD_SUCCESS = 1;
    private static final int LOAD_FAILED = 0;
    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            if(msg.what == LOAD_SUCCESS){
                Bitmap bitmap = BitmapFactory.decodeFile((String) msg.obj);
                mShowImageView.setImageBitmap(bitmap);
            }else if(msg.what == LOAD_FAILED){
                Toast.makeText(downloadImgActivity.this,"Failed",Toast.LENGTH_LONG).show();
            }
            super.handleMessage(msg);
        }
    };

    private Thread mThread;
    private Button mBtnDownloadImg;
    private ImageView mShowImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mBtnDownloadImg = findViewById(R.id.button_download);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_img);

        mBtnDownloadImg = findViewById(R.id.button_download);
        mShowImageView = findViewById(R.id.image_view_downloaded);

        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String url ="http://192.168.22.161/s6/img/img01.jpg";

                HttpURLConnection connection = null;
                InputStream inputStream = null;
                FileOutputStream fileOutputStream = null;

                try {
                    connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setReadTimeout(10000);
                    connection.setDoInput(true);
                    inputStream = connection.getInputStream();
                    File dir = new File(getApplication().getExternalCacheDir(),"img");
                    if(!dir.exists()){
                        dir.mkdirs();
                    }
                    File file = new File(dir,"img01");
                    fileOutputStream = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = inputStream.read(buffer))>0){
                        fileOutputStream.write(buffer,0,len);
                    }
                    fileOutputStream.flush();

                    Message message = new Message();
                    message.what = LOAD_SUCCESS;
                    message.obj = file.getAbsolutePath();
                    mHandler.sendMessage(message);

                } catch (Exception e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(LOAD_FAILED);
                }finally {
                    if(fileOutputStream!=null){
                        try {
                            fileOutputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if(inputStream!=null){
                        try {
                            inputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if(connection!=null){
                        connection.disconnect();
                    }
                }
            }
        });

        mBtnDownloadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThread.start();
            }
        });


    }
}
