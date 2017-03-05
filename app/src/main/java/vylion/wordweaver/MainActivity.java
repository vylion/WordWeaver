package vylion.wordweaver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import vylion.wordweaver.alexandria.Scribe;
import vylion.wordweaver.arachne.Weaver;

public class MainActivity extends AppCompatActivity {

    private Scribe scribe;
    private Toolbar toolbar;
    private RecyclerView langView;

    public static final String FIRST_TIME_LAUNCH_TAG = "first_time";
    public static final String OPEN_LANG_TAG = "open_lang";

    //****************************
    //Activity lifecycle functions
    //****************************

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        langView = (RecyclerView) findViewById(R.id.main_recycler);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) toolbar.getLayoutParams();
            layoutParams.height = (int) getResources().getDimension(R.dimen.appbar_landscape_height);
            toolbar.setLayoutParams(layoutParams);
        }

        scribe = new Scribe(this);

        //First time app start code
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if(sp.getBoolean(FIRST_TIME_LAUNCH_TAG, true)) {
            scribe.open();

            Weaver weaver = new Weaver("C=ptkbdg\nR=rl\nV=ieaou", "ki>či", "CV\nV\nCRV");
            scribe.createLang("Sample: Example", "Zompist", weaver);

            weaver = new Weaver("C=tknsmrh\nV=aioeu\nU=auoāēū\nL=āīōēū",
                    "hu>fu\nhū>fū\nsi>shi\nsī>shī\nsy>sh\nti>chi\ntī>chī\nty>ch\ntu>tsu\n" +
                            "tū>tsū\nqk>kk\nqp>pp\nqt>tt\nq[^ptk]>",
                    "CV\nCVn\nCL\nCLn\nCyU\nCyUn\nVn\nLn\nCVq\nCLq\nyU\nyUn\nwa\nL\nV");
            scribe.createLang("Sample: Pseudo-japanese", "Zompist", weaver);

            scribe.close();

            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(FIRST_TIME_LAUNCH_TAG, false);
            editor.apply();
        }

        resetView();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    //*********************
    //Activity menu options
    //*********************

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add);

        switch (item.getItemId()) {
            case R.id.main_menu_about:
                String aboutText = getString(R.string.about_text);
                String ok = getString(R.string.about_button);
                showAbout(aboutText, ok, this);
                break;
            case R.id.main_menu_add_lang:
                Intent openLang = new Intent(this, OpenLangActivity.class);
                startActivityForResult(openLang, 0);
                break;
            case R.id.main_menu_help:
                makeToast(getString(R.string.feature_not_implemented));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //*********************************
    //Activity event reaction functions
    //*********************************

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == AppCompatActivity.RESULT_OK) {
            LangViewAdapter adapter = (LangViewAdapter) langView.getAdapter();

            //adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //*************************
    //Other auxiliary functions
    //*************************

    private void resetView() {
        CoordinatorLayout layout = (CoordinatorLayout) findViewById(R.id.main_coordinator);

        LangViewAdapter adapter = new LangViewAdapter(this, scribe, layout);
        langView.setAdapter(adapter);

        RecyclerView.LayoutManager lm;
        lm = new LinearLayoutManager(this);
        langView.setLayoutManager(lm);
    }

    private void makeToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public static void showAbout(String aboutText, String ok, Context context) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setMessage(aboutText)
                .setCancelable(false)
                .setPositiveButton(ok, null)
                .create();

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void makeSnackbar(String warning) {
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) getResources().getLayout(R.layout.activity_main);

        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, warning, Snackbar.LENGTH_SHORT);

        snackbar.show();
    }

    private void makeActionSnackbar(String warning, String action, View.OnClickListener l) {
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) getResources().getLayout(R.layout.activity_main);

        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, warning, Snackbar.LENGTH_LONG)
                .setAction(action, l);

        snackbar.show();
    }
}
