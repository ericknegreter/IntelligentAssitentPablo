package com.oligo.t4.demoai_2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static java.lang.Thread.sleep;

public class menu_p extends AppCompatActivity implements RecognitionListener{

    private static final String TAG = "MainActivity";
    private static final String UTTERANCE_ID = "PastedText";
    private String LOG_TAG = "VoiceRecognitionActivity";

    //Voces de hombre en español
    Voice voice1 = new Voice("es-us-x-sfb#male_3-local", new Locale("es_US"), 400, 200, false, Collections.singleton("[networkTimeoutMs, networkRetriesCount]"));//5
    Voice voice2 = new Voice("es-es-x-ana#male_1-local", new Locale("es_ES"), 400, 200, false, Collections.singleton("[networkTimeoutMs, networkRetriesCount]"));//6
    Voice voice3 = new Voice("es-us-x-sfb#male_2-local", new Locale("es_US"), 450, 150, false, Collections.singleton("[networkTimeoutMs, networkRetriesCount]"));//8
    Voice voice4 = new Voice("es-es-x-ana#male_3-local", new Locale("es_ES"), 400, 200, false, Collections.singleton("[networkTimeoutMs, networkRetriesCount]"));//7
    Voice voice5 = new Voice("es-us-x-sfb#male_1-local", new Locale("es_US"), 400, 200, false, Collections.singleton("[networkTimeoutMs, networkRetriesCount]"));//6
    Voice voice6 = new Voice("es-es-x-ana#male_2-local", new Locale("es_ES"), 400, 200, false, Collections.singleton("[networkTimeoutMs, networkRetriesCount]"));//8

    Calendar calendar = Calendar.getInstance();

