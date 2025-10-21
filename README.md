# Git CLI Proxy with Credentials

Context-aware Git and GitHub CLI proxy that automatically selects the correct credentials based on your current directory.

## Features

- **Automatic credential selection** based on directory patterns
- **Cross-platform path matching** (Windows, Git Bash, WSL)
- **Silent operation** - no output pollution
- **Fallback support** - uses plain git/gh when no match
- **Debug mode** for verification

## Quick Start

### 1. Build

```bash
mvn clean package
```

This creates `target/xyz-jphil-git_cli_proxy_with_creds-1.0.0-uber.jar`

### 2. Configure

Edit `C:\Users\User\xyz-jphil\git_cli_proxy_with_creds\settings.xml` with your accounts and paths.

### 3. Run

```bash
# Using Java JAR (development)
java -jar target/xyz-jphil-git_cli_proxy_with_creds-1.0.0-uber.jar status

# Check which account would be used
java -jar target/xyz-jphil-git_cli_proxy_with_creds-1.0.0-uber.jar
# Output: ACCOUNT(iv,Ivan Velikanov) ACTIVATEDBY(xyz-jphil,pathParent)
```

## Configuration

### Settings Location

Default: `<user-home>/xyz-jphil/git_cli_proxy_with_creds/settings.xml`

Override: `-Dgit.proxy.config=/path/to/settings.xml`

### Example Configuration

```xml
<gitProxy>
    <binaries>
        <git path="%cmdtools%\git\bin\git.exe" />
        <gh path="%cmdtools%\gh_2.74.2_windows_amd64\bin\gh.exe" />
    </binaries>

    <accounts>
        <account id="work" name="Work Account">
            <git
                sshKey="C:\Users\User\.ssh\id_rsa_work"
                userName="work-user"
                userEmail="work@example.com" />
            <gh token="ghp_..." />
            <description>Work projects</description>
            <activationRules>
                <pathParent desc="work-projects">f:\work\projects</pathParent>
                <pathRegex desc="work-repos">.*/work/.* </pathRegex>
            </activationRules>
        </account>
    </accounts>

    <debugSettings>
        <showAccountSelectionInfo accountIdentified="none" default="none" noArgsInvocation="std.err" />
        <logs enabled="false" />
    </debugSettings>
</gitProxy>
```

## Debug Settings

```xml
<!-- Production: Silent -->
<debugSettings>
    <showAccountSelectionInfo accountIdentified="none" default="none" noArgsInvocation="std.err" />
    <logs enabled="false" />
</debugSettings>

<!-- Development: Show all -->
<debugSettings>
    <showAccountSelectionInfo accountIdentified="std.err" default="std.err" noArgsInvocation="std.err" />
    <logs enabled="true" />
</debugSettings>
```

## GraalVM Native Image (Future)

Once tested, we'll build native executables:

```bash
mvn clean package -Pnative
```

This will produce:
- `target/git.exe` - Git proxy
- `target/gh.exe` - GitHub CLI proxy

## Path Matching

### pathParent
Matches if current directory is a child of specified path:

```xml
<pathParent>C:\Users\User\code\xyz-jphil</pathParent>
```

Matches:
- `C:\Users\User\code\xyz-jphil\project1`
- `C:\Users\User\code\xyz-jphil\project1\subdir`

### pathRegex
Matches using Java regex:

```xml
<pathRegex>.*/work/.*</pathRegex>
```

### Cross-Platform Paths
Specify all variants for compatibility:

```xml
<pathParent>C:\Users\User\code\xyz-jphil</pathParent>  <!-- Windows -->
<pathParent>/c/Users/User/code/xyz-jphil</pathParent>  <!-- Git Bash -->
<pathParent>/mnt/c/Users/User/code/xyz-jphil</pathParent>  <!-- WSL -->
```

## Testing

Run from different directories to verify account selection:

```bash
# In xyz-jphil directory
cd C:\Users\User\code\xyz-jphil\some-project
java -jar git-proxy.jar
# Output: ACCOUNT(iv,Ivan Velikanov) ACTIVATEDBY(xyz-jphil,pathParent)

# In unmatched directory
cd f:\random\project
java -jar git-proxy.jar
# Output: ACCOUNT(NONE,plain git) ACTIVATEDBY(no rules matched,fallback)
```
