package hmdugin.acheibateria.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.List;

import hmdugin.acheibateria.R;
import hmdugin.acheibateria.domain.Loja;
import hmdugin.acheibateria.util.BitmapDecodeUtil;
import hmdugin.acheibateria.util.CalendarUtil;

public class CustomAdapter extends ParseQueryAdapter<ParseObject> {


    private static ParseGeoPoint mGeoPoint;
    private final String TAG = this.getClass().getSimpleName();
    Bitmap bitmap;
    private TextView txtEnd, txtDist, txtHrFunc, txtBairro;
    private ParseImageView imgLoja;

    public CustomAdapter(Context context, String className, List<Loja> lista) {
        super(context, className);
    }

    public CustomAdapter(Context context, final ParseGeoPoint geoPoint, final int distancia) {

        // Use the QueryFactory to construct a PQA that will only show
        // Todos marked as high-pri
        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("loja");
                query.whereWithinKilometers("coord", geoPoint, distancia);
                return query;
            }
        });
        mGeoPoint = geoPoint;
    }

    // Customize the layout by overriding getItemView
    @Override
    public View getItemView(ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.content_lista_lojas, null);
        }

        // super.getItemView(object, v, parent);
        Loja lojas = (Loja) object;

        // Add and download the image
        imgLoja = (ParseImageView) v.findViewById(R.id.imgLoja);
        ParseFile imageFile = lojas.getImg();
        if (imageFile != null) {

            try {
                bitmap = BitmapDecodeUtil.getRoundedCornerBitmap(BitmapDecodeUtil.decodeFile(imageFile.getFile()));
                imgLoja.setImageBitmap(bitmap);

                imgLoja.loadInBackground();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // imgLoja.setParseFile(imageFile);

        }


        // Add the title view
        TextView txtNome = (TextView) v.findViewById(R.id.txtNome);
        txtNome.setText(lojas.getNome());

        txtEnd = (TextView) v.findViewById(R.id.txtEnd);
        String endereco = lojas.getEnd();
        txtEnd.setText(endereco);
        txtBairro = (TextView) v.findViewById(R.id.txtBairro);
        txtBairro.setText(lojas.getBairro());
        txtDist = (TextView) v.findViewById(R.id.txtDist);

        double distanciaKm = mGeoPoint.distanceInKilometersTo(lojas.getCoord());
        int minutos = calculaTempoMin(distanciaKm);
        txtDist.setText(String.format("%d min", (int) minutos));
        if (!lojas.getIsWifiAvailable())
            v.findViewById(R.id.imgWifi).setVisibility(View.GONE);

        txtHrFunc = (TextView) v.findViewById(R.id.txtHrFunc);
        txtHrFunc.setText(CalendarUtil.HrFuncionamento(lojas));


        return v;
    }


    private int calculaTempoMin(double distanciaKm) {
        int distanciaM = (int) (1000 * distanciaKm);
        double segundos = distanciaM / 1.3;
        double minutos = segundos / 60;
        if ((int) minutos == 0)
            minutos = 1;
        return (int) minutos;

    }


}