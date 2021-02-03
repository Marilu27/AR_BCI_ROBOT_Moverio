package com.bci_ar.ar_bci_rasp_arrow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    // BCI-related variables
    public static final double SAMPLING_FREQ = 256.0;
    public static double f1 = 12.0;
    public static double f2 = 10.0;
    //public static double th1, th2, c;
    public static int acquisition_time_ms;
    public static int f_blink = 0;
    public static int f_ssvep = 0;

    public static boolean blink_status=false;



    public static double m, q;

    // "m"  and "q" coefficients of separating line: GENERALE CALCOLATA SU 11 SOGGETTI
    List<Double> M = Arrays.asList(0.9847, 0.8101, 0.7233, 0.6650, 0.6469, 0.7738, 0.7969);
    List<Double> Q = Arrays.asList(-37.3349, 0.2220, 7.9486, 5.5373, 6.3370, 2.5843, 0.5436);

    // acquisition times and actually chosen time
    List<Integer> A = Arrays.asList(1000, 1250, 1500, 2000, 4000, 8000, 16000);
    public static int cho = 3; //significa che quando faremo A.get(cho) il valore sarà indice 1 quindi 2


    // Other variables
    private static int layout_number = 0;
    private static final Handler hDelayed = new Handler();
    private static final int TIME_DELAY = 500;                     // 500 ms
    private SharedPreferences sharedPreferences;

    private mConnectTask connectRobot;
    private mConnectTask connectRasp;
    private TcpClient tcpClient;
    private TcpClient tcpClientRasp;
    private String SERVER_IP= "192.168.1.17";
    private String SERVER_PORT= "4444";

    private boolean running;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE);

        // DEBUG Gabba ------------------------------
        //Toast.makeText(getApplicationContext(), "ON CREATE", Toast.LENGTH_SHORT).show();

        // Initialize screen
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);





    }

    @Override
    public void onBackPressed() {
        hDelayed.removeCallbacksAndMessages(null);
        hDelayed.postDelayed(new Runnable() {
            @Override
            public void run() {
                onResume();
            }
        }, 200);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("MainActivity", "onStart");

        connectRobot=null;
        connectRasp=null;



        // Start TCP Client
        boolean IPcorrect = true;
        boolean PORTcorrect = true;
        int Server_PORT=-1;
        int Server_IP[]= new int[4];

        String ip0 = sharedPreferences.getString("ServerIP0","");
        String ip1 = sharedPreferences.getString("ServerIP1","");
        String ip2 = sharedPreferences.getString("ServerIP2","");
        String ip3 = sharedPreferences.getString("ServerIP3","");
        String port = sharedPreferences.getString("ServerPORT","");

        if(ip0 !=null && !ip0.equals("") && ip1 != null && !ip1.equals("") && ip2 != null && !ip2.equals("") && ip3 != null && !ip3.equals("") ){


            Server_IP[0]= Integer.parseInt(ip0);
            Server_IP[1]= Integer.parseInt(ip1);
            Server_IP[2]= Integer.parseInt(ip2);
            Server_IP[3]= Integer.parseInt(ip3);


            for(int k=0;k<3;k++){
                if(Server_IP[k] < 0 || Server_IP[k] > 255){
                    IPcorrect = false;
                }
            }



        }
        else{
            IPcorrect = false;
        }


        if(port != null && !port.equals("")){

            Server_PORT = Integer.parseInt(port);
            if( Server_PORT < 0 || Server_PORT >65535){
                PORTcorrect = false;
            }

        }
        else{
            PORTcorrect = false;
        }





        if(IPcorrect && PORTcorrect){
            SERVER_IP = ip0 + "." + ip1 + "." + ip2 + "." + ip3;
            SERVER_PORT = port;




            // Connection to ROBOT
            connectRasp = new mConnectTask();
            connectRasp.execute(SERVER_IP,SERVER_PORT);
/*
            // Connection to RASPBERRY
            connectRasp = new mConnectTask();
            connectRasp.execute("192.168.1.1","4443");
*/


        }
        else{
            if(!IPcorrect)
                Toast.makeText(getApplicationContext(), "IP not valid!", Toast.LENGTH_SHORT).show();
            if(!PORTcorrect)
                Toast.makeText(getApplicationContext(), "PORT not valid!", Toast.LENGTH_SHORT).show();

        }

//        connectRobot = new mConnectTask();
//        connectRobot.execute("");

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("MainActivity", "onStop");

        // DEBUG Gabba ------------------------------
        //Toast.makeText(getApplicationContext(), "ON STOP", Toast.LENGTH_SHORT).show();


/*
        // Close connection with ROBOT
        if(connectRobot!= null) {
            connectRobot.closeConnection();
            connectRobot.cancel(true);
            connectRobot = null;
        }
*/


        // Close connection with RASPBERRY
        if(connectRasp!= null) {
            connectRasp.closeConnection();
            connectRasp.cancel(true);
            connectRasp = null;
        }


    }

    @Override
    public void onRestart() {
        super.onRestart();

        // DEBUG Gabba ------------------------------
        //Toast.makeText(getApplicationContext(), "ON RESTART", Toast.LENGTH_SHORT).show();
        Log.d("MainActivity", "onRestart");
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d("MainActivity", "onPause");

        // DEBUG Gabba ------------------------------
        //Toast.makeText(getApplicationContext(), "ON PAUSE", Toast.LENGTH_SHORT).show();

        hDelayed.removeCallbacksAndMessages(null);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("MainActivity", "onResume");

        running=false;


        // STARTING FROM THE BEGINNING
        setContentView(R.layout.welcome_page);
        TextView connStatus = findViewById(R.id.status);
        Button connectButton = findViewById(R.id.connectButton);




        if ( (connectRasp != null && connectRasp.isReady())) {    //(connectRobot!= null && connectRobot.isReady())
            connStatus.setText("Connected");
            connectButton.setEnabled(false);
            //connectButton.setVisibility(View.INVISIBLE);

        } else {

/*
            if(connectRobot!= null && !connectRobot.isReady())
                Log.d("MainActivity", "Connection to ROBOT Failed");


            if(connectRasp!= null && !connectRasp.isReady())
                Log.d("MainActivity", "Connection to RASPBERRY Failed");
*/
            connStatus.setText("Disconnected");

        }
    }


    public void FSM() {
        /*            // DEGUG Gabba ----------------------------

            String a = sharedPreferences.getString("Color_square1","white");
            String b = sharedPreferences.getString("Color_square2","white");
            String c = sharedPreferences.getString("Color_background","white");
            if (a!=null && b!=null && c!=null){
                Toast.makeText(getApplicationContext(), a+" "+b+" "+c, Toast.LENGTH_SHORT).show();
            }
            else Toast.makeText(getApplicationContext(), "Nessun Valore!", Toast.LENGTH_SHORT).show();

        */
        //versione SVM
        acquisition_time_ms = A.get(cho); // in ms
        //   m = M.get(cho);
        //  q = Q.get(cho);


        //Toast.makeText(getApplicationContext(), "Start acquisition", Toast.LENGTH_SHORT).show();

        setContentView(R.layout.decision);
        TextView string = findViewById(R.id.question);
        string.setTextColor(Color.RED);
        string.setVisibility(View.VISIBLE);
        string.setText("START");

        running=true;


        hDelayed.postDelayed(new Runnable() {
            @Override
            public void run() {
                BCIbutton();
            }
        }, TIME_DELAY);


    }



    /*
     * BCI Algorithm
     */
    private void BCIbutton() {

        GLSurfaceView GLView = new GLSurfaceView(this);
        GLView.setRenderer(new OpenGLRenderer(this));
        setContentView(GLView);

        connectRasp.sendMessage("startAcquisition\n");

/*
        hDelayed.postDelayed(new Runnable() {
            @Override
            public void run() {
                //results(); // @@@@@ COMMENTARE ####
            }
        }, acquisition_time_ms);
*/


    }

    private void results() {


        // RESULTS PLOT (if any data is present)
        try {
//            if (!usb_data.isEmpty()) {

                // Elaboration
                f1 = Double.parseDouble(sharedPreferences.getString("Freq_square1_string", "12"));
                f2 = Double.parseDouble(sharedPreferences.getString("Freq_square2_string", "10"));

                // DEBUG Gabba -------------------------------------
                // Toast.makeText(getApplicationContext(),"f1 = " + Double.toString(f1) + " f2 = " + Double.toString(f2),Toast.LENGTH_SHORT).show();


                //f = classification(fft);

  //              f_blink = computeBlink(usb_data); //1 se c'è il blink forte, 50 se debole o nessun blink
  //              f_ssvep = computeSSVEP(usb_data); //1 se 12 Hz, 2 se 10 Hz


                //Toast.makeText(getApplicationContext(), "f blink= " + Integer.toString(f_blink) + "  f ssvep= " + Integer.toString(f_ssvep), Toast.LENGTH_SHORT).show();


//            }
            // DEBUG Gabba ------------------
//            else Toast.makeText(getApplicationContext(), "No USB Data", Toast.LENGTH_SHORT).show();

        } catch (NullPointerException e) {
            // do nothing: nothing to show
        }


        // DEBUG Gabba ------------------
        // Toast.makeText(getApplicationContext(),"Results",Toast.LENGTH_SHORT).show();
        //f = 1;  // provvisorio // @@@@@  COMMENTARE ####
        choice(); // @@@@@ DE-COMMENTARE
    }

    public void choice() {


        if (f_blink == 1) {

            if (blink_status==false) {
                if (tcpClient != null && tcpClient.isConnected() && !tcpClient.isClosed()) {
                    tcpClient.sendMessage("{direzione: avanti,velocita: 2}\n");
                }
                blink_status=true;
            } else if (blink_status==true) {
                if (tcpClient != null && tcpClient.isConnected() && !tcpClient.isClosed()) {
                    tcpClient.sendMessage("{direzione: stop}\n");
                }
                blink_status=false;
            }

            /*if (tcpClient != null && tcpClient.isConnected() && !tcpClient.isClosed()) {
                tcpClient.sendMessage("{direzione: avanti,velocita: 3}\n");

                //DEBUG Gabba
                // Toast.makeText(getApplicationContext(),"Stop !",Toast.LENGTH_LONG).show();
            }*/


        } /*else if (f_blink == 2) {


            if (tcpClient != null && tcpClient.isConnected() && !tcpClient.isClosed()) {
                tcpClient.sendMessage("{direzione: avanti,velocita: 3}\n");

                //DEBUG Gabba
                // Toast.makeText(getApplicationContext(),"Start !",Toast.LENGTH_LONG).show();
            }


        }*/ else {

            if (f_ssvep == 1) {

                if (tcpClient != null && tcpClient.isConnected() && !tcpClient.isClosed()) {
                    tcpClient.sendMessage("{direzione: sinistra}\n");

                    //DEBUG Gabba
                    // Toast.makeText(getApplicationContext(),"Sinistra !",Toast.LENGTH_LONG).show();
                }


            } else if (f_ssvep == 2) {

                if (tcpClient != null && tcpClient.isConnected() && !tcpClient.isClosed()) {
                    tcpClient.sendMessage("{direzione: destra}\n");

                    //DEBUG Gabba
                    // Toast.makeText(getApplicationContext(),"Destra !",Toast.LENGTH_LONG).show();
                }


            } else {

                // if (tcpClient != null && tcpClient.isConnected() && !tcpClient.isClosed()) {
                   // tcpClient.sendMessage("{direzione: avanti,velocita: 3}\n");

                    //DEBUG Gabba
                    //Toast.makeText(getApplicationContext(), "Nessun comando !", Toast.LENGTH_SHORT).show();
                //}


            }


        }

        hDelayed.postDelayed(new Runnable() {
            @Override
            public void run() {
                FSM();
            }
        }, 50);


    }


    // ------------------ by Gabba ------------------

    // Funzione richiamata quando viene cliccato sul bottone 'Settings'
    public void openSetting(View view) {

        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);

    }

    // Funzione richiamata quando viene cliccato sul bottone 'Connect'
    public void reConnect(View view) {

        if( (connectRasp != null && !connectRasp.isReady())  ){  // (connectRasp != null && !connectRasp.isReady()) || (connectRobot != null && !connectRobot.isReady())
/*
            // Close connection with RASPBERRY
            if (connectRasp != null && !connectRasp.isReady()) {
                connectRasp.closeConnection();
                connectRasp.cancel(true);
                connectRasp = null;
            }
*/

            // Close connection with ROBOT

            connectRasp.closeConnection();
            connectRasp.cancel(true);
            connectRasp = null;


            // Start TCP Client

            boolean IPcorrect = true;
            boolean PORTcorrect = true;
            int Server_PORT = -1;
            int Server_IP[] = new int[4];

            String ip0 = sharedPreferences.getString("ServerIP0", "");
            String ip1 = sharedPreferences.getString("ServerIP1", "");
            String ip2 = sharedPreferences.getString("ServerIP2", "");
            String ip3 = sharedPreferences.getString("ServerIP3", "");
            String port = sharedPreferences.getString("ServerPORT", "");

            if (ip0 != null && ip1 != null && ip2 != null && ip3 != null) {


                Server_IP[0] = Integer.parseInt(ip0);
                Server_IP[1] = Integer.parseInt(ip1);
                Server_IP[2] = Integer.parseInt(ip2);
                Server_IP[3] = Integer.parseInt(ip3);


                for (int k = 0; k < 3; k++) {
                    if (Server_IP[k] < 0 || Server_IP[k] > 255) {
                        IPcorrect = false;
                    }
                }


            } else {
                IPcorrect = false;
            }


            if (port != null) {

                Server_PORT = Integer.parseInt(port);
                if (Server_PORT < 0 || Server_PORT > 65535) {
                    PORTcorrect = false;
                }

            } else {
                PORTcorrect = false;
            }


            if (IPcorrect && PORTcorrect) {
                SERVER_IP = ip0 + "." + ip1 + "." + ip2 + "." + ip3;
                SERVER_PORT = port;


                // Connection to RASPBERRY
                connectRasp = new mConnectTask();
                connectRasp.execute(SERVER_IP, SERVER_PORT);

/*
                // Connection to RASPBERRY
                connectRasp = new mConnectTask();
                connectRasp.execute("192.168.1.1", "4443");
*/

            } else {

                if (!IPcorrect)
                    Toast.makeText(getApplicationContext(), "IP not valid!", Toast.LENGTH_SHORT).show();
                if (!PORTcorrect)
                    Toast.makeText(getApplicationContext(), "PORT not valid!", Toast.LENGTH_SHORT).show();

            }





        }


//        connectRobot = new mConnectTask();
//        connectRobot.execute("");

        hDelayed.postDelayed(new Runnable() {
            @Override
            public void run() {
                onResume();
            }
        }, 200);

    }

    // Funzione richiamata quando viene cliccato sul bottone 'Start'
    public void startFSM(View view) {
        TextView connStatus = findViewById(R.id.status);
        Button connectButton = findViewById(R.id.connectButton);


        if (connectRasp!= null && connectRasp.isReady()) {

            hDelayed.postDelayed(new Runnable() {
                @Override
                public void run() {
                    FSM();
                }
            }, 100);

        } else {

            connStatus.setText("Disconnected");
            connectButton.setEnabled(true);
        }



/*    // ##### DEBUG Gabba ---------------------
        if (tcpClient!=null && tcpClient.isConnected() && !tcpClient.isClosed()) {
            tcpClient.sendMessage("prova!!!!");
            Toast.makeText(getApplicationContext(), "Messaggio inviato!", Toast.LENGTH_SHORT).show();
        }else
            Toast.makeText(getApplicationContext(), "Messaggio NON inviato!", Toast.LENGTH_SHORT).show();
*/   // -------------------------------


    }

    // ------------------ AsyncTask ------------------------------
    public class mConnectTask extends AsyncTask<String, String, TcpClient> {

        private TcpClient client = null;



        public boolean isReady(){
            if (client != null && client.isConnected() && !client.isClosed()){
                return true;
            }
            else return false;
        }

        public void sendMessage(String message){
            if(isReady()){
                client.sendMessage(message);
                Log.d("mConnectTask","Message sent!");
            }
            else {
                Log.d("mConnectTask", "Message NOT sent!");
                Toast.makeText(getApplicationContext(), "ERROR, Message NOT sent!", Toast.LENGTH_LONG).show();
            }
        }

        public void closeConnection(){
            if(client!=null){
                client.stopClient();
                client = null;
            }
        }


        @Override
        protected TcpClient doInBackground(String... IP_Port) {


            // TCPClient object to ROBOT
            client = new TcpClient(IP_Port[0],Integer.parseInt(IP_Port[1]),new TcpClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    //Log.d("mConnectTask", message );
                    publishProgress(message);
                }
            });

            client.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //response received from server
            Log.d("mConnectTask", "response " + values[0]);

            //process server response here....
            if ("rs".equals(values[0])) {

                if(running == true) {
                    hDelayed.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            BCIbutton();
                        }
                    }, 200);
                    //Toast.makeText(getApplicationContext(), "Server Disconnected", Toast.LENGTH_LONG).show();
                }

            }

            if ("ServerClose".equals(values[0])) {

                hDelayed.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onResume();
                    }
                }, 200);
                Toast.makeText(getApplicationContext(), "Server Disconnected", Toast.LENGTH_LONG).show();


            }

            if ("ConnectionRefused".equals(values[0])) {

                hDelayed.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onResume();
                    }
                }, 200);
                Toast.makeText(getApplicationContext(), "Connection Refused", Toast.LENGTH_LONG).show();


            }


        }


    }


}