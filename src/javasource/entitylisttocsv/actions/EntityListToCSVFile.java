// This file was generated by Mendix Modeler.
//
// WARNING: Only the following code will be retained when actions are regenerated:
// - the import list
// - the code between BEGIN USER CODE and END USER CODE
// - the code between BEGIN EXTRA CODE and END EXTRA CODE
// Other code you write will be lost the next time you deploy the project.
// Special characters, e.g., é, ö, à, etc. are supported in comments.

package entitylisttocsv.actions;

import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.webui.CustomJavaAction;
import com.mendix.systemwideinterfaces.core.IMendixObject;
import com.mendix.systemwideinterfaces.core.IMendixObjectMember;
import com.mendix.core.Core;
import com.mendix.core.objectmanagement.member.MendixString;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.*;
import org.apache.commons.io.IOUtils;

/**
 * This Java action will iterate through a list of entities and export all the String attributes to a CSV file. Attributes of other types are ignored.
 * 
 * A generalisation of FIleDocument is passed with the details of where to save to CSV file.
 * 
 * There is also the option to include the names of the attributes as a header line if necessary.
 * 
 * It is not possible to change the output order of the columns.
 */
public class EntityListToCSVFile extends CustomJavaAction<java.lang.Boolean>
{
	private java.util.List<IMendixObject> Entities;
	private IMendixObject __TheCSVFileDocument;
	private system.proxies.FileDocument TheCSVFileDocument;
	private java.lang.Boolean IncludeHeader;

	public EntityListToCSVFile(IContext context, java.util.List<IMendixObject> Entities, IMendixObject TheCSVFileDocument, java.lang.Boolean IncludeHeader)
	{
		super(context);
		this.Entities = Entities;
		this.__TheCSVFileDocument = TheCSVFileDocument;
		this.IncludeHeader = IncludeHeader;
	}

	@Override
	public java.lang.Boolean executeAction() throws Exception
	{
		this.TheCSVFileDocument = __TheCSVFileDocument == null ? null : system.proxies.FileDocument.initialize(getContext(), __TheCSVFileDocument);

		// BEGIN USER CODE
		boolean addHeader = this.IncludeHeader;

		List<String> keys = new ArrayList<>();
		StringWriter sw = new StringWriter();
		BufferedWriter bw = new BufferedWriter(sw);
		CSVPrinter csvPrinter = new CSVPrinter(bw, CSVFormat.DEFAULT);

		for(IMendixObject entity : this.Entities) {
			// get the list of keys if not already available.
			if (keys.isEmpty()) {
				keys = this.getStringKeys(entity);
			}

			// add the header line if necessary.
			if (addHeader) {
				csvPrinter.printRecord(keys);
				addHeader = false;
			}

			// add the line of data.
			csvPrinter.printRecord(this.getStringValues(entity, keys));
		}

		// close the CSV printer and flush so the data is available in the StringWriter sw
		csvPrinter.flush();
		csvPrinter.close();

		// add the CSV to the FileDocument
		try (
			InputStream is = IOUtils.toInputStream(sw.toString(), StandardCharsets.UTF_8)
		) {
			Core.storeFileDocumentContent(this.getContext(), this.TheCSVFileDocument.getMendixObject(), is);
		}

		return true;
		// END USER CODE
	}

	/**
	 * Returns a string representation of this action
	 */
	@Override
	public java.lang.String toString()
	{
		return "EntityListToCSVFile";
	}

	// BEGIN EXTRA CODE
	/**
	 * Return a list of string attributes in a Mendix object.
	 * @param mendixObject
	 * @return A list of string attributes in the passed mendixObject.
	 */
	private List<String> getStringKeys(IMendixObject mendixObject) {
		List<String> keys = new ArrayList<>();
		Map<String, ? extends IMendixObjectMember<?>> members = mendixObject.getMembers(this.getContext());
		for(String key : members.keySet()) {
    		IMendixObjectMember<?> m = members.get(key);
    		if (m instanceof MendixString) {	// only use string attributes.
    			keys.add(key);
    		}
		}

		return keys;
	}

	/**
	 * Iterate over a list of keys in a Mendix object and return a list
	 * of string values.
	 * @return A list of string values matching the keys in the passed mendixObject.
	 */
	private List<String> getStringValues(IMendixObject mendixObject, List<String> keys) {
		List<String> values = new ArrayList<>();
		for(String key: keys) {
			IMendixObjectMember<?> m = mendixObject.getMember(this.getContext(), key);
			MendixString member = (MendixString) m;
			String value = member.getValue(this.getContext());
			values.add(value);
		}

		return values;
	}
	// END EXTRA CODE
}
