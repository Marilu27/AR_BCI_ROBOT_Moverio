package com.bci_ar.ar_bci_rasp_arrow;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OpenGLRenderer implements Renderer {

    private Context context;

    private int frameN;
    private float [] white;
    private float [] black;
    private float [] green;
    private float [] yellow;
    private float [] red;
    private float [] blue;
    private float [] cyan;
    private float [] magenta;
    private float [][] color_vect;

    private Square square;
    private Arrow arrow;

    private int [] flickFrame;
    private int flickFrame1;
    private int flickFrame2;
    private int color_square1;
    private int color_square2;
    private int color_background;

    private float [] square1_position;
    private float [] square2_position;

    private SharedPreferences sharedPreferences;

//    private double duty_cycle;

    public OpenGLRenderer(Context context){
        //Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        this.context=context;

        square = new Square();
        arrow = new Arrow();

        white = new float [] {1,1,1};
        red = new float [] {1,0,0};
        green = new float [] {0,1,0};
        yellow = new float [] {1,1,0};
        blue = new float [] {0,0,1};
        black = new float [] {0,0,0};
        cyan = new float [] {0,1,1};
        magenta = new float [] {1,0,1};
        color_vect = new float [][] {white,black,yellow,green,red,blue,cyan,magenta};

        square1_position = new float[] {0.0f,1.0f,-3.0f};
        square2_position = new float[] {0.0f,-1.0f,-3.0f};

        sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_preferences),context.MODE_PRIVATE);

        frameN = 0;

        flickFrame = new int [] {8,6,5,4,3,2}; // relative to 7.5 Hz, 10 Hz, 12 Hz, 15 Hz, 20 Hz, 30 Hz


        //Frequency is given by refreshRate / flickFrame;
        //For 60Hz rate : 60/8 = 7.5 Hz - 60/6 = 10 Hz
        // Moverio BT-200 refreshRate = 59.388

 //       duty_cycle = 0.25;
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);


        color_square1 = sharedPreferences.getInt("Color_square1",0);
        color_square2 = sharedPreferences.getInt("Color_square2",0);
        color_background = sharedPreferences.getInt("Color_background",1);




        String sq1_x= sharedPreferences.getString("X_square1",this.context.getString(R.string.X1));
        String sq1_y= sharedPreferences.getString("Y_square1",this.context.getString(R.string.Y1));
        String sq1_z= sharedPreferences.getString("Z_square1",this.context.getString(R.string.Z1));
        flickFrame1= flickFrame[sharedPreferences.getInt("Freq_square1",1)];

        String sq2_x= sharedPreferences.getString("X_square2",this.context.getString(R.string.X2));
        String sq2_y= sharedPreferences.getString("Y_square2",this.context.getString(R.string.Y2));
        String sq2_z= sharedPreferences.getString("Z_square2",this.context.getString(R.string.Z2));
        flickFrame2= flickFrame[sharedPreferences.getInt("Freq_square2",0)];

        if (sq1_x!=null && !sq1_x.equals("")) {
            square1_position[0] = Float.parseFloat(sq1_x);
        }
        else{
            square1_position[0] = 0.0f;
        }

        if (sq1_y!=null && !sq1_y.equals("")) {
            square1_position[1] = Float.parseFloat(sq1_y);
        }
        else{
            square1_position[1] = 1.0f;
        }

        if (sq1_z!=null && !sq1_z.equals("")) {
            square1_position[2] = Float.parseFloat(sq1_z);
        }
        else{
            square1_position[2] = -3.0f;
        }

        if (sq2_x!=null && !sq2_x.equals("")) {
            square2_position[0] = Float.parseFloat(sq2_x);
        }
        else{
            square2_position[0] = 0.0f;
        }
        if (sq2_y!=null && !sq2_y.equals("")) {
            square2_position[1] = Float.parseFloat(sq2_y);
        }
        else{
            square2_position[1] = -1.0f;
        }

        if (sq2_z!=null && !sq2_z.equals("")) {
            square2_position[2] = Float.parseFloat(sq2_z);
        }
        else{
            square2_position[2] = -3.0f;
        }

        gl.glClearColor(color_vect[color_background][0], color_vect[color_background][1], color_vect[color_background][2], 1.0f);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,GL10.GL_NICEST);

    }

    //CALLED EACH FRAME
    public void onDrawFrame(GL10 gl) {
        flickerDoubleSquare(gl, frameN);
        frameN++;
    }
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45.0f,
                (float) width / (float) height,
                0.1f, 100.0f);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    //OBJECT TO BE RENDERED
    private void flickerDoubleSquare(GL10 gl, int frameN){
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, 0.0f, -3.0f);
//        gl.glScalef(2.5f, 2.5f, 2.5f);

        // SQUARE 1 render
        gl.glPushMatrix();
        gl.glTranslatef(square1_position[0], square1_position[1], square1_position[2]); // originali -> 4.6f, 1.8f, -3.0f

        if (frameN % flickFrame1 < flickFrame1/2) arrow.draw(gl, color_vect[color_square1]);
        else arrow.draw(gl, color_vect[color_background]);

        gl.glPopMatrix();


        // SQUARE 2 render
        gl.glPushMatrix();
        gl.glTranslatef(square2_position[0], square2_position[1], square2_position[2]); // originali -> 4.6f, 1.8f, -3.0f
        gl.glRotatef(180.0f,0.0f,0.0f,1.0f);
        if (frameN % flickFrame2 < flickFrame2/2) arrow.draw(gl, color_vect[color_square2]);
        else arrow.draw(gl, color_vect[color_background]);

        gl.glPopMatrix();

    }
}
