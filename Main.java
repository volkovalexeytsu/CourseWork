package com.company;

import java.io.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Arrays;
import java.util.regex.*;
import java.util.Properties;
import java.util.List;
import java.util.LinkedList;


public class Main {

    static int ipos = 0;

    static String strtoparse = "";

    static String[] parseArray;

    static String[] typeArray;

    public static String GlobalExpr(String lex) {
        //Число
        Pattern pat = Pattern.compile("[-]?[0-9]+(,[0-9]+)?");
        Matcher matcher = pat.matcher(lex);
        if (matcher.find()) {
            if (ipos > 0) {
                if (typeArray[ipos - 1].equals("var")) {
                    return "_{" + lex + "}";
                } else {
                    return lex;
                }
            } else {
                return lex;
            }
        } else {
            switch (lex) {
                //ЧТО С ПАДЕЖАМИ!?!?!?!
                //TODO пробелы между выражениями, красоту
                //WARNING! Аккуратней с падежами
                case "СУММА":
                    return StrSumProd("\\sum");
                case "ПРОИЗВЕДЕНИЕ":
                    return StrSumProd("\\prod");
                //Тригонометрия
                case "СИНУС":
                    return StrTrig("\\sin");
                case "КОСИНУС":
                    return StrTrig("\\cos");
                //Символы отношений
                case "БОЛЬШЕ":
                    if (ipos < parseArray.length - 2) {
                        if (parseArray[ipos + 1].equals("ИЛИ") && parseArray[ipos + 2].equals("РАВНО")) {
                            ipos = ipos + 2;
                            return "\\ge ";
                        }
                    }
                    return ">";
                case "МЕНЬШЕ":
                    if (ipos < parseArray.length - 2) {
                        if (parseArray[ipos + 1].equals("ИЛИ") && parseArray[ipos + 2].equals("РАВНО")) {
                            ipos = ipos + 2;
                            return "\\le ";
                        }
                    }
                    return "<";
                case "РАВНО":
                    if (ipos > 0) {
                        if (parseArray[ipos - 1].equals("ПРИБЛИЗИТЕЛЬНО")) {
                            return "\\approx ";
                        } else {
                            return "= ";
                        }
                    } else {
                        return "= ";
                    }
                case "РАВЕН":
                    if (ipos > 0) {
                        if (parseArray[ipos - 1].equals("ПРИБЛИЗИТЕЛЬНО")) {
                            return "\\approx ";
                        } else {
                            return "= ";
                        }
                    } else {
                        return "= ";
                    }
                case "ПРИНАДЛЕЖИТ":
                    return "\\in";
                case "ВКЛЮЧЕНО":
                    return "\\subset ";
                case "ВКЛЮЧЕН":
                    return "\\subset ";
                case "ВКЛЮЧАЕТ":
                    return "\\supset ";
                //ЛОГИКА
                case "СУЩЕСТВУЕТ":
                    return "\\exists ";
                case "ДЛЯ":
                    if (ipos < parseArray.length - 1) {
                        if (
                                parseArray[ipos + 1].equals("ЛЮБОГО") ||
                                        parseArray[ipos + 1].equals("ЛЮБОЙ") ||
                                        parseArray[ipos + 1].equals("ЛЮБЫХ") ||
                                        parseArray[ipos + 1].equals("ВСЕХ") ||
                                        parseArray[ipos + 1].equals("ВСЯКОГО")
                        ) {
                            ipos++;
                            return "\\forall ";
                        } else {
                            return "";
                        }
                    } else {
                        return "";
                    }
                    //МАТЕМАТИКА
                case "ПЛЮС": //+inf Плюс-Минус
                    if (ipos < parseArray.length - 1) {
                        if (parseArray[ipos + 1].equals("МИНУС")) {
                            if (ipos < parseArray.length - 2) {
                                if (parseArray[ipos + 2].equals("БЕСКОНЕЧНОСТЬ")) {
                                    ipos = ipos + 2;
                                    return "\\pm \\infty ";
                                } else {
                                    ipos++;
                                    return "\\pm ";
                                }
                            } else {
                                ipos++;
                                return "\\pm ";
                            }
                        } else if (parseArray[ipos + 1].equals("БЕСКОНЕЧНОСТЬ")) {
                            ipos++;
                            return "+\\infty ";
                        } else {
                            return "+";
                        }
                    } else {
                        return "+";
                    }
                case "МИНУС": //-inf Плюс-Минус
                    if (ipos < parseArray.length - 1) {
                        if (parseArray[ipos + 1].equals("ПЛЮС")) {
                            if (ipos < parseArray.length - 2) {
                                if (parseArray[ipos + 2].equals("БЕСКОНЕЧНОСТЬ")) {
                                    ipos = ipos + 2;
                                    return "\\pm \\infty ";
                                } else {
                                    ipos++;
                                    return "\\pm ";
                                }
                            } else {
                                ipos++;
                                return "\\pm ";
                            }
                        } else if (parseArray[ipos + 1].equals("БЕСКОНЕЧНОСТЬ")) {
                            ipos++;
                            return "-\\infty ";
                        } else {
                            return "-";
                        }
                    } else {
                        return "-";
                    }
                case "УМНОЖИТЬ":
                    return "*";
                //TODO Бывает пределы функций и последовательностей доработать
                case "ПРЕДЕЛ":
                    return StrLim();
                case "ДРОБЬ":
                    return StrFrac();
                //TODO Есть разные интегралы
                case "ИНТЕГРАЛ":
                    return StrInteg();
                //ТЕОРИЯ
                case "ИНФИМУМ":
                    return "\\inf ";
                case "СУПРЕМУМ":
                    return "\\sup ";
                //Переменные
                case "ИКС":
                    typeArray[ipos] = "var";
                    return "x ";
                case "ИГРИК":
                    typeArray[ipos] = "var";
                    return "y ";
                case "БЕСКОНЕЧНОСТЬ": //БЕСКОНЕЧНОСТИ?
                    return "\\infty ";
                case "ЧЕРТОЧКА":
                    return "\\overline ";
                case "ЧЕРТА":
                    return "\\overline ";
                case "ШТРИХ":
                    return "'";
                case "ФАКТОРИАЛ":
                    return "!";
                case "СКОБКА":
                    if (ipos > 0) {
                        if (parseArray[ipos].equals("ОТКРЫВАЮЩАЯ")) {
                            return "(";
                        } else if (parseArray[ipos].equals("ЗАКРЫВАЮЩАЯ")) {
                            return ")";
                        } else {
                            return "";
                        }
                    } else {
                        return "";
                    }
                case "МОДУЛЬ":
                    return StrFunctions("\\left |", "\\right |");
                case "МНОЖЕСТВО": //Принадлежит множествУ //Супремум множествА
                    return StrFunctions("\\{", "\\}");
                case "ИНДЕКС": //Порядковый номер???
                    return StrFunctions("_{", "}");
                //КОРЕНЬ КВАДРАТНЫЙ - бывают и другие
                case "КОРЕНЬ":
                    return StrFunctions("\\sqrt{", "}");
                case "ПРОИЗВОДНАЯ":
                    return StrFunctions("(", ")^\\prime");
                //Степень
                case "В":
                    if (ipos < parseArray.length - 1) {
                        if (parseArray[ipos + 1].equals("СТЕПЕНИ")) {
                            ipos++;
                            return StrFunctions("^{", "}");
                        } else {
                            return "";
                        }
                    } else {
                        return "";
                    }
                case "СТЕПЕНЬ":
                    return StrFunctions("^{", "}");
                //Греческий алфавит
                case "АЛЬФА":
                    if (ipos < parseArray.length - 1) {
                        if (parseArray[ipos + 1].equals("БОЛЬШАЯ") || parseArray[ipos + 1].equals("БОЛЬШОЙ")) {
                            ipos++;
                            return "A ";
                        } else {
                            return "\\alpha ";
                        }
                    } else {
                        return "\\alpha ";
                    }
                case "БЕТА":
                    if (ipos < parseArray.length - 1) {
                        if (parseArray[ipos + 1].equals("БОЛЬШАЯ") || parseArray[ipos + 1].equals("БОЛЬШОЙ")) {
                            ipos++;
                            return "B ";
                        } else {
                            return "\\beta ";
                        }
                    } else {
                        return "\\beta ";
                    }
                case "ГАММА":
                    if (ipos < parseArray.length - 1) {
                        if (parseArray[ipos + 1].equals("БОЛЬШАЯ") || parseArray[ipos + 1].equals("БОЛЬШОЙ")) {
                            ipos++;
                            return "\\Gamma ";
                        } else {
                            return "\\gamma ";
                        }
                    } else {
                        return "\\gamma ";
                    }
                case "ДЕЛЬТА":
                    if (ipos < parseArray.length - 1) {
                        if (parseArray[ipos + 1].equals("БОЛЬШАЯ") || parseArray[ipos + 1].equals("БОЛЬШОЙ")) {
                            ipos++;
                            return "\\Delta ";
                        } else {
                            return "\\delta ";
                        }
                    } else {
                        return "\\delta ";
                    }
                case "ЭПСИЛОН":
                    if (ipos < parseArray.length - 1) {
                        if (parseArray[ipos + 1].equals("БОЛЬШАЯ") || parseArray[ipos + 1].equals("БОЛЬШОЙ")) {
                            ipos++;
                            return "E ";
                        } else {
                            return "\\epsilon ";
                        }
                    } else {
                        return "\\epsilon ";
                    }
                case "ДЗЕТА":
                    if (ipos < parseArray.length - 1) {
                        if (parseArray[ipos + 1].equals("БОЛЬШАЯ") || parseArray[ipos + 1].equals("БОЛЬШОЙ")) {
                            ipos++;
                            return "Z ";
                        } else {
                            return "\\zeta ";
                        }
                    } else {
                        return "\\zeta ";
                    }
                case "ЭТА":
                    if (ipos < parseArray.length - 1) {
                        if (parseArray[ipos + 1].equals("БОЛЬШАЯ") || parseArray[ipos + 1].equals("БОЛЬШОЙ")) {
                            ipos++;
                            return "E ";
                        } else {
                            return "\\eta ";
                        }
                    } else {
                        return "\\eta ";
                    }
                case "ТЕТА":
                    if (ipos < parseArray.length - 1) {
                        if (parseArray[ipos + 1].equals("БОЛЬШАЯ") || parseArray[ipos + 1].equals("БОЛЬШОЙ")) {
                            ipos++;
                            return "\\Theta ";
                        } else {
                            return "\\theta ";
                        }
                    } else {
                        return "\\theta ";
                    }
                case "ЙОТА":
                    if (ipos < parseArray.length - 1) {
                        if (parseArray[ipos + 1].equals("БОЛЬШАЯ") || parseArray[ipos + 1].equals("БОЛЬШОЙ")) {
                            ipos++;
                            return "I ";
                        } else {
                            return "\\iota ";
                        }
                    } else {
                        return "\\iota ";
                    }
                case "КАППА":
                    if (ipos < parseArray.length - 1) {
                        if (parseArray[ipos + 1].equals("БОЛЬШАЯ") || parseArray[ipos + 1].equals("БОЛЬШОЙ")) {
                            ipos++;
                            return "K ";
                        } else {
                            return "\\kappa ";
                        }
                    } else {
                        return "\\kappa ";
                    }
                case "ЛЯМБДА":
                    if (ipos < parseArray.length - 1) {
                        if (parseArray[ipos + 1].equals("БОЛЬШАЯ") || parseArray[ipos + 1].equals("БОЛЬШОЙ")) {
                            ipos++;
                            return "\\Lambda ";
                        } else {
                            return "\\lambda ";
                        }
                    } else {
                        return "\\lambda ";
                    }
                case "МЮ":
                    if (ipos < parseArray.length - 1) {
                        if (parseArray[ipos + 1].equals("БОЛЬШАЯ") || parseArray[ipos + 1].equals("БОЛЬШОЙ")) {
                            ipos++;
                            return "M ";
                        } else {
                            return "\\mu ";
                        }
                    } else {
                        return "\\mu ";
                    }
                case "НЮ":
                    if (ipos < parseArray.length - 1) {
                        if (parseArray[ipos + 1].equals("БОЛЬШАЯ") || parseArray[ipos + 1].equals("БОЛЬШОЙ")) {
                            ipos++;
                            return "N ";
                        } else {
                            return "\\nu ";
                        }
                    } else {
                        return "\\nu ";
                    }
                case "КСИ":
                    if (ipos < parseArray.length - 1) {
                        if (parseArray[ipos + 1].equals("БОЛЬШАЯ") || parseArray[ipos + 1].equals("БОЛЬШОЙ")) {
                            ipos++;
                            return "\\Xi ";
                        } else {
                            return "\\xi ";
                        }
                    } else {
                        return "\\xi ";
                    }
                case "ОМИКРОН":
                    if (ipos < parseArray.length - 1) {
                        if (parseArray[ipos + 1].equals("БОЛЬШАЯ") || parseArray[ipos + 1].equals("БОЛЬШОЙ")) {
                            ipos++;
                            return "O ";
                        } else {
                            return "\\omicron ";
                        }
                    } else {
                        return "\\omicron ";
                    }
                case "ПИ":
                    if (ipos < parseArray.length - 1) {
                        if (parseArray[ipos + 1].equals("БОЛЬШАЯ") || parseArray[ipos + 1].equals("БОЛЬШОЙ")) {
                            ipos++;
                            return "\\Pi ";
                        } else {
                            return "\\pi ";
                        }
                    } else {
                        return "\\pi ";
                    }
                case "РО":
                    if (ipos < parseArray.length - 1) {
                        if (parseArray[ipos + 1].equals("БОЛЬШАЯ") || parseArray[ipos + 1].equals("БОЛЬШОЙ")) {
                            ipos++;
                            return "R ";
                        } else {
                            return "\\rho ";
                        }
                    } else {
                        return "\\rho ";
                    }
                case "СИГМА":
                    if (ipos < parseArray.length - 1) {
                        if (parseArray[ipos + 1].equals("БОЛЬШАЯ") || parseArray[ipos + 1].equals("БОЛЬШОЙ")) {
                            ipos++;
                            return "\\Sigma ";
                        } else {
                            return "\\sigma ";
                        }
                    } else {
                        return "\\sigma ";
                    }
                case "ТАУ":
                    if (ipos < parseArray.length - 1) {
                        if (parseArray[ipos + 1].equals("БОЛЬШАЯ") || parseArray[ipos + 1].equals("БОЛЬШОЙ")) {
                            ipos++;
                            return "T ";
                        } else {
                            return "\\tau ";
                        }
                    } else {
                        return "\\tau ";
                    }
                case "ИПСИЛОН":
                    if (ipos < parseArray.length - 1) {
                        if (parseArray[ipos + 1].equals("БОЛЬШАЯ") || parseArray[ipos + 1].equals("БОЛЬШОЙ")) {
                            ipos++;
                            return "\\Upsilon ";
                        } else {
                            return "\\upsilon ";
                        }
                    } else {
                        return "\\upsilon ";
                    }
                case "ФИ":
                    if (ipos < parseArray.length - 1) {
                        if (parseArray[ipos + 1].equals("БОЛЬШАЯ") || parseArray[ipos + 1].equals("БОЛЬШОЙ")) {
                            ipos++;
                            return "\\Phi ";
                        } else {
                            return "\\phi ";
                        }
                    } else {
                        return "\\phi ";
                    }
                case "ХИ":
                    if (ipos < parseArray.length - 1) {
                        if (parseArray[ipos + 1].equals("БОЛЬШАЯ") || parseArray[ipos + 1].equals("БОЛЬШОЙ")) {
                            ipos++;
                            return "X ";
                        } else {
                            return "\\chi ";
                        }
                    } else {
                        return "\\chi ";
                    }
                case "ПСИ":
                    if (ipos < parseArray.length - 1) {
                        if (parseArray[ipos + 1].equals("БОЛЬШАЯ") || parseArray[ipos + 1].equals("БОЛЬШОЙ")) {
                            ipos++;
                            return "\\Psi ";
                        } else {
                            return "\\psi ";
                        }
                    } else {
                        return "\\psi ";
                    }
                case "ОМЕГА":
                    if (ipos < parseArray.length - 1) {
                        if (parseArray[ipos + 1].equals("БОЛЬШАЯ") || parseArray[ipos + 1].equals("БОЛЬШОЙ")) {
                            ipos++;
                            return "\\Omega ";
                        } else {
                            return "\\omega ";
                        }
                    } else {
                        return "\\omega ";
                    }
                default:
                    return "";
            }
        }
    }


