package org.osd.omot_app.utils;

import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import org.osd.omot_app.R;

/**
 * Utility class for providing consistent user feedback throughout the app.
 */
public class UIFeedback {

    public static void showSnackbar(View view, String message, boolean isError) {
        if (view == null || message == null) return;

        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);

        // Customize the snackbar appearance
        View snackbarView = snackbar.getView();
        TextView textView =
                snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setMaxLines(3);    // Allow multiple lines for longer messages

        if (isError) {
            snackbarView.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.md_theme_dark_errorContainer));
            textView.setTextColor(ContextCompat.getColor(view.getContext(), R.color.md_theme_dark_onErrorContainer));
        } else {
            snackbarView.setBackgroundColor(ContextCompat.getColor(view.getContext(),
                    R.color.md_theme_dark_primaryContainer));
            textView.setTextColor(ContextCompat.getColor(view.getContext(), R.color.md_theme_dark_onPrimaryContainer));
        }

        snackbar.show();
    }
}