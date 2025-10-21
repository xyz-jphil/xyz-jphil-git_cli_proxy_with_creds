package xyz.jphil.git_cli_proxy_with_creds;

import xyz.jphil.git_cli_proxy_with_creds.model.Account;
import xyz.jphil.git_cli_proxy_with_creds.model.GitProxyConfig;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class AccountMatcher {

    private final GitProxyConfig config;

    public AccountMatcher(GitProxyConfig config) {
        this.config = config;
    }

    /**
     * Finds matching account for given directory path.
     * Returns MatchResult with account and match details, or null if no match.
     */
    public MatchResult findMatch(Path currentDir) {
        String normalizedCurrentPath = normalizePath(currentDir.toAbsolutePath().toString());

        for (Account account : config.getAccounts().getAccountList()) {
            // Check pathParent rules first
            for (Account.PathParent pathParent : account.getActivationRules().getPathParents()) {
                String normalizedParent = normalizePath(pathParent.getPath());

                if (isChildOf(normalizedCurrentPath, normalizedParent)) {
                    String displayRule = pathParent.getDesc() != null ?
                        pathParent.getDesc() : pathParent.getPath();
                    return new MatchResult(account, "pathParent", displayRule, pathParent.getPath());
                }
            }

            // Check pathRegex rules
            for (Account.PathRegex pathRegex : account.getActivationRules().getPathRegexes()) {
                Pattern pattern = Pattern.compile(pathRegex.getPattern());

                if (pattern.matcher(normalizedCurrentPath).matches()) {
                    String displayRule = pathRegex.getDesc() != null ?
                        pathRegex.getDesc() : pathRegex.getPattern();
                    return new MatchResult(account, "pathRegex", displayRule, pathRegex.getPattern());
                }
            }
        }

        return null; // No match found
    }

    /**
     * Normalizes path for comparison:
     * - Converts to lowercase (Windows is case-insensitive)
     * - Converts all slashes to backslashes on Windows
     * - Handles /f/, /mnt/f/, f:/ variants
     */
    private String normalizePath(String path) {
        if (path == null) {
            return "";
        }

        String normalized = path.toLowerCase();

        // Convert various slash formats to backslash on Windows
        if (isWindows()) {
            normalized = normalized.replace('/', '\\');

            // Handle Git Bash format: /f/... → f:\...
            if (normalized.matches("^\\\\[a-z]\\\\.*")) {
                normalized = normalized.charAt(1) + ":" + normalized.substring(2);
            }

            // Handle WSL format: /mnt/f/... → f:\...
            if (normalized.startsWith("\\mnt\\")) {
                String rest = normalized.substring(5); // Skip "\mnt\"
                if (rest.length() > 2 && rest.charAt(1) == '\\') {
                    normalized = rest.charAt(0) + ":" + rest.substring(1);
                }
            }
        } else {
            // On Linux/WSL, convert backslashes to forward slashes
            normalized = normalized.replace('\\', '/');
        }

        // Remove trailing slash/backslash
        while (normalized.endsWith("\\") || normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }

        return normalized;
    }

    /**
     * Checks if childPath is a child of or equal to parentPath.
     */
    private boolean isChildOf(String childPath, String parentPath) {
        if (childPath.equals(parentPath)) {
            return true;
        }

        String separator = isWindows() ? "\\" : "/";
        return childPath.startsWith(parentPath + separator);
    }

    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    /**
     * Result of account matching with details for debugging.
     */
    public static class MatchResult {
        private final Account account;
        private final String ruleType;
        private final String displayRule;
        private final String actualPattern;

        public MatchResult(Account account, String ruleType, String displayRule, String actualPattern) {
            this.account = account;
            this.ruleType = ruleType;
            this.displayRule = displayRule;
            this.actualPattern = actualPattern;
        }

        public Account getAccount() {
            return account;
        }

        public String getRuleType() {
            return ruleType;
        }

        public String getDisplayRule() {
            return displayRule;
        }

        public String getActualPattern() {
            return actualPattern;
        }

        /**
         * Formats output as: ACCOUNT(id,name) ACTIVATEDBY(desc,ruleType)
         */
        public String format() {
            return String.format("ACCOUNT(%s,%s) ACTIVATEDBY(%s,%s)",
                account.getId(),
                account.getName(),
                displayRule,
                ruleType);
        }
    }
}
