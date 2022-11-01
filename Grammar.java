/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.cp2;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;

public class Grammar {

    private ArrayList<ProductionRule> rules = new ArrayList<>();
    //Declared for computing follow set
    private ArrayList<NonTerminal> openList = new ArrayList<>();
    private HashSet<Terminal> terminals = new HashSet<>();
    private HashMap<NonTerminal,ArrayList<ProductionRule>> nonTerminals = new HashMap<>();

    private HashMap<NonTerminal,HashMap<Terminal,Integer>> parseTable = new HashMap<>();

    private StringBuilder grammar = new StringBuilder();

    private boolean isNullable(Word word){
        if (word.getName().equals("#"))
            return true;
        if (word instanceof Terminal)
            return false;

        NonTerminal nt = (NonTerminal) word;

        //It set before
        if (nt.isNullable() != null)
            return nt.isNullable();

        for (ProductionRule rule : nonTerminals.get(nt)) {
            boolean b = true;
            for (Word w : rule.getWords()) {
                if (!isNullable(w)) {
                    b = false;
                    break;
                }
            }
            if (b){
                nt.setNullable(true);
                return true;
            }
        }
        nt.setNullable(false);
        return false;
    }

    private HashSet<Terminal> first(NonTerminal word){
        //It calculated before
        if (word.getFirstSet()!=null)
            return word.getFirstSet();

        HashSet<Terminal> set = new HashSet<>();

        for (ProductionRule rule : nonTerminals.get(word)) {
            for (Word w : rule.getWords()) {
                if (w instanceof Terminal) {
                    if(!w.getName().equals("#"))
                        set.add((Terminal) w);
                    break;
                }
                NonTerminal nt = (NonTerminal) w;
                set.addAll(first(nt));
                if (!isNullable(w))
                    break;
            }
        }

        word.setFirstSet(set);
        return set;
    }

    private HashSet<Terminal> first(int productRuleNum){
        HashSet<Terminal> set = new HashSet<>();

        for (Word w : rules.get(productRuleNum).getWords()) {
            if (w instanceof Terminal) {
                if(!w.getName().equals("#"))
                    set.add((Terminal) w);
                break;
            }
            NonTerminal nt = (NonTerminal) w;
            set.addAll(first(nt));
            if (!isNullable(w))
                break;
        }
        return set;
    }

    private HashSet<Terminal> follow (NonTerminal nonT){
        //It calculated before
        if (nonT.getFollowSet()!=null)
            return nonT.getFollowSet();

        HashSet<Terminal> set = new HashSet<>();

        if (nonT.getName().equals("PROGRAM")){
            set.add(new Terminal("$"));
            nonT.setFollowSet(set);
            return set;
        }
        openList.add(nonT);

        for (ProductionRule rule : rules) {
            int index= rule.getWords().indexOf(nonT);
            if (index > -1){
                while (index<rule.getWords().size()) {

                    if (index == rule.getWords().size() - 1) {
                        //To avoid getting stuck in the loop
                        if (!openList.contains(rule.getNonTerminal()))
                            set.addAll(follow(rule.getNonTerminal()));
                        break;
                    }
                    index++;
                    Word word = rule.getWords().get(index);
                    if (word instanceof Terminal) {
                        if(!word.getName().equals("#"))
                            set.add((Terminal) word);
                        break;
                    }
                    set.addAll(first((NonTerminal) word));
                    if (!isNullable(word))
                        break;

                }
            }
        }
        openList.clear();
        nonT.setFollowSet(set);
        return set;
    }

    public HashSet<Terminal> predictSet(int productRuleNum){

        ProductionRule rule = rules.get(productRuleNum);

        HashSet<Terminal> set = new HashSet<>(first(productRuleNum));
        if (isNullable(rule.getNonTerminal()))
            set.addAll(follow(rule.getNonTerminal()));

        return set;
    }

