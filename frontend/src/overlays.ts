import { FeatureCollection } from "geojson";
import { FillLayer } from "react-map-gl";


// import JSON file
import rl_data from "./jsonData/fullDownload.json"


// Type predicate
function isFeatureCollection(json: any): json is FeatureCollection {
    return json.type == "FeatureCollection";
}

export function overlayData(): GeoJSON.FeatureCollection | undefined {
    if(isFeatureCollection(rl_data)) {
        return rl_data
    }
    return undefined
}

const propertyName = 'holc_grade';

//geoLayer dictating colors of redlining data overlayed on the Map
export const geoLayer: FillLayer = {
    id: 'geo_data',
    type: 'fill',
    paint: {
        'fill-color': [
            'match',
            ['get', propertyName],
            'A',
            '#5bcc04',
            'B',
            '#04b8cc',
            'C',
            '#e9ed0e',
            'D',
            '#d11d1d',
            '#ccc'
        ],
        'fill-opacity': 0.2
    }
};



