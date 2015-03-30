package com.example.roobusapp;

import java.util.Locale;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.widget.Toast;


public class MessageSpoken implements TextToSpeech.OnInitListener {

	private Context context;
	private TextToSpeech tts;

	MessageSpoken(Context context) {
		this.context = context;

	}

	public void MessageFromAlaram(String route, double alaram_end_time) {

		final double speak_time = alaram_end_time;
		final String selected_route = route;
		String temp_spoken_text;

		if (alaram_end_time == 1) {
			 temp_spoken_text = selected_route + " bus will arrive in"
						+ speak_time + "  minute";

		} else {
			 temp_spoken_text = selected_route + " bus will arrive in"
						+ speak_time + "  minutes"+"                                   ";
		}

		if (alaram_end_time == 0) {
			temp_spoken_text = "The RooBus is waiting for you to get on board .Hurry up!!"+"                               ";
		}

		final String spoken_text = temp_spoken_text;

		
		tts = new TextToSpeech(context, new OnInitListener() {
			@Override
			public void onInit(int status) {
				// TODO Auto-generated method stub
				if (status == TextToSpeech.SUCCESS) {
					// isInitTTS = true;
					if (tts.isLanguageAvailable(Locale.US) >= 0) {
						tts.setLanguage(Locale.US);
						tts.setPitch(0.8f);
						tts.setSpeechRate(1.1f);
						if (tts != null) {
							tts.speak(spoken_text, TextToSpeech.QUEUE_ADD, null);
							
							Toast.makeText(
									context,
									"Attention !!",
									Toast.LENGTH_LONG).show();
	
						}

					}
				} else {
					// Intent intn = new Intent(Engine.ACTION_INSTALL_TTS_DATA);
					// startActivity(intn);
					Toast.makeText(
							context,
							"Error  !!",
							Toast.LENGTH_LONG).show();

									}
			}
		});
		
	

			}

	@Override
	public void onInit(int arg0) {
		

	}
	
	
	public void onDestroy() {
	
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        
        
    }

}
