package mad.com.inclass06;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.github.mikephil.charting.utils.MPPointD;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.http.entity.StringEntity;

public class MainActivity extends AppCompatActivity {
    ScatterChart mChart;
    Entry en;
    ArrayList<Data> finalData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mChart=(ScatterChart) findViewById(R.id.scatterchart);
        mChart.setOnChartGestureListener(onTouchListener());
         new AppAsynTask().execute("");

    }
    private OnChartGestureListener onTouchListener(){
        return new OnChartGestureListener() {
            Highlight he;
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                he=mChart.getHighlightByTouchPoint(me.getX(),me.getY());
                if(he!=null)
                Log.d("point",he.getX()+" "+he.getY());
            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                if (he != null) {
                    Entry e = mChart.getScatterData().getEntryForHighlight(he);
                    for (Data d : finalData) {
                        if (d.getCost().equals("" + e.getX()) && d.getSales().equals("" + e.getY())) {
                            MPPointD values = mChart.getValuesByTouchPoint(me.getX(), me.getY(), YAxis.AxisDependency.LEFT);

                        try {
                                JSONObject json1 = new JSONObject();
                                json1.put("id", d.getId());
                                json1.put("cost", values.x+"" );
                                json1.put("sales", values.y+"");
                                Log.d("data",json1.toString());
                                StringEntity se = new StringEntity(json1.toString());
                                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                                new AsyncTaskAPI().execute(se);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            } catch (UnsupportedEncodingException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }
            }

            @Override
            public void onChartLongPressed(MotionEvent me) {

            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {

            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {

            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {

            }


        };
    }

    class AsyncTaskAPI extends AsyncTask<StringEntity, Void, JSONObject> {

        String response;

        @Override
        protected JSONObject doInBackground(StringEntity... params) {
            HttpClient Client = new DefaultHttpClient();
            JSONObject result = new JSONObject();

            try {

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://52.42.214.119:3000/update/");
                httppost.setEntity(params[0]);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();

                response = httpclient.execute(httppost, responseHandler);


            } catch (Exception ex) {
                // failed
                Log.d("demo", ex.toString());
            }

            try {
                result = new JSONObject(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(JSONObject s) {
            super.onPostExecute(s);
            try {
                if (s.getString("status").equals("Success")) {
                   new AppAsynTask().execute("");
                }
            } catch (Exception ex) {

            }
        }
    }


    public class AppAsynTask extends AsyncTask<String, Void, ArrayList<Data>> {
        ProgressDialog pd;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected ArrayList<Data> doInBackground(String... params) {
                       try {
                StringBuilder sr = new StringBuilder();
                String urlPath = "http://52.42.214.119:3000/all";
                URL url = new URL(urlPath);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                int requestCode = con.getResponseCode();
                if (requestCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String line = "";
                    while ((line = br.readLine()) != null) {
                        sr.append(line);
                    }

                    return JsonUtil.UserJsonParser.getUserJson(sr.toString());
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Data> dataPoints) {
            super.onPostExecute(dataPoints);
            finalData= new ArrayList<>();
            finalData.addAll(dataPoints);
            mChart.setData(null);
            buildUI(dataPoints);

        }
    }


    private void buildUI(ArrayList<Data> dataPoints) {

        mChart.setDrawGridBackground(false);
        mChart.setTouchEnabled(true);
        mChart.setMaxHighlightDistance(50f);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setMaxVisibleValueCount(200);
        mChart.setPinchZoom(true);
        YAxis yl = mChart.getAxisLeft();
       yl.setAxisMinValue(0f); // this replaces setStartAtZero(true)

        mChart.getAxisRight().setEnabled(false);

        XAxis xl = mChart.getXAxis();
        xl.setAxisMinValue(0f);
        xl.setDrawGridLines(false);
        ScatterDataSet type1 = new ScatterDataSet(getDataOfType("Type 1",dataPoints), "Type 1");
        type1.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        type1.setScatterShapeHoleColor(Color.RED);
        type1.setScatterShapeHoleRadius(3f);
        type1.setColor(Color.RED);
        ScatterDataSet type2 = new ScatterDataSet(getDataOfType("Type 2",dataPoints), "Type 2");
        type2.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        type2.setScatterShapeHoleColor(Color.BLUE);
        type2.setScatterShapeHoleRadius(3f);
        type2.setColor(Color.BLUE);
        ScatterDataSet type3 = new ScatterDataSet(getDataOfType("Type 3",dataPoints), "Type 3");
        type3.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        type3.setScatterShapeHoleColor(Color.GREEN);
        type3.setScatterShapeHoleRadius(3f);
        type3.setColor(Color.GREEN);
        type1.setScatterShapeSize(8f);
        type2.setScatterShapeSize(8f);
        type3.setScatterShapeSize(8f);


        ArrayList<IScatterDataSet> dataSets = new ArrayList<IScatterDataSet>();
        dataSets.add(type1); // add the datasets
        dataSets.add(type2);
        dataSets.add(type3);

        // create a data object with the datasets
        ScatterData data = new ScatterData(dataSets);

        mChart.setData(data);
        mChart.notifyDataSetChanged();
        mChart.invalidate();

    }

    private ArrayList<Entry> getDataOfType(String s, ArrayList<Data> dataPoints) {
        ArrayList<Entry> matching=new ArrayList<Entry>();
        switch(s){
            case "Type 1":
                    for(Data d:dataPoints){
                        if(d.getItem().equals("Type 1"))
                            matching.add(new Entry(Float.parseFloat(d.getCost()),Float.parseFloat(d.getSales())));
                    }

                break;
            case "Type 2":
                for(Data d:dataPoints){
                    if(d.getItem().equals("Type 2"))
                        matching.add(new Entry(Float.parseFloat(d.getCost()),Float.parseFloat(d.getSales())));
                }
                break;
            case "Type 3":
                for(Data d:dataPoints){
                    if(d.getItem().equals("Type 3"))
                        matching.add(new Entry(Float.parseFloat(d.getCost()),Float.parseFloat(d.getSales())));
                }
                break;
        }
        Collections.sort(matching, new EntryXComparator());

        return matching;

    }



}
