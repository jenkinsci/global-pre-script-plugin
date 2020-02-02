package com.orekaria.jenkins.plugins.globalprescript;

import hudson.Extension;
import hudson.util.FormValidation;
import org.jenkinsci.plugins.scriptsecurity.sandbox.groovy.SecureGroovyScript;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

/**
 * @author Orekaria
 * Add global configuration options
 */
@Extension
public class GPSGlobalConfiguration extends jenkins.model.GlobalConfiguration {

    /**
     * @return the singleton instance
     */
    public static GPSGlobalConfiguration get() {
        return jenkins.model.GlobalConfiguration.all().get(GPSGlobalConfiguration.class);
    }

    public GPSGlobalConfiguration() {
        // When Jenkins is restarted, load any saved configuration from disk.
        load();
    }

    private SecureGroovyScript secureGroovyScript;

    /**
     * @return the currently configured scriptContent, if any
     */
    public SecureGroovyScript getSecureGroovyScript() {
        return secureGroovyScript;
    }

    /**
     * Together with {@link #getSecureGroovyScript}, binds to entry in {@code config.jelly}.
     *
     * @param secureGroovyScript the new value of this field
     */
    @DataBoundSetter
    public void setSecureGroovyScript(SecureGroovyScript secureGroovyScript) {
        this.secureGroovyScript = secureGroovyScript;
        save();
    }

    public FormValidation doCheckScriptContent(@QueryParameter String value) {
        // TODO: check if the script is valid
        // if (StringUtils.isEmpty(value)) {
        //     return FormValidation.warning("Please specify a script.");
        // }
        return FormValidation.ok();
    }
}