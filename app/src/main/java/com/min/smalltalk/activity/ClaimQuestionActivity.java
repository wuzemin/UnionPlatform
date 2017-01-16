package com.min.smalltalk.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.min.smalltalk.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClaimQuestionActivity extends AppCompatActivity {

    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_phone)
    EditText etPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim_friend);
        ButterKnife.bind(this);
    }
}
