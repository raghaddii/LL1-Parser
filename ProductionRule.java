/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.cp2;


import com.mycompany.cp2.NonTerminal;

import java.util.ArrayList;

public class ProductionRule {

    //The WordType must be NonTerminal
    private NonTerminal nonTerminal;
    private ArrayList<Word> rule;

    public ProductionRule(NonTerminal nonTerminal) {
        this.nonTerminal = nonTerminal;
        rule = new ArrayList<>();
    }

    public ProductionRule(NonTerminal nonTerminal, ArrayList<Word> rule) {
        this.nonTerminal = nonTerminal;
        this.rule = rule;
    }

    public NonTerminal getNonTerminal() {
        return nonTerminal;
    }

    public ArrayList<Word> getWords() {
        return rule;
    }


}