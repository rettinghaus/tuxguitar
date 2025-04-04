package app.tuxguitar.app.document;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.app.util.TGFileChooser;
import app.tuxguitar.app.util.TGFileUtils;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.editor.undo.TGUndoableBuffer;
import app.tuxguitar.editor.undo.TGUndoableManager;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.singleton.TGSingletonFactory;
import app.tuxguitar.util.singleton.TGSingletonUtil;

public class TGDocumentListManager {

	private TGContext context;
	private List<TGDocument> documents;

	public TGDocumentListManager(TGContext context) {
		this.context = context;
		this.documents = new ArrayList<TGDocument>();
	}

	public List<TGDocument> getDocuments() {
		return documents;
	}

	public List<TGDocument> findUnwantedDocumentsToRemove() {
		List<TGDocument> documents = new ArrayList<TGDocument>();
		for(TGDocument document : this.documents) {
			if( document.isUnwanted() && !document.isUnsaved()) {
				documents.add(document);
			}
		}
		return documents;
	}

	public void removeUnwantedDocument() {
		List<TGDocument> documentsToRemove = this.findUnwantedDocumentsToRemove();
		for(TGDocument documentToRemove : documentsToRemove) {
			if( this.documents.size() > 1 ) {
				this.documents.remove(documentToRemove);
			}
		}
	}

	public void updateLoadedDocument() {
		TGSong song = this.getLoadedSong();
		TGDocument document = this.findDocument(song);

		this.removeUnwantedDocument();

		TGUndoableManager.getInstance(this.context).setBuffer(document.getUndoableBuffer());
	}

	public TGDocument findDocument(TGSong song) {
		for(TGDocument document : this.documents) {
			if( document.getSong().equals(song) ) {
				return document;
			}
		}

		TGDocument document = new TGDocument();
		document.setSong(song);
		document.setUndoableBuffer(new TGUndoableBuffer());
		document.setUnsaved(false);
		document.setUnwanted(false);
		document.setCaretBeat(null);
		document.setCaretString(0);
		this.documents.add(document);

		return this.findDocument(song);
	}

	public TGDocument findCurrentDocument() {
		return this.findDocument(this.getLoadedSong());
	}

	public int findDocumentIndex(TGDocument document) {
		return this.documents.indexOf(document);
	}

	public int findCurrentDocumentIndex() {
		TGDocument current = this.findCurrentDocument();
		return (current != null ? this.findDocumentIndex(current) : -1);
	}

	public TGSong getLoadedSong() {
		return TGDocumentManager.getInstance(this.context).getSong();
	}

	public String getDocumentName(TGDocument document) {
		if( document.getUri() != null ){
			String decodedFileName = TGFileUtils.getDecodedFileName(document.getUri());
			if( decodedFileName != null ) {
				return decodedFileName;
			}
		}
		return TGFileChooser.getDefaultSaveFileName();
	}

	public void removeDocument(TGDocument document) {
		this.documents.remove(document);
	}

	public void removeDocuments(List<TGDocument> documents) {
		this.documents.removeAll(documents);
	}

	public int countDocuments() {
		return this.documents.size();
	}

	public boolean containsDocument(TGDocument document) {
		return this.documents.contains(document);
	}

	public static TGDocumentListManager getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGDocumentListManager.class.getName(), new TGSingletonFactory<TGDocumentListManager>() {
			public TGDocumentListManager createInstance(TGContext context) {
				return new TGDocumentListManager(context);
			}
		});
	}
}
