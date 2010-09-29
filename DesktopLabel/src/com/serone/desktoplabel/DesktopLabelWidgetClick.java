package com.serone.desktoplabel;

//NOTA DE LICENCIA
//Bajo licencia GNU GPL 3 o posterior
//Ver gpl.html

// Imports necesarios
import com.serone.desktoplabel.R;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * Configurador del widget en tiempo de ejecución
 * @author doctor@serone.org
 */
public class DesktopLabelWidgetClick extends Activity implements OnClickListener, OnCheckedChangeListener
{
	// ID del widget
	private int idWidget=AppWidgetManager.INVALID_APPWIDGET_ID;
	
	// Controles
	private EditText etiqueta;
	
	private Button botonOK;
	private Button botonColorFondo;
	private Button botonColorTexto;
	private Button botonIcono;
	
	private CheckBox checkboxColorFondo;
	private CheckBox checkboxColorTexto;
	private CheckBox checkboxIcono;

	private ImageView imagenWidget;
	private TextView etiquetaWidget;

	// Configuración final
	private int colorFondo=Color.BLACK;
	private boolean colorFondoActivo=true;
	private int colorTexto=Color.WHITE;
	private boolean colorTextoActivo=true;
	private int icono=1;
	private boolean iconoActivo=true;
	private int posicionIcono=1; // 1=izquierda, 2=derecha
	
	// Diálogos
	
	// Coloreando ahora (0=nada, 1=fondo, 2=texto)
	int coloreandoAhora=0;
	
