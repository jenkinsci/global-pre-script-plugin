package com.orekaria.jenkins.plugins.globalprescript;

import hudson.Extension;
import hudson.util.FormValidation;
import org.apache.commons.lang.StringUtils;
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

    private String scriptContent;

    /**
     * @return the currently configured scriptContent, if any
     */
    public String getScriptContent() {
        return scriptContent;
    }

    /**
     * Together with {@link #getScriptContent}, binds to entry in {@code config.jelly}.
     *
     * @param scriptContent the new value of this field
     */
    @DataBoundSetter
    public void setScriptContent(String scriptContent) {
        this.scriptContent = scriptContent;
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