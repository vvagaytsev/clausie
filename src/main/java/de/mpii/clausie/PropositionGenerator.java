package de.mpii.clausie;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.EnglishGrammaticalRelations;
import edu.stanford.nlp.trees.GrammaticalRelation;

import java.util.*;

/**
 * Handles the generation of propositions out of a given clause.
 */
public abstract class PropositionGenerator {

    protected final ClausIE clausIE;
    private Set<IndexedWord> words;

    /**
     * Relations to be excluded in every constituent of a clause except the verb.
     */
    protected static final Set<GrammaticalRelation> EXCLUDE_RELATIONS;

    /**
     * Relations to be excluded in the verb.
     */
    protected static final Set<GrammaticalRelation> EXCLUDE_RELATIONS_VERB;

    static {
        EXCLUDE_RELATIONS = new HashSet<>();
        EXCLUDE_RELATIONS.add(EnglishGrammaticalRelations.RELATIVE_CLAUSE_MODIFIER);
        EXCLUDE_RELATIONS.add(EnglishGrammaticalRelations.APPOSITIONAL_MODIFIER);
        EXCLUDE_RELATIONS.add(EnglishGrammaticalRelations.PARATAXIS);

        EXCLUDE_RELATIONS_VERB = new HashSet<>();
        EXCLUDE_RELATIONS_VERB.addAll(EXCLUDE_RELATIONS);
        EXCLUDE_RELATIONS_VERB.add(EnglishGrammaticalRelations.valueOf("dep")); //without this asome adverbs or auxiliaries will end up in the relation
    }

    /**
     * Constructs a proposition generator.
     */
    protected PropositionGenerator(ClausIE clausIE) {
        this.clausIE = clausIE;
    }

    /**
     * Generates propositions for a given clause.
     */
    public abstract void generate(List<Proposition> result, Clause clause, List<Boolean> include);

    /**
     * Generates a textual representation of a given constituent plus a set of words.
     */
    private String generatePhrase(IndexedConstituent constituent,
                                  Collection<IndexedWord> words) {
        StringBuilder result = new StringBuilder();
        String separator = "";
        result.append(separator);
        if (constituent.isPrepositionalPhrase()) {
            if (clausIE.options.lemmatize) {
                result.append(constituent.getRoot().lemma());
            } else {
                result.append(constituent.getRoot().originalText());
            }
            separator = " ";
        }

        for (IndexedWord word : words) {
            result.append(separator);
            if (clausIE.options.lemmatize) {
                result.append(word.lemma());
            } else {
                result.append(word.originalText());
            }
            separator = " ";
        }
        return result.toString();
    }

    /**
     * Generates a textual representation of a given constituent in a given clause.
     */
    public String generate(Clause clause, int constituentIndex) {
        Set<GrammaticalRelation> excludeRelations = EXCLUDE_RELATIONS;
        if (clause.verb == constituentIndex) {
            excludeRelations = EXCLUDE_RELATIONS_VERB;
        }
        return generate(
                clause,
                constituentIndex,
                excludeRelations,
                Collections.<GrammaticalRelation>emptySet()
        );
    }

    /**
     * Generates a textual representation of a given constituent in a given clause.
     */
    public String generate(Clause clause,
                           int constituentIndex,
                           Collection<GrammaticalRelation> excludeRelations,
                           Collection<GrammaticalRelation> excludeRelationsTop) {
        Constituent constituent = clause.constituents.get(constituentIndex);
        if (constituent instanceof TextConstituent) {
            String s = ((TextConstituent) constituent).text();
            words = new TreeSet<>();
            String[] keys = "value,word,lemma,tag".split(",");
            Label label = new CoreLabel(keys, createValues(s));
            IndexedWord word = new IndexedWord(label);
            words.add(word);
            return s;
        } else if (constituent instanceof IndexedConstituent) {
            IndexedConstituent iconstituent = (IndexedConstituent) constituent;
            SemanticGraph subgraph = iconstituent.createReducedSemanticGraph();
            DpUtils.removeEdges(
                    subgraph,
                    iconstituent.getRoot(),
                    excludeRelations,
                    excludeRelationsTop
            );
            words = new TreeSet<>(subgraph.descendants(iconstituent.getRoot()));
            for (IndexedWord v : iconstituent.getAdditionalVertexes()) {
                words.addAll(subgraph.descendants(v));
            }
            if (iconstituent.isPrepositionalPhrase())
                words.remove(iconstituent.getRoot());
            return generatePhrase(iconstituent, words);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private String[] createValues(String s) {
        if ("has".equals(s)) return "has,has,have,VBZ".split(",");
        if ("is".equals(s)) return "is,is,be,VBZ".split(",");
        return (s + ',' + s + ',' + s + ",NN").split(",");
    }

    public Set<IndexedWord> getWords() {
        return words;
    }
}