package com.orekaria.jenkins.plugins.globalprescript;

import hudson.Extension;
import hudson.Launcher;
import hudson.matrix.MatrixRun;
import hudson.model.*;
import hudson.model.listeners.RunListener;
import org.jenkinsci.plugins.scriptsecurity.sandbox.groovy.SecureGroovyScript;
import org.jenkinsci.plugins.scriptsecurity.scripts.ApprovalContext;

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
    public static final String APP_NAME = "Global Pre Groovy script";

    @Override
    public Environment setUpEnvironment(@Nonnull AbstractBuild build, @Nonnull Launcher launcher,
                                        @Nonnull BuildListener listener) throws IOException, InterruptedException {
        PrintStream logger = listener.getLogger();

        if (!isEligibleJobType(build)) {
            return new Environment() {
            };
        }

        Map<String, String> envVars = new LinkedHashMap<String, String>();
        envVars.putAll(build.getBuildVariables());
        envVars.putAll(build.getCharacteristicEnvVars());

        try {
            // execute Groovy script
            SecureGroovyScript secureGroovyScript = GPSGlobalConfiguration.get().getSecureGroovyScript();
            SecureGroovyScript secureGlobalGroovyScriptContent = secureGroovyScript.configuring(ApprovalContext.create());
            GPSEnvVars envVarsHelper = new GPSEnvVars();
            final Map<String, String> groovyMapEnvVars = envVarsHelper.executeGroovyScript(logger, listener, secureGlobalGroovyScriptContent, envVars);

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
}
