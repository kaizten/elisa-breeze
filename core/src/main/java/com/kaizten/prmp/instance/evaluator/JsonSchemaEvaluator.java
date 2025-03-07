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
        ValidationErrors errors = super.validate(instanceFile);
        return errors;
    }

    @Override
    public ValidationErrors validate(URI instanceURL) {
        ValidationErrors errors = super.validate(instanceURL);
        return errors;
    }

    @Override
    public ValidationErrors validate(String instanceString) {
        ValidationErrors errors = super.validate(instanceString);
        return errors;
    }
}