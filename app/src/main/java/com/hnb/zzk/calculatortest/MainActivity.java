package com.hnb.zzk.calculatortest;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.amount_number) TextView mTextDisplay;
    private boolean isComputed;         /* 表达式是否已经计算 */
    private boolean isPreviousOper;     /* 前一次输入的是否是操作符字符 */
    private int mDotCount;              /* 输入小数点的次数 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resetEnv();
        ButterKnife.bind(this);
    }

    /**
     * 响应数字按钮的点击事件
     * @param numBtn
     */
    @OnClick({R.id.btn_0, R.id.btn_1,R.id.btn_2, R.id.btn_3, R.id.btn_4, R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9})
    public void onNumbersClicked(Button numBtn) {
        /* 输入框长度限制为10个字符 */
        String inputText = mTextDisplay.getText().toString();
        if (inputText.length() <= 10) {
            StringBuffer buffer = new StringBuffer(inputText);
            buffer.append(numBtn.getText());
            mTextDisplay.setText(buffer);
        }
        isPreviousOper = false;
    }

    /**
     * 响应关于按钮的点击事件
     * @param helperBtn
     */
    @OnClick(R.id.btn_info)
    public void onInfoClicked(View helperBtn) {
        SweetAlertDialog infoDlg = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE);
        infoDlg.getProgressHelper().setBarColor(Color.CYAN);
        infoDlg.setTitleText("关于本程序");
        infoDlg.setContentText("本程序为一个简单的计算器程序\n仅用于练习Android界面布局");
        infoDlg.show();
    }

    /**
     * 清除输入内容
     */
    @OnClick(R.id.btn_clear)
    public void onClearClicked(){
        mTextDisplay.setText("");
        resetEnv();
    }

    /**
     * 开始计算
     */
    @OnClick(R.id.btn_equals)
    public void onEqualsClicked(){

    }

    /**
     * 科学计算模式和普通计算模式切换
     */
    @OnClick(R.id.btn_extension)
    public void onExtensionClicked(){
        Toast.makeText(MainActivity.this, "NOT implemented yet, will come soon...", Toast.LENGTH_SHORT).show();
    }

    /**
     * 退格删除字符时,需要根据字符串长度更新状态标志
     */
    @OnClick(R.id.btn_backspace)
    public void onBackspaceClicked(){
        if (isComputed) {
            mTextDisplay.setText("");
            resetEnv();
        }
        else {
            String oldExp = mTextDisplay.getText().toString();
            if (!oldExp.isEmpty()) {
                if (oldExp.length() == 1) {
                    resetEnv();
                    mTextDisplay.setText("");
                    return;
                }

                char curChar = oldExp.charAt(oldExp.length() - 1);
                if (curChar == '.') {
                    mDotCount--;
                }
                if (curChar == '+' || curChar == '*' || curChar == '/') {
                    isPreviousOper = false;
                }
                // 减号前面一个字符可能还是乘号或者除号,需要区别对待
                if (curChar == '-' ) {
                    char preChar = oldExp.charAt(oldExp.length() - 2);
                    if (preChar == '*' || preChar == '/') {
                        isPreviousOper = true;
                    }
                    else {
                        isPreviousOper = false;
                    }
                }
                mTextDisplay.setText(oldExp.substring(0, oldExp.length() - 1));
            }
        }
    }

    /**
     * 加法操作
     * @param button
     */
    @OnClick(R.id.btn_plus)
    public void onPlusClicked(Button button) {
        if (mTextDisplay.length() > 0 && mTextDisplay.length() < 10) {
            StringBuffer buffer = new StringBuffer(mTextDisplay.getText().toString());
            if (buffer.length() == 1 && buffer.charAt(0) == '-') {
                mTextDisplay.setText("");
                resetEnv();
                return;
            }
            /**
             * 如果前一次输入的也是操作符,那么这一次就替换掉前面输入的操作符
             * 如果前一次输入的是其他字符,那么就添加到表达式末尾
             */
            if (!isPreviousOper) {
                buffer.append(button.getText());
            }
            else {
                if (buffer.length() > 2) {
                    //
                    char preChar = buffer.charAt(buffer.length()-2);
                    if (preChar == '*' || preChar == '/') {
                        buffer.setCharAt(buffer.length()-2, button.getText().charAt(0));
                        buffer.deleteCharAt(buffer.length()-1);
                    }
                    else {
                        buffer.setCharAt(buffer.length()-1, button.getText().charAt(0));
                    }
                }
                else {
                    buffer.setCharAt(buffer.length()-1, button.getText().charAt(0));
                }
            }
            mTextDisplay.setText(buffer);
            isPreviousOper = true;
            mDotCount = 0;
        }
    }

    /**
     * 减法操作有点特殊,因为减号还可以当做负号,因此要特别处理
     * @param button
     */
    @OnClick(R.id.btn_minus)
    public void onMinusClicked(Button button) {
        if (mTextDisplay.length() < 10) {
            StringBuffer buffer = new StringBuffer(mTextDisplay.getText().toString());
            if (buffer.length() == 1 && buffer.charAt(0) == '-') {
                isPreviousOper = true;
            }
            /**
             * 如果前一次输入的是其他字符,那么就添加到表达式末尾
             * 如果前一次输入的是乘号或除号,那么还可以输入减号,这时减号当做负号
             * 如果前一次输入的是加号或减号,就当重复处理
             */
            if (!isPreviousOper) {
                buffer.append(button.getText());
            }
            else {
                char operator = buffer.charAt(buffer.length()-1);
                if (operator == '/' || operator == '*') {
                    buffer.append(button.getText());
                }
                else {
                    buffer.setCharAt(buffer.length()-1, button.getText().charAt(0));
                }
            }
            mTextDisplay.setText(buffer);
            isPreviousOper = true;
            mDotCount = 0;
        }
    }

    /**
     * 乘法操作
     * @param button
     */
    @OnClick(R.id.btn_multiply)
    public void onMultiplyClicked(Button button) {
        if (mTextDisplay.length() > 0 && mTextDisplay.length() < 10) {
            StringBuffer buffer = new StringBuffer(mTextDisplay.getText().toString());
            if (buffer.length() == 1 && buffer.charAt(0) == '-') {
                mTextDisplay.setText("");
                resetEnv();
                return;
            }
            /**
             * 如果前一个字符也是操作符,那么这一次就替换掉前面输入的操作符
             * 如果前一个字符是其他字符,那么就添加到表达式末尾
             */
            if (!isPreviousOper) {
                buffer.append(button.getText());
            }
            else {
                if (buffer.length() > 2) {
                    //
                    char preChar = buffer.charAt(buffer.length()-2);
                    if (preChar == '*' || preChar == '/') {
                        buffer.setCharAt(buffer.length()-2, button.getText().charAt(0));
                        buffer.deleteCharAt(buffer.length()-1);
                    }
                    else {
                        buffer.setCharAt(buffer.length()-1, button.getText().charAt(0));
                    }
                }
                else {
                    buffer.setCharAt(buffer.length()-1, button.getText().charAt(0));
                }
            }
            mTextDisplay.setText(buffer);
            isPreviousOper = true;
            mDotCount = 0;
        }
    }

    /**
     * 除法操作
     * @param button
     */
    @OnClick(R.id.btn_divide)
    public void onDivideClicked(Button button) {
        if (mTextDisplay.length() > 0 && mTextDisplay.length() < 10) {
            StringBuffer buffer = new StringBuffer(mTextDisplay.getText().toString());
            if (buffer.length() == 1 && buffer.charAt(0) == '-') {
                mTextDisplay.setText("");
                resetEnv();
                return;
            }
            /**
             * 如果前一次输入的也是操作符,那么这一次就替换掉前面输入的操作符
             * 如果前一次输入的是其他字符,那么就添加到表达式末尾
             */
            if (!isPreviousOper) {
                buffer.append(button.getText());
            }
            else {
                if (buffer.length() > 2) {
                    //
                    char preChar = buffer.charAt(buffer.length()-2);
                    if (preChar == '*' || preChar == '/') {
                        buffer.setCharAt(buffer.length()-2, button.getText().charAt(0));
                        buffer.deleteCharAt(buffer.length()-1);
                    }
                    else {
                        buffer.setCharAt(buffer.length()-1, button.getText().charAt(0));
                    }
                }
                else {
                    buffer.setCharAt(buffer.length()-1, button.getText().charAt(0));
                }
            }
            mTextDisplay.setText(buffer);
            isPreviousOper = true;
            mDotCount = 0;
        }
    }

    /**
     * 小数点,一个操作数中只能出现一次,使用mDotCount进行控制
     * @param button
     */
    @OnClick(R.id.btn_dot)
    public void onDotClicked(Button button) {
        if (mTextDisplay.length() < 10) {
            StringBuffer buffer = new StringBuffer(mTextDisplay.getText().toString());
            if (mDotCount == 0) {
                buffer.append(button.getText());
                mDotCount++;
                mTextDisplay.setText(buffer);
            }
        }
    }

    private void resetEnv() {
        isComputed = false;
        isPreviousOper = false;
        mDotCount = 0;
    }

}
