package de.mpii.clausie;

import java.util.Scanner;

public final class Example {

    private Example() {
    }

    public static void main(String[] args) {
        ClausIE clausIE = new ClausIE();
        clausIE.initParser();
        clausIE.getOptions().print(System.out, "# ");
        // input sentence
        // sentence =  "Bell, a telecommunication company, which is based in Los Angeles, makes and distributes electronic, computer and building products.";
        // sentence =  "A table contains columns and rows to store data and formulae.";
        // sentence = "There is a ghost in the room";
        // sentence = "Bell sometimes makes products";
        // sentence = "By using its experise, Bell made great products in 1922 in Saarland.";
        // sentence = "Albert Einstein remained in Princeton.";
        // sentence = "Albert Einstein is smart.";
        // sentence = " Bell makes electronic, computer and building products.";

        try (Scanner keyboard = new Scanner(System.in)) {
            String sentence;
            while (true) {
                System.out.println("enter a text");
                sentence = keyboard.nextLine();
                System.out.println("Input sentence   : " + sentence);

                processInput(clausIE, sentence);
            }
        }
    }

    private static void processInput(ClausIE clausIE,
                                     String sentence) {
        // parse tree
        System.out.print("Parse time       : ");
        long start = System.currentTimeMillis();
        clausIE.parse(sentence);
        long end = System.currentTimeMillis();
        System.out.println((end - start) / 1000. + "s");
        System.out.print("Dependency parse : ");
        System.out.println(clausIE.getDepTree().pennString()
                .replaceAll("\n", "\n                   ").trim());
        System.out.print("Semantic graph   : ");
        System.out.println(clausIE.getSemanticGraph().toFormattedString()
                .replaceAll("\n", "\n                   ").trim());

        // clause detection
        System.out.print("ClausIE time     : ");
        start = System.currentTimeMillis();
        clausIE.detectClauses();
        clausIE.generatePropositions();
        end = System.currentTimeMillis();
        System.out.println((end - start) / 1000. + "s");
        System.out.print("Clauses          : ");
        String sep = "";
        for (Clause clause : clausIE.getClauses()) {
            System.out.println(sep + clause.toString(clausIE.getOptions()));
            sep = "                   ";
        }

        // generate propositions
        System.out.print("Propositions     : ");
        sep = "";
        for (Proposition prop : clausIE.getPropositions()) {
            System.out.println(sep + prop);
            sep = "                   ";
        }
    }
}