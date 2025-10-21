package xyz.jphil.git_cli_proxy_with_creds;

import xyz.jphil.git_cli_proxy_with_creds.model.GitProxyConfig;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigLoader {

    private static final String DEFAULT_CONFIG_PATH =
        System.getProperty("user.home") + File.separator + "xyz-jphil" +
        File.separator + "git_cli_proxy_with_creds" + File.separator + "settings.xml";

    /**
     * Loads configuration from default location or from system property override.
     * System property: git.proxy.config
     */
    public static GitProxyConfig load() throws ConfigLoadException {
        String configPath = System.getProperty("git.proxy.config", DEFAULT_CONFIG_PATH);
        return load(Paths.get(configPath));
    }

    /**
     * Loads configuration from specified path.
     */
    public static GitProxyConfig load(Path configPath) throws ConfigLoadException {
        File configFile = configPath.toFile();

        if (!configFile.exists()) {
            throw new ConfigLoadException("Configuration file not found: " + configPath);
        }

        if (!configFile.canRead()) {
            throw new ConfigLoadException("Cannot read configuration file: " + configPath);
        }

        try {
            JAXBContext context = JAXBContext.newInstance(GitProxyConfig.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            GitProxyConfig config = (GitProxyConfig) unmarshaller.unmarshal(configFile);

            // Expand environment variables in paths
            expandEnvironmentVariables(config);

            return config;
        } catch (JAXBException e) {
            throw new ConfigLoadException("Failed to parse configuration file: " + e.getMessage(), e);
        }
    }

    /**
     * Expands Windows environment variables in the format %VAR% in all paths.
     */
    private static void expandEnvironmentVariables(GitProxyConfig config) {
        if (config.getBinaries() != null) {
            if (config.getBinaries().getGit() != null) {
                String expanded = expandEnvVars(config.getBinaries().getGit().getPath());
                config.getBinaries().getGit().setPath(expanded);
            }
            if (config.getBinaries().getGh() != null) {
                String expanded = expandEnvVars(config.getBinaries().getGh().getPath());
                config.getBinaries().getGh().setPath(expanded);
            }
        }

        // Expand in account paths
        if (config.getAccounts() != null) {
            config.getAccounts().getAccountList().forEach(account -> {
                if (account.getGitCredentials() != null) {
                    String expanded = expandEnvVars(account.getGitCredentials().getSshKey());
                    account.getGitCredentials().setSshKey(expanded);
                }
            });
        }
    }

    /**
     * Expands environment variables in format %VAR% (Windows style).
     */
    private static String expandEnvVars(String path) {
        if (path == null) {
            return null;
        }

        String result = path;

        // Match %VAR% pattern
        int start = result.indexOf('%');
        while (start != -1) {
            int end = result.indexOf('%', start + 1);
            if (end == -1) {
                break; // No closing %
            }

            String varName = result.substring(start + 1, end);
            String varValue = System.getenv(varName);

            if (varValue != null) {
                result = result.substring(0, start) + varValue + result.substring(end + 1);
                start = result.indexOf('%', start + varValue.length());
            } else {
                start = result.indexOf('%', end + 1);
            }
        }

        return result;
    }

    public static class ConfigLoadException extends Exception {
        public ConfigLoadException(String message) {
            super(message);
        }

        public ConfigLoadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
