package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static int n = 14;
    private static int N = 256;
    private static double W = 2000;
    private double direct;
    private double direct1;
    private double direct2;
    private double opposite;
    private double opposite1;
    private double opposite2;
    private double[] signal = new double[N];
    TextView res;
    double fftTime;
    double dftTime;
    long ctime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>(generateSignal(signal));
        ctime = System.currentTimeMillis();
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(fourier());
        dftTime = (double) (System.currentTimeMillis() - ctime);
        ctime = System.currentTimeMillis();
        LineGraphSeries<DataPoint> series3 = new LineGraphSeries<>(FFT());
        fftTime = (double) (System.currentTimeMillis() - ctime);
        GraphView graph = findViewById(R.id.graph1);
        customizationGraph(graph, series1, -14, 14);
        graph = findViewById(R.id.graph2);
        customizationGraph(graph, series2, 0, 800);
        graph = findViewById(R.id.graph3);
        customizationGraph(graph, series3, 0, 800);

        Toast.makeText(getApplicationContext(), String.format(
                "Results for 1000 iteration: fft = %s ms; dft = %s ms",
                Math.round(fftTime),
                Math.round(dftTime)
        ), Toast.LENGTH_LONG).show();
    }

    private DataPoint[] generateSignal(double[] res) {
        float fi, A, x;
        DataPoint[] data = new DataPoint[N];
        Random rand = new Random();
        for (int i = 0; i < N; i++) {
            A = rand.nextFloat();
            fi = rand.nextFloat();
            x = 0;
            for (int j = 0; j < n; j++) {
                x += A * Math.sin(W * i + fi);
            }
            res[i] = x;
            data[i] = new DataPoint(i, x);
        }
        return data;
    }

    private DataPoint[] fourier() {
        DataPoint[] res = new DataPoint[N];
        for (int i = 0; i < N; i++) {
            direct = opposite = 0;
            for (int j = 0; j < N - 1; j++) {
                direct += signal[j] * Math.cos(2 * Math.PI * i * j / N);
                opposite -= signal[j] * Math.sin(2 * Math.PI * i * j / N);
            }
            res[i] = new DataPoint(i, Math.sqrt(Math.pow(direct, 2) + Math.pow(opposite, 2)));
        }
        return res;
    }

    private DataPoint[] FFT() {
        DataPoint[] res = new DataPoint[N];
        for (int p = 0; p < N; p++) {
            double temp = 4 * Math.PI * p / N;
            direct1 = direct2 = opposite1 = opposite2 = 0;
            for (int i = 0; i < N / 2 - 1; i++) {
                double tmp = 4 * Math.PI * p * i / N;
                direct1 += signal[2 * i] * Math.cos(tmp);
                opposite1 += signal[2 * i] * Math.sin(tmp);
                direct2 += signal[2 * i + 1] * Math.cos(tmp);
                opposite2 += signal[2 * i + 1] * Math.sin(tmp);
            }
            if (p < N / 2) {
                res[p] = new DataPoint(p, Math.sqrt(Math.pow(
                                (direct2 + direct1 * Math.cos(temp)), 2
                ) + Math.pow(
                        (opposite2 + opposite1 * Math.sin(temp)), 2))
                );
            } else {
                res[p] = new DataPoint(p, Math.sqrt(Math.pow(
                        (direct2 - direct1 * Math.cos(temp)), 2
                ) + Math.pow(
                        (opposite2 - opposite1 * Math.sin(temp)), 2))
                );
            }
        }
        return res;
    }

    private void customizationGraph(GraphView graph, LineGraphSeries line, int miny, int maxy) {
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(20);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMaxY(maxy);
        graph.getViewport().setMinY(miny);
        graph.getViewport().setScrollable(true);
        graph.addSeries(line);
    }

}
