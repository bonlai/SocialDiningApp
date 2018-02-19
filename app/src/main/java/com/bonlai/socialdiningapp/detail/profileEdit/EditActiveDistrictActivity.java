package com.bonlai.socialdiningapp.detail.profileEdit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bonlai.socialdiningapp.R;

import java.util.Arrays;
import java.util.List;

public class EditActiveDistrictActivity extends AppCompatActivity {
    GridView mGridView;
    LinearLayout mContainer;
    LinearLayout mList;
    final CheckBox[] checkbox = new CheckBox[18];
    private int checkedCount = 0;
    private final int maxLimit=3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_active_district);

        initUI();
        addDistrictsList();
    }

    private void addDistrictsList() {
        List<String> districts = Arrays.asList(getResources().getStringArray(R.array.districts));
        int idIndex = 0;
        for (String district : districts) {
            CheckBox cb = new CheckBox(this);
            cb.setOnCheckedChangeListener(checker);
            cb.setText(district);
            checkbox[idIndex] = cb;
            cb.setId(idIndex++);
            mList.addView(cb);
        }
    }

    private void initUI() {
        //mGridView= (GridView) findViewById(R.id.districts_list);
        //mContainer = (LinearLayout) findViewById(R.id.container);
        mList  = (LinearLayout) findViewById(R.id.districts_list);
    }

    private CheckBox.OnCheckedChangeListener checker = new CheckBox.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton cb, boolean b) {
            if (checkedCount == maxLimit && b) {
                cb.setChecked(false);
                Toast.makeText(getApplicationContext(),
                        "Limit reached!!!", Toast.LENGTH_SHORT).show();
            } else if (b) {

                checkedCount++;
                CharSequence myCheck = cb.getText();
                Toast.makeText(getApplicationContext(),
                        myCheck + " checked!",
                        Toast.LENGTH_SHORT)
                        .show();
            } else if (!b) {
                checkedCount--;
            }
        };
    };
}
