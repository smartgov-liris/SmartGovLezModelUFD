All the model inputs are actually specified through the [input configuration
file](https://github.com/smartgov-liris/SmartGovSimulatorDocExamples/blob/master/documentation/Create-a-SmartGov-project.md#configuration-file).

Even if its facultative in the global SmartGovSimulator model, a few entries
are mandatory in the case of the SmartGovLezModel.

# Example
```
# Scenario
scenario=LezDeliveries

# Input Files

## OSM
nodes=osm/nodes.json
roads=osm/ways.json

## Establishments
establishments=establishment/establishments.json
fleet_profiles=establishment/defaultFleetProfile.json

## COPERT
copert_table=copert/Hot_Emissions_Parameters_France.csv

## LEZ
lez=lez/lez.json

# Output Files
outputDir=../output/lez/
```

# Details

All the specified file paths should be **relative to the configuration file**.

## Scenario
- `scenario` : `LezDeliveries` or `NoLezDeliveries`

## OSM

For more information about those files, see the [SmartGovSimulator input OSM
format](https://github.com/smartgov-liris/SmartGovSimulatorDocExamples/blob/master/documentation/Osm-Data.md).
They should be generated using the SmartGovLezModel [roads
task](/documentation/The-SmartGovLez-CLI.md#roads).
- `nodes` : OSM nodes JSON file
- `roads` : OSM ways JSON file

## Establishments

- `establishments` : path to an [establishment input file](Establishments.md)
- `fleet_profiles` : path to an [establishments COPERT fleet profiles
	file](COPERT-Profiles.md)

## Copert

- `copert_table` : path to a .csv [COPERT table](COPERT-Data.md)

