package io.github.dungla.engine.executor;

import io.github.dungla.engine.tokenization.CompareOperator;
import io.github.dungla.engine.tokenization.MathematicalOperator;
import lombok.Data;
import lombok.ToString;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Data
@ToString
public class RuleScenario {
    private String name;
    private List<Compare> when = new ArrayList<>();
    private Map<String, String> then = new HashMap<>();

    /**
     * Add rule vào object
     * @param left toán tử trái
     * @param operator toán tử so sánh
     * @param right toán tử phải
     */
    public void addRule(String left, CompareOperator operator, String right) {
        when.add(new Compare(left,operator,right));
    }

    /**
     * Chỉnh sửa object đầu ra
     * @param field field cần chỉnh sửa
     * @param value giá trị chỉnh sửa
     */
    public void AddModifyWhenTrue(String field, String value) {
        then.put(field, value);
    }


    /**
     * @return trả lại kết quả true nếu tất cả các điều kiện đúng
     *          và false nếu tất cả các điều kiện sai
     */
    public void process(Map<String, String> definedMap, Map<String, Object> objectMap) {
        if(definedMap.size() < objectMap.size()) {
            throw new IllegalArgumentException("Arguments not match");
        }

        objectMap.forEach((k1, v1) -> {
            if (!definedMap.containsValue(v1.getClass().getName())){
                throw new IllegalArgumentException("Class not match");
            }
        });

        if(check(objectMap)){
            then.forEach((k, v) -> {
                String[] args = k.split("\\.");
                if(objectMap.containsKey(args[0])) {
                    setValue(args[1], objectMap.get(args[0]), v);
                }
            });
        }

    }

    private boolean check(Map<String, Object> map) {
        AtomicBoolean result = new AtomicBoolean(true);
        when.forEach(element -> {
            if(element.left.contains("(") && element.left.contains(")")){
                String[] analyzed = element.left.replace("(", "").replace(")", "").split(" ");
                Double value = calculate(analyzed, map);
                if(!element.compareWithRight(value)){
                    result.set(false);
                }
            } else {
                String[] analyzed = element.left.split("\\.");
                Object obj = map.get(analyzed[0]);
                if(!element.compareWithRight(getValue(analyzed[1], obj))){
                    result.set(false);
                }
            }
        });
        return result.get();
    }

    private Double calculate(String[] strings, Map<String, Object> map){

        Object obj1 = map.get(strings[0].split("\\.")[0]);
        Object obj2 = map.get(strings[2].split("\\.")[0]);
        Object value1 = getValue(strings[0].split("\\.")[1], obj1);
        Object value2 = getValue(strings[2].split("\\.")[1], obj2);
        if(value1 == null || value2 == null) {
            throw new RuntimeException("Compare fail");
        }
        switch (MathematicalOperator.operator(strings[1])) {
            case ADDITION:
                return (Double) value1 + (Double) value2;
            case SUBTRACTION:
                return (Double) value1 - (Double) value2;
            case MULTIPLICATION:
                return (Double) value1 * (Double) value2;
            case DIVISION:
                return (Double) value1 / (Double) value2;
        }
        return null;
    }

    private void setValue(String field, Object object, Object value){
        Field[] fields = object.getClass().getDeclaredFields();
        for(Field f: fields) {
            if (f.getName().equals(field)) {
                f.setAccessible(true);
                try {
                    f.set(object, value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private Object getValue(String field, Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        for(Field f: fields) {
            if (f.getName().equals(field)) {
                f.setAccessible(true);
                try {
                    return f.get(object);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    @Data
    private class Compare {
        private String left;
        private CompareOperator operator;
        private String right;

        private Compare(String left, CompareOperator operator, String right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        private boolean compareWithRight(Object object) {
            switch (operator) {
                case EQUAL:
                    if (isNumber(right)){
                        return (Double) object == Double.parseDouble(right) ;
                    } else {
                        return object.equals(right);
                    }
                case NOT_EQUAL:
                    if (isNumber(right)){
                        return (Double) object != Double.parseDouble(right) ;
                    } else {
                        return !object.equals(right);
                    }
                case LESS_THAN:
                    return (Double) object < Double.parseDouble(right);
                case GREATER_THAN:
                    return (Double) object > Double.parseDouble(right);
                case LESS_THAN_OR_EQUAL:
                    return (Double) object <= Double.parseDouble(right);
                case GREATER_THAN_OR_EQUAL:
                    return (Double) object >= Double.parseDouble(right);
            }
            return true;
        }

        private boolean isNumber(String suspect) {
            if(suspect == null) {
                return false;
            }
            try {
                double d = Double.parseDouble(suspect);
            }catch (NumberFormatException e) {
                return false;
            }
            return true;
        }
    }
}
