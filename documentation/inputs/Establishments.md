# Establishments JSON input file format

Establishments are described using the following format :
```json
[
    {
        "ST8": "1",
        "id": "52126036400018",
        "name": "establishment_name",
        "rounds": [
            {
                "departure": "10.75",
                "ids": [
                    "50849661900034",
                    "51049541900028",
                    "39478915000023",
					...
                ]
            }
        ],
        "lat": "45.123456",
        "lon": "4.84576208"
    },
	...
]
```
- `ST8` : The establishment ST8 category. Can be specified using the enum
	names, or the corresponding code (see the [associated javadoc](https://smartgov-liris.github.io/SmartGovLezModelUFD/org/liris/smartgov/lez/core/agent/establishment/ST8.html))
- `id` : An arbitrary id. For France, the SIRET is a good choice.
- `name` : Establishment name
- `rounds` : A list of rounds the establishment should perform.
  - `departure` : departure hour, in **hour**, base 24. e.g. : `10.75` => `10:45am`, `17.5` => `5:30pm`
  - `ids` : an ordered list of establishments ids to deserve. The origin
	  establishment will be automatically added at the beginning and at the end
	  of the round, so **should not be added there**.
- `lat` : establishment latitude
- `lon` : establishment longitude

Establishments can then be references in the [configuration
file](Configuration-File.md) using the `establishments` field.

# Notes

## OSM links

`lat` and `lon` will actually be used to compute the shortest OSM node
available in the [preprocessed OSM graph](/documentation/The-SmartGovLez-CLI.md#roads),
so coordinates does not need to exactly correspond to anything.

## x / y coordinates

In some example input files, you might encounter something like this instead of
the `lat` / `lon` coordinates :
```json
	"x": "779191.638039029",
	"y": "2092288.84576208"
```
In it a very specific case, due to the source of our input data, that uses the
[Lambert II
projection](https://smartgov-liris.github.io/SmartGovSimulator/org/liris/smartgov/simulator/urban/geo/utils/lambert/LambertII.html).

Even if it could be interesting in the future to handle multiple projection
systems, this one is **very uncommon nowadays** so please stick to a lat / lon
usage every time its possible.
