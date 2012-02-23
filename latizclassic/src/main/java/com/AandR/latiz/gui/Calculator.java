package com.AandR.latiz.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.nfunk.jep.JEP;

/**
 * @author Dr. Richard St. John
 * @version $Revision: 1.1 $, $Date: 2007/07/20 17:59:53 $
 */
public class Calculator extends JDialog implements CaretListener, ActionListener {

    /** Parser */
    private JEP myParser;
    private JTextField expressionField;
    private JLabel valueField;
    private StringBuilder expression;
    private int caretLocation;

    public Calculator() {
        super((JDialog) null, "Scientific Calculator", false);
        expression = new StringBuilder("");
        myParser = new JEP();
        myParser.initFunTab(); // clear the contents of the function table
        myParser.initSymTab(); // clear the contents of the symbol table
        myParser.addStandardFunctions();
        myParser.addStandardConstants();
        myParser.addComplex(); // among other things adds i to the symbol table
//        myParser.setAllowAssignment(true);
        myParser.setImplicitMul(true);
        myParser.setTraverse(false);
        setContentPane(createContentPane());
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        super.setSize(540, 500);
        super.setAlwaysOnTop(true);
        int locX = (int) (1.2 * (screenDim.width - this.getWidth()) / 2);
        int locY = (int) (.6 * (screenDim.height - this.getHeight()) / 2);
        setLocation(locX, locY);
    }

    private Container createContentPane() {
        expressionField = new JTextField(30);
        expressionField.addCaretListener(this);

        Color numberColor = new Color(180, 180, 180);
        Color operatorColor = new Color(100, 100, 255);
        Color trigColor = new Color(255, 160, 160);
        Color consColor = new Color(110, 255, 110);
        JPanel numberPanel = new JPanel(new GridLayout(9, 6, 10, 10));
        numberPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        numberPanel.add(createButton("Clear", Color.WHITE, Color.RED));
        numberPanel.add(createButton("End", Color.WHITE, Color.RED));
        numberPanel.add(createButton("sinh", Color.WHITE, trigColor));
        numberPanel.add(createButton("cosh", Color.WHITE, trigColor));
        numberPanel.add(createButton("tanh", Color.WHITE, trigColor));
        numberPanel.add(createButton("i", Color.WHITE, consColor));

        numberPanel.add(createButton("rand", Color.WHITE, Color.BLACK));
        numberPanel.add(createButton("sqrt", Color.WHITE, Color.BLACK));
        numberPanel.add(createButton("asinh", Color.WHITE, trigColor));
        numberPanel.add(createButton("acosh", Color.WHITE, trigColor));
        numberPanel.add(createButton("atanh", Color.WHITE, trigColor));
        numberPanel.add(createButton("e", Color.WHITE, consColor));

        numberPanel.add(createButton("sum", Color.WHITE, Color.BLACK));
        numberPanel.add(createButton("^2", Color.WHITE, Color.BLACK));
        numberPanel.add(createButton("sin", Color.WHITE, trigColor));
        numberPanel.add(createButton("cos", Color.WHITE, trigColor));
        numberPanel.add(createButton("tan", Color.WHITE, trigColor));
        numberPanel.add(createButton("pi", Color.WHITE, consColor));

        numberPanel.add(createButton("binom", Color.WHITE, Color.BLACK));
        numberPanel.add(createButton("^-1", Color.WHITE, Color.BLACK));
        numberPanel.add(createButton("asin", Color.WHITE, trigColor));
        numberPanel.add(createButton("acos", Color.WHITE, trigColor));
        numberPanel.add(createButton("atan", Color.WHITE, trigColor));
        numberPanel.add(createButton("atan2", Color.WHITE, trigColor));

        numberPanel.add(createButton("toDeg", Color.WHITE, Color.BLACK));
        numberPanel.add(createButton("log", Color.WHITE, Color.BLACK));
        numberPanel.add(createButton(",", Color.WHITE, operatorColor));
        numberPanel.add(createButton("(", Color.WHITE, operatorColor));
        numberPanel.add(createButton(")", Color.WHITE, operatorColor));
        numberPanel.add(createButton("^", Color.WHITE, operatorColor));

        numberPanel.add(createButton("toRad", Color.WHITE, Color.BLACK));
        numberPanel.add(createButton("ln", Color.WHITE, Color.BLACK));
        numberPanel.add(createButton("7", Color.WHITE, numberColor));
        numberPanel.add(createButton("8", Color.WHITE, numberColor));
        numberPanel.add(createButton("9", Color.WHITE, numberColor));
        numberPanel.add(createButton("/", Color.WHITE, operatorColor));

        numberPanel.add(createButton("", Color.WHITE, Color.BLACK));
        numberPanel.add(createButton("exp", Color.WHITE, Color.BLACK));
        numberPanel.add(createButton("4", Color.WHITE, numberColor));
        numberPanel.add(createButton("5", Color.WHITE, numberColor));
        numberPanel.add(createButton("6", Color.WHITE, numberColor));
        numberPanel.add(createButton("*", Color.WHITE, operatorColor));

        numberPanel.add(createButton("", Color.WHITE, Color.BLACK));
        numberPanel.add(createButton("mod", Color.WHITE, Color.BLACK));
        numberPanel.add(createButton("1", Color.WHITE, numberColor));
        numberPanel.add(createButton("2", Color.WHITE, numberColor));
        numberPanel.add(createButton("3", Color.WHITE, numberColor));
        numberPanel.add(createButton("-", Color.WHITE, operatorColor));

        numberPanel.add(createButton("", Color.WHITE, Color.BLACK));
        numberPanel.add(createButton("abs", Color.WHITE, Color.BLACK));
        numberPanel.add(createButton("0", Color.WHITE, numberColor));
        numberPanel.add(createButton(".", Color.WHITE, numberColor));
        numberPanel.add(createButton("(-)", Color.WHITE, numberColor));
        numberPanel.add(createButton("+", Color.WHITE, operatorColor));
        valueField = new JLabel(" ");

        JPanel expressionPanel = new JPanel();
        expressionPanel.setLayout(new BoxLayout(expressionPanel, BoxLayout.Y_AXIS));
        expressionPanel.add(expressionField);
        JPanel ansPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        ansPanel.add(new JLabel("="));
        ansPanel.add(valueField);
        expressionPanel.add(ansPanel);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.add(expressionPanel, BorderLayout.NORTH);
        panel.add(numberPanel, BorderLayout.CENTER);
        panel.setMinimumSize(new Dimension(250, 200));
        return panel;
    }

