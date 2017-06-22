package com.tian.videomergedemo.utils;

/*import com.itheima.oschina.R;
import com.itheima.oschina.bean.SimpleBackPage;
import com.itheima.oschina.util.UIHelper;*/

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.tian.videomergedemo.R;

public class SelectPicDialog extends Dialog implements
        android.view.View.OnClickListener {


    public interface OnSelectPicOptionClick {
        void OnPicSelect(int id);
    }

    private OnSelectPicOptionClick mListener;

    private SelectPicDialog(Context context, boolean flag,
            OnCancelListener listener) {
        super(context, flag, listener);
    }

    @SuppressLint("InflateParams")
    private SelectPicDialog(Context context, int defStyle) {
        super(context, defStyle);
        View contentView = getLayoutInflater().inflate(
                R.layout.bl_pop_layout, null);
        contentView.findViewById(R.id.tv_10s).setOnClickListener(
                this);
        contentView.findViewById(R.id.tv_30s)
                .setOnClickListener(this);
        contentView.findViewById(R.id.tv_60s)
                .setOnClickListener(this);
        contentView.findViewById(R.id.tv_90s)
        .setOnClickListener(this);
        contentView.findViewById(R.id.tv_120s)
        .setOnClickListener(this);

        contentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SelectPicDialog.this.dismiss();
                return true;
            }
        });
        super.setContentView(contentView);

    }

    public SelectPicDialog(Context context) {
        this(context, R.style.quicks_option_dialog);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setGravity(Gravity.BOTTOM);

        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth();
        
        getWindow().setAttributes(p);
    }

    public void setOnSelectPicClickListener(OnSelectPicOptionClick lis) {
        mListener = lis;
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
      
        if (mListener != null) {
            mListener.OnPicSelect(id);
        }
        dismiss();
    }


}
