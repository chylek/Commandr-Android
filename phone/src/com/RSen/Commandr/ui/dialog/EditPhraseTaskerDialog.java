package com.RSen.Commandr.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.RSen.Commandr.R;
import com.RSen.Commandr.core.TaskerCommand;
import com.RSen.Commandr.core.TaskerCommands;
import com.RSen.Commandr.ui.card.TaskerCard;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

/**
 * Created by Ryan on 6/11/2014.
 */
public class EditPhraseTaskerDialog {
    public EditPhraseTaskerDialog(final Context context, final TaskerCommand command, final TaskerCard card) {
        final EditText input = new EditText(context);
        input.setText(command.activationName);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        final CheckBox isRegex = new CheckBox(context);
        isRegex.setText(context.getString(R.string.regex));
        isRegex.setChecked(command.isRegex);
        LinearLayout ll = new LinearLayout(context);
        ll.addView(input);
        ll.addView(isRegex);
        ll.setLayoutParams(lp);
        ll.setOrientation(LinearLayout.VERTICAL);
        new MaterialDialog.Builder((Activity) context)
                .title(command.taskerCommandName)
                .theme(Theme.LIGHT)  // the default is light, so you don't need this line
                .customView(ll)
                .positiveText(R.string.set)  // the default is 'OK'
                .callback(new MaterialDialog.SimpleCallback() {
                    @Override
                    public void onPositive(MaterialDialog materialDialog) {

                        command.activationName = input.getText().toString();
                        command.isRegex = isRegex.isChecked();
                        TaskerCommands.save(context);
                        card.refreshCard(command);
                    }
                })
                .negativeText(R.string.cancel)  // leaving this line out will remove the negative button
                .build()
                .show();

    }

}
