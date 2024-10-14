package org.example;

import java.util.*;

/**
 * 简易计算器，实现加减乘除运算，并可以进行undo和redo操作（撤销和重做）
 */
public class Calculator {

    private static final String CALCULATE_DATA_ZERO = "0";
    private static final String CALCULATOR_UNDO = "U";
    private static final String CALCULATOR_REDO = "R";
    private static final String CALCULATOR_ADD = "+";
    private static final String CALCULATOR_SUBTRACT = "-";
    private static final String CALCULATOR_MULTIPLY = "*";
    private static final String CALCULATOR_DIVIDE = "/";
    private static final String CALCULATOR_EQUAL = "=";
    private static final String NUMBER_REGEX = "^(-?\\d+(\\.\\d+)?)$";
    private static final String CONSTANT_REGEX_PREFIX = "REGEX_";

    public static void main(String[] args) {
        Set<String> operators = new HashSet<>();
        operators.add(CALCULATOR_ADD);
        operators.add(CALCULATOR_SUBTRACT);
        operators.add(CALCULATOR_MULTIPLY);
        operators.add(CALCULATOR_DIVIDE);
        operators.add(CALCULATOR_EQUAL);

        LinkedList<String> dataList = new LinkedList<>();
        Map<String, LinkedList<String>> dataMap = new HashMap<>();
        Scanner scanner = new Scanner(System.in);
        int i = 1;
        while (true){
            if (i % 2 == 1) {
                System.out.print("请输入数字：");
            } else {
                System.out.print("请输入运算符：");
            }
            String param = scanner.nextLine().trim();
            if(CALCULATOR_UNDO.equals(param)){
                dataList.removeLast();
                i--;
                continue;
            }
            if(CALCULATOR_REDO.equals(param)){
                dataList.clear();
                i = 1;
                continue;
            }
            if(CALCULATOR_EQUAL.equals(param)){
                //输入结束
                break;
            }
            //数字情况
            if (i % 2 == 1) {
                if (!isNumber(param)) {
                    System.out.println("数字输入错误！！！");
                    return;
                }
                if(i > 1) {
                    String opearte = dataList.getLast();
                    if(opearte.startsWith(CONSTANT_REGEX_PREFIX)){
                        LinkedList<String> regexData = dataMap.get(opearte);
                        String regexOperate = regexData.getLast();
                        if(CALCULATOR_DIVIDE.equals(regexOperate) && CALCULATE_DATA_ZERO.equals(param)){
                            System.out.println("数字输入错误！！！");
                            return;
                        }
                        regexData.add(param);
                        i++;
                        continue;
                    }
                    if(CALCULATOR_DIVIDE.equals(opearte) && CALCULATE_DATA_ZERO.equals(param)){
                        System.out.println("数字输入错误！！！");
                        return;
                    }
                }
                dataList.add(param);
                i++;
                continue;
            }
            //操作符情况
            if(!operators.contains(param)) {
                System.out.println("操作符输入错误！！！");
                return;
            }
            if(CALCULATOR_MULTIPLY.equals(param) || CALCULATOR_DIVIDE.equals(param)){
                String value = dataList.getLast();
                if(value.startsWith(CONSTANT_REGEX_PREFIX)){
                    LinkedList<String> regexData = dataMap.get(value);
                    regexData.add(param);
                    i++;
                    continue;
                }
                dataList.removeLast();
                String key = CONSTANT_REGEX_PREFIX + i;
                LinkedList<String> regexData = new LinkedList<>();
                regexData.add(value);
                regexData.add(param);
                dataMap.put(key, regexData);
                dataList.add(key);
                i++;
                continue;
            }

            dataList.add(param);
            i++;
        }
        String parseStr = String.join("", dataList);

        Map<String, Double> map = new HashMap<>();
        if(!dataMap.isEmpty()){
            for(Map.Entry<String, LinkedList<String>> entry : dataMap.entrySet()){
                String key = entry.getKey();
                LinkedList<String> regexData = entry.getValue();
                double value = doCalculate(regexData, null);
                map.put(key, value);
            }
        }
        double result = doCalculate(dataList, map);

        StringBuffer sb = new StringBuffer();
        for(int ix = 0; ix < dataList.size(); ix++){
            String str = dataList.get(ix);
            if(str.startsWith(CONSTANT_REGEX_PREFIX)){
                sb.append(String.join("", dataMap.get(str)));
            }else{
                sb.append(str);
            }
        }
        sb.append("=").append(result);

        System.out.println("结果：" + sb);
    }

    private static double doCalculate(LinkedList<String> dataList, Map<String, Double> map) {
        if(Objects.isNull(dataList) || dataList.isEmpty()){
            return 0;
        }
        String firstValue = dataList.getFirst();
        Double value;
        if(firstValue.startsWith(CONSTANT_REGEX_PREFIX)){
            value = map.get(firstValue);
        }else {
            value = Double.parseDouble(firstValue);
        }
        int idx = 1;
        while (idx < dataList.size()) {
            String str1 = dataList.get(idx++);
            String str2 = dataList.get(idx++);
            Double value2;
            if(str2.startsWith(CONSTANT_REGEX_PREFIX)){
                value2 = map.get(str2);
            }else {
                value2 =  Double.parseDouble(str2);
            }
            if(CALCULATOR_ADD.equals(str1)){
                value += value2;
            }
            if(CALCULATOR_SUBTRACT.equals(str1)){
                value -= value2;
            }
            if(CALCULATOR_MULTIPLY.equals(str1)){
                value *= value2;
            }
            if(CALCULATOR_DIVIDE.equals(str1)){
                value /= value2;
            }
        }
        return value;
    }

    private static boolean isNumber(String value){
        return value.matches(NUMBER_REGEX);
    }

}
