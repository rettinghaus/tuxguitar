package app.tuxguitar.io.image;

import app.tuxguitar.io.base.TGSongExporter;
import app.tuxguitar.io.plugin.TGSongExporterPlugin;
import app.tuxguitar.util.TGContext;

public class ImageExporterPlugin extends TGSongExporterPlugin{

	public static final String MODULE_ID = "tuxguitar-image";

	protected TGSongExporter createExporter(TGContext context) {
		return new ImageExporter(context);
	}

	public String getModuleId(){
		return MODULE_ID;
	}
}
