package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView screen;
    private Button cE, c, bS, div, multi, plus, minus, equals, dot, plus_minus;
    private Button zero, one, two, three, four, five, six, seven, eight, nine;
    private String input="",answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        screen = findViewById(R.id.screen);
        cE = findViewById(R.id.ce);
        c = findViewById(R.id.c);
        bS = findViewById(R.id.bs);
        div = findViewById(R.id.div);
        multi = findViewById(R.id.multi);
        plus = findViewById(R.id.plus);
        minus = findViewById(R.id.minus);
        equals = findViewById(R.id.equals);
        dot = findViewById(R.id.dot);
        plus_minus = findViewById(R.id.plus_minus);
        zero = findViewById(R.id.zero);
        one = findViewById(R.id.one);
        two = findViewById(R.id.two);
        three = findViewById(R.id.three);
        four = findViewById(R.id.four);
        five = findViewById(R.id.five);
        six = findViewById(R.id.six);
        seven = findViewById(R.id.seven);
        eight = findViewById(R.id.eight);
        nine = findViewById(R.id.nine);

        cE.setOnClickListener(this);
        c.setOnClickListener(this);
        bS.setOnClickListener(this);
        div.setOnClickListener(this);
        multi.setOnClickListener(this);
        plus.setOnClickListener(this);
        minus.setOnClickListener(this);
        equals.setOnClickListener(this);
        dot.setOnClickListener(this);
        plus_minus.setOnClickListener(this);
        zero.setOnClickListener(this);
        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);
        five.setOnClickListener(this);
        six.setOnClickListener(this);
        seven.setOnClickListener(this);
        eight.setOnClickListener(this);
        nine.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        Button button = (Button) v;
        String data = button.getText().toString();
        System.out.println(data);
        switch (data){
            case "C":
                input = "";
                break;
            case "BS":
                if(input.length()>0){
                    String newText = input.substring(0,input.length()-1);
                    input=newText;
                }
                break;
            case "CE":
                if(input.length()>1){
                    String newText = input.substring(0,input.length()-1);
                    input=newText + "0";
                }
                if (input.length() == 1){
                    input = "";
                }
                break;
            case "=":
                try {
                    System.out.println(input);
                    input = solve();
                    break;
                } catch (ScriptException e) {
                    e.printStackTrace();
                }
            default:
                if (input == null){
                    input = "";
                }
                input+=data;
        }
        screen.setText(input);
    }

    public String solve() throws  {
        input = input.replace("÷", "/");
        input = input.replace("×", "*");
        return String.valueOf((int)eval(input));
    }


    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }
}