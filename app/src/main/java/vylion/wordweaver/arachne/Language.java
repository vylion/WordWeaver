package vylion.wordweaver.arachne;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vylion on 3/4/17.
 */

public class Language implements Parcelable {

    private long id;
    private String name;
    private String author;
    private Weaver weaver;

    public Language(String name, Weaver weaver) {
        this.id = -1;
        this.name = name;
        this.author = null;
        this.weaver = weaver;
    }

    public Language(long id, String name, Weaver weaver) {
        this.id = id;
        this.name = name;
        this.author = null;
        this.weaver = weaver;
    }

    public Language(long id, String name, String author, Weaver weaver) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.weaver = weaver;
    }

    protected Language(Parcel in) {
        id = in.readLong();
        name = in.readString();
        author = in.readString();

        List<String> strings = new ArrayList<>();
        in.readStringList(strings);
        double dropoff = in.readDouble();
        double sylprob = in.readDouble();
        double monosylprob = in.readDouble();
        weaver = Weaver.fromParcel(strings, dropoff, sylprob, monosylprob);
    }

    public static final Creator<Language> CREATOR = new Creator<Language>() {
        @Override
        public Language createFromParcel(Parcel in) {
            return new Language(in);
        }

        @Override
        public Language[] newArray(int size) {
            return new Language[size];
        }
    };

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public Weaver getWeaver() {
        return weaver;
    }

    public void setWeaver(Weaver w) {
        weaver = w;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(author);

        dest.writeStringList(weaver.toParcelStrings());
        dest.writeDouble(weaver.getDropoffCustom());
        dest.writeDouble(weaver.getSylProbCustom());
        dest.writeDouble(weaver.getMonosylProbCustom());
    }
}
