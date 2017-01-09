package de.mpii.clausie;

import de.mpii.clausie.Constituent.Flag;
import edu.stanford.nlp.ling.IndexedWord;

import java.util.*;
import java.util.Map.Entry;

/**
 * Currently the default proposition generator generates 3-ary propositions out of a clause.
 * <p>
 * Date:  $LastChangedDate: 2013-04-23 12:50:00 +0200 (Tue, 23 Apr 2013) $
 *
 * @version $LastChangedRevision: 736 $
 */
public class DefaultPropositionGenerator extends PropositionGenerator {

    public DefaultPropositionGenerator(ClausIE clausIE) {
        super(clausIE);
    }

    @Override
    public void generate(List<Proposition> result,
                         Clause clause,
                         List<Boolean> include) {
        Proposition proposition = new Proposition();
        List<Proposition> propositions = new ArrayList<>();

        // process subject
        if (clause.subject > -1 && include.get(clause.subject)) { // subject is -1 when there is an xcomp
            proposition.constituents.add(generate(clause, clause.subject));
            proposition.setType(clause.type.name());
            Set<IndexedWord> subjWords = super.getWords();
            proposition.addSubject(subjWords);
            proposition.addItem("subject", subjWords);
        } else {
            //throw new IllegalArgumentException();
        }

        // process verb
        if (include.get(clause.verb)) {
            proposition.constituents.add(generate(clause, clause.verb));
            Set<IndexedWord> verbWords = super.getWords();
            proposition.addVerb(verbWords);
            proposition.addItem("verb", verbWords);
        } else {
            throw new IllegalArgumentException();
        }

        propositions.add(proposition);

        // process arguments
        SortedMap<String, SortedSet<Integer>> sortedIndexes = new TreeMap<>();
        SortedSet<Integer> iobjects = new TreeSet<>(clause.iobjects);
        sortedIndexes.put("iobjects", iobjects);

        SortedSet<Integer> dobjects = new TreeSet<>(clause.dobjects);
        sortedIndexes.put("dobjects", dobjects);

        SortedSet<Integer> xcomps = new TreeSet<>(clause.xcomps);
        sortedIndexes.put("xcomps", xcomps);

        SortedSet<Integer> ccomps = new TreeSet<>(clause.ccomps);
        sortedIndexes.put("ccomps", ccomps);

        SortedSet<Integer> acomps = new TreeSet<>(clause.acomps);
        sortedIndexes.put("acomps", acomps);

        SortedSet<Integer> adverbials = new TreeSet<>(clause.adverbials);
        sortedIndexes.put("adverbials", adverbials);

        if (clause.complement >= 0) {
            SortedSet<Integer> complement = new TreeSet<>();
            complement.add(clause.complement);
            sortedIndexes.put("complement", complement);
        }

        for (Entry<String, SortedSet<Integer>> entry : sortedIndexes.entrySet()) {
            for (Integer index : entry.getValue()) {
                if (clause.constituents.get(clause.verb) instanceof IndexedConstituent && clause.adverbials.contains(index) && ((IndexedConstituent) clause.constituents.get(index)).getRoot().index() < ((IndexedConstituent) clause.constituents.get(clause.verb)).getRoot().index())
                    continue;
                for (Proposition p : propositions) {
                    if (include.get(index)) {
                        p.constituents.add(generate(clause, index));
                        p.addItem(entry.getKey(), super.getWords());
                    }
                }
            }
        }

        // process adverbials  before verb
        SortedSet<Integer> sortedInd = new TreeSet<>(clause.adverbials);
        for (Integer index : sortedInd) {
            if (clause.constituents.get(clause.verb) instanceof TextConstituent || ((IndexedConstituent) clause.constituents.get(index)).getRoot().index() > ((IndexedConstituent) clause.constituents.get(clause.verb)).getRoot().index())
                break;
            if (include.get(index)) {
                for (Proposition p : propositions) {
                    p.constituents.add(generate(clause, index));
                    if (clause.getFlag(index, clausIE.options).equals(Flag.OPTIONAL)) {
                        p.optional.add(p.constituents.size());
                        p.addItem("adverbials", super.getWords());
                    }
                }
            }
        }

        // make 3-ary if needed
        if (!clausIE.options.nary) {
            for (Proposition p : propositions) {
                p.optional.clear();
                if (p.constituents.size() > 3) {
                    StringBuilder arg = new StringBuilder();
                    for (int i = 2; i < p.constituents.size(); i++) {
                        if (i > 2) arg.append(' ');
                        arg.append(p.constituents.get(i));
                    }
                    p.constituents.set(2, arg.toString());
                    for (int i = p.constituents.size() - 1; i > 2; i--) {
                        p.constituents.remove(i);
                    }
                }
            }
        }
        // we are done
        result.addAll(propositions);
    }
}