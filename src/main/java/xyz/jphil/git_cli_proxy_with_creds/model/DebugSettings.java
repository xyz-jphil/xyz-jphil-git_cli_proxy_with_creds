package xyz.jphil.git_cli_proxy_with_creds.model;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class DebugSettings {

    @XmlElement(name = "showAccountSelectionInfo")
    private ShowAccountSelectionInfo showAccountSelectionInfo;

    @XmlElement(name = "logs")
    private Logs logs;

    public ShowAccountSelectionInfo getShowAccountSelectionInfo() {
        return showAccountSelectionInfo != null ? showAccountSelectionInfo : new ShowAccountSelectionInfo();
    }

    public void setShowAccountSelectionInfo(ShowAccountSelectionInfo showAccountSelectionInfo) {
        this.showAccountSelectionInfo = showAccountSelectionInfo;
    }

    public Logs getLogs() {
        return logs != null ? logs : new Logs();
    }

    public void setLogs(Logs logs) {
        this.logs = logs;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ShowAccountSelectionInfo {
        @XmlAttribute(name = "accountIdentified")
        private String accountIdentified = "none";

        @XmlAttribute(name = "default")
        private String defaultMode = "none";

        @XmlAttribute(name = "noArgsInvocation")
        private String noArgsInvocation = "none";

        public OutputMode getAccountIdentified() {
            return OutputMode.fromString(accountIdentified);
        }

        public void setAccountIdentified(String accountIdentified) {
            this.accountIdentified = accountIdentified;
        }

        public OutputMode getDefaultMode() {
            return OutputMode.fromString(defaultMode);
        }

        public void setDefaultMode(String defaultMode) {
            this.defaultMode = defaultMode;
        }

        public OutputMode getNoArgsInvocation() {
            return OutputMode.fromString(noArgsInvocation);
        }

        public void setNoArgsInvocation(String noArgsInvocation) {
            this.noArgsInvocation = noArgsInvocation;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Logs {
        @XmlAttribute(name = "enabled")
        private boolean enabled = false;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