    public static String StrFunctions(String arg1, String arg2) {
        ipos++;
        while (!parseArray[ipos].equals("КОНЕЦ")) {
            arg1 = arg1 + GlobalExpr(parseArray[ipos]);
            //Проверка ошибок
            if (ipos == parseArray.length - 1) {
                System.out.println("Не обозначен конец выражения");
                System.exit(0);
            }
            ipos++;
        }
        ipos++; //WARNING! Аккуратней с падежами
        arg1 = arg1 + arg2;
        return arg1;
    }


	/*public static String StrIndex() {
		ipos++;
		String ind = "_{";
		while (!parseArray[ipos].equals("КОНЕЦ")) {
			ind = ind + GlobalExpr(parseArray[ipos]);
			//Проверка ошибок
			if (ipos == parseArray.length - 1) {
				System.out.println("Не обозначен конец Индекса");
				System.exit(0);
			}
			ipos++;
		}
		ipos++; //WARNING! Аккуратней с падежами
		ind = ind + "}";
		return ind;
	}*/

    //Степень в строке
	/*public static String StrStep() {
		ipos = ipos + 2;
		String step = "^{";
		while (!parseArray[ipos].equals("КОНЕЦ")) {
			step = step + GlobalExpr(parseArray[ipos]);
			//Проверка ошибок
			if (ipos == parseArray.length - 1) {
				System.out.println("Не обозначен конец Степени");
				System.exit(0);
			}
			ipos++;
		}
		ipos++; //WARNING! Аккуратней с падежами
		step = step + "}";
		return step;
	}*/

