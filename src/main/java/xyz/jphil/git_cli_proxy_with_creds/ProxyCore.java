package xyz.jphil.git_cli_proxy_with_creds;

import xyz.jphil.git_cli_proxy_with_creds.model.*;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProxyCore {

    // Version identifier: YYYYMMDD_HHMM format (UTC)
    private static final String VERSION = "20251024_1330";

    // Debug flag - set to true to see command execution details
    private static final boolean DEBUG = false;

    public enum CommandType {
        GIT, GH
    }

    /**
     * Main entry point for both git and gh proxies.
     */
    public static void execute(CommandType commandType, String[] args) {
        try {
            // Load configuration
            GitProxyConfig config = ConfigLoader.load();
            DebugSettings debugSettings = config.getDebugSettings();

            // Match account based on current directory
            AccountMatcher matcher = new AccountMatcher(config);
            AccountMatcher.MatchResult matchResult = matcher.findMatch(Paths.get("").toAbsolutePath());

            // Handle no-args invocation - show account info THEN pass through to git/gh
            if (args.length == 0) {
                handleNoArgsInvocation(config, commandType, matchResult);
                // Don't exit! Continue to execute git/gh to show their help
            }

            // Show account selection info if configured
            if (matchResult != null && debugSettings.getShowAccountSelectionInfo().getAccountIdentified() != OutputMode.NONE) {
                showAccountInfo(matchResult, debugSettings.getShowAccountSelectionInfo().getAccountIdentified());
            } else if (matchResult == null && debugSettings.getShowAccountSelectionInfo().getDefaultMode() != OutputMode.NONE) {
                showNoMatchInfo(config, debugSettings.getShowAccountSelectionInfo().getDefaultMode());
            }

            // Execute command
            int exitCode = executeCommand(config, commandType, matchResult, args);
            System.exit(exitCode);

        } catch (ConfigLoader.ConfigLoadException e) {
            System.err.println("Error loading configuration: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error executing command: " + e.getMessage());
            if (Boolean.getBoolean("git.proxy.debug")) {
                e.printStackTrace();
            }
            System.exit(1);
        }
    }

    /**
     * Handles no-args invocation - shows account selection info.
     */
    private static void handleNoArgsInvocation(GitProxyConfig config, CommandType commandType, AccountMatcher.MatchResult matchResult) {
        DebugSettings.ShowAccountSelectionInfo selectionInfo = config.getDebugSettings().getShowAccountSelectionInfo();

        if (selectionInfo.getNoArgsInvocation() == OutputMode.NONE) {
            return; // Silent
        }

        PrintStream out = selectionInfo.getNoArgsInvocation() == OutputMode.STDERR ? System.err : System.out;

        out.println("Git CLI Proxy Version: " + VERSION);
        if (matchResult != null) {
            out.println(matchResult.format());
        } else {
            out.println("ACCOUNT(NONE,plain " + commandType.name().toLowerCase() + ") ACTIVATEDBY(no rules matched,fallback)");
        }
    }

    /**
     * Shows account selection info when account is matched.
     */
    private static void showAccountInfo(AccountMatcher.MatchResult matchResult, OutputMode mode) {
        PrintStream out = mode == OutputMode.STDERR ? System.err : System.out;
        out.println(matchResult.format());
    }

    /**
     * Shows info when no account matches.
     */
    private static void showNoMatchInfo(GitProxyConfig config, OutputMode mode) {
        PrintStream out = mode == OutputMode.STDERR ? System.err : System.out;
        out.println("ACCOUNT(NONE,plain git/gh) ACTIVATEDBY(no rules matched,fallback)");
    }

    /**
     * Executes the git or gh command with appropriate credentials.
     */
    private static int executeCommand(GitProxyConfig config, CommandType commandType,
                                      AccountMatcher.MatchResult matchResult, String[] args) throws IOException, InterruptedException {

        ProcessBuilder pb = new ProcessBuilder();

        if (commandType == CommandType.GIT) {
            return executeGitCommand(config, matchResult, args, pb);
        } else {
            return executeGhCommand(config, matchResult, args, pb);
        }
    }

    /**
     * Executes git command with SSH key configuration.
     */
    private static int executeGitCommand(GitProxyConfig config, AccountMatcher.MatchResult matchResult,
                                         String[] args, ProcessBuilder pb) throws IOException, InterruptedException {

        List<String> command = new ArrayList<>();
        command.add(config.getBinaries().getGit().getPath());

        if (matchResult != null) {
            Account account = matchResult.getAccount();
            Account.GitCredentials gitCreds = account.getGitCredentials();

            // Add SSH configuration with properly escaped path to handle spaces
            // Using escaped quotes so they survive git's shell evaluation
            command.add("-c");
            String sshKeyPath = gitCreds.getSshKey().replace("\\", "/");
            command.add("core.sshCommand=ssh -i \\\"" + sshKeyPath + "\\\"");

            // Add user name and email
            command.add("-c");
            command.add("user.name=" + gitCreds.getUserName());

            command.add("-c");
            command.add("user.email=" + gitCreds.getUserEmail());
        }

        // Add user arguments
        for (String arg : args) {
            command.add(arg);
        }

        pb.command(command);
        pb.inheritIO(); // Pass through stdin/stdout/stderr

        // Debug: Print the command being executed
        if (DEBUG) {
            System.err.println("[Git Proxy v" + VERSION + "] Executing command:");
            System.err.println("  " + String.join(" ", command));
        }

        Process process = pb.start();
        return process.waitFor();
    }

    /**
     * Executes gh command with GitHub token environment variable.
     */
    private static int executeGhCommand(GitProxyConfig config, AccountMatcher.MatchResult matchResult,
                                        String[] args, ProcessBuilder pb) throws IOException, InterruptedException {

        List<String> command = new ArrayList<>();
        command.add(config.getBinaries().getGh().getPath());

        // Add user arguments
        for (String arg : args) {
            command.add(arg);
        }

        pb.command(command);

        // Set GITHUB_TOKEN environment variable if account matched
        if (matchResult != null) {
            Account account = matchResult.getAccount();
            if (account.getGhCredentials() != null) {
                Map<String, String> env = pb.environment();
                env.put("GITHUB_TOKEN", account.getGhCredentials().getToken());
            }
        }

        pb.inheritIO(); // Pass through stdin/stdout/stderr

        Process process = pb.start();
        return process.waitFor();
    }
}
