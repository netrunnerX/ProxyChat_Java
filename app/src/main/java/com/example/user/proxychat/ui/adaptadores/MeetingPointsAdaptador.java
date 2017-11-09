package com.example.user.proxychat.ui.adaptadores;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.proxychat.presenter.MeetingPointsViewHolderPresenter;
import com.example.user.proxychat.ui.interfaces.OnItemClickListener;
import com.example.user.proxychat.ui.interfaces.OnItemLongClickListener;
import com.example.user.proxychat.R;
import com.example.user.proxychat.data.MeetingPoint;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by Saul Castillo Forte on 20/05/17.
 */

/**
 * MeetingPointsAdaptador: adaptador utilizado para cargar los items con los datos de cada punto de encuentro
 * en el RecyclerView de MeetingPointsFragment
 */
public class MeetingPointsAdaptador extends RecyclerView.Adapter<MeetingPointsAdaptador.MeetingPointsViewHolder> {

    //Los atributos clickListener y longClickListener nos permitira manejar eventos
    //de click en cada item desde la clase que crea la instancia del adaptador.
    //La clase que instancia al adaptador debera implementar las interfaces OnItemClickListener
    //y OnItemLongClickListener, y establecerse a si misma como escuchador para poder gestionar
    //los eventos de click en cada item del RecyclerView
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;
    private List<String> meetingPoints;

    /**
     * Constructor parametrizado
     * @param meetingPoints lista que contiene los ids de los puntos de encuentro
     */
    public MeetingPointsAdaptador(List<String> meetingPoints) {
        this.meetingPoints = meetingPoints;
    }

    /**
     * onCreateViewHolder: este metodo se ejecuta a la hora de crear un nuevo ViewHolder.
     * Un ViewHolder es un contenedor para un item del RecyclerView
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public MeetingPointsAdaptador.MeetingPointsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Crea un View usando el layout del item
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_meeting_points, parent, false);
        //Crea objeto ConversacionesViewHolder, pasandole la vista como parametro
        return new MeetingPointsAdaptador.MeetingPointsViewHolder(v);
    }

    /**
     * onBindViewHolder: este metodo lo llama el RecyclerView a la hora de mostrar el item
     * en una posicion determinada
     * @param holder ViewHolder con los datos a mostrar
     * @param position posicion en el RecyclerView donde mostrara el item
     */
    @Override
    public void onBindViewHolder(final MeetingPointsAdaptador.MeetingPointsViewHolder holder, int position) {
        holder.viewHolderPresenter.obtenerDatosMeetingPoint(meetingPoints.get(position));
    }

    /**
     * getItemCount: este metodo devuelve el total de items en el RecyclerView
     * @return
     */
    @Override
    public int getItemCount() {

        //Como es equivalente al tamaño de la lista de puntos de encuentro, se devuelve este
        return meetingPoints.size();
    }

    /**
     * MeetingPointsViewHolder: clase que define un ViewHolder personalizado de puntos de encuentro
     */
    class MeetingPointsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener,
    MeetingPointsViewHolderPresenter.MeetingPointsViewHolderView {

        CardView cardView;
        TextView tvNombre;
        TextView tvDescripcion;
        MeetingPointsViewHolderPresenter viewHolderPresenter;

        /**
         * Constructor parametrizado
         * @param v vista del item
         */
        MeetingPointsViewHolder(View v) {
            super(v);
            viewHolderPresenter = new MeetingPointsViewHolderPresenter(this);

            //Instancia el CardView
            cardView = (CardView) v.findViewById(R.id.cardViewMeetingPoints);
            //Instancia el TextView que muestra el nombre del punto de encuentro
            tvNombre = (TextView) v.findViewById(R.id.tvNombreMp);
            //Instancia el TextView que muestra la descripcion del puunto de encuentro
            tvDescripcion = (TextView) v.findViewById(R.id.tvDescripcionMp);

            //Configura un ClickListener para la vista
            v.setOnClickListener(this);
            //Configura un LongClickListener para la vista
            v.setOnLongClickListener(this);

        }

        /**
         * onClick: metodo llamado cuando se hace click sobre la vista del item
         * @param v
         */
        @Override
        public void onClick(View v) {

            if (clickListener != null)
                //Llamamos al metodo onClick del objeto OnItemClickListener definido en el adaptador
                clickListener.onClick(v, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {

            if (longClickListener != null) {
                //Llamamos al metodo onClick del objeto OnItemLongClickListener definido en el adaptador
                return longClickListener.onLongClick(v, getAdapterPosition());
            }
            return false;
        }

        @Override
        public void mostrarMeetingPoint(MeetingPoint meetingPoint) {
            tvNombre.setText(meetingPoint.getNombre());
            tvDescripcion.setText(meetingPoint.getDescripcion());
        }
    }

    /**
     * setOnItemClickListener: metodo utilizado para configurar el clickListener del adaptador
     * @param clickListener escuchador de clicks
     */
    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    /**
     * setOnItemLongClickListener: metodo utilizado para configurar el longClickListener del adaptador
     * @param longClickListener escuchador de clicks largos
     */
    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

}
