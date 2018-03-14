package com.quaade94.galgeleg;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TableLayout;

public class Asynkron2AsyncTask extends Activity implements OnClickListener {
    ProgressBar progressBar;
    Button knap1, knap2, knap3, knap3annuller;
    AsyncTask asyncTask3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TableLayout tl = new TableLayout(this);

        EditText editText = new EditText(this);
        editText.setText("Prøv at redigere her efter du har trykket på knapperne");
        tl.addView(editText);

        // se http://bytecrafter.blogspot.com/2008/12/using-horizontal-progress-bar-in.html
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(99);
        tl.addView(progressBar);

        knap3 = new Button(this);
        knap3.setText("AsyncTask med løbende opdatering og resultat");
        tl.addView(knap3);

        knap3annuller = new Button(this);
        knap3annuller.setText("Annullér asyncTask3");
        knap3annuller.setVisibility(View.GONE); // Skjul knappen
        tl.addView(knap3annuller);

        setContentView(tl);

        knap1.setOnClickListener(this);
        knap2.setOnClickListener(this);
        knap3.setOnClickListener(this);
        knap3annuller.setOnClickListener(this);
    }

    public void onClick(View hvadBlevDerKlikketPå) {

        if (hvadBlevDerKlikketPå == knap3) {

            // en AsyncTask hvor input er int, progress er double, resultat er String
            asyncTask3 = new AsyncTask<Integer, Double, String>() {
                @Override
                protected String doInBackground(Integer... param) {
                    int antalSkridt = param[0];
                    int ventetidPrSkridtIMilisekunder = param[1];
                    for (int i = 0; i < antalSkridt; i++) {

                        SystemClock.sleep(ventetidPrSkridtIMilisekunder);
                        if (isCancelled()) {
                            return null; // stop uden resultat
                        }
                        double procent = i * 100.0 / antalSkridt;
                        double resttidISekunder = (antalSkridt - i) * ventetidPrSkridtIMilisekunder / 100 / 10.0;
                        publishProgress(procent, resttidISekunder); // sendes som parameter til onProgressUpdate()
                    }
                    return "færdig med doInBackground()!"; // resultat (String) sendes til onPostExecute()
                }

                @Override
                protected void onProgressUpdate(Double... progress) {
                    double procent = progress[0];
                    double resttidISekunder = progress[1];
                    knap3.setText("arbejder - " + procent + "% færdig, mangler " + resttidISekunder + " sekunder endnu");
                    progressBar.setProgress((int) procent);
                }

                @Override
                protected void onPostExecute(String resultat) {
                    knap3.setText(resultat);
                    knap3annuller.setVisibility(View.GONE); // Skjul knappen
                }

                @Override
                protected void onCancelled() {
                    knap3.setText("Annulleret før tid");
                    knap3annuller.setVisibility(View.GONE); // Skjul knappen
                }
            }.execute(100, 50);
            knap3annuller.setVisibility(View.VISIBLE);
        } else if (hvadBlevDerKlikketPå == knap3annuller) {
            asyncTask3.cancel(false);
        }
    }
}