package com.kaizten.prmp.solver.cli;

import com.beust.jcommander.Parameter;

public class Arguments {

    @Parameter(names = {
            "-h", "--help"
    }, description = "Prints this help and exists", help = true)
    public boolean help = false;
    @Parameter(names = {
            "-i", "--instance"
    }, description = "URI of the problem instance", required = true)
    public String instance;

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("help=").append(this.help);
        stringBuilder.append(",");
        stringBuilder.append("instance=").append(this.instance);
        return stringBuilder.toString();
    }
}