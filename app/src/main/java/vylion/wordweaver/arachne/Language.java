package vylion.wordweaver.arachne;

/**
 * Created by vylion on 3/4/17.
 */

public class Language {

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

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Weaver getWeaver() {
        return weaver;
    }

    public String getAuthor() {
        return author;
    }

    public void setWeaver(Weaver w) {
        weaver = w;
    }
}
