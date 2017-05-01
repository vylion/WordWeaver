package vylion.wordweaver;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import vylion.wordweaver.arachne.Language;
import vylion.wordweaver.arachne.Weaver;

public class OpenLangActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    public static final String SAVE_LANG_TAG = "save_language";

    private Language lang;
    private Weaver weaver;
    private Button button;
    private String title;

    private TextView generator;
    private EditText categories;
    private EditText rules;
    private EditText syllables;
    private EditText name;

    private int dropoff;
    private double dropoffCustom;
    private int sylprob;
    private double sylprobCustom;
    private int monosyl;
    private double monosylCustom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_lang);

        title = getString(R.string.default_lang_name);

        generator = (TextView) findViewById(R.id.generated_text);
        button = (Button) findViewById(R.id.open_lang_button);
        categories = (EditText) findViewById(R.id.open_lang_edit_categories);
        rules = (EditText) findViewById(R.id.open_lang_edit_rules);
        syllables = (EditText) findViewById(R.id.open_lang_edit_syllables);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);

        Intent i = getIntent();
        lang = i.getParcelableExtra(MainActivity.OPEN_LANG_TAG);

        if(lang != null) {
            title = lang.getName();
            weaver = lang.getWeaver();

            categories.setText(weaver.categoriesToString());
            rules.setText(weaver.rulesToString());
            syllables.setText(weaver.syllablesToString());

            dropoff = weaver.getDropoff().getValue();
            dropoffCustom = weaver.getDropoffCustom();
            sylprob = weaver.getSylProb().getValue();
            sylprobCustom = weaver.getSylProbCustom();
            monosyl = weaver.getMonosylProb().getValue();
            monosylCustom = weaver.getMonosylProbCustom();
        }
        else {
            name = new EditText(this);
            name.setInputType(InputType.TYPE_CLASS_TEXT);

            dropoff = 2;
            sylprob = 2;
            monosyl = 2;
            dropoffCustom = 0.5;
            sylprobCustom = 0.5;
            monosylCustom = 0.5;
        }

        Spinner dropoffSpinner = (Spinner) findViewById(R.id.dropoff_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.dropoff_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropoffSpinner.setAdapter(adapter);
        dropoffSpinner.setOnItemSelectedListener(this);
        dropoffSpinner.setSelection(dropoff);

        Spinner sylprobSpinner = (Spinner) findViewById(R.id.sylprob_spinner);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.sylprob_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sylprobSpinner.setAdapter(adapter);
        sylprobSpinner.setOnItemSelectedListener(this);
        sylprobSpinner.setSelection(sylprob);

        Spinner monosylSpinner = (Spinner) findViewById(R.id.monosyl_spinner);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.monosyl_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monosylSpinner.setAdapter(adapter);
        monosylSpinner.setOnItemSelectedListener(this);
        monosylSpinner.setSelection(monosyl);

        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        button.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.open_lang_menu, menu);
        getMenuInflater().inflate(R.menu.shared_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.open_lang_menu_save:
                saveLang();
                break;
            case R.id.open_lang_menu_clear:
                emptyFields();
                break;
            case R.id.shared_menu_help:
                makeToast(getString(R.string.feature_not_implemented));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(categories.getText() != null && rules.getText() != null && syllables.getText() != null) {
            String c = categories.getText().toString();
            String r = rules.getText().toString();
            String s = syllables.getText().toString();

            if(c.length() > 0 && s.length() > 0) {
                if (weaver != null) weaver.setParameters(c, r, s);
                else weaver = new Weaver(c, r, s);

                weaver.setDropoff(dropoff, dropoffCustom);
                weaver.setSylProb(sylprob, sylprobCustom);
                weaver.setMonosylProb(monosyl, monosylCustom);

                String gen = weaver.newWordsAsString(20);

                generator.setText(gen);
            }
        }
    }

    private void saveLang() {
        final Intent result = new Intent(this, MainActivity.class);


        if(categories.getText() != null && rules.getText() != null && syllables.getText() != null) {
            String c = categories.getText().toString();
            String r = rules.getText().toString();
            String s = syllables.getText().toString();

            if (c.length() > 0 && s.length() > 0){
                if (weaver != null) weaver.setParameters(c, r, s);
                else weaver = new Weaver(c, r, s);

                weaver.setDropoff(dropoff, dropoffCustom);
                weaver.setSylProb(sylprob, sylprobCustom);
                weaver.setMonosylProb(monosyl, monosylCustom);
            }
        }

        if(lang == null) {
            String question = getResources().getString(R.string.dialog_save_lang);
            String yes = getResources().getString(R.string.dialog_save);
            String no = getResources().getString(R.string.dialog_cancel);

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage(question)
                    .setView(name)
                    .setCancelable(false)
                    .setPositiveButton(yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String langName;

                            if(name.getText() != null && name.length() > 0) langName = name.getText().toString();
                            else langName = title;

                            lang = new Language(langName, weaver);

                            result.putExtra(SAVE_LANG_TAG, lang);
                            setResult(AppCompatActivity.RESULT_OK, result);
                            finish();
                        }
                    })
                    .setNegativeButton(no, null)
                    .create();

            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        }
        else {
            lang.setWeaver(weaver);
            result.putExtra(SAVE_LANG_TAG, lang);
            setResult(AppCompatActivity.RESULT_OK, result);
            finish();
        }
    }

    private void emptyFields() {
        String question = getResources().getString(R.string.dialog_empty_fields);
        String yes = getResources().getString(R.string.dialog_yes);
        String no = getResources().getString(R.string.dialog_no);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(question)
                .setCancelable(false)
                .setPositiveButton(yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        generator.setText("");
                        categories.setText("");
                        rules.setText("");
                        syllables.setText("");
                    }
                })
                .setNegativeButton(no, null)
                .create();

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void makeToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        switch (parent.getId()) {
            case R.id.dropoff_spinner:
                setDropoff(pos, 0.5);
                break;
            case R.id.sylprob_spinner:
                setSylProb(pos, 0.5);
                break;
            case R.id.monosyl_spinner:
                setMonosylProb(pos, 0.5);
                break;
        }
    }

    private void setDropoff(int i, double d) {
        dropoff = i;
        dropoffCustom = d;
    }

    private void setSylProb(int i, double d) {
        sylprob = i;
        sylprobCustom = d;
    }

    private void setMonosylProb(int i, double d) {
        monosyl = i;
        monosylCustom = d;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
