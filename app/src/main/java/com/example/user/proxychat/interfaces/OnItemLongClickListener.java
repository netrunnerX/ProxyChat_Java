package com.example.user.proxychat.interfaces;

import android.view.View;

/**
 * Created by Saul Castillo Forte on 23/05/17.
 */

/**
 * Interfaz OnItemLongClickListener: Interfaz para la escucha de clicks largos en cada item del RecyclerView
 */
public interface OnItemLongClickListener {
    boolean onLongClick(View view, int position);
}
