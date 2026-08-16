# Hexagonal Grid System

FitQuest uses a hexagonal grid system to partition the map into discrete, equal-sized territories. This is achieved using the [Uber H3](https://h3geo.org/) library.

## Uber H3 Integration

H3 is a hierarchical geospatial indexing system that partitions the world into hexagonal cells.

- **Indexing**: Location coordinates (Latitude, Longitude) are converted into a unique H3 index (a 64-bit integer, usually represented as a hexadecimal string).
- **Hierarchical**: H3 supports multiple resolutions. Higher resolutions represent smaller hexes.

### Native Libraries
The H3 core is written in C. FitQuest bundles the native `.so` libraries for Android in:
`fitquest/src/main/jniLibs/`

These are loaded via the `UberH3HexIndexer` class which wraps the `H3Core` Java API.

## Spatial Configuration

### Resolution
The H3 resolution determines the size of the territory.

| Resolution | Edge Length | Area | Use Case |
| :--- | :--- | :--- | :--- |
| **Res 10** | ~65.8 meters | ~0.015 km² | **Current Dev Setting**: Provides fast feedback for capturing. |
| **Res 9** | ~174.4 meters | ~0.105 km² | **Potential Production Setting**: Larger territories for better long-term gameplay. |

### Grid Ring Size (k-ring)
To determine "nearby" territory, the system uses the `gridDisk` (k-ring) function.
- **k=2**: Returns the center hex and two rings of neighbors (total 19 hexes). This is the current setting for the visible grid on the map.

## GeoJSON Mapping

To render the hexagonal grid on the [MapLibre](https://maplibre.org/) map, the H3 indexes must be converted into GeoJSON.

### `HexGeoJsonMapper`
The `HexGeoJsonMapper` object provides utility functions to:
1. Fetch boundaries (list of points) for a set of Hex IDs.
2. Construct `Polygon` and `Feature` objects.
3. Serialize the entire set into a `FeatureCollection` JSON string.

### Performance Considerations
Conversion from H3 Index to GeoJSON involves native JNI calls and string serialization.
> [!IMPORTANT]
> Always perform GeoJSON generation on a **background thread** (as done in `CaptureScreenModel`) to prevent UI frame drops and ANRs.

## Visualization Layers

The UI renders three distinct layers of hexes:
1. **Nearby Grid**: The immediate surroundings of the user (k=2).
2. **Current Hex**: The specific hex the user is currently standing in.
3. **Captured Hexes**: All hexes previously "conquered" by the user.
