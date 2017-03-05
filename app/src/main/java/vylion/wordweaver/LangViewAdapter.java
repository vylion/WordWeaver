package vylion.wordweaver;

import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import vylion.wordweaver.alexandria.Scribe;
import vylion.wordweaver.arachne.Language;


/**
 * Created by vylion on 1/3/17.
 */

public class LangViewAdapter extends RecyclerView.Adapter<LangViewAdapter.BookViewHolder> {

    public static final int COMPARE_BY_TITLE = 0;
    public static final int COMPARE_BY_CATEGORY = 1;

    private final LayoutInflater inflater;
    private List<Language> languages;
    private Context context;
    private Scribe scribe;
    private CoordinatorLayout coordinatorLayout;

    public LangViewAdapter(Context c, Scribe s, CoordinatorLayout layout) {
        scribe = s;

        inflater = LayoutInflater.from(c);
        scribe.read();
        languages = scribe.getAllLangs();
        scribe.close();
        //order();
        context = c;
        coordinatorLayout = layout;
    }

    //*****************************************
    //RecyclerViewAdapter implemented functions
    //*****************************************

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.lang_view_row, parent, false);
        BookViewHolder holder = new BookViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final BookViewHolder holder, int position) {
        Language l = languages.get(position);
        holder.setName(l.getName());
        holder.setAuthor(l.getAuthor());
    }

    @Override
    public int getItemCount() {
        return languages.size();
    }

    public void add(Language l) {
        int pos;
        LangComparator comparator = new LangComparator();

        for(pos = 0; pos < languages.size(); pos++) {
            if(comparator.compare(l, languages.get(pos)) < 1) break;
        }
        scribe.open();
        l = scribe.createLang(l);
        scribe.close();
        languages.add(pos, l);
        notifyItemInserted(pos);
    }

    public Language getItem(int pos) {
        return languages.get(pos);
    }

    public void remove(Language l) {
        int pos;
        for(pos = 0; pos < languages.size(); pos++) {
            if(l == languages.get(pos))
                break;
        }
        if(pos < languages.size()) {
            scribe.open();
            scribe.deleteBook(languages.get(pos));
            scribe.close();
            languages.remove(pos);
            notifyItemRemoved(pos);
        }
    }

    public void remove(int p) {
        scribe.open();
        scribe.deleteBook(languages.get(p));
        scribe.close();
        languages.remove(p);
        notifyItemRemoved(p);
    }

    //***********************
    //Auxiliary sorting tools
    //***********************

    public void order() {
        LangComparator comparator = new LangComparator();
        Collections.sort(languages, comparator);
        notifyDataSetChanged();
    }

    private class LangComparator implements Comparator<Language> {

        @Override
        public int compare(Language lhs, Language rhs) {
            return lhs.getName().toLowerCase().compareTo(rhs.getName().toLowerCase());
        }
    }

    //************************
    //Other auxiliary funcions
    //************************

    private Snackbar makeSnackbar(String warning) {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, warning, Snackbar.LENGTH_SHORT);

        snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        snackbar.show();

        return snackbar;
    }

    private Snackbar makeActionSnackbar(String warning, String action, View.OnClickListener l) {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, warning, Snackbar.LENGTH_LONG)
                .setAction(action, l);

        snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        snackbar.show();

        return snackbar;
    }

    private void makeDeleteSnackbar(final Language l) {
        Resources res = context.getResources();

        String warning = "\"" + l.getName() + "\" " + res.getString(R.string.snackbar_delete_warning);
        String action = res.getString(R.string.snackbar_delete_action);
        final String reverted = "\"" + l.getName() + "\" " + res.getString(R.string.snackbar_delete_reverted);

        makeActionSnackbar(warning, action, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add(l);
                makeSnackbar(reverted);
            }
        });
    }

    private void makeToast(String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    //******************************************************
    //RecyclerViewAdapter's ViewHolder custom implementation
    //******************************************************

    class BookViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener, View.OnClickListener {
        private ImageView menuButton;
        private TextView name;
        private TextView author;

        public BookViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.lang_view_row_name);
            author = (TextView) itemView.findViewById(R.id.lang_view_row_author);

            menuButton = (ImageView) itemView.findViewById(R.id.lang_view_row_menu);

            menuButton.setOnClickListener(this);
        }

        //*******************
        //Setters and getters
        //*******************

        public void setName(String t) {
            name.setText(t);
        }

        public void setAuthor(String t) {
            author.setText(t);
        }

        public View getMenuButton() {
            return menuButton;
        }

        //**********************
        //On click functionality
        //**********************

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.lang_view_row_menu:
                    showPopupMenu((ImageView) v);
            }
        }

        public void showPopupMenu(ImageView menu) {
            PopupMenu popup = new PopupMenu(context, menu, Gravity.NO_GRAVITY, R.attr.actionOverflowMenuStyle, 0);
            popup.getMenuInflater().inflate(R.menu.lang_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(this);
            popup.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.lang_menu_modify:
                    makeToast(context.getResources().getString(R.string.feature_not_implemented));
                    return true;
                case R.id.lang_menu_delete:
                    Language l = getItem(getAdapterPosition());
                    remove(getAdapterPosition());
                    makeDeleteSnackbar(l);
                    return true;
            }
            return false;
        }
    }
}