    public static void main(String arg[]) {
        Calculator calculator = new Calculator();
    }

    public void caretUpdate(CaretEvent e) {
        expression.delete(0, expression.length());
        expression.append(expressionField.getText());
        myParser.parseExpression(expressionField.getText());
        updateResult();
    }

    private JButton createButton(String label, Color textColor, Color buttonColor) {
        Dimension dim = new Dimension(40, 25);
        JButton button = new JButton(label);
        button.addActionListener(this);
        button.setForeground(textColor);
        button.setBackground(buttonColor);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 11.0f));
        button.setPreferredSize(dim);
        button.setMinimumSize(dim);
        button.setMaximumSize(dim);
        return button;
    }

    /**
     * This method uses JEP's getValueAsObject() method to obtain the current
     * value of the expression entered.
     */
    private void updateResult() {
        Object result;

        // Get the value
        result = myParser.getValueAsObject();
        valueField.setText(String.valueOf(result));

        // Is the result ok?
        if (result != null) {
            valueField.setText(result.toString());
        } else {
            valueField.setText(" ");
        }
    }

    public void actionPerformed(ActionEvent e) {
        String label = ((JButton) e.getSource()).getText();
        caretLocation = expressionField.getCaretPosition();
        int caretOffset = 0;
        if (label.equalsIgnoreCase("Clear")) {
            label = "";
            expression.delete(0, expression.length());
            expressionField.setText(label);
            expressionField.setCaretPosition(0);
            expressionField.setFocusable(true);
            expressionField.requestFocus();
            return;
        } else if (label.equalsIgnoreCase("exp") || label.equalsIgnoreCase("mod") || label.equalsIgnoreCase("abs")) {
            label += "()";
            caretOffset = -1;
        } else if (label.equalsIgnoreCase("(-)")) {
            label = "-";
        } else if (label.startsWith("cos") || label.startsWith("sin") || label.startsWith("tan")) {
            label += "()";
            caretOffset = -1;
        } else if (label.startsWith("acos") || label.startsWith("asin") || label.startsWith("atan")) {
            label += "()";
            caretOffset = -1;
        } else if (label.startsWith("sqrt") || label.startsWith("log") || label.startsWith("ln")) {
            label += "()";
            caretOffset = -1;
        } else if (label.startsWith("sum") || label.startsWith("binom")) {
            label += "()";
            caretOffset = -1;
        } else if (label.startsWith("rand")) {
            label += "()";
            caretOffset = 0;
        } else if (label.startsWith("toDeg")) {
            label = "*(180/pi)";
        } else if (label.startsWith("toRad")) {
            label = "*(pi/180)";
        } else if (label.startsWith("End")) {
            label = "";
            caretLocation = expressionField.getText().length();
        }
        expression.insert(caretLocation, label);
        expressionField.setText(expression.toString());
        expressionField.setCaretPosition(caretLocation + label.length() + caretOffset);
        expressionField.setFocusable(true);
        expressionField.requestFocus();
    }
}
