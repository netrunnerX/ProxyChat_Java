package com.example.user.proxychat.data.repository;

import com.example.user.proxychat.data.MeetingPoint;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by net on 22/11/17.
 */

public class MeetingPointsCercanosRepository {
    private Map<String, Marker> mPointsCercanosMark;
    private Map<String, MeetingPoint> mPointsCercanosPerfil;
    private static MeetingPointsCercanosRepository meetingPointsCercanosRepository;

    private MeetingPointsCercanosRepository() {
        mPointsCercanosMark = new HashMap<>();
        mPointsCercanosPerfil = new HashMap<>();
    }

    public static MeetingPointsCercanosRepository getInstance() {
        if (meetingPointsCercanosRepository == null) {
            synchronized (MeetingPointsCercanosRepository.class) {
                if (meetingPointsCercanosRepository == null) {
                    meetingPointsCercanosRepository = new MeetingPointsCercanosRepository();
                }
            }
        }
        return meetingPointsCercanosRepository;
    }

    public void putMeetingPointCercano(MeetingPoint meetingPoint, Marker marker) {
        //Añade el objeto MeetingPoint al map de puntos de encuentro cercanos
        mPointsCercanosPerfil.put(meetingPoint.getId(), meetingPoint);
        //Añade el marcador al map de marcadores de puntos de encuentro cercanos
        mPointsCercanosMark.put(meetingPoint.getId(), marker);
    }

    public void removeMeetingPointCercano(String meetingPointId) {
        //Elimina el marcador del mapa
        mPointsCercanosMark.get(meetingPointId).remove();
        //Elimina el marcador de la lista de marcadores
        mPointsCercanosMark.remove(meetingPointId);
        //Elimina el marcador de la lista de puntos de encuentro
        mPointsCercanosPerfil.remove(meetingPointId);
    }

    public void actualizarLocalizacionMeetingPointCercano(String meetingPointId, LatLng latLng) {
        mPointsCercanosMark.get(meetingPointId).setPosition(latLng);
    }

    public MeetingPoint getMeetingPointPerfil(String meetingPointId) {
        return mPointsCercanosPerfil.get(meetingPointId);
    }
}
