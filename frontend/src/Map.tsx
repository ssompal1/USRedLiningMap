import React, { useState, useEffect} from 'react';
import Map,{Layer, MapLayerMouseEvent, Source, ViewStateChangeEvent} from "react-map-gl"
import 'mapbox-gl/dist/mapbox-gl.css'
import {myKey} from './private/key'
import { geoLayer, overlayData } from './overlays';


/**
 * This is the class representing the Map in the web page. It utilizes a state hook in order to automatically update the map view
 * based on user drag and zoom. 
 * @returns 
 */
export default function RedLineMap() {
  //create state variable for adjusting display of map
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

  //Render map with data overlayed. Move and click call function of state hook so view of map is automatically updated
  return (
    <div className="map-demo"
    aria-label='Web page containing interactive map displaying Home Owners Loan Corporation Designations of Neighborhoods in the 1930s in the United States'
    >
    <Map
        mapboxAccessToken={myKey}
        latitude={viewState.latitude}
        longitude={viewState.longitude}
        zoom={viewState.zoom}
        pitch={viewState.pitch}
        bearing={viewState.bearing}
        padding={viewState.padding}
        //updates view of map based on user movement on mousepad
        onMove = {(ev:ViewStateChangeEvent) => setViewState(ev.viewState)}
        onClick={(ev: MapLayerMouseEvent) => console.log(ev)}
        aria-label='The interactive map with HOLC designations.'
        aria-roledescription='Use the mouse to drag and zoom around the map'
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
}
