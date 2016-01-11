package com.hnb.zzk.calculatortest;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Stack;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.amount_number) EditText mTextDisplay;

    private boolean isComputed;         /* 表达式是否已经计算 */
    private boolean isPreviousOper;     /* 前一次输入的是否是操作符字符 */
    private int mDotCount;              /* 输入小数点的次数 */

    private Stack<Double> mOperandsStack;      /* 操作数栈 */
    private Stack<Character> mOperatorsStack;  /* 操作符栈 */
    private HashMap<Character, Integer> mOperatorMapping;
    private static final String OPERATORS = "+-*/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resetEnv();

        mOperandsStack = new Stack<>();
        mOperatorsStack = new Stack<>();
        mOperatorMapping = new HashMap<>(4);
        mOperatorMapping.put('+', 1);
        mOperatorMapping.put('-', 1);
        mOperatorMapping.put('*', 4);
        mOperatorMapping.put('/', 4);

        ButterKnife.bind(this);
    }

    /**
     * 响应数字按钮的点击事件
     * @param numBtn
     */
    @OnClick({R.id.btn_0, R.id.btn_1,R.id.btn_2, R.id.btn_3, R.id.btn_4, R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9})
    public void onNumbersClicked(Button numBtn) {
        if (isComputed) {
            mTextDisplay.setText("");
            resetEnv();
            mOperatorsStack.clear();
            mOperandsStack.clear();
        }
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
        infoDlg.setTitleText("关于");
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
        isComputed = true;
        String expression = mTextDisplay.getText().toString();
        String result = parseExpression(expression);
        if (result == null) {
            SweetAlertDialog errorDlg = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
            errorDlg.getProgressHelper().setBarColor(Color.CYAN);
            errorDlg.setTitleText("操作错误");
            errorDlg.setContentText("请检查输入后再试!");
            errorDlg.show();
            mTextDisplay.setText("");
        }
        else {
            mTextDisplay.setText(result);
        }
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
     * 加法操作,乘法操作,除法操作
     * @param button
     */
    @OnClick({R.id.btn_plus, R.id.btn_multiply, R.id.btn_divide})
    public void onOtherOperatorsClicked(Button button) {
        if (isComputed) {
            mTextDisplay.setText("");
            resetEnv();
            return;
        }
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

    /**
     * 解析表达式并计算表达式的值
     * @param expression
     */
    private String parseExpression(final String expression) {
        if (expression == null || expression.isEmpty()) {
            return null;
        }
        StringBuffer operand = new StringBuffer();
        int nextPos = 0;
        int expLen = expression.length();
        double secondOperand = 0, firstOperand = 0;
        String result;

        for (int i = 0; i < expLen; ++i) {
            // 碰到了操作数,继续追加
            if (OPERATORS.indexOf(expression.charAt(i)) == -1) {
                operand.append(expression.charAt(i));
                continue;
            }
            else {
                // 如果第一个符号是符号,那么把它当做操作数的一部分
                // 如果当前操作符是减号,且前一个字符是乘号,那还是要把它当做数字的一部分.
                if(i == 0 || expression.charAt(i-1) == '*' || expression.charAt(i-1) == '/') {
                    operand.append(expression.charAt(i));
                    continue;
                }
                mOperandsStack.push(Double.valueOf(operand.toString()));
                if (!mOperatorsStack.empty()) {
                    /* 如果栈里面的操作符优先级大于当前的操作符,那么出栈计算,把结果压入操作数栈,当前的操作符入栈 */
                    boolean needPop = isPrevGreaterThanPost(mOperatorsStack.peek(), expression.charAt(i));
                    if (needPop) {
                        secondOperand = mOperandsStack.pop();
                        firstOperand = mOperandsStack.pop();
                        result = doComputing(firstOperand, secondOperand, mOperatorsStack.pop());
                        if (result.equals("NaN")) {
                            return result;
                        }
                        mOperandsStack.push(Double.valueOf(result));
                    }
                }
                mOperatorsStack.push(expression.charAt(i));

                operand.setLength(0);
            }
        }

        if (operand.length() != 0) {
            mOperandsStack.push(Double.valueOf(operand.toString()));
        }

        /**
         * 如果操作符栈和操作数栈中还有元素,继续计算
         * 需要判断操作数栈和操作符栈是否为空,防止出现空栈异常
         */
        while(!mOperatorsStack.empty()) {
            int count = 0;
            if (!mOperandsStack.empty()) {
                secondOperand = mOperandsStack.pop();
                ++count;
            }
            if (!mOperandsStack.empty()) {
                firstOperand = mOperandsStack.pop();
                ++count;
            }
            if (count == 2 && !mOperatorsStack.empty()) {
                result = doComputing(firstOperand, secondOperand, mOperatorsStack.pop());
                if (result.equals("NaN")) {
                    return result;
                }
                mOperandsStack.push(Double.valueOf(result));
            }
            else {
                return "ERROR";
            }
        }
        // 最后计算结果中,操作数栈必须只有一个元素,且操作符栈是空的,才是合法
        if (!mOperandsStack.empty() && mOperatorsStack.empty()) {
            return mOperandsStack.pop().toString();
        }
        return "ERROR";
    }

    /**
     * 计算加减乘除
     * @param firstOperand
     * @param secondOperand
     * @param operator
     * @return
     */
    private String doComputing(final double firstOperand, final double secondOperand, final char operator) {
        double result = 0;
        boolean overflowFlag = false;
        switch (operator) {
            case '+':
                result = firstOperand + secondOperand;
                break;
            case '-':
                result = firstOperand - secondOperand;
                break;
            case '*':
                result = firstOperand * secondOperand;
                break;
            case '/':
                if (secondOperand == 0) {
                    overflowFlag = true;
                }
                else {
                    result = firstOperand / secondOperand;
                }
                break;
            default:
                break;
        }
        if (overflowFlag) {
            return "NaN";
        }

        return String.format("%.3f", result);
    }

    /**
     * 比较操作符的优先级,判断是否需要出栈计算
     * @param prev
     * @param post
     * @return
     */
    private boolean isPrevGreaterThanPost(final char prev, final char post) {
        int i = mOperatorMapping.get(prev);
        int j = mOperatorMapping.get(post);
        int interval = mOperatorMapping.get(prev) - mOperatorMapping.get(post);
        return interval > 0;
    }


}
