package xyz.jphil.git_cli_proxy_with_creds.model;

public enum OutputMode {
    STDOUT("std.out"),
    STDERR("std.err"),
    NONE("none");

    private final String xmlValue;

    OutputMode(String xmlValue) {
        this.xmlValue = xmlValue;
    }

    public String getXmlValue() {
        return xmlValue;
    }

    public static OutputMode fromString(String value) {
        if (value == null) {
            return NONE;
        }
        for (OutputMode mode : values()) {
            if (mode.xmlValue.equalsIgnoreCase(value)) {
                return mode;
            }
        }
        return NONE;
    }
}
