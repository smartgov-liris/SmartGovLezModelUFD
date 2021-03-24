The first base input of the SmartGovLez model is the osm data, that will be
used to compute rounds trajectories and establishments locations.

This input is constituted by two files : a `nodes` file and a `ways` file.
Details about the input formats are available in the [SmartGovSimulator
documentation](https://github.com/smartgov-liris/SmartGovSimulator/wiki/Osm-Data).

The model provides a way to convert a .osm file to those input json files :
check the [roads task
documentation](The-SmartGovLez-CLI#roads).

Generated files should then be used in the [input configuration
file](Configuration-File) using the `nodes` and `roads` fields.

# Preprocess

From the data from the [input .osm
file](https://github.com/smartgov-liris/SmartGovSimulator/wiki/Osm-Data#preprocess-data),
the following [highways](https://wiki.openstreetmap.org/wiki/Highways) are kept :
- "motorway"
- "trunk"
- "primary"
- "secondary"
- "tertiary"
- "unclassified"
- "residential"
- "motorway_link"
- "trunk_link"
- "primary_link"
- "secondary_link"
- "tertiary_link"
- "living_street"

In addition, "service" ways are also kept, but only if they have a defined
"service" tag that corresponds to "alley", "parking-aisle" or "driveway".

See the [OSM documentation](https://wiki.openstreetmap.org/wiki/Highways) for
more details about highways.

This preprocess can be applied using the [roads task](The-SmartGovLez-CLI#roads).

# Initialization and dead ends

During the [initialization process](The-SmartGovLez-CLI#init), an extra
preprocessing is applied to OSM roads, in order to fix dead ends where agents
could be stuck. The detailed process is described in the [SmartGovSimulator
documentation](https://github.com/smartgov-liris/SmartGovSimulator/wiki/Dead-Ends-Fix),
that provides a generic OSM data algorithm to handle those cases.
