package com.LPT.main;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.Color;
import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
//import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.lang.Exception;
import java.util.Comparator;
import java.util.stream.Collectors;


enum RomanNumeral {
    I(1), IV(4), V(5), IX(9), X(10),
    XL(40), L(50), XC(90), C(100),
    CD(400), D(500), CM(900), M(1000);

    private int value;

    RomanNumeral(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static List<RomanNumeral> getReverseSortedValues() {
        return Arrays.stream(values())
                .sorted(Comparator.comparing((RomanNumeral e) -> e.value).reversed())
                .collect(Collectors.toList());
    }
}


public class Panel extends JPanel {

    JTextField input = new JTextField("Enter expression", 20);
    JLabel output = new JLabel("Result");
    JButton button = new JButton("Calculate");
    static String[] dev_list = new String[]{"+", "-", "*", "/"};
    //IOException MyException = new IOException();

    public Panel() {
        setLayout(null);
            //Поле для ввода текста
        input.setBounds(10, 10, 215, 25);
        input.setHorizontalAlignment(SwingConstants.CENTER);
        add(input);
            //Поле для вывода текста
        output.setBounds(10, 80, 215, 75);
        Border solidBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
        output.setBorder(solidBorder);
        output.setHorizontalAlignment(SwingConstants.CENTER);
        output.setVerticalAlignment(SwingConstants.CENTER);
        Font font = new Font(null, Font.BOLD, 20);
        output.setFont(font);
        add(output);
            //Кнопка расчета
        button.setBounds(68, 40, 100, 35);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);
        ActionListener actionListener = new ButtonListener();
        button.addActionListener(actionListener);
        add(button);
    }

    public class ButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e){ //Обработка нажатия кнопки расчета
            String text = input.getText();
            String res;
            try {
                res = calculator(text);
                Font font = new Font(null, Font.BOLD, 20);
                output.setFont(font);
                output.setText(res);

            } catch (MyException s){
                res = "<html>throws Exception //<br>" + s.getMessage();
                Font font = new Font(null, Font.PLAIN, 12);
                output.setFont(font);
                output.setText(res);
            }
        }

        public String calculator(String text) throws MyException { //Модуль калькулятора
            boolean roman_num = true; //Признак арабских чисел

                //Определение наличия математического действия
            int dev_pos = check_operator(text);
            if (dev_pos == 0) {
                throw new MyException("т.к. строка не является математической операцией</html>", 1);
            }
                //Проверка на второго оператора
            if (check_operator(text.substring(dev_pos + 1, text.length())) != 0) {
                throw new MyException("т.к. формат математической операции не удовлетворяет заданию - два операнда и один оператор (+, -, /, *)</html>", 2);
            }

                //Выделить главные элементы задания
            String str_1 = text.substring(0, dev_pos).trim();
            String str_2 = text.substring(dev_pos + 1, text.length()).trim();
            char operator = text.charAt(dev_pos);

                //Проверка латинских цифр и перевод в арабские цифры
            int num_1 = 0;
            int num_2 = 0;
            num_1 = roman_to_arab_numbers(str_1);
            num_2 = roman_to_arab_numbers(str_2);

            if (num_1 == 0 ^ num_2 == 0) {
                throw new MyException("т.к. используются одновременно разные системы счисления</html>", 3);
            }

            try {
                if (num_1 == 0) {
                    num_1 = Integer.parseInt(str_1);
                    roman_num = false;
                }
                if (num_2 == 0) {
                    num_2 = Integer.parseInt(str_2);
                    roman_num = false;
                }
            }catch (NumberFormatException e){
                throw new MyException("т.к. используются недопустимые символы</html>", 4);
            }

            if (num_1 < 1 | num_1 > 10 | num_2 < 1 | num_2 > 10) {
                throw new MyException("т.к. допускаются числа только от 1 до 10</html>", 5);
            }

            //Математическая операция
            int res = 0;
            switch (operator) {
                case '+':
                    res = num_1 + num_2;
                    break;
                case '-':
                    res = num_1 - num_2;
                    break;
                case '*':
                    res = num_1 * num_2;
                    break;
                case '/':
                    res = num_1 / num_2;
                    break;
                default:
                    //выдать исключение
                    break;
            }
                //Перевод в арабские числа и в строку
            String ret = "";
            if (roman_num){
                ret = arab_to_roman_numbers(res);
                if (ret == "-1"){
                    throw new MyException("т.к. в римской системе нет отрицательных чисел</html>", 6);
                }
            } else{
                ret = Integer.toString(res);
            }
            return ret;
        }

        public static int check_operator(String text) { //Определение наличия математического действия
            //System.out.println(text);
            int dev_pos = 0;
            for (String dev : dev_list) {
                dev_pos = text.indexOf(dev);
                if (dev_pos > 0) {
                    break;
                }
            }
            if (dev_pos < 1) {
                dev_pos = 0;
            }
            return dev_pos;
        }
        public static int roman_to_arab_numbers(String text){ //Модуль перевода римских цифр в арабские
            String romanNumeral = text.toUpperCase();
            int res = 0;
            List<RomanNumeral> romanNumerals = RomanNumeral.getReverseSortedValues();
            int i = 0;
            while ((romanNumeral.length() > 0) && (i < romanNumerals.size())) {
                RomanNumeral symbol = romanNumerals.get(i);
                if (romanNumeral.startsWith(symbol.name())) {
                    res += symbol.getValue();
                    romanNumeral = romanNumeral.substring(symbol.name().length());
                } else {
                    i++;
                }
            }

            if (romanNumeral.length() > 0) {
                res = 0;
            }

            return res;
        }
        public static String arab_to_roman_numbers (int number) {//Модуль перевода арабских чисел в римские
            if ((number <= 0) || (number > 4000)) {
                return "-1";
            }

            List<RomanNumeral> romanNumerals = RomanNumeral.getReverseSortedValues();

            int i = 0;
            StringBuilder sb = new StringBuilder();

            while ((number > 0) && (i < romanNumerals.size())) {
                RomanNumeral currentSymbol = romanNumerals.get(i);
                if (currentSymbol.getValue() <= number) {
                    sb.append(currentSymbol.name());
                    number -= currentSymbol.getValue();
                } else {
                    i++;
                }
            }
            return sb.toString();
        }

    }

    public class MyException extends Exception { //Модуль обработки исключений
        private int number;

        public int getNumber() {
            return number;
        }

        public MyException(String message, int num) {
            super(message);
            number = num;
            System.out.println(message);
        }

    }
}



