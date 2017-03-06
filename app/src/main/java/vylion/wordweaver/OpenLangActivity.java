package vylion.wordweaver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import vylion.wordweaver.arachne.Language;
import vylion.wordweaver.arachne.Weaver;

public class OpenLangActivity extends AppCompatActivity implements View.OnClickListener {

    private Language lang;
    private Weaver weaver;
    private Button button;
    private String title;

    private TextView generator;
    private EditText categories;
    private EditText rules;
    private EditText syllables;

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
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        lang = i.getParcelableExtra(MainActivity.OPEN_LANG_TAG);

        if(lang != null) {
            title = lang.getName();
            weaver = lang.getWeaver();

            categories.setText(weaver.categoriesToString());
            rules.setText(weaver.rulesToString());
            syllables.setText(weaver.syllablesToString());
        }

        toolbar.setTitle(title);

        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(categories.getText() != null && rules.getText() != null && syllables.getText() != null) {
            String c = categories.getText().toString();
            String r = rules.getText().toString();
            String s = syllables.getText().toString();

            if(c.length() > 0 && s.length() > 0)
                weaver = new Weaver(c, r, s);
            else weaver = null;
        }
        else weaver = null;

        if(weaver != null) {
            String gen = weaver.newWordsAsString(20);

            generator.setText(gen);
        }
    }
}
