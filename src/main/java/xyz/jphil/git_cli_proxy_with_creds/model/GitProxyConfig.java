package xyz.jphil.git_cli_proxy_with_creds.model;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "gitProxy")
@XmlAccessorType(XmlAccessType.FIELD)
public class GitProxyConfig {

    @XmlElement(name = "binaries", required = true)
    private Binaries binaries;

    @XmlElement(name = "accounts", required = true)
    private Accounts accounts;

    @XmlElement(name = "debugSettings")
    private DebugSettings debugSettings;

    public Binaries getBinaries() {
        return binaries;
    }

    public void setBinaries(Binaries binaries) {
        this.binaries = binaries;
    }

    public Accounts getAccounts() {
        return accounts;
    }

    public void setAccounts(Accounts accounts) {
        this.accounts = accounts;
    }

    public DebugSettings getDebugSettings() {
        return debugSettings != null ? debugSettings : new DebugSettings();
    }

    public void setDebugSettings(DebugSettings debugSettings) {
        this.debugSettings = debugSettings;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Binaries {
        @XmlElement(name = "git", required = true)
        private BinaryPath git;

        @XmlElement(name = "gh", required = true)
        private BinaryPath gh;

        public BinaryPath getGit() {
            return git;
        }

        public void setGit(BinaryPath git) {
            this.git = git;
        }

        public BinaryPath getGh() {
            return gh;
        }

        public void setGh(BinaryPath gh) {
            this.gh = gh;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class BinaryPath {
        @XmlAttribute(name = "path", required = true)
        private String path;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Accounts {
        @XmlElement(name = "account")
        private List<Account> accountList = new ArrayList<>();

        public List<Account> getAccountList() {
            return accountList;
        }

        public void setAccountList(List<Account> accountList) {
            this.accountList = accountList;
        }
    }
}
