package nikifor.fishka;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static java.lang.Math.round;

public class MainActivity extends AppCompatActivity {

    private MapView mapview;
    private ArrayList<PlacemarkMapObject> placeList;
    public static final String EXTRA_MESSAGE = "nikifor.fishka.placeName";

    MapObjectTapListener tapListener = new MapObjectTapListener() {
        @Override
        public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {

//            Toast toast = Toast.makeText(getApplicationContext(),
//                    String.valueOf(mapObject.getUserData()) , Toast.LENGTH_SHORT);  //String.valueOf(point.getLatitude()) + ' ' + String.valueOf(point.getLongitude())
//            toast.show();

            Intent intent = new Intent(getApplicationContext(), PlaceActivity.class);
            intent.putExtra(EXTRA_MESSAGE, String.valueOf(mapObject.getUserData()));
            startActivity(intent);

            return true;
        }
    };

    private Bitmap drawSimpleBitmap(String text, int textSize) {
        Paint paint = new Paint();
        paint.setTextSize(textSize);

        int picSizeX = round(paint.measureText(text)) + 40; //(int) round(text.length() * textSize /1.75) + 20;
        int picSizeY = (int) round(textSize * 1.5);

        Bitmap bitmap = Bitmap.createBitmap(picSizeX, picSizeY, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        // отрисовка плейсмарка
        paint.setColor(Color.RED);
        paint.setStrokeWidth(7);
        paint.setStyle(Paint.Style.FILL);
        RectF rect = new RectF();
        rect.set(0, 0, picSizeX , picSizeY);
        canvas.drawRoundRect(rect, 20, 20, paint);
        paint.setColor(Color.WHITE);
        rect.set(7, 7, picSizeX - 7, picSizeY - 7);
        canvas.drawRoundRect(rect, 20, 20, paint);

        // отрисовка текстa
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(text, 20, textSize + 3 , paint);
        return bitmap;
    }

    private PlacemarkMapObject addPlaceMarker(Double pLant, Double pLong, String name) {
        PlacemarkMapObject marker = mapview.getMap().getMapObjects().addPlacemark(new Point(pLant, pLong),
                ImageProvider.fromBitmap(drawSimpleBitmap(name, 36)));
        marker.setUserData(name);
        marker.addTapListener(tapListener);
        return marker;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey("11d0548e-f96b-440c-9432-70044b046792");
        MapKitFactory.initialize(this);

        setContentView(R.layout.activity_main);
        mapview = (MapView)findViewById(R.id.mapview);
        mapview.getMap().move(
                new CameraPosition(new Point(59.930186, 30.333411), 11.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);

        MapObjectCollection moc = mapview.getMap().getMapObjects();
        placeList = new ArrayList<PlacemarkMapObject>();
        placeList.clear();

        try {
            String result = new PostRequest().execute("http://fishka.herokuapp.com/get_places/").get();
            //result = "{" + result + "}";
            Log.d("result: ", result);
            //JSONObject places = new JSONObject(result);
            JSONArray arrayPlaces = new JSONArray(result); //places.getJSONArray("");
            for (int i = 0; i < arrayPlaces.length(); i++) {
                JSONObject place = arrayPlaces.getJSONObject(i);
                String la = place.getString("lant");
                String lo = place.getString("long");
                Log.d("lant: ", la);
                Log.d("lodg: ", lo);


                PlacemarkMapObject mark = addPlaceMarker(   Double.parseDouble(place.getString("lant")),
                                                            Double.parseDouble(place.getString("long")),
                                                            place.getString("name") );
                placeList.add(mark);

//                MapObjectTapListener tapListener = new MapObjectTapListener() {
//                    @Override
//                    public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
//                        Toast toast = Toast.makeText(getApplicationContext(),
//                                String.valueOf(point.getLatitude()) + ' ' + String.valueOf(point.getLongitude()) , Toast.LENGTH_SHORT);
//                        toast.show();
//                        return true;
//                    }
//                };
//                 moc.addPlacemark(new Point(Double.parseDouble(place.getString("lant")),
//                        Double.parseDouble(place.getString("long"))),
//                        ImageProvider.fromBitmap(drawSimpleBitmap(place.getString("name"), 36))).addTapListener(tapListener);


            }
//            Log.d("------Request: ", v);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onStop() {
        // Вызов onStop нужно передавать инстансам MapView и MapKit.
        this.mapview.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        // Вызов onStart нужно передавать инстансам MapView и MapKit.
        super.onStart();
        MapKitFactory.getInstance().onStart();
        this.mapview.onStart();
    }
}