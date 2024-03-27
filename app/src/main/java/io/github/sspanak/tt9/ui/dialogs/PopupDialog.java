package io.github.sspanak.tt9.ui.dialogs;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import io.github.sspanak.tt9.ui.UI;
import io.github.sspanak.tt9.util.ConsumerCompat;

abstract public class PopupDialog {
	public static final String INTENT_CLOSE = "tt9.popup_dialog.close";
	public static final String PARAMETER_DIALOG_TYPE = "popup_type";


	protected final Context context;
	protected final ConsumerCompat<String> activityFinisher;
	protected String title;
	protected String message;
	protected String OKLabel;

	public PopupDialog(@NonNull Context context, @NonNull Intent intent, ConsumerCompat<String> activityFinisher) {
		this.activityFinisher = activityFinisher;
		this.context = context;
	}

	protected void close() {
		if (activityFinisher != null) {
			activityFinisher.accept("");
		}
	}

	protected void render(Runnable OKAction) {
		UI.confirm(context, title, message, OKLabel, OKAction, () -> activityFinisher.accept(""));
	}

	abstract public void render();
}
