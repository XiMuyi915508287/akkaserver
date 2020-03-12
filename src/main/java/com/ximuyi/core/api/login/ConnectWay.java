package com.ximuyi.core.api.login;

public enum ConnectWay {
    /** 外部链接（重新打开游戏） */
    Outside
    /** 内部链接（游戏中重连） */
    ,Inside
    ;

    public static ConnectWay getConnectWay(int index) {
        ConnectWay[] connectWays = ConnectWay.values();
        return index < 0 || index >= connectWays.length ? null : connectWays[index];
    }
}
