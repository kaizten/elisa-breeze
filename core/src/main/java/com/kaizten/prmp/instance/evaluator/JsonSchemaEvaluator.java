package com.kaizten.prmp.instance.evaluator;

import com.kaizten.opt.validation.ValidationErrors;
import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.opt.datasource.validation.ProblemInstanceJsonValidator;

import java.io.File;
import java.net.URI;

import org.json.JSONObject;

public class JsonSchemaEvaluator extends ProblemInstanceJsonValidator<PersonsReducedMobilityProblem> {

    public JsonSchemaEvaluator(JSONObject jsonSchema) {
        super(jsonSchema);
    }

    @Override
    public ValidationErrors validate(File instanceFile) {
        return super.validate(instanceFile);
    }

    @Override
    public ValidationErrors validate(URI instanceURL) {
        return super.validate(instanceURL);
    }

    @Override
    public ValidationErrors validate(String instanceString) {
        return super.validate(instanceString);
    }
}