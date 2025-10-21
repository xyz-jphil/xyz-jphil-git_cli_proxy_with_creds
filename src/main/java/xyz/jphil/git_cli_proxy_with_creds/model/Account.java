package xyz.jphil.git_cli_proxy_with_creds.model;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Account {

    @XmlAttribute(name = "id", required = true)
    private String id;

    @XmlAttribute(name = "name", required = true)
    private String name;

    @XmlElement(name = "git", required = true)
    private GitCredentials gitCredentials;

    @XmlElement(name = "gh")
    private GhCredentials ghCredentials;

    @XmlElement(name = "description")
    private String description;

    @XmlElement(name = "activationRules", required = true)
    private ActivationRules activationRules;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GitCredentials getGitCredentials() {
        return gitCredentials;
    }

    public void setGitCredentials(GitCredentials gitCredentials) {
        this.gitCredentials = gitCredentials;
    }

    public GhCredentials getGhCredentials() {
        return ghCredentials;
    }

    public void setGhCredentials(GhCredentials ghCredentials) {
        this.ghCredentials = ghCredentials;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ActivationRules getActivationRules() {
        return activationRules;
    }

    public void setActivationRules(ActivationRules activationRules) {
        this.activationRules = activationRules;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class GitCredentials {
        @XmlAttribute(name = "sshKey", required = true)
        private String sshKey;

        @XmlAttribute(name = "userName", required = true)
        private String userName;

        @XmlAttribute(name = "userEmail", required = true)
        private String userEmail;

        public String getSshKey() {
            return sshKey;
        }

        public void setSshKey(String sshKey) {
            this.sshKey = sshKey;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public void setUserEmail(String userEmail) {
            this.userEmail = userEmail;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class GhCredentials {
        @XmlAttribute(name = "token", required = true)
        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ActivationRules {
        @XmlElement(name = "pathParent")
        private List<PathParent> pathParents = new ArrayList<>();

        @XmlElement(name = "pathRegex")
        private List<PathRegex> pathRegexes = new ArrayList<>();

        public List<PathParent> getPathParents() {
            return pathParents;
        }

        public void setPathParents(List<PathParent> pathParents) {
            this.pathParents = pathParents;
        }

        public List<PathRegex> getPathRegexes() {
            return pathRegexes;
        }

        public void setPathRegexes(List<PathRegex> pathRegexes) {
            this.pathRegexes = pathRegexes;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class PathParent {
        @XmlAttribute(name = "desc")
        private String desc;

        @XmlValue
        private String path;

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class PathRegex {
        @XmlAttribute(name = "desc")
        private String desc;

        @XmlValue
        private String pattern;

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }
    }
}