    public void initGrammar(String url){

        while (true) {
            File file = new File(url);
            try (Scanner scanner = new Scanner(file)) {
                int i = 0;
                while (scanner.hasNext()) {
                    String rule = scanner.nextLine();
                    grammar.append(i++).append(". ").append(rule);
                    String nonT = rule.substring(0, rule.indexOf(":")).trim();

                    NonTerminal nt = new NonTerminal(nonT);
                    if (!nonTerminals.containsKey(nt))
                        nonTerminals.put(nt, new ArrayList<>());

                    String[] words = rule.substring(rule.indexOf(":") + 1).trim().split(" ");
                    ArrayList<Word> list = new ArrayList<>();
                    for (String s : words) {
                        if (s.toLowerCase().equals(s)) {
                            Terminal t = new Terminal(s);
                            list.add(t);
                            if (!s.equals("#"))
                                terminals.add(t);
                        } else
                            list.add(new NonTerminal(s));
                    }
                    ProductionRule pr = new ProductionRule(nt, list);
                    rules.add(pr);
                    nonTerminals.get(nt).add(pr);
                    grammar.append("\n");
                }

                terminals.add(new Terminal("$"));
                break;
            } catch (FileNotFoundException e) {
                System.out.println("The url is wrong. Please reEnter the input file url");
                Scanner scanner = new Scanner(System.in);
                url = scanner.next();
            }
        }
    }

    public void computeParseTable(){
        for (int i=0; i < rules.size(); i++) {
            NonTerminal nt = rules.get(i).getNonTerminal();
            if (!parseTable.containsKey(nt))
                parseTable.put(nt, new HashMap<>());
            HashSet<Terminal> set = predictSet(i);
            for (Terminal t : set) {
                parseTable.get(nt).put(t,i);
            }
        }
    }

    public void run (String url){
        initGrammar(url);

        //Compute nullable for each Non-Terminal
        for (NonTerminal nt : nonTerminals.keySet()) {
            isNullable(nt);
        }

        //Compute FirstSet for each Non-Terminal
        for (NonTerminal nt : nonTerminals.keySet()) {
            first(nt);
        }

        //Compute FollowSet for each Non-Terminal
        for (NonTerminal nt : nonTerminals.keySet()) {
            follow(nt);
        }

        computeParseTable();
    }

    private void Sd() {
        System.out.println("NT       |isNullable|        First Set             |        Follow Set            ");
        System.out.println("------------------------------------------------------------------------------");

       for (NonTerminal nt : nonTerminals.keySet()) {
           System.out.printf("%-9s|  %-8s| %-29s| %s\n",nt,nt.isNullable(),
                 Arrays.toString(nt.getFirstSet().toArray()),Arrays.toString(nt.getFollowSet().toArray()));
        }
    }

    private void printFollows() {
        System.out.println("Follows: ");
        for (NonTerminal nt : nonTerminals.keySet()) {
            System.out.printf("%-8s : %s\n",nt,Arrays.toString(nt.getFollowSet().toArray()));
        }
    }
    private void printFirsts() {
        System.out.println("Firsts: ");
        for (NonTerminal nt : nonTerminals.keySet()) {
            System.out.printf("%-8s : %s\n",nt,Arrays.toString(nt.getFirstSet().toArray()));
        }
    }

    private void printParseTable() {
        System.out.println("PARSE TABLE:");
        System.out.print("---      ");
        for (Terminal t : terminals) {
            System.out.printf("|  %-6s",t);
        }
        System.out.println();
        System.out.print("----------");
        for (Terminal t : terminals) {
            System.out.print("---------");
        }
        System.out.println();

        for (NonTerminal nt : nonTerminals.keySet()) {
            System.out.printf("%-9s",nt);
            for (Terminal t : terminals) {
                if (parseTable.get(nt).containsKey(t))
                    System.out.printf("|   %-2d   ", parseTable.get(nt).get(t));
                else
                    System.out.print("|        ");
            }
            System.out.println();
        }
    }

    private void printGrammar() {
        System.out.println(grammar);
    }


    public static void main(String[] args) throws FileNotFoundException {

        System.out.println(" LL(1) Parser \n");
        String url = "src/main/java/com/mycompany/cp2/input.txt";

        Grammar grammar = new Grammar();
        grammar.run(url);

        PrintStream out = new PrintStream(new FileOutputStream("src/main/java/com/mycompany/cp2/Output.txt"));
        System.setOut(out);

            System.out.println();
            grammar.Sd();
System.out.println();
System.out.println();
            grammar.printParseTable();
            
    }
}