/**
 *******************************************************************************
 **	�t�@�C���� : balancer_param.c
 **
 **	�T�v       : �|���U�q����p�����[�^�[
 **
 ** ���L       : �|���U�q����p�����[�^�[�͐�������ɑ傫�ȉe����^���܂��B
 **
 *******************************************************************************
 **/

/*============================================================================
 * �f�[�^��`
 *===========================================================================*/
float A_D = 0.8F;	/* ���[�p�X�t�B���^�W��(���E�ԗւ̕��ω�]�p�x�p) */
float A_R = 0.996F;	/* ���[�p�X�t�B���^�W��(���E�ԗւ̖ڕW���ω�]�p�x�p) */

/* ��ԃt�B�[�h�o�b�N�W��
 * K_F[0]: �ԗ։�]�p�x�W��
 * K_F[1]: �ԑ̌X�Ίp�x�W��
 * K_F[2]: �ԗ։�]�p���x�W��
 * K_F[3]: �ԑ̌X�Ίp���x�W��
 */
float K_F[4] = {-0.86526F, -30.73965F, -1.14828F*0.70F, -2.29757F};  // m=0.05;R=0.05;M=0.79;W=0.177;D=0.08;H=0.140
float K_I = -0.44721F;   /* �T�[�{����p�ϕ��t�B�[�h�o�b�N�W�� */

float K_PHIDOT = 25.0F*2.75F; /* �ԑ̖ڕW����p���x�W�� */
float K_THETADOT = 6.00F; /* ���[�^�ڕW��]�p���x�W�� */

const float BATTERY_GAIN = 0.001089F;	/* PWM�o�͎Z�o�p�o�b�e���d���␳�W�� */
const float BATTERY_OFFSET = 0.625F;	/* PWM�o�͎Z�o�p�o�b�e���d���␳�I�t�Z�b�g */

/*****************************************************************************
 * �萔�ύX���e�ɂ��Ă͉��L�T�C�g���Q�Ƃ��Ă�������. 20180224 ���C�n�� ����
 * �萔�ύX�ӏ��̉��
 * http://bit.ly/2EN22ku
 */
/******************************** END OF FILE ********************************/