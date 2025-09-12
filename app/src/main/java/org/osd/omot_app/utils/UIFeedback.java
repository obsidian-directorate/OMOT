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

    /**
     * Shows a themed Snackbar with the specified message and style.
     * @param view The view to anchor the Snackbar to.
     * @param message The message to display.
     * @param isError Whether this is an error message (uses error colors) or info.
     */
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

    /**
     * Shows a success-themed Snackbar with green accents
     */
    public static void showSuccessSnackbar(View view, String message) {
        showThemedSnackbar(view, message, R.color.obsidian_accent_green, R.color.black);
    }

    /**
     * Shows a warning-themed Snackbar with yellow/orange accents.
     */
    public static void showWarningSnackbar(View view, String message) {
        showThemedSnackbar(view, message, R.color.md_theme_dark_tertiaryContainer, R.color.md_theme_dark_onTertiaryContainer);
    }

    /**
     * Shows an error-themed Snackbar.
     */
    public static void showErrorSnackbar(View view, String message) {
        showSnackbar(view, message, true);
    }

    /**
     * Generic method for showing themed snackbars.
     */
    private static void showThemedSnackbar(View view, String message, int backgroundColorRes,
                                           int textColorRes) {
        if (view == null || message == null) return;

        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);

        View snackbarView = snackbar.getView();
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setMaxLines(3);

        snackbarView.setBackgroundColor(ContextCompat.getColor(view.getContext(), backgroundColorRes));
        textView.setTextColor(ContextCompat.getColor(view.getContext(), textColorRes));

        snackbar.show();
    }
}