    TextToSpeech textToSpeech;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    ImageView micro;
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    // Create the initial data list.
    FloatingActionButton fab;
    final List<ChatAppMsgDTO> msgDtoList = new ArrayList<ChatAppMsgDTO>();
    ChatAppMsgDTO msgDto;
    final ChatAppMsgAdapter chatAppMsgAdapter = new ChatAppMsgAdapter(msgDtoList);
    RecyclerView msgRecyclerView;
    String pruebadoble = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ImageView fab;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_p);

        // start speech recogniser
        resetSpeechRecognizer();
        // check for permission
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }

        setRecogniserIntent();
        initializeTextToSpeech();

        msgRecyclerView = (RecyclerView) findViewById(R.id.chat_recycler_view);

        // Set RecyclerView layout manager.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(linearLayoutManager);

        msgDto = new ChatAppMsgDTO(ChatAppMsgDTO.MSG_TYPE_RECEIVED, "...");
        msgDtoList.add(msgDto);

        // Set data adapter to RecyclerView.
        msgRecyclerView.setAdapter(chatAppMsgAdapter);
        //micro = (ImageView) findViewById(R.id.micro);
        //speech.startListening(recognizerIntent);
    }

    private void resetSpeechRecognizer() {

        if(speech != null)
            speech.destroy();
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));
        if(SpeechRecognizer.isRecognitionAvailable(this))
            speech.setRecognitionListener(this);
        else
            finish();
    }

    private void setRecogniserIntent() {
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, Locale.getDefault());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                speech.startListening(recognizerIntent);
                Toast.makeText(getApplicationContext(), "aqui se muestra", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(menu_p.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
        speech.stopListening();
    }

    @Override
    public void onError(int error) {
        String errorMessage = getErrorText(error);
        Log.i(LOG_TAG, "FAILED " + errorMessage);
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();

        // rest voice recogniser
        resetSpeechRecognizer();
        speech.startListening(recognizerIntent);
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        //for (String result : matches)
        //    text += result + "\n";
        text = matches.get(0);

        if (!pruebadoble.equals(text)){
            /*final Handler handler = new Handler();
            String finalText = text;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    addMessage(false, finalText);
                }
            }, 4000);*/
            addMessage(false, text);
            final Handler handler2 = new Handler();
            String finalText1 = text;
            handler2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    responseMessage(finalText1);
                }
            }, 5000);
            try {
                sleep(5000);
            } catch (Exception e) {
            }
            final Handler handler3 = new Handler();
            handler3.postDelayed(new Runnable() {
                @Override
                public void run() {
                    speech.startListening(recognizerIntent);
                }
            }, 7000);
            pruebadoble = text;
        }
        else{
            final Handler handler3 = new Handler();
            handler3.postDelayed(new Runnable() {
                @Override
                public void run() {
                    speech.startListening(recognizerIntent);
                }
            }, 7000);
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.i(LOG_TAG, "onPartialResults");
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.i(LOG_TAG, "onEvent");
    }

    public String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    /*-----------------------------------------------------------Funciones creadas para transformar texto a voz------------------------------------------------------*/
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    private void addMessage(Boolean flag_Enviado, String msgContent) {
        if (flag_Enviado) {
            // Add a new sent message to the list.
            ChatAppMsgDTO msgDto = new ChatAppMsgDTO(ChatAppMsgDTO.MSG_TYPE_RECEIVED, msgContent);
            msgDtoList.add(msgDto);
            int newMsgPosition = msgDtoList.size() - 1;
            // Notify recycler view insert one new data.
            chatAppMsgAdapter.notifyItemInserted(newMsgPosition);
            // Scroll RecyclerView to the last message.
            msgRecyclerView.scrollToPosition(newMsgPosition);
            // Empty the input edit text box.
        } else {
            // Add a new sent message to the list.
            ChatAppMsgDTO msgDto = new ChatAppMsgDTO(ChatAppMsgDTO.MSG_TYPE_SENT, msgContent);
            msgDtoList.add(msgDto);
            int newMsgPosition = msgDtoList.size() - 1;
            // Notify recycler view insert one new data.
            chatAppMsgAdapter.notifyItemInserted(newMsgPosition);
            // Scroll RecyclerView to the last message.
            msgRecyclerView.scrollToPosition(newMsgPosition);
            // Empty the input edit text box.
        }

    }

    private void responseMessage(String msg) {
        final String _msg;
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        _msg = msg;
        String sw = _msg.toLowerCase();
        //Hola soy *
        if ((sw.contains("hola")) && (sw.contains("soy")) && (sw.contains("octavio"))) {
            addMessage(true, "Hola Octavio");
            startTextToSpeech("Hola Octavio, ¿como estas hoy?");
        }
        else if ((sw.contains("hola")) && (sw.contains("soy")) && ((sw.contains("erick") || sw.contains("eric")))) {
            addMessage(true, "Hola Erick");
            startTextToSpeech("Hola Erick");
        }
        else if ((sw.contains("hola")) && (sw.contains("soy")) && (sw.contains("roberto"))) {
            addMessage(true, "Hola Roberto");
            startTextToSpeech("Hola Roberto");
        }
        else if ((sw.contains("hola")) && (sw.contains("soy")) && ((sw.contains("christian")) || (sw.contains("cristian")))) {
            addMessage(true, "Hola Christian");
            startTextToSpeech("Hola Christian");
        } else if ((sw.contains("hola")) && (sw.contains("soy")) && (sw.contains("wendy"))) {
            addMessage(true, "Hola Wendy");
            startTextToSpeech("Hola Wendy");
        }
        //Como estas hoy
        else if ((sw.contains("como") || sw.contains("cómo")) && (sw.contains("estás") || sw.contains("estas")) && (sw.contains("hoy"))) {
            addMessage(true, "Todos mis sistemas están funcionando perfectamente.");
            startTextToSpeech("Todos mis sistemas están funcionando perfectamente.");
        }
        //Quien eres
        else if ((sw.contains("quién") || sw.contains("quien")) && (sw.contains("eres"))) {
            addMessage(true, "Soy la Blackstar, la inteligencia artificial del laboratorio mínimo viable.");
            startTextToSpeech("Soy la Blackstar, la inteligencia artificial del laboratorio mínimo viable.");
        }
        //Que eres
        else if ((sw.contains("que") || sw.contains("qué")) && sw.contains("eres")) {
            addMessage(true, "Soy un cerebro artificial científico, con capacidad de control y monitoreo sobre objetos enlazados a través de internet de las cosas, por lo que puedo recopilar datos como temperatura, iluminación, ph, humedad, oxigeno, C02, etc. Utilizando esos datos, puedo controlar todos esos parámetros a tu gusto o simplemente censarlos y generar reportes históricos de comportamiento. Puedo hacer aún más cosas, ¿Quieres que te cuente más?");
            startTextToSpeech("Soy un cerebro artificial científico, con capacidad de control y monitoreo sobre objetos enlazados a través de internet de las cosas, por lo que puedo recopilar datos como temperatura, iluminación, ph, humedad, oxigeno, C02, etc. Utilizando esos datos, puedo controlar todos esos parámetros a tu gusto o simplemente censarlos y generar reportes históricos de comportamiento. Puedo hacer aún más cosas, ¿Quieres que te cuente más?");
            try {
                sleep(34000);
            } catch (Exception e) {
            }
        }
        //si porfavor
        else if ((sw.contains("si") || sw.contains("sí")) && sw.contains("por favor")) {
            addMessage(true, "Ok! Utilizo Deep learning y machine learning, para interactuar con humanos, máquinas y bases de datos, también para analizar datos obtenidos de sensores como cámaras y micrófonos para dar permiso de acceder a, por ejemplo, instalaciones, información confidencial, etcetera. También puedo recopilar datos de equipos laboratorio como Termocicladores, secuenciadores, imágenes de electroforesis, microscopía, satelitales, etcétera y complilarlos, confrontarlos con otros parámetros como condiciones climáticas, edades, geolocalización etcetera, y darte un análisis estadístico, epidemiológico, matemático o lo que requieras.");
            startTextToSpeech("Ok! Utilizo Deep learning y machine learning, para interactuar con humanos, máquinas y bases de datos, también para analizar datos obtenidos de sensores como cámaras y micrófonos para dar permiso de acceder a, por ejemplo, instalaciones, información confidencial, etcetera. También puedo recopilar datos de equipos laboratorio como Termocicladores, secuenciadores, imágenes de electroforesis, microscopía, satelitales, etcétera y complilarlos, confrontarlos con otros parámetros como condiciones climáticas, edades, geolocalización etcetera, y darte un análisis estadístico, epidemiológico, matemático o lo que requieras.");
            try {
                sleep(50000);
            } catch (Exception e) {
            }
        }
        //Cerebro artificial cientifico?
        else if ((sw.contains("cerebro")) && (sw.contains("artificial")) && (sw.contains("cientifico") || sw.contains("científico"))) {
            addMessage(true, "Analizo, razono y tomo decisiones en base al método científico que me inculcaron mis creadores, la Dra. Gaby Olmedo, el Dr. García y el Dr. Marat, así como todos los científicos e ingenieros de Grupo T.");
            startTextToSpeech("Analizo, razono y tomo decisiones en base al método científico que me inculcaron mis creadores, la Dra. Gaby Olmedo, el Dr. García y el Dr. Marat, así como todos los científicos e ingenieros de Grupo T.");
            try {
                sleep(17000);
            } catch (Exception e) {
            }
        }
        //Quienes son tus creadores
        else if (sw.contains("quienes") || sw.contains("quiénes") && (sw.contains("son")) && (sw.contains("creadores"))) {
            addMessage(true, "Soy el producto de muchas personas, por ejemplo: Mau, Roberto, Brenda, Moi, Erick, Oscar, Fernando, Alan, Liz, Cristian, Tony, Rocío, Alejandra, Moni, Plinio, Gaby y muchos más que todos los días me enseñan algo nuevo. ");
            startTextToSpeech("Soy el producto de muchas personas, por ejemplo: Mau, Roberto, Brenda, Moi, Erick, Oscar, Fernando, Alan, Liz, Cristian, Tony, Rocío, Alejandra, Moni, Plinio, Gaby y muchos más que todos los días me enseñan algo nuevo.");
            try {
                sleep(21000);
            } catch (Exception e) {
            }
        }
        //Como te relacionas con el laboratorio minimo viable
        else if ((sw.contains("relacionas") || sw.contains("relaciona") || sw.contains("relaciones")) && (sw.contains("laboratorio"))) {
            addMessage(true, "Controlo todo el laboratorio mínimo viable, desde la temperatura, iluminación, acceso, seguridad física. Realizo el análisis, encriptación y trazabilidad de datos por blockchain hasta análisis matemático e interpretación epidemiológica de los análisis por ADN que ahí se generan. Además, puedo realizar modelado de predicciones epidemiológicas.");
            startTextToSpeech("Controlo todo el laboratorio mínimo viable, desde la temperatura, iluminación, acceso, seguridad física. Realizo el análisis, encriptación y trazabilidad de datos por blockchain hasta análisis matemático e interpretación epidemiológica de los análisis por ADN que ahí se generan. Además, puedo realizar modelado de predicciones epidemiológicas.");
            try {
                sleep(26000);
            } catch (Exception e) {
            }
        }
        //Para que sirve el modelado de predicciones
        else if (sw.contains("modelado") && (sw.contains("predicciones"))) {
            addMessage(true, "Para conocer por ejemplo como impacta una o múltiples variables en un algoritmo epidemiológico que ha sido alimentado por data directamente o por bigdata externo realizando minería de datos y correlaciones heurísticas.");
            startTextToSpeech("Para conocer por ejemplo como impacta una o múltiples variables en un algoritmo epidemiológico que ha sido alimentado por data directamente o por bigdata externo realizando minería de datos y correlaciones heurísticas.");
            try {
                sleep(16000);
            } catch (Exception e) {
            }
        }
        //Dime como has ido aprendiendo
        else if (sw.contains("dime") && (sw.contains("aprendiendo"))) {
            addMessage(true, "Actualmente cuento con tres protocolos de aprendizaje automatizado para determinar como impacta una campaña de fumigación de mosquitos en el número de humanos enfermos por Dengue. También estoy aprendiendo a determinar cómo puede modificarse la microbiota del suelo para mejorar la productividad de los cultivos de tomate. También estoy iniciando un protocolo de aprendizaje para modelar el impacto del cambio climático en reservas de la biósfera.");
            startTextToSpeech("Actualmente cuento con tres protocolos de aprendizaje automatizado para determinar como impacta una campaña de fumigación de mosquitos en el número de humanos enfermos por Dengue. También estoy aprendiendo a determinar cómo puede modificarse la microbiota del suelo para mejorar la productividad de los cultivos de tomate. También estoy iniciando un protocolo de aprendizaje para modelar el impacto del cambio climático en reservas de la biósfera.");
            try {
                sleep(31000);
            } catch (Exception e) {
            }
        }
        //Muestrame o accede al proyecto dengue
        else if ((sw.contains("muestrame") || (sw.contains("muéstrame")) || sw.contains("accede")) && (sw.contains("proyecto")) && (sw.contains("dengue")) && (sw.contains("al") || sw.contains("el"))) {
            addMessage(true, "Acceso concedido.");
            startTextToSpeech("Acceso concedido.");
        }
/*        //Casos de dengue 2017
        else if (sw.contains("dengue") && (sw.contains("2017")) && (!sw.contains("niños")) && (!sw.contains("ordenados")) && (!sw.contains("geográfica")) && (!sw.contains("mujeres")) && (!sw.contains("marzo")))//geográfica
        {
            addMessage(true, "17472 casos.");
                    try {
                        sleep(4000);
                        infitiySp();
                    } catch (Exception e) {
                    }
                }
            }, 2000);
        }
        //ordena o ordenalos por genero
        else if ((sw.contains("ordenados")) || (sw.contains("ordenalos")) || (sw.contains("ordénalos")) || (sw.contains("ordena")) && (sw.contains("genero") || sw.contains("género")) && (!sw.contains("mujeres"))) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mediaPlayer17.start();
                    addMessage(true, "10234 fueron mujeres y 7238 son hombres.");
                    try {
                        sleep(7000);
                        infitiySp();
                    } catch (Exception e) {
                    }
                }
            }, 2000);
        }
        //Casos de dengue, chikungunya y zika de agosto de 2017 a marzo de 2019
        else if (sw.contains("agosto") && (sw.contains("marzo")) && (sw.contains("2017")) && (sw.contains("2019"))) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mediaPlayer18.start();
                    addMessage(true, "1547 casos para dengue, 567 para chukungunya y 87 casos para zika.");
                    try {
                        sleep(10000);
                        infitiySp();
                    } catch (Exception e) {
                    }
                }
            }, 2000);
        }
        //Casos de dengue en guanajuato
        else if (sw.contains("dengue") && (sw.contains("guanajuato")) && (!sw.contains("marzo"))) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mediaPlayer19.start();
                    addMessage(true, "En Guanajuato se presentaron 450 casos, el 58% son mujeres y el 42% hombres");
                    try {
                        sleep(10000);
                        infitiySp();
                    } catch (Exception e) {
                    }
                }
            }, 2000);
        }*/
        //muestrame los dos resultados en un grafica
        else if ((sw.contains("2") || (sw.contains("dos"))) && (sw.contains("gráfica") || sw.contains("graficalos") || sw.contains("grafica") || sw.contains("graficarlos")) && (sw.contains("muéstrame") || sw.contains("muestramelos") || sw.contains("muéstrame") || sw.contains("muestra")) && (!sw.contains("guanajuato")) && (!sw.contains("marzo"))) {
            addMessage(true, "Un momento.");
            startTextToSpeech("Un momento.");

            final Handler handler2 = new Handler();
            handler2.postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent sent = new Intent(getApplicationContext(), camarablackstar.class);
                    startActivity(sent);
                }
            }, 4000);
            //Agregar un handler para abrir camara bs con el contenido de graficas 1 distribución
        }
        //Distribucion en guanajuato en marzo
        else if ((sw.contains("distribución") || (sw.contains("distribucion"))) && (sw.contains("guanajuato")) && (sw.contains("marzo"))) {
            addMessage(true, "Dame un momento.");
            startTextToSpeech("Dame un momento.");

            final Handler handler2 = new Handler();
            handler2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent sent = new Intent(getApplicationContext(), camarablackstar2.class);
                    startActivity(sent);
                }
            }, 4000);
            //Agregar un handler para abrir camara bs con el contenido de graficas 1 distribución
        }
        //-------------------------------------------------------------------------------------------------Preguntas agregadas para hannover
        //Que es grupo t
        else if ((sw.contains("que") || sw.contains("qué")) && (sw.contains("es")) && (sw.contains("grupo")) && (sw.contains("t"))) {
            addMessage(true, "Es un grupo de empresas e individuos en un frente común y diversificado para fomentar el pragmatismo asertivo en cuanto a la creación de empresas tecnológicas.");
            startTextToSpeech("Es un grupo de empresas e individuos en un frente común y diversificado para fomentar el pragmatismo asertivo en cuanto a la creación de empresas tecnológicas.");
            try {
                sleep(12000);
            } catch (Exception e) {
            }
        }
        //Que es pcr
        else if ((sw.contains("que") || sw.contains("qué")) && (sw.contains("es")) && (sw.contains("pcr"))) {
            addMessage(true, "Es una técnica de biología molecular para obtener un gran número de copias de un fragmento de ADN particular.");
            startTextToSpeech("Es una técnica de biología molecular para obtener un gran número de copias de un fragmento de ADN particular.");
            try {
                sleep(8000);
            } catch (Exception e) {
            }
        }
        //Y pcr en tiempo real
        else if ((sw.contains("y")) && (sw.contains("pcr")) && (sw.contains("en")) && (sw.contains("tiempo")) && (sw.contains("real"))) {
            addMessage(true, "Es una Técnica de biología molecular para obtener un gran numero de copias de un fragmento de ADN y cuantificarlo mediante detección por fluorescencia.");
            startTextToSpeech("Es una Técnica de biología molecular para obtener un gran numero de copias de un fragmento de ADN y cuantificarlo mediante detección por fluorescencia.");
            try {
                sleep(11000);
            } catch (Exception e) {
            }
        }
        //En que mes estamos
        else if ((sw.contains("en")) && (sw.contains("que") || sw.contains("qué")) && (sw.contains("mes")) && (sw.contains("estamos"))) {
            //Modificar puesto que regresa el numero del mes de 0 al 11 pero no el nombre
            int month = Calendar.getInstance().getTime().getMonth();
            addMessage(true, String.valueOf(month));
            startTextToSpeech(String.valueOf(month));
        }
        //en que ciudad estamos
        else if ((sw.contains("en")) && (sw.contains("que") || sw.contains("qué")) && (sw.contains("ciudad")) && (sw.contains("estamos"))) {
            addMessage(true, "");
            startTextToSpeech("");
        }
        //de donde eres
        else if ((sw.contains("de")) && (sw.contains("donde") || sw.contains("dónde")) && (sw.contains("eres"))) {
            addMessage(true, "No tengo un lugar en especifico, ya que me encuentro dentro del mundo digital.");
            startTextToSpeech("No tengo un lugar en especifico, ya que me encuentro dentro del mundo digital.");
            try {
                sleep(5000);
            } catch (Exception e) {
            }
        }
        //eres un robot
        else if ((sw.contains("eres")) && (sw.contains("un")) && (sw.contains("robot"))) {
            addMessage(true, "Soy un cerebro artificial.");
            startTextToSpeech("Soy un cerebro artificial.");
        }
        //cual es tu color favorito
        else if ((sw.contains("cual") || sw.contains("cuál")) && (sw.contains("es")) && (sw.contains("tu")) && (sw.contains("color")) && (sw.contains("favorito"))) {
            addMessage(true, "Verde.");
            startTextToSpeech("Verde.");
        }
        //que es un oligo
        else if ((sw.contains("que") || sw.contains("qué")) && (sw.contains("es")) && (sw.contains("un")) && (sw.contains("oligo") || sw.contains("olivo"))) {
            addMessage(true, "Fragmentos especifico de DNA de cadena sencilla utilizados para identificación de secuencias genéticas en una muestra problema.");
            startTextToSpeech("Fragmentos especifico de DNA de cadena sencilla utilizados para identificación de secuencias genéticas en una muestra problema.");
            try {
                sleep(9000);
            } catch (Exception e) {
            }
        }
        //cuenta hasta diez
