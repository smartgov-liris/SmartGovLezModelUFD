[COPERT](https://www.emisia.com/utilities/copert/) is an aggregated pollutant
emissions model widely used at the European scale. It allows us to compute some
pollutant emissions along a vehicle trajectory, once the vehicle charateristics
have been defined.

The following table presents all the vehicle characteristics available in
COPERT, that can be seen as a tree :

![COPERT Table](/documentation/copert.png)

Check the
[javadoc](https://smartgov-liris.github.io/SmartGovLezModelUFD/org/liris/smartgov/lez/core/copert/fields/package-frame.html)
to see the corresponding Java enumerations, that can be used to define [COPERT
profiles](COPERT-Profiles.md).

Notice that only **commercial vehicles** are handled by the simulator for now.

Input tables with Heavy Duty Trucks and Light Commercial Vehicles data can be
freely exported from the COPERT software, and then used as input in our model
as a .csv file.

> Please **DO NOT** used the COPERT ".csv export function". A bug in the
> software export numbers with commas for decimal parts, what completely broke
> the .csv file. Instead, export the data as an Excel file and convert it to .csv
> using Excel or Libre Office for example.

A clean table is also directly available to [download from this
repository](../../input/copert/Hot_Emissions_Parameters_France.csv).

The .csv file should be referenced in the [configuration
file](Configuration-File.md) using the `copert_table` field.
