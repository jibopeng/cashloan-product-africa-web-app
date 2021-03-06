package com.ajaya.cashloan.rule.model.srule.config.builder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.ajaya.cashloan.rule.model.srule.config.condition.AbstractCondition;
import com.ajaya.cashloan.rule.model.srule.config.condition.Condition;
import com.ajaya.cashloan.rule.model.srule.config.condition.ConditionItem;
import com.ajaya.cashloan.rule.model.srule.config.rule.Rule;
import com.ajaya.cashloan.rule.model.srule.config.rule.RuleBasic;
import com.ajaya.cashloan.rule.model.srule.exception.RuleValueException;
import com.ajaya.cashloan.rule.model.srule.utils.ConditionOpt;

/**
 * Created by syq on 2016/12/17.
 */
public abstract class SimpleRuleBuilder<H, T extends Rule> extends AbstractRuleBuilder<H, T> {


    private Class<H> oClazz;


    private RuleConfigurerAdapter<H, T, SimpleRuleBuilder<H, T>> ruleConfigurer;


    @SuppressWarnings("All")
    public SimpleRuleBuilder() {
        super();
        Type type = getClass().getGenericSuperclass();
        this.oClazz = (Class<H>) ((ParameterizedType) type).getActualTypeArguments()[0];
        this.ruleConfigurer = new SimpleRuleConfigurer<H, T, SimpleRuleBuilder<H, T>>();
        this.ruleConfigurer.setBuilder(this);
    }


    @Override
    @SuppressWarnings("All")
    protected T doBuild() throws Exception {
        RuleBasic ruleBasic = threadLocalRules.get();
        return (T) ruleBasic;
    }


    @Override
    public ConditionItem newConditionItems() {
        return new ConditionItem();
    }

    @Override
    public RuleConfigurer<H> newRule(long id, String column, ConditionItem conditionItem) throws IllegalAccessException
            , InstantiationException {
        RuleBasic<H> ruleBasic = concrete();
        ruleBasic.setColumn(column);
        ruleBasic.setId(id);
        ruleBasic.setConditions(itemTolist(conditionItem));
        ruleBasic.setValueType(oClazz);
        threadLocalRules.set(ruleBasic);
        return ruleConfigurer;
    }


    @SuppressWarnings("All")
    private List<Condition<H>> itemTolist(ConditionItem conditionItem) throws RuleValueException {
        List<Condition<H>> conditions = new ArrayList<Condition<H>>();
        while (conditionItem.hasNext()) {
            Object[] objs = conditionItem.next();
            AbstractCondition<H> condition = new AbstractCondition<H>();
            condition.opt(ConditionOpt.getOpt((String) objs[0]));
            if(objs[1] instanceof Long || objs[1] instanceof Integer || objs[1] instanceof Float){
                objs[1] = Double.valueOf(objs[1].toString());
            }
            if (objs[1].getClass() != oClazz) {
                throw new RuleValueException("item value :" + objs[1] + ",is not match to the type of " + oClazz.getName());
            }
            condition.value((H) objs[1]);
            conditions.add(condition);
        }
        return conditions;
    }
}