    //Модуль в строке
	/*public static String StrModule() {
		ipos++;
		String mod = "\\left |";
		while (!parseArray[ipos].equals("КОНЕЦ")) {
			mod = mod + GlobalExpr(parseArray[ipos]);
			//Проверка ошибок
			if (ipos == parseArray.length - 1) {
				System.out.println("Не обозначен конец модуля");
				System.exit(0);
			}
			ipos++;
		}
		ipos++; //WARNING! Аккуратней с падежами
		mod = mod + "\\right |";
		return mod;
	}*/

    //Множество в строке
	/*public static String StrMnoj() {
		ipos++;
		String mnoj = "\\{";
		while (!parseArray[ipos].equals("КОНЕЦ")) {
			mnoj = mnoj + GlobalExpr(parseArray[ipos]);
			//Проверка ошибок
			if (ipos == parseArray.length - 1) {
				System.out.println("Не обозначен конец множества");
				System.exit(0);
			}
			ipos++;
		}
		ipos++; //WARNING! Аккуратней с падежами
		mnoj = mnoj + "\\}";
		return mnoj;
	}*/

    //ПО... ОТ... ДО...?
    public static String StrSumProd(String f) {
        ipos++;
        String leftSide = f + "^{";
        String middle = "";
        String rightSide = "";
        while (!parseArray[ipos].equals("ОТ")) {
            rightSide = rightSide + GlobalExpr(parseArray[ipos]);
            //Проверка ошибок
            if (ipos == parseArray.length - 1) {
                System.out.println("Не обозначен конец выражения");
                System.exit(0);
            }
            ipos++;
        }
        ipos++;
        while (!parseArray[ipos].equals("ДО")) {
            middle = middle + GlobalExpr(parseArray[ipos]);
            //Проверка ошибок
            if (ipos == parseArray.length - 1) {
                System.out.println("Не обозначен конец выражения");
                System.exit(0);
            }
            ipos++;
        }
        ipos++;
        leftSide = leftSide + GlobalExpr(parseArray[ipos]) + "}_{" + middle + "} {" + rightSide + "}";
        return leftSide;
    }

