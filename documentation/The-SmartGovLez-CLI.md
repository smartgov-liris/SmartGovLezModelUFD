The provided command line interface provides a way to manage model inputs and
outputs.

The [last
build](https://github.com/smartgov-liris/SmartGovLezModel/blob/master/SmartGovLez-MASTER.jar)
can be found at the root of the repository, as `SmartGovLez-MASTER.jar`.

# Launch

The .jar can be run using the following command (make sure you have a JRE 8+
installed on your system) :

`java -jar SmartGovLez-MASTER.jar`

An important option to use is the maximum memory allocated to the JVM. If your
model has relatively heavy inputs (especially because of OSM data), you might
need to increase this memory size, otherwise the JVM will crash with memory
errors.

To to so, add the `-Xmx<size>` option to the Java command, depending on your
system resources. For example, to allow 6GB RAM usage :

`java -Xmx6g -jar SmartGovLez-MASTER.jar`

From now, the command above will be mentionned simply as `smartgovlez`.

So running `smartgovlez`, `smartgovlez -h` or `smartgovlez --help` will
display an help message about available tasks.

# Tasks

## Roads
```
usage: smartgovlez roads -f <file> [-h] -n <file> -w <file>

Build JSON nodes and ways input file from the specified osm node.
 -f,--osm-file <file>     Input OSM file
 -h,--help                Displays this help message
 -n,--nodes-file <file>   JSON nodes output file
 -w,--ways-file <file>    JSON ways output file

 Process :
- Loads the input .osm file.
- Filters ways to keep required highways.
- Filters tags to keep 'highway', 'name', 'ref', 'oneway' and 'service'
tags.
- Writes the output nodes and ways files to [nodes-file] and [ways-file]
```

The first required task is the `road` task. It allows you to build the `nodes`
and `ways` file from a preprocessed .osm file (see the [SmartGovSimulator
documentation](https://github.com/smartgov-liris/SmartGovSimulator/wiki/Osm-Data#preprocess-data)
for details).

More precisely, the following *highways* are kept :
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

The generated files might then be used as an input for the following processes.

## Init
```
usage: smartgovlez init [-c <file>] [-h]
Run the initialization process, without launching the simulation.
 -c,--config-file <file>   Input configuration file
 -h,--help                 Displays this help message

Process:
- Loads OSM data from preprocessed nodes and ways files, specified as
<nodes> and <roads> fields in the specified configuration.
- Builds establishments, delivery drivers and fleets, and compute the
shortest path of the first step of each round.
- Writes initial nodes and arcs to <output>/init folder.

Notice that this step is just a convenient way to check the initial
configuration, but is not required before launching the "main" task, that
will perform the initialization step anyway.
```

The `Init` task is actually optional, but can be helpful to debug OSM and
establishments inputs. It will load and preprocess the data as if it where the
`Run` task, but does not run any simulation.

In particular, the first trajectory of each agent (to the first establishment
of each round) is computed : if, for some reason, a path can't be built, errors
will be thrown.

This task takes as input a valid [configuration
file](Configuration-File). If the option is not specify, the task will
look for a `config.properties` file in the current working directory.

All the entries should obviously point to valid input files, as specified in
the [configuration file documentation](Configuration-File). Only the
`lez` field is optional, is the selected `scenario` is `NoLezDeliveries`.

[Standard outputs](Standard-Outputs) are finally written to the
`[outputDir]/init` directory.

## Run

```
usage: smartgovlez run [-c <file>] [-h] [-p] [-t <int>]
Run the simutation, with the specified configuration.
 -c,--config-file <file>   Input configuration file
 -h,--help                 Displays this help message
 -p,--pretty-print         Enables JSON pretty printing
 -t,--max-ticks <int>      Max ticks (1 tick = 1 second)

Raw results are written to the <outputDir>/simulation folder.
The simulation runs until max ticks count as been reached (default to 10
days) or when the last round has ended.
```
The `run` task has the same requirements as the [init](#Init) in terms of
configuration input, and performs exactly the same operations at start.

The differences is once the initialization process is done (without errors),
the simulation runs until the `max-ticks` count has been reach (if not specify,
a `max-ticks` count that correspond to 10 days is applied) or when all the
rounds [specified in
input](Establishments#Establishments-json-input-file-format) have ended.

If the `--pretty-print` option is enabled, JSON outputs will be indented,
otherwise a compact syntax is used.

[Standard outputs](Standard-Outputs) are finally written to the
`[outputDir]/simulation` directory.

Also, [Standard outputs](Standard-Outputs) are writte to the `[outputDir]/init`
directory at the end of the initialization process, just as the [init](#Init)
task does.

## Tile

```
usage: smartgovlez tile -a <file> [-h] -n <file> -o <file> [-p] [-s <arg>]
Build JSON tiles with pollution values from simulation output.
 -a,--arcs-file <file>      JSON arcs file, output of "run"
 -h,--help                  Displays this help message
 -n,--nodes-file <file>     JSON initial nodes file
 -o,--tiles-output <file>   JSON output tiles
 -p,--pretty-print          Enables pretty JSON printing
 -s,--tile-size <arg>       Tile size in meter
```

The `tile` task can be used to generate [pollution tiles
output](Tile-Map-Generation) from the [output polluted arcs](Standard-Outputs#Arcs).

- `--arcs-file` : the output arc file path
- `--nodes-file` : the output nodes file path
- `--tiles-output` : json file output path
- `--tile-size` : The size of the generated tiles, in meter
- `--pretty-print` : If enabled, the json output file will be indented


