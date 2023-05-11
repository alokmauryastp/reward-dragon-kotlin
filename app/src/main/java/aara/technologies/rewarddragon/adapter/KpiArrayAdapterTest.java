package aara.technologies.rewarddragon.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import aara.technologies.rewarddragon.R;
import aara.technologies.rewarddragon.model.KpiData;


public class KpiArrayAdapterTest extends ArrayAdapter {

    private final String TAG = "KpiArrayAdapterTest";
    @Nullable
    private final List<KpiData> arrayList;
    private final Context context;

    public KpiArrayAdapterTest(@NotNull Context context, @NotNull List<KpiData> arrayList) {
        super(context, 0, arrayList);
        this.context = context;
        this.arrayList = arrayList;
    }


    @Override
    public int getCount() {
        return arrayList.size();
    }

    @NotNull
    public View getView(int position, @androidx.annotation.Nullable @Nullable View convertView, @NotNull ViewGroup parent) {
        //    Intrinsics.checkNotNullParameter(parent, "parent");
        View currentItemView = convertView;
        if (convertView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.custom_kpi_list, parent, false);
        }


        KpiData kpiData = arrayList.get(position);
        Log.i(TAG, "getView: " + arrayList);


        TextView rulename = currentItemView.findViewById(R.id.rulename);
        TextView rulepoint = currentItemView.findViewById(R.id.rulepoint);

        rulename.setText(kpiData.getRule());
        rulepoint.setText(String.valueOf(kpiData.getPoint()));

        return currentItemView;
    }


}