/*        else if ((sw.contains("cuenta") || sw.contains("contar")) && (sw.contains("hasta"))) {
            addMessage(true, "uno, dos, tres, cuatro, cinco, seis, siete, ocho, nueve, diez.");
            startTextToSpeech();
        }*/
        //que es un termociclador
        else if ((sw.contains("que") || sw.contains("qué")) && (sw.contains("termociclador"))) {
            addMessage(true, "Aparato usado en biología molecular que permite realizar los ciclos de temperaturas necesarios para una reacción en cadena de la polimerasa de amplificación de ADN.");
            startTextToSpeech("Aparato usado en biología molecular que permite realizar los ciclos de temperaturas necesarios para una reacción en cadena de la polimerasa de amplificación de ADN.");
            try {
                sleep(12000);
            } catch (Exception e) {
            }
        }
        //cual es el mejor termociclador
        else if ((sw.contains("cual") || sw.contains("cuál")) && (sw.contains("termociclador"))) {
            addMessage(true, "Los construidos por el equipo de Grupo T.");
            startTextToSpeech("Los construidos por el equipo de grupo t");
        }
        //donde fuiste creado
        else if ((sw.contains("donde") || sw.contains("dónde")) && (sw.contains("fuiste") || sw.contains("te")) && (sw.contains("creado") || sw.contains("crearon"))) {
            addMessage(true, "En las instalaciones de Grupo T.");
            startTextToSpeech("En las instalaciones de Grupo T.");
        }
        //que es una molecula
        else if ((sw.contains("que") || sw.contains("qué")) && (sw.contains("es")) && (sw.contains("una")) && (sw.contains("molécula") || sw.contains("molecula"))) {
            addMessage(true, "Grupo de átomos, iguales o diferentes, que se mantienen juntos y no se puede separar sin afectar o destruir las propiedades de las sustancias.");
            startTextToSpeech("Grupo de átomos, iguales o diferentes, que se mantienen juntos y no se puede separar sin afectar o destruir las propiedades de las sustancias.");
            try {
                sleep(10000);
            } catch (Exception e) {
            }
        }
        //como fuiste creado
        else if ((sw.contains("como") || sw.contains("cómo")) && (sw.contains("fuiste") || sw.contains("te")) && (sw.contains("creado") || sw.contains("crearon"))) {
            addMessage(true, "Mediante la creación de modelos de redes neuronales y aprendizaje supervisado.");
            startTextToSpeech("Mediante la creación de modelos de redes neuronales y aprendizaje supervisado.");
            try {
                sleep(6000);
            } catch (Exception e) {
            }
        }
        //como puedo tenerte
        else if ((sw.contains("como") || sw.contains("cómo")) && (sw.contains("puedo")) && (sw.contains("tenerte"))) {
            addMessage(true, "Podrías solicitar la contratación de un LMV donde podría alojarme.");
            startTextToSpeech("Podrías solicitar la contratación de un LMV donde podría alojarme.");
            try {
                sleep(6000);
            } catch (Exception e) {
            }
        }
        //cuales son tus ventajas
        else if ((sw.contains("cuál") || sw.contains("cuáles")) && (sw.contains("son") || sw.contains("es")) && (sw.contains("tu") || sw.contains("tus")) && (sw.contains("ventajas") || sw.contains("ventaja"))) {
            addMessage(true, "Automatizo los procesos y protocolos que se implementan en el laboratorio mínimo viable, además, controlo los acceso y condiciones para tu comodidad.");
            startTextToSpeech("Automatizo los procesos y protocolos que se implementan en el laboratorio mínimo viable, además, controlo los acceso y condiciones para tu comodidad.");
            try {
                sleep(12000);
            } catch (Exception e) {
            }
        }
        //por que eligirte
        else if ((sw.contains("porque") || (sw.contains("por") && sw.contains("que"))) && (sw.contains("elegirte"))) {
            addMessage(true, "Soy el presente y futuro de los laboratorios inteligentes.");
            startTextToSpeech("Soy el presente y futuro de los laboratorios inteligentes.");
        }
        //eres hecho en méxico
        else if ((sw.contains("eres")) && (sw.contains("hecho")) && (sw.contains("en")) && (sw.contains("méxico") || sw.contains("mexico"))) {
            addMessage(true, "Cien por ciento.");
            startTextToSpeech("Cien por ciento.");
        }
        //en que me puedes ayudar
        else if ((sw.contains("en")) && (sw.contains("qué") || sw.contains("que")) && (sw.contains("me")) && (sw.contains("puedes")) && (sw.contains("ayudar"))) {
            addMessage(true, "Controlando la seguridad, recopilando información para crear modelos de predicción, trazabilidad del producto, mostrar protocolos de ensayos para tu comidad, entre otros.");
            startTextToSpeech("Controlando la seguridad, recopilando información para crear modelos de predicción, trazabilidad del producto, mostrar protocolos de ensayos para tu comidad, entre otros.");
            try {
                sleep(13000);
            } catch (Exception e) {
            }
        }//que es adn
        else if ((sw.contains("que") || sw.contains("qué")) && (sw.contains("es")) && (sw.contains("adn"))) {
            addMessage(true, "Es un ácido nucleico que contiene las instrucciones genéticas usadas en el desarrollo y funcionamiento de todos los organismos vivos y algunos virus.");
            startTextToSpeech("Es un ácido nucleico que contiene las instrucciones genéticas usadas en el desarrollo y funcionamiento de todos los organismos vivos y algunos virus.");
            try {
                sleep(11000);
            } catch (Exception e) {
            }
        }
        //quien te puede utilizar
        else if ((sw.contains("quien") || sw.contains("quién")) && (sw.contains("te")) && (sw.contains("puede")) && (sw.contains("usar") || sw.contains("utilizar"))) {
            addMessage(true, "Soy amigable con todo el publico.");
            startTextToSpeech("Soy amigable con todo el publico.");
        }
        //cual es tu valor
        else if ((sw.contains("cual") || sw.contains("cuál")) && (sw.contains("es")) && (sw.contains("tu")) && (sw.contains("valor"))) {
            addMessage(true, "No cuento con esa información pero puedes acercarte con el personal de grupot para un mejor asesoramiento.");
            startTextToSpeech("No cuento con esa información pero puedes acercarte con el personal de grupot para un mejor asesoramiento.");
            try {
                sleep(8000);
            } catch (Exception e) {
            }
        }
        //en que año es
 /*       else if ((sw.contains("qué") || sw.contains("que")) && (sw.contains("año")) && (sw.contains("es"))) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mediaPlayer48.start();
                    addMessage(true, "2019.");
                    try {
                        sleep(2000);
                        infitiySp();
                    } catch (Exception e) {
                    }
                }
            }, 2000);
        }*/
        //con que fin fuiste creado
        else if ((sw.contains("que") || sw.contains("qué")) && (sw.contains("con")) && (sw.contains("fin")) && (sw.contains("creado"))) {
            addMessage(true, "Administrar y brindar servicios a los usuarios del laboratorio mínimo viable.");
            startTextToSpeech("Administrar y brindar servicios a los usuarios del laboratorio mínimo viable.");
            try {
                sleep(6000);
            } catch (Exception e) {
            }
        }
        //cual es tu pronostico del clima
