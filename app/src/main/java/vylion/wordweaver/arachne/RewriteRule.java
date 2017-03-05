package vylion.wordweaver.arachne;

/**
 * Created by vylion on 3/4/17.
 */
public class RewriteRule {

    private String before;
    private String after;

    public RewriteRule(String before, String after) {
        this.before = before;
        this.after = after;
    }

    public RewriteRule(String input) {
        String[] rule = input.split(">");

        before = rule[0];
        if(rule.length > 1) after = rule[1];
        else after = "";
    }

    public String getBefore() {
        return before;
    }

    public String getAfter() {
        return after;
    }

    @Override
    public String toString() {
        return before + ">" + after;
    }

    public String print() {
        return before + " -> " + after;
    }
}
