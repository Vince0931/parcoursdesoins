package queryFiles;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ontologie.EIG;
import parameters.Util;

public class GetSunburstHierarchy implements FileQuery{

	final static Logger logger = LoggerFactory.getLogger(GetSunburstHierarchy.class);
	
	public static final String fileName = "EventHierarchy4Sunburst.csv";
	private final String MIMEtype = "text/csv";
	
	private HashMap<IRI,IRI> childParent= new HashMap<IRI,IRI>();


	private HashMap<String, String> hierarchy;
	
	private void setChildParent(RepositoryConnection con, IRI eventIRI){
		RepositoryResult<Statement> statements = con.getStatements(null, RDFS.SUBCLASSOF, eventIRI);
		while(statements.hasNext()){
			Statement stat = statements.next();
			IRI subIRI = (IRI)stat.getSubject();
			if (childParent.containsKey(subIRI)){
				logger.error("This class does not handle multiaxial terminology");
			}
			childParent.put(subIRI, eventIRI);
			setChildParent(con, subIRI); // get children recursively
		}
		statements.close();
	}
	
	public IRI getParent(IRI child){
		return(childParent.get(child));
	}
	
	public HashMap<String,String> getHierarchy(){
		HashMap<String,String> classLocation = new HashMap<String,String>();
		for (IRI child : childParent.keySet()){
			String childName = child.getLocalName();
			StringBuilder sb = new StringBuilder();
			sb.insert(0,child.getLocalName());
			sb.insert(0,"-");
			while ((child = getParent(child)) != null){
				sb.insert(0,child.getLocalName());
				sb.insert(0,"-");
			}
			sb.deleteCharAt(0); // remove first -
			classLocation.put(childName, sb.toString());
		}
		return(classLocation);
	}
	
	public GetSunburstHierarchy(String HierarchyFileName, IRI classeNameIRI) throws RDFParseException, RepositoryException, IOException{
		// TODO Auto-generated method stub
		InputStream ontologyInput = Util.classLoader.getResourceAsStream(HierarchyFileName);
		// p RDF triple in memory : 
		Repository rep = new SailRepository(new MemoryStore());
		rep.initialize();
		RepositoryConnection con = rep.getConnection();
		con.add(ontologyInput, EIG.NAMESPACE, RDFFormat.TURTLE);
		ontologyInput.close();
		setChildParent(con, classeNameIRI);
		this.hierarchy = getHierarchy();
		con.close();
		rep.shutDown();
	}

	@Override
	public void sendBytes(OutputStream os) throws IOException {
		StringBuilder line = new StringBuilder();
		//header
		line.append("event");
		line.append("\t");
		line.append("tree");
		line.append("\n");
		os.write(line.toString().getBytes());
		line.setLength(0);
		
		for (String className : hierarchy.keySet()){
			line.append(className);
			line.append("\t");
			line.append(hierarchy.get(className));
			line.append("\n");
			os.write(line.toString().getBytes());
			line.setLength(0);
		}
	}

	@Override
	public String getFileName() {
		return fileName;
	}

	@Override
	public String getMIMEtype() {
		return MIMEtype;
	}
}