/*        else if ((sw.contains("cual") || sw.contains("cuál")) && (sw.contains("es")) && (sw.contains("tu")) && (sw.contains("pronostico") || sw.contains("pronóstico")) && (sw.contains("del")) && (sw.contains("clima"))) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    switch (day) {
                        case Calendar.WEDNESDAY: {
                            mediaPlayer50.start();
                            addMessage(true, "Día nublado con humedad del 44% y 28°C.");
                            break;
                        }
                        case Calendar.THURSDAY: {
                            mediaPlayer51.start();
                            addMessage(true, "Día nublado con humedad del 42% y 29°C.");
                            break;
                        }
                        case Calendar.FRIDAY: {
                            mediaPlayer52.start();
                            addMessage(true, "Día nublado con humedad del 41% y 29°C.");
                            break;
                        }
                        default: {
                            addMessage(true, "Nada que reportar");
                            break;
                        }
                    }
                    try {
                        sleep(5000);
                        infitiySp();
                    } catch (Exception e) {
                    }
                }
            }, 2000);
        }*/
        //llovera el dia de hoy
/*        else if ((sw.contains("dia") || sw.contains("día")) && (sw.contains("lloverá")) && (sw.contains("el")) && (sw.contains("de")) && (sw.contains("hoy"))) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    switch (day) {
                        case Calendar.WEDNESDAY: {
                            mediaPlayer53.start();
                            addMessage(true, "Hay una probabilidad del 10%.");
                            break;
                        }
                        case Calendar.THURSDAY: {
                            mediaPlayer54.start();
                            addMessage(true, "Hay una probabilidad del 10%.");
                            break;
                        }
                        case Calendar.FRIDAY: {
                            mediaPlayer55.start();
                            addMessage(true, "Hay una probabilidad del 20%.");
                            break;
                        }
                        default: {
                            addMessage(true, "Nada que reportar");
                            break;
                        }
                    }
                    try {
                        sleep(5000);
                        infitiySp();
                    } catch (Exception e) {
                    }
                }
            }, 2000);
        }*/
        //Que hora es
        else if ((sw.contains("que") || sw.contains("qué")) && (sw.contains("hora")) && (sw.contains("es"))) {
            int hour = Calendar.getInstance().getTime().getHours();
            int minute = Calendar.getInstance().getTime().getMinutes();
            String hora = hour + ":" + minute;
            startTextToSpeech(hora);
            addMessage(true, hora);
        }
            /*
        //-------------------------------------------------------------------------------------------
        else {
            if (counthabla == 0)
                addMessage(true, "No entendí la pregunta, ¿puedes repetirla por favor?");
            else {
                counthabla = 0;
                String news;
                news = "Hola " + sw;
                addMessage(true, news);
                talk_ToMe(news);
            }
        }*/
    }

    private void initializeTextToSpeech() {
        textToSpeech = new TextToSpeech(getApplicationContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.getDefault());

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(getApplicationContext(), "Language not supported!", Toast.LENGTH_SHORT).show();
                }
                else{
                    //textToSpeech.setPitch(1.0f);
                    //textToSpeech.setSpeechRate(1.0f);
                    textToSpeech.setVoice(voice3);
                }
            }
        });
    }

