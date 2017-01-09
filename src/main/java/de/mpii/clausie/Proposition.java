package de.mpii.clausie;

import edu.stanford.nlp.ling.IndexedWord;

import java.util.*;

/**
 * Stores a proposition.
 * <p>
 * Date: $LastChangedDate: 2013-04-24 11:54:36 +0200 (Wed, 24 Apr 2013) $
 *
 * @version $LastChangedRevision: 741 $
 */
public class Proposition {

    /**
     * Constituents of the proposition.
     */
    List<String> constituents = new ArrayList<>();
    /**
     * Position of optional constituents.
     */
    Set<Integer> optional = new HashSet<>();
    //IndexedWords
    private Set<IndexedWord> subjectWords;
    private Set<IndexedWord> verbWords;
    private Map<String, Set<IndexedWord>> items;
    private Object subject;
    private String type;

    // TODO: types of constituents (e.g., optionality)
    // sentence ID etc.

    public Proposition() {
    }

    /**
     * Returns the subject of the proposition.
     */
    public String subject() {
        return constituents.get(0);
    }

    /**
     * Returns the relation of the proposition.
     */
    public String relation() {
        return constituents.get(1);
    }

    /**
     * Returns a constituent in a given position.
     */
    public String argument(int i) {
        return constituents.get(i + 2);
    }

    /**
     * Returns the number of arguments.
     */
    public int noArguments() {
        return constituents.size() - 2;
    }

    /**
     * Checks if an argument is optional.
     */
    public boolean isOptionalArgument(int i) {
        return optional.contains(i + 2);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String sep = "(";
        for (int i = 0; i < constituents.size(); i++) {
            String constituent = constituents.get(i);
            sb.append(sep);
            sep = ", ";
            sb.append('"');
            sb.append(constituent);
            sb.append('"');
            if (optional.contains(i)) {
                sb.append('?');
            }
        }
        sb.append(')');
        return sb.toString();
    }

    @Override
    public Proposition clone() {
        Proposition clone = new Proposition();
        clone.constituents = new ArrayList<>(constituents);
        clone.optional = new HashSet<>(optional);
        return clone;
    }

    public void addSubject(Set<IndexedWord> subjWords) {
        this.subjectWords = subjWords;
    }

    public void addVerb(Set<IndexedWord> verbWords) {
        this.verbWords = verbWords;
    }

    public void addItem(String type, Set<IndexedWord> words) {
        if (items == null) {
            items = new HashMap<>();
        }
        items.put(type, words);
    }

    public Set<IndexedWord> getSubject() {
        return subjectWords;
    }

    public Set<IndexedWord> getVerb() {
        return verbWords;
    }

    public Map<String, Set<IndexedWord>> getItems() {
        if (items == null) return Collections.emptyMap();
        return items;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}