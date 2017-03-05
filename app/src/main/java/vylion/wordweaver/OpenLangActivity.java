package vylion.wordweaver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import vylion.wordweaver.arachne.Weaver;

public class OpenLangActivity extends AppCompatActivity implements View.OnClickListener {

    private Weaver weaver;
    private Button button;
    private TextView generator;

    private EditText categories;
    private EditText rules;
    private EditText syllables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_lang);

        generator = (TextView) findViewById(R.id.generated_text);
        button = (Button) findViewById(R.id.open_lang_button);
        categories = (EditText) findViewById(R.id.open_lang_edit_categories);
        rules = (EditText) findViewById(R.id.open_lang_edit_rules);
        syllables = (EditText) findViewById(R.id.open_lang_edit_syllables);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(categories.getText() != null && rules.getText() != null && syllables.getText() != null) {
            String c = categories.getText().toString();
            String r = rules.getText().toString();
            String s = syllables.getText().toString();

            weaver = new Weaver(c, r, s);
        }
        else weaver = null;

        if(weaver != null) {
            String gen = weaver.newWordsAsString(20);

            generator.setText(gen);
        }
    }
}