    public static String StrInteg() {
        ipos++;
        String integ = "\\int ";
        if (parseArray[ipos].equals("ОТ")) {
            ipos++;
            integ = integ + "_" + GlobalExpr(parseArray[ipos]);
            ipos++;
            if (parseArray[ipos].equals("ДО")) {
                ipos++;
                integ = integ + "^" + GlobalExpr(parseArray[ipos]);
            }
            ipos++;
        }
        while (!parseArray[ipos].equals("КОНЕЦ")) {
            integ = integ + GlobalExpr(parseArray[ipos]);
            //Проверка ошибок
            if (ipos == parseArray.length - 1) {
                System.out.println("Не обозначен конец выражения");
                System.exit(0);
            }
            ipos++;
        }
        return integ;
    }


    //Тригонометрия в строке
    public static String StrTrig(String trig) {
        ipos++;
        System.out.println(parseArray[ipos]);
        trig = trig + "(";
        if (parseArray[ipos].equals("УГЛА")) {
            ipos = ipos + 1;
            trig = trig + GlobalExpr(parseArray[ipos]) + ")";
            return trig;
        } else {
            while (!parseArray[ipos].equals("КОНЕЦ")) {
                trig = trig + GlobalExpr(parseArray[ipos]);
                //Проверка ошибок
                if (ipos == parseArray.length - 1) {
                    System.out.println("Не обозначен конец выражения");
                    System.exit(0);
                }
                ipos++;
            }
            ipos++; //WARNING! Аккуратней с падежами
            trig = trig + ")";
            return trig;
        }
    }

