package com.example.yuanmengzeng.hexagonblock;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

/**
 *
 * Created by yuanmengzeng on 2016/6/12.
 */
public class ExitDialog extends Dialog implements View.OnClickListener{

    private Activity mActivity;
    private DismissListner dismissListner;

    public ExitDialog(Activity context,DismissListner dismissListner) {
        super(context, R.style.dim_back_dialog);
        mActivity = context;
        this.dismissListner = dismissListner;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exit_dialog);
        findViewById(R.id.exit).setOnClickListener(this);
        findViewById(R.id.continue_game).setOnClickListener(this);
        Window w = getWindow();
        w.getAttributes().width = getContext().getResources().getDisplayMetrics().widthPixels * 3 / 4;
        w.getAttributes().height = getContext().getResources().getDisplayMetrics().heightPixels / 2;
        w.setGravity(Gravity.CENTER);
        w.setWindowAnimations(R.style.dialog_anim_scale_in_out);
    }

    @Override
    public void show() {
        setCanceledOnTouchOutside(true);
        super.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.exit:
                dismiss(true);
                mActivity.finish();
                break;
            case R.id.continue_game:
                dismiss(false);
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if(dismissListner!=null){
            dismissListner.onDismiss(false);
        }
    }

    private void dismiss(boolean exit){
        super.dismiss();
        if(dismissListner!=null){
            dismissListner.onDismiss(exit);
        }
    }

    public interface DismissListner{
        void onDismiss(boolean exit);
    }
}
