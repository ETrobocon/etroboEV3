/*
 *  RemoteTask.java (for leJOS EV3)
 *  Created on: 2016/02/11
 *  Updated on: 2018/04/30
 *  Copyright (c) 2016-2018 Embedded Technology Software Design Robot Contest
 */
package jp.etrobo.ev3.sample;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * PC との通信制御タスク。
 */
public class RemoteTask implements Runnable {
    public static final int   REMOTE_COMMAND_START = 71;   // 'g'
    public static final int   REMOTE_COMMAND_STOP  = 83;   // 's'

    private static final int   SOCKET_PORT          = 7360; // PCと接続するポート

    private ServerSocket    server;
    private Socket          client;
    private InputStream     inputStream;
    private DataInputStream dataInputStream;
    private int             remoteCommand;

    /**
     * コンストラクタ。
     */
    public RemoteTask() {
        server = null;
        client = null;
        inputStream = null;
        dataInputStream = null;
        remoteCommand = 0;
    }

    /**
     * PC との通信制御。
     */
    @Override
    public void run() {
        try {
            if (server == null) { // 未接続
                server = new ServerSocket(SOCKET_PORT);
                client = server.accept();
                inputStream = client.getInputStream();
                dataInputStream = new DataInputStream(inputStream);
            } else {
                if (dataInputStream.available() > 0) {
                    remoteCommand = dataInputStream.readInt();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            server = null;
            dataInputStream = null;
        }
    }

    /**
     * リモートコマンドのチェック。
     * @param command コマンド
     */
    public final boolean checkRemoteCommand(int command) {
        if (remoteCommand > 0) {
            if (remoteCommand == command) {
                return true;
            }
        }
        return false;
    }

    /**
     * 終了処理。
     */
    public void close() {
        if (server != null) {
            try { server.close(); } catch (IOException ex) {}
        }
    }
}
