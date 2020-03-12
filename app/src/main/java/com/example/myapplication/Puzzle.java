package com.example.myapplication;

//import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.event.EventNotifier;
import com.example.myapplication.event.EventListener;
import com.example.myapplication.event.Event;
import com.squareup.seismic.ShakeDetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class Puzzle extends AppCompatActivity implements EventListener, View.OnClickListener, ShakeDetector.Listener, View.OnTouchListener {

    private EventNotifier notifier = new EventNotifier();
    private Modele modele;
    private Controller controller;
    private TableLayout layout;
    private TextView text;
    private int MAX;
    private PieceView[][] tabView;
    private Bitmap image;
    private boolean shake = true;
    private boolean init = false;
    private FrameLayout winBox;
    private FrameLayout imagePreview;
    private ImageView imagePreviewImage;
    private TextView moveText;
    private TextView shakeText;
    private Button buttonPreview;

    //private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        MAX = getIntent().getIntExtra("difficulty", 3);

        this.modele = new Modele(MAX);
        this.controller = new Controller(this.modele);

        modele.addEventListener(this);
        this.addEventListener(controller);

        this.layout = findViewById(R.id.layout);
        this.winBox = findViewById(R.id.winBox);
        this.winBox.setVisibility(View.INVISIBLE);
        this.moveText = findViewById(R.id.move);
        this.shakeText = findViewById(R.id.textShake);
        this.imagePreview = findViewById(R.id.imagePreview);
        this.imagePreview.setVisibility(View.INVISIBLE);
        this.imagePreviewImage = findViewById(R.id.imagePreviewImage);
        this.buttonPreview = findViewById(R.id.previewButton);
        this.buttonPreview.setOnTouchListener(this);

        tabView = new PieceView[MAX][MAX];

        for (int i = 0; i<MAX; i++){
            TableRow tbr = new TableRow(this);
            tbr.setGravity(Gravity.CENTER);
            this.layout.addView(tbr);
        }

        String filename = getIntent().getStringExtra("image");
        try {
            FileInputStream is = this.openFileInput(filename);
            this.image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }



        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        ShakeDetector shakeDetector = new ShakeDetector(this);
        shakeDetector.setSensitivity(11);
        shakeDetector.start(sensorManager);

    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            startGame();
        }
    }

    @Override
    public void actionADeclancher(Event evt) {
        if (evt.getSource() == this.modele){
            if(evt.getComment() == "update"){
                if(evt.getData() instanceof String[][]){
                    String[][] s = (String[][]) evt.getData();
                    this.setImage(s);
                }
            } else if(evt.getComment() == "win"){
                this.win();
            } else if(evt.getComment() == "switch"){
                ArrayList<String> data = (ArrayList<String>) evt.getData();
                System.out.println(data);
                switchCase(Integer.parseInt(data.get(0)), Integer.parseInt(data.get(1)), data.get(2), Integer.parseInt(data.get(3)), Integer.parseInt(data.get(4)), data.get(5));
            } else if(evt.getComment() == "move"){
                String s = evt.getData() + "";
                this.moveText.setText(s);
            }
        }
    }

    public void addEventListener(EventListener listener) {
        notifier.addEventListener(listener);
    }

    public void removeEventListener(EventListener listener) {
        notifier.removeEventListener(listener);
    }

    public void win(){
        this.winBox.setVisibility(View.VISIBLE);
        for (int i = 0; i < MAX; i++) {
            for (int j = 0; j < MAX; j++) {
                tabView[i][j].lock();
            }
        }
    }

    public void startGame(){
        if(this.init == false) {
            System.out.println("start");
            //Bitmap icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.test2);
            this.cutImage(this.image);
            this.initializeTab();
            this.shake = true;
            this.init = true;
            this.imagePreviewImage.setImageBitmap(this.image);
            notifier.diffuserEvent(new Event(this, null, "initialize"));
        }
    }

    @Override
    public void onClick(View v) {

        if(v == findViewById(R.id.buttonRandom)) {
            if(init && shake) {
                this.shake = false;
                notifier.diffuserEvent(new Event(this, null, "random"));
                findViewById(R.id.buttonRandom).setVisibility(View.INVISIBLE);
                this.shakeText.setVisibility(View.INVISIBLE);
            }
        }
        //this.setImage(testString);
        //notifier.diffuserEvent(new Event(this, 1, "add"));
    }

    public void cutImage(Bitmap selectedImage){
        int widthOriginal = selectedImage.getWidth();
        int heightOriginal = selectedImage.getHeight();

        int width = findViewById(R.id.layout).getWidth();
        int height = findViewById(R.id.layout).getHeight();

        int widthNew;
        int heightNew;

        if(widthOriginal > heightOriginal){
            widthNew = width;
            heightNew = (heightOriginal * height) / widthOriginal;
        }else{
            heightNew = height;
            widthNew = (widthOriginal * width) / heightOriginal;
        }

        Bitmap newBitmap = Bitmap.createScaledBitmap(selectedImage, widthNew, heightNew, false);

        int largeur = (widthNew / MAX);
        int hauteur = (heightNew / MAX);

        for (int i = 0; i < MAX; i++) {
            for (int j = 0; j < MAX; j++) {
                String nom = i+"_"+j;
                Bitmap tmp = Bitmap.createBitmap(newBitmap, j * largeur, i * hauteur, largeur, hauteur);
                //image_view.setImageBitmap(tmp);
                saveImage(this,tmp,nom,"jpg");
                //sauverImage(img.getSubimage(j * largeur, i * hauteur, largeur, hauteur), nom);
            }
        }
    }

    public void initializeTab(){

        int width = findViewById(R.id.layout).getWidth() / MAX;
        int height = findViewById(R.id.layout).getHeight() / MAX;
        //int height = findViewById(R.id.layout).getWidth() / MAX;

        for(int i = 0; i < MAX; i++){
            TableRow row = (TableRow) this.layout.getChildAt(i);
            for(int j = 0; j < MAX; j++){
                PieceView image = new PieceView(this, i, j);
                image.addEventListener(controller);
                image.lock();
                image.setAdjustViewBounds(true);
                image.setImageBitmap(loadImageBitmap(this,i+"_"+j,"jpg"));
                image.setMaxWidth(width);
                image.setMaxHeight(height);
                //image.setForegroundGravity(Gravity.CENTER);

                row.addView(image);
                tabView[i][j] = image;
            }
        }
    }

    public void setImage(String[][] s){

        for(int i = 0; i < MAX; i++) {
            TableRow row = (TableRow) this.layout.getChildAt(i);
            row.removeAllViews();
            for (int j = 0; j < MAX; j++) {
                PieceView image = tabView[i][j];
                image.unlock();
                image.setImageBitmap(loadImageBitmap(this,s[i][j],"jpg"));
                image.setColorFilter(null);
                row.addView(image);
                tabView[i][j] = image;
            }
        }
    }

    public void switchCase(int r1, int c1, String image1, int r2, int c2, String image2){
            TableRow row1 = (TableRow) this.layout.getChildAt(r1);
            TableRow row2 = (TableRow) this.layout.getChildAt(r2);
            PieceView pv1 = (PieceView) row1.getChildAt(c1);
            PieceView pv2 = (PieceView) row2.getChildAt(c2);
            pv1.setImageBitmap(loadImageBitmap(this,image1,"jpg"));
            pv2.setImageBitmap(loadImageBitmap(this,image2,"jpg"));
            pv1.setColorFilter(null);
            pv2.setColorFilter(null);
            tabView[r1][c1] = pv1;
            tabView[r2][c2] = pv2;
    }

    public void saveImage(Context context, Bitmap bitmap, String name, String extension){
        name = name + "." + extension;
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = context.openFileOutput(name, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap loadImageBitmap(Context context,String name,String extension){
        name = name + "." + extension;
        FileInputStream fileInputStream;
        Bitmap bitmap = null;
        try{
            fileInputStream = context.openFileInput(name);
            bitmap = BitmapFactory.decodeStream(fileInputStream);
            fileInputStream.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    public void hearShake() {
        if(init && shake) {
            Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vib.vibrate(250);
            this.shake = false;
            notifier.diffuserEvent(new Event(this, null, "random"));
            findViewById(R.id.buttonRandom).setVisibility(View.INVISIBLE);
            this.shakeText.setVisibility(View.INVISIBLE);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        File file=new File(this.getFilesDir().getAbsolutePath()+"/");
        if(file.exists())file.delete();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        System.out.println("TEST");
        if(v == this.buttonPreview){
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                this.imagePreview.setVisibility(View.VISIBLE);
                return true;
            } else if(event.getAction() == MotionEvent.ACTION_UP){
                this.imagePreview.setVisibility(View.INVISIBLE);
                return true;
            }
        }
        return false;
    }
}
