package xyz.jphil.git_cli_proxy_with_creds;

public class GitProxyMain  {

    public static void main(String[] args) {
        ProxyCore.execute(ProxyCore.CommandType.GIT, args);
    }
}
