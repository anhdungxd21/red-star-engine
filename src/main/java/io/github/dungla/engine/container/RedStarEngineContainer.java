package io.github.dungla.engine.container;

import io.github.dungla.engine.executor.RuleScenario;
import io.github.dungla.engine.tokenization.Initialization;

import java.io.FileNotFoundException;
import java.util.*;

public class RedStarEngineContainer {
    private static RedStarEngineContainer instance = null;
    private String ruleFilePath = "";
    private Map<String, String> definedMap = new HashMap<>();
    private List<String> importedStatement = new ArrayList<>();
    private List<RuleScenario> ruleScenarioList = new ArrayList<>();

    private RedStarEngineContainer(){
    }

    public static RedStarEngineContainer getInstance() {
        if(instance == null) {
            synchronized(RedStarEngineContainer.class) {
                if(null == instance) {
                    instance = new RedStarEngineContainer();
                }
            }
        }
        return instance;
    }

    public static RedStarEngineContainer getReadyInstance(String ruleFilePath) throws Exception {
        if(instance == null) getInstance();
        instance.setResource(ruleFilePath);
        instance.init();
        return instance;
    }

    public void processRule(Map<String, Object> map) {
        ruleScenarioList.forEach(element ->{
            element.process(definedMap, map);
        });
    }

    public void setResource(String fileName) {
        this.ruleFilePath = Objects.requireNonNull(fileName);
    }

    public void init() throws Exception {
        if(ruleFilePath.equals("") ) {
            throw new FileNotFoundException(RedStarEngineContainer.class + ": file name was not set");
        }
        Initialization init = new Initialization();
        List<String> statementList = Objects.requireNonNull(init.tokenization(ruleFilePath));
        this.definedMap = init.definedStatement(statementList);
        this.importedStatement = init.importedStatement(statementList);
        while (statementList.size() != 0) {
            this.ruleScenarioList.add(init.ruleMaker(init.splitOut(statementList)));
        }
    }
}
