package org.example.server;

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

    public static final String R_CORRECT = "CORRECT";
    public static final String R_WRONG   = "WRONG";
    public static final String R_TIMEOUT = "TIMEOUT";

    public static final String E_WIN          = "WIN";
    public static final String E_LOSE         = "LOSE";
    public static final String E_DRAW         = "DRAW";
    public static final String E_OPPONENT_LEFT = "OPP_LEFT";

    private Protocol() {}
}
