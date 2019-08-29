# SmartGovLezModel

A Low Emission Zones model implementation for the
[SmartGovSimulator](https://github.com/smartgov-liris/SmartGovSimulator).

![LEZ example](lez.png)

## Introduction

A Low Emission Zone (LEZ) is a special urban area where more polluting vehicles are
not allowed to enter.

Perimeters and permissions of such zones is very variable in the European
Union, so this project from the [LIRIS](https://liris.cnrs.fr/en) is an attempt
to evaluate the impact of such zones on pollutant emissions, in the context of
the Urban Goods Transport.

## Features

The following features have been implemented :
- Fleet generation from an input establishment set
- Determination of establishments concerned by the LEZ given 
  a LEZ perimeter / permissions
- Vehicle replacements depending on the LEZ 
- Rounds simulation over a day, and pollutant emissions computation with /
	without the input LEZ, using the [COPERT
	model](https://www.emisia.com/utilities/copert/).
- Pollution tile map generation from the output

## Build from source

From the repository where you want to install the source code, run :
`git clone https://github.com/smartgov-liris/SmartGovLezModel`

`cd SmartGovSimulator`

### Command line build

To build the project using the [Gradle CLI](https://docs.gradle.org/current/userguide/command_line_interface.html), run :

- `./gradlew build` (UNIX)
- `gradlew.bat build` (Windows)

This will compile the Java classes, and run all the unit tests.

### IntelliJ IDEA

To import the project in the IntelliJ IDEA :

`File` -> `New` -> `Project from Existing Sources` (or `Module from Existing Sources`) -> select the `SmartGovLezModel` folder -> `Import project from external model` -> select `Gradle` -> `Finish`

### Eclipse IDE

To import the project in the Eclipse Java IDE :

`File` -> `Import...` -> `Gradle` -> `Existing Gradle Project` ->  select the `SmartGovLezModel` folder -> `Finish`<Paste> 

