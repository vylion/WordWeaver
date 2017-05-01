package vylion.wordweaver.arachne;

import android.text.TextUtils;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by vylion on 3/4/17.
 */
public class Weaver {
    public enum Dropoff {
        EQUAL, FAST, MEDIUM, SLOW, MOLASSES, CUSTOM
    }

    public enum SylProb {
        EQUAL, HEAVY, MEDIUM, LIGHT, CUSTOM
    }

    public enum MonosylProb {
        ALWAYS, MOSTLY, FREQUENT, LESS, RARE, CUSTOM
    }

    private Map<String, String[]> categories;
    private List<RewriteRule> rules;
    private List<String[]> syllables;
    private Dropoff dropoff;
    private double dropoffCustom;
    private SylProb sylProb;
    private double sylProbCustom;
    private MonosylProb monosylProb;
    private double monosylProbCustom;
    private Random rand;

    private Weaver() {
        categories = new HashMap<>();
        rules = new ArrayList<>();
        syllables = new ArrayList<>();

        dropoff = Dropoff.MEDIUM;
        sylProb = SylProb.MEDIUM;
        monosylProb = MonosylProb.FREQUENT;

        rand = new Random();

        dropoffCustom = 0.5;
        sylProbCustom= 0.5;
        monosylProbCustom = 0.5;
    }

    public Weaver(String c, String r, String s) {
        this();

        setParameters(c, r, s);
    }

    public void setParameters(String c, String r, String s) {
        c = c.trim();
        r = r.trim();
        s = s.trim();

        categories = new HashMap<>();
        rules = new ArrayList<>();
        syllables = new ArrayList<>();

        String[] categs = c.split("\n");
        for(int i = 0; i < categs.length; i++) {
            categs[i] = categs[i].trim();

            if(categs[i].length() > 0) {
                String[] categ = categs[i].split("=");
                if (categ[0].length() > 0 && categ[1].length() > 0) {
                    String[] letters = categ[1].split(" ");
                    if (letters.length > 1)
                        categories.put(categ[0], letters);
                    else
                        categories.put(categ[0], categ[1].split("(?!^)"));
                }
            }
        }

        String[] rs = r.split("\n");
        for(int i = 0; i < rs.length; i++) {
            if(rs[i].length() > 1 && rs[i].contains(">"))
                rules.add(new RewriteRule(rs[i]));
        }
        String[] syllables = s.split("\n");
        for(int i = 0; i < syllables.length; i++) {
            syllables[i] = syllables[i].trim();

            if(syllables[i].length() > 0) {
                String[] syllable = syllables[i].split(" ");
                if (syllable.length > 1)
                    this.syllables.add(syllable);
                else
                    this.syllables.add(syllable[0].split("(?!^)"));
            }
        }
    }

    public Weaver(String c, String r, String s, String d, double dCustom, String sylP, double sylPCustom, String monoP, double monoPCustom) {
        this(c, r, s);

        dropoff = Dropoff.valueOf(d);
        dropoffCustom = dCustom;

        sylProb = SylProb.valueOf(sylP);
        sylProbCustom = sylPCustom;

        monosylProb = MonosylProb.valueOf(monoP);
        monosylProbCustom = monoPCustom;
    }

    public void setDropoff(Dropoff d, Double custom) {
        dropoff = d;

        if(d == Dropoff.CUSTOM) {
            if(custom == null)
                dropoffCustom = 1;
            else
                dropoffCustom = custom;
        }
    }

    public void setDropoff(int i, Double custom) {
        switch (i) {
            default:
            case 0:
                setDropoff(Dropoff.EQUAL, null);
                break;
            case 1:
                setDropoff(Dropoff.FAST, null);
                break;
            case 2:
                setDropoff(Dropoff.MEDIUM, null);
                break;
            case 3:
                setDropoff(Dropoff.SLOW, null);
                break;
            case 4:
                setDropoff(Dropoff.MOLASSES, null);
                break;
            case 5:
                setDropoff(Dropoff.CUSTOM, custom);
                break;
        }
    }

    public void setSylProb(SylProb p, Double custom) {
        sylProb = p;

        if(p == SylProb.CUSTOM) {
            if(custom == null)
                sylProbCustom = 1;
            else
                sylProbCustom = custom;
        }
    }

    public void setSylProb(int i, Double custom) {
        switch (i) {
            default:
            case 0:
                setSylProb(SylProb.EQUAL, null);
                break;
            case 1:
                setSylProb(SylProb.HEAVY, null);
                break;
            case 2:
                setSylProb(SylProb.MEDIUM, null);
                break;
            case 3:
                setSylProb(SylProb.LIGHT, null);
                break;
            case 4:
                setSylProb(SylProb.CUSTOM, custom);
                break;
        }
    }

    public void setMonosylProb(MonosylProb p, Double custom) {
        monosylProb = p;

        if(p == MonosylProb.CUSTOM) {
            if(custom == null)
                monosylProbCustom = 1;
            else
                monosylProbCustom = custom;
        }
    }

    public void setMonosylProb(int i, Double custom) {
        switch (i) {
            default:
            case 0:
                setMonosylProb(MonosylProb.ALWAYS, null);
                break;
            case 1:
                setMonosylProb(MonosylProb.MOSTLY, null);
                break;
            case 2:
                setMonosylProb(MonosylProb.FREQUENT, null);
                break;
            case 3:
                setMonosylProb(MonosylProb.LESS, null);
                break;
            case 4:
                setMonosylProb(MonosylProb.RARE, null);
                break;
            case 5:
                setMonosylProb(MonosylProb.CUSTOM, custom);
                break;
        }
    }

