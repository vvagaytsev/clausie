package de.mpii.clausie;

import edu.stanford.nlp.ling.IndexedWord;

import javax.annotation.Nonnull;
import javax.annotation.WillNotClose;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * A dictionary stores a set of strings.
 * <p>
 * Date: $LastChangedDate: 2013-04-23 12:03:16 +0200 (Tue, 23 Apr 2013) $
 *
 * @version $LastChangedRevision: 735 $
 */
public class Dictionary {

    /**
     * Stores the strings
     */
    public final Set<String> words = new HashSet<>();

    public Dictionary() {
    }

    public int size() {
        return words.size();
    }

    public boolean contains(String word) {
        return words.contains(word);
    }

    public boolean contains(IndexedWord word) {
        return words.contains(word.lemma());
    }

    /**
     * Loads the dictionary out of an {@link InputStream}. Each line
     * of the original file should contain an entry to the dictionary
     */
    public void load(@Nonnull @WillNotClose InputStream in) throws IOException {
        DataInput data = new DataInputStream(in);
        String line = data.readLine();
        while (line != null) {
            line = line.trim();
            if (!line.isEmpty()) { // treat everything else as comments
                if (Character.isLetter(line.charAt(0))) {
                    words.add(line);
                }
            }
            line = data.readLine();
        }
    }

    public Set<String> words() {
        return words;
    }
}