    //Дробь в строке
    public static String StrFrac() {
        ipos++;
        String frac = "\\frac{";
        //Числитель
        while (!parseArray[ipos].equals("КОНЕЦ")) {
            frac = frac + GlobalExpr(parseArray[ipos]);
            //Проверка ошибок
            if (ipos == parseArray.length - 1) {
                System.out.println("Не обозначен конец выражения");
                System.exit(0);
            }
            ipos++;
        }
        ipos++; //WARNING! Аккуратней с падежами
        frac = frac + "}{";
        //Знаменатель
        while (!parseArray[ipos].equals("КОНЕЦ")) {
            frac = frac + GlobalExpr(parseArray[ipos]);
            //Проверка ошибок
            if (ipos == parseArray.length - 1) {
                System.out.println("Не обозначен конец выражения");
                System.exit(0);
            }
            ipos++;
        }
        frac = frac + "}";
        return frac;
    }

    //Предел в строке
    public static String StrLim() {
        ipos++;
        String leftSide = "\\lim_{";
        String rightSide = "";
        while (!parseArray[ipos].equals("ПРИ")) {
            rightSide = rightSide + GlobalExpr(parseArray[ipos]);
            //Проверка ошибок
            if (ipos == parseArray.length - 1) {
                System.out.println("Не обозначен конец выражения");
                System.exit(0);
            }
            ipos++;
        }
        ipos++;
        leftSide = leftSide + GlobalExpr(parseArray[ipos]);
        while (!parseArray[ipos].equals("К")) {
            //Проверка ошибок
            if (ipos == parseArray.length - 1) {
                System.out.println("Не обозначен конец выражения");
                System.exit(0);
            }
            ipos++;
        }
        leftSide = leftSide + "\\to ";
        ipos++;
        leftSide = leftSide + GlobalExpr(parseArray[ipos]) + "}" + rightSide;
        return leftSide;
    }

