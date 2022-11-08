import './Puzzle.css';
import React, { useState, useEffect} from 'react';
import Map,{ViewStateChangeEvent} from "react-map-gl"
import 'mapbox-gl/dist/mapbox-gl.css'
import {myKey} from './private/key'



export default function Demo() {
  const [viewState, setViewState] = React.useState({
    longitude: -71.4129,
    latitude: 41.8245,
    zoom: 10
  })
  return (
    <Map
        mapboxAccessToken={myKey}
        latitude={viewState.latitude}
        longitude={viewState.longitude}
        zoom={viewState.zoom}
        onMove = {(ev:ViewStateChangeEvent) => setViewState(ev.viewState)}
        style={{width:window.innerWidth,height:window.innerHeight*0.9}}
        mapStyle={'mapbox://styles/mapbox/light-v10'}>
    </Map>
  )

  //GEARUP

}
