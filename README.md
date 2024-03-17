# HURON

The Chinese translation of huron is 休伦湖（Lake Huron）, My colleague took it as the name of a data processing project. But the world is unpredictable, So I borrowed it as a commemoration.

## Basic ideas

The data is extracted through an extractor and send to the collector, the collector will further processes the data. At same time, the data will be persisted to the database to meet further analysis needs.

![huron.drawio](./script/images/huron.drawio.svg)

## Module intruduction

| #    | module name     | description                                                  |
| ---- | --------------- | ------------------------------------------------------------ |
| 1    | huron-core      | The core implementation of the project, including basic models and interfaces |
| 2    | huron-rectifier | Data buffering                                               |
| 3    | huron-assemble  | Module assembly                                              |
| 4    | huron-basic     | Basic runtime container                                      |
| 5    | huron-view      | Data display                                                 |
| 6    | huron-extractor | The specific implementation of data extraction               |

