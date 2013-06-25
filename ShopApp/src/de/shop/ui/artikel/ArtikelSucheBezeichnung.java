package de.shop.ui.artikel;

import static de.shop.util.Constants.ARTIKEL_KEY;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import de.shop.R;
import de.shop.data.Artikel;
import de.shop.ui.kunde.KundenListe;
import de.shop.ui.main.Main;
import de.shop.ui.main.Prefs;

public class ArtikelSucheBezeichnung extends Fragment implements OnClickListener {	
	private EditText bezeichnungTxt;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		// attachToRoot = false, weil die Verwaltung des Fragments durch die Activity erfolgt
		return inflater.inflate(R.layout.artikel_suche_bezeichnung, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		bezeichnungTxt = (EditText) view.findViewById(R.id.bezeichnung_edt);
    	
		// KundenSucheNachname (this) ist gleichzeitig der Listener, wenn der Suchen-Button angeklickt wird
		// und implementiert deshalb die Methode onClick() unten
		view.findViewById(R.id.btn_suchen).setOnClickListener(this);
		
	    // Evtl. vorhandene Tabs der ACTIVITY loeschen
    	final ActionBar actionBar = getActivity().getActionBar();
    	actionBar.setDisplayShowTitleEnabled(true);
    	actionBar.removeAllTabs();
    }
	
	@Override // OnClickListener
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_suchen:
				final String bezeichnung = bezeichnungTxt.getText().toString();
				final Main mainActivity = (Main) getActivity();
				final Artikel artikel = mainActivity.getArtikelServiceBinder().sucheArtikelByBezeichnung(bezeichnung);
		
				final Intent intent = new Intent(getActivity(), ArtikelListe.class);
				intent.putExtra(ARTIKEL_KEY, artikel);
				startActivity(intent);
				break;
		
			default:
				break;
		}
    }
    
	@Override
	// Nur aufgerufen, falls setHasOptionsMenu(true) in onCreateView() aufgerufen wird
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.main, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.einstellungen:
				getFragmentManager().beginTransaction()
                                    .replace(R.id.details, new Prefs())
                                    .addToBackStack(null)
                                    .commit();
				return true;
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
