The SmartGovSimulator is generally [compliant with
Jackson](https://github.com/smartgov-liris/SmartGovSimulator/wiki/Running-simulations#monitoring-shuttle-using-json-outputs),
and so the natural and priviledge output is the JSON format.

The SmartGovLezModel provides a few standard outputs, that are produced by the
[init](The-SmartGovLez-CLI#init) and [run](The-SmartGovLez-CLI#init)
tasks.


# Nodes

Nodes are written in the following format :
```json
[ {
	"id" : "1000248158",
	"outgoingArcs" : [ "1112287", "1112297" ],
	"incomingArcs" : [ "1112286", "1112296" ],
	"road" : "44083129",
	"position" : [ 45.9145871, 4.6806218 ]
	},
	...
]
```

- `id` : OSM node id
- `outgoingArcs` : list of outgoing arcs ids
- `incomingArcs` : list of incoming arcs ids
- `road` : id of the OSM *way* to which this node belongs to
- `position` : geographical position in latitude / longitude

Notice the differences with the [OSM nodes input
file](https://github.com/smartgov-liris/SmartGovSimulator/wiki/Osm-Data#nodes-file).
Those files must actually be distincts, because the concept of *arcs* does not
directly exists in OSM : only *ways* (i.e. *roads*) are used, so incoming and
outgoing arcs of each node are actually generated during the initialization
process.


# Arcs

Arcs are generated during the initialization process, from the OSM nodes and
ways. Arcs are written using the following format :
```json
[ {
	"id" : "0",
	"startNode" : "1156367438",
	"targetNode" : "2733733129",
	"length" : 14.36867966144259,
	"roadDirection" : "FORWARD",
	"pollution" : {
		"NOx" : 0.0,
		"VOC" : 0.0,
		"CO" : 0.0,
		"PM" : 0.0,
		"CH4" : 0.0,
		"N2O" : 0.0,
		"NH3" : 0.0,
		"FC" : 0.0
	},
	"inLez" : false
	},
	...
]
```
- `id` : Generated arc id
- `startNode` : id of the source OSM node
- `targetNode` : id of the target OSM node
- `length` : arc length in meter
- `roadDirection` : `FORWARD` or `BACKWARD`, depending on this arc represents
	the natural direction of the road it has been generated from or not
- `pollution` :
   - `[pollutant]` : pollution in **g/s**
- `inLez` : `true` if and only if the arc in inside a Low Emission Zone


# Pollution peeks

Pollution peeks are the maximum pollution values recorded on arcs during the
simulation. They can be used to compute relative values from the arcs'
pollution.

Pollution peeks are written as follow :
```json
{
	"NOx" : 0.0,
	"VOC" : 0.0,
	"CO" : 0.0,
	"PM" : 0.0,
	"CH4" : 0.0,
	"N2O" : 0.0,
	"NH3" : 0.0,
	"FC" : 0.0
}
```

Where values are given in **g/s** for each pollutant.

# Establishments


```json
[ {
	"id" : "41019384100021",
	"name" : "Establishment name",
	"activity" : "SMALL_SHOP",
	"location" : [ 45.85072575275749, 5.064033640753399 ],
	"closestOsmNode" : "1395126159",
	"fleet" : {
		"0" : {
			"id" : "0",
			"category" : "LIGHT_WEIGHT",
			"fuel" : "PETROL",
			"segment" : "N1_I",
			"euroNorm" : "EURO1",
			"technology" : "NONE",
			"critAir" : "NONE"
		}
		...
	},
	"rounds" : {
		"0" : {
			"establishments" : [ "75054415700019", "21690123102892", ... ],
			"initialWeight" : 0.0
		}
		...
	}
},
...
]
```

- `id` : establishment id
- `name` : establisment name
- `activity` : [ST8
	category](https://smartgov-liris.github.io/SmartGovLezModel/org/liris/smartgov/lez/core/agent/establishment/ST8.html)
- `location` : original location, in latitude / longitude
- `closestOsmNode` : id of the closest OSM node, used as a delivery point
- `fleet` : generated fleet
   - `[vehicle id]` : id used to identify the vehicle in the establishment
	   (**only usable by establishment**)
      - `id` : vehicle id (same as key)
      - `field` : `value`, [COPERT
		  characteristics](https://smartgov-liris.github.io/SmartGovLezModel/org/liris/smartgov/lez/core/copert/fields/package-frame.html)
- `rounds` : establishment rounds
   - `[vehicle id]` : id of the vehicle that must perform this round
      - `establishments` : ordered list of establishment ids to deliver,
		  without the origin establishment (start and end point of the round)
      - `initialWeight` : initial goods weight. Not used currently.

`fleet` and `rounds` are void when the establishment is not the origin of any
round.

Notice the differences with the [establishments input
file](Establishments#establishments-json-input-file-format), mainly
due to the OSM integration and the fleet generation.
