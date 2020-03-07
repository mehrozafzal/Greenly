package greenely.greenely.retail.ui.charts;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;

public class LeftAxisValueFormatter implements IAxisValueFormatter {

    private DecimalFormat mFormat;

    public LeftAxisValueFormatter() {
        mFormat = new DecimalFormat("#");
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return mFormat.format(value/100) + "";
    }
}

