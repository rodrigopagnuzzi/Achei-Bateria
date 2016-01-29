package eliteapps.SOSBattery.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseAnalytics;

import eliteapps.SOSBattery.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarregueiFragment extends Fragment {


    public CarregueiFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_carreguei, container, false);
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.app_bar2); // Attaching the layout to the toolbar object


        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);


        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);

        TextView t1 = (TextView) v.findViewById(R.id.textMsgCarregar);
        TextView t2 = (TextView) v.findViewById(R.id.textMsgCarregar2);
        Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Leelawadee.ttf");
        t1.setTypeface(type);
        t2.setTypeface(type);

        v.findViewById(R.id.botãoFeedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseAnalytics.trackEventInBackground("Carregou_Email");

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "hmdugin@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Sugestão/Comentário para SOS Battery");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, "Enviando Email..."));
            }
        });

        return v;
    }


}