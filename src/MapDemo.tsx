import './Puzzle.css';
import React, { useState, useEffect} from 'react';
import Map,{Layer, MapLayerMouseEvent, Source, ViewStateChangeEvent} from "react-map-gl"
import 'mapbox-gl/dist/mapbox-gl.css'
import {myKey} from './private/key'
import { geoLayer, overlayData } from './overlays';



export default function Demo() {
  const [viewState, setViewState] = React.useState({
    longitude: -71.4129,
    latitude: 41.8245,
    zoom: 10,
    bearing: 0,
    pitch: 0,
    padding: {top: 1, bottom: 20, left: 1, right: 1}
  })

  const [overlay, setOverlay] = useState<GeoJSON.FeatureCollection | undefined>(undefined)

  useEffect(() => {
    setOverlay(overlayData)
  }, []) 

  return (
    <div className="App">
    <Map
        mapboxAccessToken={myKey}
        latitude={viewState.latitude}
        longitude={viewState.longitude}
        zoom={viewState.zoom}
        pitch={viewState.pitch}
        bearing={viewState.bearing}
        padding={viewState.padding}
        onMove = {(ev:ViewStateChangeEvent) => setViewState(ev.viewState)}
        onClick={(ev: MapLayerMouseEvent) => console.log(ev)}
        style={{width:window.innerWidth,height:window.innerHeight*0.9}}
        mapStyle={'mapbox://styles/mapbox/light-v10'}>
          <Source
          id="geo_data"
          type="geojson"
          data={overlay}
          >
            <Layer {...geoLayer}>

            </Layer>
          </Source>
    </Map>
    </div>
  )

  //GEARUP

}
