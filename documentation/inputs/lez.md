# LEZ

The model can take a LEZ as input, used when the `scenario` is set to
`LezDeliveries` in the [configuration
file](https://github.com/smartgov-liris/SmartGovSimulatorDocExamples/blob/master/documentation/Dead-Ends-Fix.md).

# Format

An input LEZ is described using the following JSON format :
```json
{
	"perimeter": [
		[
			45.7169499806014,
			4.844092509471722
		],
		[
			45.725966824108625,
			4.84245482766124
		],
		[
			45.72737631035004,
			4.831012263833104
		],
		...
	],
	"allowed": [
		"CRITAIR_1",
		"CRITAIR_2",
		...
	]
}
```

- `perimeter` : An ordered list of points that describe the LEZ polygon. Each
	point is specified as `[latitude, longitude]`. If the polygon is not close,
	it will be automatically at runtime.
- `allowed` : List of permissions, based on the [French
	Crit'Airs](https://www.crit-air.fr/en/information-about-the-critair-vignette/the-french-vignette-critair/who-will-get-which-critair-colours.html)
	([possible values](https://smartgov-liris.github.io/SmartGovLezModelUFD/org/liris/smartgov/lez/core/environment/lez/criteria/CritAir.html)).
	The API actually provide a [generic
	interface](https://smartgov-liris.github.io/SmartGovLezModelUFD/org/liris/smartgov/lez/core/environment/lez/criteria/LezCriteria.html)
	to build other criterias, however only CritAirs have been used in the
	context of this model.

# User Interface

An web user interface has been developed to easily draw some LEZ and generate a
JSON file with the write format.

![LEZ Example](/documentation/lez.png)

See the [GitHub repository](https://github.com/smartgov-liris/lez-viewer) for
documentation. The tool is also available online at
https://smartgov-liris.github.io/lez-viewer/ .

Downloaded json files can be used directly as input in the [Configuration
File](Configuration-File.md), using the `lez` field.