    //Нода для Предела
    public static String NodeLim(NodeList eqProps) {
        String to = "";
        String func = "";
        String var = "";
        for (int j = 0; j < eqProps.getLength(); j++) {
            Node eqProp = eqProps.item(j);
            // Если нода не текст, то это один из параметров
            if (eqProp.getNodeType() != Node.TEXT_NODE) {
                if (eqProp.getNodeName().equals("Function")) {
                    strtoparse = eqProp.getAttributes().getNamedItem("val").getNodeValue();
                    parseArray = strtoparse.split("[ ,.;:]");
                    typeArray = new String[parseArray.length];
                    Arrays.fill(typeArray, "");
                    ipos = 0;
                    while (ipos < parseArray.length) {
                        func = func + GlobalExpr(parseArray[ipos]);
                        ipos++;
                    }
                } else if (eqProp.getNodeName().equals("Variable")) {
                    strtoparse = eqProp.getAttributes().getNamedItem("val").getNodeValue();
                    parseArray = strtoparse.split("[ ,.;:]");
                    typeArray = new String[parseArray.length];
                    Arrays.fill(typeArray, "");
                    ipos = 0;
                    while (ipos < parseArray.length) {
                        var = var + GlobalExpr(parseArray[ipos]);
                        ipos++;
                    }
                } else if (eqProp.getNodeName().equals("To")) {
                    strtoparse = eqProp.getAttributes().getNamedItem("val").getNodeValue();
                    parseArray = strtoparse.split("[ ,.;:]");
                    typeArray = new String[parseArray.length];
                    Arrays.fill(typeArray, "");
                    ipos = 0;
                    while (ipos < parseArray.length) {
                        to = to + GlobalExpr(parseArray[ipos]);
                        ipos++;
                    }
                }
            }
        }
        String str = "\\lim_{" + var + "\\to " + to + "}" + func;
        return str;
    }

