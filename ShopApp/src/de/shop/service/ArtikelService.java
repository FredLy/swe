package de.shop.service;

import static de.shop.ui.main.Prefs.mock;

import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import de.shop.data.Artikel;

public class ArtikelService extends Service {
	private static final String LOG_TAG = ArtikelService.class.getSimpleName();
	
	private final ArtikelServiceBinder binder = new ArtikelServiceBinder();

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	public class ArtikelServiceBinder extends Binder {
		public Artikel sucheArtikelById(Long id) {
			
			// (evtl. mehrere) Parameter vom Typ "Long", Resultat vom Typ "Artikel"
			final AsyncTask<Long, Void, Artikel> sucheArtikelByIdTask = new AsyncTask<Long, Void, Artikel>() {
				@Override
	    		protected void onPreExecute() {
					Log.d(LOG_TAG, "... ProgressDialog im laufenden Thread starten ...");
				}
				
				@Override
				// Neuer Thread (hier: Emulation des REST-Aufrufs), damit der UI-Thread nicht blockiert wird
				protected Artikel doInBackground(Long... ids) {
					final Long artikelId = ids[0];
					Artikel artikel;
			    	if (mock) {
			    		artikel = Mock.sucheArtikelById(artikelId);
			    	}
			    	else {
			    		Log.e(LOG_TAG, "Suche nach Artikelnummer ist nicht implementiert");
			    		return null;
			    	}
					Log.d(LOG_TAG + ".AsyncTask", "doInBackground: " + artikel);
					return artikel;
				}
				
				@Override
	    		protected void onPostExecute(Artikel artikel) {
					Log.d(LOG_TAG, "... ProgressDialog im laufenden Thread beenden ...");
	    		}
			};

			sucheArtikelByIdTask.execute(id);
			Artikel artikel = null;
	    	try {
	    		artikel = sucheArtikelByIdTask.get(3L, TimeUnit.SECONDS);
			}
	    	catch (Exception e) {
	    		Log.e(LOG_TAG, e.getMessage(), e);
			}
			return artikel;
		}
		
		public Artikel sucheArtikelByBezeichnung(String bezeichnung) {
			// (evtl. mehrere) Parameter vom Typ "String", Resultat vom Typ "ArrayList<Kunde>"
			final AsyncTask<String, Void, Artikel> sucheArtikelByBezeichnungTask = new AsyncTask<String, Void, Artikel>() {
				@Override
	    		protected void onPreExecute() {
					Log.d(LOG_TAG, "... ProgressDialog im laufenden Thread starten ...");
				}
				
				@Override
				// Neuer Thread (hier: Emulation des REST-Aufrufs), damit der UI-Thread nicht blockiert wird
				protected Artikel doInBackground(String... bezeichnungen) {
					final String bezeichnung = bezeichnungen[0];
					Artikel artikel;
			    	if (mock) {
			    		artikel = Mock.sucheArtikelByBezeichnung(bezeichnung);
			    	}
			    	else {
			    		Log.e(LOG_TAG, "Suche nach ArtikelBezeichnung ist nicht implementiert");
			    		return null;
			    	}
					Log.d(LOG_TAG + ".AsyncTask", "doInBackground: " + artikel);
					return artikel;
				}
				
				@Override
	    		protected void onPostExecute(Artikel artikel) {
					Log.d(LOG_TAG, "... ProgressDialog im laufenden Thread beenden ...");
	    		}
			};
			
			sucheArtikelByBezeichnungTask.execute(bezeichnung);
			Artikel artikel = null;
			try {
				artikel = sucheArtikelByBezeichnungTask.get(3L, TimeUnit.SECONDS);
			}
	    	catch (Exception e) {
	    		Log.e(LOG_TAG, e.getMessage(), e);
			}

			return artikel;
	    }	
	}
}
