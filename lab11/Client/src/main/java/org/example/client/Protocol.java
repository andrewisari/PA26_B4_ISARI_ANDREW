package org.example.client;

public final class Protocol {

    public static final String SEPARATOR = "|";
    public static final String SPLIT_REGEX = "\\|";

    public static final String C_NAME = "NAME";
    public static final String C_ANS  = "ANS";
    public static final String C_QUIT = "QUIT";

    public static final String S_WELCOME  = "WELCOME";
    public static final String S_WAIT     = "WAIT";
    public static final String S_START    = "START";
    public static final String S_QUESTION = "Q";
    public static final String S_RESULT   = "RESULT";
    public static final String S_END      = "END";
    public static final String S_INFO     = "INFO";
    public static final String S_ERROR    = "ERR";

    private Protocol() {}
}
