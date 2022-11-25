package io.github.dungla.engine.tokenization;

import io.github.dungla.engine.executor.RuleScenario;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Initialization {

    private static final String IMPORT_STATEMENT = "import";
    private static final String DEFINE_STATEMENT = "define";

    private static final String BEGIN_RULE = "rule";
    private static final String END_RULE = "end";
    private static final String WHEN = "when";
    private static final String THEN = "then";

    public List<String> splitOut(List<String> listRule) {
        Iterator<String> iterator = listRule.iterator();
        List<String> list = new ArrayList<>();
        while (iterator.hasNext()) {
            String line = iterator.next().trim();
            if(line.contains(BEGIN_RULE) && BEGIN_RULE.equals(line.substring(0,4))) {
                list.add(line);
                iterator.remove();
                continue;
            }
            if(line.contains(END_RULE) && END_RULE.equals(line.substring(0,3))) {
                list.add(line);
                iterator.remove();
                break;
            } else {
                list.add(line);
                iterator.remove();
            }
        }
        return list;
    }

    public RuleScenario ruleMaker(List<String> fuels) {

        Iterator<String> iterator = fuels.iterator();

        RuleScenario ruleScenario = new RuleScenario();
        // -1: nothing, 0 = when, 1 = then
        int mode = -1;
        while(iterator.hasNext()) {
            String line = iterator.next().replace(";", "").replace("\"","");
            if(line.contains(BEGIN_RULE) && BEGIN_RULE.equals(line.substring(0,4))) {
                ruleScenario.setName(line.replace(BEGIN_RULE,"").replace("\"","").trim());
            }
            if(line.contains(WHEN)) {
                mode = 0;
                continue;
            }
            if(line.contains(THEN)){
                mode = 1;
                continue;
            }
            if(line.contains(END_RULE) && END_RULE.equals(line.substring(0,3))) break;

            if(mode == 0) {
                if(line.contains("(")) {
                    String[] args = line.substring(line.indexOf(")")+1).trim().split(" ");
                    ruleScenario.addRule(line.substring(0,line.indexOf(")")+1),
                            CompareOperator.operator(args[0]),
                            args[1]);
                }else {
                    String[] args = line.split(" ");
                    ruleScenario.addRule(args[0], CompareOperator.operator(args[1]), args[2]);
                }
            }
            if(mode == 1) {
                String[] args = line.split("=");
                ruleScenario.AddModifyWhenTrue(args[0].trim(), args[1].trim());
            }
        }

        return ruleScenario;
    }

    public Map<String, String> definedStatement(List<String> list) throws Exception {
        Map<String, String> definedMap = new HashMap<>();
        Iterator<String> iterable = list.iterator();
        while (iterable.hasNext()) {
            String line = iterable.next();
            if (line.contains(DEFINE_STATEMENT) && DEFINE_STATEMENT.equals(line.substring(0,6))) {
                String[] word = line.replace(";","").split(" ");
                if(definedMap.containsKey(word[2]) || definedMap.containsValue(word[1])) {
                    throw new Exception("duplicate define statement");
                }
                definedMap.put(word[2], word[1]);
                iterable.remove();
            }
        }
        return definedMap;
    }

    public List<String> importedStatement(List<String> list) throws Exception {
        List<String> importList = new ArrayList<>();
        Iterator<String> iterable = list.iterator();
        while (iterable.hasNext()) {
            String line = iterable.next();
            if (line.contains(IMPORT_STATEMENT) && IMPORT_STATEMENT.equals(line.substring(0,6))) {
                String[] word = line.replace(";","").split(" ");
                if(importList.contains(word[1])) {
                    throw new Exception("duplicate import statement");
                }
                importList.add(word[1]);
                iterable.remove();
            }
        }
        return importList;
    }

    public ArrayList<String> tokenization(String fileName) {
        String path = Objects.requireNonNull(Initialization.class.getClassLoader().getResource(fileName)).getPath().replace("%20"," ");
        ArrayList<String> list = null;
        try (BufferedReader br
                     = new BufferedReader(new FileReader(path))) {
            list = new ArrayList<>();
            String line;
            while ((line=br.readLine()) != null){
                if(!line.equals("")) list.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
