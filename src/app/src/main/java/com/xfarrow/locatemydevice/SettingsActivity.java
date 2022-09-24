package com.xfarrow.locatemydevice;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Button buttonEnterPin;
    private EditText editTextLmdCommand;
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        settings = new Settings(this);
        setViews();
        setValues();
        setListeners();
    }

    private void setViews(){
        buttonEnterPin = findViewById(R.id.buttonEnterPassword);
        editTextLmdCommand = findViewById(R.id.editTextLmdCommand);
    }

    private void setValues(){
        editTextLmdCommand.setText(settings.get(Settings.SMS_COMMAND));
    }

    private void setListeners(){
        buttonEnterPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(SettingsActivity.this);
                alert.setTitle("Password");
                alert.setMessage("Enter Password");
                EditText input = new EditText(SettingsActivity.this);
                input.setTransformationMethod(new PasswordTransformationMethod());
                alert.setView(input);

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String text = input.getText().toString();
                        if (!text.isEmpty()) {
                            settings.set(Settings.PASSWORD, CipherUtils.get256Sha(text));
                        }
                        else{
                            Toast.makeText(SettingsActivity.this, "Cannot use a blank password. Aborted!", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                alert.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                final AlertDialog dialog = alert.create();
                dialog.show();

                // Disable button "OK" if the PIN contains a space or it's empty.
                input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(
                                !charSequence.toString().equals("") && !charSequence.toString().contains(" ")
                        );
                    }

                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                    @Override
                    public void afterTextChanged(Editable editable) {}
                });
            }
        });

        editTextLmdCommand.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    Toast.makeText(SettingsActivity.this, "Empty SMS command not allowed, reverted to default (LMD)", Toast.LENGTH_LONG).show();
                    settings.set(Settings.SMS_COMMAND, settings.defaultValues(Settings.SMS_COMMAND));
                } else {
                    settings.set(Settings.SMS_COMMAND, s.toString());
                }
            }
        });
    }
}