    //Нода для дроби
    public static String NodeFrac(NodeList eqProps) {
        String up = "";
        String down = "";
        for (int j = 0; j < eqProps.getLength(); j++) {
            Node eqProp = eqProps.item(j);
            // Если нода не текст, то это один из параметров
            if (eqProp.getNodeType() != Node.TEXT_NODE) {
                if (eqProp.getNodeName().equals("Chislitel")) {
                    strtoparse = eqProp.getAttributes().getNamedItem("val").getNodeValue();//Значени в val
                    System.out.println(strtoparse);
                    up = TomitaRun(strtoparse + ".");
                    /*TomitaParser tp2 = new TomitaParser();
                    LinkedList<String> strtp = new LinkedList();
                    strtp.add(strtoparse);
                    try {
                        Document doc = tp2.run(strtp.toArray(new String[strtp.size()]));
                        Node r = doc.getDocumentElement();
                        System.out.println(r.getNodeName());
                    } catch (IOException ex) {
                        ex.printStackTrace(System.out);
                    }
                    parseArray = strtoparse.split("[ ,.;:]");//Получаем массив слов из строки
                    typeArray = new String[parseArray.length];
                    for (int i = 0; i < typeArray.length; i++) {
                        typeArray[i] = "";
                    }
                    ipos = 0;
                    //Запускаем разбор слов
                    while (ipos < parseArray.length) {
                        up = up + GlobalExpr(parseArray[ipos]);
                        ipos++;
                    }*/
                } else if (eqProp.getNodeName().equals("Znamenatel")) {
                    strtoparse = eqProp.getAttributes().getNamedItem("val").getNodeValue();
                    parseArray = strtoparse.split("[ ,.;:]");
                    typeArray = new String[parseArray.length];
                    Arrays.fill(typeArray, "");
                    ipos = 0;
                    while (ipos < parseArray.length) {
                        down = down + GlobalExpr(parseArray[ipos]);
                        ipos++;
                    }
                }
            }
        }
        String str = "\\frac{" + up + "} {" + down + "}";
        return str;
    }

    public static String NodeSqrt(NodeList eqProps)
    {
        String str = "";
        String Recursion = eqProps.item(0).getAttributes().getNamedItem("val").getNodeValue();
        String Value = eqProps.item(1).getAttributes().getNamedItem("val").getNodeValue();
        if (Recursion.equals(Value)) {
            str = "\\sqrt{" + GlobalExpr(Value) + "}";
        }
        else {
            str = "\\sqrt{" + TomitaRun(Recursion + ".") + "}";
        }
        return str;
    }



