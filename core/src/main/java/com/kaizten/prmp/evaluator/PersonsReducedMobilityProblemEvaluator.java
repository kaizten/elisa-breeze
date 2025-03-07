package com.kaizten.prmp.evaluator;

import com.kaizten.opt.evaluator.EvaluatorSingleObjectiveFunction;
import com.kaizten.prmp.domain.solution.PersonsReducedMobilitySolution;

public class PersonsReducedMobilityProblemEvaluator extends EvaluatorSingleObjectiveFunction<PersonsReducedMobilitySolution> {

    public PersonsReducedMobilityProblemEvaluator() {
        super();
    }

    @Override
    public void evaluate(PersonsReducedMobilitySolution solution) {
        super.setObjectiveFunctionValue(0);
    }

    @Override
    public void fillSolution(PersonsReducedMobilitySolution solution) {
    }
}