    private String[] getRandSyllable() {
        double cutoff;

        switch (sylProb) {
            default:
            case EQUAL:
                return syllables.get(rand.nextInt(syllables.size()));
            case HEAVY:
                cutoff = 0.45;
                break;
            case MEDIUM:
                cutoff = 0.3;
                break;
            case LIGHT:
                cutoff = 0.15;
                break;
            case CUSTOM:
                cutoff = sylProbCustom;
        }
        int current = 0;
        double jump = rand.nextDouble();

        while(jump > cutoff) {
            current++;
            if(current == syllables.size()) current= 0;
            jump = rand.nextDouble();
        }

        return syllables.get(current);
    }

    private String getRandLetter(String c) {
        String[] category = categories.get(c);
        if(category == null) return c;

        double cutoff;

        switch (dropoff) {
            default:
            case EQUAL:
                return category[rand.nextInt(category.length)];
            case FAST:
                cutoff = 0.45;
                break;
            case MEDIUM:
                cutoff = 0.3;
                break;
            case SLOW:
                cutoff = 0.15;
                break;
            case CUSTOM:
                cutoff = dropoffCustom;
        }
        int current = 0;
        double jump = rand.nextDouble();

        while(jump > cutoff) {
            current++;
            if(current == category.length) current= 0;
            jump = rand.nextDouble();
        }

        return category[current];
    }

    private String makeSyllable() {
        String res = "";
        String[] syllable = getRandSyllable();

        for(int i = 0; i < syllable.length; i++) {
            String category = syllable[i];
            res += getRandLetter(category);
        }

        return res;
    }

    public String rewrite(String word) {
        for(int i = 0; i < rules.size(); i++) {
            RewriteRule rule = rules.get(i);

            if(word.contains(rule.getBefore())) {
                String[] chunks = word.split(rule.getBefore());
                if(chunks.length == 1) word = chunks[0] + rule.getAfter();
                else if(chunks.length > 1) word = TextUtils.join(rule.getAfter(), chunks);
                else word = rule.getAfter();
            }
        }
        return word;
    }

    public String newWord() {
        return rewrite(makeWord());
    }

    private String makeWord() {
        String word = makeSyllable();

        double cutoff;

        switch (monosylProb) {
            default:
            case ALWAYS:
                return word;
            case MOSTLY:
                cutoff = 0.6;
                break;
            case FREQUENT:
                cutoff = 0.45;
                break;
            case LESS:
                cutoff = 0.3;
                break;
            case RARE:
                cutoff = 0.15;
                break;
            case CUSTOM:
                cutoff = monosylProbCustom;
        }

        double jump = rand.nextDouble();
        if(jump > cutoff) {
            word += makeWord();
        }

        return word;
    }

    public List<String> newWords(int i) {
        ArrayList<String> words = new ArrayList<>();

        for(int j = 0; j < i; j++) {
            words.add(newWord());
        }

        return words;
    }

    public String newWordsAsString(int i) {
        ArrayList<String> words = (ArrayList<String>) newWords(i);
        return TextUtils.join(" ", words);
    }

    public String categoriesToString() {
        List<String> catName = new ArrayList<>(categories.keySet());
        List<String[]> catContent = new ArrayList<>(categories.values());
        String s = "";

        for(int i = 0; i < catContent.size(); i++) s += catName.get(i) + "=" + TextUtils.join(" ", catContent.get(i)) + "\n";

        return s;
    }

    public String rulesToString() {
        String s = "";

        for(int i = 0; i < rules.size(); i++) s+= rules.get(i) + "\n";

        return s;
    }

    public String syllablesToString() {
        String s = "";

        for(int i = 0; i < syllables.size(); i++) s+= TextUtils.join(" ", syllables.get(i)) + "\n";

        return s;
    }

    public String dropoffToString() {
        return dropoff.toString();
    }

    public double getDropoffCustom() {
        return dropoffCustom;
    }

    public String sylProbToString() {
        return sylProb.toString();
    }

    public double getSylProbCustom() {
        return sylProbCustom;
    }

    public String monosylProbToString() {
        return monosylProb.toString();
    }

    public double getMonosylProbCustom() {
        return monosylProbCustom;
    }

    public List<String> toParcelStrings() {
        ArrayList<String> strings = new ArrayList<>();

        strings.add(categoriesToString());
        strings.add(rulesToString());
        strings.add(syllablesToString());
        strings.add(dropoffToString());
        strings.add(sylProbToString());
        strings.add(monosylProbToString());

        return strings;
    }

    public static Weaver fromParcel(List<String> s, double dropoffCustom, double sylProbCustom, double monosylProbCustom) {
        return new Weaver(s.get(0), s.get(1), s.get(2), s.get(3), dropoffCustom, s.get(4), sylProbCustom, s.get(5), monosylProbCustom);
    }

    public String toString() {
        String s = "*WORD WEAVER*\nCategories:\n";
        s += categoriesToString();

        s += "\nRewrite rules:\n";
        for(int i = 0; i < rules.size(); i++) s+= rules.get(i).print() + "\n";

        s += "\nSyllable types:\n";

        return s;
    }
}
