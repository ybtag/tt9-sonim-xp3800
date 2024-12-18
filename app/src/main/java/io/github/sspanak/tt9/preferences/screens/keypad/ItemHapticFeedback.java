package io.github.sspanak.tt9.preferences.screens.keypad;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.SwitchPreferenceCompat;

import io.github.sspanak.tt9.preferences.items.ItemClickable;
import io.github.sspanak.tt9.preferences.settings.SettingsStore;

class ItemHapticFeedback extends ItemClickable {
	static final String NAME = "pref_haptic_feedback";
	private final SettingsStore settings;

	ItemHapticFeedback(Preference item, @NonNull SettingsStore settings) {
		super(item);
		this.settings = settings;
	}

	@Override
	protected boolean onClick(Preference p) {
		return true;
	}

	ItemHapticFeedback populate() {
		onLayoutChange(settings.getMainViewLayout());
		return this;
	}

	private void onLayoutChange(int mainViewLayout) {
		if (item != null) {
			item.setEnabled(mainViewLayout == SettingsStore.LAYOUT_NUMPAD || mainViewLayout == SettingsStore.LAYOUT_SMALL);
			((SwitchPreferenceCompat) item).setChecked(settings.getHapticFeedback());
		}
	}
}
