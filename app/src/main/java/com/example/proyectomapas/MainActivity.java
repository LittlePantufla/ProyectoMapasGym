package com.example.proyectomapas;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Se declaran las variables que se usarán para enlazar por las ids
    private TextView textView;
    private ImageView imageView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Se enlazan las ids
        textView = findViewById(R.id.texto1);
        imageView = findViewById(R.id.gym);
        progressBar = findViewById(R.id.barraProceso);

        // Crear y ejecutar el Thread para poder simular la carga de una imagen
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Se simula una operación que toma tiempo (prácticamente 7 segundos)
                try {
                    Thread.sleep(7000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Ahora se tiene que actualizar la interfaz de usuario
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Ocultar la barra de progreso
                        progressBar.setVisibility(View.GONE);
                        // Actualizar el texto
                        textView.setText("Imagen cargada correctamente");
                        // Hacer visible el ImageView
                        imageView.setVisibility(View.VISIBLE);
                        // Actualizar la imagen en el caso que sea necesaria
                        imageView.setImageResource(R.drawable.gym);
                    }
                });
            }
        });

        // Se inicia el hilo creado
        thread.start();

        // Carga la configuración del mapa usando las preferencias predeterminadas
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        // Obtiene la referencia al componente MapView del layout
        MapView mapView = findViewById(R.id.mapView);
        // Establece la fuente de azulejos del mapa a MAPNIK (mapa estándar)
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        // Activa los controles de zoom en el mapa
        mapView.setBuiltInZoomControls(true);
        // Activa el control multitáctil para el mapa (zoom con dos dedos)
        mapView.setMultiTouchControls(true);

        // Coordenadas del IP Santo Tomás, Chile
        double ipSantoTomasLatitud = -33.4493141; // Latitud del IP Santo Tomás
        double ipSantoTomasLongitud = -70.6624069; // Longitud del IP Santo Tomás

        // Crear objetos GeoPoint para las coordenadas definidas
        GeoPoint IPsantoTomasPoint = new GeoPoint(ipSantoTomasLatitud, ipSantoTomasLongitud);
        // Configura la vista inicial del mapa centrada en el IP Santo Tomás con un nivel de zoom de 15
        mapView.getController().setZoom(15.0);
        // Centra el mapa en el punto de Santiago
        mapView.getController().setCenter(IPsantoTomasPoint);

        // Crear un marcador para el IP Santo Tomás y agregarlo al mapa
        Marker marcadorSantoTomas = new Marker(mapView);
        marcadorSantoTomas.setPosition(IPsantoTomasPoint);
        marcadorSantoTomas.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marcadorSantoTomas.setTitle("IP Santo Tomás, Chile");
        marcadorSantoTomas.setSnippet("Un Instituto Tomista");
        mapView.getOverlays().add(marcadorSantoTomas);

        // Coordenadas Parque
        double parqueLatitud = -33.460973; // Latitud del Parque
        double parqueLongitud = -70.640032; // Longitud del Parque

        // Crear objetos GeoPoint para las coordenadas definidas
        GeoPoint parquePoint = new GeoPoint(parqueLatitud, parqueLongitud);

        // Crear un marcador para el Parque y agregarlo al mapa
        Marker marcadorParque = new Marker(mapView);
        marcadorParque.setPosition(parquePoint);
        marcadorParque.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marcadorParque.setTitle("Gimmnasio Smart Fit Grajales");
        marcadorParque.setSnippet("Un Parque");
        mapView.getOverlays().add(marcadorParque);

        // Crear una línea que conecte los 2 marcadores
        Polyline linea = new Polyline();
        linea.addPoint(IPsantoTomasPoint);
        linea.addPoint(parquePoint);
        linea.setColor(0xFF0000FF);
        linea.setWidth(5);
        mapView.getOverlayManager().add(linea);

        // Configurar el Spinner para cambiar el tipo de mapa
        Spinner mapTypeSpinner = findViewById(R.id.mapTypeSpinner);
        String[] mapTypes = {"Mapa Normal", "Mapa de Transporte", "Mapa Topográfico"};

        // Crear un ArrayAdapter para poblar el Spinner con los tipos de mapas
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mapTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mapTypeSpinner.setAdapter(adapter);

        // Listener para detectar cambios en la selección del Spinner
        mapTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mapView.setTileSource(TileSourceFactory.MAPNIK);
                        break;
                    case 1:
                        mapView.setTileSource(new XYTileSource(
                                "PublicTransport",
                                0, 18, 256, ".png", new String[]{
                                "https://tile.memomaps.de/tilegen/"}));
                        break;
                    case 2:
                        mapView.setTileSource(new XYTileSource(
                                "USGS_Satellite", 0, 18, 256, ".png", new String[]{
                                "https://a.tile.opentopomap.org/",
                                "https://b.tile.opentopomap.org/",
                                "https://c.tile.opentopomap.org/"}));
                        break;
                }
            }

            // No se hace nada cuando no se selecciona ningún elemento
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
