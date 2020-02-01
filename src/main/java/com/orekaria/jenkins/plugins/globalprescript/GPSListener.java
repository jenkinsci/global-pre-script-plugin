package com.orekaria.jenkins.plugins.globalprescript;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.matrix.MatrixRun;
import hudson.model.*;
import hudson.model.listeners.RunListener;
import org.jenkinsci.plugins.scriptsecurity.sandbox.groovy.SecureGroovyScript;
import org.jenkinsci.plugins.scriptsecurity.scripts.ApprovalContext;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Orekaria
 */
@Extension
public class GPSListener extends RunListener<Run> implements Serializable {

    @Override
    public Environment setUpEnvironment(@Nonnull AbstractBuild build, @Nonnull Launcher launcher,
                                        @Nonnull BuildListener listener) throws IOException, InterruptedException {
        PrintStream logger = listener.getLogger();

        if (!isEligibleJobType(build)) {
            return new Environment() {
            };
        }

        Map<String, String> envVars = build.getBuildVariables();
        GPSEnvVars envVarsHelper = new GPSEnvVars();
        String globalGroovyScriptContent = GPSGlobalConfiguration.get().getScriptContent();

        try {
            //Evaluate Groovy script
            SecureGroovyScript secureGlobalGroovyScriptContent = new SecureGroovyScript(globalGroovyScriptContent, false, null).configuring(ApprovalContext.create());
            final Map<String, String> groovyMapEnvVars = envVarsHelper.executeGroovyScript(logger, listener, secureGlobalGroovyScriptContent, envVars);

            logger.println("Global Groovy script: injecting variables");
            return new Environment() {
                @Override
                public void buildEnvVars(Map<String, String> env) {
                    env.putAll(groovyMapEnvVars);
                }
            };
        } catch (Exception e) {
            logger.println(e.getMessage());
            return new Environment() {
            };
        }
    }

    private boolean isEligibleJobType(@Nonnull Run<?, ?> build) {
        final Job job;
        if (build instanceof MatrixRun) {
            job = ((MatrixRun) build).getParentBuild().getParent();
        } else {
            job = build.getParent();
        }

        return job instanceof BuildableItemWithBuildWrappers;

    }

    @CheckForNull
    private Node getNode() {
        Computer computer = Computer.currentComputer();
        if (computer == null) {
            return null;
        }
        return computer.getNode();
    }

    @CheckForNull
    private FilePath getNodeRootPath() {
        Node node = getNode();
        if (node != null) {
            return node.getRootPath();
        }
        return null;
    }

}
