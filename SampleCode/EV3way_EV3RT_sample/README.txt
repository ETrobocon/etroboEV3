1 �T�v

�@EV3way-ET �p�̃T���v�����O�����ł��B
�@NXTway-ET �p�T���v�����O�������ڐA���Ă��܂��B

2 �����

�@���s�́@�@�@�@�@�@EV3way-ET (�uEV3wayET16.pdf�v�Q�� )
�@�@�@�@�@�@�@�@�@�@���[�^A�F�K�� B�F�E�� C�F���� D�F���g�p
�@�@�@�@�@�@�@�@�@�@�Z���T1�F�^�b�`�Z���T 2�F�����g�Z���T 3�F�J���[�Z���T 4�F�W���C���Z���T
�@RTOS�@�@�@�@�@�@�@TOPPERS/HRP2 Kernel
�@PC�J�����@�@�@�@Microsoft Windows 8.1 64bit Home Premium
�@�@�@�@�@�@�@�@�@�@Microsoft Windows XP 32bit �ȍ~�ŉ\�Ǝv���܂��B
�@microSD �@�@�@�@�@���[�J�A�e�ʔC�� FAT32�t�H�[�}�b�g
�@Bluetooth�z�X�g �@USB�h���O���^�C�v
�@�@�@�@�@�@�@�@�@�@���Ő��h���C�o�[���K�v
�@�@�@�@�@�@�@�@�@�@Windows 8.1 64bit �̏ꍇ�A���L����_�E�����[�h
�@�@�@�@�@�@�@�@�@�@http://dynabook.com/assistpc/osup/windows8/manu/compo/TC00442200.htm
�@���s�R�[�X�@�@�@�@�T���v���R�[�X�Ȃǃ��C���g���[�X�m�F�p�̃R�[�X���K�v�ł��B

3 �T���v�����O����

�@sample_c4 �@�@�@�@��֓|���Ń��C���g���[�X���s���܂�
�@�@�@�@�@�@�@�@�@�@�����g�Z���T�ɂ���Q�����m���s���܂�
�@�@�@�@�@�@�@�@�@�@�K���ɂ�銮�S��~�̏�Ԃ���X�^�[�g�ł��܂�
�@�@�@�@�@�@�@�@�@�@Bluetooth�ʐM�ɂ�郊���[�g�X�^�[�g���\�ł�

4 ��ȃt�@�C���\��

�@app.c�@�@�@�@�@�@�\�[�X�t�@�C��
�@app.h�@�@�@�@�@�@�w�b�_�[�t�@�C��
�@app.cfg�@�@�@�@�@TOPPERS/HRP2 Kernel�p�R���t�B�M�����[�V�����t�@�C��
�@Makefile.inc�@�@�����N���郂�W���[�����`����t�@�C��

5 �g�p�菇

�@5.1 �r���h

�@�@sample_c4�t�H���_�� hrp2/sdk/workspace/ �����ɒu���Ă��������B
�@�@�C���X�g�[������v���O�����̎��s�܂Ŏ菇�́ATOPPERS/EV3RT��Web�T�C�g
�@�@(http://dev.toppers.jp/trac_user/ev3pf/wiki/WhatsEV3RT)���Q�Ƃ��Ă��������B

�@�@sample_c4 �̃r���h��cygwin64�N����A�ȉ��̃R�}���h�����s���Ă��������B

�@�@�E���I���[�f�B���O�`���̏ꍇ

�@�@�@�@$ cd hrp2/sdk/workspace
�@�@�@�@$ make app=sample_c4

�@�@�@�r���h�ɐ�������� "app" �Ƃ������s�t�@�C������������܂��B


�@�@�E�X�^���h�A���[���`���̏ꍇ

�@�@�@�@$ cd hrp2/sdk/workspace
�@�@�@�@$ make img=sample_c4

�@�@�@�r���h�ɐ�������� "uImage" �Ƃ������s�t�@�C������������܂��B


�@5.2 ���s�t�@�C����EV3�ւ̓]���Ǝ��s

�@�@��L�Ńr���h���� "app" �� "uImage" �� EV3 �Ŏ��s������@�ɂ��ẮA
�@�@TOPPERS/EV3RT��Web�T�C�g(http://dev.toppers.jp/trac_user/ev3pf/wiki/SampleProgram)
�@�@���Q�Ƃ��Ă��������B

�@�@�v���O�����̎��s�̑O�ɂ͐K�������_�ʒu�ł����ԏ�ɂ����Ă����Ă��������B
�@�@�v���O�����N����ɐK�������S��~�ʒu�ɓ����܂��̂Œ��ӂ��Ă��������B

�@�@��ʂ� "EV3way-ET sample_c4" �ƃv���O���������\�������΁A�N�������ł��B

�@5.3 �v���O�����̑���菇

�@�@�E���s�̂����C���̍��G�b�W�ɔz�u���܂�

�@�@�E�^�b�`�Z���T�����ő��s���J�n���܂�

�@�@�E���s���I������ɂ́AEV3�{�̂� [Back] �{�^�����������Ă��������B

�@5.4 Bluetooth�ڑ�

�@�@TOPPERS/EV3RT�ł̓��[�U�v���O�����̋N���O��Bluetooth�ڑ����s���܂��B 

�@�@�u5.2 ���s �v�̏�ԂŁA�z�X�g���Ƃ̐ڑ���Ƃ��s���Ă��������B
�@�@TeraTerm���̃^�[�~�i���\�t�g�̃V���A���ʐM�@�\��EV3�̉��zCOM�|�[�g��
�@�@�w�肵�Đڑ����Ă��������B

�@�@�u5.3 �v���O�����̑���菇�v�̏�ԂŃ^�b�`�Z���T�[����ɁAPC����
�@�@�u1�v(�����R�[�h31h)�𑗐M����ƁA���s���J�n���܂��B

