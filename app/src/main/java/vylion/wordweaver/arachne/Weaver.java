package vylion.wordweaver.arachne;

import android.text.TextUtils;

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
        rules = new ArrayList<RewriteRule>();
        syllables = new ArrayList<>();

        dropoff = Dropoff.EQUAL;
        sylProb = SylProb.EQUAL;
        monosylProb = MonosylProb.FREQUENT;

        rand = new Random();

        dropoffCustom = 0.5;
        sylProbCustom= 0.5;
        monosylProbCustom = 0.5;
    }

    public Weaver(String c, String r, String s) {
        this();

        c = c.trim();
        r = r.trim();
        s = s.trim();

        String[] categs = c.split("\n");
        for(int i = 0; i < categs.length; i++) {
            String[] categ = categs[i].split("=");
            if(categ[0].length() > 0 && categ[1].length() > 0) {
                String[] letters = categ[1].split(" ");
                if(letters.length > 1)
                    categories.put(categ[0], letters);
                else
                categories.put(categ[0], categ[1].split("(?!^)"));
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

    public void setSylProb(SylProb p, Double custom) {
        sylProb = p;

        if(p == SylProb.CUSTOM) {
            if(custom == null)
                sylProbCustom = 1;
            else
                sylProbCustom = custom;
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
            String[] chunks = word.split(rule.getBefore());
            if(chunks.length > 1) word = TextUtils.join(rule.getAfter(), chunks);
            else if (chunks.length == 0) word = rule.getAfter();
            else word = chunks[0];
        }

        return word;
    }

    public String newWord() {
        String word = makeSyllable();

        double cutoff;

        switch (monosylProb) {
            default:
            case ALWAYS:
                return rewrite(word);
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
            word += newWord();
        }

        return rewrite(word);
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

        for(int i = 0; i < rules.size(); i++) s+= rules.get(i).print() + "\n";

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

    public String toString() {
        String s = "*WORD WEAVER*\nCategories:\n";
        s += categoriesToString();

        s += "\nRewrite rules:\n";
        for(int i = 0; i < rules.size(); i++) s+= rules.get(i).print() + "\n";

        s += "\nSyllable types:\n";

        return s;
    }
}
