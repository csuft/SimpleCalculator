package com.hnb.zzk.calculatortest;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {

    /**
     * Numbers
     */
    @Bind(R.id.btn_0)Button mButtonZero;
    @Bind(R.id.btn_1)Button mButtonOne;
    @Bind(R.id.btn_2)Button mButtonTwo;
    @Bind(R.id.btn_3)Button mButtonThree;
    @Bind(R.id.btn_4)Button mButtonFour;
    @Bind(R.id.btn_5)Button mButtonFive;
    @Bind(R.id.btn_6)Button mButtonSix;
    @Bind(R.id.btn_7)Button mButtonSeven;
    @Bind(R.id.btn_8)Button mButtonEight;
    @Bind(R.id.btn_9)Button mButtonNine;

    /**
     * Operators
     */
    @Bind(R.id.btn_plus) Button mButtonPlus;
    @Bind(R.id.btn_minus) Button mButtonMinus;
    @Bind(R.id.btn_multiply) Button mButtonMultiply;
    @Bind(R.id.btn_divide) Button mButtonDivide;
    @Bind(R.id.btn_dot) Button mButtonDot;

    /**
     * Helper buttons
     */
    @Bind(R.id.btn_info) ImageButton mButtonInfo;
    @Bind(R.id.btn_extension) ImageView mButtonExtension;
    @Bind(R.id.btn_clear) ImageView mButtonClear;
    @Bind(R.id.btn_backspace) ImageView mButtonBackspace;
    @Bind(R.id.btn_equals) Button mButtonEquals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_0, R.id.btn_1,R.id.btn_2, R.id.btn_3, R.id.btn_4, R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9})
    public void onNumbersClicked(Button numBtn) {
        Toast.makeText(this, numBtn.getText(), Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.btn_info, R.id.btn_extension, R.id.btn_clear, R.id.btn_backspace, R.id.btn_equals})
    public void onHelperClicked(View helperBtn) {
        switch (helperBtn.getId())
        {
            case R.id.btn_info:
                SweetAlertDialog infoDlg = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE);
                infoDlg.getProgressHelper().setBarColor(Color.CYAN);
                infoDlg.setTitleText("关于本程序");
                infoDlg.setContentText("本程序为一个简单的计算器程序\n仅用于练习Android界面布局");
                infoDlg.show();
                break;
            case R.id.btn_extension:
                Toast.makeText(this, "btn extension", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_clear:
                Toast.makeText(this, "btn clear", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_backspace:
                Toast.makeText(this, "btn backspace", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_equals:
                Toast.makeText(this, "btn equals", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

    }

    @OnClick({R.id.btn_plus, R.id.btn_minus,R.id.btn_multiply, R.id.btn_divide, R.id.btn_dot})
    public void onOperatorClicked(View numBtn) {
        Toast.makeText(this, "ID:" + numBtn.getId(), Toast.LENGTH_SHORT).show();
    }
}