    public static String GlobalCase(Node eq) {
        NodeList eqProps = eq.getChildNodes(); //Подэлементы - параметры выражения
        //В зависимости от верхнего узла парсинга - корня грамматики
        switch(eq.getNodeName()) {
            case "Predel":
                return NodeLim(eqProps);
            case "Fraction":
                return NodeFrac(eqProps);
            case "Sqrt":
                return NodeSqrt(eqProps);
            default:
                return "notFound";
        }
    }

    public static String TomitaRun(String str) {
        TomitaParser tp = new TomitaParser();
        LinkedList<String> strtp = new LinkedList();
        strtp.add(str);
        String ret = "";
        try {
            Document document = tp.run(strtp.toArray(new String[strtp.size()]));
            Node root = document.getDocumentElement();
            System.out.println(root.getNodeName());
            // Просматриваем все подэлементы корневого
            NodeList docs = root.getChildNodes();
            for (int i = 0; i < docs.getLength(); i++) {
                Node doc = docs.item(i);
                System.out.println(doc.getNodeName());
                // Если нода не текст - заходим внутрь
                if (doc.getNodeType() != Node.TEXT_NODE) {
                    NodeList facts = doc.getChildNodes();
                    for (int j = 0; j < facts.getLength(); j++) {
                        Node fact = facts.item(j);
                        // Если нода не текст - заходим внутрь
                        if (fact.getNodeType() != Node.TEXT_NODE && !fact.getNodeName().equals("Leads")) {
                            NodeList eqs = fact.getChildNodes();
                            Node eq = eqs.item(0); //Берём вершину парсинга
                            // Если нода не текст - вызываем
                            if (eq.getNodeType() != Node.TEXT_NODE) {
                                System.out.println(eq.getNodeName());
                                ret = GlobalCase(eq); //Вызываем глобальное выражение
                            }
                        }
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }
        return ret;
    }

    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");

        try {

            TomitaParser tp = new TomitaParser();
            List<String> test = new LinkedList();
            BufferedReader br = new BufferedReader(new FileReader("test.txt"));
            try {
                String str = null;
                while ((str = br.readLine()) != null) {
                    test.add(str);
                }
            }
            finally {
                br.close();
            }
            // Создается дерево DOM документа из файла
            Document document = tp.run(test.toArray(new String[test.size()]));

            //файл, который хранит свойства нашего проекта
            File file = new File("data.properties");

            //создаем объект Properties и загружаем в него данные из файла.
            Properties properties = new Properties();
            properties.load(new FileReader(file));

            //получаем значения свойств из объекта Properties
            String tex = properties.getProperty("texfile");

            //Выходной файл
            BufferedWriter writer = new BufferedWriter(new FileWriter(tex));
            //Начало тех документа
            writer.write("\\documentclass{article}\n");
            writer.write("\\begin{document}\n");
            // Получаем корневой элемент
            Node root = document.getDocumentElement();
            System.out.println("Mathematical equasion:");
            System.out.println();
            // Просматриваем все подэлементы корневого
            NodeList docs = root.getChildNodes();
            for (int i = 0; i < docs.getLength(); i++) {
                Node doc = docs.item(i);
                // Если нода не текст - заходим внутрь
                if (doc.getNodeType() != Node.TEXT_NODE) {
                    NodeList facts = doc.getChildNodes();
                    for (int j = 0; j < facts.getLength(); j++) {
                        Node fact = facts.item(j);
                        // Если нода не текст - заходим внутрь
                        if (fact.getNodeType() != Node.TEXT_NODE && !fact.getNodeName().equals("Leads")) {
                            NodeList eqs = fact.getChildNodes();
                            Node eq = eqs.item(0); //Берём вершину парсинга
                            // Если нода не текст - вызываем
                            if (eq.getNodeType() != Node.TEXT_NODE) {
                                System.out.println(eq.getNodeName());
                                writer.write("$$" + GlobalCase(eq) + "$$\n"); //Вызываем глобальное выражение
                            }
                        }
                    }
                }
            }
            writer.write("\\end{document}\n");
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }
    }
}