package xyz.jphil.git_cli_proxy_with_creds;
import org.graalvm.nativeimage.ProcessProperties;
public class GenericMain {
    public static void main(String[] args) {
        var executableName = ProcessProperties.getExecutableName();
        if(executableName.startsWith("git")){
            GitProxyMain.main(args);
        }else if(executableName.startsWith("gh")){
            GhProxyMain.main(args);
        }else{
            System.out.println("Executable name must be either git or gh or must start with either of these to trigger the correct executable. Or use dedicated Main and call from java. Current executable name was "+executableName);
        }
    }
}
