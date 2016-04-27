1 概要

　EV3way-ET 用のサンプルログラムです。
　NXTway-ET 用サンプルログラムを移植しています。

2 動作環境

　走行体　　　　　　EV3way-ET (「EV3wayET16.pdf」参照 )
　　　　　　　　　　モータA：尻尾 B：右輪 C：左輪 D：未使用
　　　　　　　　　　センサ1：タッチセンサ 2：超音波センサ 3：カラーセンサ 4：ジャイロセンサ
　RTOS　　　　　　　TOPPERS/HRP2 Kernel
　PC開発環境　　　　Microsoft Windows 8.1 64bit Home Premium
　　　　　　　　　　Microsoft Windows XP 32bit 以降で可能と思われます。
　microSD 　　　　　メーカ、容量任意 FAT32フォーマット
　Bluetoothホスト 　USBドングルタイプ
　　　　　　　　　　東芝製ドライバーが必要
　　　　　　　　　　Windows 8.1 64bit の場合、下記からダウンロード
　　　　　　　　　　http://dynabook.com/assistpc/osup/windows8/manu/compo/TC00442200.htm
　走行コース　　　　サンプルコースなどライントレース確認用のコースが必要です。

3 サンプルログラム

　sample_c4 　　　　二輪倒立でライントレースを行います
　　　　　　　　　　超音波センサによる障害物検知を行います
　　　　　　　　　　尻尾による完全停止の状態からスタートできます
　　　　　　　　　　Bluetooth通信によるリモートスタートが可能です

4 主なファイル構成

　app.c　　　　　　ソースファイル
　app.h　　　　　　ヘッダーファイル
　app.cfg　　　　　TOPPERS/HRP2 Kernel用コンフィギュレーションファイル
　Makefile.inc　　リンクするモジュールを定義するファイル

5 使用手順

　5.1 ビルド

　　sample_c4フォルダを hrp2/sdk/workspace/ 直下に置いてください。
　　インストールからプログラムの実行まで手順は、TOPPERS/EV3RTのWebサイト
　　(http://dev.toppers.jp/trac_user/ev3pf/wiki/WhatsEV3RT)を参照してください。

　　sample_c4 のビルドはcygwin64起動後、以下のコマンドを実行してください。

　　・動的ローディング形式の場合

　　　　$ cd hrp2/sdk/workspace
　　　　$ make app=sample_c4

　　　ビルドに成功すると "app" という実行ファイルが生成されます。


　　・スタンドアローン形式の場合

　　　　$ cd hrp2/sdk/workspace
　　　　$ make img=sample_c4

　　　ビルドに成功すると "uImage" という実行ファイルが生成されます。


　5.2 実行ファイルのEV3への転送と実行

　　上記でビルドした "app" や "uImage" を EV3 で実行する方法については、
　　TOPPERS/EV3RTのWebサイト(http://dev.toppers.jp/trac_user/ev3pf/wiki/SampleProgram)
　　を参照してください。

　　プログラムの実行の前には尻尾を原点位置である一番上にあげておいてください。
　　プログラム起動後に尻尾が完全停止位置に動きますので注意してください。

　　画面に "EV3way-ET sample_c4" とプログラム名が表示されれば、起動成功です。

　5.3 プログラムの操作手順

　　・走行体をラインの左エッジに配置します

　　・タッチセンサ押下で走行を開始します

　　・走行を終了するには、EV3本体の [Back] ボタンを押下してください。

　5.4 Bluetooth接続

　　TOPPERS/EV3RTではユーザプログラムの起動前にBluetooth接続を行います。 

　　「5.2 実行 」の状態で、ホスト側との接続作業を行ってください。
　　TeraTerm等のターミナルソフトのシリアル通信機能でEV3の仮想COMポートを
　　指定して接続してください。

　　「5.3 プログラムの操作手順」の状態でタッチセンサー代わりに、PCから
　　「1」(文字コード31h)を送信すると、走行を開始します。