    /**
     * Evento de creación
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Context contexto=this.getApplicationContext();
        
        // Especificamos el layout
        setContentView(R.layout.layout_actividad_click);
    
        // Cogemos el id desde el intent (lo hemos metido al asociar el evento)
        // NOTA: Como los extras daban problemas, hemos metido el id como una
        // uri de datos Android. Ver la documentación para más detalles.
        Intent intent=this.getIntent(); 
        String uri=intent.getData().toString();
        
        this.idWidget=new Integer(uri.split("//")[1]);

        // Si no ha ido bien...
        if(this.idWidget==AppWidgetManager.INVALID_APPWIDGET_ID)
        {
        	Utils.logError("No se ha podido coger el ID del widget, cancelando configuración.");

        	// Retornamos CANCEL
        	Intent cancelResult=new Intent();
        	cancelResult.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, this.idWidget);
            this.setResult(RESULT_CANCELED, cancelResult);
            
            // Mostramos al usuario
            // FIXME: Ántes de activar hacer multiidioma
            //Utils.mostrarToast(contexto, "No se ha podido coger el ID del widget, cancelando configuración.");
            
        	// Salimos sin hacer nada más
        	this.finish();
        }
        else Utils.logAviso("Reconfigurando widget #"+this.idWidget);

        // Cogemos los controles
        this.etiqueta=(EditText)findViewById(R.id.etiqueta);
        
        this.botonOK=(Button)findViewById(R.id.botonAceptar);
        this.botonOK.setOnClickListener(this);
        this.botonColorFondo=(Button)findViewById(R.id.botonColorFondo);
        this.botonColorFondo.setOnClickListener(this);
        this.botonColorTexto=(Button)findViewById(R.id.botonColorTexto);
        this.botonColorTexto.setOnClickListener(this);
        this.botonIcono=(Button)findViewById(R.id.botonIcono);
        this.botonIcono.setOnClickListener(this);

        this.checkboxColorFondo=(CheckBox)findViewById(R.id.checkboxColorFondoTransparente);
        this.checkboxColorFondo.setOnCheckedChangeListener(this);
        this.checkboxColorTexto=(CheckBox)findViewById(R.id.checkboxColorTextoTransparente);
        this.checkboxColorTexto.setOnCheckedChangeListener(this);
        this.checkboxIcono=(CheckBox)findViewById(R.id.checkboxSinIcono);
        this.checkboxIcono.setOnCheckedChangeListener(this);

        this.imagenWidget=(ImageView)findViewById(R.id.imagenWidget);
        this.etiquetaWidget=(TextView)findViewById(R.id.etiquetaWidget);

        // Configuramos la imagen
		this.imagenWidget.setMaxHeight(60);
		this.imagenWidget.setMaxWidth(60);
		
        // Reconfiguramos la vista con la configuración actual
        SharedPreferences prefs=contexto.getSharedPreferences("DesktopLabel", 0);
        
        this.etiqueta.setText(prefs.getString("Widget="+this.idWidget+" (Etiqueta)", "DesktopLabel"));
        
        this.colorFondo=prefs.getInt("Widget="+this.idWidget+" (ColorFondo)", Color.BLACK);
        boolean colorFondoActivoConfig=prefs.getBoolean("Widget="+this.idWidget+" (MostrarColorFondo)", true);
        this.checkboxColorFondo.setChecked(!colorFondoActivoConfig);
        
        this.colorTexto=prefs.getInt("Widget="+this.idWidget+" (ColorTexto)", Color.WHITE);
        boolean colorTextoActivoConfig=prefs.getBoolean("Widget="+this.idWidget+" (MostrarColorTexto)", true);
        this.checkboxColorTexto.setChecked(!colorTextoActivoConfig);
        
        this.icono=prefs.getInt("Widget="+this.idWidget+" (Icono)", 1);
        boolean iconoActivoConfig=prefs.getBoolean("Widget="+this.idWidget+" (MostrarIcono)", true);
        this.checkboxIcono.setChecked(!iconoActivoConfig);
        
        // Actualizamos la vista previa
        this.actualizarVistaPrevia();
    }

    /**
     * Eventos del botón
     */
	public void onClick(View v)
	{
		// Según el botón
		if(v==(View)this.botonOK)
		{
			// Se ha pulsado el botón, guardamos la cadena en las SharedPreferences
			// y finalizamos el configurador
			Context contexto=this.getApplicationContext();
	
			// Cogemos la etiqueta
			String etiquetaFinal=this.etiqueta.getText().toString();
			
			// Guardamos
	        SharedPreferences.Editor prefs=contexto.getSharedPreferences("DesktopLabel", 0).edit();
	        
	        prefs.putString( "Widget="+this.idWidget+" (Etiqueta)", 		 etiquetaFinal);

	        prefs.putInt(	 "Widget="+this.idWidget+" (ColorFondo)", 		 this.colorFondo);
	        prefs.putBoolean("Widget="+this.idWidget+" (MostrarColorFondo)", this.colorFondoActivo);

	        prefs.putInt(	 "Widget="+this.idWidget+" (ColorTexto)", 		 this.colorTexto);
	        prefs.putBoolean("Widget="+this.idWidget+" (MostrarColorTexto)", this.colorTextoActivo);
	        
	        prefs.putInt(	 "Widget="+this.idWidget+" (Icono)", 			 this.icono);
	        prefs.putBoolean("Widget="+this.idWidget+" (MostrarIcono)", 	 this.iconoActivo);
	        prefs.putInt(    "Widget="+this.idWidget+" (PosicionIcono)", 	 this.posicionIcono);
	        
	        prefs.commit();
			
	        // Ponemos el result a OK y retornamos el ID
	    	Intent okResult=new Intent();
	    	okResult.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, this.idWidget);
	        this.setResult(RESULT_OK, okResult);

	        // Cogemos las dimensiones del widget
	        SharedPreferences pref=contexto.getSharedPreferences("DesktopLabel", 0);
	        
	        int anchura=pref.getInt( "Widget="+this.idWidget+" (Anchura)", 0);
	        int altura=pref.getInt("Widget="+this.idWidget+" (Altura)", 0);
	        
	        // Forzamos la primera actualización
	        AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(contexto);
	        
	        DesktopLabelWidgetStatic.actualizar(
	        	contexto, appWidgetManager, this.idWidget, etiquetaFinal,
	        	this.icono, this.iconoActivo, this.colorFondo, this.colorFondoActivo,
	        	this.colorTexto, this.colorTextoActivo, anchura, altura, 
	        	this.posicionIcono);
	        
            // Mostramos al usuario
	        Utils.mostrarToast(contexto, this.getString(R.string.widget_actualizado));
            
	        // Salimos
	    	Utils.logInfo("Configuración del widget completada (etiqueta="+etiquetaFinal+")");
	    	this.finish();
		}
		else if(v==(View)this.botonColorFondo)
		{
			this.coloreandoAhora=1;
			
			// Mostramos la actividad
            Intent nuevoIntent = new Intent(v.getContext(), SeroneWidgetColor.class);
            this.startActivityForResult(nuevoIntent, 0);
		}
		else if(v==(View)this.botonColorTexto)
		{
			this.coloreandoAhora=2;
			
			// Mostramos la actividad
            Intent nuevoIntent = new Intent(v.getContext(), SeroneWidgetColor.class);
            this.startActivityForResult(nuevoIntent, 1);
		}
		else if(v==(View)this.botonIcono)
		{
			// Mostramos la actividad
            Intent nuevoIntent = new Intent(v.getContext(), SeroneWidgetIcono.class);
            this.startActivityForResult(nuevoIntent, 2);
		}
	}
	
	/**
	 * Actualiza la vista previa
	 */
	public void actualizarVistaPrevia()
	{
		this.imagenWidget.setBackgroundColor(this.colorFondo);
        this.etiquetaWidget.setBackgroundColor(this.colorFondo);
        
		this.etiquetaWidget.setTextColor(this.colorTexto);
		
		this.cambiarIcono(this.icono);
	}
	
	/**
	 * Atiende a los cambios de icono 
	 */
	public void cambiarIcono(int numIcono)
	{	
		this.icono=numIcono;
		this.iconoActivo=!this.checkboxColorTexto.isChecked();

		// Cambiamos la visibilidad del icono
		if(this.checkboxIcono.isChecked())
		{
			// Cambiamos el margen
			this.imagenWidget.setPadding(5, 5, 5, 5);

	        // Configuramos la imagen
			this.imagenWidget.setMaxHeight(60);
			this.imagenWidget.setMaxWidth(0);
			
		    // Quitamos el icono
			this.imagenWidget.setImageResource(R.drawable.vacio);
			this.imagenWidget.setVisibility(View.GONE); 
		}
		else
		{
			// Cambiamos el margen
			this.imagenWidget.setPadding(10, 10, 10, 10);

	        // Configuramos la imagen
			this.imagenWidget.setMaxHeight(60);
			this.imagenWidget.setMaxWidth(60);
			
		    // Cambiamos el icono
			     if(numIcono==1)  this.imagenWidget.setImageResource(R.drawable.icono001);
			else if(numIcono==2)  this.imagenWidget.setImageResource(R.drawable.icono002);
			else if(numIcono==3)  this.imagenWidget.setImageResource(R.drawable.icono003);
			else if(numIcono==4)  this.imagenWidget.setImageResource(R.drawable.icono004);
			else if(numIcono==5)  this.imagenWidget.setImageResource(R.drawable.icono005);
			else if(numIcono==6)  this.imagenWidget.setImageResource(R.drawable.icono006);
			else if(numIcono==7)  this.imagenWidget.setImageResource(R.drawable.icono007);
			else if(numIcono==8)  this.imagenWidget.setImageResource(R.drawable.icono008);
			else if(numIcono==9)  this.imagenWidget.setImageResource(R.drawable.icono009);
			else if(numIcono==10) this.imagenWidget.setImageResource(R.drawable.icono010);
			else if(numIcono==11) this.imagenWidget.setImageResource(R.drawable.icono011);
			else if(numIcono==12) this.imagenWidget.setImageResource(R.drawable.icono012);
			else if(numIcono==13) this.imagenWidget.setImageResource(R.drawable.icono013);
			else if(numIcono==14) this.imagenWidget.setImageResource(R.drawable.icono014);
			else if(numIcono==15) this.imagenWidget.setImageResource(R.drawable.icono015);
			else if(numIcono==16) this.imagenWidget.setImageResource(R.drawable.icono016);
			else if(numIcono==17) this.imagenWidget.setImageResource(R.drawable.icono017);
			else if(numIcono==18) this.imagenWidget.setImageResource(R.drawable.icono018);
			else if(numIcono==19) this.imagenWidget.setImageResource(R.drawable.icono019);
			else if(numIcono==20) this.imagenWidget.setImageResource(R.drawable.icono020);
			else if(numIcono==21) this.imagenWidget.setImageResource(R.drawable.icono021);
			else if(numIcono==22) this.imagenWidget.setImageResource(R.drawable.icono022);
			else if(numIcono==23) this.imagenWidget.setImageResource(R.drawable.icono023);
			else if(numIcono==24) this.imagenWidget.setImageResource(R.drawable.icono024);
			else if(numIcono==25) this.imagenWidget.setImageResource(R.drawable.icono025);
			else if(numIcono==26) this.imagenWidget.setImageResource(R.drawable.icono026);
			else if(numIcono==27) this.imagenWidget.setImageResource(R.drawable.icono027);
			else if(numIcono==28) this.imagenWidget.setImageResource(R.drawable.icono028);
		}
	}
	
	/**
	 * Atiende a los cambios de color y de icono
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch(requestCode)
		{
			case 0:
			{
				// Color de fondo
				this.colorFondo=resultCode;
				this.colorFondoActivo=!this.checkboxColorFondo.isChecked();
				
				// Coloreamos
				if(this.checkboxColorFondo.isChecked())
				{
					this.imagenWidget.setBackgroundColor(Color.TRANSPARENT);
			        this.etiquetaWidget.setBackgroundColor(Color.TRANSPARENT);
				}
				else
				{
					this.imagenWidget.setBackgroundColor(resultCode);
			        this.etiquetaWidget.setBackgroundColor(resultCode);
				}
				
		        // Reseteamos
		        this.coloreandoAhora=0;
		        
				break;
			}
			
			case 1:
			{
				// Color de texto
				this.colorTexto=resultCode;
				this.colorTextoActivo=!this.checkboxColorTexto.isChecked();
				
				// Coloreamos
				if(this.checkboxColorTexto.isChecked())
				{
					this.etiquetaWidget.setTextColor(Color.TRANSPARENT);
				}
				else
				{
					this.etiquetaWidget.setTextColor(resultCode);
				}
				
		        // Reseteamos
		        this.coloreandoAhora=0;
		        
				break;
			}
			
			case 2:
			{
				// Color de icono
				break;
			}
		}
	}
	
	/**
	 * Listener de los checkboxes
	 */
	public void onCheckedChanged(CompoundButton v, boolean isChecked)
	{
		if(v==(View)this.checkboxColorFondo)
		{
	        this.coloreandoAhora=1;
			this.actualizarVistaPrevia();
		}
		else if(v==(View)this.checkboxColorTexto)
		{
	        this.coloreandoAhora=2;
			this.actualizarVistaPrevia();
		}
		else if(v==(View)this.checkboxIcono)
		{
			this.cambiarIcono(this.icono);
		}
	}
}