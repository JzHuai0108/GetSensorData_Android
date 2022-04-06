package es.csic.getsensordata;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import es.csic.getsensordata.R;

public class PantallaPreferencias extends PreferenceActivity {
	
	static int frecuencia=50;
	static float loggingdelay = 3;
	static float loggingduration = 30;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pantalla_preferencias);
		
		
		Preference.OnPreferenceChangeListener changeListener_OPT2 = new Preference.OnPreferenceChangeListener() {
		    public boolean onPreferenceChange(Preference preference, Object newValue) {
		        // Code goes here
		    	frecuencia=Integer.parseInt(newValue.toString());
		    	preference.setSummary("Select desired update rate (Now "+frecuencia+" Hz)");
				return true;
		    }
		};
		EditTextPreference pref_OPT2 = (EditTextPreference)findPreference("opcion2");
		pref_OPT2.setOnPreferenceChangeListener(changeListener_OPT2);
		pref_OPT2.setSummary("Select desired update rate (Now "+frecuencia+" Hz)");

		final String loggingdelaymsg = getString(R.string.texto_loggingdelay);
		Preference.OnPreferenceChangeListener changeListenerDelay = new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				loggingdelay=Float.parseFloat(newValue.toString());
				preference.setSummary(loggingdelaymsg + " (Now "
						+ String.format("%.1f", loggingdelay) +" s)");
				return true;
			}
		};
		EditTextPreference pref_loggingdelay = (EditTextPreference)findPreference("optiondelay");
		pref_loggingdelay.setOnPreferenceChangeListener(changeListenerDelay);
		pref_loggingdelay.setSummary(loggingdelaymsg + " (Now "
				+ String.format("%.1f", loggingdelay) +" s)");

		final String loggingdurationmsg = getString(R.string.texto_loggingduration);
		Preference.OnPreferenceChangeListener changeListenerDuration = new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				loggingduration=Float.parseFloat(newValue.toString());
				preference.setSummary(loggingdurationmsg + " (Now "
						+ String.format("%.1f", loggingduration) +" s)");
				return true;
			}
		};
		EditTextPreference pref_loggingduration = (EditTextPreference)findPreference("optionduration");
		pref_loggingduration.setOnPreferenceChangeListener(changeListenerDuration);
		pref_loggingduration.setSummary(loggingdurationmsg + " (Now "
				+ String.format("%.1f", loggingduration) +" s)");

	} // end-on create
	}