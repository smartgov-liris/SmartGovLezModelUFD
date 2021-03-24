COPERT profiles are a concept introduced with the model to generate some
vehicle populations, from each pollutant emissions can be computed using the
[COPERT model](https://www.emisia.com/utilities/copert/).

Values mentionned in this part comes from the input [COPERT Data](COPERT-Data).

# Concept

It would be impossible to get data for each activity category for each
vehicle class (241 vehicles registered in the COPERT model). However, our
population still needs to be constituted by completely defined vehicles, in
order to compute pollutant emissions using the COPERT equation.

To solve this problem, we designed a *COPERT profile* concept that allow the
user to specify some vehicle categories in a very flexible way. The principle
is that un-determined fields are then chosen randomly and uniformely in the
input COPERT table to generate vehicles, always respecting values specified in
the COPERT profile.

Once a profile is defined, it can be used to generate a vehicle population of
any size.

# Syntax

Here is the general form of a COPERT profile :
```json
{
	"header": "Root_COPERT_Header",
	"values": 
	[
			{
				"value": "Parent_Header_Value",
				"rate": 0.5,
				"subProfile": {
					"header": "Child_Header",
					"values":
					[ {
						"value": "Parent_Header_Value_1",
						"rate": 0.2 
					},
					{
						"value": "Parent_Header_Value_2",
						"rate": 0.4 
					},
					...
					]
				}
			},
			...
	]
}
```

- `header` : A COPERT Header, from which we will defined sub-categories.
	Possible values :
   - `CATEGORY`
   - `FUEL`
   - `SEGMENT`
   - `EURO_STANDARD`
   - `TECHNOLOGY`
- `values` : The set of values used for this header. Each value can itself
	define sub-categories
   - `value` : value associated to the header. See the
	   [COPERT data](COPERT-Data) and the
	   [javadoc](https://smartgov-liris.github.io/SmartGovLezModel/org/liris/smartgov/lez/core/copert/fields/package-frame.html)
	   to check which values are available for each header.
   - `rate` : proportion of the given value. The sum of all rates at a given
	   level must equal to 1.0, otherwise errors will be thrown.
   - `subProfile` : optional. If defined, the root structure presented there is
	   used recursively. Following `headers` / `values` must be understood as
	   "Child value for the given child header with the given rate,
	   provided that the parent value for the parent header is `value`"

# Example

Here is an example COPERT Profile :
> Please **do not** try to use this directly as an input profile, see the
> [Profiles](#Profiles) section.
```json
{
	"header": "CATEGORY",
	"values": 
	[
		{
			"value": "LIGHT_WEIGHT",
			"rate": 0.5,
			"subProfile": {
				"header": "EURO_STANDARD",
				"values":
				[ {
					"value": "EURO1",
					"rate": 1.0
				} ]
			}
		},
		{
		"value": "HEAVY_DUTY_TRUCK",
			"rate": 0.5,
			"subProfile": {
				"header": "FUEL",
				"values":
				[ {
					"value": "DIESEL",
					"rate": 1,
					"subProfile": {
						"header": "SEGMENT",
						"values":
						[ {
							"value": "ARTICULATED",
							"rate": 0.2,
							"subProfile": {
								"header": "EURO_STANDARD",
								"values":
								[ {
									"value": "EURO1",
									"rate": 1.0
								} ]
							}
						},
						{
						"value": "RIGID",
						"rate": 0.8,
							"subProfile": {
								"header": "EURO_STANDARD",
								"values":
								[ {
									"value": "EURO1",
									"rate": 1.0
								} ]
							}
						} ]
					}
				} ]
			}
		}
	]
}
```

Graphically speaking, this profile could be understood as the following tree :

![COPERT Profile](copert_profile.png)

Formally speaking, it should be understood as :
- We consider a population constituted by 50% HEAVY_DUTY_TRUCKs and 50%
	LIGHT_WEIGHT_VEHICLEs.
- Among the LIGHT_WEIGHT_VEHICLEs, we only consider EURO1 vehicles.
- Among the HEAVY_DUTY_TRUCKs, we only consider DIESEL vehicles.
- Among those DIESEL powered HEAVY_DUTY_TRUCKs, we consider 20% articulated
	vehicles, and 80% rigid vehicles.
- Among those ARTICULED and RIGID vehicles, we only consider EURO1 vehicles.

# Profiles

Once this *COPERT profile* concept is established, we can use it to define
profiles by ST8 category.

The final input profile should have the following format :
```json
{
	"default": {
		default_input_profile
	},
	"AGRICULTURE": {
		agriculture_copert_profile
	},
	...
}
```

- `default` : default profile, used for ST8 categories that are not explicitly
	defined. An input file can only contain only a default profile.
- `ST8_CATEGORY` : Custom value for [ST8
	categories](https://smartgov-liris.github.io/SmartGovLezModel/org/liris/smartgov/lez/core/agent/establishment/ST8.html)

A complete example COPERT input profile can be found on [this
repository](https://github.com/smartgov-liris/SmartGovLezModel/blob/master/input/establishment/defaultFleetProfile.json).

The final input should be reference in the [configuration
file](Configuration-File) using the `fleet_profiles` field.
