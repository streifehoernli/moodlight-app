package ch.zhaw.hhrt.moodlightreferenceapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.rarepebble.colorpicker.ColorPickerView;


/**
 * Red-Green-Blue+White view (fragment) of the app.
 * Standard user interface where the brightness of each coloured LED can be adjusted individually
 * by changing the position of the assigned SeekBar.
 * <br>
 * <img src="{@docRoot}/images/RgbwFragment.png" alt="screenshot">
 *
 * @author Hanspeter Hochreutener, hhrt@zhaw.ch
 * @version 0.8 date 21.10.2016
 */
public class RgbwFragment extends Fragment {


    /**
     * Empty constructor is required
     */
    public RgbwFragment() {
    }


    /**
     * Called when the fragment is created.
     *
     * @param savedInstanceState state of the app before it was stopped the last time
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Add the components to the view.
     *
     * @param inflater           the LayoutLnflator
     * @param container          the ViewGroup
     * @param savedInstanceState saved state, if available
     * @return the view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        @SuppressWarnings("FieldCanBeLocal")
        View view = inflater.inflate(R.layout.fragment_rgbw, container, false);
        return view;
    }

    /**
     * Called after "onCreateView()".
     * As the components of the view are available only now
     * the Listeners cannot be attached in "onCreate()" or "onCreateView()".
     *
     * @param view               the active view
     * @param savedInstanceState saved state, if available
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lightOnOffListener();
        colorActionListener();
    }


    private void colorActionListener() {
        @SuppressWarnings("ConstantConditions") final Button setColor = (Button) getView().findViewById(R.id.button);

        setColor.setOnClickListener(
                new Button.OnClickListener() {     // Uses an inner anonymous class
                    public void onClick(View view) {
                        ColorPickerView cpv = (ColorPickerView) getView().findViewById(R.id.colorPicker);
                        String hexvalue = Integer.toHexString(cpv.getColor());
                        int r = Integer.parseInt(hexvalue.substring(2, 4), 16);
                        int g = Integer.parseInt(hexvalue.substring(4, 6), 16);
                        int b = Integer.parseInt(hexvalue.substring(6, 8), 16);
                        //RGBWColor rgbw = hsvToRgbw(Double.valueOf(360.0 / 255.0 * hue).floatValue(), Double.valueOf(sat / 255.0).floatValue(), Double.valueOf(value / 255.0).floatValue());

                        RGBWColor rgbw = convertRgbToRgbw(r,g,b);
                        ((MainActivity) getActivity()).sendData("red " + rgbw.getR());
                        ((MainActivity) getActivity()).sendData("green " + rgbw.getG());
                        ((MainActivity) getActivity()).sendData("blue " + rgbw.getB());
                        ((MainActivity) getActivity()).sendData("white " + rgbw.getW());

                        cpv.setOriginalColor(cpv.getColor());
                    }
                });
    }

    private void lightOnOffListener() {
        @SuppressWarnings("ConstantConditions") final Switch lightSwitch = (Switch) getView().findViewById(R.id.switchLightOnOff);
        lightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String data = "0";
                if (b) {
                    data = "1";
                }

                ((MainActivity) getActivity()).sendData("toggle " + data);
            }
        });

    }

    public static RGBWColor hsvToRgbw(float H, float S, float V) {

        float R_= 0f, G_= 0f, B_ = 0f;
        float C = V * S;
        float X = C * (1f - java.lang.Math.abs((H / 60f % 2f - 1f)));

        if (H >= 0 && H < 60) {
            R_ = C;
            G_ = X;
            B_ = 0;
        }
        if (H >= 60f && H < 120f) {
            R_ = X;
            G_ = C;
            B_ = 0;
        }
        if (H >= 120f && H < 180f) {
            R_ = 0;
            G_ = C;
            B_ = X;
        }
        if (H >= 180f && H < 240f) {
            R_ = 0;
            G_ = X;
            B_ = C;
        }
        if (H >= 240f && H < 300f) {
            R_ = X;
            G_ = 0;
            B_ = C;
        }
        if (H >= 300f && H < 360f) {
            R_ = C;
            G_ = 0;
            B_ = X;
        }

        float m = V - C;

        float R = (R_ + m) * 255;
        float G = (G_ + m) * 255;
        float B = (B_ + m) * 255;

        return convertRgbToRgbw(R, G, B);
    }

    private static RGBWColor convertRgbToRgbw(float r, float g, float b) {
        float W0 = Math.min(r, Math.min(g, b));
        float max = Math.max(r, Math.max(g, b));

        float M = W0 / max;
        float R0 = (1 + M) * r - W0;
        float G0 = (1 + M) * g - W0;
        float B0 = (1 + M) * b - W0;

        return new RGBWColor((int) R0, (int) G0, (int) B0, (int) W0);
    }

}