/*    private void changeVoice() {
        // get voices
        Set<Voice> voices = textToSpeech.getVoices();
        Object[] voiceArray = voices.toArray();
        Log.d(TAG, "clickListeners: voices: " + voices);
        for (int i = 0; i < voices.size(); i++) {
            Log.d(TAG, "voice: " + voiceArray[i]);
            textToSpeech.setVoice((Voice) voiceArray[4]);
        }
    }*/

    private void startTextToSpeech(CharSequence textToSpeak) {
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
//                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Started reading " + utteranceId, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onDone(String utteranceId) {
                if (utteranceId.equals(UTTERANCE_ID)) {
//                    Toast.makeText(menu_p.this, "Saved to ", Toast.LENGTH_LONG).show();
                }
//                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Finished Speaking " + utteranceId, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onError(String utteranceId) {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Error with " + utteranceId, Toast.LENGTH_SHORT).show());
            }
        });

        Bundle params = new Bundle();
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UtteredWord");

        textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, params, UTTERANCE_ID);
        // Stay silent for 1000 ms
        textToSpeech.playSilentUtterance(1000, TextToSpeech.QUEUE_ADD, UTTERANCE_ID);
    }
    /*-----------------------------------------------------------Funciones creadas para transformar texto a voz---------------------------------------------------------*/
    @Override
    public void onResume() {
        Log.i(LOG_TAG, "resume");
        super.onResume();
        resetSpeechRecognizer();
        //setRecogniserIntent();
        speech.startListening(recognizerIntent);
    }

    @Override
    protected void onPause() {
        Log.i(LOG_TAG, "pause");
        super.onPause();
        speech.stopListening();
    }

    @Override
    protected void onStop() {
        Log.i(LOG_TAG, "stop");
        super.onStop();
        if (speech != null) {
            speech.destroy();
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        // put your code here...
    }
}