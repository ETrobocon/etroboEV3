■セットアップ
　Windows:
　  etrobobtset.bat を (leJOSインストールフォルダ)\bin\ にコピーしてください
　　etroboTools.jar を (leJOSインストールフォルダ)\lib\pc\ にコピーしてください

　Mac OS X:
  　etrobobtset を (leJOSインストールフォルダ)/bin/ にコピーしてください
　　etroboTools.jar を (leJOSインストールフォルダ)/lib/pc/ にコピーしてください

■利用方法
　etrobobtset [ -n <name> ] [ -p <pincode> ] [ -a <ipaddress> ]

　　Bluetooth 関連の設定の変更を行います。具体的には以下を行います。
　　　* EV3 のデバイス名を設定します
　　　* ペアリング時の PIN コード入力を強制するようにします
　　　* EV3 側の PIN コードを設定します。

　　オプション：
　　　-n <name>
　　　　　EV3 に設定するデバイス名を指定します。デフォルトは "ET000"。
　　　-p <pincode>
　　　　　EV3 に設定する Bluetooth pin コードを設定します。デフォルトは "1234"。
　　　-a <ipaddress>
　　　　　EV3に接続するための IP アドレスを設定します。デフォルトは "10.0.1.1"。
