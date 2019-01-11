# EntityListToCSV

This Java action will iterate through a list of entities and export all the String attributes to a CSV file. Attributes of other types are ignored. If you wish to use other types, convert them to Strings elsewhere and store them in the entity you are using for the export.

A generalisation of FIleDocument is passed with the details of where to save to CSV file. If you don't have your own generalisation, use CSVFIleDocument.

There is also the option to include the names of the attributes as a header line if necessary.

It is not possible to change the output order of the columns.
